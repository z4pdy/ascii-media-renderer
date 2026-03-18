package com.zapdy.asciimediarenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YouTubeUtils {
    public static String getYouTubeDirectVideoStreamUrl(String youtubeUrl) {
        String youTubeDirectVideoStreamUrl = "";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "yt-dlp", "--js-runtimes", "node", "-g", "-f", "bv", youtubeUrl
            );            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            youTubeDirectVideoStreamUrl = reader.readLine().toString();
            process.waitFor();
		} 
        catch (IOException e) {
			e.printStackTrace();
		} 
        catch (InterruptedException e) {
			e.printStackTrace();
		}
        return youTubeDirectVideoStreamUrl;
    }
}
