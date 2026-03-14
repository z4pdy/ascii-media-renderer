package com.zapdy.asciimediarenderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class AsciiMediaRenderer {
    private static final double RED_WEIGHT = 0.21;
    private static final double GREEN_WEIGHT = 0.72;
    private static final double BLUE_WEIGHT = 0.07;
    private static final int BOTTOM_OFFSET = 1;
    private static final char[] ASCII_CHARACTERS = " -.`-,:'_;~*\"\\/^i!rl+|I=)(t<j>f1}{vx?L7z][JcTnuysYkohF4eaV3205pbqdXPZUC69K#AwHmg8E%&S$DORNGQBMW@".toCharArray();

    private static char getCharacterFromBrightness(double brightness) {
        double brightnessPercentage = brightness / 255;
        int asciiCharIndex = (int) (brightnessPercentage * (ASCII_CHARACTERS.length - 1));
        return ASCII_CHARACTERS[asciiCharIndex];
    }

    private static double getBrightnessFromRGB(int rgb) {
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = rgb & 0xFF;

        return (RED_WEIGHT * r) + (GREEN_WEIGHT * g) + (BLUE_WEIGHT * b);
    }

    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        Graphics2D graphics2d = resizedImage.createGraphics();
        graphics2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        graphics2d.drawImage(image, 0, 0, width, height, null);
        graphics2d.dispose();
        return resizedImage;
    }
    
    public static void printAsciiImage(BufferedImage image, int terminalColumns, int terminalRows) {
        terminalRows = terminalRows - BOTTOM_OFFSET;
        image = resizeImage(image, terminalColumns / 2, terminalRows);
        int leftOffset = (terminalColumns - image.getWidth()) / 2;
        for (int h = 0; h < image.getHeight(); h++) {
            for (int i = 0; i < leftOffset; i++) {
                IO.print(" ");
            }
            for (int w = 0; w < image.getWidth(); w++) {
                double brightness = getBrightnessFromRGB(image.getRGB(w, h));
                IO.print(getCharacterFromBrightness(brightness));
            }
            IO.print("\n");
        }
    }
}
