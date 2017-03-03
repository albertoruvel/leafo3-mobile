package com.leafo3.main;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leafo3.R;
import com.leafo3.model.DamageClassChart;
import com.leafo3.model.DamageClassChartModel;
import com.leafo3.task.PieChartTask;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by root on 7/08/15.
 */
public class PieChartFragment extends Fragment {
    private PieChartView chart;
    private PieChartData data;

    public PieChartFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        final View view = inflater.inflate(R.layout.fragment_percentage_chart, container, false);
        chart = (PieChartView)view.findViewById(R.id.fragment_percentage_chart);

        //start the task
        PieChartTask task = new PieChartTask(getActivity(), new PieChartTask.PieChartTaskHandler() {
            @Override
            public void onPieChartSuccess(List<DamageClassChart> data) {
                setupChart(data);
            }

            @Override
            public void onPieChartError() {

            }
        });
        task.execute();
        return view;
    }

    public void setupChart(List<DamageClassChart> model){
        final int numValues = model.size();
        List<SliceValue> values = new ArrayList<>();
        SliceValue value = null;
        for(int i = 0; i < numValues; i ++){
            value = new SliceValue(model.get(i).getCount(), ChartUtils.pickColor());
            value.setLabel(model.get(i).getIsoCode());
            values.add(value);
        }

        data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasLabelsOutside(true);
        data.setSlicesSpacing(5);
        chart.setPieChartData(data);

    }
}
