package com.leafo3.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leafo3.main.LeafActivity;
import com.leafo3.R;
import com.leafo3.model.Leaf;
import com.leafo3.util.EnvironmentUtils;
import com.leafo3.util.ImageUtil;

import java.util.List;

/**
 * Created by root on 9/08/15.
 */
public class LeafRecordRecyclerAdapter extends
        RecyclerView.Adapter<LeafRecordRecyclerAdapter.LeafRecordViewHolder>{

    private List<Leaf> leafs;
    private Context cxt;


    public LeafRecordRecyclerAdapter(List<Leaf> leafs, Context cxt){
        this.leafs = leafs;
        this.cxt = cxt;
    }

    @Override
    public LeafRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaf_record_card_view, parent, false);

        return new LeafRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LeafRecordViewHolder holder, final int position) {
        final Leaf leaf = leafs.get(position);
        String url = EnvironmentUtils.getImageUrl(EnvironmentUtils.ImageType.ORIGINAL, leaf.getId());
        ImageUtil.loadImagePicasso(holder.leafView, holder.leafView.getContext(), url);
        holder.title.setText(leaf.getTitle());
        holder.comment.setText(leaf.getComment());
        holder.damageClass.setText("Class: " + leaf.getDamageClass());
        holder.damagePercentage.setText("Damage: " + leaf.getPercentage() + "%");
        holder.isoCode.setText("ISO Code: " + leaf.getIsoCode().toUpperCase());
        //set the listener
        holder.leafView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.leafView.getContext(), LeafActivity.class);
                intent.putExtra(LeafActivity.LEAF_ID, leafs.get(position).getId());
                ((Activity)cxt).startActivity(intent);
            }
        });

        String firstColor = "B2B200";
        String secColor = "996300";
        String thirdColor = "B20000";
        String damageFormat = "Damage: <font color=#%s>%s</font>";
        String classFormat = "Class: <font color=#%s>%d</font>";
        String isoFormat = "ISO: <font color=#%s>%s</font>";
        //set colors
        if(leafs.get(position).getDamageClass() > 0 && leafs.get(position).getDamageClass() <= 3){
            holder.damageClass.setText(Html.fromHtml(String.format(classFormat, firstColor, leaf.getDamageClass())));
            holder.damagePercentage.setText(Html.fromHtml(String.format(damageFormat, firstColor, leaf.getPercentage() + "%")));
        }else if(leafs.get(position).getDamageClass() > 3 && leafs.get(position).getDamageClass() <= 5){
            //set text color to orange
            holder.damageClass.setText(Html.fromHtml(String.format(classFormat, secColor, leaf.getDamageClass())));
            holder.damagePercentage.setText(Html.fromHtml(String.format(damageFormat, secColor, leaf.getPercentage() + "%")));
        }else{
            //set text color to red
            holder.damageClass.setText(Html.fromHtml(String.format(classFormat, thirdColor, leaf.getDamageClass())));
            holder.damagePercentage.setText(Html.fromHtml(String.format(damageFormat, thirdColor, leaf.getPercentage() + "%")));
        }
    }

    @Override
    public int getItemCount() {
        return leafs.size();
    }

    public static class LeafRecordViewHolder extends RecyclerView.ViewHolder{

        public ImageView leafView;
        public TextView title, comment, damageClass, damagePercentage, isoCode;

        public LeafRecordViewHolder(View view){
            super(view);
            leafView = (ImageView)view.findViewById(R.id.leaf_record_card_view_image);
            title = (TextView)view.findViewById(R.id.leaf_record_card_view_title);
            comment = (TextView)view.findViewById(R.id.leaf_record_card_view_comment);
            damageClass = (TextView)view.findViewById(R.id.leaf_record_card_view_damage);
            damagePercentage = (TextView)view.findViewById(R.id.leaf_record_card_view_percentage);
            isoCode = (TextView)view.findViewById(R.id.leaf_record_card_view_iso);
        }
    }
}
