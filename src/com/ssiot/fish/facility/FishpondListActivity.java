package com.ssiot.fish.facility;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.remote.data.business.FacilitiesFishpond;
import com.ssiot.remote.data.model.FacilitiesFishpondModel;
import com.ssiot.remote.data.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class FishpondListActivity extends HeadActivity{
    private static final String tag = "FishpondListActivity";
    
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    List<FacilitiesFishpondModel> mDatas = new ArrayList<FacilitiesFishpondModel>();
    RecyclerView mRecyclerView;
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;

                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiperefresh_list);
        
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelist_layout);
        mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
        Log.v(tag, "----------------------");
        swipeRefreshLayout.addView(mRecyclerView);
        Log.v(tag, "------------------1");
        mRecyclerView.setHasFixedSize(true);
        Log.v(tag, "------------------2");
        LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
        bLinearLayoutManager.setOrientation(1);
        mRecyclerView.setLayoutManager(bLinearLayoutManager);
        
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetFishpondThread().start();
            }
        });
        mAdapter = new FishpondAdapter(mDatas);
        setAdapter(mAdapter);
        
        new GetFishpondThread().start();
    }
    
    public final void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }
    
    private class GetFishpondThread extends Thread{
        @Override
        public void run() {
            List<FacilitiesFishpondModel> list = new FacilitiesFishpond().GetModelList(" 1=1");
            if (null != list && list.size() > 0){
                mDatas.clear();
                mDatas.addAll(list);
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private class FishpondAdapter extends RecyclerView.Adapter{
        private List<FacilitiesFishpondModel> list;
        
        public FishpondAdapter(List<FacilitiesFishpondModel> lis){
            list = lis;
        }
        
        @Override
        public int getItemCount() {
            return list.size();
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
            return new FishpondViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            FishpondViewHolder holder = (FishpondViewHolder) viewHolder;
            FacilitiesFishpondModel fishpondModel = list.get(i);
            holder.fillData(fishpondModel);
        }
    }
    
    protected class FishpondViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private TextView mTitleView;
        private TextView mContentView;
        FacilitiesFishpondModel fishpond;
        public FishpondViewHolder(View v) {
            super(v);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mContentView = (TextView) v.findViewById(R.id.txt_content);
            v.setOnClickListener(this);
        }
        
        public void fillData(FacilitiesFishpondModel model){
            fishpond = model;
            mTitleView.setText(model._name);
            mContentView.setText(model._addr);
        }

        @Override
        public void onClick(View v) {
            if (fishpond._latitute * fishpond._longitude != 0){
                Intent intent = new Intent(FishpondListActivity.this, MapActivity.class);
                
                intent.putExtra("x", fishpond._longitude);
                intent.putExtra("y", fishpond._latitute);
                float[] floats = {fishpond._longitude, fishpond._latitute};
                intent.putExtra("locations", floats);
                startActivity(intent);
            } else {
                Toast.makeText(FishpondListActivity.this, "无地图信息", Toast.LENGTH_SHORT).show();
            }
            
        }
        
    }
}