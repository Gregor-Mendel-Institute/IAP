package de.ipk.ag_ba.image.operations.blocks.cmds.maize;

import ij.measure.ResultsTable;
import de.ipk.ag_ba.image.analysis.gernally.ImageProcessorOptions.Setting;
import de.ipk.ag_ba.image.operations.ImageOperation;
import de.ipk.ag_ba.image.operations.blocks.cmds.data_structures.AbstractSnapshotAnalysisBlockFIS;
import de.ipk.ag_ba.image.operations.blocks.properties.BlockProperty;
import de.ipk.ag_ba.image.operations.blocks.properties.PropertyNames;
import de.ipk.ag_ba.image.structures.FlexibleImage;

/**
 * @author klukas, pape
 */
public class BlockIntensityAnalysis extends AbstractSnapshotAnalysisBlockFIS {
	
	@Override
	protected boolean isChangingImages() {
		return false;
	}
	
	private int visibleFilledPixels, nirFilledPixels;
	BlockProperty markerDistanceHorizontally;
	
	@Override
	protected void prepare() {
		super.prepare();
		if (getInput().getImages().getVis() != null)
			this.visibleFilledPixels = getInput().getImages().getVis().getIO().countFilledPixels();
		
		if (getInput().getImages().getNir() != null) {
			this.nirFilledPixels = getInput().getImages().getNir().getIO().countFilledPixels();
		}
		if (getProperties() != null && getProperties().getNumericProperty(0, 1, PropertyNames.MARKER_DISTANCE_LEFT_RIGHT) != null)
			markerDistanceHorizontally = getProperties().getNumericProperty(0, 1, PropertyNames.MARKER_DISTANCE_LEFT_RIGHT);
	}
	
	/**
	 * ndvi [-1-1]
	 */
	@Override
	protected FlexibleImage processVISmask() {
		
		if (getInput().getMasks().getVis() != null) {
			
			ImageOperation io = new ImageOperation(getInput().getMasks().getVis());
			double visibleIntensitySumR = io.intensitySumOfChannel(false, true, false, false);
			double visibleIntensitySumG = io.intensitySumOfChannel(false, false, true, false);
			double visibleIntensitySumB = io.intensitySumOfChannel(false, false, false, true);
			double averageVisR = visibleIntensitySumR / visibleFilledPixels;
			double averageVisG = visibleIntensitySumG / visibleFilledPixels;
			double averageVisB = visibleIntensitySumB / visibleFilledPixels;
			
			ResultsTable rt = new ResultsTable();
			rt.incrementCounter();
			rt.addValue("ndvi.vis.red.intensity.average", averageVisR);
			rt.addValue("ndvi.vis.green.intensity.average", averageVisG);
			rt.addValue("ndvi.vis.blue.intensity.average", averageVisB);
			
			if (getInput().getMasks().getNir() != null) {
				double nirIntensitySum = getInput().getMasks().getNir().getIO().intensitySumOfChannel(false, true, false, false);
				double averageNir = 1 - nirIntensitySum / nirFilledPixels;
				rt.addValue("ndvi.nir.intensity.average", averageNir);
				
				double ndvi = (averageNir - averageVisR) / (averageNir + averageVisR);
				rt.addValue("ndvi", ndvi);
			}
			getProperties().storeResults("RESULT_" + options.getCameraPosition() + ".", rt, getBlockPosition());
			return getInput().getMasks().getVis();
		} else
			return null;
	}
	
	@Override
	protected FlexibleImage processFLUOmask() {
		if (getInput().getMasks().getFluo() != null) {
			ImageOperation io = new ImageOperation(getInput().getMasks().getFluo());
			if (markerDistanceHorizontally != null) {
				ResultsTable rt = io.intensity(7).calculateHistorgram(markerDistanceHorizontally, options.getIntSetting(Setting.REAL_MARKER_DISTANCE)); // markerDistanceHorizontally
				if (rt != null)
					getProperties().storeResults("RESULT_" + options.getCameraPosition() + ".fluo.", rt, getBlockPosition());
			}
			return io.getImage();
		} else
			return null;
	}
	
	@Override
	protected FlexibleImage processNIRmask() {
		if (getInput().getMasks().getNir() != null) {
			ImageOperation io = new ImageOperation(getInput().getMasks().getNir());
			if (getInput().getMasks().getNir().getHeight() > 1) {
				if (markerDistanceHorizontally != null) {
					ResultsTable rt = io.intensity(7).calculateHistorgram(markerDistanceHorizontally, options.getIntSetting(Setting.REAL_MARKER_DISTANCE)); // markerDistanceHorizontally
					if (rt != null)
						getProperties().storeResults("RESULT_" + options.getCameraPosition() + ".nir.", rt, getBlockPosition());
				}
			}
			return io.getImage();
		} else
			return null;
	}
}
