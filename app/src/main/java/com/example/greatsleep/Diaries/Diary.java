package com.example.greatsleep.Diaries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Diary {

    private String title;
    private String text;
    private String date;
    private static final int MAX_TITLE_LENGHT = 25;

    public Diary(){}
    public Diary(String text, String title, String date)
    {
        this.text = text;
        this.title=title;
        this.date = date;
    }

    public String getText()
    {
        return text;
    }


    public String getTitle(int end)
    {
        return getTitle(0, end);
    }


    public String getTitle(int start, int end)
    {
        if(title.length() < end)
            return title;
        return title.substring(start, end);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getDate()
    {
        return date;
    }

    @Override
    public String toString()
    {
        return getTitle(MAX_TITLE_LENGHT);
    }

}
