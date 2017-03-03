package com.leafo3.task;

import android.content.Context;

import com.leafo3.model.DamageClassChart;
import com.leafo3.model.DamageClassChartModel;
import com.leafo3.util.EnvironmentUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

/**
 * Created by root on 6/08/15.
 */
public class DamageClassChartTask extends BaseTask {
    private static final String PATH = "/rest/leafs/class_chart";

    private ResponseEntity<DamageClassChartModel> responseEntity;
    private String url;

    private DamageClassChartTaskHandler handler;
    public DamageClassChartTask(Context context, DamageClassChartTaskHandler handler) {
        super(context, "");
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);

        try{
            responseEntity = template.exchange(url, HttpMethod.GET, entity, DamageClassChartModel.class);
        }catch(HttpClientErrorException ex){
            hasError = true;
        }
        return null;
    }

    @Override
    public void onPreExecute(){
        this.url = EnvironmentUtils.getCurrentHost() + PATH;
    }

    @Override
    public void onPostExecute(Void param){
        if(hasError)
            handler.onDamageClassError();
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK)
            handler.onDamageClassSuccess(responseEntity.getBody().getItems());
    }

    public interface DamageClassChartTaskHandler{
        void onDamageClassSuccess(List<DamageClassChart> model);
        void onDamageClassError();
    }
}
