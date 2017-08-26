package chen.zy.game.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import chen.zy.game.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences GameSettings;

    private SharedPreferences.Editor gsEditor;

    private TextView text_soundSwitch;

    private TextView text_solidColorSwitch;

    private TextView text_themeText;

    private ImageView soundSwitchImage;

    private ImageView solidColorSwitchImage;

    private LinearLayout settingsLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    public void init() {
        settingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        text_soundSwitch = (TextView) findViewById(R.id.soundSwitch);
        text_solidColorSwitch = (TextView) findViewById(R.id.solidColorSwitch);
        text_themeText = (TextView) findViewById(R.id.themeSwitch);
        soundSwitchImage = (ImageView) findViewById(R.id.soundSwitchImage);
        solidColorSwitchImage = (ImageView) findViewById(R.id.solidColorSwitchImage);
        GameSettings = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        gsEditor = GameSettings.edit();
        boolean flag;
        flag = GameSettings.getBoolean("SoundSwitch", false);
        if (flag) {
            soundSwitchImage.setImageResource(R.drawable.open);
            text_soundSwitch.setText("音效：开");
        }
        flag = GameSettings.getBoolean("SolidColorSwitch", false);
        if (flag) {
            solidColorSwitchImage.setImageResource(R.drawable.open);
            text_solidColorSwitch.setText("纯色块：开");
        }
        int index = GameSettings.getInt("ThemeIndex", 1);
        switch (index) {
            case 1:
                settingsLayout.setBackgroundResource(R.drawable.back1);
                text_themeText.setText("主题一");
                break;
            case 2:
                settingsLayout.setBackgroundResource(R.drawable.back2);
                text_themeText.setText("主题二");
                break;
            case 3:
                settingsLayout.setBackgroundResource(R.drawable.back3);
                text_themeText.setText("主题三");
                break;
            case 4:
                settingsLayout.setBackgroundResource(R.drawable.back4);
                text_themeText.setText("主题四");
                break;
            case 5:
                settingsLayout.setBackgroundResource(R.drawable.back5);
                text_themeText.setText("主题五");
                break;
            case 6:
                settingsLayout.setBackgroundResource(R.drawable.back6);
                text_themeText.setText("主题六");
                break;
        }
    }

    public void soundSwitch(View view) {
        boolean flag = GameSettings.getBoolean("SoundSwitch", false);
        if (flag) {
            soundSwitchImage.setImageResource(R.drawable.close);
            text_soundSwitch.setText("音效：关");
        } else {
            soundSwitchImage.setImageResource(R.drawable.open);
            text_soundSwitch.setText("音效：开");
        }
        gsEditor.putBoolean("SoundSwitch", !flag);
        gsEditor.apply();
    }

    public void solidColorSwitch(View view) {
        boolean flag = GameSettings.getBoolean("SolidColorSwitch", false);
        if (flag) {
            solidColorSwitchImage.setImageResource(R.drawable.close);
            text_solidColorSwitch.setText("纯色块：关");
        } else {
            solidColorSwitchImage.setImageResource(R.drawable.open);
            text_solidColorSwitch.setText("纯色块：开");
        }
        gsEditor.putBoolean("SolidColorSwitch", !flag);
        gsEditor.apply();
    }

    public void themeSwitch(View view) {
        int index = GameSettings.getInt("ThemeIndex", 1);
        if (index == 1) {
            settingsLayout.setBackgroundResource(R.drawable.back2);
            text_themeText.setText("主题二");
        } else if (index == 2) {
            settingsLayout.setBackgroundResource(R.drawable.back3);
            text_themeText.setText("主题三");
        } else if (index == 3) {
            settingsLayout.setBackgroundResource(R.drawable.back4);
            text_themeText.setText("主题四");
        } else if (index == 4) {
            settingsLayout.setBackgroundResource(R.drawable.back5);
            text_themeText.setText("主题五");
        } else if (index == 5) {
            settingsLayout.setBackgroundResource(R.drawable.back6);
            text_themeText.setText("主题六");
        } else if (index == 6) {
            settingsLayout.setBackgroundResource(R.drawable.back1);
            text_themeText.setText("主题一");
        }
        if (index == 6) {
            gsEditor.putInt("ThemeIndex", 1);
        } else {
            gsEditor.putInt("ThemeIndex", index + 1);
        }
        gsEditor.apply();
    }

}
