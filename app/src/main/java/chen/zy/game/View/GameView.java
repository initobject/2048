package chen.zy.game.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import chen.zy.game.Listen.ScoreChangeListen;
import chen.zy.game.R;

public class GameView extends GridLayout {

    // 存储所有方块
    private Card[][] Cards;

    // 当前游戏的行数与列数
    private int Row;

    // 游戏记录
    private SharedPreferences gameRecord;

    private SharedPreferences.Editor grEditor;

    public ScoreChangeListen scoreChangeListen = null;

    private Context context;

    //当前得分
    private int Score;

    public SoundPool soundPool;

    private int soundID;

    private boolean soundSwitch;

    private class Point {

        int x;

        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewSet);
        Row = mTypedArray.getInt(R.styleable.ViewSet_Row, 5);
        mTypedArray.recycle();
        super.setColumnCount(Row);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewSet);
        Row = mTypedArray.getInt(R.styleable.ViewSet_Row, 5);
        mTypedArray.recycle();
        super.setColumnCount(Row);
        init();
    }

    public GameView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    // 初始化
    private void init() {
        gameRecord = context.getSharedPreferences("GameRecord", Context.MODE_PRIVATE);
        SharedPreferences GameSettings = context.getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        boolean flag = GameSettings.getBoolean("SolidColorSwitch", false);
        soundSwitch = GameSettings.getBoolean("SoundSwitch", false);
        //SoundPool的构建方法在5.0系统之后发生了变化
        if (Build.VERSION.SDK_INT < 21) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        } else {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        }
        soundID = soundPool.load(context, R.raw.sound, 1);
        grEditor = gameRecord.edit();
        Cards = new Card[Row][Row];
        for (int y = 0; y < Row; y++) {
            for (int x = 0; x < Row; x++) {
                Cards[x][y] = new Card(context);
                Cards[x][y].flag = flag;
            }
        }
        // 添加两个初始方块
        randomCard();
        randomCard();
    }

    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        // 计算方块的边长
        int cardWidth = (Math.min(w, h) - 5) / Row;
        // 添加方块
        addCard(cardWidth);
    }

    // 计算分数
    private void countScore(int num) {
        Score = Score + num;
        if (scoreChangeListen != null) {
            scoreChangeListen.OnNowScoreChange(Score);
            if (soundSwitch) {
                soundPool.play(soundID, 1, 1, 0, 0, 1);
            }
        }
    }

    // 添加方块
    private void addCard(int cardWidth) {
        for (int y = 0; y < Row; y++) {
            for (int x = 0; x < Row; x++) {
                addView(Cards[x][y], cardWidth, cardWidth);
            }
        }
    }

    // 生成伪随机方块
    private void randomCard() {
        List<Point> points = new ArrayList<>();
        for (int x = 0; x < Row; x++) {
            for (int y = 0; y < Row; y++) {
                // 如果还有空白方块
                if (Cards[x][y].getNum() == 0) {
                    points.add(new Point(x, y));
                }
            }
        }
        if (points.size() == 0) {
            return;
        }
        int index = points.size() / 2;
        Cards[points.get(index).x][points.get(index).y].setNum(2);
    }

    // 左移
    private void moveLeftCard() {
        allMoveLeft();
        for (int y = 0; y < Row; y++) {
            for (int x = 0; x < Row - 1; x++) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x + 1][y])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x + 1][y].setNum(0);
                        countScore(num);
                        allMoveLeft();
                    }
                }
            }
        }
        randomCard();
    }

    // 右移
    private void moveRightCard() {
        allMoveRight();
        for (int y = 0; y < Row; y++) {
            for (int x = Row - 1; x > 0; x--) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x - 1][y])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x - 1][y].setNum(0);
                        countScore(num);
                        allMoveRight();
                    }
                }
            }
        }
        randomCard();
    }

    // 上移
    private void moveUpCard() {
        allMoveUp();
        for (int x = 0; x < Row; x++) {
            for (int y = 0; y < Row - 1; y++) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x][y + 1])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x][y + 1].setNum(0);
                        countScore(num);
                        allMoveUp();
                    }
                }
            }
        }
        randomCard();
    }

    // 下移
    private void moveDownCard() {
        allMoveDown();
        for (int x = 0; x < Row; x++) {
            for (int y = Row - 1; y > 0; y--) {
                if (Cards[x][y].getNum() != 0) {
                    if (Cards[x][y].equals(Cards[x][y - 1])) {
                        int num = Cards[x][y].getNum();
                        Cards[x][y].setNum(2 * num);
                        Cards[x][y - 1].setNum(0);
                        countScore(num);
                        allMoveDown();
                    }
                }
            }
        }
        randomCard();
    }

    // 全部左移
    private void allMoveLeft() {
        for (int y = 0; y < Row; y++) {
            int i = 0;
            for (int x = 0; x < Row; x++) {
                if (Cards[x][y].getNum() != 0) {
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[i++][y].setNum(num);
                }
            }
        }
    }

    // 全部右移
    private void allMoveRight() {
        for (int y = 0; y < Row; y++) {
            int i = Row - 1;
            for (int x = Row - 1; x > -1; x--) {
                if (Cards[x][y].getNum() != 0) {
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[i--][y].setNum(num);
                }
            }
        }
    }

    // 全部上移
    private void allMoveUp() {
        for (int x = 0; x < Row; x++) {
            int i = 0;
            for (int y = 0; y < Row; y++) {
                if (Cards[x][y].getNum() != 0) {
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[x][i++].setNum(num);
                }
            }
        }
    }

    // 全部下移
    private void allMoveDown() {
        for (int x = 0; x < Row; x++) {
            int i = Row - 1;
            for (int y = Row - 1; y > -1; y--) {
                if (Cards[x][y].getNum() != 0) {
                    int num = Cards[x][y].getNum();
                    Cards[x][y].setNum(0);
                    Cards[x][i--].setNum(num);
                }
            }
        }
    }

    // 触屏事件监听
    float X;
    float Y;
    float OffsetX;
    float OffsetY;
    int HintCount = 0;
    public boolean isHalfway = true;

    public boolean onTouchEvent(MotionEvent event) {
        // 为了避免当游戏结束时消息多次提示
        if (HintCount == 1) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                X = event.getX();
                Y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                OffsetX = event.getX() - X;
                OffsetY = event.getY() - Y;
                if (Math.abs(OffsetX) > (Math.abs(OffsetY))) {
                    if (OffsetX < -5) {
                        moveLeftCard();
                    } else if (OffsetX > 5) {
                        moveRightCard();
                    }
                } else {
                    if (OffsetY < -5) {
                        moveUpCard();
                    } else if (OffsetY > 5) {
                        moveDownCard();
                    }
                }
                HintMessage();
                break;
        }
        return true;
    }

    // 判断游戏是否结束
    private boolean isOver() {
        for (int y = 0; y < Row; y++) {
            for (int x = 0; x < Row; x++) {
                if ((Cards[x][y].getNum() == 0) || (x - 1 >= 0 && Cards[x - 1][y].equals(Cards[x][y]))
                        || (x + 1 <= Row - 1 && Cards[x + 1][y].equals(Cards[x][y]))
                        || (y - 1 >= 0 && Cards[x][y - 1].equals(Cards[x][y]))
                        || (y + 1 <= Row - 1 && Cards[x][y + 1].equals(Cards[x][y]))) {
                    return false;
                }
            }
        }
        return true;
    }

    // 当游戏结束时提示信息
    private void HintMessage() {
        if (isOver()) {
            Toast.makeText(getContext(), "游戏结束啦", Toast.LENGTH_SHORT).show();
            HintCount = 1;
        }
    }

    //重新开始
    public void restart() {
        for (int y = 0; y < Row; y++) {
            for (int x = 0; x < Row; x++) {
                Cards[x][y].setNum(0);
            }
        }
        Score = 0;
        HintCount = 0;
        // 添加两个初始方块
        randomCard();
        randomCard();
    }

    //保存游戏
    public void saveGame() {
        grEditor.clear();
        grEditor.putInt("Row", Row);
        grEditor.putInt("Score", Score);
        int k = 0;
        for (int i = 0; i < Row; i++) {
            for (int j = 0; j < Row; j++) {
                k++;
                String str = k + "";
                grEditor.putInt(str, Cards[i][j].getNum());
            }
        }
        if (grEditor.commit()) {
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "保存失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    // 恢复游戏
    public void recoverGame() {
        int k = 0;
        for (int i = 0; i < Row; i++) {
            for (int j = 0; j < Row; j++) {
                int num = gameRecord.getInt(String.valueOf(++k), 0);
                Cards[i][j].setNum(num);
            }
        }
        Score = gameRecord.getInt("Score", 0);
        scoreChangeListen.OnNowScoreChange(Score);
    }

}
