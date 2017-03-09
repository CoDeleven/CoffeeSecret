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
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.container.LinesSelectedActivity;
import com.dhy.coffeesecret.ui.container.fragments.BeanListFragment;
import com.dhy.coffeesecret.ui.cup.adapter.CuppingListAdapter;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.dhy.coffeesecret.R.string.acidity;
import static com.dhy.coffeesecret.R.string.after_taste;
import static com.dhy.coffeesecret.R.string.baked;
import static com.dhy.coffeesecret.R.string.balance;
import static com.dhy.coffeesecret.R.string.dry_and_frag;
import static com.dhy.coffeesecret.R.string.faced;
import static com.dhy.coffeesecret.R.string.flavor;
import static com.dhy.coffeesecret.R.string.overdev;
import static com.dhy.coffeesecret.R.string.scorched;
import static com.dhy.coffeesecret.R.string.sweet;
import static com.dhy.coffeesecret.R.string.taste;
import static com.dhy.coffeesecret.R.string.tipped;
import static com.dhy.coffeesecret.R.string.underdev;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.*;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.VIEW_TYPE;

public class CupFragment extends Fragment {

    private static final int GET_CUPPING_INFOS = 0x00001;
    private static final int LOADING = 0x00010;
    private static final int NO_LOADING = 0x00100;
    private static final int LOADING_SUCCESS = 0x200000;
    private static final int LOADING_ERROR = 0x300000;

    public static final int REQ_CODE_NEW = 0x0002;
    public static final int REQ_CODE_EDIT = 0x0020;
    public static final int RESULT_CODE_ADD = 0x0200;
    public static final int RESULT_CODE_UPDATE = 0x666;
    public static final int RESULT_CODE_NONE = 0x2333;
    public static final int RESULT_CODE_DElETE = 0x5555;

    private OnCupInteractionListener mListener;
    private View mCuppingView;
    private Context mContext;
    private ImageView mAddButton;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private CuppingInfoHandler mHandler;

    private List<CuppingInfo> cuppingInfos;
    private CuppingListAdapter mAdapter;

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
                Intent intent = new Intent(mContext, NewCuppingActivity.class);
                intent.putExtra(TARGET, cuppingInfos.get(position));
                intent.putExtra(VIEW_TYPE, SHOW_INFO);
                startActivityForResult(intent, REQ_CODE_EDIT);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
        mRecyclerView.addItemDecoration(new DividerDecoration(mContext));

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), LinesSelectedActivity.class);
                Intent intent = new Intent(getActivity(), NewCuppingActivity.class);
                intent.putExtra(VIEW_TYPE, NEW_CUPPING);
                startActivityForResult(intent, REQ_CODE_NEW);
//                startActivity(intent);
            }
        });
        mHandler = new CuppingInfoHandler(this);
        mHandler.sendEmptyMessage(GET_CUPPING_INFOS);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(GET_CUPPING_INFOS);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_EDIT) {
            if (resultCode == RESULT_CODE_UPDATE) {
                CuppingInfo info = (CuppingInfo) data.getSerializableExtra(TARGET);
                mAdapter.update(info);
            } else if (resultCode == RESULT_CODE_DElETE) {
                CuppingInfo info = (CuppingInfo) data.getSerializableExtra(TARGET);
                mAdapter.delete(info);
            }
        } else if (requestCode == REQ_CODE_NEW) {
            if (resultCode == RESULT_CODE_ADD) {
                CuppingInfo info = (CuppingInfo) data.getSerializableExtra(TARGET);
                mAdapter.add(info);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCuppingView = inflater.inflate(R.layout.fragment_cup, container, false);
        mContext = getContext();
        return mCuppingView;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

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
    //加载数据
    public void loadInfos() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String str = HttpUtils.getStringFromServer(URLs.GET_ALL_CUPPING);
                    Type type = new TypeToken<ArrayList<CuppingInfo>>() {
                    }.getType();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    List<CuppingInfo> newInfos = gson.fromJson(str, type);
                    cuppingInfos.clear();
                    cuppingInfos.addAll(newInfos);
                    mHandler.sendEmptyMessage(LOADING_SUCCESS);
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(LOADING_ERROR);
                }
            }
        }.start();
    }

    //        for (int i = 0; i < 20; i++) {
//            CuppingInfo cuppingInfo = new CuppingInfo();
//            cuppingInfo.setId(i);
//            cuppingInfo.setName("mxf---" + i);
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Date date = format.parse("2011-2-" + i / 5 + 1);
//                cuppingInfo.setDate(date);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            addData(cuppingInfo);
//            cuppingInfo.setBakeReport(new BakeReport());
//            cuppingInfos.add(cuppingInfo);
//        }
    // TODO: 2017/2/26
    void addData(CuppingInfo cuppingInfo) {

        cuppingInfo.setAcidity(8);
        cuppingInfo.setAfterTaste(8);
        cuppingInfo.setBalance(8);
        cuppingInfo.setDryAndFragrant(8);
        cuppingInfo.setFlavor(8);
        cuppingInfo.setTaste(8);
        cuppingInfo.setBalance(2);
        cuppingInfo.setSweetness(5);
        cuppingInfo.setOverall(9);

        cuppingInfo.setBaked(7);
        cuppingInfo.setFaced(4);
        cuppingInfo.setScorched(3);
        cuppingInfo.setUnderdevelopment(2);
        cuppingInfo.setTipped(9);
        cuppingInfo.setOverdevelopment(6);
    }

    public interface OnCupInteractionListener {
        void onCupInteraction(Uri uri);
    }

    private class CuppingInfoHandler extends Handler {

        private WeakReference<CupFragment> mWeakReference;

        CuppingInfoHandler(CupFragment fragment) {
            this.mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final CupFragment fragment = mWeakReference.get();

            switch (msg.what) {
                case GET_CUPPING_INFOS:
                    sendEmptyMessage(LOADING);
                    loadInfos();
                    break;
                case LOADING:
                    if (!mRefreshLayout.isRefreshing()) {
                        fragment.mRefreshLayout.setRefreshing(true);
                    }
                    break;
                case NO_LOADING:
                    if (mRefreshLayout.isRefreshing()) {
                        fragment.mRefreshLayout.setRefreshing(false);
                    }
                    break;
                case LOADING_SUCCESS:
                    T.showShort(mContext, "success");
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
}
