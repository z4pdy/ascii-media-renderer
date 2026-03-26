package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.jline.terminal.Size;

public class Main {
    private static void printUsage() {
        String usage = """
        Usage: ascii-media-renderer [mode] <file-path | url> [flags]

        --help, -h              Show this help message

        [mode] 
            --image, -i                 Render an image as ASCII art
            --video, -v                 Render a video as ASCII art
            --youtube, -y               Render a YouTube video as ASCII art
            --youtube-search, -ys       Render a searched YouTube video as ASCII art

        [flags]
            --reversed, -r      Reverse brightness
        """;
        IO.println(usage);
    }


    private static void displayAsciiVideo(FFmpegFrameGrabber videoGrabber, Size size, boolean reversed) {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);

        videoGrabber.setOption("loglevel", "quiet");
        AsciiMediaRenderer.displayAsciiVideo(videoGrabber, size.getColumns(), size.getRows(), reversed); 
    }

    private static void displayAsciiVideoFromFile(File file, Size size, boolean reversed) {
        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
        displayAsciiVideo(videoGrabber, size, reversed);
    }

    private static void displayAsciiVideoFromYouTube(String youtubeUrl, Size size, boolean reversed) {
        String youTubeDirectVideoStreamUrl = YouTubeUtils.getYouTubeDirectVideoStreamUrl(youtubeUrl);
        if (youTubeDirectVideoStreamUrl.isEmpty()){ 
            IO.println("Failed to fetch YouTube direct video stream url.");
            System.exit(1);
        }

        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(youTubeDirectVideoStreamUrl);
        displayAsciiVideo(videoGrabber, size, reversed);
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
            printUsage();
            System.exit(0);
        }

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
            displayAsciiVideoFromFile(file, size, reversed);
        }
        else if (args[0].equals("--youtube") || args[0].equals("-y")) {
            String youtubeUrl = args[1];
            displayAsciiVideoFromYouTube(youtubeUrl, size, reversed);
        }
        else if (args[0].equals("--youtube-search") || args[0].equals("-ys")) {
            String youtubeSearchQuery = args[1];
            String youtubeUrl = YouTubeUtils.getYoutubeUrlFromSearchQuery(youtubeSearchQuery);
            if (youtubeUrl.isEmpty()) {
                IO.println("Failed to fetch YouTube Url from search query.");
                System.exit(1);
            }
            displayAsciiVideoFromYouTube(youtubeUrl, size, reversed);
        }
        else {
            IO.println("Unknown mode: ".concat(args[0]));
            printUsage();
            System.exit(0);
        }

    }
}
