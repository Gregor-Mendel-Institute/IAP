package de.ipk.ag_ba.image.operation;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.Vector2d;

/**
 * @author Christian Klukas
 */
public class ImageCalculation {
	
	private final ImageOperation imageOperation;
	
	public ImageCalculation(ImageOperation imageOperation) {
		this.imageOperation = imageOperation;
	}
	
	/**
	 * @return center of gravity, or NULL, if no foreground pixels can be found.
	 */
	public Vector2d getCOG() {
		double sumX = 0;
		double sumY = 0;
		int n = 0;
		int x = 0;
		int y = 0;
		int w = imageOperation.getWidth();
		for (int p : imageOperation.getImageAs1dArray()) {
			x++;
			if (x == w) {
				y++;
				x = 0;
			}
			if (p != ImageOperation.BACKGROUND_COLORint) {
				sumX += x;
				sumY += y;
				n++;
			}
		}
		if (n > 0)
			return new Vector2d(sumX / n, sumY / n);
		else
			return null;
	}
	
	public ImageOperation io() {
		return imageOperation;
	}
	
	public ImageOperation printColorCodes(boolean doIt) {
		if (!doIt)
			return imageOperation;
		int[] pix = imageOperation.getAs1D();
		HashSet<Integer> printed = new HashSet<Integer>();
		for (int i : pix) {
			if (!printed.contains(i)) {
				Color c = new Color(i);
				System.out.println("INT=" + i + ", RGB=" + c.getRed() + "/" + c.getGreen() + "/" + c.getBlue() + ", IS IO BACKGROUND?: "
						+ (i == ImageOperation.BACKGROUND_COLORint));
				printed.add(i);
			}
		}
		return imageOperation;
	}
	
	public int[] getColors() {
		LinkedHashSet<Integer> colors = new LinkedHashSet<>();
		int[] pix = imageOperation.getAs1D();
		for (int i : pix) {
			colors.add(i);
		}
		int[] cols = new int[colors.size()];
		int idx = 0;
		for (Integer c : colors)
			cols[idx++] = c;
		return cols;
	}
	
	public Double calculateAverageDistanceTo(Vector2d cog) {
		double sumDist = 0;
		int n = 0;
		int x = 0;
		int y = 0;
		int w = imageOperation.getWidth();
		for (int p : imageOperation.getImageAs1dArray()) {
			x++;
			if (x == w) {
				y++;
				x = 0;
			}
			if (p != ImageOperation.BACKGROUND_COLORint) {
				sumDist += Math.abs(cog.distance(x, y));
				n++;
			}
		}
		if (n > 0)
			return sumDist / n;
		else
			return null;
	}
	
}
