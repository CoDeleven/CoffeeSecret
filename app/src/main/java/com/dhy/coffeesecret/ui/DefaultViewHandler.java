package com.dhy.coffeesecret.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.dhy.coffeesecret.utils.T;

/**
 * Created by CoDeleven on 17-8-25.
 */

public class DefaultViewHandler extends Handler {
    public static final int GET_DATA = 0x12, LOADING = 0x23, NO_LOADING = 0x32, LOADING_SUCCESS = 0x43, LOADING_ERROR = 0x53;
    private Handling mHandler;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    private Context mContext;
    public DefaultViewHandler(Context context){
        this.mContext = context;
    }

    public interface Handling{
        void doDataHandle();
        void doLoadingHandle();
        void doNoLoadingHandle();
        void doLoadingSuccessHandle();
        void doLoadingErrorHandle();
    }
    public void setHandling(Handling handling){
        this.mHandler = handling;
    }

    public void setRefreshLayout(SwipeRefreshLayout mRefreshLayout) {
        this.mRefreshLayout = mRefreshLayout;
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case GET_DATA:
                sendEmptyMessage(LOADING);
                mHandler.doDataHandle();
                break;
            case LOADING:
                if(mRefreshLayout != null){
                    if (!mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(true);
                    }
                }
                mHandler.doLoadingHandle();
                break;
            case NO_LOADING:
                if(mRefreshLayout != null){
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }
                mHandler.doNoLoadingHandle();
                break;
            case LOADING_SUCCESS:
                if(mAdapter != null){
                    mAdapter.notifyDataSetChanged();
                }
                sendEmptyMessage(NO_LOADING);
                mHandler.doLoadingSuccessHandle();
                break;
            case LOADING_ERROR:
                T.showShort(mContext, "error");
                mHandler.doLoadingErrorHandle();
                break;
        }
    }
}
