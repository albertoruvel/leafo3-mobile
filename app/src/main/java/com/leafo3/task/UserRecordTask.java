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
public class UserRecordTask extends BaseTask{

    private static final String USER_RECORDS_PATH = "/rest/leafs/me?email=%s&page_size=%d&page_number=%d";
    private static final String NEWEST_RECORDS = "/rest/leafs/news?page_size=%d&page_number=%d";

    private ResponseEntity<LeafRecordResponse> responseEntity;
    private UserRecordTaskType recordType;
    private int pageNumber;

    private UserRecordTaskHandler handler;

    private String url;

    public UserRecordTask(Context context, UserRecordTaskType type, UserRecordTaskHandler handler, int pageNumber){
        super(context, "");
        this.recordType = type;
        this.pageNumber = pageNumber;
        this.handler = handler;
    }

    @Override
    public void onPreExecute(){

        switch(recordType){
            case USER_RECORDS:
                final String email = EnvironmentUtils.getUserEmail(context);
                url = String.format(EnvironmentUtils.getCurrentHost() + USER_RECORDS_PATH, email,
                        EnvironmentUtils.DEFAULT_PAGE_SIZE, pageNumber);

                break;
            case NEWEST_RECORDS:
                url = String.format(EnvironmentUtils.getCurrentHost() + NEWEST_RECORDS,
                        EnvironmentUtils.DEFAULT_PAGE_SIZE, pageNumber);
                break;
        }
    }

    @Override
    public void onPostExecute(Void params){
        if(hasError)
            handler.onRecordError();
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK)
            handler.onRecordSuccess(responseEntity.getBody().getData());
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST)
            handler.onRecordError();
    }

    @Override
    public Void doInBackground(Void... param){
        //create entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity entity = new HttpEntity(headers);

        try{
            responseEntity = template.exchange(url, HttpMethod.GET, entity, LeafRecordResponse.class);
        }catch(HttpClientErrorException ex){
            hasError = true;
        }
        return null;
    }

    public enum UserRecordTaskType{
        USER_RECORDS, NEWEST_RECORDS, BY_DAMAGE_CLASS
    }

    public interface UserRecordTaskHandler{
        void onRecordSuccess(Page<Leaf> response);
        void onRecordError();
    }
}
