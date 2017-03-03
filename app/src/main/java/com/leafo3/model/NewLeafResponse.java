package com.leafo3.model;

/**
 * Created by Alberto Rubalcaba on 4/18/2015.
 */
public class NewLeafResponse {
    private boolean success;
    private Leaf data;

    public NewLeafResponse(boolean success, Leaf leaf) {
        this.success = success;
        this.data = leaf;
    }

    public NewLeafResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Leaf getData() {
        return data;
    }

    public void setData(Leaf leaf){
        this.data = leaf;
    }
}
