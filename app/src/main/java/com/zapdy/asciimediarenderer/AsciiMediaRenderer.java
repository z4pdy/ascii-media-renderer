package com.zapdy.asciimediarenderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameUtils;

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
    
    public static void displayAsciiImage(BufferedImage image, int terminalColumns, int terminalRows) {
        displayAsciiImage(image, terminalColumns, terminalRows, false);
    }

    public static void displayAsciiImage(BufferedImage image, int terminalColumns, int terminalRows, Boolean clearTerminal) {
        StringBuilder asciiImage = new StringBuilder();
        terminalRows = terminalRows - BOTTOM_OFFSET;
        image = resizeImage(image, terminalColumns / 2, terminalRows);
        int leftOffset = (terminalColumns - image.getWidth()) / 2;
        for (int h = 0; h < image.getHeight(); h++) {
            for (int i = 0; i < leftOffset; i++) {
                asciiImage.append(" ");
            }
            for (int w = 0; w < image.getWidth(); w++) {
                double brightness = getBrightnessFromRGB(image.getRGB(w, h));
                asciiImage.append(getCharacterFromBrightness(brightness));
            }
            asciiImage.append("\n");
        }
        if (clearTerminal) {
            IO.print("\033[H" + asciiImage.toString());
            System.out.flush();        
        }
        else {
            IO.print(asciiImage.toString());
        }
    }

    public static void displayAsciiVideo(FFmpegFrameGrabber videoGrabber, int terminalColumns, int terminalRows) {
        TerminalUtils.clearTerminal();
        try {
			videoGrabber.start();
            double frameRate = videoGrabber.getFrameRate();
            long frameDelay = (long) (1000 / frameRate);
            Frame frame;
            while ((frame = videoGrabber.grabFrame()) != null) {
                BufferedImage image = Java2DFrameUtils.toBufferedImage(frame);
                displayAsciiImage(image, terminalColumns, terminalRows, true);
                Thread.sleep(frameDelay);
            }
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
