package com.example.greatsleep.Clock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingMainActivity;
import com.example.greatsleep.TypeFaceProvider;
import com.unity3d.player.UnityPlayerActivity;

import java.util.Calendar;

public class ClockFragment extends Fragment {
    int hour=0;
    int minutes=0;
    Switch vibrate;
    Button sound;
    Button game;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    PendingIntent pendingIntentSet;
    TimePicker timePicker;
    AlarmManager alarm;
    Intent intent;
    View mainView;
    Button confirm;
    int currentDate;
    int currentHour;
    int currentMinute;
    Calendar calendar;
    Toolbar toolbar;
    TextView information;
    SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    TextView soundText,gameText;
    RelativeLayout soundlayout,gamelayout;
    Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface= TypeFaceProvider.getTypeFace(getActivity(),"introtype.ttf");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView=inflater.inflate(R.layout.fragment_clock, container, false);

        ((AppCompatActivity) getActivity ()). setSupportActionBar ( toolbar );
        setHasOptionsMenu(true);
        vibrate=(Switch)mainView.findViewById(R.id.switch1);
        sound=(Button)mainView.findViewById(R.id.soundSet);
        game=(Button)mainView.findViewById(R.id.gameset);
        soundText=mainView.findViewById(R.id.soundtext);
        gameText=mainView.findViewById(R.id.gametext);

        soundText.setTypeface(typeface);
        gameText.setTypeface(typeface);

        soundlayout=mainView.findViewById(R.id.soundlayout);
        gamelayout=mainView.findViewById(R.id.gamelayout);
        TextView volumetext=mainView.findViewById(R.id.volumetext);
        vibrate.setTypeface(typeface);
        volumetext.setTypeface(typeface);

        timePicker=(TimePicker)mainView.findViewById(R.id.timerpick);
        toolbar=mainView.findViewById(R.id.toolbar_clock);
        toolbar.inflateMenu(R.menu.menu_example);
        information=mainView.findViewById(R.id.clock_information);
        information.setTypeface(typeface);

        preferences =getActivity().getSharedPreferences("clock", Context.MODE_PRIVATE);
        editor = preferences.edit();
        alarm=(AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        AudioManager am=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        seekBar=mainView.findViewById(R.id.volume_setting);
        seekBar.setMax(15);
        seekBar.setProgress(preferences.getInt("volume",3));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<3){
                    seekBar.setProgress(3);
                    progress=3;
                }
                am.setStreamVolume(AudioManager.STREAM_MUSIC,preferences.getInt("volume",3),0);
                editor.putInt("volume",progress);
                editor.apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer=MediaPlayer.create(getActivity(),preferences.getInt("tune",1800000));
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }
        });

        confirm=mainView.findViewById(R.id.clock_confirm);
        confirm.setTypeface(typeface);
        vibrate.setChecked(preferences.getBoolean("vibration",true));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_setting:
                        Intent intent=new Intent(getActivity(), SettingMainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_cancel:
                        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                        View view = getLayoutInflater().inflate(R.layout.alarm_dialog_design,null);//嵌入View
                        Button d_confirm=view.findViewById(R.id.alarm_dialog_confirm);
                        Button d_cancel=view.findViewById(R.id.alarm_dialog_cancel);
                        TextView dialog_message=view.findViewById(R.id.alarm_dialog_message);
                        ab.setView(view);
                        AlertDialog dialog=ab.create();
                        dialog_message.setText("確定取消鬧鐘嗎?");
                        d_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putBoolean("is_alarm",false);
                                editor.putString("name",null);
                                information.setText(null);
                                editor.apply();
                                toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
                                alarm.cancel(pendingIntentSet);
                                dialog.dismiss();
                            }
                        });
                        d_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                }
                return false;
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour=hourOfDay;
                minutes=minute;
            }
        });
        //前往更改鬧鐘鈴聲頁面
        sound.setOnClickListener(v -> startActivity(new Intent(getActivity(),ClockSound.class)));
        soundText.setOnClickListener(v -> startActivity(new Intent(getActivity(),ClockSound.class)));
        soundlayout.setOnClickListener(v -> startActivity(new Intent(getActivity(),ClockSound.class)));
        game.setOnClickListener(v -> startActivity(new Intent(getActivity(),GameSet.class)));
        gameText.setOnClickListener(v -> startActivity(new Intent(getActivity(),GameSet.class)));
        gamelayout.setOnClickListener(v->startActivity(new Intent(getActivity(),GameSet.class)));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferences.getBoolean("is_alarm",false)){
                    Toast.makeText(getActivity(),"鬧鐘尚未取消，請先取消再做設定",Toast.LENGTH_SHORT).show();
                }
                else{
                    //設定響鈴時可以在主頁進行
                    intent.addCategory (Intent.CATEGORY_DEFAULT );
                    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK );
                    //時間設定
                    calendar =Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        hour=timePicker.getHour();
                        minutes=timePicker.getMinute();
                    }
                    else{
                        hour=timePicker.getCurrentHour();
                        minutes=timePicker.getCurrentMinute();
                    }

                    if (calendar.get(Calendar.HOUR_OF_DAY)>hour){//如果選擇的時間小於獲取的系統時間日期
                        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1);
                    }
                    else if (calendar.get(Calendar.HOUR_OF_DAY)==hour){
                        if (calendar.get(Calendar.MINUTE)>=minutes) {
                            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1);
                        }
                    }
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE,minutes);
                    calendar.set(Calendar.SECOND,0);

                    currentDate=calendar.get(Calendar.DATE);
                    currentHour=calendar.get(Calendar.HOUR_OF_DAY);
                    currentMinute=calendar.get(Calendar.MINUTE);

                    long triggerAtMillis= calendar.getTimeInMillis();
                    information.setText("您選擇的時間:"+currentDate+"日"+currentHour+"時"+currentMinute+"分");
                    alarm.set(AlarmManager.RTC_WAKEUP,triggerAtMillis,pendingIntentSet);

                    toolbar.getMenu().findItem(R.id.action_cancel).setVisible(true);
                    editor.putBoolean("is_alarm",true);
                    editor.putString("name",currentDate+"日"+currentHour+"時"+currentMinute+"分");
                    editor.putBoolean("activityforalarm",true);
                    editor.apply();
                    Toast.makeText(getActivity(),"您選擇的時間："+currentDate+"日"+currentHour+"時"+currentMinute+"分",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return mainView;
    }
    @Override
    public void onResume() {
        super.onResume();
        String gameActivity=preferences.getString("gameactivity","UnityPlayerActivity.class");

        switch (gameActivity){
            case "UnityPlayerActivity.class":
                intent = new Intent(getActivity(), UnityPlayerActivity.class);
                break;
            case "ClockGame2.class":
                intent = new Intent(getActivity(), ClockGame2.class);
                break;
            case "ClockGame3.class":
                intent = new Intent(getActivity(), ClockGame3.class);
                break;
            case "ClockAlarm.class":
                intent = new Intent(getActivity(), ClockAlarm.class);
                break;
        }
        pendingIntentSet=PendingIntent.getActivity(getActivity().getApplicationContext(),1,intent,0);

        Calendar calendar1=Calendar.getInstance();
        if(preferences.getBoolean("is_alarm",false)){
            if(calendar1.get(Calendar.MINUTE)>=currentMinute){
                if(calendar1.get(Calendar.HOUR_OF_DAY)>=currentHour){
                    if(calendar1.get(Calendar.DATE)>=currentDate){
                        editor.putBoolean("is_alarm",false);
                        editor.putString("name","您選擇的時間：無");
                        editor.apply();
                    }
                }
            }
        }

        information.setText(preferences.getString("name","您選擇的時間：無"));
        if(preferences.getBoolean("is_alarm",false)){
            toolbar.getMenu().findItem(R.id.action_cancel).setVisible(true);
        }
        else {
            toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
        }
        vibrate.setChecked(preferences.getBoolean("vibration",true));
        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //是否震動
                editor.putBoolean("vibration",isChecked);
                editor.apply();
            }
        });
        //更改按鈕文字
        soundText.setText("鬧鐘鈴聲\n  "+preferences.getString("text","鈴聲一"));
        gameText.setText("鬧鐘遊戲\n  "+preferences.getString("gametext","遊戲一"));
    }
}