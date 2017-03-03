package com.leafo3.model;

import java.util.List;

/**
 * Created by root on 5/08/15.
 */
public class LeafRecordResponse {

    private Page<Leaf> data;
    private boolean success;
    private String message;

    public LeafRecordResponse() {
    }

    public Page<Leaf> getData() {
        return data;
    }

    public void setData(Page<Leaf> leafs) {
        this.data = leafs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
