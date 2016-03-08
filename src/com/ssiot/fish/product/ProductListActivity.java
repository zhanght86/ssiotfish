package com.ssiot.fish.product;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;

import com.ssiot.fish.MyListActivity;
import com.ssiot.remote.data.business.FishDrug;
import com.ssiot.remote.data.business.FishFeed;
import com.ssiot.remote.data.business.FishProduction;
import com.ssiot.remote.data.business.FishSmall;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends MyListActivity{
    private String tableName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tableName = getIntent().getStringExtra("whichtable");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetProductThread().start();
            }
        });
        new GetProductThread().start();
    }
    
    private class GetProductThread extends Thread{
        @Override
        public void run() {
            List list = null;
            if (!TextUtils.isEmpty(tableName)){
                if (tableName.equals("FishProduction")){
                    list = new FishProduction().GetModelList(" UserID=" + userid);
                } else if (tableName.equals("FishDrug")){
                    list = new FishDrug().GetModelList(" UserID=" + userid);
                } else if (tableName.equals("FishFeed")){
                    list = new FishFeed().GetModelList(" UserID=" + userid);
                } else if (tableName.equals("FishSmall")){
                    list = new FishSmall().GetModelList(" UserID=" + userid);
                }
            }
            
            if (null != list && list.size() > 0){
                mDatas.clear();
                List<CustomModel> customModels = new ArrayList<MyListActivity.CustomModel>(); 
                for (int i = 0; i < list.size(); i ++){
                    try {
                        customModels.add(new CustomModel(list.get(i)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mDatas.addAll(customModels);
            }
            sendRefreshInThread();
        }
    }
}