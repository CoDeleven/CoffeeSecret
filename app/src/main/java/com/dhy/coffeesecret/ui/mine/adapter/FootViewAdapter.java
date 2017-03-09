package com.dhy.coffeesecret.ui.mine.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * CoffeeSecret
 * Created by Simo on 2017/3/3.
 */

public abstract class FootViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_ITEM = 0;
    private static final int ITEM_VIEW_TYPE_FOOT = 1;

    public abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public abstract RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolderWithoutFoot(RecyclerView.ViewHolder holder, int position);

    public abstract int getItemWithFootCount();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == ITEM_VIEW_TYPE_ITEM ? onCreateItemViewHolder(parent, viewType) :
                onCreateFootViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemWithFootCount()) {
            return;
        }
        onBindViewHolderWithoutFoot(holder, position);
    }

    @Override
    public int getItemCount() {
        return getItemWithFootCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount()-1 ? ITEM_VIEW_TYPE_FOOT : ITEM_VIEW_TYPE_ITEM;
    }
}
