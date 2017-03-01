package com.dhy.coffeesecret.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/26.
 */

public class QuickEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_ITEM = 0;
    private static final int ITEM_VIEW_TYPE_FOOT = 1;

    private Context context;
    private View footView;
    private ArrayList<String> events;
    private LayoutInflater mLayoutInflater;
    private OnItemLongClickListener onItemLongClickListener;

    public QuickEventAdapter(Context context, ArrayList<String> events, View footView) {
        this.events = events;
        this.context = context;
        this.footView = footView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == ITEM_VIEW_TYPE_ITEM ? new QuickEventViewHolder(mLayoutInflater.inflate(R.layout.item_quick_event, null)) :
                new FootViewHolder(footView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() -1) {   // 如果是footView 直接结束
            return;
        }
        QuickEventViewHolder qeHoler = (QuickEventViewHolder) holder;
        qeHoler.textQuickEvent.setText(events.get(position));
        qeHoler.quickEventLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClicked(events.get(holder.getAdapterPosition()));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount()-1 ? ITEM_VIEW_TYPE_FOOT : ITEM_VIEW_TYPE_ITEM;
    }

    class QuickEventViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.text_quick_event)
        TextView textQuickEvent;
        @Bind(R.id.quick_event_layout)
        RelativeLayout quickEventLayout;

         QuickEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

         FootViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClicked(String itemString);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
