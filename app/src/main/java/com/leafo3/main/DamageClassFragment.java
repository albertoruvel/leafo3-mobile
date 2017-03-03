package com.leafo3.main;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leafo3.R;
import com.leafo3.model.DamageClassChart;
import com.leafo3.model.DamageClassChartModel;
import com.leafo3.task.DamageClassChartTask;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by root on 7/08/15.
 */
public class DamageClassFragment extends Fragment {

    private ColumnChartView chart;
    private ColumnChartData data;

    public DamageClassFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        final View view= inflater.inflate(R.layout.fragment_damage_class, container, false);
        //get the chart
        chart = (ColumnChartView)view.findViewById(R.id.fragment_damage_class_chart);
        chart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
             @Override
             public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

             }

             @Override
             public void onValueDeselected() {

             }

        });
        DamageClassChartTask task = new DamageClassChartTask(getActivity(), new DamageClassChartTask.DamageClassChartTaskHandler() {
            @Override
            public void onDamageClassSuccess(List<DamageClassChart> model) {
                setupChart(model);
            }

            @Override
            public void onDamageClassError() {

            }
        });
        task.execute();
        return view;
    }

    private void setupChart(List<DamageClassChart> model) {
        chart.setInteractive(true);
        //setup value
        final int columnsCount = model.size();

        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        Column column;

        for(int i = 0; i < columnsCount; i ++){
            values = new ArrayList<>();
            SubcolumnValue value = new SubcolumnValue(Float.valueOf(String.valueOf(model.get(i).getAvg())), ChartUtils.pickColor());
            value.setLabel(model.get(i).getIsoCode().toUpperCase());
            values.add(value);
            column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        data = new ColumnChartData(columns);
        data.setStacked(true);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Country ISO code");
        axisY.setName("Damage class average");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        chart.setColumnChartData(data);
    }

}
