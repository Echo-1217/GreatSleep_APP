package com.example.greatsleep.Station;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.greatsleep.Clock.ClockAlarm;
import com.example.greatsleep.MainActivity;
import com.example.greatsleep.R;
import com.example.greatsleep.SettingActivity;
import com.example.greatsleep.TypeFaceProvider;

import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

public class StationAlarm extends AppCompatActivity {
    Vibrator v;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SettingActivity settingActivity=new SettingActivity();
    Timer timer;
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_alarm);
        typeface= TypeFaceProvider.getTypeFace(this,"introtype.ttf");
        preferences = getSharedPreferences("station", Context.MODE_PRIVATE);
        editor = preferences.edit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //突破鎖屏代碼
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock( "" );
        keyguardLock.disableKeyguard();
        //彈出對話框
        AlertDialog.Builder ab = new AlertDialog.Builder(StationAlarm.this);
        View view = getLayoutInflater().inflate(R.layout.alarm_dialog_design,null);//嵌入View
        Button d_confirm=view.findViewById(R.id.alarm_dialog_confirm);
        Button d_cancel=view.findViewById(R.id.alarm_dialog_cancel);
        d_confirm.setTypeface(typeface);
        d_cancel.setTypeface(typeface);
        TextView dialog_message=view.findViewById(R.id.alarm_dialog_message);
        dialog_message.setTypeface(typeface);
        dialog_message.setText("快要到站囉!");
        ab.setView(view);
        AlertDialog dialog=ab.create();
        d_cancel.setVisibility(View.INVISIBLE);
        //點選對話框外動作
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                v.cancel();
                editor.putBoolean("is_alarm",false);
                editor.putString("name",null);
                editor.apply();
                StationAlarm.this.finish();
            }
        });
        d_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                v.cancel();
                editor.putBoolean("is_alarm",false);
                editor.putString("name",null);
                editor.apply();
                dialog.dismiss();
                StationAlarm.this.finish();
            }
        });
        dialog.show();

        timer = new Timer();
        timer.schedule(new Tasks(), 0, 2000);
    }
    private class Tasks extends TimerTask {
        @Override
        public void run() {
            settingActivity.vibrate(2);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        v.cancel();
        editor.putBoolean("is_alarm",false);
        editor.putString("name",null);
        editor.apply();
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
        if(preferences.getBoolean("is_alarm",false)){
            v=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
            v.vibrate(new long[]{1000,1000, 1000,1000},0);
        }
        else{
            v.cancel();
            startActivity(new Intent(StationAlarm.this, MainActivity.class));
        }
    }

}