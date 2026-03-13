package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        File file = new File(args[0]);
        IO.println(file.getAbsolutePath());
    	try {
			BufferedImage image = ImageIO.read(file);
            if (image == null) {
                IO.println("No registered ImageReader was able to read stream");
                return;
            }
            AsciiMediaRenderer.printAsciiImage(image); 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
