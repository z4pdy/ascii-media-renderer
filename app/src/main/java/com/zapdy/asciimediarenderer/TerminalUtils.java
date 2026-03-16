package com.zapdy.asciimediarenderer;

import java.io.IOException;

import org.jline.terminal.Size;
import org.jline.terminal.TerminalBuilder;

public class TerminalUtils {
    private static final int DEFAULT_TERMINAL_WIDTH = 170;
    private static final int DEFAULT_TERMINAL_HEIGHT = 40;

    public static Size getTerminalSize() {
        Size size = new Size(DEFAULT_TERMINAL_WIDTH, DEFAULT_TERMINAL_HEIGHT);
        Size detectedTerminalSize;
        try {
            detectedTerminalSize = TerminalBuilder.builder().system(true).build().getSize();
            if (detectedTerminalSize.getColumns() != 0 && detectedTerminalSize.getRows() != 0) {
                size = detectedTerminalSize;
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
}
