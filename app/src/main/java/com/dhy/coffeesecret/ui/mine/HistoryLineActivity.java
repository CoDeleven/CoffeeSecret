package com.dhy.coffeesecret.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.device.ReportActivity;
import com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.dhy.coffeesecret.views.SearchEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryLineActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener,LinesSelectorHandler.Handling {

    public static String REFER_LINE = "refer_line";
    @Bind(R.id.id_lines_list)
    RecyclerView listView;
    @Bind(R.id.id_back)
    ImageView back;
    @Bind(R.id.id_swipeRefresh)
    SwipeRefreshLayout mRefreshLayout;
    private HistoryLineAdapter mAdapter;
    private SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private boolean isAddSearchFragment = false;
    private List<BakeReport> bakeReportList = new ArrayList<>();
    private LinesSelectorHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_lines);
        ButterKnife.bind(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar_device_activtiy);
        init();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (searchFragment != null && !searchFragment.isHidden()) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.hide(searchFragment);
            tx.commit();
        } else {
            finish();
        }
    }

    private void init() {
        /*
         *从intent中获取烘焙报告对象 如果没有 则加载MyApplication中的bakeReports  by mxf 2017-03-22
         */
        List<BakeReport> reports = (List<BakeReport>) getIntent().getSerializableExtra("bakeReports");
        Map<String, ? extends BakeReport> bakeReports;
        if(reports == null){
            bakeReports = ((MyApplication) getApplication()).getBakeReports();
            bakeReportList.addAll(bakeReports.values());
        }else {
            bakeReportList.addAll(reports);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setLayoutManager(layoutManager);
        mAdapter = new HistoryLineAdapter(this, bakeReportList, new HistoryLineAdapter.OnLineClickedListener() {
            @Override
            public void onLineClicked(BakeReport bakeReport) {
                ((MyApplication) (getApplication())).setBakeReport(bakeReport);

                Intent intent = new Intent(HistoryLineActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
        listView.setAdapter(mAdapter);
        StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(mAdapter);
        listView.addItemDecoration(decoration);
        listView.addItemDecoration(new DividerDecoration(this));

        mHandler = new LinesSelectorHandler(this, mRefreshLayout, mAdapter);
        mHandler.setHandling(this);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(LOADING_SUCCESS);
            }
        });
    }

    @Override
    public void onClick(View view) {
        searchBar.setText("");
    }

    @Override
    public void starSearchPage() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);

        if (!isAddSearchFragment) {
            Bundle bundle = new Bundle();
            // bundle.putSerializable("reportList", (Serializable) getDatas());
            searchFragment.setArguments(bundle);
            tx.add(R.id.id_lines_container, searchFragment, "search_line");
            isAddSearchFragment = true;
        } else {
            tx.show(searchFragment);
        }
        tx.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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