package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;

import java.util.List;

/**
 * Created by CoDeleven on 17-2-7.
 */

public class LinesAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<BakeReport> reports;

    public LinesAdapter(List<BakeReport> reports, Context context) {
        this.reports = reports;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reports.size();
    }

    @Override
    public Object getItem(int i) {
        return reports.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.conta_lines_item_part, viewGroup, false);
            viewHolder.lineName = (TextView) view.findViewById(R.id.id_lines_item_name);
            viewHolder.dateName = (TextView) view.findViewById(R.id.id_lines_item_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.lineName.setText(reports.get(i).getBeanName());
        viewHolder.dateName.setText("日期:" + String.format("%1$tY-%1$tm-%1$te", reports.get(i).getBakeDate()));
        return view;
    }
}