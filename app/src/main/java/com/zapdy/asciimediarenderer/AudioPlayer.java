package com.zapdy.asciimediarenderer;

import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

public class AudioPlayer {
    private FFmpegFrameGrabber audioGrabber;
    private SourceDataLine line;

    public AudioPlayer(FFmpegFrameGrabber audioGrabber) {
        this.audioGrabber = audioGrabber;
    }

    private static byte[] convertSamplesToBytes(Frame frame) {
        ShortBuffer buffer = (ShortBuffer) frame.samples[0];
        byte[] bytes = new byte[buffer.remaining() * 2];

        int index = 0;
        while (buffer.hasRemaining()) {
            short sample = buffer.get();
            bytes[index++] = (byte) (sample & 0xff);
            bytes[index++] = (byte) ((sample >> 8) & 0xff);
        }

        return bytes;
    }

    public void prepare() {
        try {
			audioGrabber.start();
		} 
        catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException("Failed to start FFmpegFrameGrabber", e);
		}
        AudioFormat format = new AudioFormat(
            audioGrabber.getSampleRate(),
            16,
            audioGrabber.getAudioChannels(),
            true,
            false
        );

        try {
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
        } 
        catch (LineUnavailableException e) {
            close();
            throw new RuntimeException("Failed to open audio output line", e);
        }

    }

    public void play() {
        line.start();
        Frame audioFrame;
        try {
			while ((audioFrame = audioGrabber.grabSamples()) != null) {
                byte[] audioBytes = convertSamplesToBytes(audioFrame);
                line.write(
                    audioBytes,
                    0,
                    audioBytes.length
                );
			}
		} 
        catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException("Failed to grab audio frame", e);
		}
    }

    public void close() {
        line.drain();
        line.close();
        try {
			audioGrabber.close();
		} 
        catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            throw new RuntimeException("Failed to close audio grabber", e);
		}
    }
}
