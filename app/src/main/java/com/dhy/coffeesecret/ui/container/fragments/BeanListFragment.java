package com.dhy.coffeesecret.ui.container.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.BeanInfoActivity;
import com.dhy.coffeesecret.ui.container.adapters.BeanListAdapter;
import com.dhy.coffeesecret.ui.container.adapters.CountryListAdapter;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/12.
 */

public class BeanListFragment extends Fragment implements OnQuickSideBarTouchListener {

    private static final String TAG = "BeanListFragment";

    private HashMap<String, Integer> letters = new HashMap<>();
    private ArrayList<BeanInfo> coffeeBeanInfos;
    private View beanListView;
    private LinearLayout btnCountryChoose = null;
    private LinearLayout btnScreen = null;
    private TextView countryName = null;
    private RecyclerView beanListRecycler = null;
    private SwipeRefreshLayout refreshBeanList = null;
    private QuickSideBarView quickSideBarView = null;
    private QuickSideBarTipsView quickSideBarTipsView = null;
    private PopupWindow mSortPopupWindow;
    private PopupWindow mScreenPopupWindow;
    private Context context;
    private String title;
    private boolean isPopupWindowShowing = false;
    private boolean isRefresh = false;

    public BeanListFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        mHandler.sendEmptyMessageDelayed(INIT_POPUP_WINDOW, 1000);
    }

    private void init() {

        btnCountryChoose = (LinearLayout) beanListView.findViewById(R.id.btn_country_choose);
        btnScreen = (LinearLayout) beanListView.findViewById(R.id.btn_screen);
        countryName = (TextView) beanListView.findViewById(R.id.country_name);
        beanListRecycler = (RecyclerView) beanListView.findViewById(R.id.bean_list);
        refreshBeanList = (SwipeRefreshLayout) beanListView.findViewById(R.id.refresh_bean_list);
        quickSideBarView = (QuickSideBarView) beanListView.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) beanListView.findViewById(R.id.quickSideBarTipsView);

        coffeeBeanInfos = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        beanListRecycler.setLayoutManager(layoutManager);

        BeanListAdapter adapter = new BeanListAdapter(context, coffeeBeanInfos, new BeanListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(context, BeanInfoActivity.class);
                intent.putExtra("beanInfo", "beanInfo");
                Log.i(TAG, "onItemClicked: position = " + position);
                startActivity(intent);
            }
        });

        beanListRecycler.setAdapter(adapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
        beanListRecycler.addItemDecoration(headersDecoration);
        beanListRecycler.addItemDecoration(new DividerDecoration(context));

        quickSideBarView.setOnQuickSideBarTouchListener(this);
        mHandler.sendEmptyMessage(GET_BEAN_INFOS);

        refreshBeanList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(GET_BEAN_INFOS);
            }
        });
    }

    private void getBeanInfos() {

        Log.i(TAG, "------------------开始加载豆种信息------------------");

        ArrayList<BeanInfo> coffeeBeanInfoList = new ArrayList<>();
        String[] beanLists = null;
        switch (title) {
            case "全部":
                beanLists = TestData.beanList1;
                break;
            case "中美":
                beanLists = TestData.beanList2;
                break;
            case "南美":
                beanLists = TestData.beanList3;
                break;
            case "大洋":
                beanLists = TestData.beanList4;
                break;
            case "亚洲":
                beanLists = TestData.beanList5;
                break;
            case "非洲":
                beanLists = TestData.beanList6;
                break;
            default:
                beanLists = TestData.beanList7;
                break;
        }

        for (int i = 0; i < beanLists.length; i++) {
            BeanInfo beanInfo = new BeanInfo();
            beanInfo.setName(beanLists[i]);

            coffeeBeanInfoList.add(beanInfo);

            if (!letters.containsKey(beanLists[i].substring(0, 1))) {
                letters.put(beanLists[i].substring(0, 1), i);
            }
        }
        coffeeBeanInfos.clear();
        coffeeBeanInfos.addAll(coffeeBeanInfoList);

        Log.i(TAG, "------------------豆种信息加载结束------------------");
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void initCountryPopupWindow() {
        initPopupWindow("country");
        btnCountryChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPopupWindowShowing) {
                    mSortPopupWindow.dismiss();
                } else {
                    mSortPopupWindow.showAsDropDown(btnCountryChoose);
                    isPopupWindowShowing = true;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void initScreenPopupWindow() {
        initPopupWindow("screen");
        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPopupWindowShowing) {
                    mScreenPopupWindow.dismiss();
                } else {
                    mScreenPopupWindow.showAsDropDown(btnScreen);
                    isPopupWindowShowing = true;
                }
            }
        });
    }

    private void initPopupWindow(String type) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = null;
        switch (type) {
            case "country":
                contentView = inflater.inflate(R.layout.ppw_conta_country, null);
                setCountryList(contentView);
                mSortPopupWindow = getPopupWindow(contentView);
                break;
            case "screen":
                contentView = inflater.inflate(R.layout.ppw_conta_screen, null);
                mScreenPopupWindow = getPopupWindow(contentView);
                break;
            default:
                break;
        }
    }

    private PopupWindow getPopupWindow(View contentView) {
        PopupWindow popupWindow = new PopupWindow(contentView, MATCH_PARENT, WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isPopupWindowShowing = false;
            }
        });
        return popupWindow;
    }

    private void setCountryList(View contentView) {
        RecyclerView countryList = (RecyclerView) contentView.findViewById(R.id.country_list);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        countryList.setLayoutManager(layoutManager);
        final ArrayList<String> countries = new ArrayList<String>();
        String[] countryArray = null;
        switch (title) {
            case "全部":
                countryArray = TestData.countryList1;
                break;
            case "中美":
                countryArray = TestData.countryList2;
                break;
            case "南美":
                countryArray = TestData.countryList3;
                break;
            case "大洋":
                countryArray = TestData.countryList4;
                break;
            case "亚洲":
                countryArray = TestData.countryList5;
                break;
            case "非洲":
                countryArray = TestData.countryList6;
                break;
            default:
                countryArray = TestData.countryList7;
                break;
        }
        Collections.addAll(countries, countryArray);
        countryList.setAdapter(new CountryListAdapter(context, countries, new CountryListAdapter.OnCountryClickListener() {
            @Override
            public void onCountryClicked(int position) {
                countryName.setText(countries.get(position));
                mSortPopupWindow.dismiss();
            }
        }));
        countryName.setText(countries.get(0));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beanListView = inflater.inflate(R.layout.fragment_bean_list, container, false);
        context = getActivity();
        return beanListView;
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            beanListRecycler.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }
    private static final int GET_BEAN_INFOS = 111;
    private static final int LOADING = 222;
    private static final int NO_LOADING = 333;
    private static final int INIT_POPUP_WINDOW = 444;

    private Handler mHandler = new BeanListHandler(this);

    private class BeanListHandler extends Handler {

        private final WeakReference<BeanListFragment> mActivity;

        public BeanListHandler(BeanListFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BeanListFragment activity = mActivity.get();
            switch (msg.what) {
                case GET_BEAN_INFOS:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(LOADING);

                            try {
                                Thread.sleep(2000);
                                T.showShort(context, "BeanInfo start load");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getBeanInfos();
                        }
                    }).start();

                    mHandler.sendEmptyMessageDelayed(NO_LOADING, 4000);

                    break;
                case LOADING:
                    if (!refreshBeanList.isRefreshing()) {
                        activity.refreshBeanList.setRefreshing(true);
                    }
                    T.showShort(context, "refresh start");
                    break;
                case NO_LOADING:
                    if (refreshBeanList.isRefreshing()) {
                        refreshBeanList.setRefreshing(false);
                    }
                    T.showShort(context, "refresh finish");
                    break;
                case INIT_POPUP_WINDOW:
                    initCountryPopupWindow();
                    initScreenPopupWindow();
                    break;
                default:
                    T.showShort(context, "you send a wrong message");
                    break;
            }
        }
    }


    public List<BeanInfo> getBeaninfoList() {
        return this.coffeeBeanInfos;
    }
}
