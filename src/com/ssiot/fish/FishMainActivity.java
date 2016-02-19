package com.ssiot.fish;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.AdapterView;

import com.ssiot.fish.question.QuestionListActivity;
import com.ssiot.remote.BrowserActivity;

import java.util.ArrayList;

public class FishMainActivity extends HeadActivity {
    private GridView gridView1;
    ArrayList<CellModel> cells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main_fish);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_cekong,"测控中心", "map_zhengwu"));//政务管理
        cells.add(new CellModel(R.drawable.cell_data,"统计报表", "map_qiye"));//企业管理
        cells.add(new CellModel(R.drawable.cell_video,"视频监控", "map_jstg"));//技术推广
        cells.add(new CellModel(R.drawable.cell_renwuzhongxin,"任务中心", "map_zhuisu"));//质量追溯
        cells.add(new CellModel(R.drawable.cell_yubingzhengduan,"鱼病诊断", "map_xinxi"));//信息服务
        cells.add(new CellModel(R.drawable.cell_zhuanjiazaixian,"专家在线", "map_jinrong",CellModel.MODE_URL).setUrl("http://www.zhdcoop.com/"));//金融服务
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生产管理", "map_webbrow"));//市场动态
        cells.add(new CellModel(R.drawable.cell_yuchangguanli,"渔场管理", "map_qiyezaixian"));//企业在线
        cells.add(new CellModel(R.drawable.cell_wuzijiaoyi,"物资交易", "map_yztb"));//渔资淘宝
        cells.add(new CellModel(R.drawable.cell_hudongjiaoliu,"互动交流", "map_hudong"));
        cells.add(new CellModel(R.drawable.cell_qiyehuizong,"企业汇总", "bdsj"));//绑定手机
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"市场动态", "map_yonghuzhinan"));//用户指南
        cells.add(new CellModel(R.drawable.cell_zhengwuguanli,"更新", "map_findupdate"));
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
                switch (model.openType) {
                    case CellModel.MODE_URL:
                        Intent intent1 = new Intent(FishMainActivity.this, BrowserActivity.class);
                        intent1.putExtra("url", model.urlString);
//                        intent1.putExtra(LeibieManageActivity.headname, str2);
                        startActivity(intent1);
                        break;

                    default:
                        break;
                }
                if ("互动交流".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, QuestionListActivity.class);
                    startActivity(intent);
                }
            }
        }
        
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.fish_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
