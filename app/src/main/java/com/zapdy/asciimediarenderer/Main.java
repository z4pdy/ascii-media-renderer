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
        if (args.length != 2) {
            IO.println("Incorrect number of arguments");
            System.exit(0);
        }

        Size size = TerminalUtils.getTerminalSize();
        
        if (args[0].equals("--image") || args[0].equals("-i")) {
            File file = new File(args[1]);
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
        else if (args[0].equals("--video") || args[0].equals("-v")) {
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);

            File file = new File(args[1]);
            FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
            videoGrabber.setOption("loglevel", "quiet");
            AsciiMediaRenderer.printAsciiVideo(videoGrabber, size.getColumns(), size.getRows()); 
        }
    }
}
