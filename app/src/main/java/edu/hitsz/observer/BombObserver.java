package edu.hitsz.observer;

/**
 * 炸弹道具观察者接口
 */
public interface BombObserver {
    /**
     * 当炸弹生效时被调用
     */
    void onBombActivate();
}