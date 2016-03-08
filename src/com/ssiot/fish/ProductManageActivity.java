package com.ssiot.fish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssiot.fish.product.FishEditActivity;
import com.ssiot.fish.product.ProductEditActivity;
import com.ssiot.fish.product.ProductListActivity;
import com.ssiot.remote.BrowserActivity;

import java.util.ArrayList;

public class ProductManageActivity extends HeadActivity{
    private static final String tag = "ProductManageActivity";
    private GridView gridView1;
    ArrayList<CellModel> cells;
    private SharedPreferences mPref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main_fish);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_cekong,"入库", "map_in"));
        cells.add(new CellModel(R.drawable.cell_cekong,"出库", "map_out"));
        cells.add(new CellModel(R.drawable.cell_cekong,"出入库列表", "map_productlist"));
        cells.add(new CellModel(R.drawable.cell_cekong,"鱼药", "map_fishdrug"));
        cells.add(new CellModel(R.drawable.cell_cekong,"鱼药列表", "map_fishdruglist"));
        cells.add(new CellModel(R.drawable.cell_cekong,"饲料", "map_fishdruglist"));
        cells.add(new CellModel(R.drawable.cell_cekong,"饲料列表", "map_fishdruglist"));
        cells.add(new CellModel(R.drawable.cell_cekong,"鱼苗", "map_fishdruglist"));
        cells.add(new CellModel(R.drawable.cell_cekong,"鱼苗列表", "map_fishdruglist"));
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
                if ("入库".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ProductEditActivity.class);
                    i.putExtra("isproductin", true);
                    startActivity(i);
                } else if ("出库".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, ProductEditActivity.class);
                    intent.putExtra("isproductin", false);
                    startActivity(intent);
                } else if ("出入库列表".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
                    intent.putExtra("whichtable", "FishProduction");
                    startActivity(intent);
                } else if ("鱼药".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, FishEditActivity.class);
                    intent.putExtra("edittable", "FishDrug");
                    startActivity(intent);
                } else if ("鱼药列表".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
                    intent.putExtra("whichtable", "FishDrug");
                    startActivity(intent);
                } else if ("饲料".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, FishEditActivity.class);
                    intent.putExtra("edittable", "FishFeed");
                    startActivity(intent);
                } else if ("饲料列表".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
                    intent.putExtra("whichtable", "FishFeed");
                    startActivity(intent);
                } else if ("鱼苗".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, FishEditActivity.class);
                    intent.putExtra("edittable", "FishSmall");
                    startActivity(intent);
                } else if ("鱼苗列表".equals(model.itemText)){
                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
                    intent.putExtra("whichtable", "FishSmall");
                    startActivity(intent);
                }
            }
        }
    };
}