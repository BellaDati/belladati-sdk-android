package android.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * A basic stub implementation to use for testing.
 * 
 * @author Chris Hennigfeld
 */
public class BitmapFactory {

	public static Bitmap decodeStream(InputStream input) throws IOException {
		BufferedImage image = ImageIO.read(input);
		if (image == null) {
			return null;
		}
		return new Bitmap(image);
	}
}
