package com.dhy.coffeesecret.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;
import com.dhy.coffeesecret.utils.FormatUtils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * Created by CoDeleven on 17-3-12.
 */

public class HistoryLineAdapter extends RecyclerView.Adapter<HistoryLineAdapter.HistoryLineViewHolder> implements StickyRecyclerHeadersAdapter<HistoryLineAdapter.HistoryLineViewHolder> {
    private LayoutInflater layoutInflater;
    private List<BakeReport> bakeReports;
    private OnItemClickListener onLineClickedListener;
    public HistoryLineAdapter(Context context, List<BakeReport> bakeReports,  OnItemClickListener onLineClickedListener) {
        this.onLineClickedListener = onLineClickedListener;
        this.layoutInflater = LayoutInflater.from(context);
        this.bakeReports = bakeReports;
    }

    @Override
    public void onBindViewHolder(HistoryLineViewHolder holder, final int position) {
        holder.itemName.setText(bakeReports.get(position).getBeanInfoSimples().get(0).getBeanName());
        holder.itemDate.setText(bakeReports.get(position).getDate());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLineClickedListener.onItemClick(bakeReports.get(position));
            }
        });
    }

    @Override
    public HistoryLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryLineViewHolder(layoutInflater.inflate(R.layout.conta_lines_item_part, parent, false));

    }

    @Override
    public long getHeaderId(int position) {
        return FormatUtils.date2IdWithoutTimestamp(bakeReports.get(position).getDate());
    }

    @Override
    public void onBindHeaderViewHolder(HistoryLineViewHolder holder, int position) {
        TextView letter = (TextView) holder.itemView;
        letter.setText(FormatUtils.dateWidthoutTimestamp(bakeReports.get(position).getDate()));
    }

    @Override
    public HistoryLineViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HistoryLineViewHolder(layoutInflater.inflate(R.layout.item_bean_list_header, parent, false));
    }

    @Override
    public int getItemCount() {
        return bakeReports.size();
    }

    /*public interface OnItemClickListener {
        void onItemClicked(Serializable serializable);
    }*/

    class HistoryLineViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemDate;
        View layout;
        public HistoryLineViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            itemName = (TextView) layout.findViewById(R.id.id_lines_item_name);
            itemDate = (TextView) layout.findViewById(R.id.id_lines_item_date);

        }
    }
}
