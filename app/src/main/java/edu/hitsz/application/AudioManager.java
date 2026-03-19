// AudioManager.java
package edu.hitsz.application;

import java.util.HashMap;
import java.util.Map;

/**
 * 音效管理器
 */
public class AudioManager {
    private static Map<String, MusicThread> playingMusic = new HashMap<>();

    // 根据您现有的音效文件调整路径
    public static final String BGM = "src/audio/bgm.wav";
    public static final String BOSS_BGM = "src/audio/bgm_boss.wav";
    public static final String BULLET_HIT = "src/audio/bullet_hit.wav";
    public static final String BOMB_EXPLODE = "src/audio/bomb_explosion.wav";  // 修正文件名
    public static final String PROP_ACTIVATE = "src/audio/get_supply.wav";     // 修正文件名
    public static final String GAME_OVER = "src/audio/game_over.wav";
    public static final String BULLET_SHOOT = "src/audio/bullet.wav";          // 添加子弹射击音效

    /**
     * 播放音效
     * @param audioPath 音效路径
     * @param loop 是否循环
     * @param key 音效键（用于停止特定音效）
     */
    public static void playAudio(String audioPath, boolean loop, String key) {
        try {
            // 如果已经有同key的音效在播放，先停止
            if (playingMusic.containsKey(key)) {
                playingMusic.get(key).stopMusic();
            }

            MusicThread music = new MusicThread(audioPath, loop);
            music.start();
            playingMusic.put(key, music);
        } catch (Exception e) {
            System.err.println("播放音效失败: " + audioPath + " - " + e.getMessage());
        }
    }

    /**
     * 播放一次性音效（不需要停止）
     */
    public static void playOnce(String audioPath) {
        try {
            MusicThread music = new MusicThread(audioPath, false);
            music.start();
        } catch (Exception e) {
            System.err.println("播放音效失败: " + audioPath + " - " + e.getMessage());
        }
    }

    /**
     * 停止特定音效
     */
    public static void stopAudio(String key) {
        if (playingMusic.containsKey(key)) {
            playingMusic.get(key).stopMusic();
            playingMusic.remove(key);
        }
    }

    /**
     * 停止所有音效
     */
    public static void stopAll() {
        for (MusicThread music : playingMusic.values()) {
            music.stopMusic();
        }
        playingMusic.clear();
    }

    /**
     * 检查音效文件是否存在（用于调试）
     */
    public static boolean checkAudioFile(String audioPath) {
        try {
            java.io.File file = new java.io.File(audioPath);
            boolean exists = file.exists();
            System.out.println("检查音效文件: " + audioPath + " - " + (exists ? "存在" : "缺失"));
            return exists;
        } catch (Exception e) {
            System.err.println("检查音效文件失败: " + audioPath);
            return false;
        }
    }
}