package com.dhy.coffeesecret.ui.device.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.dhy.coffeesecret.utils.T;

/**
 * Created by CoDeleven on 17-3-19.
 */

public class LinesSelectorHandler extends Handler {
    public static final int GET_LINES_INFOS = 0, LOADING = 1, NO_LOADING = 2, LOADING_SUCCESS = 3, LOADING_ERROR = 4;
    private Handling mHanding;
    private Context mContext;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    public LinesSelectorHandler(Context context, SwipeRefreshLayout swipeRefreshLayout, RecyclerView.Adapter adapter){
        this.mContext = context;
        this.mRefreshLayout = swipeRefreshLayout;
        this.mAdapter = adapter;
    }

    public interface Handling{
        void handling();
    }
    public void setHandling(Handling handling){
        this.mHanding = handling;
    }
    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {
            case GET_LINES_INFOS:
                sendEmptyMessage(LOADING);
                mHanding.handling();
                break;
            case LOADING:
                if (!mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(true);
                }
                break;
            case NO_LOADING:
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                break;
            case LOADING_SUCCESS:
                mAdapter.notifyDataSetChanged();
                sendEmptyMessage(NO_LOADING);
                break;
            case LOADING_ERROR:
                T.showShort(mContext, "error");
                sendEmptyMessage(NO_LOADING);
                break;
        }
        super.handleMessage(msg);
    }
}
