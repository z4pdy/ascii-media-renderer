package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.jline.terminal.Size;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            IO.println("Incorrect number of arguments");
            System.exit(1);
        }

        boolean reversed = false;
        if (args.length == 3) {
            if (args[2].equals("--reversed") || args[2].equals("-r")) {
                reversed = true;
            }
        }

        Size size = TerminalUtils.getTerminalSize();
        File file = new File(args[1]);
        if (args[0].equals("--image") || args[0].equals("-i")) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } 
            catch (IOException e) {
                throw new RuntimeException("Failed to open the image file", e);
            }
            if (image == null) {
                throw new RuntimeException("No registered ImageReader was able to read stream");
            }
            AsciiMediaRenderer.displayAsciiImage(image, size.getColumns(), size.getRows(), reversed); 
        }
        else if (args[0].equals("--video") || args[0].equals("-v")) {
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);

            FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
            videoGrabber.setOption("loglevel", "quiet");
            AsciiMediaRenderer.displayAsciiVideo(videoGrabber, size.getColumns(), size.getRows(), reversed); 
        }
        else if (args[0].equals("--youtube") || args[0].equals("-y")) {
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);

            String youtubeUrl = args[1];
            String youTubeDirectVideoStreamUrl = YouTubeUtils.getYouTubeDirectVideoStreamUrl(youtubeUrl);
            if (youTubeDirectVideoStreamUrl.isEmpty()){ 
                IO.println("Failed to fetch YouTube direct video stream url.");
                System.exit(1);
            }
            FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(youTubeDirectVideoStreamUrl);
            videoGrabber.setOption("loglevel", "quiet");
            AsciiMediaRenderer.displayAsciiVideo(videoGrabber, size.getColumns(), size.getRows(), reversed); 
        }
    }
}
