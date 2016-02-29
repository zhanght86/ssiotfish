package com.ssiot.fish.question;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageButton;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.fish.question.widget.VerticalSwipeRefreshLayout;
import com.ssiot.remote.data.business.Question;
import com.ssiot.remote.data.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class QuestionListActivity extends HeadActivity{
    private static final String tag = "QuestionListActivity";
    List<QuestionModel> questions = new ArrayList<QuestionModel>();
    VerticalSwipeRefreshLayout questionLayout;
    private ImageButton btnNew;
    private int currentPages = 0;
    
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    questionLayout.refreshUI();
                    break;
                    
                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_user_list);
        btnNew = (ImageButton) findViewById(R.id.question_new);
        questionLayout = (VerticalSwipeRefreshLayout) findViewById(R.id.ask_listview);
        questionLayout.setAdapter(new QuestionCardAdapter(questions));
        questionLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetQuestionThread().start();
            }
        });
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, QuestionNewActivity.class);
                startActivity(intent);
            }
        });
        
        new GetQuestionThread().start();
    }
    
    private class GetQuestionThread extends Thread{
        @Override
        public void run() {
            List<QuestionModel> list = new Question().GetPageList(currentPages);
            if (null != list){
                questions.clear();
                questions.addAll(list);
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private void loadMore(){
        currentPages ++;
        List<QuestionModel> list = new Question().GetPageList(currentPages);
        if (null != list){
            questions.addAll(list);//TODO 重复
        }
        mHandler.sendEmptyMessage(MSG_GET_END);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}