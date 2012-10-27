package de.ipk.ag_ba.image.operations.blocks.cmds;

import de.ipk.ag_ba.commands.ImageConfiguration;
import de.ipk.ag_ba.image.analysis.options.ImageProcessorOptions.Setting;
import de.ipk.ag_ba.image.operation.ImageOperation;
import de.ipk.ag_ba.image.operations.blocks.cmds.data_structures.AbstractSnapshotAnalysisBlockFIS;
import de.ipk.ag_ba.image.structures.FlexibleImage;

public class BlockEnlargeVisAndFluoMasks extends AbstractSnapshotAnalysisBlockFIS {
	
	@Override
	protected FlexibleImage processVISmask() {
		return enlargeMask(input().masks().vis(), options.getIntSetting(Setting.RGB_SIDE_NUMBER_OF_ERODE_LOOPS),
						options.getIntSetting(Setting.RGB_SIDE_NUMBER_OF_DILATE_LOOPS), ImageConfiguration.RgbTop);
	}
	
	@Override
	protected FlexibleImage processFLUOmask() {
		return enlargeMask(input().masks().fluo(), options.getIntSetting(Setting.RGB_SIDE_NUMBER_OF_ERODE_LOOPS),
				options.getIntSetting(Setting.RGB_SIDE_NUMBER_OF_DILATE_LOOPS), ImageConfiguration.FluoTop);
	}
	
	private FlexibleImage enlargeMask(FlexibleImage workImage, int numberOfErodeLoops, int numberOfDilateLoops,
			ImageConfiguration typ) {
		
		ImageOperation io = new ImageOperation(workImage);
		for (int i = 0; i < numberOfErodeLoops; i++)
			io.erode();
		for (int i = 0; i < numberOfDilateLoops; i++)
			io.dilate();
		
		return io.getImage();
	}
}
