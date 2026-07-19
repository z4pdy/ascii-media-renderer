package com.zapdy.asciimediarenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YouTubeUtils {
    public static YouTubeDirectStreamUrls getYouTubeDirectStreamUrls(String youTubeUrl, boolean enableAudio) {
        String format;
        if (enableAudio) {
            format = "worstvideo+worstaudio";
        }
        else {
            format = "worstvideo";
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
            "yt-dlp", 
            "--remote-components", "ejs:github", 
            "--js-runtimes", "node",
            "-g", 
            "-f", format, 
            youTubeUrl
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

        String youTubeDirectVideoStreamUrl = "";
        String youTubeDirectAudioStreamUrl = "";


        String line;
        try {
			while ((line = reader.readLine()) != null) {
			    if (line.startsWith("http")) {
                    if (line.contains("mime=video")) {
                        youTubeDirectVideoStreamUrl = line;
                    }
                    else if (line.contains("mime=audio") && enableAudio) {
			            youTubeDirectAudioStreamUrl = line;
                    }
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

        if (youTubeDirectVideoStreamUrl.isEmpty() || (youTubeDirectAudioStreamUrl.isEmpty() && enableAudio)) { 
            throw new RuntimeException("Failed to fetch YouTube direct stream url.");
        }
        return new YouTubeDirectStreamUrls(youTubeDirectVideoStreamUrl, youTubeDirectAudioStreamUrl);
    }

    public static String getYouTubeUrlFromSearchQuery(String searchQuery) {
        String youTubeUrl = "";
        ProcessBuilder processBuilder = new ProcessBuilder(
            "yt-dlp", 
            "--remote-components", "ejs:github", 
            "--js-runtimes", "node",
            "ytsearch1:".concat(searchQuery), 
            "--print", "webpage_url" 
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
			        youTubeUrl = line;
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
    
        if (youTubeUrl.isEmpty()) {
            throw new RuntimeException("Failed to fetch YouTube Url from search query.");
        }

        return youTubeUrl;
    } 
}
