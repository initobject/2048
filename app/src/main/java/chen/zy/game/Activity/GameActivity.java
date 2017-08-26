package chen.zy.game.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import chen.zy.game.Listen.ScoreChangeListen;
import chen.zy.game.R;
import chen.zy.game.View.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    private TextView text_nowScore;

    private TextView text_highestScore;

    private ScoreChangeListen scoreChangeListen;

    private SharedPreferences.Editor gsEditor;

    // 历史最高分
    private int highestScore;

    // 用于实现“在点击一次返回键退出程序”的效果
    private boolean isExit = false;

    private boolean flag;

    private int temp;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int row = intent.getIntExtra("Row", 4);
        if (row == 4) {
            setContentView(R.layout.activity_four);
        } else if (row == 5) {
            setContentView(R.layout.activity_five);
        } else {
            setContentView(R.layout.activity_six);
        }
        init();
        //判断是否需要恢复游戏记录
        if (intent.getBooleanExtra("RecoverGame", false)) {
            gameView.recoverGame();
        }
    }

    // 初始化
    public void init() {
        gameView = (GameView) findViewById(R.id.gameView_five);
        text_nowScore = (TextView) findViewById(R.id.nowScore);
        text_highestScore = (TextView) findViewById(R.id.highestScore);
        TextView text_restart = (TextView) findViewById(R.id.restart);
        TextView text_saveGame = (TextView) findViewById(R.id.save_game);
        SharedPreferences gameSettings = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        gsEditor = gameSettings.edit();
        highestScore = gameSettings.getInt("HighestScore", 0);
        text_nowScore.setText("当前得分\n" + 0);
        text_highestScore.setText("最高得分\n" + highestScore);
        flag = true;
        int themeIndex = gameSettings.getInt("ThemeIndex", 1);
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        int resource = 0;
        switch (themeIndex) {
            case 1:
                resource = R.drawable.back1;
                break;
            case 2:
                resource = R.drawable.back2;
                break;
            case 3:
                resource = R.drawable.back3;
                break;
            case 4:
                resource = R.drawable.back4;
                break;
            case 5:
                resource = R.drawable.back5;
                break;
            case 6:
                resource = R.drawable.back6;
                break;
        }
        if (rootLayout != null && resource != 0) {
            rootLayout.setBackgroundResource(resource);
        }
        //重新开始游戏
        text_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage("确认重新开始游戏吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.restart();
                        text_nowScore.setText("当前得分\n" + 0);
                        if (temp != 0) {
                            scoreChangeListen.OnHighestScoreChange(temp);
                            highestScore = temp;
                            flag = true;
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        //保存游戏
        text_saveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage("确认保存游戏吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.saveGame();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        scoreChangeListen = new ScoreChangeListen() {
            @Override
            public void OnNowScoreChange(int Score) {
                text_nowScore.setText("当前得分\n" + Score);
                if (Score > highestScore) {
                    if (flag && highestScore != 0) {
                        Toast.makeText(GameActivity.this, "打破最高纪录啦，请继续保持", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                    temp = Score;
                    text_highestScore.setText("最高得分\n" + temp);
                }
            }

            @Override
            public void OnHighestScoreChange(int Score) {
                gsEditor.putInt("HighestScore", Score);
                gsEditor.apply();
            }
        };
        gameView.scoreChangeListen = scoreChangeListen;
    }

    // 重写返回键监听事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            if (gameView.isHalfway) {
                Toast.makeText(this, "再按一次结束游戏,建议保存游戏", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "再按一次结束游戏", Toast.LENGTH_SHORT).show();
            }
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (temp != 0) {
            scoreChangeListen.OnHighestScoreChange(temp);
        }
        gameView.soundPool.release();
    }

}
