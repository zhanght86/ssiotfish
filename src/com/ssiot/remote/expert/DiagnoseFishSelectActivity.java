package com.ssiot.remote.expert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;

public class DiagnoseFishSelectActivity extends HeadActivity{
    private static final String tag = "DiagnoseFishSelectActivity";
    String[] mItems;
    int[] mIntItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_select);
        mItems = getResources().getStringArray(R.array.fishes);
        mIntItems = getResources().getIntArray(R.array.fishes_int);
        initList();
    }
    
    private void initList(){
        ListView listView = (ListView) findViewById(R.id.fish_select_list);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mItems));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DiagnoseFishSelectActivity.this, DiagnoseFishActivity.class);
                i.putExtra("dieaseextra", "/GetContent/"+ mIntItems[position]);
                startActivity(i);
            }
        });
    }
    
}