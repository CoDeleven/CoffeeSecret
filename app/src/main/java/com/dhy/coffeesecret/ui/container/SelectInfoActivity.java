package com.dhy.coffeesecret.ui.container;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.container.adapters.InfoListAdapter;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.dhy.coffeesecret.views.SearchEditText;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectInfoActivity extends AppCompatActivity implements OnQuickSideBarTouchListener, SearchEditText.SearchBarListener {

    @Bind(R.id.btn_cancel)
    TextView btnCancel;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.btn_save)
    TextView btnSave;
    @Bind(R.id.search_edit)
    SearchEditText searchEdit;
    @Bind(R.id.info_list)
    RecyclerView infoList;
    @Bind(R.id.quickSideBarTipsView)
    QuickSideBarTipsView quickSideBarTipsView;
    @Bind(R.id.quickSideBarView)
    QuickSideBarView quickSideBarView;
    @Bind(R.id.activity_select_info)
    RelativeLayout activitySelectInfo;

    private Context context;
    private String infoType;
    private boolean isAddSearchFragment = false;
    private SearchFragment searchFragment;
    private ArrayList<String> dataList = null;
    private ArrayList<String> species = null;
    private InfoListAdapter infoAdapter = null;
    private HashMap<String, Integer> letters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_info);
        ButterKnife.bind(this);

        context = SelectInfoActivity.this;
        dataList = new ArrayList<>();
        species = new ArrayList<>();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initInfoList();
        initView();
    }

    private void initInfoList() {
        infoType = getIntent().getStringExtra("info_type");

        switch (infoType) {
            case "country":
                titleText.setText("国家");
                mHandler.sendEmptyMessage(GET_COUNTRY_LIST);
                break;
            case "area":
                titleText.setText("产地");
                mHandler.sendEmptyMessage(GET_AREA_LIST);
                break;
            case "manor":
                titleText.setText("庄园");
                mHandler.sendEmptyMessage(GET_MANOR_LIST);
                break;
            case "species":
                titleText.setText("豆种");
                mHandler.sendEmptyMessage(GET_SPECIES_LIST);
                break;
            default:
                mHandler.sendEmptyMessage(SHOW_WRONG_TOAST);
                break;
        }
    }

    private void initView() {
        btnSave.setVisibility(View.GONE);

        searchEdit.setSearchBarListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(SelectInfoActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        infoAdapter = new InfoListAdapter(context, dataList, new InfoListAdapter.OnInfoListClickListener() {
            @Override
            public void onInfoClicked(int position) {
                exitToRight(dataList.get(position));
            }
        });

        infoList.setLayoutManager(manager);
        infoList.setAdapter(infoAdapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(infoAdapter);
        infoList.addItemDecoration(headersDecoration);
        infoList.addItemDecoration(new DividerDecoration(context));

        quickSideBarView.setOnQuickSideBarTouchListener(this);
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            infoList.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    private void getDataList(int listType) {
        switch (listType) {
            case GET_COUNTRY_LIST:
                Collections.addAll(dataList, TestData.beanList7);
                break;
            case GET_AREA_LIST:
                Collections.addAll(dataList, TestData.beanList1);
                break;
            case GET_MANOR_LIST:
                Collections.addAll(dataList, TestData.beanList2);
                break;
            case GET_SPECIES_LIST:
                Collections.addAll(species, TestData.countryList7);
                Collections.addAll(dataList, TestData.beanList4);
                break;
            default:
                break;
        }

        for (int i = 0; i < dataList.size(); i++) {

            if (!letters.containsKey(dataList.get(0).substring(0, 1))) {
                letters.put(dataList.get(0).substring(0, 1), i);
            }
        }
    }

    @Override
    public void starSearchPage() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);

        if (!isAddSearchFragment && dataList.size() > 0) {
            searchFragment = new SearchFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("infoList", dataList);
            searchFragment.setArguments(bundle);
            searchFragment.addOnSearchCallBack(new SearchFragment.OnSearchCallBack() {
                @Override
                public void onSearchCallBack(String info) {
                    exitToRight(info);
                }
            });
            tx.add(R.id.activity_select_info, searchFragment, "search_info");
            isAddSearchFragment = true;
        } else {
            tx.show(searchFragment);
        }
        tx.commit();
    }

    @OnClick(R.id.btn_cancel)
    public void onClick() {
        exitToRight();
    }

    private static final int GET_COUNTRY_LIST = 111;
    private static final int GET_AREA_LIST = 222;
    private static final int GET_MANOR_LIST = 333;
    private static final int GET_SPECIES_LIST = 444;
    private static final int SHOW_WRONG_TOAST = 555;
    private SelectHandler mHandler = new SelectHandler(SelectInfoActivity.this);

    private class SelectHandler extends Handler {
        private final WeakReference<SelectInfoActivity> mActivity;

        public SelectHandler(SelectInfoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SelectInfoActivity activity = mActivity.get();
            switch (msg.what) {
                case GET_COUNTRY_LIST:
                    activity.getDataList(GET_COUNTRY_LIST);
                    break;
                case GET_AREA_LIST:
                    activity.getDataList(GET_AREA_LIST);
                    break;
                case GET_MANOR_LIST:
                    activity.getDataList(GET_MANOR_LIST);
                    break;
                case GET_SPECIES_LIST:
                    activity.getDataList(GET_SPECIES_LIST);
                    break;
                case SHOW_WRONG_TOAST:
                    T.showShort(activity.context, "网络连接失败");
                default:
                    break;
            }
        }
    }

    private void exitToRight(String info) {
        Intent intent = new Intent();
        intent.putExtra("info", info);
        setResult(RESULT_OK, intent);
        exitToRight();
    }

    private void exitToRight() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        if (isAddSearchFragment) {
            searchFragment.remove();
            isAddSearchFragment = false;
        } else {
            exitToRight();
        }
    }
}
