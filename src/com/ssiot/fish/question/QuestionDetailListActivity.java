package com.ssiot.fish.question;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.fish.question.widget.VerticalSwipeRefreshLayout;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.business.Answer;
import com.ssiot.remote.data.business.Question;
import com.ssiot.remote.data.model.AnswerModel;
import com.ssiot.remote.data.model.QuestionModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QuestionDetailListActivity extends HeadActivity{
    private static final String tag = "QuestionDetailListActivity";
    VerticalSwipeRefreshLayout mSwipeReFreshLayout;
    QuestionModel qModel;
    List<AnswerModel> datasList = new ArrayList<AnswerModel>();
    EditText mEditText;
    TextView mSendBtn;
    SharedPreferences mPref;
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_ADD_END = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    if (null != mSwipeReFreshLayout){
                        mSwipeReFreshLayout.refreshUI();
                    }
                    break;
                case MSG_ADD_END:
                    if (msg.arg1 > 0){
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        mEditText.setText("");
                        new GetAnswerThread().start();
                    }
                    break;

                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mPref = PreferenceManager.getDefaultSharedPreferences(QuestionDetailListActivity.this);
        qModel = (QuestionModel) getIntent().getSerializableExtra("questionmodel");
        if (null == qModel){
            Log.e(tag, "-----error");
        }
        setContentView(R.layout.activity_ask_detail);
        mSwipeReFreshLayout = (VerticalSwipeRefreshLayout) findViewById(R.id.shop_cmt_listview);
        mSwipeReFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetAnswerThread().start();
            }
        });
        if (null != qModel){
            mSwipeReFreshLayout.setAdapter(new AnswerCardAdapter(datasList, qModel));
        }
        RelativeLayout bottomBar = (RelativeLayout) findViewById(R.id.ask_detail_tool);
        if (qModel._userId == mPref.getInt(Utils.PREF_USERID, -1)){
            bottomBar.setVisibility(View.GONE);
        }
        mEditText = (EditText) findViewById(R.id.shop_cmt_submit_edittext);
        mSendBtn = (TextView) findViewById(R.id.shop_cmt_submit_button);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                if (!TextUtils.isEmpty(text)){
                    
                    
                    final AnswerModel model = new AnswerModel();
                    model._questionid = qModel._id;
                    model._userid = mPref.getInt(Utils.PREF_USERID, 0);
                    model._username = mPref.getString(Utils.PREF_USERNAMETEXT, "");
                    model._addr = mPref.getString(Utils.PREF_ADDR, "");
                    model._userpicurl = mPref.getString(Utils.PREF_AVATAR, "");
                    model._createTime = new Timestamp(System.currentTimeMillis());
                    model._contenttext = text;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int ret = new Answer().Add(model);
                            int replayadd = new Question().UpdateReplyCount(qModel._id, qModel._replyCount + 1);
                            Message m = mHandler.obtainMessage(MSG_ADD_END);
                            m.arg1 = ret;
                            mHandler.sendMessage(m);
                        }
                    }).start();
                }
            }
        });
        new GetAnswerThread().start();
    }
    
    private class GetAnswerThread extends Thread{
        @Override
        public void run() {
            List<AnswerModel> list = new Answer().GetModelList(" QuestionID=" + qModel._id);
            datasList.clear();
            if (null != list){
                datasList.addAll(list);
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
}