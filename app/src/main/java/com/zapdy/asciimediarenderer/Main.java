package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jline.terminal.*;

public class Main {
    public static void main(String[] args) {
        Size size = new Size(170, 40);

        Size detectedTerminalSize;
		try {
			detectedTerminalSize = TerminalBuilder.builder().system(true).build().getSize();
            if (detectedTerminalSize.getColumns() != 0 && detectedTerminalSize.getRows() != 0) {
                size = detectedTerminalSize;
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

        File file = new File(args[0]);
    	try {
			BufferedImage image = ImageIO.read(file);
            if (image == null) {
                IO.println("No registered ImageReader was able to read stream");
                return;
            }
            AsciiMediaRenderer.printAsciiImage(image, size.getColumns(), size.getRows()); 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
