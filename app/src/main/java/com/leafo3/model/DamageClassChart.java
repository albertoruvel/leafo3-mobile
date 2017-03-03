package com.leafo3.model;

import java.util.List;

/**
 * Created by root on 7/08/15.
 */
public class DamageClassChart {
    private int count;
    private String isoCode;
    private double avg;

    public DamageClassChart() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }
}
