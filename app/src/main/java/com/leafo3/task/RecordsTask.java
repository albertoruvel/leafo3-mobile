package com.leafo3.task;

import android.content.Context;

import com.leafo3.model.Leaf;
import com.leafo3.model.LeafRecordResponse;
import com.leafo3.model.Page;
import com.leafo3.util.EnvironmentUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by root on 5/08/15.
 */
public class RecordsTask extends BaseTask {

    private static final String PATH = "/rest/leafs/records?email=%s&record=%d&page_number=%d";

    private ResponseEntity<LeafRecordResponse> responseEntity;
    private RecordType type;
    private RecordsTaskHandler handler;

    private String url;
    private int pageNumber;

    public RecordsTask(Context context, RecordType type, RecordsTaskHandler handler, int pageNumber) {
        super(context, "");
        this.type = type;
        this.handler = handler;
        this.pageNumber= pageNumber;
    }

    @Override
    public void onPreExecute(){
        int recordType = -1;
        switch(type){
            case USER:
                recordType = 0;
                break;
            case NEWEST:
                recordType = 1;
                break;
            case DAMAGED:
                recordType = 2;
                break;
        }
        url = String.format(EnvironmentUtils.getCurrentHost() + PATH,
                EnvironmentUtils.getUserEmail(context), recordType, pageNumber);
    }

    @Override
    public void onPostExecute(Void param){
        if(hasError)
            handler.onRecordsError();
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK)
            handler.onRecordsSuccess(responseEntity.getBody().getData());
    }


    @Override
    protected Void doInBackground(Void... voids) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
        try{
            responseEntity =template.exchange(url, HttpMethod.GET, entity, LeafRecordResponse.class);
        }catch(HttpClientErrorException ex){
            hasError = true;
        }
        return null;
    }

    public interface RecordsTaskHandler{
        void onRecordsSuccess(Page<Leaf> leafs);
        void onRecordsError();
    }

    public enum RecordType{
        USER, NEWEST, DAMAGED
    }
}
