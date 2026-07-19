package com.zapdy.asciimediarenderer;

import org.bytedeco.ffmpeg.global.avutil;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameUtils;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class AsciiMediaRenderer {
    private static final int BOTTOM_OFFSET = 2;

    public static void displayAsciiImage(BufferedImage image, int terminalColumns, int terminalRows, boolean reversed, boolean transparentBackground) {
        StringBuilder asciiImage = new StringBuilder();
        terminalRows = terminalRows - BOTTOM_OFFSET;
        Dimension newImageSize = AsciiImageUtils.calculateImageSize(image, terminalColumns, terminalRows);
        image = AsciiImageUtils.resizeImage(image, newImageSize.width, newImageSize.height);
        image = AsciiImageUtils.adjustImageContrast(image);
        char backgroundChar = ' ';
        if (transparentBackground) {
            backgroundChar = AsciiImageUtils.getBackgroundCharacter(image, reversed);
        }
        int leftOffset = (terminalColumns - image.getWidth()) / 2;
        for (int h = 0; h < image.getHeight(); h++) {
            for (int i = 0; i < leftOffset; i++) {
                asciiImage.append(" ");
            }
            for (int w = 0; w < image.getWidth(); w++) {
                int brightness = AsciiImageUtils.getBrightnessFromRGB(image.getRGB(w, h));
                if (brightness == -1) {
                    asciiImage.append(" ");
                    continue;
                }
                char character = AsciiImageUtils.getCharacterFromBrightness(brightness, reversed);
                if (transparentBackground && backgroundChar != ' ' && backgroundChar == character) {
                    character = ' ';
                }
                asciiImage.append(character);
            }
            asciiImage.append("\n");
        }
        IO.print(asciiImage.toString());
    }

	public static void playAsciiVideo(FFmpegFrameGrabber videoGrabber, FFmpegFrameGrabber audioGrabber, int terminalColumns, int terminalRows, boolean reversed, boolean transparentBackground) {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);

        AudioPlayer audioPlayer;
        if (audioGrabber != null) {
            audioPlayer = new AudioPlayer(audioGrabber);
            audioPlayer.prepare();
        }
        else {
            audioPlayer = null;
        }
        TerminalUtils.clearTerminal();
        try {
			videoGrabber.start();
		} 
        catch (Exception e) {
            try {
                videoGrabber.close();
            } 
            catch (org.bytedeco.javacv.FrameGrabber.Exception e2) {
                e2.printStackTrace();
            }

            throw new RuntimeException("Failed to start FFmpegFrameGrabber", e);
		}
        long firstTimestamp = -1;
        long playbackStart = -1;
        Frame frame;
        try {
			while ((frame = videoGrabber.grabImage()) != null) {
                long frameTimestamp = videoGrabber.getTimestamp();

                if (firstTimestamp == -1) {
                    firstTimestamp = frameTimestamp;
                    playbackStart = System.nanoTime();

                    if (audioPlayer != null) {
                        Thread audioThread = new Thread(() -> {
                            audioPlayer.play();
                        });
                        audioThread.start();
                    }
                }

			    BufferedImage image = Java2DFrameUtils.toBufferedImage(frame);

                IO.print("\033[H");
			    displayAsciiImage(image, terminalColumns, terminalRows, reversed, transparentBackground);
                System.out.flush();

                long actualTimestamp = (System.nanoTime() - playbackStart) / 1000;
                long delay = ((frameTimestamp - firstTimestamp) - actualTimestamp) / 1000;
                if (delay < -500) {
                    throw new RuntimeException("Terminal resolution is too high for real-time ASCII playback");
                }
                else if (delay < 0) {
                    delay = 0;
                }
			    Thread.sleep(delay);
			}
		} 
        catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            throw new RuntimeException("Failed to grab frame", e);
		} 
        catch (InterruptedException e) {
            throw new RuntimeException("Error occured during frame delay sleep", e);
		}
        finally {
            if (audioPlayer != null) {
                audioPlayer.close();
            }
            try {
				videoGrabber.close();
			} 
            catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                throw new RuntimeException("Failed to close video grabber", e);
			}
        }
    }
}
