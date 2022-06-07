package com.example.greatsleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.example.greatsleep.Clock.ClockFragment;
import com.example.greatsleep.Diaries.DiaryMenuFragment;
import com.example.greatsleep.Dreams.DreamFragment;
import com.example.greatsleep.SleepData.SleepFragment;
import com.example.greatsleep.Station.StationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.example.greatsleep.Clock.ClockFragment;
import com.example.greatsleep.Diaries.DiaryMenuFragment;
import com.example.greatsleep.Dreams.DreamFragment;
import com.example.greatsleep.SleepData.SleepFragment;
import com.example.greatsleep.Station.StationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends SettingActivity{
    FirebaseAuth firebaseAuth;
    private FragmentManager fmgr;
    private SleepFragment sleepFragment;
    private StationFragment stationFragment;
    private ClockFragment clockFragment;
    private DreamFragment dreamFragment;
    private DiaryMenuFragment diaryMenuFragment;
    SharedPreferences station_preferences;
    SharedPreferences.Editor station_editor;
    SharedPreferences clock_preferences;
    SharedPreferences.Editor clock_editor;
    BottomNavigationView bottomNavigationView;
    private int change;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //到站提示儲存
        station_preferences = getSharedPreferences("station", Context.MODE_PRIVATE);
        station_editor = station_preferences.edit();
        //鬧鐘儲存
        clock_preferences=getSharedPreferences("clock", Context.MODE_PRIVATE);
        clock_editor=clock_preferences.edit();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();

        fmgr=getSupportFragmentManager();
        sleepFragment=new SleepFragment();
        stationFragment=new StationFragment();
        clockFragment=new ClockFragment();
        dreamFragment=new DreamFragment();
        diaryMenuFragment=new DiaryMenuFragment();

        //設定初始介面
        FragmentTransaction transaction=fmgr.beginTransaction();
        transaction.add(R.id.container1,sleepFragment);
        transaction.commit();

        connect();

        change =R.id.action_sleep;
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        BottomNavigationItemView bottomNavigationItemView=bottomNavigationView.getChildAt(0).findViewById(R.id.action_sleep);
        bottomNavigationItemView.setOnLongClickListener(v -> true);
        bottomNavigationItemView=bottomNavigationView.getChildAt(0).findViewById(R.id.action_station);
        bottomNavigationItemView.setOnLongClickListener(v -> true);
        bottomNavigationItemView=bottomNavigationView.getChildAt(0).findViewById(R.id.action_clock);
        bottomNavigationItemView.setOnLongClickListener(v -> true);
        bottomNavigationItemView=bottomNavigationView.getChildAt(0).findViewById(R.id.action_dream);
        bottomNavigationItemView.setOnLongClickListener(v -> true);
        bottomNavigationItemView=bottomNavigationView.getChildAt(0).findViewById(R.id.action_diary);
        bottomNavigationItemView.setOnLongClickListener(v -> true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                //換介面
                switch(item.getItemId()){
                    case R.id.action_sleep:
                        FragmentTransaction transaction=fmgr.beginTransaction();
                        transaction.setCustomAnimations(R.animator.slide_left_in,R.animator.slide_right_out);
                        transaction.replace(R.id.container1,sleepFragment);
                        change =R.id.action_sleep;
                        transaction.commit();
                        break;
                    case R.id.action_station:
                        transaction=fmgr.beginTransaction();
                        if(change ==R.id.action_sleep)
                            transaction.setCustomAnimations(R.animator.slide_right_in,R.animator.slide_left_out);
                        else
                            transaction.setCustomAnimations(R.animator.slide_left_in,R.animator.slide_right_out);
                        transaction.replace(R.id.container1,stationFragment);
                        change =R.id.action_station;
                        transaction.commit();
                        break;
                    case R.id.action_clock:
                        transaction=fmgr.beginTransaction();
                        if(change ==R.id.action_sleep || change ==R.id.action_station)
                            transaction.setCustomAnimations(R.animator.slide_right_in,R.animator.slide_left_out);
                        else
                            transaction.setCustomAnimations(R.animator.slide_left_in,R.animator.slide_right_out);
                        transaction.replace(R.id.container1,clockFragment);
                        change =R.id.action_clock;
                        transaction.commit();
                        break;
                    case R.id.action_dream:
                        transaction=fmgr.beginTransaction();
                        if(change ==R.id.action_diary)
                            transaction.setCustomAnimations(R.animator.slide_left_in,R.animator.slide_right_out);
                        else
                            transaction.setCustomAnimations(R.animator.slide_right_in,R.animator.slide_left_out);
                        transaction.replace(R.id.container1,dreamFragment);
                        change =R.id.action_dream;
                        transaction.commit();
                        break;
                    case R.id.action_diary:
                        transaction=fmgr.beginTransaction();
                        transaction.setCustomAnimations(R.animator.slide_right_in,R.animator.slide_left_out);
                        transaction.replace(R.id.container1,diaryMenuFragment);
                        change=R.id.action_diary;
                        transaction.commit();
                        break;
                }
                return true;
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            //user not login
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else{
            //user login
            String email=user.getEmail();
        }
    }

    //返回鍵當作home鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}