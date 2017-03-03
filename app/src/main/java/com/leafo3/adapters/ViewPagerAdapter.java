package com.leafo3.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.leafo3.main.DamageClassFragment;
import com.leafo3.main.PieChartFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/08/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static List<Fragment> FRAGMENTS;
    static{
        FRAGMENTS = new ArrayList<>();
        FRAGMENTS.add(new DamageClassFragment());
        FRAGMENTS.add(new PieChartFragment());
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FRAGMENTS.get(position);
    }

    @Override
    public int getCount() {
        return FRAGMENTS.size();
    }

    @Override
    public String getPageTitle(int position){
        String title = "";
        switch(position){
            case 0:
                title = "Damage class";
                break;
            case 1:
                title = "Leafs count";
                break;
            case 2:
                break;
        }

        return title;
    }
}
