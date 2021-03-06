package de.ipk.ag_ba.image.operation.canvas;

import iap.blocks.data_structures.RunnableOnImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

import org.Vector2i;

import de.ipk.ag_ba.image.operation.ImageOperation;
import de.ipk.ag_ba.image.operation.PositionAndColor;
import de.ipk.ag_ba.image.operations.complex_hull.Line;
import de.ipk.ag_ba.image.operations.complex_hull.Point;
import de.ipk.ag_ba.image.operations.skeleton.RunnableWithBooleanResult;
import de.ipk.ag_ba.image.structures.Image;
import de.ipk_gatersleben.ag_pbi.mmd.experimentdata.images.ImageData;

public class ImageCanvas {
	
	private Image image;
	
	public ImageCanvas(Image image) {
		this.image = image;
	}
	
	/**
	 * @param alpha
	 *           0..1 (0 == solid)
	 *           - opacity of the filled rectangle, Wikipedia: Alpha_Blending C = αAA + (1 − αA)B
	 */
	public ImageCanvas drawLine(int x0, int y0, int x1, int y1, int color, double alpha, int size) {
		int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
		int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
		int err = dx + dy, e2; /* error value e_xy */
		
		while (true) { /* loop */
			fillRect(x0 - size, y0 - size, size + size, size + size, color, alpha);
			if (x0 == x1 && y0 == y1)
				break;
			e2 = 2 * err;
			if (e2 >= dy) {
				err += dy;
				x0 += sx;
			} /* e_xy+e_x > 0 */
			if (e2 <= dx) {
				err += dx;
				y0 += sy;
			} /* e_xy+e_y < 0 */
		}
		return this;
	}
	
	/**
	 * @param alpha
	 *           0..1 (0 == solid)
	 *           - opacity of the filled rectangle, Wikipedia: Alpha_Blending C = αAA + (1 − αA)B
	 */
	public ImageCanvas fillRect(int x, int y, int w, int h, int color, double alpha) {
		if (alpha == 1d) {
			System.out.println("Alpha value 1.0 used, skip drawing.");
			return this;
		}
		int colorBlueRect = (color & 0x0000ff);
		int wi = image.getWidth();
		int hi = image.getHeight();
		if (hi <= 1)
			return this;
		int[] img = image.getAs1A();
		int r, g, b, c, red, green, blue;
		int colorRedRect = (color & 0xff0000) >> 16;
		int colorGreenRect = (color & 0x00ff00) >> 8;
		boolean doAlpha = Math.abs(alpha) > 0.00001;
		for (int xi = x; xi <= x + w; xi++)
			for (int yi = y; yi <= y + h; yi++) {
				if (xi >= 0 && xi < wi) {
					int i = xi + yi * wi;
					if (i >= 0 && i < img.length) {
						if (doAlpha) {
							c = img[i];
							r = (c & 0xff0000) >> 16;
							g = (c & 0x00ff00) >> 8;
							b = (c & 0x0000ff);
							red = (int) (alpha * r + (1 - alpha) * colorRedRect);
							green = (int) (alpha * g + (1 - alpha) * colorGreenRect);
							blue = (int) (alpha * b + (1 - alpha) * colorBlueRect);
						} else {
							red = colorRedRect;
							green = colorGreenRect;
							blue = colorBlueRect;
						}
						img[i] = (0xFF << 24 | (red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
					}
				}
			}
		return this;
	}
	
	public ImageCanvas fillRect(int x, int y, int w, int h, int color) {
		int wi = image.getWidth();
		int hi = image.getHeight();
		int[] img = image.getAs1A();
		
		int red = (color & 0xff0000) >> 16;
		int green = (color & 0x00ff00) >> 8;
		int blue = (color & 0x0000ff);
		
		for (int xi = x; xi <= x + w; xi++)
			for (int yi = y; yi <= y + h; yi++) {
				int i = xi + yi * wi;
				if (i >= 0 && i < img.length) {
					img[i] = (0xFF << 24 | (red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
				}
			}
		image = new Image(wi, hi, img);
		return this;
	}
	
	/**
	 * Draws the border of a Circle.(based on Bresenham-Algorithm, see Wikipedia)
	 * 
	 * @param mx
	 * @param my
	 * @param radius
	 * @param color
	 * @param alpha
	 *           0 == solid
	 *           - ! opacity, function corrupt
	 * @param s
	 *           - Stroke width
	 * @return
	 */
	public ImageCanvas drawCircle(int mx, int my, int radius, int color, double alpha, int strokeWidth) {
		int s = strokeWidth;
		int f = 1 - radius;
		int ddF_x = 0;
		int ddF_y = -2 * radius;
		int x = 0;
		int y = radius;
		
		fillRect(mx - s, my + radius - s, s, s, color, alpha);
		fillRect(mx - s, my - radius - s, s, s, color, alpha);
		fillRect(mx + radius - s, my - s, s, s, color, alpha);
		fillRect(mx - radius - s, my - s, s, s, color, alpha);
		while (x < y) {
			if (f >= 0) {
				y--;
				ddF_y += 2;
				f += ddF_y;
			}
			x++;
			ddF_x += 2;
			f += ddF_x + 1;
			
			fillRect(mx + x - s, my + y - s, s, s, color, alpha);
			fillRect(mx - x - s, my + y - s, s, s, color, alpha);
			fillRect(mx + x - s, my - y - s, s, s, color, alpha);
			fillRect(mx - x - s, my - y - s, s, s, color, alpha);
			fillRect(mx + y - s, my + x - s, s, s, color, alpha);
			fillRect(mx - y - s, my + x - s, s, s, color, alpha);
			fillRect(mx + y - s, my - x - s, s, s, color, alpha);
			fillRect(mx - y - s, my - x - s, s, s, color, alpha);
		}
		return this;
	}
	
	/**
	 * Draws a filled Circle.
	 * 
	 * @param mx
	 * @param my
	 * @param radius
	 * @param color
	 * @param alpha
	 * @return
	 */
	public ImageCanvas fillCircle(int mx, int my, int radius, int color, double alpha) {
		int x = 0;
		int y = radius;
		int d = radius - 1;
		while (y >= x) {
			fillRect(mx - x, my - y, 2 * x, 1, color, alpha);
			fillRect(mx - y, my - x, 2 * y, 1, color, alpha);
			fillRect(mx - x, my + y, 2 * x, 1, color, alpha);
			fillRect(mx - y, my + x, 2 * y, 1, color, alpha);
			if (y == x)
				break;
			if (d < 0) {
				d += 2 * x + 3;
			} else {
				y -= 1;
				d += 2 * x - 2 * y + 5;
			}
			x += 1;
		}
		return this;
	}
	
	/**
	 * Draws a filled Circle.
	 * 
	 * @param mx
	 * @param my
	 * @param radius
	 * @param color
	 * @param alpha
	 * @return
	 */
	public ImageCanvas fillCircle(int mx, int my, int innerRadius, int outerRadius, int color, double alpha) {
		for (int x = mx - outerRadius; x < mx + outerRadius; x++)
			for (int y = my - outerRadius; y < my + outerRadius; y++) {
				double d = Math.sqrt((x - mx) * (x - mx) + (y - my) * (y - my));
				if (d > innerRadius && d < outerRadius)
					fillRect(x, y, 1, 1, color, alpha);
			}
		return this;
	}
	
	public Image getImage() {
		return image;
	}
	
	public ImageCanvas fillRectOutside(Rectangle rectangle, int backgroundColorint) {
		int w = image.getWidth();
		int h = image.getHeight();
		ImageCanvas res = this;
		
		int xL = rectangle.x - 1;
		if (xL > 0)
			res = fillRect(0, 0, xL, h, backgroundColorint, 1);
		
		int yT = rectangle.y - 1;
		if (yT > 0)
			res = fillRect(0, 0, w, yT, backgroundColorint, 1);
		
		int xR = rectangle.x + rectangle.width + 1;
		if (xR < w && xR > 0)
			res = fillRect(xR, 0, w - xR, h, backgroundColorint, 1);
		
		int yB = rectangle.y + rectangle.height + 1;
		if (yB < h && yB > 0)
			res = fillRect(0, yB, w, h - yB, backgroundColorint, 1);
		
		return res;
	}
	
	private BufferedImage buf = null;
	
	public Graphics getGraphics() {
		if (buf == null)
			buf = image.getAsBufferedImage(true);
		return buf.getGraphics();
	}
	
	public void updateFromGraphics() {
		image = new Image(Image.processTransparency(null, buf));
	}
	
	/**
	 * @param alpha
	 *           0..1 (0 == solid)
	 *           - opacity of the filled rectangle, Wikipedia: Alpha_Blending C = αAA + (1 − αA)B
	 */
	public ImageCanvas drawLine(Line sp, int color, double alpha, int size) {
		return drawLine((int) sp.getP0().x, (int) sp.getP0().y, (int) sp.getP1().x, (int) sp.getP1().y, color, alpha, size);
	}
	
	public ImageCanvas drawLine(Point p1, Point p2, int color, double alpha, int size) {
		return drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y, color, alpha, size);
	}
	
	public ImageOperation io() {
		return new ImageOperation(getImage());
	}
	
	public ImageCanvas drawSideHistogram(ArrayList<Double> corrs, boolean doIt) {
		return drawSideHistogram(corrs, doIt, 0.05d);
	}
	
	public ImageCanvas drawSideHistogram() {
		int[][] img = getImage().getAs2A();
		int h = img[0].length;
		int w = img.length;
		ArrayList<Double> values = io().calculateVerticalPattern();
		
		int maxW = (int) (w * 0.15d);
		for (int y = 0; y < h; y++)
			values.set(y, values.get(y) * maxW);
		int color = Color.BLUE.getRGB();
		double alpha = 0.5;
		int size = 1;
		for (int y = 0; y < h; y++)
			if (values.get(y) > 0)
				drawLine((int) (w - values.get(y)), y, w - 1, y, color, alpha, size);
		
		return this;
	}
	
	public ImageCanvas drawSideHistogram(ArrayList<Double> corrs, boolean doIt, double widthPercent) {
		if (!doIt)
			return this;
		
		int[][] img = getImage().getAs2A();
		int h = img[0].length;
		int w = img.length;
		ArrayList<Double> values = new ArrayList<Double>(h);
		for (int y = 0; y < h; y++)
			values.add(0d);
		int maxW = (int) (w * widthPercent);
		for (int y = 0; y < h; y++) {
			try {
				values.set(y, corrs.get(y - h / 2 + corrs.size() / 2) * maxW + maxW);
			} catch (Exception e) {
				// empty
			}
		}
		int color = Color.BLUE.getRGB();
		double alpha = 0.5;
		int size = 1;
		for (int y = 0; y < h; y++)
			if (values.get(y) != null && values.get(y) > 0) {
				drawLine((int) (w - values.get(y)) - 1, y, w - 2, y, color, alpha, size);
			}
		
		return this;
	}
	
	public ImageCanvas drawTopHistogram(ArrayList<Double> corrs, boolean doIt) {
		if (!doIt)
			return this;
		
		ImageCanvas ic = getImage().io().rotate90().canvas().drawSideHistogram(corrs, doIt, 0.025)
				.getImage().io().rotate90().canvas();
		
		return ic;
	}
	
	public ImageCanvas drawImage(Image image2, int ox, int oy) {
		image = image.io().drawAndFillRect(ox, oy, image2.getAs2A()).getImage();
		return this;
	}
	
	public ImageCanvas text(int x, int y, String text, Color color) {
		image.io().ip().getProcessor().setColor(color);
		image.io().ip().getProcessor().drawString(text, x, y);
		return this;
	}
	
	public ImageCanvas text(int x, int y, String text, Color color, int size) {
		image.io().ip().getProcessor().setColor(color);
		image.io().ip().getProcessor().setFont(new Font("", Font.BOLD, size));
		image.io().ip().getProcessor().drawString(text, x, y);
		image.io().ip().getProcessor().reset();
		return this;
	}
	
	public ImageCanvas text(int x, int y, String text, Color color, int size, TextJustification justification) {
		image.io().ip().getProcessor().setJustification(justification.getValue());
		image.io().ip().getProcessor().setColor(color);
		image.io().ip().getProcessor().setFont(new Font("", Font.BOLD, size));
		image.io().ip().getProcessor().drawString(text, x, y);
		image.io().ip().getProcessor().reset();
		image.io().ip().getProcessor().setJustification(TextJustification.LEFT.getValue());
		return this;
	}
	
	public ImageCanvas drawRectangle(int x, int y, int w, int h, Color c, int thickness) {
		int ci = c.getRGB();
		return drawLine(x, y, x + w, y, ci, 0, thickness)
				.drawLine(x + w, y, x + w, y + h, ci, 0, thickness)
				.drawLine(x + w, y + h, x, y + h, ci, 0, thickness)
				.drawLine(x, y + h, x, y, ci, 0, thickness);
	}
	
	public ImageCanvas drawRectangle(int x, int y, int w, int h, int ci, int thickness) {
		return drawLine(x, y, x + w, y, ci, 0, thickness)
				.drawLine(x + w, y, x + w, y + h, ci, 0, thickness)
				.drawLine(x + w, y + h, x, y + h, ci, 0, thickness)
				.drawLine(x, y + h, x, y, ci, 0, thickness);
	}
	
	public ImageCanvas drawRectanglePoints(int x, int y, int w, int h, Color c, int thickness) {
		int ci = c.getRGB();
		return drawLine(x, y, x + 1, y + 1, ci, 0, thickness)
				.drawLine(x + w, y, x + w + 1, y + 1, ci, 0, thickness)
				.drawLine(x + w, y + w, x + w + 1, y + h + 1, ci, 0, thickness)
				.drawLine(x, y + h, x + 1, y + h + 1, ci, 0, thickness);
	}
	
	public static void markPoint(final int x, final int y, ArrayList<RunnableOnImage> postProcessing, final Color color) {
		postProcessing.add(new RunnableOnImage() {
			@Override
			public Image postProcess(Image in) {
				return in.io().canvas().drawRectanglePoints(x - 5, y - 5, 10, 10, color, 0).getImage();
			}
		});
	}
	
	private static RunnableWithBooleanResult truePP = new RunnableWithBooleanResult() {
		
		@Override
		public boolean enabled() {
			return true;
		}
	};
	
	public static void markPoint2(final int x, final int y, ArrayList<RunnableOnImage> postProcessing) {
		markPoint2(x, y, postProcessing, truePP);
	}
	
	public static void markPoint2(final int x, final int y, ArrayList<RunnableOnImage> postProcessing, final RunnableWithBooleanResult check) {
		postProcessing.add(new RunnableOnImage() {
			@Override
			public Image postProcess(Image in) {
				if (check.enabled())
					return in.io().canvas().drawRectanglePoints(x - 15, y - 15, 30, 30, Color.ORANGE, 0).getImage();
				else
					return in;
			}
		});
	}
	
	public static void text(final int x, final int y, final String text, final Color color, ArrayList<RunnableOnImage> postProcessing) {
		text(x, y, text, color, postProcessing, truePP);
	}
	
	public static void text(final int x, final int y, final String text, final Color color, ArrayList<RunnableOnImage> postProcessing,
			final RunnableWithBooleanResult check) {
		postProcessing.add(new RunnableOnImage() {
			@Override
			public Image postProcess(Image in) {
				if (check.enabled())
					return in.io().canvas().text(x, y, text, color).getImage();
				else
					return in;
			}
		});
	}
	
	public static int[][] getTextImage(int w, int h, String message) {
		Image i = new Image(w, h, ImageOperation.BACKGROUND_COLORint);
		return i.io().canvas().text(20, 65, message, Color.GRAY, 25).getImage().getAs2A();
	}
	
	public ImageCanvas drawLine(java.awt.Point p1, java.awt.Point p2, int color, double alpha, int size) {
		return drawLine(p1.x, p1.y, p2.x, p2.y, color, alpha, size);
	}
	
	public ImageCanvas drawLine(Vector2i v1, Vector2i v2, int rgb, double alpha, int size) {
		return drawLine(v1.x, v1.y, v2.x, v2.y, rgb, alpha, size);
	}
	
	public ImageCanvas drawClock(int r, int border, ImageData id, Color clockBorderColor, Color clockHandColor, Color clockTextColor) {
		int w = image.getWidth();
		int h = image.getHeight();
		if (w < r * 2 || h < r * 2)
			return this;
		else {
			int x = w - r - 3 * border;
			int y = h - r - 3 * border;
			ImageCanvas canvas = drawCircle(
					x,
					y, r, clockBorderColor.getRGB(), 0.5, 2);
			canvas = canvas.text(2 + w - r - 4 * border, h - 2 * r - 3 * border, "0", clockTextColor);
			canvas = canvas.text(5 + w - r - 5 * border, h, "12", clockTextColor);
			canvas = canvas.text(w - 2 * border, h - r - 1 * border - border / 2, "6", clockTextColor);
			canvas = canvas.text(w - 2 * r - 6 * border - 5, h - r - 1 * border - border / 2, "18", clockTextColor);
			Long t = id.getParentSample().getSampleFineTimeOrRowId();
			if (t != null) {
				Date date = new Date(t);
				double day = date.getHours() / 24d + date.getMinutes() / 24d / 60d + date.getSeconds() / 24d / 60d / 60d;
				day = day * 2 * Math.PI - Math.PI / 2;
				canvas = canvas.drawLine(x, y, (int) (x + r * Math.cos(day)), (int) (y + r * Math.sin(day)), clockHandColor.getRGB(), 0.5, 1);
			}
			return canvas;
		}
	}
	
	public ImageCanvas markPoints(ArrayList<PositionAndColor> pixels, int color, double alpha) {
		if (pixels != null)
			for (PositionAndColor pac : pixels) {
				drawLine(pac.x, pac.y, pac.x + 1, pac.y, color, alpha, 1);
			}
		return this;
	}
	
}
