package com.dhy.coffeesecret.views;

import android.content.Context;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by CoDeleven on 17-2-27.
 */

public class ReportMarker extends MarkerView {
    @Bind(R.id.id_report_tips_temp)
    TextView temp;
    @Bind(R.id.id_report_tips_event)
    TextView event;
    public ReportMarker(Context context, int layoutResource) {
        super(context, layoutResource);
        ButterKnife.bind(this);
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(!"".equals(e.getEvent().getDescription())){
            temp.setText("温度:" + e.getY());
            event.setText("事件:" + e.getEvent().getDescription());
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
