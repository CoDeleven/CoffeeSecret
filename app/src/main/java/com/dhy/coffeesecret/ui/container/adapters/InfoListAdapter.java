package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/21.
 */

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoListViewHolder>
        implements StickyRecyclerHeadersAdapter<InfoListAdapter.InfoListViewHolder> {

    private Context context;
    private ArrayList<String> species;
    private ArrayList<String> infoList;
    private LayoutInflater mLayoutInflater;
    private OnInfoListClickListener onInfoListClickListener;

    public InfoListAdapter(Context context, ArrayList<String> infoList, OnInfoListClickListener onInfoListClickListener) {

        this.context = context;
        this.infoList = infoList;
        this.onInfoListClickListener = onInfoListClickListener;

        mLayoutInflater = LayoutInflater.from(context);

    }

    public InfoListAdapter(Context context, ArrayList<String> species, ArrayList<String> infoList, OnInfoListClickListener onInfoListClickListener) {

        this(context, infoList, onInfoListClickListener);
        this.species = species;
    }

    @Override
    public long getHeaderId(int position) {
        return species == null ? infoList.get(position).charAt(0) : species.get(position).charAt(0);
    }

    @Override
    public InfoListViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new InfoListViewHolder(mLayoutInflater.inflate(R.layout.item_bean_list_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(final InfoListViewHolder holder, int position) {
        TextView letter = (TextView) holder.itemView;
        if (species == null) {
            letter.setText(String.valueOf(infoList.get(position).charAt(0)));
        } else {
            letter.setText(species.get(position));
            letter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInfoListClickListener.onInfoClicked(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public InfoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoListViewHolder(mLayoutInflater.inflate(R.layout.item_info_list, null));
    }

    @Override
    public void onBindViewHolder(final InfoListViewHolder holder, int position) {
        holder.infoTV.setText(infoList.get(position));
        holder.infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInfoListClickListener.onInfoClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public interface OnInfoListClickListener {
        void onInfoClicked(int position);
    }

    class InfoListViewHolder extends RecyclerView.ViewHolder {

        private TextView infoTV = null;
        private LinearLayout infoLayout = null;

        InfoListViewHolder(View itemView) {
            super(itemView);
            infoTV = (TextView) itemView.findViewById(R.id.info_text);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.info_layout);
        }
    }
}

