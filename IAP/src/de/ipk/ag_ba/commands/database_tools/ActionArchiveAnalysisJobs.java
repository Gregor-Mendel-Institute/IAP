/*******************************************************************************
 * Copyright (c) 2010 Image Analysis Group, IPK Gatersleben
 *******************************************************************************/
/*
 * Created on Sep 30, 2010 by Christian Klukas
 */
package de.ipk.ag_ba.commands.database_tools;

import java.util.ArrayList;
import java.util.Collection;

import de.ipk.ag_ba.commands.AbstractNavigationAction;
import de.ipk.ag_ba.gui.MainPanelComponent;
import de.ipk.ag_ba.gui.navigation_model.NavigationButton;
import de.ipk.ag_ba.mongo.MongoDB;
import de.ipk.ag_ba.server.task_management.BatchCmd;
import de.ipk.ag_ba.server.task_management.CloudAnalysisStatus;
import de.ipk_gatersleben.ag_nw.graffiti.services.task.BackgroundTaskHelper;

/**
 * @author klukas
 */
public class ActionArchiveAnalysisJobs extends AbstractNavigationAction {
	
	private final MongoDB m;
	protected int nJobs = -1;
	private String commandResult = "";
	
	public ActionArchiveAnalysisJobs(final MongoDB m) {
		super("Deletes running and scheduled compute tasks");
		this.m = m;
	}
	
	@Override
	public String getDefaultImage() {
		if (doArchive())
			return "img/ext/gpl2/Gnome-Utilities-System-Monitor-64-still.png";
		else
			return "img/ext/gpl2/Gnome-Utilities-System-Monitor-64.png";
	}
	
	@Override
	public boolean requestTitleUpdates() {
		return true;
	}
	
	@Override
	public String getDefaultTitle() {
		if (doArchive())
			return "Archive " + (nJobs >= 0 ? nJobs + "" : "") + " analysis tasks";
		else
			return "Reactivate " + (nJobs >= 0 ? nJobs + "" : "") + " analysis tasks";
	}
	
	@Override
	public ArrayList<NavigationButton> getResultNewActionSet() {
		return null;
	}
	
	@Override
	public MainPanelComponent getResultMainPanel() {
		return new MainPanelComponent(commandResult);
	}
	
	@Override
	public boolean isProvidingActions() {
		return false;
	}
	
	private boolean doArchiveLastResult = false;
	private long doArchiveLastCountCheck = 0;
	private boolean doArchiveCheckRunning = false;
	
	private synchronized boolean doArchive() {
		if (System.currentTimeMillis() - doArchiveLastCountCheck > 30 * 1000 && !doArchiveCheckRunning) {
			doArchiveCheckRunning = true;
			BackgroundTaskHelper.issueSimpleTask(
					"Check Analysis Job Count",
					"Determine number of compute jobs",
					new Runnable() {
						@Override
						public void run() {
							Collection<BatchCmd> availableJobs = m.batch().getAll();
							int nScheduled = 0;
							int nArchived = 0;
							for (BatchCmd c : availableJobs) {
								if (c.getRunStatus() == CloudAnalysisStatus.SCHEDULED)
									nScheduled++;
								else
									if (c.getRunStatus() == CloudAnalysisStatus.ARCHIVED)
										nArchived++;
							}
							if (nScheduled > 0) {
								ActionArchiveAnalysisJobs.this.nJobs = nScheduled;
								ActionArchiveAnalysisJobs.this.doArchiveLastResult = true;
							} else {
								ActionArchiveAnalysisJobs.this.nJobs = nArchived;
								ActionArchiveAnalysisJobs.this.doArchiveLastResult = false;
							}
						}
					}, new Runnable() {
						@Override
						public void run() {
							doArchiveLastResult = false;
							doArchiveCheckRunning = false;
							doArchiveLastCountCheck = System.currentTimeMillis();
						}
					});
		}
		return doArchiveLastResult;
	}
	
	@Override
	public ArrayList<NavigationButton> getResultNewNavigationSet(ArrayList<NavigationButton> currentSet) {
		return currentSet;
	}
	
	@Override
	public void performActionCalculateResults(NavigationButton src) throws Exception {
		boolean doArchive = doArchiveLastResult;
		int n = 0;
		Collection<BatchCmd> availableJobs = m.batch().getAll();
		for (BatchCmd c : availableJobs) {
			if (doArchive) {
				if (c.getRunStatus() == CloudAnalysisStatus.SCHEDULED)
					if (m.batch().claim(c, CloudAnalysisStatus.ARCHIVED, false))
						n++;
			} else {
				if (c.getRunStatus() == CloudAnalysisStatus.ARCHIVED)
					if (m.batch().claim(c, CloudAnalysisStatus.SCHEDULED, false))
						n++;
			}
		}
		if (doArchive)
			commandResult = "Archived " + n + " compute tasks!";
		else
			commandResult = "Reactivated " + n + " compute tasks!";
		
	}
}
