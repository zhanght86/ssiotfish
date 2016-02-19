package com.ssiot.fish.question.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;//jingbo 导入新版v7 library源码内的RecyclerView模块
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import com.ssiot.fish.R;
import com.ssiot.fish.question.QuestionCardAdapter;
import com.ssiot.fish.question.QuestionBean;

import java.util.ArrayList;

public class VerticalSwipeRefreshLayout extends SwipeRefreshLayout{
    private static final String tag = "VerticalSwipeRefreshLayout";
  private RecyclerView aRecyclerView;
  private LinearLayoutManager bLinearLayoutManager;
  QuestionCardAdapter cAdapter;
//  private final RecyclerView.OnScrollListener iOnScrollListen;

  private static final int MSG_REFRESH_END = 1;
  private Handler mHandler = new Handler(){
      @Override
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          switch (msg.what) {
          case MSG_REFRESH_END:
              
              setRefreshing(false);
              cAdapter.notifyDataSetChanged();
              //swipeRefreshLayout.setEnabled(false);
              break;
          default:
              break;
          }
      }
  };
  
  public VerticalSwipeRefreshLayout(Context paramContext){
    this(paramContext, null);
  }

  public VerticalSwipeRefreshLayout(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    Log.v(tag, "----new VSwipeRefreshLayout----");
//    iOnScrollListen = new aw(this);
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = R.color.master_green_color;
    setColorSchemeResources(arrayOfInt);////设置跑动的颜色值
    aRecyclerView = ((RecyclerView)LayoutInflater.from(paramContext).inflate(R.layout.vertical_recycler_view, null));
    addView(aRecyclerView);
    aRecyclerView.setHasFixedSize(true);
    bLinearLayoutManager = new LinearLayoutManager(paramContext);
    bLinearLayoutManager.setOrientation(1);
    aRecyclerView.setLayoutManager(bLinearLayoutManager);
    ArrayList<QuestionBean> datas = new ArrayList<QuestionBean>();
    cAdapter = new QuestionCardAdapter(datas);
    aRecyclerView.setAdapter(cAdapter);//没adapter就没circleview在转动
//    aRecyclerView.addOnScrollListener(iOnScrollListen);
//    aRecyclerView.setOnTouchListener(new ax(this));
//    setOnRefreshListener(new ay(this));
//    aRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
    setSize(SwipeRefreshLayout.LARGE);
    setOnRefreshListener(new OnRefreshListener() {
        @Override
        public void onRefresh() {
            // TODO Auto-generated method stub
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    data.clear();
//                    for(int i=0;i<20;i++){
//                        data.add("SwipeRefreshLayout下拉刷新"+i);
//                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(MSG_REFRESH_END);
                }
            }).start();
        }
    });
  }

    public final void a(int paramInt) {
        bLinearLayoutManager.scrollToPosition(paramInt);
    }

    public final void a(int paramInt1, int paramInt2) {
        bLinearLayoutManager.scrollToPositionWithOffset(paramInt1, paramInt2);
    }

    public final LinearLayoutManager getLayoutManager() {
        return bLinearLayoutManager;
    }

    public final void setAdapter(QuestionCardAdapter parama) {
        cAdapter = parama;
        aRecyclerView.setAdapter(parama);
    }

    public final void setItemAnimator(RecyclerView.ItemAnimator paramItemAnimator) {
        aRecyclerView.setItemAnimator(paramItemAnimator);
    }

    public final void refreshUI(){
        setRefreshing(false);
        if (null != cAdapter){
            cAdapter.notifyDataSetChanged();
        } else {
            Log.e(tag, "-------questioncardadapter == null!!!");
        }
        
    }
}