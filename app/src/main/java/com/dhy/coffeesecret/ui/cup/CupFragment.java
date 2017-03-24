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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dhy.coffeesecret.MainActivity;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.container.adapters.HandlerAdapter;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.cup.filter.Filter;
import com.dhy.coffeesecret.ui.cup.adapter.CuppingListAdapter;
import com.dhy.coffeesecret.ui.cup.comparator.BaseComparator;
import com.dhy.coffeesecret.ui.cup.comparator.DateComparator;
import com.dhy.coffeesecret.ui.cup.comparator.OrderBy;
import com.dhy.coffeesecret.ui.cup.comparator.ScoreComparator;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.edmodo.rangebar.RangeBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.*;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.VIEW_TYPE;

public class CupFragment extends Fragment implements View.OnClickListener {

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

    private View mCuppingView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private PopupWindow mSortWindow;
    private PopupWindow mScreenWindow;

    private View mSortButton;
    private View mScreenButton;
    private TextView mSortText;

    private CuppingInfoHandler mHandler;

    private List<CuppingInfo> cuppingInfos;
    private List<CuppingInfo> allCuppingInfos;
    private CuppingListAdapter mAdapter;
    private boolean isShow;

    private BaseComparator currentComparator;
    private StickyRecyclerHeadersDecoration decor;
    private boolean hasDecor = true;
    private ScoreComparator scoreAscComparator;
    private ScoreComparator scoreDescComparator;
    private DateComparator dateAscComparator;
    private DateComparator dateDescComparator;

    private Filter filter;
    private boolean isAddSearchFragment;
    private SearchFragment searchFragment;


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
        mRecyclerView = (RecyclerView) mCuppingView.findViewById(R.id.rv_cupping);
        mSortButton = mCuppingView.findViewById(R.id.btn_sort);
        mScreenButton = mCuppingView.findViewById(R.id.btn_screen);
        mSortText = (TextView) mCuppingView.findViewById(R.id.sort_type);
        mAdapter = new CuppingListAdapter(mContext, cuppingInfos);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        initSortPopupWindow(inflater);
        initScreenPopupWindow(inflater);

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

        mCuppingView.findViewById(R.id.iv_add).setOnClickListener(this);
        mCuppingView.findViewById(R.id.iv_search).setOnClickListener(this);

        mSortButton.setOnClickListener(this);
        mScreenButton.setOnClickListener(this);

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
                sortList();
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

    private void sortList(){
        if(cuppingInfos != null&& currentComparator != null){
            Collections.sort(cuppingInfos,currentComparator);
        }
    }

    private void initSortPopupWindow(LayoutInflater inflater) {
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
        mSortWindow = getPopupWindow(contentView, MATCH_PARENT, WRAP_CONTENT);
    }

    private void initScreenPopupWindow(LayoutInflater inflater) {

        final List<String> items = new ArrayList<>();
        items.add("全部");
        items.add("近三日");
        items.add("近一月");
        items.add("近三月");
        items.add("近一年");
        items.add("更久");

        filter = new Filter();
        View contentView = inflater.inflate(R.layout.ppw_cupping_screen, null);
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.date_screen);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new HandlerAdapter(getContext(), items, new HandlerAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                filter.dateSelection = position;
            }
        }));

        final TextView firView = (TextView) contentView.findViewById(R.id.tv_first_score);
        final TextView secView = (TextView) contentView.findViewById(R.id.tv_second_score);

        RangeBar rangeBar = (RangeBar) contentView.findViewById(R.id.rb_score);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {
                int leftIndex = rangeBar.getLeftIndex();
                int rightIndex = rangeBar.getRightIndex();
                firView.setText(leftIndex + "");
                secView.setText(rightIndex + "");
                filter.max = rightIndex;
                filter.min = leftIndex;
            }
        });
        mScreenWindow = getPopupWindow(contentView, WRAP_CONTENT, WRAP_CONTENT);
    }

    private void setSortOrder(int id) {
        switch (id) {
            case R.id.tv_max:
                mSortText.setText(SORT_ORDER[0]);
                if (scoreAscComparator == null) {
                    scoreAscComparator = new ScoreComparator(OrderBy.ASC);
                }
                currentComparator = scoreAscComparator;
                toggleDecor(false);
                break;
            case R.id.tv_min:
                mSortText.setText(SORT_ORDER[1]);
                if (scoreDescComparator == null) {
                    scoreDescComparator = new ScoreComparator();
                }
                currentComparator = scoreDescComparator;
                toggleDecor(false);
                break;
            case R.id.tv_early:
                mSortText.setText(SORT_ORDER[2]);
                if (dateAscComparator == null) {
                    dateAscComparator = new DateComparator(OrderBy.ASC);
                }
                currentComparator = dateAscComparator;
                toggleDecor(true);
                break;
            case R.id.tv_later:
                mSortText.setText(SORT_ORDER[3]);
                if (dateDescComparator == null) {
                    dateDescComparator = new DateComparator();
                }
                currentComparator = dateDescComparator;
                toggleDecor(true);
                break;
            default:
                break;
        }
        sortList();
        mAdapter.notifyDataSetChanged();
        mSortWindow.dismiss();
        isShow = false;
    }

    private void toggleDecor(boolean show) {
        if (show && !hasDecor) {
            mRecyclerView.addItemDecoration(decor);
            hasDecor = true;
        } else if (!show && hasDecor) {
            mRecyclerView.removeItemDecoration(decor);
            hasDecor = false;
        }
    }

    public PopupWindow getPopupWindow(View content, int width, int height) {
        PopupWindow popupWindow = new PopupWindow(content, width, height);
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

//                    Map<String, BakeReport> bakeReports = TestData.getBakeReports(getActivity());
//                    String next = bakeReports.keySet().iterator().next();
//                    for (CuppingInfo newInfo : newInfos) {
//                        BakeReport report = bakeReports.get(next);
//                        newInfo.setBakeReport(report);
//                    }
                    cuppingInfos.clear();
                    cuppingInfos.addAll(newInfos);
                    mHandler.sendEmptyMessage(LOADING_SUCCESS);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(LOADING_ERROR);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sort:
                if (mSortWindow != null && !isShow) {
                    mSortWindow.showAsDropDown(mSortButton);
                    isShow = true;
                }
                break;
            case R.id.btn_screen:
                if (mScreenWindow != null && !isShow) {
                    mScreenWindow.showAsDropDown(mScreenButton);
                    isShow = true;
                }
                break;
            case R.id.iv_add:
                Intent intent = new Intent(getActivity(), NewCuppingActivity.class);
                intent.putExtra(VIEW_TYPE, NEW_CUPPING);
                startActivityForResult(intent, REQ_CODE_NEW);
                break;
            case R.id.iv_search:
                search();
                break;
            case R.id.btn_confirm:
                startScreen();
                mScreenWindow.dismiss();
                break;
        }
    }

    private void search() {
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);

        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }
        if (!isAddSearchFragment) {
            searchFragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("cuppingInfos", (Serializable) (allCuppingInfos == null?cuppingInfos:allCuppingInfos));
            searchFragment.setArguments(bundle);
            tx.add(R.id.activity_main, searchFragment, "search_cupping");
            isAddSearchFragment = true;

            searchFragment.setOnResultClickListenr(new SearchFragment.OnResultClickListenr() {
                @Override
                public void onItemClick(Serializable serializable) {
                    Intent intent = new Intent(mContext, NewCuppingActivity.class);
                    intent.putExtra(TARGET, serializable);
                    intent.putExtra(VIEW_TYPE, SHOW_INFO);
                    startActivityForResult(intent, REQ_CODE_EDIT);
                }
            });
        } else {
            tx.show(searchFragment);
        }
        tx.commit();
    }

    private void startScreen() {
        List<CuppingInfo> temp = new ArrayList<>();
        if(allCuppingInfos == null){
            allCuppingInfos = new ArrayList<>();
            allCuppingInfos.addAll(cuppingInfos);
        }
        for (CuppingInfo cuppingInfo : allCuppingInfos) {
            if(filter.doFilter(cuppingInfo)){
                temp.add(cuppingInfo);
            }
        }

        cuppingInfos.clear();
        cuppingInfos.addAll(temp);
        sortList();
        mAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }
        if (isAddSearchFragment) {
            searchFragment.remove();
            isAddSearchFragment = false;
        }
    }


    public boolean isAddSearch() {
        return isAddSearchFragment;
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
                    sortList();
                    mAdapter.notifyDataSetChanged();
                    sendEmptyMessage(NO_LOADING);
                    break;
                case LOADING_ERROR:
                    T.showShort(mContext, "网络连接失败");
                    sendEmptyMessage(NO_LOADING);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
