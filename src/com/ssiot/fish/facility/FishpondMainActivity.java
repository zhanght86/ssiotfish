package com.ssiot.fish.facility;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.baidu.mapapi.SDKInitializer;
import com.ssiot.fish.CellModel;
import com.ssiot.fish.FishMainActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.ImageAdapter;
import com.ssiot.fish.R;
import com.ssiot.fish.question.QuestionListActivity;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.expert.DiagnoseFishSelectActivity;

import java.util.ArrayList;

public class FishpondMainActivity extends HeadActivity{
    private GridView gridView1;
    ArrayList<CellModel> cells;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main_fish);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_cekong,"新建渔场", "map_newyuchang"));
        cells.add(new CellModel(R.drawable.cell_cekong,"我的渔场", "map_myyuchang"));
        ImageAdapter adapter = new ImageAdapter(this, cells);
        gridView1.setAdapter(adapter);
        gridView1.setSelector(new ColorDrawable(0));
        gridView1.setOnItemClickListener(gvAppListen);
    }
    
    AdapterView.OnItemClickListener gvAppListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != cells){
                CellModel model = cells.get(position);
                if ("新建渔场".equals(model.itemText)){
                    Intent i = new Intent(FishpondMainActivity.this, FishpondNewActivity.class);
                    startActivity(i);
                } else if ("我的渔场".equals(model.itemText)){
                    Intent intent = new Intent(FishpondMainActivity.this, FishpondListActivity.class);
                    startActivity(intent);
                }
            }
        }
    };
}