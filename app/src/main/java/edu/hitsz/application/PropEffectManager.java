package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.config.PropConfig;
import edu.hitsz.strategy.RingShoot;
import edu.hitsz.strategy.ScatterShoot;
import edu.hitsz.strategy.ShootStrategy;
import edu.hitsz.strategy.StraightShoot;  // 添加导入

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 道具效果管理器 - 使用 Runnable 接口实现多线程
 */
public class PropEffectManager {
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private static HeroAircraft hero;

    // 保存原始状态
    private static ShootStrategy originalStrategy;
    private static int originalShootNum;
    private static int originalPower;

    // 添加标志位，防止重复保存
    private static boolean isEffectActive = false;

    public static void setHero(HeroAircraft heroAircraft) {
        hero = heroAircraft;
        // 重置状态
        resetOriginalState();
    }

    /**
     * 重置原始状态（游戏开始时调用）
     */
    private static void resetOriginalState() {
        if (hero != null) {
            originalStrategy = new StraightShoot();  // 明确设置为直射策略
            originalShootNum = hero.getShootNum();
            originalPower = hero.getPower();
            isEffectActive = false;
            System.out.println("重置初始状态 - 策略: StraightShoot, 子弹数量: " + originalShootNum + ", 威力: " + originalPower);
        }
    }

    /**
     * 激活子弹道具效果 - 散射
     */
    public static void activateBulletProp() {
        if (hero == null) return;

        // 如果已经有道具效果在运行，先取消
        if (isEffectActive) {
            shutdown();
            executor = Executors.newScheduledThreadPool(2);
        }

        // 保存当前状态
        saveCurrentState();

        // 切换到散射策略并应用配置参数
        hero.setShootStrategy(new ScatterShoot());
        hero.setShootNum(PropConfig.BulletProp.SHOOT_NUM);
        hero.setPower(PropConfig.BulletProp.POWER);

        isEffectActive = true;

        System.out.println("激活散射道具效果 - 子弹数量: " + PropConfig.BulletProp.SHOOT_NUM +
                ", 威力: " + PropConfig.BulletProp.POWER + " - 持续10秒");

        // 使用 Runnable 创建定时恢复任务
        Runnable restoreTask = () -> {
            restoreOriginalState();
            System.out.println("散射效果结束，恢复原始射击");
        };

        // 10秒后恢复原始状态
        executor.schedule(restoreTask, 10, TimeUnit.SECONDS);
    }

    /**
     * 激活超级子弹道具效果 - 环射
     */
    public static void activateSuperBulletProp() {
        if (hero == null) return;

        // 如果已经有道具效果在运行，先取消
        if (isEffectActive) {
            shutdown();
            executor = Executors.newScheduledThreadPool(2);
        }

        // 保存当前状态
        saveCurrentState();

        // 切换到环射策略并应用配置参数
        hero.setShootStrategy(new RingShoot());
        hero.setShootNum(PropConfig.SuperBulletProp.SHOOT_NUM);
        hero.setPower(PropConfig.SuperBulletProp.POWER);

        isEffectActive = true;

        System.out.println("激活超级子弹道具效果 - 子弹数量: " + PropConfig.SuperBulletProp.SHOOT_NUM +
                ", 威力: " + PropConfig.SuperBulletProp.POWER + " - 持续8秒");

        // 使用 Runnable 创建定时恢复任务
        Runnable restoreTask = () -> {
            restoreOriginalState();
            System.out.println("超级子弹效果结束，恢复原始射击");
        };

        // 8秒后恢复原始状态
        executor.schedule(restoreTask, 8, TimeUnit.SECONDS);
    }

    /**
     * 保存当前状态
     */
    private static void saveCurrentState() {
        if (hero != null) {
            originalStrategy = hero.getShootStrategy();
            originalShootNum = hero.getShootNum();
            originalPower = hero.getPower();
            System.out.println("保存当前状态 - 策略: " + originalStrategy.getClass().getSimpleName() +
                    ", 子弹数量: " + originalShootNum + ", 威力: " + originalPower);
        }
    }

    /**
     * 恢复原始状态
     */
    private static void restoreOriginalState() {
        if (hero != null) {
            // 确保恢复到直射策略
            if (originalStrategy == null) {
                originalStrategy = new StraightShoot();
            }
            hero.setShootStrategy(originalStrategy);
            hero.setShootNum(originalShootNum);
            hero.setPower(originalPower);
            isEffectActive = false;
            System.out.println("恢复原始状态 - 策略: " + originalStrategy.getClass().getSimpleName() +
                    ", 子弹数量: " + originalShootNum + ", 威力: " + originalPower);
        }
    }

    /**
     * 停止所有道具效果并立即恢复状态
     */
    public static void shutdown() {
        // 立即恢复原始状态
        restoreOriginalState();

        // 关闭执行器
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 重新创建执行器
        executor = Executors.newScheduledThreadPool(2);
    }
}