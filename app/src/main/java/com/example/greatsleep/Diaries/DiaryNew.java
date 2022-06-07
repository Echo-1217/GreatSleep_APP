package com.example.greatsleep.Diaries;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greatsleep.R;
import com.example.greatsleep.TypeFaceProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryNew extends AppCompatActivity {
    private EditText etext;
    private EditText etitle;
    private FloatingActionButton button;
    private DiaryMenuFragment.IntentOption mode;
    private DiaryMenuFragment.IntentOption dialogOption;
    private Diary data;
    TextView date;
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_new);
        typeface= TypeFaceProvider.getTypeFace(this,"introtype.ttf");
        etext = (EditText)findViewById(R.id.diary_edit);

        etitle=(EditText)findViewById(R.id.diary_title);
        etext.setTypeface(typeface);
        etitle.setTypeface(typeface);

        date=findViewById(R.id.date);
        date.setTypeface(typeface);

        button = findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etitle.getText().toString().equals("")){
                    Toast.makeText(DiaryNew.this,"請輸入標題",Toast.LENGTH_SHORT).show();
                }
                else if(etext.getText().toString().equals("")) {
                    Toast.makeText(DiaryNew.this, "請輸入您的日記內容", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveButtonClick(button);
                }
            }
        });

        Intent intent = getIntent();
        mode = (DiaryMenuFragment.IntentOption) intent.getSerializableExtra("MODE");

        switch(mode)
        {
            case NEW:
                break;
            case EDIT:
                String text = intent.getStringExtra("text");
                String title=intent.getStringExtra("title");
                String date = intent.getStringExtra("date");
                data = new Diary(text,title,date);
                break;
            default:
                break;
        }
        String title = getIntent().getStringExtra("title");

        String text = getIntent().getStringExtra("text");

        String date_time=getIntent().getStringExtra("date");

        if(date_time!=null) {
            date.setText(date_time.substring(0,16));
        }
        else{
            String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            date.setText(nowDate);
        }

        if(title!=null)
        {
            etitle.setText(title, TextView.BufferType.EDITABLE);
        }

        if(text!=null)
        {
            etext.setText(text, TextView.BufferType.EDITABLE);
        }
    }

    private void cancelActivity()
    {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void reactToUser()
    {
        switch(mode)
        {
            case NEW:
            {
                String title=etitle.getText().toString();

                Intent intent = new Intent();
                if(!title.isEmpty())
                {
                    intent.putExtra("title", title);
                }

                String text = etext.getText().toString();
                if(!text.isEmpty())
                {
                    intent.putExtra("text", text);
                    intent.putExtra("MODE", DiaryMenuFragment.IntentOption.NEW);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(DiaryNew.this,  "左滑可刪除日記", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    cancelActivity();
                }
            }
            break;

            case EDIT:
            {
                String title=etitle.getText().toString();
                String text = etext.getText().toString();
                String date=data.getDate();

                if(text != null && !text.isEmpty())
                {
                    Intent intent = new Intent();
                    intent.putExtra("title",title);
                    intent.putExtra("text", text);
                    intent.putExtra("date",date);
                    intent.putExtra("MODE", DiaryMenuFragment.IntentOption.EDIT);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else//Text is empty, delete it instead of overwrite to empty text
                {
                    finishIntentWithDelete();
                }
            }
            break;

            default:
                cancelActivity();
                break;
        }
    }

    public void saveButtonClick(View v)
    {
        if(mode != DiaryMenuFragment.IntentOption.NEW)
        {
            AlertDialog.Builder ab = new AlertDialog.Builder(DiaryNew.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_design,null);//嵌入View
            Button d_confirm=view.findViewById(R.id.dialog_confirm);
            Button d_cancel=view.findViewById(R.id.dialog_cancel);
            TextView dialog_message=view.findViewById(R.id.dialog_message);
            ab.setView(view);
            AlertDialog dialog=ab.create();
            dialog_message.setText("原先的日記內容將會消失");
            d_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogOption == DiaryMenuFragment.IntentOption.DELETE)
                    {
                        finishIntentWithDelete();
                    }
                    else if(dialogOption == DiaryMenuFragment.IntentOption.CANCEL)
                    {
                        cancelActivity();
                    }
                    else
                    {
                        reactToUser();
                    }
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
        else
        {
            reactToUser();
        }
    }

    private void finishIntentWithDelete()
    {
        Intent intent = new Intent();
        intent.putExtra("title", data.getTitle());
        intent.putExtra("text", data.getText());
        intent.putExtra("date",data.getDate());
        intent.putExtra("MODE", DiaryMenuFragment.IntentOption.DELETE);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setdone(View view) {
        DiaryNew.this.finish();
    }
}