package com.zapdy.asciimediarenderer;

import java.io.IOException;

public class AudioPlayer {
    public static void playAudio(String audioUrl) {
        ProcessBuilder processBuilder = new ProcessBuilder (
            "ffplay",
            "-nodisp",
            "-autoexit",
            audioUrl
        );

        try {
			processBuilder.start();
		} 
        catch (IOException e) {
            throw new RuntimeException("Failed to start ffplay process", e);
		}

    }
}
