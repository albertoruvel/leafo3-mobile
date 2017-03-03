package com.leafo3.model;

/**
 * Created by root on 6/08/15.
 */
public class LeafResponse {
    private Leaf data;
    private boolean success;

    public LeafResponse() {
    }

    public Leaf getData() {
        return data;
    }

    public void setData(Leaf data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
