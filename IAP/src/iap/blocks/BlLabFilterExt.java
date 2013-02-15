/**
 * 
 */
package iap.blocks;

import iap.blocks.data_structures.AbstractSnapshotAnalysisBlockFIS;
import iap.pipelines.ImageProcessorOptions.CameraPosition;

import java.util.HashSet;

import de.ipk.ag_ba.image.operation.ImageOperation;
import de.ipk.ag_ba.image.structures.FlexibleImage;
import de.ipk.ag_ba.image.structures.FlexibleImageStack;
import de.ipk.ag_ba.image.structures.FlexibleImageType;

/**
 * Uses a lab-based pixel filter for the vis images.
 * 
 * @author Klukas, entzian
 */
public class BlLabFilterExt extends AbstractSnapshotAnalysisBlockFIS {
	
	boolean debug;
	
	Integer[] LAB_MIN_L_VALUE_FLUO, LAB_MIN_A_VALUE_FLUO;
	Integer[] LAB_MIN_B_VALUE_FLUO, LAB_MAX_L_VALUE_FLUO;
	Integer[] LAB_MAX_A_VALUE_FLUO, LAB_MAX_B_VALUE_FLUO;
	
	Integer[] LAB_MIN_L_VALUE_VIS, LAB_MAX_L_VALUE_VIS;
	Integer[] LAB_MIN_A_VALUE_VIS, LAB_MAX_A_VALUE_VIS;
	Integer[] LAB_MIN_B_VALUE_VIS, LAB_MAX_B_VALUE_VIS;
	
	@Override
	protected void prepare() {
		super.prepare();
		debug = getBoolean("debug", false);
	}
	
	@Override
	protected FlexibleImage processVISmask() {
		if (input().masks().vis() == null || input().images().vis() == null) {
			return null;
		} else {
			
			if (!getBoolean("enable Vis", true)) {
				return input().masks().vis();
			}
			
			ImageOperation visMask = input().masks().vis().io().copy();
			FlexibleImageStack fis = debug ? new FlexibleImageStack() : null;
			if (fis != null)
				fis.addImage("start", visMask.getImage(), null);
			
			if (getBoolean("Filter by HSV Color Space", false)) {
				visMask = visMask
						.blur(getDouble("Blur", 1))
						.filterRemoveHSV(
								getDouble("HSV-max-disctance", (1 / 2d - 1 / 3d)),
								getDouble("HSV-hue", (2 / 3d)),
								getDouble("HSV-max-lightness", (200d / 255d))); // filter out blue
				visMask = input().images().vis().io().copy().applyMask(
						visMask.closing(
								getInt("Closing-count-dilate", 2),
								getInt("Closing-count-erode", 4)).getImage(),
						options.getBackground());
				
				if (fis != null)
					fis.addImage("blue filtered by HSV", visMask.getImage(), null);
			}
			
			if (getBoolean("Filter by LAB Color Space", true)) {
				initLABfilterValuesVis();
				
				double blueCurbWidthBarley0_1 = 0;
				double blueCurbHeightEndBarly0_8 = 1;
				FlexibleImage toBeFiltered = visMask.hq_thresholdLAB_multi_color_or_and_not(
						LAB_MIN_L_VALUE_VIS, LAB_MAX_L_VALUE_VIS,
						LAB_MIN_A_VALUE_VIS, LAB_MAX_A_VALUE_VIS,
						LAB_MIN_B_VALUE_VIS, LAB_MAX_B_VALUE_VIS,
						options.getBackground(),
						Integer.MAX_VALUE,
						getBoolean("Return filtered image", true),
						new Integer[] {}, new Integer[] {},
						new Integer[] {}, new Integer[] {},
						new Integer[] {}, new Integer[] {},
						blueCurbWidthBarley0_1,
						blueCurbHeightEndBarly0_8).
						show("removed lab", debug).getImage();
				
				// visMask = visMask.filterRemoveLAB(
				// LAB_MIN_L_VALUE_VIS,
				// LAB_MAX_L_VALUE_VIS,
				// LAB_MIN_A_VALUE_VIS,
				// LAB_MAX_A_VALUE_VIS,
				// LAB_MIN_B_VALUE_VIS,
				// LAB_MAX_B_VALUE_VIS,
				// options.getBackground(), false);
				
				visMask = visMask.applyMaskInversed_ResizeMaskIfNeeded(toBeFiltered, options.getBackground());
				
				if (fis != null)
					fis.addImage("main lab filter", visMask.getImage(), null);
			}
			return visMask.getImage().show("VISS", debug);
		}
	}
	
	@Override
	protected FlexibleImage processFLUOmask() {
		if (input().masks().fluo() == null || input().images().fluo() == null) {
			return null;
		} else {
			
			if (!getBoolean("enable Fluo", false)) {
				return input().masks().fluo();
			}
			
			ImageOperation fluoMask = input().masks().fluo().io().copy();
			FlexibleImageStack fis = debug ? new FlexibleImageStack() : null;
			if (fis != null)
				fis.addImage("start", fluoMask.getImage(), null);
			
			if (getBoolean("Filter by HSV Color Space", false)) {
				fluoMask = fluoMask
						.blur(getDouble("Blur", 1))
						.filterRemoveHSV(
								getDouble("HSV-max-disctance", (1 / 2d - 1 / 3d)),
								getDouble("HSV-hue", (2 / 3d)),
								getDouble("HSV-max-lightness", (200d / 255d))); // filter out blue
				fluoMask = input().images().fluo().io().copy().applyMask(
						fluoMask.closing(
								getInt("Closing-count-dilate", 2),
								getInt("Closing-count-erode", 4)).getImage(),
						options.getBackground());
				
				if (fis != null)
					fis.addImage("blue filtered by HSV", fluoMask.getImage(), null);
			}
			
			if (getBoolean("Filter by LAB Color Space", true)) {
				initLABfilterValuesFluo();
				
				double blueCurbWidthBarley0_1 = 0;
				double blueCurbHeightEndBarly0_8 = 1;
				FlexibleImage toBeFiltered = fluoMask.hq_thresholdLAB_multi_color_or_and_not(
						LAB_MIN_L_VALUE_FLUO, LAB_MAX_L_VALUE_FLUO,
						LAB_MIN_A_VALUE_FLUO, LAB_MAX_A_VALUE_FLUO,
						LAB_MIN_B_VALUE_FLUO, LAB_MAX_B_VALUE_FLUO,
						options.getBackground(),
						Integer.MAX_VALUE,
						getBoolean("Return filtered image", true),
						new Integer[] {}, new Integer[] {},
						new Integer[] {}, new Integer[] {},
						new Integer[] {}, new Integer[] {},
						blueCurbWidthBarley0_1,
						blueCurbHeightEndBarly0_8).
						show("removed lab", debug).getImage();
				
				fluoMask = fluoMask.applyMaskInversed_ResizeMaskIfNeeded(toBeFiltered, options.getBackground());
				
				if (fis != null)
					fis.addImage("main lab filter", fluoMask.getImage(), null);
			}
			return fluoMask.getImage().show("VISS", debug);
		}
	}
	
	private void initLABfilterValuesVis() {
		if (options.isBarley()) {
			if (options.getCameraPosition() == CameraPosition.TOP) {
				
				LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 100, -1, -1, -1 });
				LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
				LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
				LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 135, -1, -1, -1 });
				LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 123, -1, -1, -1 });
				LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
				
			} else {
				LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 0, -1, -1, -1 });
				LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
				LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
				LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 255, -1, -1, -1 });
				LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 123, -1, -1, -1 });
				LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
				
			}
		} else
			if (options.isArabidopsis()) {
				if (options.getCameraPosition() == CameraPosition.TOP) {
					
					LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 125, -1, -1, -1 });
					LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 135, -1, -1, -1 });
					LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 120, -1, -1, -1 });
					LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
					
				} else {
					
					LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 123, -1, -1, -1 });
					LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
					
				}
			} else
				if (options.isMaize()) {
					if (options.getCameraPosition() == CameraPosition.TOP) {
						
						LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 50 * 255 / 100, -1, -1, -1 });
						LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
						LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
						LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 120, -1, -1, -1 });
						LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 125, -1, -1, -1 });
						LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
						
					} else {
						
						LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 0, -1, -1, -1 });
						LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
						LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
						LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 255, -1, -1, -1 });
						LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 122, -1, -1, -1 });
						LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
						
					}
				} else {
					
					LAB_MIN_L_VALUE_VIS = getIntArray("Lab-filter-vis-min-l-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_L_VALUE_VIS = getIntArray("Lab-filter-vis-max-l-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_A_VALUE_VIS = getIntArray("Lab-filter-vis-min-a-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_A_VALUE_VIS = getIntArray("Lab-filter-vis-max-a-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_B_VALUE_VIS = getIntArray("Lab-filter-vis-min-b-array", new Integer[] { 122, -1, -1, -1 });
					LAB_MAX_B_VALUE_VIS = getIntArray("Lab-filter-vis-max-b-array", new Integer[] { 255, -1, -1, -1 });
					
				}
	}
	
	private void initLABfilterValuesFluo() {
		if (options.isBarley()) {
			if (options.getCameraPosition() == CameraPosition.TOP) {
				
				LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 100, -1, -1, -1 });
				LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
				LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
				LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 135, -1, -1, -1 });
				LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 123, -1, -1, -1 });
				LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
				
			} else {
				LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 0, -1, -1, -1 });
				LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
				LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
				LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 255, -1, -1, -1 });
				LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 123, -1, -1, -1 });
				LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
				
			}
		} else
			if (options.isArabidopsis()) {
				if (options.getCameraPosition() == CameraPosition.TOP) {
					
					LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 125, -1, -1, -1 });
					LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 135, -1, -1, -1 });
					LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 120, -1, -1, -1 });
					LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
					
				} else {
					
					LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 123, -1, -1, -1 });
					LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
					
				}
			} else
				if (options.isMaize()) {
					if (options.getCameraPosition() == CameraPosition.TOP) {
						
						LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 50 * 255 / 100, -1, -1, -1 });
						LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
						LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
						LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 120, -1, -1, -1 });
						LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 125, -1, -1, -1 });
						LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
						
					} else {
						
						LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 0, -1, -1, -1 });
						LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
						LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
						LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 255, -1, -1, -1 });
						LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 122, -1, -1, -1 });
						LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
						
					}
				} else {
					
					LAB_MIN_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-l-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_L_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-l-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-a-array", new Integer[] { 0, -1, -1, -1 });
					LAB_MAX_A_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-a-array", new Integer[] { 255, -1, -1, -1 });
					LAB_MIN_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-min-b-array", new Integer[] { 122, -1, -1, -1 });
					LAB_MAX_B_VALUE_FLUO = getIntArray("Lab-filter-fluo-max-b-array", new Integer[] { 255, -1, -1, -1 });
					
				}
	}
	
	@Override
	public HashSet<FlexibleImageType> getInputTypes() {
		HashSet<FlexibleImageType> res = new HashSet<FlexibleImageType>();
		res.add(FlexibleImageType.VIS);
		return res;
	}
	
	@Override
	public HashSet<FlexibleImageType> getOutputTypes() {
		return getInputTypes();
	}
	
}