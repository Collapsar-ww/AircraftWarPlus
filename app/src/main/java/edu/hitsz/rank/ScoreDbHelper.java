package edu.hitsz.rank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite 数据库管理类
 *
 * 负责数据库的创建与升级。
 * 数据库文件存储在 Android 沙箱：data/data/[包名]/databases/scores.db
 */
public class ScoreDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "scores.db";
    private static final int    DB_VERSION = 2;

    public static final String TABLE_SCORES  = "scores";
    public static final String COL_ID        = "_id";
    public static final String COL_NAME      = "player_name";
    public static final String COL_SCORE     = "score";
    public static final String COL_TIME      = "time";
    public static final String COL_DIFFICULTY = "difficulty";

    public ScoreDbHelper(Context context) {
        // context.getApplicationContext() 确保不持有 Activity 引用
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SCORES + " ("
                + COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME       + " TEXT NOT NULL, "
                + COL_SCORE      + " INTEGER NOT NULL, "
                + COL_TIME       + " TEXT NOT NULL, "
                + COL_DIFFICULTY + " TEXT NOT NULL DEFAULT '简单')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }
}
