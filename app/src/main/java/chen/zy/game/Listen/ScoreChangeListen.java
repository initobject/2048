package chen.zy.game.Listen;

/**
 * 回调函数
 * Created by ZY on 2016/7/18.
 */
public interface ScoreChangeListen {

    void OnNowScoreChange(int Score);

    void OnHighestScoreChange(int Score);

}
