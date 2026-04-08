package edu.hitsz.rank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 得分 DAO 的 SQLite 实现（Android 平台）
 *
 * 复用 Windows 版 ScoreDao 接口，适配 Android 沙箱存储：
 *   - 数据库路径由 context.getFilesDir() 所在沙箱目录管理（SQLiteOpenHelper 自动处理）
 *   - saveToFile() / loadFromFile() 为空操作（SQLite 自动持久化）
 */
public class ScoreDaoSQLite implements ScoreDao {

    private final ScoreDbHelper dbHelper;

    public ScoreDaoSQLite(Context context) {
        dbHelper = new ScoreDbHelper(context);
    }

    @Override
    public void insert(Score score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScoreDbHelper.COL_NAME,  score.getPlayerName());
        values.put(ScoreDbHelper.COL_SCORE, score.getScore());
        values.put(ScoreDbHelper.COL_TIME,  score.getTime());
        db.insert(ScoreDbHelper.TABLE_SCORES, null, values);
    }

    @Override
    public List<Score> findAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Score> result = new ArrayList<>();

        Cursor cursor = db.query(
                ScoreDbHelper.TABLE_SCORES,
                null, null, null, null, null,
                ScoreDbHelper.COL_SCORE + " DESC");

        while (cursor.moveToNext()) {
            int    id    = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_ID));
            String name  = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_NAME));
            int    score = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_SCORE));
            String time  = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbHelper.COL_TIME));
            result.add(new Score(id, name, score, time));
        }
        cursor.close();
        return result;
    }

    @Override
    public void delete(Score score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ScoreDbHelper.TABLE_SCORES,
                ScoreDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(score.getId())});
    }

    /** SQLite 自动持久化，无需手动保存文件 */
    @Override
    public void saveToFile() {}

    /** SQLite 自动持久化，无需手动加载文件 */
    @Override
    public void loadFromFile() {}
}
