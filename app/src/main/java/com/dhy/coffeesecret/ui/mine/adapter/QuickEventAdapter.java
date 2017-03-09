package com.dhy.coffeesecret.ui.mine.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.DensityUtils;

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

    public QuickEventAdapter(Context context, ArrayList<String> events, View footView) {
        this.events = events;
        this.context = context;
        this.footView = footView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new QuickEventViewHolder(mLayoutInflater.inflate(R.layout.item_quick_event, null));
    }

    @Override
    public RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType) {
        return new FootViewHolder(footView);
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

        qeHolder.quickEventLayout.setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0;
            float layoutX = 0;
            float diff = 0;
            float lastDiff = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        qeHolder.quickEventLayout.setClickable(false);
                        layoutX = qeHolder.quickEventLayout.getX();
                        diff = lastX - event.getX();

                        if (lastDiff >= 0) {
                            qeHolder.quickEventLayout.setX(layoutX - diff);
                        }
                        lastX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (diff == 0) {
                            onItemClickListener.onItemClicked(events.get(qeHolder.getAdapterPosition()));
                            return false;
                        }
                    case MotionEvent.ACTION_CANCEL:
//                        Log.i(TAG, "onTouch: up = "  + (lastX - event.getX()));
//                        Log.i(TAG, "onTouch: viewX = "  + DensityUtils.px2dp(context, qeHolder.quickEventLayout.getX()));
                        float x = DensityUtils.dp2px(context, qeHolder.quickEventLayout.getX());
                        if (x > DensityUtils.dp2px(context, -35)) {
                            startAnimation(x, 0, qeHolder);
                        } else {
                            startAnimation(x, -70, qeHolder);
                        }

                        qeHolder.quickEventLayout.setClickable(true);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 动画
     * @param x 控件所在的X坐标
     */
    public void startAnimation(float x, float limitX, final QuickEventViewHolder qeHolder) {
        ValueAnimator anim = ValueAnimator.ofFloat(x
                , DensityUtils.dp2px(context, limitX));
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Message msg = new Message();
                msg.arg1 = ((Float)animation.getAnimatedValue()).intValue();
                msg.obj = qeHolder;
                mHandler.sendMessage(msg);
            }
        });
        anim.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((QuickEventViewHolder)msg.obj).quickEventLayout.setX(msg.arg1);
        }
    };

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


        QuickEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
