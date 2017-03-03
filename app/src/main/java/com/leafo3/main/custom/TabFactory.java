package com.leafo3.main.custom;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by root on 9/08/15.
 */
public class TabFactory implements TabHost.TabContentFactory {
    private Context context;

    public TabFactory(Context context){
        this.context = context;
    }

    @Override
    public View createTabContent(String tag) {
        View view = new View(context);
        view.setMinimumHeight(0);
        view.setMinimumHeight(0);;
        return view;
    }
}
