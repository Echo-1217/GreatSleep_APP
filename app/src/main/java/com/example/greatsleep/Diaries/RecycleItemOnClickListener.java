package com.example.greatsleep.Diaries;

import android.view.View;

public interface RecycleItemOnClickListener {
    public void onItemClick(View v, int position);

    public void onItemDeleteClick(View v,int position);
}
