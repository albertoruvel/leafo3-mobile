package com.leafo3.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leafo3.R;
import com.leafo3.model.Leaf;
import com.leafo3.util.EnvironmentUtils;
import com.leafo3.util.ImageUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Alberto Rubalcaba on 4/18/2015.
 */
public class LeafRecordAdapter extends ArrayAdapter<Leaf>{
    private List<Leaf> leafs;
    LayoutInflater inflater;
    private int positionDiff;

    public LeafRecordAdapter(Context context, List<Leaf> objects) {
        super(context, R.layout.leaf_record_item, objects);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.leafs = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        LeafRecordPlaceholder ph = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.leaf_record_item, container, false);
            ph = new LeafRecordPlaceholder();
            convertView.setTag(ph);
        }else{
            ph = (LeafRecordPlaceholder)convertView.getTag();
        }
        ph.imageView = (ImageView)convertView.findViewById(R.id.leaf_record_item_image_view);
        ph.nameView = (TextView)convertView.findViewById(R.id.leaf_record_item_leaf_name);
        ph.classView = (TextView)convertView.findViewById(R.id.leaf_record_item_leaf_class);


        ImageUtil.loadImagePicasso(ph.imageView, getContext(), EnvironmentUtils.getImageUrl(EnvironmentUtils.ImageType.ORIGINAL, leafs.get(position).getId()));
        ph.nameView.setText(leafs.get(position).getTitle());
        ph.classView.setText("Class " + leafs.get(position).getDamageClass());
        //set texts
        return convertView;
    }

    private Bitmap getBitmap(String imagePath){
        //load image
        File file = new File(imagePath);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        //scales
        Bitmap result = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        return result;
    }


    static class LeafRecordPlaceholder{
        ImageView imageView;
        TextView nameView;
        TextView classView;
    }
}
