package com.dhy.coffeesecret.ui.counters.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;
import com.dhy.coffeesecret.ui.common.views.DividerDecoration;
import com.dhy.coffeesecret.ui.counters.BeanInfoActivity;
import com.dhy.coffeesecret.ui.counters.adapters.BeanListAdapter;
import com.dhy.coffeesecret.ui.counters.adapters.CountryListAdapter;
import com.dhy.coffeesecret.ui.counters.adapters.HandlerAdapter;
import com.dhy.coffeesecret.url.UrlBean;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.utils.Utils;
import com.edmodo.rangebar.RangeBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.jesse.nativelogger.NLogger;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/12.
 */

public class BeanListFragment extends Fragment implements OnQuickSideBarTouchListener {

    private static final String TAG = "BeanListFragment";
    private static final int GET_BEAN_INFOS = 111;
    private static final int LOADING = 222;
    private static final int NO_LOADING = 333;
    private static final int INIT_POPUP_WINDOW = 444;
    private static final int TOAST_1 = 555;
    private static final int TOAST_2 = 666;
    private static final int TOAST_3 = 777;
    private static final int START_SCREEN = 888;
    private HashMap<String, Integer> letters = new HashMap<>();
    private List<BeanInfo> coffeeBeanInfos;
    private List<BeanInfo> coffeeBeanInfoTemp;
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
    private BeanListAdapter beanListAdapter;
    private Context context;
    private String title = "";
    private int maxPrice = 1005;
    private int maxWeight = 505;
    private String screenHandler = "全部";
    ;
    private int[] screenPrice = new int[2];
    private int[] screenWeight = new int[2];
    private boolean isPopupWindowShowing = false;
    private Handler mHandler = new BeanListHandler(this);

    // TODO 因为此处需要用到position，而继承不需要
    // private int curPosition = -1;

    public BeanListFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.sendEmptyMessage(INIT_POPUP_WINDOW);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.sendEmptyMessage(GET_BEAN_INFOS);
    }

    private void init() {

        btnCountryChoose = (LinearLayout) beanListView.findViewById(R.id.btn_country_choose);
        btnScreen = (LinearLayout) beanListView.findViewById(R.id.btn_screen);
        countryName = (TextView) beanListView.findViewById(R.id.country_name);
        beanListRecycler = (RecyclerView) beanListView.findViewById(R.id.bean_list);
        refreshBeanList = (SwipeRefreshLayout) beanListView.findViewById(R.id.refresh_info_list);
        quickSideBarView = (QuickSideBarView) beanListView.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) beanListView.findViewById(R.id.quickSideBarTipsView);

        coffeeBeanInfos = new ArrayList<>();
        coffeeBeanInfoTemp = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        beanListRecycler.setLayoutManager(layoutManager);

        beanListAdapter = new BeanListAdapter(context, coffeeBeanInfos, new OnItemClickListener() {
            @Override
            public void onItemClick(Parcelable parcelable) {
                hook((BeanInfo) parcelable);

            }

/*            @Override
            public void onItemClicked(int position) {
                curPosition = position;
            }*/
        });

        beanListRecycler.setAdapter(beanListAdapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(beanListAdapter);
        beanListRecycler.addItemDecoration(headersDecoration);
        beanListRecycler.addItemDecoration(new DividerDecoration(context));

        quickSideBarView.setOnQuickSideBarTouchListener(this);

        refreshBeanList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(GET_BEAN_INFOS);
            }
        });

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

    // 钩子函数，时间紧急，使用继承
    public void hook(BeanInfo beanInfo) {
        Intent intent = new Intent(context, BeanInfoActivity.class);
        intent.putExtra(UniExtraKey.EXTRA_BEAN_INFO.getKey(), beanInfo);
        // startActivityForResult(intent, curPosition);
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        startActivity(intent);
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
                setScreenCondition(contentView);
                mScreenPopupWindow = getPopupWindow(contentView);
                break;
            default:
                break;
        }
    }


    private PopupWindow getPopupWindow(View contentView) {
        PopupWindow popupWindow = new PopupWindow(contentView, WRAP_CONTENT, WRAP_CONTENT);
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

    private void getBeanInfos() {

        NLogger.d(TAG, "------------------开始加载豆种信息------------------");
        mHandler.sendEmptyMessage(LOADING);

        Gson gson = new Gson();
        String beanInfoListJson = "";
        try {
            MyApplication application = (MyApplication) getActivity().getApplication();
            String token = application.getToken();
            String url = UrlBean.getAll(token);
            beanInfoListJson = HttpUtils.getStringFromServer(url,token,getActivity());
            Log.d(TAG, "BeanInfoList->" + beanInfoListJson);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(TOAST_3);
        }

        // beanInfoListJson = TestData.beaninfos;
        ArrayList<BeanInfo> beanInfoss = null;
        try {
            beanInfoss = gson.fromJson(beanInfoListJson, new TypeToken<ArrayList<BeanInfo>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            NLogger.d(TAG, "beanInfoListJson" + beanInfoListJson);
            NLogger.d(TAG, "getBeanInfos: " + beanInfoss);
        }

        if (beanInfoss == null) {
            mHandler.sendEmptyMessage(TOAST_3);
        } else {
            beanInfoss = sortByArea(beanInfoss);
            getLetters(beanInfoss);
            if (coffeeBeanInfoTemp != null) {
                coffeeBeanInfoTemp.clear();
            } else {
                coffeeBeanInfoTemp = new LinkedList<>();
            }
            coffeeBeanInfoTemp.addAll(beanInfoss);
/*            for (BeanInfo b : beanInfoss) {
                if (b.getContinent().equals(title)) {
                    coffeeBeanInfoTemp.add(b);
                }
            }*/
        }

        if (coffeeBeanInfos != null) {
            coffeeBeanInfos.clear();
            coffeeBeanInfos.addAll(coffeeBeanInfoTemp);
        }
        NLogger.d(TAG, "------------------title:" + title + "豆种信息加载结束------------------");
        NLogger.d(TAG, title + "->当前coffeeBeanInfoTemp.size():" + coffeeBeanInfoTemp.size());
        mHandler.sendEmptyMessage(NO_LOADING);
        mHandler.sendEmptyMessage(START_SCREEN);
    }

    private ArrayList<BeanInfo> sortByArea(ArrayList<BeanInfo> beanInfoss) {

        for (int i = beanInfoss.size() - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {

                if (beanInfoss.get(j).getArea() == null) {
                    break;
                }
                char a = Utils.getFirstPinYinLetter(beanInfoss.get(j + 1).getArea()).charAt(0);
                char b = Utils.getFirstPinYinLetter(beanInfoss.get(j).getArea()).charAt(0);
                if (a < b) {
                    BeanInfo beanInfo = beanInfoss.get(j);
                    beanInfoss.set(j, beanInfoss.get(j + 1));
                    beanInfoss.set(j + 1, beanInfo);
                } else if (a == b) {
                    // 如果第一个字符比较是相等的，则比较第二个字符

                    String area1 = beanInfoss.get(j + 1).getArea();
                    String area2 = beanInfoss.get(j).getArea();
                    // 如果没有第二个字符，则不进行判断
                    if (area1.length() >= 2 && area2.length() >= 2) {
                        char c = Utils.getFirstPinYinLetter(area1).charAt(1);
                        char d = Utils.getFirstPinYinLetter(area2).charAt(1);
                        if (c < d) {
                            BeanInfo beanInfo = beanInfoss.get(j);
                            beanInfoss.set(j, beanInfoss.get(j + 1));
                            beanInfoss.set(j + 1, beanInfo);
                        }
                    }
                }
            }
        }

        return beanInfoss;
    }

    private void getLetters(ArrayList<BeanInfo> beanInfoss) {

        int i = 0;

        for (BeanInfo b : beanInfoss) {
            String letter = Utils.getFirstPinYinLetter(b.getArea()).substring(0, 1);

            if (!letters.containsKey(letter)) {
                letters.put(letter, i);
            }
            i++;
        }

    }

    private void setScreenCondition(View contentView) {
        RecyclerView handlerList = (RecyclerView) contentView.findViewById(R.id.handler_recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        handlerList.setLayoutManager(layoutManager);
        final ArrayList<String> handlers = TestData.getHandlerList();
        HandlerAdapter adapter = new HandlerAdapter(context, handlers, new HandlerAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                screenHandler = handlers.get(position);
            }
        });
        handlerList.setAdapter(adapter);
        final TextView tvFirstPrice = (TextView) contentView.findViewById(R.id.tv_first_price);
        final TextView tvSecondPrice = (TextView) contentView.findViewById(R.id.tv_second_price);
        final TextView tvFirstWeight = (TextView) contentView.findViewById(R.id.tv_first_weight);
        final TextView tvSecondWeight = (TextView) contentView.findViewById(R.id.tv_second_weight);
        RangeBar rangePrice = (RangeBar) contentView.findViewById(R.id.range_price);
        RangeBar rangeWeight = (RangeBar) contentView.findViewById(R.id.range_weight);

        screenPrice[0] = 0;
        screenPrice[1] = maxPrice - 1;
        screenWeight[0] = 0;
        screenWeight[1] = maxWeight - 1;
        tvFirstPrice.setText(0 + "");
        tvSecondPrice.setText("不限");
        tvFirstWeight.setText(0 + "");
        tvSecondWeight.setText("不限");
        rangePrice.setTickCount(maxPrice + 50);
        rangeWeight.setTickCount(maxWeight + 50);
        rangePrice.setThumbIndices(0, maxPrice + 49);
        rangeWeight.setThumbIndices(0, maxWeight + 49);
        rangePrice.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int thumb1, int thumb2) {
                thumb1 = thumb1 < 50 ? 0 : thumb1 - 50;
                thumb2 = thumb2 - 50;
                tvFirstPrice.setText(thumb1 + "");
                if (thumb2 >= maxPrice - 5) {
                    tvSecondPrice.setText("1000+");
                } else {
                    tvSecondPrice.setText(thumb2 + "");
                }
                screenPrice[0] = thumb1;
                screenPrice[1] = thumb2;
            }
        });

        rangeWeight.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int thumb1, int thumb2) {

                thumb1 = thumb1 < 50 ? 0 : thumb1 - 50;
                thumb2 = thumb2 - 50;
                tvFirstWeight.setText(thumb1 + "");

                if (thumb2 > maxWeight - 5) {
                    tvSecondWeight.setText("500+");
                } else {
                    tvSecondWeight.setText(thumb2 + "");
                }
                screenWeight[0] = thumb1;
                screenWeight[1] = thumb2;
            }
        });

        Button btnConfirmCondition = (Button) contentView.findViewById(R.id.btn_confirm_condition);
        btnConfirmCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(START_SCREEN);
                mScreenPopupWindow.dismiss();
            }
        });
    }

    private void setCountryList(View contentView) {
        RecyclerView countryList = (RecyclerView) contentView.findViewById(R.id.country_list);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        countryList.setLayoutManager(layoutManager);
        final ArrayList<String> countries = new ArrayList<String>();
        String[] countryArray = null;
        switch (title) {
            case "全部":
                countryArray = getResources().getStringArray(R.array.all);
                break;
            case "中美":
                countryArray = getResources().getStringArray(R.array.central_america);
                break;
            case "南美":
                countryArray = getResources().getStringArray(R.array.south_america);
                break;
            case "大洋":
                countryArray = getResources().getStringArray(R.array.oceania);
                break;
            case "亚洲":
                countryArray = getResources().getStringArray(R.array.asia);
                break;
            case "非洲":
                countryArray = getResources().getStringArray(R.array.africa);
                break;
            default:
                countryArray = getResources().getStringArray(R.array.other);
                break;
        }
        Collections.addAll(countries, countryArray);
        countryList.setAdapter(new CountryListAdapter(context, countries, new CountryListAdapter.OnCountryClickListener() {
            @Override
            public void onCountryClicked(int position) {
                String cName = countries.get(position);
                countryName.setText(cName);
                if (cName.equals("全部")) {
                    coffeeBeanInfos.clear();
                    coffeeBeanInfos.addAll(coffeeBeanInfoTemp);
                } else {
                    coffeeBeanInfos.clear();
                    for (BeanInfo beanInfo : coffeeBeanInfoTemp) {
                        if (beanInfo.getCountry().equals(cName)) {
                            coffeeBeanInfos.add(beanInfo);
                        }
                    }
                }
                beanListAdapter.notifyDataSetChanged();
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

    public List<BeanInfo> getBeaninfoList() {
        return this.coffeeBeanInfos;
    }

    private void startScreen() {
        coffeeBeanInfos.clear();
        coffeeBeanInfos.addAll(coffeeBeanInfoTemp);

        sortContinentOfStartScreen();

        sortProcessOfStartScreen();

        sortPriceOfStartScreen();

        sortWeightOfStartScreen();

        if (!doSortByScreen()) {
            return;
        }
        // coffeeBeanInfos.clear();
        // coffeeBeanInfos.addAll(coffeeBeanInfoTemp);

        beanListAdapter.notifyDataSetChanged();
    }

    /**
     * 分解startScreen 方法
     * 划分地区
     */
    private void sortContinentOfStartScreen() {
        List<BeanInfo> foo = new LinkedList<>();
        if (!doSortByScreen()) {
            return;
        }
        for (BeanInfo beanInfo : coffeeBeanInfos) {
            if (title.equals(beanInfo.getContinent()) || "全部".equals(title)) {
                foo.add(beanInfo);
            }
        }
        coffeeBeanInfos.clear();
        coffeeBeanInfos.addAll(foo);
    }

    /**
     * 分解startScreen 方法
     * 划分处理方法
     */
    private void sortProcessOfStartScreen() {
        if (!doSortByScreen()) {
            return;
        }
        if ("全部".equals(screenHandler) || "".equals(screenHandler)) {
            return;
        }
        ArrayList<BeanInfo> foo = new ArrayList<>();
        for (BeanInfo beanInfo : coffeeBeanInfos) {
            if (beanInfo.getProcess().equals(screenHandler)) {
                foo.add(beanInfo);
            }
        }
        coffeeBeanInfos.clear();
        coffeeBeanInfos.addAll(foo);
    }

    /**
     * 分解startScreen 方法
     * 划分 价格 方法
     */
    private void sortPriceOfStartScreen() {
        if (!doSortByScreen()) {
            return;
        }
        List<BeanInfo> foo = new ArrayList<>();

        // if ((screenPrice[0] > 0 || screenPrice[1] < maxPrice - 1)) {
            for (BeanInfo beanInfo : coffeeBeanInfos) {
                if (beanInfo.getPrice() >= screenPrice[0]
                        && (screenPrice[1] > 1000 ? true : beanInfo.getPrice() <= screenPrice[1])) {
                    foo.add(beanInfo);
                }
            }
        // }
        coffeeBeanInfos.clear();
        coffeeBeanInfos.addAll(foo);
    }

    private void sortWeightOfStartScreen() {
        if (!doSortByScreen()) {
            return;
        }
        List<BeanInfo> foo = new ArrayList<>();

        // if (screenWeight[0] > 0 || screenWeight[1] < maxWeight - 1) {
            for (BeanInfo beanInfo : coffeeBeanInfos) {
                Log.i(TAG, "startScreen: weight = " + beanInfo.getStockWeight());
                if (beanInfo.getStockWeight() >= screenWeight[0] && beanInfo.getStockWeight() <= screenWeight[1]) {
                    foo.add(beanInfo);
                }
            }
        // }
        coffeeBeanInfos.clear();
        coffeeBeanInfos.addAll(foo);
    }

    private boolean doSortByScreen() {
        return coffeeBeanInfoTemp.size() > 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            coffeeBeanInfoTemp.set(requestCode, (BeanInfo) data.getParcelableExtra("new_bean_info"));
            coffeeBeanInfos.set(requestCode, (BeanInfo) data.getParcelableExtra("new_bean_info"));
        }
    }

    private class BeanListHandler extends Handler {

        private final WeakReference<BeanListFragment> mActivity;

        BeanListHandler(BeanListFragment activity) {
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
                            activity.getBeanInfos();
                        }
                    }).start();
                    break;
                case LOADING:
                    if (refreshBeanList != null && !refreshBeanList.isRefreshing()) {
                        activity.refreshBeanList.setRefreshing(true);
                    }
                    break;
                case NO_LOADING:
                    if (refreshBeanList != null && refreshBeanList.isRefreshing()) {
                        activity.refreshBeanList.setRefreshing(false);
                    }
                    if (countryName != null) {
                        countryName.setText("全部");
                    }

                    beanListAdapter.notifyDataSetChanged();
                    break;
                case INIT_POPUP_WINDOW:
                    Log.d(TAG, "INIT_POPUP_WINDOW");
                    initCountryPopupWindow();
                    initScreenPopupWindow();
                    break;
                case TOAST_1:
                    T.showShort(activity.getActivity(), "refresh start");
                    break;
                case TOAST_2:
                    T.showShort(activity.getActivity(), "BeanInfo start load");
                    break;
                case TOAST_3:
                    T.showShort(activity.getActivity(), "网络连接失败");
                    break;
                case START_SCREEN:
                    activity.startScreen();
                    break;
                default:
                    break;
            }
        }

    }
}
