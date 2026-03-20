package com.zapdy.asciimediarenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YouTubeUtils {
    public static String getYouTubeDirectVideoStreamUrl(String youtubeUrl) {
        String youTubeDirectVideoStreamUrl = "";
        ProcessBuilder processBuilder = new ProcessBuilder(
            "yt-dlp", 
            "--remote-components", "ejs:github", 
            "-g", 
            "-f", "bv", 
            youtubeUrl
        );            
        processBuilder.redirectErrorStream(true);
        Process process;
		try {
			process = processBuilder.start();
		} 
        catch (IOException e) {
            throw new RuntimeException("Failed to start yt-dlp process", e);
		}
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        try {
			while ((line = reader.readLine()) != null) {
			    if (line.startsWith("http")) {
			        youTubeDirectVideoStreamUrl = line;
			        break;
			    }
			}
		} 
        catch (IOException e) {
			throw new RuntimeException("Failed to read yt-dlp process output", e);
		}

        try {
			process.waitFor();
		} 
        catch (InterruptedException e) {
			throw new RuntimeException("yt-dlp process was interrupted", e);
		}
        return youTubeDirectVideoStreamUrl;
    }
}
