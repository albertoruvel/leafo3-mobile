package com.leafo3.task;

import android.content.Context;
import com.leafo3.model.Leaf;
import com.leafo3.model.NewLeafResponse;
import com.leafo3.util.CustomRestTemplate;
import com.leafo3.util.EnvironmentUtils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Alberto Rubalcaba on 4/12/2015.
 */
public class NewLeafTask extends BaseTask {

    private static final String PATH = "/rest/leafs/create";

    private Leaf leaf;
    private ResponseEntity<NewLeafResponse> responseEntity;
    private NewLeafTaskHandler handler;
    private InputStream stream;

    public NewLeafTask(Context context, Leaf leaf, InputStream is, NewLeafTaskHandler handler){
        super(context, "Uploading your leaf");
        this.leaf = leaf;
        this.handler = handler;
        this.stream = is;
    }


    @Override
    protected Void doInBackground(Void... params){
        final String url = EnvironmentUtils.getCurrentHost() + PATH;
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.setCharset(Charset.forName("UTF-8"));

        RestTemplate template = new CustomRestTemplate();
        template.getMessageConverters().add( formHttpMessageConverter );
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("location", leaf.getLocation());
        try{
            byte[] bytes = getBytes();
            map.add("file", new ByteArrayResource(bytes){
                @Override
                public String getFilename() {
                    return "file.jpg";
                }
            });
        }catch(IOException ex){

        }

        map.add("email", leaf.getUploadedBy());
        map.add("title", leaf.getTitle());
        map.add("comment", leaf.getComment());
        final String isoCode = EnvironmentUtils.getUserCountry(context);
        map.add("iso", isoCode);

        HttpHeaders imageHeaders = new HttpHeaders();
        imageHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        try{
            HttpEntity<MultiValueMap<String, Object>> imageEntity = new HttpEntity<MultiValueMap<String, Object>>(map, imageHeaders);
            responseEntity = template.exchange(url, HttpMethod.POST, imageEntity, NewLeafResponse.class);
        }catch (Exception ex){
            hasError = true;
        }
        return null;
    }

    private byte[] getBytes() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        byte[] bytes = new byte[1024];
        while((read = stream.read(bytes, 0, bytes.length)) != -1)
            out.write(bytes, 0, read);

        out.flush();
        return out.toByteArray();

    }

    @Override
    protected void onPostExecute(Void param){
        if(hasError)
            //error
            handler.onNewLeafError();
        else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK){
            //ok
            handler.onNewLeafSuccess(responseEntity.getBody().getData());
        }else if(responseEntity != null && responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST){
            handler.onNewLeafError();
        }
    }


    public interface NewLeafTaskHandler{
        void onNewLeafSuccess(Leaf leaf);
        void onNewLeafError();
    }
}
