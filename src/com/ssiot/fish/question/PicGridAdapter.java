package com.ssiot.fish.question;

import android.R.mipmap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ssiot.fish.R;

import java.util.ArrayList;

public class PicGridAdapter extends BaseAdapter{
    ArrayList<Bitmap> mImgs;
    private Context mContext;

    public PicGridAdapter(Context context, ArrayList<Bitmap> imgs){
        mImgs = imgs;
        mContext = context;
    }
    
    @Override
    public int getCount() {
        return mImgs.size() + 1;//最后一个的add
    }

    @Override
    public Object getItem(int position) {
        return mImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            convertView = imageView;
        } else {
//            imageView = (ImageView) convertView.getTag();
            imageView = (ImageView) convertView;
        }
        if (position < mImgs.size()){
//            Bitmap bmp = BitmapFactory.decodeFile(mImgs.get(position));
            Bitmap bmp = mImgs.get(position);
            imageView.setImageBitmap(bmp);
        } else {
            imageView.setImageResource(R.drawable.icon_image_add);
        }
        return convertView;
    }
    
}