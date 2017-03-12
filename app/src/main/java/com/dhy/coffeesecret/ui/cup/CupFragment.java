package com.dhy.coffeesecret.ui.cup;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.pojo.TempratureSet;
import com.dhy.coffeesecret.ui.cup.adapter.CuppingListAdapter;
import com.dhy.coffeesecret.ui.cup.comparator.BaseComparator;
import com.dhy.coffeesecret.ui.cup.comparator.DateComparator;
import com.dhy.coffeesecret.ui.cup.comparator.OrderBy;
import com.dhy.coffeesecret.ui.cup.comparator.ScoreComparator;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
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

    public static final String[] SORT_ORDER = {"评分最高", "评分最低", "最早的杯测", "最晚的杯测"}; // TODO: 2017/3/8  i18n

    private OnCupInteractionListener mListener;
    private View mCuppingView;
    private Context mContext;
    private ImageView mAddButton;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private PopupWindow mSortWindow;

    private View mSortButton;
    private View mScreenButton;
    private TextView mSortText;

    private CuppingInfoHandler mHandler;

    private List<CuppingInfo> cuppingInfos;
    private CuppingListAdapter mAdapter;
    private boolean isShow;

    private BaseComparator currentComparator;
    private StickyRecyclerHeadersDecoration decor;
    private boolean hasDecor = true;


    public CupFragment() {
        cuppingInfos = new ArrayList<>();
        currentComparator = new DateComparator();
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
        mSortButton = mCuppingView.findViewById(R.id.btn_sort);
        mScreenButton = mCuppingView.findViewById(R.id.btn_screen);
        mSortText = (TextView) mCuppingView.findViewById(R.id.sort_type);
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

        decor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.addItemDecoration(new DividerDecoration(mContext));

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewCuppingActivity.class);
                intent.putExtra(VIEW_TYPE, NEW_CUPPING);
                startActivityForResult(intent, REQ_CODE_NEW);
            }
        });
        initPopupWindow(); // TODO: 2017/3/8
        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSortWindow != null && !isShow) {
                    mSortWindow.showAsDropDown(mSortButton);
                    isShow = true;
                }
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

    private void initPopupWindow() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.ppw_cupping_sort, null);
        RadioGroup rg = (RadioGroup) contentView.findViewById(R.id.rg_type);
        for (int i = 0; i < rg.getChildCount(); i++) {
            rg.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSortOrder(view.getId());
                }
            });
        }
        mSortWindow = getPopupWindow(contentView);
    }

    private void setSortOrder(int id) {
        switch (id) {
            case R.id.tv_max:
                mSortText.setText(SORT_ORDER[0]);
                Collections.sort(cuppingInfos, new ScoreComparator(OrderBy.ASC));
                currentComparator = new ScoreComparator(OrderBy.ASC);
                mRecyclerView.removeItemDecoration(decor);
                if (hasDecor) {
                    mRecyclerView.removeItemDecoration(decor);
                    hasDecor = false;
                }
                break;
            case R.id.tv_min:
                Collections.sort(cuppingInfos, new ScoreComparator());
                currentComparator = new ScoreComparator();
                mSortText.setText(SORT_ORDER[1]);
                if (hasDecor) {
                    mRecyclerView.removeItemDecoration(decor);
                    hasDecor = false;
                }
                break;
            case R.id.tv_early:
                Collections.sort(cuppingInfos, new DateComparator(OrderBy.ASC));
                currentComparator = new DateComparator(OrderBy.ASC);
                mSortText.setText(SORT_ORDER[2]);
                if (!hasDecor) {
                    mRecyclerView.addItemDecoration(decor);
                    hasDecor = true;
                }
                break;
            case R.id.tv_later:
                Collections.sort(cuppingInfos, new DateComparator());
                currentComparator = new DateComparator();
                mSortText.setText(SORT_ORDER[3]);
                if (!hasDecor) {
                    mRecyclerView.addItemDecoration(decor);
                    hasDecor = true;
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
        mSortWindow.dismiss();
        isShow = false;
    }

    public PopupWindow getPopupWindow(View content) {
        PopupWindow popupWindow = new PopupWindow(content, MATCH_PARENT, WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isShow = false;
            }
        });
        return popupWindow;
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
//                    String str = HttpUtils.getStringFromServer(URLs.GET_ALL_CUPPING);
//                    System.out.println(str);
                    String str = TestData.cuppingInfos;
                    Type type = new TypeToken<ArrayList<CuppingInfo>>() {
                    }.getType();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    List<CuppingInfo> newInfos = gson.fromJson(str, type);
                    System.out.println(newInfos);

                    Map<String, BakeReport> bakeReports = TestData.getBakeReports(getActivity());
                    String next = bakeReports.keySet().iterator().next();
                    for (CuppingInfo newInfo : newInfos) {
                        BakeReport report = bakeReports.get(next);
                        newInfo.setBakeReport(report);
                    }

                    cuppingInfos.clear();
                    cuppingInfos.addAll(newInfos);
                    mHandler.sendEmptyMessage(LOADING_SUCCESS);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(LOADING_ERROR);
                }
            }
        }.start();
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
                    Collections.sort(cuppingInfos, currentComparator);
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
}
