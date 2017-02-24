package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;

import java.util.List;

/**
 * Created by CoDeleven on 17-2-7.
 */

public class LineListAdapter extends RecyclerView.Adapter<LineListAdapter.LineViewHolder> {
    private Context context;
    private List<BakeReport> reports;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater inflater;

    public LineListAdapter(Context context, List<BakeReport> reports, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.reports = reports;
        this.onItemClickListener = onItemClickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public LineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineViewHolder(inflater.inflate(R.layout.conta_lines_item_part, null));
    }

    @Override
    public void onBindViewHolder(final LineViewHolder holder, int position) {
        holder.lineName.setText(reports.get(position).getBeanName());
        holder.dateName.setText("日期:" + String.format("%1$tY-%1$tm-%1$te", reports.get(position).getBakeDate()));
        holder.lineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClicked(holder.getAdapterPosition(), reports.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, BakeReport report);
    }

    class LineViewHolder extends RecyclerView.ViewHolder {
        private TextView lineName;
        private TextView dateName;
        private RelativeLayout lineLayout;

        public LineViewHolder(View itemView) {
            super(itemView);

            lineName = (TextView) itemView.findViewById(R.id.id_lines_item_name);
            dateName = (TextView) itemView.findViewById(R.id.id_lines_item_date);
            lineLayout = (RelativeLayout) itemView.findViewById(R.id.item_line_layout);
        }
    }

}
