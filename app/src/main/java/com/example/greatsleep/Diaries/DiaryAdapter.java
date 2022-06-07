package com.example.greatsleep.Diaries;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.greatsleep.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    RecycleItemOnClickListener clickListener;
    static RecyclerView list;
    Context context;
    ArrayList<Diary> diaryArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userdocument;
    DiaryMenuFragment font;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    public DiaryAdapter(Context context, ArrayList<Diary> diaryArrayList, RecyclerView list) throws Exception {
        this.list = list;
        this.context = context;
        this.diaryArrayList = diaryArrayList;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;
        private Button btDelete;
        private SwipeRevealLayout swipeRevealLayout;
        public ViewHolder(View v) {
            super(v);
            preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
            editor = preferences.edit();
            userdocument = preferences.getString("userdocument","");

            btDelete = v.findViewById(R.id.button_Delete);
            swipeRevealLayout = v.findViewById(R.id.swipeLayout);
            title = (TextView) v.findViewById(R.id.ItemContent);
            date = (TextView) v.findViewById(R.id.DateText);

            date.setKeyListener(null);
        }
    }
    @NonNull
    @Override
    public DiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_diary_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        font=new DiaryMenuFragment();
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder holder, int position) {
        Diary diary = diaryArrayList.get(position);
        viewBinderHelper.setOpenOnlyOne(true);//設置swipe只能有一個item被拉出
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));//綁定Layout
        holder.swipeRevealLayout.close(true);
        holder.title.setText(diary.getTitle());
        holder.date.setText(diary.getDate().substring(0,16));

        holder.title.setTypeface(font.typeface);
        holder.date.setTypeface(font.typeface);
        holder.btDelete.setTypeface(font.typeface);

        holder.itemView.setBackgroundColor(Color.parseColor("#80424242"));

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    int pos = position;
                    clickListener.onItemClick(v, pos);
                }

            }
        });
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    int pos = position;
                    clickListener.onItemClick(v, pos);
                }
            }
        });

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeRevealLayout.close(true);
                onDeleteClick(v, position);
            }
        });//holder.btDelete
    }

    public boolean onDeleteClick(View view,int position) {
        if (clickListener != null) {
            int pos = position;
            clickListener.onItemDeleteClick(view, pos);
        }
        return false;
    }
    @Override
    public int getItemCount() {
        return diaryArrayList.size();
    }

    public void setClickListener(RecycleItemOnClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public void removeData(Diary data) throws Exception
    {
        int pos = getPos(data);
        if(pos < 0)
            throw new Exception("Unable to find item");

        diaryArrayList.remove(pos);
        db.collection("User").document(userdocument).collection("diary")
                .document(data.getDate().replace("/","")).delete();
        this.notifyItemRemoved(pos);
        this.notifyDataSetChanged();

    }

    public Diary getData(int pos) {
        return diaryArrayList.get(pos);
    }
    private int getPos(Diary data) {
        for (int i = 0; i < diaryArrayList.size(); i++) {
            if (diaryArrayList.get(i).getDate().equals(data.getDate()))
                return i;
        }
        return -1;
    }
}


