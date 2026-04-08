package edu.hitsz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.hitsz.rank.RankingManager;
import edu.hitsz.rank.Score;

/**
 * 排行榜页面
 *
 * 功能：
 * 1. 展示 Top-10 历史得分
 * 2. 支持逐条删除得分记录
 * 3. 返回主菜单
 */
public class LeaderboardActivity extends Activity {

    private ListView lvScores;
    private ScoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_leaderboard);

        lvScores = findViewById(R.id.lvScores);
        refreshList();
    }

    /** 刷新列表（每次删除后调用） */
    private void refreshList() {
        List<Score> scores = RankingManager.getAllScores();
        adapter = new ScoreAdapter(this, scores);
        lvScores.setAdapter(adapter);
    }

    /** "返回主菜单"按钮回调（activity_leaderboard.xml 中 android:onClick 绑定） */
    public void onBackToMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // =========================================================================
    // 自定义 ArrayAdapter
    // =========================================================================

    private class ScoreAdapter extends ArrayAdapter<Score> {

        ScoreAdapter(Context context, List<Score> scores) {
            super(context, R.layout.item_score, scores);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_score, parent, false);
                holder = new ViewHolder();
                holder.tvRank   = convertView.findViewById(R.id.tvRank);
                holder.tvName   = convertView.findViewById(R.id.tvName);
                holder.tvScore  = convertView.findViewById(R.id.tvScore);
                holder.tvTime   = convertView.findViewById(R.id.tvTime);
                holder.btnDelete = convertView.findViewById(R.id.btnDelete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Score score = getItem(position);
            if (score == null) return convertView;

            holder.tvRank.setText(String.valueOf(position + 1));
            holder.tvName.setText(score.getPlayerName());
            holder.tvScore.setText(String.valueOf(score.getScore()));
            holder.tvTime.setText(score.getTime());

            // 前三名金/银/铜色
            int rankColor;
            switch (position) {
                case 0:  rankColor = 0xFFFFD700; break; // 金
                case 1:  rankColor = 0xFFC0C0C0; break; // 银
                case 2:  rankColor = 0xFFCD7F32; break; // 铜
                default: rankColor = 0xFFAAAAAA; break;
            }
            holder.tvRank.setTextColor(rankColor);

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(LeaderboardActivity.this)
                        .setTitle("确认删除")
                        .setMessage("删除 " + score.getPlayerName() + " 的记录（" + score.getScore() + " 分）？")
                        .setPositiveButton("删除", (dialog, which) -> {
                            RankingManager.deleteScore(score);
                            refreshList();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            });

            return convertView;
        }

        private class ViewHolder {
            TextView tvRank, tvName, tvScore, tvTime;
            Button btnDelete;
        }
    }
}
