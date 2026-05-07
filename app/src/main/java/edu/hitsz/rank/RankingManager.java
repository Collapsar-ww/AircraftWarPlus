package edu.hitsz.rank;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 排行榜管理器（Android 适配版）
 *
 * 使用方式：
 *   1. 在 MainActivity / Application 中调用 RankingManager.init(context) 完成初始化
 *   2. 之后直接调用静态方法操作排行榜
 */
public class RankingManager {

    private static final int MAX_RANK = 10;

    private static ScoreDao dao;

    /** 初始化（需在使用前调用一次，传入 ApplicationContext） */
    public static void init(Context context) {
        if (dao == null) {
            dao = new ScoreDaoSQLite(context.getApplicationContext());
        }
    }

    /** 添加得分记录，自动维持每个难度 Top-10 */
    public static void addScore(String playerName, int score, String difficulty) {
        if (dao == null) return;
        String time = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(new Date());
        dao.insert(new Score(-1, playerName, score, time, difficulty));

        // 每个难度各保留前 10 名
        List<Score> byDiff = dao.findByDifficulty(difficulty);
        for (int i = MAX_RANK; i < byDiff.size(); i++) {
            dao.delete(byDiff.get(i));
        }
    }

    /** 获取指定难度的得分记录（按分数降序） */
    public static List<Score> getScoresByDifficulty(String difficulty) {
        if (dao == null) return new ArrayList<>();
        return dao.findByDifficulty(difficulty);
    }

    /** 删除指定得分记录 */
    public static void deleteScore(Score score) {
        if (dao == null) return;
        dao.delete(score);
    }
}
