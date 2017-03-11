package com.dhy.coffeesecret.ui.container.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.BeanInfoActivity;
import com.dhy.coffeesecret.ui.container.adapters.BeanListAdapter;
import com.dhy.coffeesecret.ui.container.adapters.CountryListAdapter;
import com.dhy.coffeesecret.ui.container.adapters.HandlerAdapter;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.edmodo.rangebar.RangeBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    private ArrayList<BeanInfo> coffeeBeanInfos;
    private ArrayList<BeanInfo> coffeeBeanInfoTemp;
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
    private int maxPrice = 2000;
    private int maxWeight = 10;
    private String screenHandler = "";
    private int[] screenPrice = new int[2];
    private int[] screenWeight = new int[2];
    private boolean isPopupWindowShowing = false;
    private Handler mHandler = new BeanListHandler(this);

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
        refreshBeanList = (SwipeRefreshLayout) beanListView.findViewById(R.id.refresh_info_list);
        quickSideBarView = (QuickSideBarView) beanListView.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) beanListView.findViewById(R.id.quickSideBarTipsView);

        coffeeBeanInfos = new ArrayList<>();
        coffeeBeanInfoTemp = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        beanListRecycler.setLayoutManager(layoutManager);

        beanListAdapter = new BeanListAdapter(context, coffeeBeanInfos, new BeanListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(context, BeanInfoActivity.class);
                intent.putExtra("beanInfo", coffeeBeanInfos.get(position));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
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

        Log.i(TAG, "------------------开始加载豆种信息------------------");
        mHandler.sendEmptyMessage(LOADING);

//        FormBody body = new FormBody.Builder()
//                .add("username", "Simo")
//                .add("action", "getBeanList")
//                .build();
//        Request request = new Request.Builder()
////                .url(Global.DOMAIN + Global.ENTRANCE_1)
//                .url("http://httpbin.org/post")
//                .post(body)
//                .build();
//        new OkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                mHandler.sendEmptyMessage(TOAST_1);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });

        Gson gson = new Gson();
        String beanInfoListJson = "";
        try {
            beanInfoListJson = HttpUtils.getStringFromServer(URLs.GET_ALL_BEAN_INFO);
        } catch (IOException e) {
            mHandler.sendEmptyMessage(TOAST_3);
        }
        String[] beanLists = null;
        beanInfoListJson = TestData.beaninfos;

        ArrayList<BeanInfo> beanInfoss = gson.fromJson(beanInfoListJson, new TypeToken<ArrayList<BeanInfo>>() {
        }.getType());
        Log.i(TAG, "getBeanInfos: " + beanInfoss);
        if (beanInfoss == null) {
            mHandler.sendEmptyMessage(TOAST_3);
        } else {
            beanInfoss = sortByArea(beanInfoss);
            getLetters(beanInfoss);
            coffeeBeanInfoTemp.clear();
            for (BeanInfo b : beanInfoss) {
                if (b.getContinent().equals(title)) {
                    coffeeBeanInfoTemp.add(b);
                } else if (title.equals("全部")) {
                    coffeeBeanInfoTemp.add(b);
                }
            }
        }

        if (coffeeBeanInfos != null) {
            coffeeBeanInfos.clear();
            coffeeBeanInfos.addAll(coffeeBeanInfoTemp);
        }

        mHandler.sendEmptyMessage(NO_LOADING);
        Log.i(TAG, "------------------豆种信息加载结束------------------");
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
                    char c = Utils.getFirstPinYinLetter(beanInfoss.get(j + 1).getArea()).charAt(1);
                    char d = Utils.getFirstPinYinLetter(beanInfoss.get(j).getArea()).charAt(1);
                    if (c < d) {
                        BeanInfo beanInfo = beanInfoss.get(j);
                        beanInfoss.set(j, beanInfoss.get(j + 1));
                        beanInfoss.set(j + 1, beanInfo);
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
        rangePrice.setTickCount(maxPrice);
        rangeWeight.setTickCount(maxWeight);
        rangePrice.setThumbIndices(0, maxPrice - 1);
        rangeWeight.setThumbIndices(0, maxWeight - 1);
        rangePrice.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int thumb1, int thumb2) {
                tvFirstPrice.setText(thumb1 + "");
                tvSecondPrice.setText(thumb2 + "");
                screenPrice[0] = thumb1;
                screenPrice[1] = thumb2;
            }
        });

        rangeWeight.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int thumb1, int thumb2) {
                tvFirstWeight.setText(thumb1 + "");
                tvSecondWeight.setText(thumb2 + "");
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
        mHandler.sendEmptyMessage(GET_BEAN_INFOS);
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
        ArrayList<BeanInfo> beanInfos = new ArrayList<>();
        beanInfos.addAll(coffeeBeanInfoTemp);
        if (screenHandler.equals("全部")  || screenHandler.equals("")) {
            coffeeBeanInfos.clear();
            coffeeBeanInfos.addAll(coffeeBeanInfoTemp);
        } else if (!screenHandler.equals("")) {
            coffeeBeanInfos.clear();
            for (BeanInfo beanInfo : beanInfos) {
                if (beanInfo.getProcess().equals(screenHandler)) {
                    coffeeBeanInfos.add(beanInfo);
                }
            }
        }
        Log.i(TAG, "startScreen: infos1 = " + coffeeBeanInfos.size());
        beanInfos.clear();
        beanInfos.addAll(coffeeBeanInfos);
        if ((screenPrice[0] > 0 || screenPrice[1] < maxPrice - 1)) {
            coffeeBeanInfos.clear();
            for (BeanInfo beanInfo : beanInfos) {
                if (beanInfo.getPrice() >= screenPrice[0] && beanInfo.getPrice() < screenPrice[1]) {
                    coffeeBeanInfos.add(beanInfo);
                }
            }
        }
        Log.i(TAG, "startScreen: infos2 = " + coffeeBeanInfos.size());
        beanInfos.clear();
        beanInfos.addAll(coffeeBeanInfos);
        if (screenWeight[0] > 0 || screenWeight[1] < maxWeight - 1) {
            coffeeBeanInfos.clear();
            for (BeanInfo beanInfo : beanInfos) {
                Log.i(TAG, "startScreen: weight = " + beanInfo.getStockWeight());
                if (beanInfo.getPrice() > screenWeight[0] && beanInfo.getStockWeight() < screenWeight[1]) {
                    coffeeBeanInfos.add(beanInfo);
                }
            }
        }

        beanListAdapter.notifyDataSetChanged();
    }

    private void screenByHandler() {

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
                    break;
                case INIT_POPUP_WINDOW:
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
