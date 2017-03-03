package com.leafo3.main;

import com.leafo3.R;
import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leafo3.adapters.LeafRecordAdapter;
import com.leafo3.adapters.LeafRecordRecyclerAdapter;
import com.leafo3.main.custom.GenericScrollListener;
import com.leafo3.model.Leaf;
import com.leafo3.model.Page;
import com.leafo3.task.RecordsTask;
import com.leafo3.util.EnvironmentUtils;
import com.leafo3.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LeafRecordActivity extends ActionBarActivity {

    //constraints
    private static final int GALLERY_CODE = 200;
    private static final int CAMERA_CODE = 100;

    private static final int PERMISSISONS_REQUEST_CODE = 431;

    //controls
    //private ListView listView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar mapProgress, listProgress;
    private GoogleMap googleMap;
    private Spinner spinner;
    private SwipeRefreshLayout refreshLayout;
    private View noMoreItemsView;
    private View loadingView;
    private LinearLayout buttonLayout;
    private Button newLeaf;

    //data
    private static final String[] spinnerItems = { "My leafs", "Newest leafs", "Order by damage class" };
    private RecordsTask.RecordType recordType = RecordsTask.RecordType.USER;
    private static int pageNumber = 1;
    private GenericScrollListener<RecordsTask> scrollListener;
    private LocationManager locationManager;
    private boolean located;
    private String currentPhotoPath;

    //adapter
    private LeafRecordRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaf_record);
        init();
    }

    private void init(){
        this.recyclerView = (RecyclerView)findViewById(R.id.activity_record_recycler_view);
        //set map
        this.googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.activity_leaf_record_map))
                .getMap();
        this.mapProgress = (ProgressBar)findViewById(R.id.activity_leaf_record_map_progress);
        this.listProgress = (ProgressBar)findViewById(R.id.activity_leaf_record_list_progress);
        this.spinner = (Spinner)findViewById(R.id.activity_leaf_record_spinner);
        this.refreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_leaf_record_refresh_layout);
        this.refreshLayout.setOnRefreshListener(refreshListener);
        this.refreshLayout.setColorSchemeColors(R.color.green, R.color.green_darker, R.color.green_darkest);
        this.noMoreItemsView = getLayoutInflater().inflate(R.layout.no_more_items_layout, null, false);
        this.loadingView = getLayoutInflater().inflate(R.layout.loading_next_page_layout, null, false);


        this.buttonLayout = (LinearLayout)findViewById(R.id.activity_leaf_record_button_layout);
        this.newLeaf= (Button)findViewById(R.id.activity_leaf_record_create_leaf);

        this.newLeaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = { "Gallery", "Camera" };
                AlertDialog dialog = new AlertDialog.Builder(LeafRecordActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, GALLERY_CODE);
                                }else if(i == 1){
                                    //camera
                                    //start camera intent
                                    final String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/leafo3/";
                                    currentPhotoPath = directory + "leaf_" + new SimpleDateFormat("MM-dd-yyyy HH-mm").format(new Date()) + ".jpg";
                                    File file = new File(currentPhotoPath);
                                    try{
                                        file.createNewFile();
                                    }catch(IOException ex){
                                        //error
                                    }

                                    Uri uri = Uri.fromFile(file);
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.setType("image/*");
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                    startActivityForResult(intent, CAMERA_CODE);
                                }
                            }
                        })
                        .setCancelable(true)
                        .create();
                dialog.show();
            }
        });
        //populate spinner
        initSpinner();
        RecordsTask task = new RecordsTask(this, recordType, recordsHandler, pageNumber);
        task.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CODE && resultCode == RESULT_OK){
            //send path to PhotoActivity
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.PATH_PARAM, currentPhotoPath);

            startActivity(intent);
        }else if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            //get uri
            Uri uri = data.getData();
            final String imagePath = getPath(uri);
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.PATH_PARAM, imagePath);

            startActivity(intent);
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //center the map
            if(! located){
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                located = true;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshLayout.setRefreshing(true);
            pageNumber = 1;
            //creates a new task
            RecordsTask task = new RecordsTask(LeafRecordActivity.this, recordType, recordsHandler, pageNumber);
            task.execute();
        }
    };

    private void initSpinner(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(arrayAdapter);
        //create an onclick listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                refresh(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void refresh(int position){
        // your code here
        pageNumber = 1;
        RecordsTask task = null;
        switch(position){
            case 0:
                //get user leaves
                task = new RecordsTask(LeafRecordActivity.this, RecordsTask.RecordType.USER, recordsHandler, pageNumber);
                break;
            case 1:
                task = new RecordsTask(LeafRecordActivity.this, RecordsTask.RecordType.NEWEST, recordsHandler, pageNumber);
                break;
            case 2:
                task = new RecordsTask(LeafRecordActivity.this, RecordsTask.RecordType.DAMAGED, recordsHandler, pageNumber);
                break;
        }
        task.execute();
        //show the list progress bar
        listProgress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private final RecordsTask.RecordsTaskHandler recordsHandler = new RecordsTask.RecordsTaskHandler() {
        @Override
        public void onRecordsSuccess(Page<Leaf> leafs) {
            //first handler
            //if refreshlayout is refreshing
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            if(leafs.getPageItems().isEmpty()){
                //show the button layout
                listProgress.setVisibility(View.GONE);
                listProgress.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.VISIBLE);
            }else{
                recyclerView.setVisibility(View.VISIBLE);
                listProgress.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);
                adapter = new LeafRecordRecyclerAdapter(leafs.getPageItems(), LeafRecordActivity.this);
                recyclerView.setHasFixedSize(false);
                layoutManager = new LinearLayoutManager(LeafRecordActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState){}


                    @Override
                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        int topRowVerticalPosition =
                                (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                        refreshLayout.setEnabled(topRowVerticalPosition >= 0);
                    }
                });

                recyclerView.setVisibility(View.VISIBLE);
                /**recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //get the selected leaf
                        final Leaf leaf = (Leaf)listView.getAdapter().getItem(i);
                        //get the id
                        final String id = leaf.getId();
                        Intent intent = new Intent(LeafRecordActivity.this, LeafActivity.class);
                        intent.putExtra(LeafActivity.LEAF_ID, id);
                        startActivity(intent);
                    }
                });**/
                listProgress.setVisibility(View.GONE);
                //create the scroll listener
                pageNumber = leafs.getPageNumber();
                pageNumber ++;

                /**scrollListener = new GenericScrollListener<>(LeafRecordActivity.this,
                        refreshLayout, listView, new RecordsTask(LeafRecordActivity.this, recordType,
                        nextPageHandler, pageNumber), scrollListenerLooping);
                scrollListener.setPageNumber(pageNumber);
                scrollListener.setPagesAvailable(leafs.getPagesAvailable());**/
                //create marks for every leaf on the map and center the map to the first leaf coordinates
                createMapMarkers(leafs, true);

            }
        }

        @Override
        public void onRecordsError() {

        }
    };

    private void createMapMarkers(Page<Leaf> leafs, boolean deleteCurrentOnes){
        if(deleteCurrentOnes){
            if(googleMap != null){
                googleMap.clear();
                for(final Leaf leaf : leafs.getPageItems()){
                    String[] coord = leaf.getLocation().split(",");
                    final double lat = Double.parseDouble(coord[0]);
                    final double lon = Double.parseDouble(coord[1]);
                    //create a marker
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon)).title(leaf.getTitle())
                            .snippet("Class " + leaf.getDamageClass())
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                }
                //center to the first position
                String[] array = leafs.getPageItems().get(0).getLocation().split(",");

                LatLng pos = new LatLng(Double.parseDouble(array[0]), Double.parseDouble(array[1]));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            }
        }else{
            //just add them
            for(final Leaf leaf : leafs.getPageItems()){
                String[] coord = leaf.getLocation().split(",");
                final double lat = Double.parseDouble(coord[0]);
                final double lon = Double.parseDouble(coord[1]);
                //create a marker
                ImageUtil.getPicassoInstance(this)
                        .load(Uri.parse(EnvironmentUtils.getImageUrl(EnvironmentUtils.ImageType.PROCESSED, leaf.getId())))
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon)).title(leaf.getTitle())
                                        .snippet(leaf.getComment() + "\nClass " + leaf.getDamageClass())
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                //create a default drawable
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon)).title(leaf.getTitle())
                                        .snippet(leaf.getComment() + "\nClass " + leaf.getDamageClass())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.leaf_icon)));
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
        }
    }

    private final RecordsTask.RecordsTaskHandler nextPageHandler = new RecordsTask.RecordsTaskHandler() {
        @Override
        public void onRecordsSuccess(Page<Leaf> leafs) {
            if(! leafs.getPageItems().isEmpty()){
                //adapter.addAll(leafs.getPageItems());
                adapter.notifyDataSetChanged();
                scrollListener.setPagesAvailable(leafs.getPagesAvailable());
                scrollListener.setPageNumber(leafs.getPageNumber());
                createMapMarkers(leafs, false);
                if(leafs.getPagesAvailable() == leafs.getPageNumber()){
                    //no more items
                    //remove all footers
                    //listView.removeFooterView(loadingView);
                    //listView.addFooterView(noMoreItemsView);
                }else return;//listView.removeFooterView(loadingView);
            }
        }

        @Override
        public void onRecordsError() {

        }
    };

    private final GenericScrollListener.ScrollListenerLooping scrollListenerLooping = new GenericScrollListener.ScrollListenerLooping() {
        @Override
        public void onNeedTaskInstance(int pNumber) {
            RecordsTask task = new RecordsTask(LeafRecordActivity.this, recordType, nextPageHandler, pNumber);
            //listView.addFooterView(loadingView);
            scrollListener.setNewTask(task);
        }
    };

    private void getLocationManager(){
        try{
            this.locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        }catch(SecurityException ex){

        }
    }

    private void checkPermissions(){
        Intent intent = null;
        boolean coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(coarsePermission && fineLocationPermission){
            getLocationManager();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSISONS_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch(requestCode){
            case PERMISSISONS_REQUEST_CODE:
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    getLocationManager();
                }
                break;
        }
    }

}
