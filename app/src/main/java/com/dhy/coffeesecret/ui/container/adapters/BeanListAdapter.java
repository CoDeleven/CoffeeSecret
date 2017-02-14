package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/12.
 */

public class BeanListAdapter extends RecyclerView.Adapter<BeanListAdapter.BeanListViewHolder>
        implements StickyRecyclerHeadersAdapter<BeanListAdapter.BeanListViewHolder> {

    private Context context;
    private List<BeanInfo> coffeeBeanInfoList;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater inflater;

    public BeanListAdapter(Context context, List<BeanInfo> coffeeBeanInfoList, OnItemClickListener onItemClickListener) {

        this.context = context;
        this.coffeeBeanInfoList = coffeeBeanInfoList;
        this.onItemClickListener = onItemClickListener;

        inflater = LayoutInflater.from(context);

    }

    @Override
    public BeanListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeanListViewHolder(inflater.inflate(R.layout.item_bean_list, null));
    }

    @Override
    public void onBindViewHolder(BeanListViewHolder holder, final int position) {
        holder.beanName.setText(coffeeBeanInfoList.get(position).getName());
        holder.itemBeanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClicked(position);
            }
        });
    }


    @Override
    public long getHeaderId(int position) {
        return coffeeBeanInfoList.get(position).getName().charAt(0);
    }

    @Override
    public BeanListViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new BeanListViewHolder(inflater.inflate(R.layout.item_bean_list_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(BeanListViewHolder holder, int position) {
        TextView letter = (TextView) holder.itemView;
        letter.setText(String.valueOf(coffeeBeanInfoList.get(position).getName().charAt(0)));
    }

    @Override
    public int getItemCount() {
        return coffeeBeanInfoList.size();
    }

    class BeanListViewHolder extends RecyclerView.ViewHolder{

        private TextView beanName = null;
        private LinearLayout itemBeanLayout = null;

        public BeanListViewHolder(View itemView) {
            super(itemView);
            beanName = (TextView) itemView.findViewById(R.id.list_bean_produceArea);
            itemBeanLayout = (LinearLayout) itemView.findViewById(R.id.item_bean_layout);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
