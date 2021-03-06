package iap.blocks.extraction;

import iap.blocks.data_structures.AbstractBlock;
import iap.blocks.data_structures.AbstractSnapshotAnalysisBlock;
import iap.blocks.data_structures.BlockType;
import iap.blocks.data_structures.CalculatedProperty;
import iap.blocks.data_structures.CalculatedPropertyDescription;
import iap.blocks.data_structures.CalculatesProperties;
import iap.blocks.data_structures.RunnableOnImageSet;
import iap.pipelines.ImageProcessorOptionsAndResults.CameraPosition;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.BackgroundTaskStatusProviderSupportingExternalCall;

import de.ipk.ag_ba.image.operation.ImageOperation;
import de.ipk.ag_ba.image.operation.TopBottomLeftRight;
import de.ipk.ag_ba.image.operations.blocks.properties.BlockResultSet;
import de.ipk.ag_ba.image.structures.CameraType;
import de.ipk.ag_ba.image.structures.Image;

/**
 * Calculates the plant width and height properties from the visible/fluorescence light image mask.
 * 
 * @author klukas, pape
 */
public class BlCalcWidthAndHeight extends
		AbstractSnapshotAnalysisBlock implements CalculatesProperties {
	
	private static final String HEIGHT = "height";
	private static final String WIDTH = "width";
	private static final String HEIGHT_NORM = "height.norm";
	private static final String WIDTH_NORM = "width.norm";
	
	Double realMarkerDistHorizontal;
	Double distHorizontal;
	
	boolean process_vis;
	boolean process_fluo;
	
	boolean debug = false;
	
	@Override
	protected void prepare() {
		super.prepare();
		debug = getBoolean("debug", false);
		
		realMarkerDistHorizontal = optionsAndResults.getREAL_MARKER_DISTANCE();
		distHorizontal = optionsAndResults.getCalculatedBlueMarkerDistance();
		
		process_vis = getBoolean("Process Vis mask", true);
		process_fluo = getBoolean("Process Fluo mask", true);
	}
	
	@Override
	public boolean isChangingImages() {
		return false;
	}
	
	@Override
	protected Image processVISmask() {
		Image res = input().masks().vis();
		
		if (res != null)
			if (process_vis)
				res = processWandH(input().masks().vis(), CameraType.VIS);

		return res;
	}
	
	@Override
	protected Image processFLUOmask() {
		Image res = input().masks().fluo();
		
		if (res != null)
			if (process_vis)
				res = processWandH(input().masks().fluo(), CameraType.FLUO);

		return res;
	}
	
	public Image processWandH(Image img, CameraType ct) {
		final int vertYsoilLevelF = -1;
		int background = optionsAndResults.getBackground();
				
		if (optionsAndResults.getCameraPosition() == CameraPosition.SIDE && img != null) {
			final TopBottomLeftRight temp = getWidthAndHeightSide(img,
					background, vertYsoilLevelF);
			
//			double resf = img.getCameraType() == CameraType.FLUO ? (double) input().masks().vis()
//					.getWidth()
//					/ (double) img.getWidth()
//					* (input().images().fluo().getWidth() / (double) input()
//							.images().fluo().getHeight())
//					/ (input().images().vis().getWidth() / (double) input()
//							.images().vis().getHeight())
//					: 1.0;
			
			double resfw = img.getCameraType() == CameraType.FLUO ? (double) input().masks().vis().getWidth() / (double) input().masks().fluo().getWidth() : 1.0;
			double resfh = img.getCameraType() == CameraType.FLUO ? (double) input().masks().vis().getHeight() / (double) input().masks().fluo().getHeight() : 1.0;
			
			final Point values = temp != null ? new Point(Math.abs(temp
					.getRightX() - temp.getLeftX()), Math.abs(temp.getBottomY()
					- temp.getTopY())) : null;
			
			if (values != null) {
				boolean drawVerticalHeightBar = getBoolean("Draw Height Line", true);
				if (drawVerticalHeightBar)
					getResultSet().addImagePostProcessor(
							new RunnableOnImageSet() {
								@Override
								public Image postProcessMask(
										Image visRes) {
									if (vertYsoilLevelF > 0)
										visRes = visRes
												.io()
												.canvas()
												.fillRect(
														values.x
																/ 2
																+ temp.getLeftX(),
														vertYsoilLevelF - values.y,
														10,
														values.y,
														Color.MAGENTA.getRGB(),
														255)
												.drawLine(values.x / 2 + temp.getLeftX() - 50, vertYsoilLevelF - values.y, values.x / 2 + temp.getLeftX() + 50,
														vertYsoilLevelF - values.y, Color.MAGENTA.getRGB(), 0.5, 5)
												.drawLine(values.x / 2 + temp.getLeftX() - 50, vertYsoilLevelF, values.x / 2 + temp.getLeftX() + 50,
														vertYsoilLevelF, Color.MAGENTA.getRGB(), 0.5, 5)
												.getImage()
												.show("DEBUG", debug);
									else {
										visRes = visRes
												.io()
												.canvas()
												.fillRect(
														temp.getLeftX() + (temp.getRightX() - temp.getLeftX()) / 2,
														temp.getTopY() - temp.getBottomY()
																+ temp.getTopY() + temp.getBottomY()
																- temp.getTopY(),
														(temp.getRightX() - temp.getLeftX()) / 2,
														10,
														Color.BLUE.getRGB(), 0.8)
												.fillRect(
														values.x
																/ 2
																+ temp.getLeftX(),
														temp.getTopY() - temp.getBottomY()
																+ temp.getTopY() + temp.getBottomY()
																- temp.getTopY(),
														10,
														temp.getBottomY()
																- temp.getTopY(),
														Color.BLUE.getRGB(), 0.8)
												.getImage()
												.show("DEBUG", debug);
									}
									return visRes;
								}
								
								@Override
								public CameraType getConfig() {
									return ct;
								}
								
								@Override
								public Image postProcessImage(Image image) {
									return image;
								}
							});

				if (distHorizontal != null && realMarkerDistHorizontal != null) {
					getResultSet()
							.setNumericResult(
									getBlockPosition(),
									new Trait(cp(), ct, TraitCategory.GEOMETRY, WIDTH_NORM),
									values.x * (realMarkerDistHorizontal / distHorizontal) * resfw, "mm", this, input().images().getAnyInfo());
					getResultSet()
							.setNumericResult(
									getBlockPosition(),
									new Trait(cp(), ct, TraitCategory.GEOMETRY, HEIGHT_NORM),
									values.y * (realMarkerDistHorizontal / distHorizontal) * resfh, "mm", this, input().images().getAnyInfo());
				}
				getResultSet().setNumericResult(getBlockPosition(),
						new Trait(cp(), ct, TraitCategory.GEOMETRY, WIDTH), values.x, "px", this, input().images().getAnyInfo());
				getResultSet().setNumericResult(getBlockPosition(),
						new Trait(cp(), ct, TraitCategory.GEOMETRY, HEIGHT), values.y, "px", this, input().images().getAnyInfo());
				
			}
		}
		return img;
	}
	
	private TopBottomLeftRight getWidthAndHeightSide(Image vis,
			int background, int vertYsoilLevel) {
		TopBottomLeftRight temp = new ImageOperation(vis)
				.getExtremePoints(background);
		if (temp != null) {
			if (vertYsoilLevel > 0)
				temp.setBottom(vertYsoilLevel);
			return temp;
		} else
			return null;
	}
	
	@Override
	public void postProcessResultsForAllTimesAndAngles(
			TreeMap<String, TreeMap<Long, Double>> plandID2time2waterData,
			TreeMap<Long, TreeMap<String, HashMap<String, BlockResultSet>>> time2allResultsForSnapshot,
			TreeMap<Long, TreeMap<String, HashMap<String, BlockResultSet>>> time2summaryResult,
			BackgroundTaskStatusProviderSupportingExternalCall optStatus,
			CalculatesProperties propertyCalculator) throws InterruptedException {
		super.postProcessResultsForAllTimesAndAngles(plandID2time2waterData,
				time2allResultsForSnapshot, time2summaryResult, optStatus, this);
		
		for (CameraPosition cp : new CameraPosition[] { CameraPosition.SIDE, CameraPosition.TOP })
			for (CameraType ct : CameraType.values())
				calculateRelativeValues(time2allResultsForSnapshot, time2summaryResult, getBlockPosition(),
						new String[] {
								new Trait(cp, ct, TraitCategory.GEOMETRY, "width").toString(),
								new Trait(cp, ct, TraitCategory.GEOMETRY, "width.norm").toString(),
								new Trait(cp, ct, TraitCategory.GEOMETRY, "height").toString(),
								new Trait(cp, ct, TraitCategory.GEOMETRY, "height.norm").toString()
						}, this);
	}
	
	@Override
	public HashSet<CameraType> getCameraInputTypes() {
		HashSet<CameraType> res = new HashSet<CameraType>();
		res.add(CameraType.VIS);
		res.add(CameraType.FLUO);
		return res;
	}
	
	@Override
	public HashSet<CameraType> getCameraOutputTypes() {
		return getCameraInputTypes();
	}
	
	@Override
	public BlockType getBlockType() {
		return BlockType.FEATURE_EXTRACTION;
	}
	
	@Override
	public String getName() {
		return "Calculate Width and Height (Side)";
	}
	
	@Override
	public String getDescription() {
		return "Calculates the plant width and height properties from the visible light / fluorescence image mask.";
	}
	
	@Override
	public CalculatedPropertyDescription[] getCalculatedProperties() {
		return new CalculatedPropertyDescription[] {
				new CalculatedProperty(WIDTH,
						"Width of the plant, measured as the horizontal distance in pixels "
								+ "from the most left plant pixel to the most right plant pixel."),
				new CalculatedProperty(WIDTH_NORM,
						"Width of the plant, measured as the horizontal distance in pixels "
								+ "from the most left plant pixel to the most right plant pixel normalized to mm."),
				new CalculatedProperty(HEIGHT,
						"Height of the plant, measured as the vertical distance in pixels "
								+ "from the most top plant pixel to the most bottom plant pixel."),
				new CalculatedProperty(HEIGHT_NORM,
						"Height of the plant, measured as the vertical distance in pixels "
								+ "from the most top plant pixel to the most bottom plant pixel normalized to mm.")
		};
	}
}
