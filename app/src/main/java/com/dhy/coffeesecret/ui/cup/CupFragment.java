package com.dhy.coffeesecret.ui.cup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.container.fragments.BeanListFragment;
import com.dhy.coffeesecret.ui.cup.adapter.CuppingListAdapter;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.*;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.VIEW_TYPE;

public class CupFragment extends Fragment {


    private OnCupInteractionListener mListener;
    private View mCuppingView;
    private Context mContext;
    private ImageView mAddButton;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private CuppingInfoHandler mHandler;

    private List<CuppingInfo> cuppingInfos;
    private CuppingListAdapter mAdapter;


    private static final int GET_CUPPING_INFOS = 0x00001;
    private static final int LOADING = 0x00010;
    private static final int NO_LOADING = 0x00100;

    public CupFragment() {
        cuppingInfos = new ArrayList<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRefreshLayout = (SwipeRefreshLayout) mCuppingView.findViewById(R.id.refresh_layout);
        mAddButton = (ImageView) mCuppingView.findViewById(R.id.iv_add);
        mRecyclerView = (RecyclerView) mCuppingView.findViewById(R.id.rv_cupping);
        mAdapter = new CuppingListAdapter(mContext, cuppingInfos);

        mAdapter.setOnItemClickListener(new CuppingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(mContext,NewCuppingActivity.class);
                intent.putExtra(TARGET,cuppingInfos.get(position));
                intent.putExtra(VIEW_TYPE, SHOW_INFO);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
        mRecyclerView.addItemDecoration(new DividerDecoration(mContext));

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewCuppingActivity.class);
                intent.putExtra(VIEW_TYPE, NEW_CUPPING);
                startActivity(intent);
            }
        });
        mHandler =  new CuppingInfoHandler(this);
        mHandler.sendEmptyMessage(GET_CUPPING_INFOS);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(GET_CUPPING_INFOS);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCuppingView = inflater.inflate(R.layout.fragment_cup, container, false);
        mContext = getContext();
        return mCuppingView;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCupInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCupInteractionListener) {
            mListener = (OnCupInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContainerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadInfos(){
        // TODO: 2017/2/23  加载数据
        for (int i = 0; i < 20; i++) {
            CuppingInfo cuppingInfo = new CuppingInfo();
            cuppingInfo.setName("mxf---"+i);
            cuppingInfo.setScore((60+5*i)%100);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse("2011-2-"+i/5+1);
                cuppingInfo.setDate(date);
            }catch (Exception e){
                e.printStackTrace();
            }

            cuppingInfos.add(cuppingInfo);
        }
    }

    public interface OnCupInteractionListener {
        void onCupInteraction(Uri uri);
    }

    private class CuppingInfoHandler extends Handler{

        private WeakReference<CupFragment> mWeakReference;

        CuppingInfoHandler(CupFragment fragment){
            this.mWeakReference = new WeakReference<CupFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final CupFragment fragment = mWeakReference.get();

            switch (msg.what) {
                case GET_CUPPING_INFOS:
                    new Thread(){
                        @Override
                        public void run() {
                            CuppingInfoHandler.this.sendEmptyMessage(LOADING);
                            SystemClock.sleep(2000);
                            loadInfos();
                        }
                    }.start();

                    CuppingInfoHandler.this.sendEmptyMessageDelayed(NO_LOADING,3000);
                    break;
                case LOADING:
                    if (!mRefreshLayout.isRefreshing()) {
                        fragment.mRefreshLayout.setRefreshing(true);
                    }
                    T.showShort(fragment.mContext, "refresh start");
                    break;
                case NO_LOADING:
                    if (mRefreshLayout.isRefreshing()) {
                        fragment.mRefreshLayout.setRefreshing(false);
                    }
                    T.showShort(fragment.mContext, "refresh start");
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
