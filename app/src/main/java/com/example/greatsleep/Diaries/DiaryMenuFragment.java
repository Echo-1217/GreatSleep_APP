package com.example.greatsleep.Diaries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingMainActivity;
import com.example.greatsleep.TypeFaceProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class DiaryMenuFragment extends Fragment implements RecycleItemOnClickListener {
    private static Context context;
    private DiaryAdapter diaryAdapter;
    private FloatingActionButton addbutton;
    private RecyclerView recyclerView;
    ArrayList<Diary> diaryArrayList;
    //FireStore及getEmail
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Map<String, Object> Fdiary = new HashMap<>();
    String nowDate;
    String email;
    View view;
    String temp;
    static Typeface typeface;
    private Toolbar toolbar;
    public enum IntentOption {
        NEW, EDIT, DELETE, CANCEL
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface= TypeFaceProvider.getTypeFace(getActivity(),"introtype.ttf");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_diary_menu, container, false);

        toolbar=view.findViewById(R.id.toolbar_diary);
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
        //GetEmail
        preferences = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
        editor = preferences.edit();
        email = preferences.getString("userdocument","");

        DiaryMenuFragment.context =getActivity().getApplicationContext();
        addbutton = view.findViewById(R.id.add_button);
        //設定recycleView
        recyclerView = (RecyclerView) view.findViewById(R.id.diary_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        diaryArrayList = new ArrayList<Diary>();
        try {
            diaryAdapter = new DiaryAdapter(
                    DiaryMenuFragment.getAppContext(), diaryArrayList,(RecyclerView) view.findViewById(R.id.diary_list));

        } catch (Exception e) {
            showError("", e.getMessage(), getActivity());
        }
        recyclerView.setAdapter(diaryAdapter);
        diaryAdapter.setClickListener(this);
        EventChangeListener();

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newButtonClick(addbutton);
            }
        });


        return view;
    }

    public void newButtonClick(View v)
    {
        Intent intent = new Intent(getActivity(), DiaryNew.class);
        intent.putExtra("MODE", IntentOption.NEW);
        this.startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //get Login Email
        Log.v("userdocument",email);

        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                IntentOption mode = (IntentOption) data.getSerializableExtra("MODE");
                nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                if(mode == IntentOption.NEW)
                {
                    try
                    {
                        String title=data.getStringExtra("title");
                        String text = data.getStringExtra("text");

                        Fdiary.put("text", text);
                        Fdiary.put("title", title);
                        Fdiary.put("date",nowDate);
                        db.collection("User").document(email).collection("diary").
                                document(nowDate.replace("/","")).set(Fdiary, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ss", "Diary successfully created!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ss", "Diary Error writing created", e);
                                    }
                                });
                    }
                    catch(Exception e)
                    {
                        showError("", e.getMessage(), getActivity());
                    }
                }
                else if(mode == IntentOption.EDIT)
                {
                    String title=data.getStringExtra("title");
                    String text = data.getStringExtra("text");
                    String date=data.getStringExtra("date");

                    try
                    {
                        Log.d("date",date);
                        db.collection("User").document(email).collection("diary")
                                .document(date.replace("/","")).delete();

                        Fdiary.put("text", text);
                        Fdiary.put("title", title);
                        Fdiary.put("date",nowDate);
                        db.collection("User").document(email).collection("diary")
                                .document(nowDate.replace("/","")).set(Fdiary, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ss", "Diary successfully modify!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ss", "Diary Error writing modify", e);
                                    }
                                });
                    }
                    catch(Exception e)
                    {
                        showError("", e.getMessage(), getActivity());
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        temp=null;
    }
    //將Diary存成物件到Recycleview
    private void EventChangeListener(){
        db.collection("User").document(email).collection("diary")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FireStore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                if(dc.getDocument().toObject(Diary.class).getTitle()==temp){
                                }
                                else{
                                    diaryArrayList.add(dc.getDocument().toObject(Diary.class));
                                }
                                temp=dc.getDocument().toObject(Diary.class).getTitle();
                            }
                            else if (dc.getType() == DocumentChange.Type.REMOVED){
                                diaryArrayList.remove(dc.getDocument().toObject(Diary.class));
                                try {
                                    diaryAdapter.removeData(dc.getDocument().toObject(Diary.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                diaryAdapter.notifyDataSetChanged();
                            }
                            diaryAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static Context getAppContext()
    {
        return DiaryMenuFragment.context;
    }

    @Override
    public void onItemClick(View v, int position)
    {
        Diary data = diaryAdapter.getData(position);
        Intent intent = new Intent(getActivity(), DiaryNew.class);
        intent.putExtra("title",data.getTitle());
        intent.putExtra("text", data.getText());
        intent.putExtra("date",data.getDate());
        intent.putExtra("MODE", IntentOption.EDIT);
        editor.putString("position",data.getDate());
        editor.apply();
        this.startActivityForResult(intent, 1);
    }

    @Override
    public void onItemDeleteClick(View v, int position) {
        Diary data = diaryAdapter.getData(position);
        Log.v("sdf",position+"   錯誤發生  aa "+ data);
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_design,null);//嵌入View
        Button d_confirm=view.findViewById(R.id.dialog_confirm);
        Button d_cancel=view.findViewById(R.id.dialog_cancel);
        d_confirm.setTypeface(typeface);
        d_cancel.setTypeface(typeface);
        TextView dialog_message=view.findViewById(R.id.dialog_message);
        dialog_message.setTypeface(typeface);
        ab.setView(view);
        AlertDialog dialog=ab.create();
        dialog_message.setText("日記的內容將會消失，確定要刪除嗎?");
        d_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    diaryAdapter.removeData(data);

                }
                catch(Exception e)
                {
                    showError("", e.getMessage(),getActivity());
                }
                diaryAdapter.notifyDataSetChanged();
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
    }

    public static void showError(String title, String msg, Activity activity)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
            }
        });
        builder.create().show();
    }
}