package edu.hitsz.aircraft;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public abstract class AbstractAircraft extends AbstractFlyingObject {
    protected int maxHp;//最大血量
    protected int hp;//血量
    protected int direction;// 飞行方向（英雄机 -1，敌机 +1）
    protected int power;//子弹威力
    protected int shootNum;//一次射击子弹个数

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp, int direction,int power,int shootNum) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
        this.direction = direction;
        this.power = power;
        this.shootNum = shootNum;
    }

    public int getDirection() {
        return direction;
    }

    public abstract List<BaseBullet> shoot();

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getPower() {
        return power;
    }

    public int getShootNum(){
        return shootNum;
    }
}
