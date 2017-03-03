package com.leafo3.main;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leafo3.R;

public class AboutActivity extends ActionBarActivity {

    private TextView albertoruvelText;

    private static final String URL = "http://albertoruvel.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        albertoruvelText = (TextView)findViewById(R.id.aboutActivityWeb);

        albertoruvelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open my url
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(URL));
                startActivity(intent);
            }
        });
    }


}
