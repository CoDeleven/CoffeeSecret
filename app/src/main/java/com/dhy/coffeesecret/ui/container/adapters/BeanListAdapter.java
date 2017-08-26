package com.dhy.coffeesecret.ui.container.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.device.fragments.OnItemClickListener;
import com.dhy.coffeesecret.utils.Utils;
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
    public long getHeaderId(int position) {
        return Utils.getFirstPinYinLetter(coffeeBeanInfoList.get(position).getArea()).charAt(0);
    }

    @Override
    public BeanListViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new BeanListViewHolder(inflater.inflate(R.layout.item_bean_list_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(BeanListViewHolder holder, int position) {
        TextView letter = (TextView) holder.itemView;
        letter.setText(Utils.getFirstPinYinLetter(coffeeBeanInfoList.get(position).getArea()).substring(0, 1));
    }

    @Override
    public BeanListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeanListViewHolder(inflater.inflate(R.layout.item_bean_list, null));
    }


    @Override
    public void onBindViewHolder(final BeanListViewHolder holder, final int position) {

        BeanInfo beaninfo = coffeeBeanInfoList.get(position);
        if (beaninfo.getDrawablePath().equals("")) {
            beaninfo.setDrawablePath("default");
        }
        if (!beaninfo.getDrawablePath().trim().equals("")) {
            holder.beanIcon.setImageResource(Utils.getContainerIcon(beaninfo.getDrawablePath()));
        }
        holder.beanName.setText(beaninfo.getName());
        holder.beanSubName.setText(beaninfo.getCountry() + "-" + beaninfo.getArea() + "-" + beaninfo.getManor() + "-" + beaninfo.getLevel());
        holder.beanWeight.setText(Utils.get2PrecisionFloat((float)beaninfo.getStockWeight()) + "kg");
        holder.itemBeanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onItemClickListener.onItemClicked(holder.getAdapterPosition());
                onItemClickListener.onItemClick(coffeeBeanInfoList.get(holder.getAdapterPosition()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return coffeeBeanInfoList.size();
    }

/*    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
    */
    class BeanListViewHolder extends RecyclerView.ViewHolder {

        private ImageView beanIcon = null;
        private TextView beanName = null;
        private TextView beanSubName = null;
        private TextView beanWeight = null;
        private LinearLayout itemBeanLayout = null;

        BeanListViewHolder(View itemView) {
            super(itemView);

            beanIcon = (ImageView) itemView.findViewById(R.id.list_bean_icon);
            beanName = (TextView) itemView.findViewById(R.id.list_bean_name);
            beanSubName = (TextView) itemView.findViewById(R.id.list_bean_subname);
            beanWeight = (TextView) itemView.findViewById(R.id.bean_weight);
            itemBeanLayout = (LinearLayout) itemView.findViewById(R.id.item_bean_layout);
        }
    }
}
