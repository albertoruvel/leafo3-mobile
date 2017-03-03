package com.leafo3.task;

import android.content.Context;

import com.leafo3.model.DamageClassChart;
import com.leafo3.model.DamageClassChartModel;
import com.leafo3.model.PieChartModel;
import com.leafo3.util.EnvironmentUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 7/08/15.
 */
public class PieChartTask extends BaseTask {

    private static final String PATH = "/rest/leafs/pie_chart";

    private ResponseEntity<PieChartModel> responseEntity;
    private PieChartTaskHandler handler;
    private String url;

    public PieChartTask(Context context, PieChartTaskHandler handler) {
        super(context, "");
        this.handler = handler;
    }

    @Override
    public void onPostExecute(Void param){
        if(hasError)
            handler.onPieChartError();
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK)
            handler.onPieChartSuccess(responseEntity.getBody().getItems());
    }

    @Override
    public void onPreExecute(){
        url = EnvironmentUtils.getCurrentHost() + PATH;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.synchronizedList(mediaTypes));
        HttpEntity entity = new HttpEntity(headers);
        try{
            responseEntity = template.exchange(url, HttpMethod.GET, entity, PieChartModel.class);
        }catch(HttpClientErrorException ex){
            hasError = true;
        }
        return null;
    }

    public interface PieChartTaskHandler{
        void onPieChartSuccess(List<DamageClassChart> data);
        void onPieChartError();
    }
}
