package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;

import java.util.ArrayList;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/15.
 */

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> countryList;
    private LayoutInflater mLayoutInflater;
    private OnCountryClickListener onCountryClickListener;

    public CountryListAdapter(Context context, ArrayList<String> countryList, OnCountryClickListener onCountryClickListener) {
        this.context = context;
        this.countryList = countryList;
        this.onCountryClickListener = onCountryClickListener;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_country, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.countryTV.setText(countryList.get(position));
        holder.countryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCountryClickListener.onCountryClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public interface OnCountryClickListener {
        void onCountryClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView countryTV = null;
        private LinearLayout countryLayout = null;

        public ViewHolder(View itemView) {
            super(itemView);
            countryTV = (TextView) itemView.findViewById(R.id.country_text);
            countryLayout = (LinearLayout) itemView.findViewById(R.id.country_layout);
        }
    }
}
