package com.leafo3.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alberto Rubalcaba on 4/11/2015.
 */
public abstract class BaseTask extends AsyncTask<Void, Void, Void> {

    private static List<HttpMessageConverter<?>> converters;
    protected boolean hasError;

    static{
        converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new GsonHttpMessageConverter());
    }

    protected Context context;
    private ProgressDialog progressDialog;
    private String message;
    protected RestTemplate template;

    public BaseTask(Context context, String message){
        this.context = context;
        //init progress dialog
        this.message = message;
        this.template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Override
    protected void onPreExecute(){
        showProgressDialog();
    }

    protected void showProgressDialog(){
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage(message);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    protected void dismissProgressDialog(){
        if(this.progressDialog.isShowing())
            this.progressDialog.dismiss();
    }
}
