package com.leafo3.util;

import android.util.Log;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Alberto Rubalcaba on 4/19/2015.
 */
public class CustomRestTemplate extends RestTemplate {

    //timout to 35 seconds
    private static final int TIMEOUT = 120 * 1000;

    public CustomRestTemplate(){
        if (getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
            Log.d("HTTP", "HttpUrlConnection is used");
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(TIMEOUT);
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setReadTimeout(TIMEOUT);
        } else if (getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
            Log.d("HTTP", "HttpClient is used");
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setReadTimeout(TIMEOUT);
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(TIMEOUT);
        }
    }
}
