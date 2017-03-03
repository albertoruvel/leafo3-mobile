package com.leafo3.model;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 6/08/15.
 */
public class DamageClassChartModel {
    private List<DamageClassChart> items;

    public List<DamageClassChart> getItems() {
        return items;
    }

    public void setItems(List<DamageClassChart> items) {
        this.items = items;
    }

    public DamageClassChartModel() {

    }

    public DamageClassChartModel(List<DamageClassChart> items) {

        this.items = items;
    }
}
