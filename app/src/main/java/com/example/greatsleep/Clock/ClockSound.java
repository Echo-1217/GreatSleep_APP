package com.example.greatsleep.Clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.greatsleep.R;
import com.example.greatsleep.TypeFaceProvider;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

public class ClockSound extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton1,mRadioButton2,mRadioButton3,mRadioButton4,mRadioButton5,mRadioButton6,
            mRadioButton7,mRadioButton8,mRadioButton9,mRadioButton10;
    Typeface typeface;
    AlarmSet sound = new AlarmSet(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_sound);
        typeface= TypeFaceProvider.getTypeFace(this,"introtype.ttf");
        SharedPreferences preferences = getSharedPreferences("clock", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        mRadioButton1 = (RadioButton) findViewById(R.id.mRadioButton1);
        mRadioButton2 = (RadioButton) findViewById(R.id.mRadioButton2);
        mRadioButton3 = (RadioButton) findViewById(R.id.mRadioButton3);
        mRadioButton4 = (RadioButton) findViewById(R.id.mRadioButton4);
        mRadioButton5 = (RadioButton) findViewById(R.id.mRadioButton5);
        mRadioButton6 = (RadioButton) findViewById(R.id.mRadioButton6);
        mRadioButton7 = (RadioButton) findViewById(R.id.mRadioButton7);
        mRadioButton8 = (RadioButton) findViewById(R.id.mRadioButton8);
        mRadioButton9 = (RadioButton) findViewById(R.id.mRadioButton9);
        mRadioButton10 = (RadioButton) findViewById(R.id.mRadioButton10);

        mRadioButton1.setTypeface(typeface);
        mRadioButton2.setTypeface(typeface);
        mRadioButton3.setTypeface(typeface);
        mRadioButton4.setTypeface(typeface);
        mRadioButton5.setTypeface(typeface);
        mRadioButton6.setTypeface(typeface);
        mRadioButton7.setTypeface(typeface);
        mRadioButton8.setTypeface(typeface);
        mRadioButton9.setTypeface(typeface);
        mRadioButton10.setTypeface(typeface);

        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);

        mRadioGroup.check(preferences.getInt("button",R.id.mRadioButton1));
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
                switch (checkedId){
                    case R.id.mRadioButton1:
                        editor.putInt("tune", R.raw.sound1);
                        editor.putInt("button",R.id.mRadioButton1);
                        editor.putString("text","鈴聲一");
                        editor.apply();
                        break;
                    case R.id.mRadioButton2:
                        editor.putInt("tune", R.raw.sound2);
                        editor.putInt("button",R.id.mRadioButton2);
                        editor.putString("text","鈴聲二");
                        editor.apply();
                        break;
                    case R.id.mRadioButton3:
                        editor.putInt("tune", R.raw.sound3);
                        editor.putInt("button",R.id.mRadioButton3);
                        editor.putString("text","鈴聲三");
                        editor.apply();
                        break;
                    case R.id.mRadioButton4:
                        editor.putInt("tune", R.raw.sound4);
                        editor.putInt("button",R.id.mRadioButton4);
                        editor.putString("text","鈴聲四");
                        editor.apply();
                        break;
                    case R.id.mRadioButton5:
                        editor.putInt("tune", R.raw.sound5);
                        editor.putInt("button",R.id.mRadioButton5);
                        editor.putString("text","鈴聲五");
                        editor.apply();
                        break;
                    case R.id.mRadioButton6:
                        editor.putInt("tune", R.raw.sound6);
                        editor.putInt("button",R.id.mRadioButton6);
                        editor.putString("text","鈴聲六");
                        editor.apply();
                        break;
                    case R.id.mRadioButton7:
                        editor.putInt("tune", R.raw.sound7);
                        editor.putInt("button",R.id.mRadioButton7);
                        editor.putString("text","鈴聲七");
                        editor.apply();
                        break;
                    case R.id.mRadioButton8:
                        editor.putInt("tune", R.raw.sound8);
                        editor.putInt("button",R.id.mRadioButton8);
                        editor.putString("text","鈴聲八");
                        editor.apply();
                        break;
                    case R.id.mRadioButton9:
                        editor.putInt("tune", R.raw.sound9);
                        editor.putInt("button",R.id.mRadioButton9);
                        editor.putString("text","鈴聲九");
                        editor.apply();
                        break;
                    case R.id.mRadioButton10:
                        editor.putInt("tune", R.raw.sound10);
                        editor.putInt("button",R.id.mRadioButton10);
                        editor.putString("text","鈴聲十");
                        editor.apply();
                        break;
                }
                sound.stopTune();
                sound.chooseTrack(preferences.getInt("tune",0));
                sound.playTune();
            }
        });
    }
    public void setdone(View view) {
        ClockSound.this.finish();
    }

    @Override
    protected void onPause() {
        sound.stopTune();
        super.onPause();
    }
}