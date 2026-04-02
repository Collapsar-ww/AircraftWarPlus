package edu.hitsz.application;

/**
 * 音乐线程 Android 存根
 *
 * 原桌面版基于 javax.sound.sampled.Clip。
 * Phase 2 中将替换为 MediaPlayer（BGM）/ SoundPool（短音效）实现。
 * 当前版本所有方法均为空操作，保证编译通过。
 */
public class MusicThread extends Thread {

    private final String audioPath;
    private final boolean loop;
    private volatile boolean playing = false;

    public MusicThread(String audioPath, boolean loop) {
        this.audioPath = audioPath;
        this.loop = loop;
        setDaemon(true);
    }

    @Override
    public void run() {
        // TODO Phase 2: 用 MediaPlayer 播放 assets/ 下的音频文件
        playing = true;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stopMusic() {
        playing = false;
        interrupt();
    }
}
