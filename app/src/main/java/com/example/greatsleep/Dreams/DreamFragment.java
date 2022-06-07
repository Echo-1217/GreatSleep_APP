package com.example.greatsleep.Dreams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingMainActivity;
import com.example.greatsleep.TypeFaceProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class DreamFragment extends Fragment {
    private View mainView;
    private Toolbar toolbar;
    private Button btnshow;
    private EditText editText;
    private String category="";
    private String str;
    private String sql;
    private Integer i;
    private Spinner spinner;
    private String type;
    private String name;
    private String r="";
    private String url="http://163.13.201.92/dreamdata.php";
    private JSONArray jsonArray;
    JSONObject jsonObject;
    private Integer j;
    TextView text1;

    private MyExpandableListAdapter myExpandableListAdapter;
    private ExpandableListView expandableListView;
    private ArrayList<String> TitleName ;//父層標題
    private ArrayList<String> itemArray ;//子層陣列
    private TextView introduce;
    public AlertDialog dialog;

    Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface= TypeFaceProvider.getTypeFace(getActivity(),"introtype.ttf");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.setThreadPolicy
                (new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build()
                );
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        );
        mainView=inflater.inflate(R.layout.fragment_dream, container, false);
        text1=mainView.findViewById(R.id.dreamtext1);
        text1.setTypeface(typeface);

        dialog = new SpotsDialog(getActivity(),"請稍後...", R.style.Custom);
        dialog.setCancelable(false);

        expandableListView = mainView.findViewById(R.id.expandAbleListView);
        expandableListView.setVisibility(View.INVISIBLE);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                expandableListView.collapseGroup(groupPosition);
                return false;
            }
        });

        introduce=mainView.findViewById(R.id.introduce);
        introduce.setTypeface(typeface);
        toolbar=mainView.findViewById(R.id.toolbar_dream);

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
        btnshow = (Button) mainView.findViewById(R.id.search);
        editText = (EditText)  mainView.findViewById(R.id.editText);
        spinner = (Spinner) mainView.findViewById(R.id.spinner);
        btnshow.setTypeface(typeface);
        editText.setTypeface(typeface);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.dream_type,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,false);
        category = "dongwu"; type="動物";
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name = spinner.getSelectedItem().toString();
                if (name.equals("動物"))
                {
                    category = "dongwu"; type="動物";
                }
                else if(name.equals("鬼神"))
                {
                    category = "guishen";type="鬼神";
                }
                else if(name.equals("活動"))
                {
                    category = "huodong";type="活動";
                }
                else if(name.equals("建築"))
                {
                    category = "jianzhu";type="建築";
                }
                else if(name.equals("其他"))
                {
                    category = "qita";type="其他";
                }
                else if(name.equals("人物"))
                {
                    category = "renwu";type="人物";
                }
                else if(name.equals("生活"))
                {
                    category = "shenghuo";type="生活";
                }
                else if(name.equals("物品"))
                {
                    category = "wupin";type="物品";
                }
                else if(name.equals("孕婦"))
                {
                    category = "yunfu";type="孕婦";
                }
                else if(name.equals("植物"))
                {
                    category = "zhiwu";type="植物";
                }
                else
                {
                    category = "ziran";type="自然";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = editText.getText().toString().trim();
                if(jsonArray!=null && str.equals("")){
                    Toast.makeText(getActivity(),"系統將顯示 『"+type+"』 選項的所有內容\n"+"(周公解夢官網提供)", Toast.LENGTH_SHORT).show();
                }
                else if(jsonArray!=null && jsonArray.length()>0){
                    Toast.makeText(getActivity(),"系統將顯示包含『"+str+"』的搜尋內容\n"+"(周公解夢官網提供)", Toast.LENGTH_SHORT).show();
                }
                new Task().execute();
            }
        });
        return mainView;
    }

    //不需要動
    class Task extends AsyncTask<Void, Void, Void> {
        String error = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            introduce.setVisibility(View.INVISIBLE);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            TitleName = new ArrayList<>();//父層標題
            itemArray = new ArrayList<>();//子層陣列
            j=0;
            if(str.equals("") && !(category.equals("")))
            {
                jsonArray = DBString.DB1("select * from "+category,url);
            }
            else
            {
                jsonArray = DBString.DB1("select * from "+category+" WHERE name LIKE "+"\'"+"%"+str+"%"  +  "\'"+" order by LENGTH(name);",url);
            }
            try{
                for(int i=0;i<jsonArray.length();i++)
                {
                    j++;
                    jsonObject = jsonArray.getJSONObject(i);
                    if (str.equals(""))
                    {
                        String t= jsonObject.getString("content")
                                .replaceAll("。","。\n\n").replaceAll("？","?\n\n")
                                .replaceAll("，請看下面由","。\n")
                                .replaceAll("小編幫你整理的夢見"+jsonObject.getString("name")+"的詳細解說吧。","")
                                .replaceAll("\\("+"周公解夢官網"+"\\)","")
                                .replaceAll("此夢過後。","").replaceAll("一些意義：","一些意義："+"\n")
                                .replaceAll("\\("+"由周公解夢","").replaceAll("/提供\\)","")
                                .replaceAll("\\("+"來自\\）","").replaceAll("@","").replaceAll("^^","")
                                .replaceAll("WWW@.","")
                                .replaceAll("夢見"+jsonObject.getString("name")+"的案例分析","\n"+"夢見"+jsonObject.getString("name")+"的案例分析："+"\n\n");
                        itemArray.add(i,t);
                        TitleName.add(i,j+". "+jsonObject.getString("name"));
                    }
                    else
                    {
                        String t=jsonObject.getString("content")
                                .replaceAll("。","。\n\n").replaceAll("？","?\n\n")
                                .replaceAll("，請看下面由","。\n")
                                .replaceAll("小編幫你整理的夢見"+jsonObject.getString("name")+"的詳細解說吧。","")
                                .replaceAll("\\("+"周公解夢官網"+"\\)","")
                                .replaceAll("此夢過後。","").replaceAll("一些意義：","一些意義："+"\n")
                                .replaceAll("\\("+"由周公解夢","").replaceAll("/提供\\)","")
                                .replaceAll("\\("+"來自\\）","").replaceAll("@","").replaceAll("^^","")
                                .replaceAll("WWW@.","")
                                .replaceAll("夢見"+jsonObject.getString("name")+"的案例分析","\n"+"夢見"+jsonObject.getString("name")+"的案例分析："+"\n\n");
                        itemArray.add(i,t);
                        TitleName.add(i,j+". "+jsonObject.getString("name"));
                    }
                }
            }
            catch (Exception e)
            {
                error = e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            if(!error.equals("")){
                introduce.setVisibility(View.VISIBLE);
                introduce.setText("請檢查是否已連上網路!");
                expandableListView.setVisibility(View.INVISIBLE);
            }
            else if(jsonArray.length()>0){
                expandableListView.setVisibility(View.VISIBLE);
                myExpandableListAdapter = new MyExpandableListAdapter();
                expandableListView.setAdapter(myExpandableListAdapter);
            }
            else{
                introduce.setVisibility(View.VISIBLE);
                introduce.setText("查無搜尋內容，請選擇相關類別!");
                expandableListView.setVisibility(View.INVISIBLE);
            }
            dialog.dismiss();
            super.onPostExecute(unused);
        }
    }

    private class MyExpandableListAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {//父陣列長度
            return TitleName.size();
        }
        @Override
        public int getChildrenCount(int groupPosition) {//子陣列長度
            return 1;
        }
        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public boolean hasStableIds() {
            return false;
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {//設置父項目的View
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandedlistview_item,null);
            }
            convertView.setTag(R.layout.expandedlistview_item,groupPosition);
            TextView Title = convertView.findViewById(R.id.textView_ItemTitle);
            Title.setTypeface(typeface);
            Title.setText(TitleName.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null){//設置子項目的View
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandedlistview_child,null);
            }
            convertView.setTag(R.layout.expandedlistview_child,groupPosition);
            TextView item = convertView.findViewById(R.id.textView_child1);
            item.setTypeface(typeface);
            item.setText(itemArray.get(groupPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {//設置子項目是否可點擊
            return true;
        }
    }
}