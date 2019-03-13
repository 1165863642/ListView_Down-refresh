package com.a11658.android_listview_down_refresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReFlashListView.IReflashListener {

    private ReFlashListView listview;
    MyAdapter adapter;
    List<APKEntity> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        listview = findViewById(R.id.listview);
        getList();
        adapter = new MyAdapter(this, mList);
        adapter.notifyDataSetChanged();
        listview.setInterface(this);
        listview.setAdapter(adapter);
    }

    public List<APKEntity> getList() {
        for (int i = 0; i < 10; i++) {
            mList.add(new APKEntity(R.drawable.ic_launcher_foreground, "默认数据", "这是一个神奇的应用"));
        }
        return mList;
    }

    public List<APKEntity> setReflashData() {
        for (int i = 0; i < 2; i++) {
            mList.add(0, new APKEntity(R.drawable.ic_launcher_foreground, "刷新数据", "这是一个神奇的应用"));
        }
        return mList;
    }

    @Override
    public void onReflash() {
        //获取到最新的数据
        setReflashData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //通知界面显示
                adapter.notifyDataSetChanged();
                listview.invalidate();
                //通知listview刷新数据显示
                listview.reflashComplete();
            }
        }, 2000);


    }
}
