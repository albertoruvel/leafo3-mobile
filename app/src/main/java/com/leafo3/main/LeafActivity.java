package com.leafo3.main;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leafo3.R;
import com.leafo3.model.Leaf;
import com.leafo3.task.LeafTask;
import com.leafo3.util.EnvironmentUtils;
import com.leafo3.util.ImageUtil;

public class LeafActivity extends AppCompatActivity {

    //widgets
    private ImageView leafPhoto, processedPhoto;
    private TextView title, comment, damagePercentage, damageClass;
    private GoogleMap googleMap;

    private ScrollView scrollView;
    private ProgressBar progressBar;

    //constraints
    public static final String LEAF_ID = "leaf_id";

    //data
    private String leafId;
    private Leaf currentLeaf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaf);
        //get leaf id
        leafId = getIntent().getStringExtra(LEAF_ID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get the leaf
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        scrollView = (ScrollView)findViewById(R.id.activity_leaf_view);
        progressBar = (ProgressBar)findViewById(R.id.activity_leaf_progress);
        leafPhoto = (ImageView)findViewById(R.id.activity_leaf_photo);
        processedPhoto = (ImageView)findViewById(R.id.activity_leaf_processed_photo);
        title = (TextView)findViewById(R.id.activity_leaf_title);
        comment = (TextView)findViewById(R.id.activity_leaf_comment);
        damagePercentage = (TextView)findViewById(R.id.activity_leaf_damage_percentage);
        damageClass = (TextView)findViewById(R.id.activity_leaf_damage_class);
        googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.activity_leaf_location)).getMap();
        //create the leaf task
        LeafTask task = new LeafTask(this, leafId, new LeafTask.LeafTaskHandler() {
            @Override
            public void onLeafSuccess(Leaf leaf) {
                if(leaf != null){
                    //set values
                    bindLeaf(leaf);
                }
            }

            @Override
            public void onLeafError() {

            }
        });
        task.execute();
    }

    private void bindLeaf(Leaf leaf){
        //load both images
        ImageUtil.loadImagePicasso(leafPhoto, this, EnvironmentUtils.getImageUrl(EnvironmentUtils.ImageType.ORIGINAL, leaf.getId()));
        ImageUtil.loadImagePicasso(processedPhoto, this, EnvironmentUtils.getImageUrl(EnvironmentUtils.ImageType.PROCESSED, leaf.getId()));
        title.setText(leaf.getTitle());
        comment.setText(leaf.getComment());
        damagePercentage.setText(leaf.getPercentage() + "%");
        damageClass.setText(String.valueOf(leaf.getDamageClass()));
        //center the map with zoom 20
        final String[] coord = leaf.getLocation().split(",");
        Double lat = Double.parseDouble(coord[0]);
        Double lon = Double.parseDouble(coord[1]);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10.0f));

        //set a marker
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(leaf.getTitle())
                .snippet("Current leaf location")
                .icon(BitmapDescriptorFactory.defaultMarker()));
        //save the leaf
        this.currentLeaf = leaf;

        //show the layout
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }
}
