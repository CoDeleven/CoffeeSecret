package com.dhy.coffeesecret.ui.device.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.interfaces.ToolbarOperation;
import com.dhy.coffeesecret.ui.device.views.ScrollViewContainer;
import com.github.mikephil.charting.charts.BarLineChartBase;


public class LineDetailFragment extends Fragment implements BarLineChartBase.InterceptorView {
    private ScrollViewContainer scrollContainer;
    private ToolbarOperation toolbarOperation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_line_detail, container, false);


        // 为图表添加拦截器
        scrollContainer = (ScrollViewContainer) view.findViewById(R.id.sl_1);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.linechart_device_activity).findViewById(R.id.id_report_viewpager);
        FragmentPagerAdapter adapter = (FragmentPagerAdapter) viewPager.getAdapter();
        LineChartPart chart = (LineChartPart) adapter.getItem(0);
        LineChartPart chart2 = (LineChartPart) adapter.getItem(1);
        chart.setInterceptorView(this);
        chart2.setInterceptorView(this);
        return view;
    }


    @Override
    public void intercept(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            scrollContainer.requestDisallowInterceptTouchEvent(false);
            scrollContainer.setCanPullUp(true);
        } else {
            scrollContainer.requestDisallowInterceptTouchEvent(true);
            scrollContainer.setCanPullUp(false);
        }

    }

    public void setToolbarOperation(ToolbarOperation operation) {
        this.toolbarOperation = operation;
    }
}
