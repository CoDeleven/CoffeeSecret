package com.dhy.coffeesecret.ui.device.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.views.ScrollViewContainer;
import com.github.mikephil.charting.charts.BarLineChartBase;


public class ReportActivity extends AppCompatActivity implements BarLineChartBase.InterceptorView {
    private ScrollViewContainer scrollContainer;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conta_report);
        tableLayout = (TableLayout) findViewById(R.id.id_report_table);
        scrollContainer = (ScrollViewContainer) findViewById(R.id.id_conta_report_svc);
        ViewPager viewPager = (ViewPager) findViewById(R.id.id_report_linechart).findViewById(R.id.id_report_viewpager);
        FragmentPagerAdapter adapter = (FragmentPagerAdapter) viewPager.getAdapter();
        LineChartPart chart = (LineChartPart) adapter.getItem(0);
        LineChartPart chart2 = (LineChartPart) adapter.getItem(1);
        chart.setInterceptorView(this);
        chart2.setInterceptorView(this);
        init();
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


    private void init() {
        for (int i = 0; i < 40; ++i) {
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(10, 10, 10, 10);
            TableLayout.LayoutParams p = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(p);
            for (int j = 0; j < 5; ++j) {
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setText("ggggg");
                tableRow.addView(textView, lp);
            }
            tableRow.setGravity(Gravity.CENTER);
            tableLayout.addView(tableRow, p);
        }
    }
}
