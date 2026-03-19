package edu.hitsz.application;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;

public class MusicThread extends Thread {
    private final String path;
    private Clip clip;
    private boolean loop = false;
    private volatile boolean isPlaying = false;

    public MusicThread(String path) {
        this.path = path;
    }

    public MusicThread(String path, boolean loop) {
        this.path = path;
        this.loop = loop;
    }

    @Override
    public void run() {
        try {
            File file = new File(path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(ais);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }

            clip.start();
            isPlaying = true;

            // 等待播放结束（非循环音效）
            if (!loop) {
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        isPlaying = false;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
        isPlaying = false;
        loop = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}