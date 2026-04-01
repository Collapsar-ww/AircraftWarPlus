package edu.hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.BulletProp;
import edu.hitsz.prop.SuperBulletProp;

import edu.hitsz.R;

/**
 * Android 版图片资源管理器
 *
 * 设计目标：
 * 1. 替代桌面版 FileInputStream + ImageIO
 * 2. 统一从 Android drawable 资源加载
 * 3. 统一维护对象类 -> 图片映射
 * 4. 后续可扩展做缩放缓存 / 低内存优化 / 多分辨率适配
 *
 * 使用方式：
 * 1. 在 Android Application / Activity 启动时调用：
 *      ImageManager.init(context);
 * 2. 之后全局可直接访问：
 *      ImageManager.HERO_IMAGE
 *      ImageManager.get(obj)
 */
public final class ImageManager {

    private ImageManager() {}

    /**
     * 类名-图片映射
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    /**
     * 初始化标记，防止重复初始化
     */
    private static boolean initialized = false;

    // ========================
    // 背景图
    // ========================
    public static Bitmap EASY_BACKGROUND_IMAGE;
    public static Bitmap NORMAL_BACKGROUND_IMAGE;
    public static Bitmap HARD_BACKGROUND_IMAGE;

    // ========================
    // 飞机 / 子弹 / 道具图
    // ========================
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap SUPER_ELITE_ENEMY_IMAGE;
    public static Bitmap BLOOD_PROP_IMAGE;
    public static Bitmap BOMB_PROP_IMAGE;
    public static Bitmap BULLET_PROP_IMAGE;
    public static Bitmap SUPER_BULLET_PROP_IMAGE;

    /**
     * 当前背景图
     */
    private static Bitmap currentBackground;


    /**
     * 初始化资源（必须在 Android 启动时调用一次）
     */
    public static void init(Context context) {
        if (initialized) {
            return;
        }

        // ===== 背景 =====
        EASY_BACKGROUND_IMAGE = load(context, R.drawable.bg_easy);
        NORMAL_BACKGROUND_IMAGE = load(context, R.drawable.bg_normal);
        HARD_BACKGROUND_IMAGE = load(context, R.drawable.bg_hard);

        // ===== 飞机 =====
        HERO_IMAGE = load(context, R.drawable.hero);
        MOB_ENEMY_IMAGE = load(context, R.drawable.mob);
        ELITE_ENEMY_IMAGE = load(context, R.drawable.elite);
        BOSS_ENEMY_IMAGE = load(context, R.drawable.boss);
        SUPER_ELITE_ENEMY_IMAGE = load(context, R.drawable.elite_plus);

        // ===== 子弹 =====
        HERO_BULLET_IMAGE = load(context, R.drawable.bullet_hero);
        ENEMY_BULLET_IMAGE = load(context, R.drawable.bullet_enemy);

        // ===== 道具 =====
        BLOOD_PROP_IMAGE = load(context, R.drawable.prop_blood);
        BOMB_PROP_IMAGE = load(context, R.drawable.prop_bomb);
        BULLET_PROP_IMAGE = load(context, R.drawable.prop_bullet);
        SUPER_BULLET_PROP_IMAGE = load(context, R.drawable.prop_bullet_plus);

        // 默认背景
        currentBackground = EASY_BACKGROUND_IMAGE;

        // ===== 建立类映射 =====
        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(SuperEliteEnemy.class.getName(), SUPER_ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BloodProp.class.getName(), BLOOD_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BombProp.class.getName(), BOMB_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BulletProp.class.getName(), BULLET_PROP_IMAGE);
        CLASSNAME_IMAGE_MAP.put(SuperBulletProp.class.getName(), SUPER_BULLET_PROP_IMAGE);

        initialized = true;
        System.out.println("ImageManager 初始化完成（Android）");
    }

    /**
     * 加载单张图片
     */
    private static Bitmap load(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    /**
     * 根据难度切换背景图
     */
    public static void setBackgroundByDifficulty(String difficulty) {
        if (!initialized) {
            throw new IllegalStateException("ImageManager 尚未初始化，请先调用 ImageManager.init(context)");
        }

        switch (difficulty) {
            case "简单":
                currentBackground = EASY_BACKGROUND_IMAGE;
                System.out.println("使用简单难度背景 (bg_easy)");
                break;
            case "普通":
                currentBackground = NORMAL_BACKGROUND_IMAGE;
                System.out.println("使用普通难度背景 (bg_normal)");
                break;
            case "困难":
                currentBackground = HARD_BACKGROUND_IMAGE;
                System.out.println("使用困难难度背景 (bg_hard)");
                break;
            default:
                currentBackground = EASY_BACKGROUND_IMAGE;
                System.out.println("使用默认背景 (bg_easy)");
                break;
        }
    }

    /**
     * 获取当前背景图
     */
    public static Bitmap getCurrentBackground() {
        return currentBackground;
    }

    /**
     * 保持兼容旧接口
     */
    public static Bitmap getBackground() {
        return getCurrentBackground();
    }

    /**
     * 根据类名获取图片
     */
    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    /**
     * 根据对象获取图片
     */
    public static Bitmap get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }

    /**
     * 判断是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
}