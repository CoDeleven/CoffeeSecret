package com.dhy.coffeesecret.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dhy.coffeesecret.R;

import java.util.List;

/**
 * Created by mxf on 2017/3/29.
 */
public class AccountAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<String> accounts;
    private Context context;
    private OnItemDelete onItemDelete;
    private OnItemClick onItemClick;


    public AccountAdapter(List<String> accounts,Context context,OnItemDelete onItemDelete) {
        this.accounts = accounts;
        this.context = context;
        this.onItemDelete = onItemDelete;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        holder.accountView.setText(accounts.get(position));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemDelete.onItemDelete(position);
                notifyDataSetChanged();
            }
        });

        holder.accountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClick != null){
                    onItemClick.onItemClick(accounts.get(position));
                }
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account,null);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public interface OnItemDelete {
        void onItemDelete(int position);
    }
    public interface OnItemClick{
        void onItemClick(String str);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder {

    ImageButton deleteButton;
    TextView accountView;

    public ItemViewHolder(View itemView) {
        super(itemView);
        deleteButton = (ImageButton) itemView.findViewById(R.id.iv_clear);
        accountView = (TextView) itemView.findViewById(R.id.account);
    }
}
