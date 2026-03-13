package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;

public class AsciiMediaRenderer {
    private static final double RED_WEIGHT = 0.21;
    private static final double GREEN_WEIGHT = 0.72;
    private static final double BLUE_WEIGHT = 0.07;

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
    
    public static void printAsciiImage(BufferedImage image) {
        for (int h = 0; h < image.getHeight(); h++) {
            for (int w = 0; w < image.getWidth(); w++) {
                double brightness = getBrightnessFromRGB(image.getRGB(w, h));
                IO.print(getCharacterFromBrightness(brightness));
            }
            IO.println();
        }
    }
}
