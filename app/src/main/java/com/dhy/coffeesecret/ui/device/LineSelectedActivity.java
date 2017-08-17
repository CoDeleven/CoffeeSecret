package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;
import com.dhy.coffeesecret.url.UrlBake;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.UIUtils;
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

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_ADD;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.GET_LINES_INFOS;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.LOADING_ERROR;
import static com.dhy.coffeesecret.ui.device.handler.LinesSelectorHandler.LOADING_SUCCESS;

/**
 * Created by CoDeleven on 17-3-12.
 */

public class LineSelectedActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener, LinesSelectorHandler.Handling {
    @Bind(R.id.id_lines_list)
    RecyclerView listView;
    @Bind(R.id.id_swipeRefresh)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.id_back)
    ImageView back;
    @Bind(R.id.lines_selected_srh)
    SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private List<BakeReport> bakeReportList = new ArrayList<>();
    private LinesSelectorHandler mHandler;
    private HistoryLineAdapter mAdapter;
    private boolean isAddSearchFragment = false;
    private MyApplication application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.history_lines);

        application = (MyApplication) getApplication();
        UIUtils.steepToolBar(this);
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

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.sendEmptyMessage(GET_LINES_INFOS);
    }

    private void init() {
        // Map<String, ? extends BakeReport> bakeReports = ((MyApplication) getApplication()).getBakeReports();

        // bakeReportList.addAll(bakeReports.values());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setLayoutManager(layoutManager);
        mAdapter = new HistoryLineAdapter(this, bakeReportList, new HistoryLineAdapter.OnLineClickedListener() {
            @Override
            public void onLineClicked(BakeReport bakeReport) {
                ((MyApplication) (getApplication())).setBakeReport(bakeReport);

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
        searchBar.setText("");
    }

    @Override
    public void starSearchPage() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }

        if(!isAddSearchFragment){
            Bundle bundle = new Bundle();
            bundle.putSerializable("reportList", new ArrayList<>(bakeReportList));
            searchFragment.setArguments(bundle);
            tx.add(R.id.id_lines_container, searchFragment, "search_line");
            isAddSearchFragment = true;
        }else {
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
                    String all = UrlBake.getAll(application.getToken());
                    String temp = HttpUtils.getStringFromServer(all);
                    Type type = new TypeToken<Map<String, BakeReport>>() {
                    }.getType();
                    Map<String, BakeReport> bakeReports = new Gson().fromJson(temp, type);
                    // 清除上一次留下的数据
                    bakeReportList.clear();
                    // 重新添加数据
                    bakeReportList.addAll(bakeReports.values());
                    // 进行排序
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
