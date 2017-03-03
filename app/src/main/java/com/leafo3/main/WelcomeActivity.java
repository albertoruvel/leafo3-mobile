package com.leafo3.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leafo3.R;
import com.leafo3.util.EnvironmentUtils;

public class WelcomeActivity extends ActionBarActivity {

    private EditText emailEditText, country;
    private Button readyButton;

    private static final int INTERNET_PERMISSION_CODE = 918;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        checkInternetPermission();
        if(EnvironmentUtils.getUserEmail(WelcomeActivity.this) != null && ! EnvironmentUtils.getUserEmail(WelcomeActivity.this).isEmpty()){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
        init();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(EnvironmentUtils.getUserEmail(WelcomeActivity.this) != null && ! EnvironmentUtils.getUserEmail(WelcomeActivity.this).isEmpty())
            finish();
    }

    private void init(){
        this.emailEditText = (EditText)findViewById(R.id.activity_welcome_email_edit);
        this.readyButton = (Button)findViewById(R.id.activity_welcome_ready_button);
        this.country = (EditText)findViewById(R.id.activity_welcome_country);
        //set listener
        this.readyButton.setOnClickListener(readyButtonListener);
        //get the country
        String countryIso = EnvironmentUtils.getUserCountry(this);
        //set the country text
        country.setText(countryIso);
    }



    private View.OnClickListener readyButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //save the email
            String email = emailEditText.getText().toString();
            if(email == null || email.isEmpty()){
                //shows a toast
                Toast.makeText(WelcomeActivity.this, "No email provided", Toast.LENGTH_LONG).show();
            }else{
                //validate email
                if(EnvironmentUtils.validateEmail(email)){
                    //saves the email
                    EnvironmentUtils.saveUserEmail(WelcomeActivity.this, email);
                    //start main activity
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    emailEditText.setError("Must enter a valid email");
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch(requestCode){
            case INTERNET_PERMISSION_CODE:
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(this, "Need internet connection to use this application", Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
                break;
        }
    }

    private void checkInternetPermission(){
        boolean internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        if(! internetPermission){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, INTERNET_PERMISSION_CODE);
        }
    }


}
