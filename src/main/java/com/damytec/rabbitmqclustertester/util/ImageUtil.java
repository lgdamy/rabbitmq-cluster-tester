package com.damytec.rabbitmqclustertester.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author lgdamy on 24/01/2021
 */
public class ImageUtil {

    private ImageUtil() {}

    public static Image createPlaceHolder(int width, int height) {
        BufferedImage bi = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(Color.RED);
        g2d.setStroke(new BasicStroke((float)height / 8f));
        g2d.drawLine(0, height / 2, width, height / 2);
        g2d.setStroke(new BasicStroke((float)width / 8f));
        g2d.drawLine(width / 2, 0, width / 2, height);
        g2d.rotate(Math.toRadians(45));
        g2d.dispose();
        return bi;
    }

    public static Image brightness(Image in, float scale) {
        BufferedImage bi = new BufferedImage(in.getWidth(null), in.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(in, 0, 0, null);
        int[] pixel = { 0, 0, 0, 0 };
        float[] hsbvals = { 0, 0, 0 };
        for (int i = 0; i < bi.getHeight(); i++) {
            for (int j = 0; j < bi.getWidth(); j++) {
                bi.getRaster().getPixel( j, i, pixel );
                Color.RGBtoHSB( pixel[0], pixel[1], pixel[2], hsbvals);
                float newBrightness = Math.min(1f,hsbvals[2] * scale);
                Color c = new Color( Color.HSBtoRGB( hsbvals[0], hsbvals[1], newBrightness ) );
                bi.getRaster().setPixel( j, i, new int[]{ c.getRed(), c.getGreen(), c.getBlue(), pixel[3] } );
            }
        }
        return bi;
    }

    public static Image invertColors(Image in) {
        BufferedImage bi = new BufferedImage(in.getWidth(null), in.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(in, 0, 0, null);
        int[] pixel = { 0, 0, 0, 0 };
        float[] hsbvals = { 0, 0, 0 };
        for (int i = 0; i < bi.getHeight(); i++) {
            for (int j = 0; j < bi.getWidth(); j++) {
                bi.getRaster().getPixel( j, i, pixel );
                bi.getRaster().setPixel( j, i, new int[]{ 255 - pixel[0], 255 - pixel[1], 255 - pixel[2], pixel[3] } );
            }
        }
        return bi;
    }
}
