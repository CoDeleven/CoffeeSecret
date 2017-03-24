package com.dhy.coffeesecret.ui.cup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.adapters.LinesAdapter;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.device.ReportActivity;
import com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler;
import com.dhy.coffeesecret.ui.mine.HistoryLineActivity;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.dhy.coffeesecret.views.SearchEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_ADD;
import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_EXIT;
import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_NONE;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.GET_LINES_INFOS;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.LOADING_ERROR;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.LOADING_SUCCESS;

public class LinesSelectedActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener, LinesSelectorHandler.Handling {

    private RecyclerView listView;
    private SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private boolean isAddSearchFragment = false;
    private List<BakeReport> bakeReportList = new ArrayList<>();
    private ImageView backButton;
    private LinesSelectorHandler mHandler;
    SwipeRefreshLayout mRefreshLayout;
    private HistoryLineAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.cupping_lines);
        listView = (RecyclerView) findViewById(R.id.id_lines_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar_device_activtiy);
        backButton = (ImageView) findViewById(R.id.iv_back);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.id_swipeRefresh);
        searchBar = (SearchEditText)findViewById(R.id.lines_selected_srh) ;
        init();
        getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    public void onBackPressed() {
        if (searchFragment != null && !searchFragment.isHidden()) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.hide(searchFragment);
            tx.commit();
        } else {
            setResult(RESULT_CODE_NONE);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.sendEmptyMessage(GET_LINES_INFOS);
    }

    private void init() {
        // TODO 校赛视频，暂时注释
        // Map<String, ? extends BakeReport> bakeReports = ((MyApplication) getApplication()).getBakeReports();

        // bakeReportList.addAll(bakeReports.values()); // FIXME: 2017/3/9
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setLayoutManager(layoutManager);
        mAdapter = new HistoryLineAdapter(this, bakeReportList, new HistoryLineAdapter.OnLineClickedListener() {
            @Override
            public void onLineClicked(BakeReport bakeReport) {;
                Intent intent = new Intent();
                intent.putExtra("report", bakeReport);
                setResult(RESULT_CODE_ADD, intent);
                finish();
            }
        });
        listView.setAdapter(mAdapter);
        StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(mAdapter);
        listView.addItemDecoration(decoration);
        listView.addItemDecoration(new DividerDecoration(this));


        searchBar.setSearchBarListener(this);
        backButton.setOnClickListener(this);


        mHandler = new LinesSelectorHandler(this, mRefreshLayout, mAdapter);
        mHandler.setHandling(this);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(GET_LINES_INFOS);
            }
        });


        searchBar.setSearchBarListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lines_selected_del:
                searchBar.setText("");
                break;
            case R.id.iv_back:
                setResult(RESULT_CODE_EXIT);
                finish();
                overridePendingTransition(R.anim.in_fade, R.anim.out_to_right);
                break;
        }


    }

    public void back(View view) {
        setResult(RESULT_CODE_NONE);
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void starSearchPage() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);

        Bundle bundle = new Bundle();
        bundle.putSerializable("reportList", new ArrayList<>(bakeReportList));
        searchFragment.setArguments(bundle);
        tx.add(R.id.id_lines_container, searchFragment, "search_line");

        tx.commit();
    }


    @Override
    public void handling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String temp = HttpUtils.getStringFromServer(URLs.GET_ALL_BAKE_REPORT);
                    Type type = new TypeToken<Map<String, BakeReport>>() {
                    }.getType();
                    Map<String, BakeReport> bakeReports = new Gson().fromJson(temp, type);
                    // 清除上一次留下的数据
                    bakeReportList.clear();
                    // 重新添加数据
                    bakeReportList.addAll(bakeReports.values());
                    Collections.sort(bakeReportList, new Comparator<BakeReport>() {
                        @Override
                        public int compare(BakeReport o1, BakeReport o2) {
                            return (int) (Utils.date2IdWithTimestamp(o2.getDate()) - Utils.date2IdWithTimestamp(o1.getDate()));
                        }
                    });
                    // 请求成功
                    mHandler.sendEmptyMessage(LOADING_SUCCESS);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(LOADING_ERROR);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
