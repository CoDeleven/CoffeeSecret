package com.dhy.coffeesecret.ui.cup.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.hedgehog.ratingbar.RatingBar;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * Created by mxf on 2017/2/22.
 */
public class CuppingListAdapter extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter{

    private List<CuppingInfo> cuppingInfos;
    private Context context;

    public CuppingListAdapter(Context context, List<CuppingInfo> cuppingInfos) {
        this.context = context;
        this.cuppingInfos = cuppingInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cupping_title, parent, false);
        return new CuppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CuppingViewHolder cuppingViewHolder = (CuppingViewHolder) holder;
        CuppingInfo info = cuppingInfos.get(position);
        float score = info.getScore() / 20f;
        cuppingViewHolder.itemTitle.setText(info.getTitle());
        cuppingViewHolder.ratingBar.setStar(score);
    }

    @Override
    public long getHeaderId(int position) {
        return cuppingInfos.get(position).getDate().getTime();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cupping_header, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView tv = (TextView) holder.itemView;
        tv.setText(cuppingInfos.get(position).getDate().toGMTString());
    }

    @Override
    public int getItemCount() {
        return cuppingInfos.size();
    }

    private  class CuppingViewHolder extends RecyclerView.ViewHolder{

        public TextView itemTitle;
        public RatingBar ratingBar;

        public CuppingViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.tv);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rb);
        }
    }

}
