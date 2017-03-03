package com.leafo3.task;

import android.content.Context;

import com.leafo3.model.Leaf;
import com.leafo3.model.LeafResponse;
import com.leafo3.util.EnvironmentUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by root on 6/08/15.
 */
public class LeafTask extends BaseTask {

    private static final String PATH = "/rest/leafs/find?id=%s";
    private String leafId;

    private String url;
    private LeafTaskHandler handler;
    private ResponseEntity<LeafResponse> responseEntity;

    public LeafTask(Context context, String leafId, LeafTaskHandler handler) {
        super(context, "");
        this.leafId = leafId;
        this.handler = handler;
    }

    @Override
    public void onPreExecute(){
        url = String.format(EnvironmentUtils.getCurrentHost() + PATH, leafId);
    }

    @Override
    public void onPostExecute(Void param){
        if(hasError)
            handler.onLeafError();
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK)
            handler.onLeafSuccess(responseEntity.getBody().getData());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);

        try{
            responseEntity = template.exchange(url, HttpMethod.GET, entity, LeafResponse.class);
        }catch(HttpClientErrorException ex){
            hasError = true;
        }
        return null;
    }


    public interface LeafTaskHandler{
        void onLeafSuccess(Leaf leaf);
        void onLeafError();
    }
}
