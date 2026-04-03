package edu.hitsz.application;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.R;

/**
 * Android 音频管理器
 *
 * 职责：
 * 1. 管理背景音乐（MediaPlayer）
 * 2. 管理短音效（SoundPool）
 * 3. 提供统一播放 / 暂停 / 停止接口
 * 4. 提供音乐总开关
 */
public class AudioManager {

    // ===== 全局 Application Context =====
    private static Context appContext;

    // ===== 音乐总开关 =====
    private static boolean musicEnabled = true;

    // ===== BGM（长音频）=====
    private static MediaPlayer bgmPlayer;
    private static MediaPlayer bossBgmPlayer;

    // ===== 音效（短音频）=====
    private static SoundPool soundPool;
    private static final Map<String, Integer> soundMap = new HashMap<>();

    // ===== 初始化状态 =====
    private static boolean initialized = false;

    // =====================================================================
    // 兼容旧桌面版常量（仅为避免旧代码报错）
    // =====================================================================
    public static final String BGM = "bgm";
    public static final String BOSS_BGM = "bgm_boss";
    public static final String BULLET_HIT = "bullet_hit";
    public static final String BOMB_EXPLODE = "bomb_explosion";
    public static final String PROP_ACTIVATE = "get_supply";
    public static final String GAME_OVER = "game_over";
    public static final String BULLET_SHOOT = "bullet";

    /**
     * 初始化音频系统（建议在 MainActivity 启动时调用一次）
     */
    public static void init(Context context) {
        if (initialized) return;

        appContext = context.getApplicationContext();

        // 1. 初始化 SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(10)
                .build();

        // 2. 预加载短音效
        soundMap.put("bullet_hit", soundPool.load(appContext, R.raw.bullet_hit, 1));
        soundMap.put("bomb_explosion", soundPool.load(appContext, R.raw.bomb_explosion, 1));
        soundMap.put("get_supply", soundPool.load(appContext, R.raw.get_supply, 1));
        soundMap.put("game_over", soundPool.load(appContext, R.raw.game_over, 1));
        soundMap.put("bullet", soundPool.load(appContext, R.raw.bullet, 1));

        // 3. 初始化 BGM（懒加载）
        bgmPlayer = null;
        bossBgmPlayer = null;

        initialized = true;
    }

    // =====================================================================
    // 音乐开关
    // =====================================================================

    public static void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) {
            pauseAll();
        }
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    public static void toggleMusic() {
        setMusicEnabled(!musicEnabled);
    }

    // =====================================================================
    // BGM 播放（MediaPlayer）
    // =====================================================================

    public static void playBgm() {
        if (!musicEnabled || appContext == null) return;
        ensureInit();

        stopBossBgm();

        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(appContext, R.raw.bgm);

            if (bgmPlayer == null) {
                System.out.println("BGM加载失败！");
                return;
            }

            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
            }
        }

        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    public static void playBossBgm() {
        if (!musicEnabled || appContext == null) return;
        ensureInit();

        stopBgm();

        if (bossBgmPlayer == null) {
            bossBgmPlayer = MediaPlayer.create(appContext, R.raw.bgm_boss);
            if (bossBgmPlayer != null) {
                bossBgmPlayer.setLooping(true);
            }
        }

        if (bossBgmPlayer != null && !bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.start();
        }
    }

    public static void stopBgm() {
        if (bgmPlayer != null) {
            try {
                if (bgmPlayer.isPlaying()) {
                    bgmPlayer.stop();
                }
            } catch (Exception ignored) {}
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }

    public static void stopBossBgm() {
        if (bossBgmPlayer != null) {
            try {
                if (bossBgmPlayer.isPlaying()) {
                    bossBgmPlayer.stop();
                }
            } catch (Exception ignored) {}
            bossBgmPlayer.release();
            bossBgmPlayer = null;
        }
    }

    public static void pauseAllBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
        if (bossBgmPlayer != null && bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.pause();
        }
    }

    public static void resumeBgm() {
        if (!musicEnabled) return;

        if (bossBgmPlayer != null) {
            try {
                bossBgmPlayer.start();
                return;
            } catch (Exception ignored) {}
        }

        if (bgmPlayer != null) {
            try {
                bgmPlayer.start();
            } catch (Exception ignored) {}
        }
    }

    // =====================================================================
    // 短音效播放（SoundPool）
    // =====================================================================

    private static void playSound(String key) {
        if (!musicEnabled || soundPool == null || !soundMap.containsKey(key)) return;

        Integer soundId = soundMap.get(key);
        if (soundId != null) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    // =====================================================================
    // 新接口（推荐用这个，不需要 context）
    // =====================================================================

    public static void playBullet() {
        ensureInit();
        playSound("bullet");
    }

    public static void playBulletHit() {
        ensureInit();
        playSound("bullet_hit");
    }

    public static void playBombExplosion() {
        ensureInit();
        playSound("bomb_explosion");
    }

    public static void playGetSupply() {
        ensureInit();
        playSound("get_supply");
    }

    public static void playGameOver() {
        ensureInit();
        playSound("game_over");
    }

    // =====================================================================
    // 通用控制接口
    // =====================================================================

    public static void pauseAll() {
        pauseAllBgm();
    }

    public static void resumeAll() {
        resumeBgm();
    }

    public static void stopAll() {
        stopBgm();
        stopBossBgm();
    }

    // =====================================================================
    // 兼容旧接口（为了避免旧代码报错）
    // =====================================================================

    public static void playOnce(String key) {
        ensureInit();
        playSound(key);
    }

    public static void playAudio(String key, boolean loop, String alias) {
        ensureInit();

        if ("bgm".equals(key)) {
            playBgm();
            return;
        }
        if ("bgm_boss".equals(key)) {
            playBossBgm();
            return;
        }

        playSound(key);
    }

    public static void stopAudio(String key) {
        if ("boss".equals(key)) {
            stopBossBgm();
        } else if ("bgm".equals(key)) {
            stopBgm();
        }
    }

    // =====================================================================
    // 释放资源（退出整个应用时调用）
    // =====================================================================

    public static void release() {
        stopAll();

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        soundMap.clear();
        initialized = false;
    }

    // =====================================================================
    // 内部工具
    // =====================================================================

    private static void ensureInit() {
        if (!initialized && appContext != null) {
            init(appContext);
        }
    }
}