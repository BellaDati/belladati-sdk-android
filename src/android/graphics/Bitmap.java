package android.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

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

	public boolean compress(CompressFormat format, int quality, OutputStream stream) {
		try {
			ImageIO.write(image, "png", stream);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public enum CompressFormat {
		JPEG, PNG
	}
}
