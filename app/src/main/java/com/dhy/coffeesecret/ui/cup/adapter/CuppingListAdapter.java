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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by mxf on 2017/2/22.
 */
public class CuppingListAdapter extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter {

    private List<CuppingInfo> cuppingInfos;
    private Context context;

    private OnItemClickListener mListener;

    public CuppingListAdapter(Context context, List<CuppingInfo> cuppingInfos) {
        this.context = context;
        this.cuppingInfos = cuppingInfos;
    }

    public void add(CuppingInfo info){
        cuppingInfos.add(info);
        notifyDataSetChanged();
    }

    public void update(CuppingInfo info){
        int i = cuppingInfos.indexOf(info);
        cuppingInfos.remove(i);
        cuppingInfos.add(i,info);
        notifyDataSetChanged();
    }

    public void delete(CuppingInfo info) {
        int i = cuppingInfos.indexOf(info);
        System.out.println(i);
        cuppingInfos.remove(i);
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cupping_title, parent, false);
        return new CuppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CuppingViewHolder cuppingViewHolder = (CuppingViewHolder) holder;
        CuppingInfo info = cuppingInfos.get(position);
        float score = info.getScore() / 20f;
        cuppingViewHolder.itemTitle.setText(info.getName());
        cuppingViewHolder.ratingBar.setStar(score);

        cuppingViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public long getHeaderId(int position) {
        return cuppingInfos.get(position).getDate().getTime();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cupping_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView tv = (TextView) holder.itemView;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = cuppingInfos.get(position).getDate();
        tv.setText(format.format(date));
    }

    @Override
    public int getItemCount() {
        return cuppingInfos.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private class CuppingViewHolder extends RecyclerView.ViewHolder {

        public TextView itemTitle;
        public RatingBar ratingBar;

        public CuppingViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.tv);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rb);
            ratingBar.setmClickable(false);
        }
    }

}
