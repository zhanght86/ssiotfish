package com.ssiot.fish.question.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ssiot.fish.R;
import com.ssiot.remote.data.model.AnswerModel;
import com.ssiot.remote.data.model.QuestionModel;

public class AnswerCardView extends FrameLayout{
    private static final String tag = "AnswerCardView";
    private View aView;
    private ViewStub stub;
    private ViewStub countstub;
    
    TextView mUserNameView;
    TextView mContentTextView;
    public AnswerCardView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    
    public AnswerCardView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        
    }

    public AnswerCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        if ((attrs == null) || (defStyle == 0)) {
            
        }
        aView = LayoutInflater.from(context).inflate(R.layout.reply_detail_extra_card_view, this);
        stub = (ViewStub) aView.findViewById(R.id.question_detail_card);
        countstub = (ViewStub) aView.findViewById(R.id.ask_card_replycount_root);
        if (stub == null){
            Log.e(tag, "!!!!!!!!!!!!!!!stub==null");//放在初始化时不会返回null，费解
        }
        mUserNameView = (TextView) aView.findViewById(R.id.reply_detail_card_username);
        mContentTextView = (TextView) aView.findViewById(R.id.reply_detail_card_content);
        if (mUserNameView == null){
            Log.e("ssssssssssss", "dddddddd3333333333333ddddddddd");
        }
    }
    
    public void fillData(AnswerModel model){
        mUserNameView.setText(model._username);
        mContentTextView.setText(model._contenttext);
    }
    
    public void fillHeadData(QuestionModel qModel){//TODO TODO ??????
//        ViewStub stub = (ViewStub) aView.findViewById(R.id.question_detail_card);
//        if (stub == null){
            Log.e("ssssssssssss", "dddddddddddddddddddddddd");
//        }
//        stub.inflate();
        Log.e(tag, "----fillHeadData-----");
        stub.setVisibility(View.VISIBLE);
        countstub.setVisibility(View.VISIBLE);
        TextView titleView = (TextView) aView.findViewById(R.id.ask_card_title);
        TextView contentTextView = (TextView) aView.findViewById(R.id.ask_card_content);
        TextView addrView = (TextView) aView.findViewById(R.id.ask_card_address);
        titleView.setText(qModel._title);
        contentTextView.setText(qModel._contentText);
        if (!TextUtils.isEmpty(qModel._addr)){
            addrView.setText(qModel._addr);
        }
        aView.findViewById(R.id.reply_detail_card).setVisibility(View.GONE);
        
        
    }
}