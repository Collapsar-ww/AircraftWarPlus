package edu.hitsz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Random;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.Game;
import edu.hitsz.application.ImageManager;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.AbstractProp;

/**
 * 游戏画面 View
 *
 * 职责：
 * 1. 每帧调用 game.update()
 * 2. 绘制背景、飞机、子弹、道具
 * 3. 处理手指拖动控制英雄机
 * 4. 显示基础 UI（分数、血量、结束提示）
 */
public class GameView extends View {

    private final Game game;
    private final Paint paint = new Paint();

    // 屏幕震动随机偏移
    private final Random random = new Random();

    public GameView(Context context, Game game) {
        super(context);
        this.game = game;

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. 先更新游戏逻辑
        game.update();

        // 2. 处理屏幕震动偏移
        int offsetX = 0;
        int offsetY = 0;
        if (game.getShakeRemaining() > 0) {
            int intensity = game.getShakeIntensity();
            offsetX = random.nextInt(intensity * 2 + 1) - intensity;
            offsetY = random.nextInt(intensity * 2 + 1) - intensity;
            canvas.save();
            canvas.translate(offsetX, offsetY);
        }

        // 3. 画背景
        drawBackground(canvas);

        // 4. 画所有游戏对象
        drawAllObjects(canvas);

        // 5. 画 UI
        drawUI(canvas);

        // 6. 如果有震动，恢复画布
        if (game.getShakeRemaining() > 0) {
            canvas.restore();
        }

        // 7. 游戏未结束则继续刷新
        if (game.getGameState() != Game.GameState.OVER) {
            postInvalidateDelayed(16); // 约 60 FPS
        }
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Canvas canvas) {
        Bitmap bg = ImageManager.getCurrentBackground();
        if (bg != null) {
            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(bg, null, dst, null);
        } else {
            canvas.drawColor(Color.BLACK);
        }
    }

    /**
     * 绘制所有对象
     */
    private void drawAllObjects(Canvas canvas) {
        // 英雄机
        drawObject(canvas, game.getHeroAircraft());

        // 敌机
        List<AbstractAircraft> enemies = game.getEnemyAircrafts();
        for (AbstractAircraft enemy : enemies) {
            drawObject(canvas, enemy);
        }

        // 英雄子弹
        List<BaseBullet> heroBullets = game.getHeroBullets();
        for (BaseBullet bullet : heroBullets) {
            drawObject(canvas, bullet);
        }

        // 敌机子弹
        List<BaseBullet> enemyBullets = game.getEnemyBullets();
        for (BaseBullet bullet : enemyBullets) {
            drawObject(canvas, bullet);
        }

        // 道具
        List<AbstractProp> props = game.getProps();
        for (AbstractProp prop : props) {
            drawObject(canvas, prop);
        }
    }

    /**
     * 绘制单个飞行对象
     */
    private void drawObject(Canvas canvas, AbstractFlyingObject obj) {
        if (obj == null || obj.notValid()) {
            return;
        }

        Bitmap bitmap = ImageManager.get(obj);
        if (bitmap == null) {
            return;
        }

        // ★ 把图片真实尺寸同步回逻辑层
        obj.setSize(bitmap.getWidth(), bitmap.getHeight());

        int x = obj.getLocationX() - bitmap.getWidth() / 2;
        int y = obj.getLocationY() - bitmap.getHeight() / 2;

        canvas.drawBitmap(bitmap, x, y, null);
    }

    /**
     * 绘制基础 UI
     */
    private void drawUI(Canvas canvas) {
        HeroAircraft hero = game.getHeroAircraft();

        // 分数
        canvas.drawText("Score: " + game.getScore(), 30, 60, paint);

        // 血量（如果没有 getHp() 这里会报错，后面告诉你怎么改）
        canvas.drawText("HP: " + hero.getHp(), 30, 120, paint);

        // 时间（秒）
        canvas.drawText("Time: " + (game.getTime() / 1000), 30, 180, paint);

        // 暂停提示
        if (game.getGameState() == Game.GameState.PAUSED) {
            paint.setTextSize(90);
            canvas.drawText("PAUSED", getWidth() / 2f - 180, getHeight() / 2f, paint);
            paint.setTextSize(50);
        }

        // 结束提示
        if (game.getGameState() == Game.GameState.OVER) {
            paint.setTextSize(90);
            canvas.drawText("GAME OVER", getWidth() / 2f - 250, getHeight() / 2f, paint);
            paint.setTextSize(50);
        }
    }

    /**
     * 手指触摸控制英雄机移动
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game.getGameState() != Game.GameState.RUNNING) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                game.setHeroLocation(event.getX(), event.getY());
                return true;
        }

        return super.onTouchEvent(event);
    }
}