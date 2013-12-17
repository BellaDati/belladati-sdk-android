package android.graphics;

import java.awt.image.BufferedImage;

/**
 * A basic stub implementation to use for testing.
 * 
 * @author Chris Hennigfeld
 */
public class Bitmap {

	private final BufferedImage image;

	public Bitmap(BufferedImage image) {
		this.image = image;
	}

	public int getHeight() {
		return image.getHeight();
	}

	public int getWidth() {
		return image.getWidth();
	}
}
