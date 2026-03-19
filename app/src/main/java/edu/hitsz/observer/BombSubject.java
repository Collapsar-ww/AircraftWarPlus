package edu.hitsz.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 炸弹道具主题（被观察者）
 */
public class BombSubject {
    private List<BombObserver> observers = new ArrayList<>();

    public void addObserver(BombObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BombObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        // 使用新的列表避免并发修改异常
        List<BombObserver> observersToNotify = new ArrayList<>(observers);
        for (BombObserver observer : observersToNotify) {
            observer.onBombActivate();
        }
    }

    /**
     * 炸弹生效时调用此方法
     */
    public void bombActivated() {

        if (observers.isEmpty()) {
            return;
        }

        notifyObservers();

        // 通知后清空观察者列表，准备下一轮注册
        observers.clear();
    }
}