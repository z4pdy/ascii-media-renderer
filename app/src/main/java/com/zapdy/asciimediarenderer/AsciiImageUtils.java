package com.zapdy.asciimediarenderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.Dimension;


public class AsciiImageUtils {
    private static final double RED_WEIGHT = 0.21;
    private static final double GREEN_WEIGHT = 0.72;
    private static final double BLUE_WEIGHT = 0.07;
    private static final char[] ASCII_CHARACTERS = " -.`-,:'_;~*\"\\/^i!rl+|I=)(t<j>f1}{vx?L7z][JcTnuysYkohF4eaV3205pbqdXPZUC69K#AwHmg8E%&S$DORNGQBMW@".toCharArray();
    private static final double CHAR_RATIO = 0.5;

    public static int getBrightnessFromRGB(int rgb) {
        final int a = (rgb >> 24) & 0xFF;
        if (a == 0) {
            return -1; // transparent
        }
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = rgb & 0xFF;

        return (int) ((RED_WEIGHT * r) + (GREEN_WEIGHT * g) + (BLUE_WEIGHT * b));
    }
    
    public static char getCharacterFromBrightness(int brightness, boolean reversed) {
        double brightnessPercentage = ((double) brightness) / 255;
        int asciiCharIndex = (int) (brightnessPercentage * (ASCII_CHARACTERS.length - 1));
        if (reversed) {
            asciiCharIndex = (ASCII_CHARACTERS.length - 1) - asciiCharIndex; 
        }
        return ASCII_CHARACTERS[asciiCharIndex];
    }
    
    public static char getBackgroundCharacter(BufferedImage image, boolean reversed) {
        char backgroundChar = ' ';
        int topLeftBrightness = getBrightnessFromRGB(image.getRGB(0, 0));
        int topRightBrightness = getBrightnessFromRGB(image.getRGB(image.getWidth() - 1, 0));
        int bottomLeftBrightness = getBrightnessFromRGB(image.getRGB(0, image.getHeight() - 1));
        int bottomRightBrightness = getBrightnessFromRGB(image.getRGB(image.getWidth() - 1, image.getHeight() - 1));

        if (topLeftBrightness == topRightBrightness &&
            topLeftBrightness == bottomLeftBrightness &&
            topLeftBrightness == bottomRightBrightness) {

            backgroundChar = getCharacterFromBrightness(topLeftBrightness, reversed);
        }
        return backgroundChar;
    } 


    public static BufferedImage adjustImageContrast(BufferedImage image) {
        RescaleOp contrast = new RescaleOp(1.4f, 10f, null);
        BufferedImage contrastedImage = contrast.filter(image, null); 
        return contrastedImage; 
    }

    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = resizedImage.createGraphics();
        graphics2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        graphics2d.drawImage(image, 0, 0, width, height, null);
        graphics2d.dispose();
        return resizedImage;
    }

    public static Dimension calculateImageSize(BufferedImage image, int width, int height) {
        double ratio = ((double) image.getWidth()) / image.getHeight();
        ratio /= CHAR_RATIO;
        int newWidth, newHeight;

        newHeight = height;
        newWidth = (int) (newHeight * ratio);


        if (newWidth > width) {
            newWidth = width;
            newHeight = (int) (newWidth / ratio); 
        }
         
        return new Dimension(newWidth, newHeight);
	}
}
