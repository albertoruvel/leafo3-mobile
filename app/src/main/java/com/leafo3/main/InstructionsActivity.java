package com.leafo3.main;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.leafo3.adapters.InstructionsAdapter;
import com.leafo3.R;

public class InstructionsActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private LinearLayout dots;
    private ImageButton gotit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        init();
    }

    private void init(){
        viewPager = (ViewPager)findViewById(R.id.activity_inst_view_pager);
        dots = (LinearLayout)findViewById(R.id.activity_inst_dots);
        pagerAdapter = new InstructionsAdapter(InstructionsActivity.this);
        for(int i = 0; i < this.pagerAdapter.getCount(); i ++){
            ImageButton imgButton = new ImageButton(InstructionsActivity.this);
            imgButton.setTag(i);
            imgButton.setImageResource(R.drawable.dot_selector);
            imgButton.setBackgroundResource(0);
            imgButton.setPadding(5, 5, 5, 5);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(20, 20);
            imgButton.setLayoutParams(params);
            if(i == 0)
                imgButton.setSelected(true);
            dots.addView(imgButton);
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i("PAGER", "Page selected: " + i);
                for(int k = 0 ; k < pagerAdapter.getCount(); k ++){
                    if(k != i){
                        ((ImageView)dots.findViewWithTag(k)).setSelected(false);
                    }
                }
                ((ImageView)dots.findViewWithTag(i)).setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        gotit = (ImageButton)findViewById(R.id.activity_inst_got_it);
        gotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
