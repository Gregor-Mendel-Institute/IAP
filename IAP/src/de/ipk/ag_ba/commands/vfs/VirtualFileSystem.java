package de.ipk.ag_ba.commands.vfs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.BackgroundTaskStatusProviderSupportingExternalCall;
import org.ReleaseInfo;
import org.graffiti.plugin.io.resources.IOurl;

import de.ipk.ag_ba.commands.ActionToggleSettingDefaultIsFalse;
import de.ipk.ag_ba.gui.interfaces.NavigationAction;
import de.ipk.ag_ba.gui.util.ExperimentReference;
import de.ipk_gatersleben.ag_nw.graffiti.plugins.gui.layout_control.network.TabAglet;

/**
 * @author klukas
 */
public abstract class VirtualFileSystem {
	
	public static Collection<VirtualFileSystem> getKnown() {
		ArrayList<VirtualFileSystem> res = new ArrayList<VirtualFileSystem>();
		res.add(new VirtualFileSystemFolderStorage(
				"file-desktop",
				"File I/O",
				"Desktop" + File.separator + "VFS",
				ReleaseInfo.getDesktopFolder() + File.separator + "VFS"));
		VirtualFileSystem vfsUdp = new VirtualFileSystemFolderStorage(
				"file-udp",
				"Network UDP Receiver",
				"Desktop" + File.separator + "UDP",
				ReleaseInfo.getDesktopFolder() + File.separator + "UDP");
		Runnable startAction = new Runnable() {
			@Override
			public void run() {
				TabAglet.enabler.doClick();
			}
		};
		ActionToggleSettingDefaultIsFalse toggleUdpReceive = new ActionToggleSettingDefaultIsFalse(
				null, startAction,
				"Enable receiving of experiment data by opening a UDP port",
				"Receive Experiments (UDP)",
				"udp_reciever");
		vfsUdp.addNavigationAction(toggleUdpReceive);
		res.add(vfsUdp);
		return res;
	}
	
	private ArrayList<NavigationAction> additionalNavigationActions = new ArrayList<NavigationAction>();
	
	private void addNavigationAction(ActionToggleSettingDefaultIsFalse navAction) {
		additionalNavigationActions.add(navAction);
	}
	
	public ArrayList<NavigationAction> getAdditionalNavigationActions() {
		return additionalNavigationActions;
	}
	
	public abstract String getTargetName();
	
	public abstract String getTransferProtocolName();
	
	public abstract String getTargetPathName();
	
	public abstract String getPrefix();
	
	public String getResultPathNameForUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return List of file names found at root of VFS source
	 */
	public abstract ArrayList<String> listFiles(String optSubDirectory);
	
	public abstract IOurl getIOurlFor(String fileName);
	
	public void saveExperiment(ExperimentReference experimentReference,
			BackgroundTaskStatusProviderSupportingExternalCall statusProvider) throws Exception {
		ActionDataExportToVfs a = new ActionDataExportToVfs(null, experimentReference, this);
		a.performActionCalculateResults(null);
	}
	
	public String[] listFiles(String subdirectory, FilenameFilter optFilenameFilter) {
		ArrayList<String> files = new ArrayList<String>();
		for (String s : listFiles(subdirectory)) {
			if (optFilenameFilter == null || optFilenameFilter.accept(null, s))
				files.add(s);
		}
		return files.toArray(new String[] {});
	}
	
	public ArrayList<NavigationAction> getAdditionalEntries() {
		return new ArrayList<NavigationAction>();
	}
}
