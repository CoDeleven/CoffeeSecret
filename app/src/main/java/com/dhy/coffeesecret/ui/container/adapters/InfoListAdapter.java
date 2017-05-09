package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.Species;
import com.dhy.coffeesecret.utils.Utils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/21.
 */

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoListViewHolder>
        implements StickyRecyclerHeadersAdapter<InfoListAdapter.InfoListViewHolder> {

    private Context context;
    private ArrayList<Species> speciesList;
    private ArrayList<String> infoList;
    private LayoutInflater mLayoutInflater;
    private OnInfoListClickListener onInfoListClickListener;

    public InfoListAdapter(Context context, ArrayList<String> infoList, OnInfoListClickListener onInfoListClickListener) {

        this.context = context;
        this.infoList = infoList;
        this.onInfoListClickListener = onInfoListClickListener;
        mLayoutInflater = LayoutInflater.from(context);

    }

    public InfoListAdapter(Context context, ArrayList<Species> speciesList, ArrayList<String> infoList, OnInfoListClickListener onInfoListClickListener) {

        this(context, infoList, onInfoListClickListener);
        this.speciesList = speciesList;
    }

    @Override
    public long getHeaderId(int position) {
        return speciesList == null || speciesList.size() == 0 ? Utils.getFirstPinYinLetter(infoList.get(position)).charAt(0) :
                Utils.getFirstPinYinLetter(speciesList.get(position).getOneSpecies()).charAt(0);
    }

    @Override
    public InfoListViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new InfoListViewHolder(mLayoutInflater.inflate(R.layout.item_bean_list_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(final InfoListViewHolder holder, int position) {
        TextView letter = (TextView) holder.itemView;
        if (speciesList == null || speciesList.size() == 0) {
            letter.setText(Utils.getFirstPinYinLetter(infoList.get(position)).substring(0,1));
        } else {
            final String headItem = speciesList.get(position).getOneSpecies();
            letter.setText(headItem);
            letter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInfoListClickListener.onInfoClicked(headItem);
                }
            });
        }
    }

    @Override
    public InfoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoListViewHolder(mLayoutInflater.inflate(R.layout.item_info_list, null));
    }

    @Override
    public void onBindViewHolder(InfoListViewHolder holder, int position) {
        String item = "";
        if (speciesList == null || speciesList.size() == 0) {
            item = infoList.get(position);
            final String finalItem = item;
            holder.infoTV.setText(item);
            holder.infoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInfoListClickListener.onInfoClicked(finalItem);
                }
            });
        } else {
            item = speciesList.get(position).getSpecies();
            final String finalItem = item;
            item = item.substring(1, item.length());
            holder.infoTV.setText(item);
            holder.infoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInfoListClickListener.onInfoClicked(finalItem);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return speciesList == null || speciesList.size() == 0 ? infoList.size() : speciesList.size();
    }

    public interface OnInfoListClickListener {
        void onInfoClicked(String item);
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

