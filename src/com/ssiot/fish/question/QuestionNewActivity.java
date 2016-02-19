package com.ssiot.fish.question;

import android.app.Activity;
import android.os.Bundle;

import com.ssiot.fish.R;

public class QuestionNewActivity extends Activity{
    private static final String tag = "QuestionNewActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question_extra);
//        if (getActionBar() != null){
//            getActionBar().hide();
//        }
    }
}