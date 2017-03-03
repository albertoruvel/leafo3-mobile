package com.leafo3.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.leafo3.R;

/**
 * Created by Alberto Rubalcaba on 4/19/2015.
 */
public class InstructionsAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    public InstructionsAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout)object);
    }

    public float getPageWidth(int position){
        return 1f;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.instruction_item, container, false);
        final ImageView imageView = (ImageView)itemView.findViewById(R.id.instruction_item_image_view);
        switch (position){
            case 0:
                imageView.setImageResource(R.drawable._1);
                break;
            case 1:
                imageView.setImageResource(R.drawable._2);
                break;
            case 2:
                imageView.setImageResource(R.drawable._3);
                break;
            case 3:
                imageView.setImageResource(R.drawable._4);
                break;
            case 4:
                imageView.setImageResource(R.drawable._5);
                break;
            case 5:
                imageView.setImageResource(R.drawable._6);
                break;
            case 6:
                imageView.setImageResource(R.drawable._7);
                break;
            case 7:
                imageView.setImageResource(R.drawable._8);
                break;
            case 8:
                imageView.setImageResource(R.drawable._9);
                break;
            case 9:
                imageView.setImageResource(R.drawable._10);
                break;
            case 10:
                imageView.setImageResource(R.drawable._11);
                break;
        }
        ((ViewPager)container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj){
        ((ViewPager)container).removeView((RelativeLayout)obj);
    }
}
