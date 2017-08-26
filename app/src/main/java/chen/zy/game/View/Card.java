package chen.zy.game.View;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

    private TextView label;

    private int num = 0;

    //用于判断是否纯色块
    public boolean flag;

    public Card(Context context) {
        super(context);
        label = new TextView(context);
        label.setGravity(Gravity.CENTER);
        label.setTextSize(24);
        label.setBackgroundColor(Color.parseColor("#77E8E2D8"));
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 5, 0, 0);
        addView(label, lp);
    }

    public void setNum(int num) {
        this.num = num;
        if (num == 0) {
            label.setText("");
            label.setBackgroundColor(Color.parseColor("#77E8E2D8"));
        } else {
            if (!flag) {
                label.setText(String.valueOf(num));
            }
            changeCardColor();
        }
    }

    public int getNum() {
        return num;
    }

    public void changeCardColor() {
        switch (num) {
            case 2:
                label.setBackgroundColor(Color.parseColor("#5DB8E8"));
                break;
            case 4:
                label.setBackgroundColor(Color.parseColor("#A52812"));
                break;
            case 8:
                label.setBackgroundColor(Color.parseColor("#0E7171"));
                break;
            case 16:
                label.setBackgroundColor(Color.parseColor("#C0BB39"));
                break;
            case 32:
                label.setBackgroundColor(Color.parseColor("#623889"));
                break;
            case 64:
                label.setBackgroundColor(Color.parseColor("#5C7235"));
                break;
            case 128:
                label.setBackgroundColor(Color.parseColor("#826FA3"));
                break;
            case 256:
                label.setBackgroundColor(Color.parseColor("#355659"));
                break;
            case 512:
                label.setBackgroundColor(Color.parseColor("#BB719B"));
                break;
            case 1024:
                label.setBackgroundColor(Color.parseColor("#9B8B53"));
                break;
            case 2048:
                label.setBackgroundColor(Color.parseColor("#196A5D"));
                break;
            default:
                label.setBackgroundColor(Color.parseColor("#8A7760"));
        }
    }

    public boolean equals(Card c) {
        return this.getNum() == c.getNum();
    }

}