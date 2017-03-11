package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.dhy.coffeesecret.R;

import java.util.List;

/**
 * CoffeeSecret
 * Created by Simo on 2017/3/9.
 */

public class HandlerAdapter extends RecyclerView.Adapter<HandlerAdapter.HandlerViewHolder> {

    private Context context;
    private int selectedPosition;
    private List<String> handlerList;
    private LayoutInflater mLayoutInflater;
    private OnItemSelectListener onItemSelectListener;

    public HandlerAdapter(Context context, List<String> handlerList, OnItemSelectListener onItemSelectListener) {
        this.context = context;
        this.handlerList = handlerList;
        this.onItemSelectListener = onItemSelectListener;

        selectedPosition = 0;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public HandlerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HandlerViewHolder(mLayoutInflater.inflate(R.layout.item_selectable_btn, null));
    }

    @Override
    public void onBindViewHolder(final HandlerViewHolder holder, int position) {
        if (position == selectedPosition) {
            holder.btnHandler.setChecked(true);
            holder.btnHandler.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.btnHandler.setChecked(false);
            holder.btnHandler.setTextColor(context.getResources().getColor(R.color.border_color));
        }
        holder.btnHandler.setText(handlerList.get(position));
        holder.btnHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelectListener.onItemSelected(holder.getAdapterPosition());
                setSelectedPosition(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return handlerList.size();
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public interface OnItemSelectListener {
        void onItemSelected(int position);
    }

    class HandlerViewHolder extends RecyclerView.ViewHolder {

        private RadioButton btnHandler = null;

        HandlerViewHolder(View itemView) {
            super(itemView);
            btnHandler = (RadioButton) itemView.findViewById(R.id.btn_handler);
        }
    }

}
