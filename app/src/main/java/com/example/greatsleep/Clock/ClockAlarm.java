package com.example.greatsleep.Clock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.TextView;

import com.example.greatsleep.Diaries.DiaryNew;
import com.example.greatsleep.MainActivity;
import com.example.greatsleep.R;
import com.example.greatsleep.Station.StationAlarm;
import com.example.greatsleep.TypeFaceProvider;

import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

public class ClockAlarm extends AppCompatActivity {
    SharedPreferences preferences;
    private MediaPlayer mediaPlayer;
    private Vibrator v;
    SharedPreferences.Editor editor;
    AudioManager am;
    Timer timer;
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        typeface= TypeFaceProvider.getTypeFace(this,"introtype.ttf");
        v = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        //取得鈴聲設定
        preferences = getSharedPreferences("clock", Context.MODE_PRIVATE);
        editor = preferences.edit();
        mediaPlayer = MediaPlayer.create(this, preferences.getInt("tune", R.raw.sound1));

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, preferences.getInt("volume", 3), 0);

        timer = new Timer();
        timer.schedule(new Tasks(), 5000, 5000);

        //突破鎖屏代碼
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
        keyguardLock.disableKeyguard();
        //彈出對話框
        AlertDialog.Builder ab = new AlertDialog.Builder(ClockAlarm.this);
        View view = getLayoutInflater().inflate(R.layout.alarm_dialog_design, null);//嵌入View
        Button d_confirm = view.findViewById(R.id.alarm_dialog_confirm);
        Button d_cancel = view.findViewById(R.id.alarm_dialog_cancel);
        TextView dialog_message = view.findViewById(R.id.alarm_dialog_message);
        dialog_message.setTypeface(typeface);
        dialog_message.setText("起床了!!  時間到囉~~~");
        ab.setView(view);
        AlertDialog dialog = ab.create();
        d_cancel.setVisibility(View.INVISIBLE);
        //點選對話框外動作
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                editor.putBoolean("is_alarm", false);
                editor.putString("name", "您選擇的時間：無");
                editor.apply();
                v.cancel();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
                dialog.dismiss();
                ClockAlarm.this.finish();
            }
        });
        d_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                editor.putBoolean("is_alarm", false);
                editor.putString("name", "您選擇的時間：無");
                editor.apply();
                v.cancel();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
                dialog.dismiss();
                ClockAlarm.this.finish();
            }
        });
        dialog.show();

    }
    private class Tasks extends TimerTask{
        @Override
        public void run() {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,FLAG_PLAY_SOUND);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        editor.putBoolean("is_alarm", false);
        editor.putString("name", "您選擇的時間：無");
        editor.apply();
        v.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //振動器
        Boolean isVibrate=preferences.getBoolean("vibration",true);

        if(preferences.getBoolean("is_alarm",false)){
            if(isVibrate){
                v.vibrate(new long[]{1000, 1000, 1000, 1000}, 0);
            }
            else{
                v.cancel();
            }
        }
        else{
            v.cancel();
            startActivity(new Intent(ClockAlarm.this, MainActivity.class));
        }

        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
}