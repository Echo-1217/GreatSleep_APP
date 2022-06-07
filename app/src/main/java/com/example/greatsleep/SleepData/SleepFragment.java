package com.example.greatsleep.SleepData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingMainActivity;
import com.example.greatsleep.TypeFaceProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import android.widget.Button;
import android.widget.TextView;

import android.content.Context;
import android.content.SharedPreferences;


import android.util.Log;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.goyourlife.gofitsdk.GoFITSdk;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.example.greatsleep.SettingActivity._goFITSdk;

public class SleepFragment extends Fragment {
    private View mainView;
    private Toolbar toolbar;
    public Button bt_scan;
    public Button bt_pair;
    public TextView state;

    public String mPairingCode = null;
    public String mPairingTime = null;
    public String success = "Success !\n";

    public String step_data = null;

    TabLayout tabLayout;
    ViewPager viewPager;
    TextView battery;

    //FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //推播日期
    private int RuleBreaker = 0;//儲存違規次數
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar calendar = Calendar.getInstance();
    Calendar setNotificationTime = Calendar.getInstance(); //指定通知時間
    Calendar mCal = Calendar.getInstance();
    String awake;
    int dayhour;
    private static Context context;
    Handler mHandler;
    String electric;
    Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface= TypeFaceProvider.getTypeFace(getActivity(),"introtype.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_sleep, container, false);
        tabLayout=mainView.findViewById(R.id.tabLayout);
        viewPager=mainView.findViewById(R.id.viewPager);
        getTabs();
        toolbar=mainView.findViewById(R.id.toolbar_sleep);
        battery=mainView.findViewById(R.id.sleep_information);
        battery.setTypeface(typeface);

        mHandler=new Handler();
        mHandler.post(runnable);

        toolbar.inflateMenu(R.menu.menu_example);
        toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.action_setting){
                    Intent intent=new Intent(getActivity(), SettingMainActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        SleepFragment.context = getActivity().getApplicationContext();
        //設定Alarm
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(context.ALARM_SERVICE);
        Intent AlarmIntent = new Intent(context, SleepAlarmReceiver.class);
        PendingIntent AlarmPending = PendingIntent.getBroadcast(getContext(), 0, AlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //設定每天23點執行
        setNotificationTime = Calendar.getInstance();
        setNotificationTime.set(Calendar.HOUR_OF_DAY,23); //23：00
        setNotificationTime.set(Calendar.MINUTE, 0);
        setNotificationTime.set(Calendar.SECOND, 0);

        //取得使用者個人檔案
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
        //測試
//        String userdocument = "s0958952199@gmail.com";
        String userdocument = preferences.getString("userdocument","");//測試Email:s0958952199@gmail.com
        Log.d("ss", "userdocument " + userdocument);
        //從FireStore獲取睡間
        CollectionReference colRef = db.collection("User").document(userdocument).collection("sleepdata");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String today = df.format(mCal.getTime());


                    db.collection("User").document(userdocument).collection("sleepdata").document(today)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> data = new HashMap<>();
                                    data = document.getData();
                                    String dayawake = data.get("睡著時間").toString();
                                    Log.d("AWAKE", "" + dayawake);
                                    Date date = new Date();
                                    try {
                                        date = dateFormat.parse(dayawake);
                                        calendar.setTime(date);
                                        dayhour = calendar.get(Calendar.HOUR);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                                else {
                                    Log.d("ss", "No such document");
                                }
                            }
                            else {
                                Log.d("ss", "get failed with ", task.getException());
                            }
                        }
                    });
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Log.d("mnb", queryDocumentSnapshot.getId() + " =>3 " + queryDocumentSnapshot.getData());
                        Map<String, Object> awakeData = new HashMap<>();
                        Log.d("mnb", awakeData + " =>1 ");
                        awakeData = queryDocumentSnapshot.getData();
                        Log.d("mnb", awakeData + " =>2 ");
                        awake = awakeData.get("睡著時間").toString();
                        Log.d("AWAKE", "" + awake);
                        Date date = new Date();
                        try {
                            date = dateFormat.parse(awake);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //判斷是否超過12點上床睡覺
                        //如果違規次數超過3次,就推播(RuleBreaker>=3),直到不違規,不推播(RuleBreaker=0)
                        calendar.setTime(date);
                        int hour = calendar.get(Calendar.HOUR);
                        Log.d("Hour", String.valueOf(hour));
                        if (Math.abs(dayhour-hour)>=2) {
                            RuleBreaker++;
                        } else {
                            RuleBreaker = 0;
                        }
                    }
                    if (RuleBreaker >= 3) { //違規大於3次推播
                        //設定一天推播一次
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, setNotificationTime.getTimeInMillis()
                                ,AlarmManager.INTERVAL_DAY,AlarmPending);
                    } else {
                        RuleBreaker = 0;
                    }
                }
            }
        });
        return mainView;
    }
    @Override
    public void onResume() {
        super.onResume();
        getTabs();
    }
    public void getTabs(){
        final SleepPageSwapAdapter viewPagerAdapter=new SleepPageSwapAdapter(getActivity().getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPagerAdapter.addFragment(SleepDataFragment1.getInstance(),"睡眠日均");
                viewPagerAdapter.addFragment(SleepDataFragment2.getInstance(),"睡眠月均");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setOnLongClickListener(v -> true);
            }
        });
    }

    final Runnable runnable=new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(runnable, 5000);
            get_battery_info();
            battery.setText(electric);
        }
    };

    @Override
    public void onPause() {
        mHandler.removeCallbacks(runnable);
        super.onPause();
    }


    public void get_battery_info(){
        if (_goFITSdk != null) {
            // Demo - getDeviceBatteryValue API
            _goFITSdk.getDeviceBatteryValue(new GoFITSdk.GetDeviceInfoCallback() {
                @Override
                public void onSuccess(String info) {
                    electric="電量: "+info;
                    Log.i("_tag", "getDeviceBatteryValue() : onSuccess() : info = " + info);
                }
                @Override
                public void onFailure(int errorCode, String errorMsg) {
                    electric="尚未連接手環";
                    Log.i("_tag", "getDeviceBatteryValue() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                }
            });
        }
        battery.setText(electric);
    }
}