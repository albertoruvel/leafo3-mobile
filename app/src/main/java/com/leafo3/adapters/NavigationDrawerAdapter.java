package com.leafo3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leafo3.R;
import com.leafo3.model.DrawerItem;
import com.leafo3.util.EnvironmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 9/08/15.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<DrawerItem> {

    private static final String[] TITLES = { "", "Records", "How to", "About" };
    private static final int[] RESOURCES = {
            -1,
            R.drawable.ic_view_list_black_24dp,
            R.drawable.ic_info_black_24dp,
            R.drawable.ic_help_black_24dp};

    private static List<DrawerItem> items;

    static{
        items = new ArrayList<>();
        items.add(new DrawerItem(TITLES[0], RESOURCES[0]));
        items.add(new DrawerItem(TITLES[1], RESOURCES[1]));
        items.add(new DrawerItem(TITLES[2], RESOURCES[2]));
        items.add(new DrawerItem(TITLES[3], RESOURCES[3]));
    }
    public NavigationDrawerAdapter(Context context) {
        super(context, R.layout.drawer_item, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = null;
        if(position == 0){
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.drawer_header, parent, false);
            //just take the email
            ((TextView)view.findViewById(R.id.drawer_header_email)).setText(EnvironmentUtils.getUserEmail(getContext()));
        }
        else{
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.drawer_item, parent, false);
            //set title
            ((TextView)view.findViewById(R.id.navigation_drawer_title)).setText(getItem(position).getTitle());
            ((ImageView)view.findViewById(R.id.navigation_drawer_profile)).
                    setImageDrawable(getContext().getResources().
                            getDrawable(getItem(position).getImageResource()));
        }

        return view;
    }
}
