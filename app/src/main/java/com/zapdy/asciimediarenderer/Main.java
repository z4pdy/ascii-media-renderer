package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
            --reversed, -r                  Reverse brightness
            --transparent-background, -t    Transparent background
            --no-audio, -n                  Disable audio
        """;
        IO.println(usage);
    }

    private static void playAsciiVideoFromYouTube(String youTubeUrl, Size size, boolean reversed, boolean transparentBackground, boolean enableAudio) {
        YouTubeDirectStreamUrls youTubeDirectStreamUrls = YouTubeUtils.getYouTubeDirectStreamUrls(youTubeUrl, enableAudio);
        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(youTubeDirectStreamUrls.videoUrl());
        FFmpegFrameGrabber audioGrabber = null;
        if (enableAudio) {
            audioGrabber = new FFmpegFrameGrabber(youTubeDirectStreamUrls.audioUrl());
        }
        AsciiMediaRenderer.playAsciiVideo(videoGrabber, audioGrabber, size.getColumns(), size.getRows(), reversed, transparentBackground);
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
        boolean transparentBackground = false;
        boolean enableAudio = true;
        
        if (args.length >= 3) {
            for (int i = 2; i < args.length; i++) {
                switch (args[i]) {
                    case "-r", "--reversed" ->
                        reversed = true;
                    case "-t", "--transparent-background" ->
                        transparentBackground = true;
                    case "-n", "--no-audio" ->
                        enableAudio = false;
                    default -> {
                        IO.println("Unknown option: " + args[i]);
                        System.exit(1);
                    }
                }
            }
        }

        Size size = TerminalUtils.getTerminalSize();

        switch (args[0]) {
            case "-i", "--image" -> {
                File file = new File(args[1]);
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
                AsciiMediaRenderer.displayAsciiImage(image, size.getColumns(), size.getRows(), reversed, transparentBackground); 
            }
            case "-v" , "--video" -> {
                File file = new File(args[1]);
                FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
                FFmpegFrameGrabber audioGrabber = null;
                if (enableAudio && videoGrabber.getAudioChannels() > 0) {
                    audioGrabber = new FFmpegFrameGrabber(file);
                }
                AsciiMediaRenderer.playAsciiVideo(videoGrabber, audioGrabber, size.getColumns(), size.getRows(), reversed, transparentBackground);
            }
            case "-y" , "--youtube" -> {
                String youTubeUrl = args[1];
                playAsciiVideoFromYouTube(youTubeUrl, size, reversed, transparentBackground, enableAudio);
            }
            case "-ys" , "--youtube-search" -> {
                String youTubeSearchQuery = args[1];
                String youTubeUrl = YouTubeUtils.getYouTubeUrlFromSearchQuery(youTubeSearchQuery);
                playAsciiVideoFromYouTube(youTubeUrl, size, reversed, transparentBackground, enableAudio);
            }
            default -> {
                IO.println("Unknown mode: ".concat(args[0]));
                printUsage();
                System.exit(1);
            }
        }
    }
}
