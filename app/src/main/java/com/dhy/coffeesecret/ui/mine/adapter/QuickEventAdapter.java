package com.dhy.coffeesecret.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.dhy.coffeesecret.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/26.
 */

public class QuickEventAdapter extends FootViewAdapter<RecyclerView.ViewHolder> {

    private static final String TAG = "QuickEventAdapter";
    private Context context;
    private View footView;
    private ArrayList<String> events;
    private LayoutInflater mLayoutInflater;
    private onItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private onFootClickListener onFootClickListener;
    public QuickEventAdapter(Context context, ArrayList<String> events) {
        this.events = events;
        this.context = context;
        // this.footView = footView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new QuickEventViewHolder(mLayoutInflater.inflate(R.layout.item_quick_event, parent, false));
    }

    @Override
    public RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType) {
        return new FootViewHolder(getFootView(parent));
    }

    private View getFootView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View footView = inflater.inflate(R.layout.item_quick_event_foot, parent, false);
        RelativeLayout addQuickEvent = (RelativeLayout) footView.findViewById(R.id.add_quick_event);
        addQuickEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFootClickListener.onFootClicked(v);
            }
        });
        return footView;
    }

    public void setOnFootClickListener(QuickEventAdapter.onFootClickListener onFootClickListener) {
        this.onFootClickListener = onFootClickListener;
    }

    @Override
    public void onBindViewHolderWithoutFoot(RecyclerView.ViewHolder holder, int position) {
        final QuickEventViewHolder qeHolder = (QuickEventViewHolder) holder;
        qeHolder.textQuickEvent.setText(events.get(position));
        qeHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClicked(qeHolder.getAdapterPosition());
            }
        });
        qeHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        qeHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        qeHolder.swipeLayout.setLeftSwipeEnabled(false);
        qeHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, qeHolder.btnDelete);
        // qeHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, null);
    }


    @Override
    public int getItemWithFootCount() {
        return events.size();
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }
    public interface onItemClickListener {
        void onItemClicked(String itemString);
    }

    public interface onFootClickListener{
        void onFootClicked(View view);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(int position);
    }

    class QuickEventViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.text_quick_event)
        TextView textQuickEvent;
        @Bind(R.id.btn_delete)
        TextView btnDelete;
        @Bind(R.id.quick_event_layout)
        LinearLayout quickEventLayout;
        @Bind(R.id.id_swipe_layout)
        SwipeLayout swipeLayout;
        QuickEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
