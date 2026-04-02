package edu.hitsz.basic;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.config.GameConfig;

/**
 * 可飞行对象的父类（平台无关版）
 *
 * 职责：
 * 1. 管理飞行对象位置、速度、碰撞、生命周期
 * 2. 持有图片资源引用（不依赖具体图像类型）
 *
 * 说明：
 * - 已移除 BufferedImage 依赖
 * - 后续 Android 可直接接 Bitmap
 */
public abstract class AbstractFlyingObject {

    /**
     * x 轴坐标（图片中心）
     */
    protected int locationX;

    /**
     * y 轴坐标（图片中心）
     */
    protected int locationY;

    /**
     * x 轴移动速度
     */
    protected int speedX;

    /**
     * y 轴移动速度
     */
    protected int speedY;

    /**
     * 图片资源对象（平台无关）
     * 例如：
     * - 桌面版可为 BufferedImage
     * - Android 可为 Bitmap
     */
    protected Object image = null;

    /**
     * 对象宽度
     */
    protected int width = -1;

    /**
     * 对象高度
     */
    protected int height = -1;

    /**
     * 有效（生存）标记
     */
    protected boolean isValid = true;

    public AbstractFlyingObject() {
    }

    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * 可飞行对象根据速度移动
     * 若飞行对象触碰到横向边界，横向速度反向
     */
    public void forward() {
        locationX += speedX;
        locationY += speedY;

        if (locationX <= 0 || locationX >= GameConfig.Screen.WIDTH) {
            speedX = -speedX;
        }
    }

    /**
     * 碰撞检测
     *
     * @param flyingObject 撞击对方
     * @return true: 我方被击中; false: 未被击中
     */
    public boolean crash(AbstractFlyingObject flyingObject) {
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;

        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth + this.getWidth()) / 2 > locationX
                && x - (fWidth + this.getWidth()) / 2 < locationX
                && y + (fHeight / fFactor + this.getHeight() / factor) / 2 > locationY
                && y - (fHeight / fFactor + this.getHeight() / factor) / 2 < locationY;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocation(double locationX, double locationY) {
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    public int getSpeedY() {
        return speedY;
    }

    /**
     * 获取图片资源对象
     */
    public Object getImage() {
        if (image == null) {
            image = ImageManager.get(this);
        }
        return image;
    }

    /**
     * 手动设置图片资源对象
     */
    public void setImage(Object image) {
        this.image = image;
    }

    /**
     * 获取对象宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 设置对象宽度
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 获取对象高度
     */
    public int getHeight() {
        return height;
    }

    /**
     * 设置对象高度
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 一次性设置尺寸
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean notValid() {
        return !this.isValid;
    }

    /**
     * 标记消失
     */
    public void vanish() {
        isValid = false;
    }
}