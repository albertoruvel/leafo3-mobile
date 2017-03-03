package com.leafo3.main;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.leafo3.R;
import com.leafo3.model.Leaf;
import com.leafo3.task.NewLeafTask;
import com.leafo3.util.EnvironmentUtils;
import com.leafo3.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PhotoActivity extends ActionBarActivity {

    private static final int UPLOAD_COMPLETE_NOTIFICATION_ID = 324;
    private static final int PERMISSIONS_REQUEST_CODE = 815;

    public static final String PATH_PARAM = "path";
    private Uri path;
    private ImageView imageView;
    private Button button;
    private Bitmap bitmap;
    private LocationManager locationManager;
    private String locationString;
    private EditText titleEditText;
    private EditText commentEditText;
    private String compressedImage;

    private boolean gotLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        init();
        //get path
        path = Uri.parse(getIntent().getStringExtra(PATH_PARAM));
        //load image
        loadImage();
    }

    private void init(){
        this.imageView = (ImageView)findViewById(R.id.activity_photo_picture_image_view);
        this.button = (Button)findViewById(R.id.activity_photo_send_button);
        this.titleEditText = (EditText)findViewById(R.id.activity_photo_title);
        this.commentEditText = (EditText)findViewById(R.id.activity_photo_comment);

        this.button.setOnClickListener(onClickListener);
        this.locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        try{
            boolean coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if(fineLocationPermission && coarsePermission){
                this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
                this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5, locationListener);
            }else ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE);
        }catch(SecurityException ex){
            Log.e("PhotoActivity", ex.getMessage());
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(!gotLocation){
                locationString = location.getLatitude() + "," + location.getLongitude();
                Toast.makeText(PhotoActivity.this,
                        "Updated location, you can now upload your leaf picture", Toast.LENGTH_LONG).show();
                gotLocation = true;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //validate
            String title = titleEditText.getText().toString();
            String comment = commentEditText.getText().toString();
            if(title == null || title.isEmpty()){
                Toast.makeText(PhotoActivity.this, "Must specify a title", Toast.LENGTH_LONG).show();
                return;
            }else if(comment == null || comment.isEmpty()){
                Toast.makeText(PhotoActivity.this, "Any comment or description for this leaf?", Toast.LENGTH_LONG).show();
                return;
            }
            if(locationString == null || locationString.isEmpty()){
                Toast.makeText(PhotoActivity.this, "We're trying to get your location in order to upload your leaf image", Toast.LENGTH_LONG).show();
                return;
            }
            //create a new leaf
            final Leaf leaf = getLeaf(title, comment);
            try{
                NewLeafTask task = new NewLeafTask(PhotoActivity.this, leaf, getContentResolver().openInputStream(path), new NewLeafTask.NewLeafTaskHandler() {
                    @Override
                    public void onNewLeafSuccess(Leaf leaf) {
                        Toast.makeText(PhotoActivity.this, "Thanks for sharing with us :)", Toast.LENGTH_LONG).show();
                        //creates the notification
                        showUploadCompleteNotification(leaf);
                        finish();
                    }

                    @Override
                    public void onNewLeafError() {
                        Toast.makeText(PhotoActivity.this, "Could not upload your leaf info", Toast.LENGTH_LONG).show();
                    }
                });
                task.execute();
            }catch(FileNotFoundException ex){
                Log.e("PhotoActivity", ex.getMessage());
            }
        }
    };

    private void showUploadCompleteNotification(final Leaf leaf) {
        ImageUtil.getPicassoInstance(this)
                .load(Uri.parse(EnvironmentUtils.getImageUrl(EnvironmentUtils.ImageType.PROCESSED, leaf.getId())))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        //set the bitmap
                        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification.Builder builder = new Notification.Builder(PhotoActivity.this);
                        Intent intent = new Intent(PhotoActivity.this, LeafActivity.class);
                        intent.putExtra(LeafActivity.LEAF_ID, leaf.getId());

                        PendingIntent pIntent = PendingIntent.getActivity(PhotoActivity.this, 0, intent, 0);
                        //builder.setSmallIcon(android.R.drawable.ic_menu_info_details);
                        builder.setSmallIcon(R.drawable.ic_launcher);
                        builder.setContentTitle("Processed leaf");
                        builder.setContentText("Tap to watch leaf data");

                        builder.setContentIntent(pIntent);
                        builder.setLargeIcon(bitmap);
                        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
                        builder.getNotification().flags |= Notification.DEFAULT_VIBRATE;

                        manager.notify(UPLOAD_COMPLETE_NOTIFICATION_ID, builder.build());
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private Leaf getLeaf(String title, String comment){
        Leaf leaf = new Leaf();
        leaf.setImageUrl(path.toString());
        leaf.setComment(comment);
        leaf.setTitle(title);
        leaf.setLocation(locationString);
        final String email = EnvironmentUtils.getUserEmail(this);
        leaf.setUploadedBy(email);
        return leaf;
    }

    private void loadImage(){
        /**File file = new File(this.path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        //rotate bitmap
        Bitmap rotated = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);
        this.bitmap = rotated;
        this.compressedImage = this.path.replace(".jpg", "_compressed.jpg");
        //compress the image
        OutputStream stream = null;
        try{
            stream = new FileOutputStream(this.compressedImage);

            rotated.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        }catch(IOException ex){
            //could not compress image
        }
        **/
        this.imageView.setImageURI(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    try{
                        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
                        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5, locationListener);
                    }catch(SecurityException ex){

                    }
                }
                break;
        }
    }
}
