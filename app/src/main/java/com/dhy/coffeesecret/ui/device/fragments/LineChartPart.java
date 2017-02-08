package com.dhy.coffeesecret.ui.device.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.github.mikephil.charting.charts.BarLineChartBase;

public class LineChartPart extends Fragment {
    public static final int NORMAL = 1, MODE1 = 2, MODE2 = 3;
    private RelativeLayout rl;
    private LinearLayout ll;
    private BaseChart4Coffee chart;
    private BarLineChartBase.InterceptorView interceptorView;
    private int curMode = 1;

    public LineChartPart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.conta_report_chart_info, container, false);
        chart = (BaseChart4Coffee) view.findViewById(R.id.id_line_chart);
        rl = (RelativeLayout) view.findViewById(R.id.id_report_lineinfo_part1);
        ll = (LinearLayout) view.findViewById(R.id.id_report_lineinfo_part2);
        rl.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);
        // chart.addLine(BaseChart4Coffee.BEANLINE);
        chart.setInterceptorView(interceptorView);
        init();
        return view;
    }

    public void setMode(int mode) {
        this.curMode = mode;
    }

    private void init() {
        switch (curMode) {
            case NORMAL:
                break;
            case MODE1:
                rl.setVisibility(View.VISIBLE);
                chart.addLine(BaseChart4Coffee.BEANLINE);
                chart.addLine(BaseChart4Coffee.INWINDLINE);
                chart.addLine(BaseChart4Coffee.OUTWINDLINE);
                break;
            case MODE2:
                ll.setVisibility(View.VISIBLE);
                chart.addLine(BaseChart4Coffee.ACCBEANLINE);
                chart.addLine(BaseChart4Coffee.ACCINWINDLINE);
                chart.addLine(BaseChart4Coffee.ACCOUTWINDLINE);
                break;
        }
    }

    public void setInterceptorView(BarLineChartBase.InterceptorView interceptorView) {
        this.interceptorView = interceptorView;
    }
}
