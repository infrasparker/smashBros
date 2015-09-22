package src.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageHandler {
    
    public static int calcHeightViaWidth(int w, BufferedImage i) {
        return (int)Math.rint((double)w * i.getHeight() / i.getWidth());
    }
    
    public static int calcWidthViaHeight(int h, BufferedImage i) {
        return (int)Math.rint((double)h * i.getWidth() / i.getHeight());
    }
    
    public static BufferedImage resize(BufferedImage i, int newW) {
        return resize(i, (double) newW / i.getWidth());
    }
    
    public static BufferedImage resize(BufferedImage i, double s) {
        AffineTransform tx = AffineTransform.getScaleInstance(s, s);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(i, null);
    }
    
    public static BufferedImage rotateCW(BufferedImage i, double angle) {
        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(angle), i.getWidth() / 2, i.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(i, null);
    }

    public static BufferedImage flipHorizontal(BufferedImage i) {
	AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	tx.translate(-i.getWidth(), 0);
	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	return op.filter(i, null);
    }

    public static BufferedImage flipVertical(BufferedImage i) {
	AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
	tx.translate(0, -i.getWidth());
	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	return op.filter(i, null);
    }
}
