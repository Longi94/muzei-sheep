package in.dragonbra.muzeisheepbackend.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * @author lngtr
 * @since 2018-12-24
 */
public class ImageTool {

    /**
     * Calculate image similarity. The lower the better. 0 is identical. This assumes that the images contain the same
     * amount of pixels. Does not assume anything about the width and height, just goes through the pixels one by one.
     * BGR byte order assumed.
     *
     * @param a an image file
     * @param b an image file
     * @return the similarity
     * @throws IOException io exception
     */
    public static double similarity(File a, File b) throws IOException {
        BufferedImage image1 = ImageIO.read(a);
        BufferedImage image2 = ImageIO.read(b);

        byte[] pixels1 = ((DataBufferByte) image1.getRaster().getDataBuffer()).getData();
        byte[] pixels2 = ((DataBufferByte) image2.getRaster().getDataBuffer()).getData();

        double sum = 0.0;

        for (int i = 0; i < pixels1.length; i += 3) {
            double r1 = (pixels1[i + 2] & 0xff) / 255.0;
            double g1 = (pixels1[i + 1] & 0xff) / 255.0;
            double b1 = (pixels1[i] & 0xff) / 255.0;

            double r2 = (pixels2[i + 2] & 0xff) / 255.0;
            double g2 = (pixels2[i + 1] & 0xff) / 255.0;
            double b2 = (pixels2[i] & 0xff) / 255.0;

            double distance = Math.sqrt(
                    Math.pow(r2 - r1, 2) +
                            Math.pow(g2 - g1, 2) +
                            Math.pow(b2 - b1, 2)
            );

            sum += distance;
        }

        return sum / (pixels1.length / 3.0);
    }
}
