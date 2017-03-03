package com.leafo3.main;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.leafo3.adapters.NavigationDrawerAdapter;
import com.leafo3.adapters.ViewPagerAdapter;
import com.leafo3.R;
import com.leafo3.main.custom.TabFactory;
import com.leafo3.util.EnvironmentUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity {
    private static final int CAMERA_CODE = 100;
    private static final int GALLERY_CODE = 200;

    private String currentPhotoPath;

    //nav drawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerRelativeLayout;

    //toolbar
    private Toolbar toolbar;

    //view pager
    private ViewPager viewPager;

    private TabHost tabHost;

    private File currentPhotoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private static void addTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec spec){
        spec.setContent(new TabFactory(activity));
        tabHost.addTab(spec);
    }

    private TabHost.OnTabChangeListener onTabChangedListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            int position = tabHost.getCurrentTab();
            viewPager.setCurrentItem(position);
        }
    };


    private void init(){
        File directory= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/LeafO3/");
        directory.mkdir();
        currentPhotoFile = new File(directory, System.currentTimeMillis() + ",jpg");
        try{
            currentPhotoFile.createNewFile();
        }catch(Exception ex){

        }
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        //tabhost
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();

        MainActivity.addTab(this, tabHost, tabHost.newTabSpec("DAMAGE_CLASS").setIndicator("DAMAGE"));
        MainActivity.addTab(this, tabHost, tabHost.newTabSpec("COUNT_TAG").setIndicator("LEAFS COUNT"));

        tabHost.setOnTabChangedListener(onTabChangedListener);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        //create the adapter
        ViewPagerAdapter vPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(vPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                tabHost.setCurrentTab(position);
            }
        });


        //setup action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("LeafO3");
        //getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //setup drawer layout
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.navigation_drawer);
        drawerRelativeLayout = (RelativeLayout)findViewById(R.id.navigation_drawer_content);
        //TODO: create adapter
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                handlePositionClick(i);
            }
        });
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            @Override
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //setup analytics frame

    }

    private void handlePositionClick(int i){
        Intent intent = null;
        switch(i){
            case 0:
                //profile
                break;
            case 1:
                intent = new Intent(this, LeafRecordActivity.class);
                startActivity(intent);
                //records
                break ;
            case 2:
                intent = new Intent(this, InstructionsActivity.class);
                startActivity(intent);
                //instructions
                break;
            case 3:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                //about
                break;
        }
    }

    private void showCollaborateDialog(){
        final String[] items = { "Gallery", "Camera" };

        //hide drawer
        drawerLayout.closeDrawers();
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent, GALLERY_CODE);
                        }else if(i == 1){
                            //camera
                            //start camera intent
                            final String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/leafo3/";
                            currentPhotoPath = directory + "leaf_" + new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(new Date()) + ".jpg";
                            File file = new File(currentPhotoPath);
                            try{
                                file.createNewFile();
                            }catch(IOException ex){
                                //error
                            }

                            Uri uri = Uri.fromFile(file);
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, CAMERA_CODE);
                            }else{
                                Toast.makeText(MainActivity.this, "Could not open camera", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    private View.OnClickListener cameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CODE && resultCode == RESULT_OK){
            //send path to PhotoActivity
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.PATH_PARAM, currentPhotoPath);

            startActivity(intent);
        }else if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            //get uri
            Uri uri = data.getData();
            currentPhotoFile = new File(uri.getPath());
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.PATH_PARAM, uri.toString());

            startActivity(intent);
        }/**else if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK){
            Uri selected = data.getData();
            currentPhotoPath = getPath(selected);
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.PATH_PARAM, currentPhotoPath);

            startActivity(intent);
        }**/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.collaborateMenuItem:
                showCollaborateDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
