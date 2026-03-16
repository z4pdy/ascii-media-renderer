package com.zapdy.asciimediarenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jline.terminal.*;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;

public class Main {
    public static void main(String[] args) {
        // TODO: add --reversed flag
        if (args.length != 2) {
            IO.println("Incorrect number of arguments");
            System.exit(0);
        }

        Size size = TerminalUtils.getTerminalSize();
        File file = new File(args[1]);
        if (args[0].equals("--image") || args[0].equals("-i")) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            if (image == null) {
                IO.println("No registered ImageReader was able to read stream");
                return;
            }
            AsciiMediaRenderer.displayAsciiImage(image, size.getColumns(), size.getRows()); 
        }
        else if (args[0].equals("--video") || args[0].equals("-v")) {
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);

            FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
            videoGrabber.setOption("loglevel", "quiet");
            AsciiMediaRenderer.displayAsciiVideo(videoGrabber, size.getColumns(), size.getRows()); 
        }
        else if (args[0].equals("--youtube") || args[0].equals("-y")) {
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);

            String youtubeLink = args[1];
            FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(youtubeLink);
            videoGrabber.setOption("loglevel", "quiet");
            AsciiMediaRenderer.displayAsciiVideo(videoGrabber, size.getColumns(), size.getRows()); 
        }
    }
}
