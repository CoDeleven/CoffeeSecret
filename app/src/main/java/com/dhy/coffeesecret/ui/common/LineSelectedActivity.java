package com.dhy.coffeesecret.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;
import com.dhy.coffeesecret.utils.SystemStatusBarUtils;
import com.dhy.coffeesecret.ui.common.views.DividerDecoration;
import com.dhy.coffeesecret.ui.common.views.SearchEditText;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jesse.nativelogger.NLogger;

/**
 * Created by CoDeleven on 17-3-12.
 */

public abstract class LineSelectedActivity extends AppCompatActivity implements
        SearchEditText.SearchBarListener, OnItemClickListener,DefaultViewHandler.Handling {
    private static final String TAG = LineSelectedActivity.class.getSimpleName();
    @Bind(R.id.id_lines_list)
    RecyclerView listView;
    @Bind(R.id.id_swipeRefresh)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.id_back)
    ImageView back;
    @Bind(R.id.lines_selected_srh)
    SearchEditText searchBar;
    @Bind(R.id.id_toolbar_title)
    TextView toolbarTitle;
    private SearchFragment searchFragment;
    protected DefaultViewHandler mHandler;
    private RecyclerView.Adapter mAdapter;
    private boolean isAddSearchFragment = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.history_lines);
        SystemStatusBarUtils.steepToolBar(this);
        ButterKnife.bind(this);
        init();
        initTitle();
        mHandler.sendEmptyMessage(DefaultViewHandler.GET_DATA);
    }

    protected void initTitle(){
    }


    @Override
    public void onBackPressed() {
        if (searchFragment != null && !searchFragment.isHidden()) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.hide(searchFragment);
            tx.commit();
        } else {
            finish();
        }
    }

    // protected abstract void fetchDataFromServer();

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setLayoutManager(layoutManager);
        // mAdapter = new HistoryLineAdapter(this, getBatchData(getIntent().getExtras()), this);
        mAdapter = initAdapter();
        listView.setAdapter(mAdapter);
        if(mAdapter instanceof StickyRecyclerHeadersAdapter){
            StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter)mAdapter);
            listView.addItemDecoration(decoration);
        }
        listView.addItemDecoration(new DividerDecoration(this));
        mHandler = new DefaultViewHandler(this);
        mHandler.setRefreshLayout(mRefreshLayout);
        mHandler.setAdapter(mAdapter);
        mHandler.setHandling(this);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessage(DefaultViewHandler.GET_DATA);
            }
        });

        searchBar.setSearchBarListener(this);

    }

    protected abstract RecyclerView.Adapter<?> initAdapter();

    @OnClick({R.id.lines_selected_srh, R.id.id_back})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lines_selected_srh:
                searchBar.setText("");
                break;
            case R.id.id_back:
                finish();
                break;
        }
    }

    @Override
    public void startSearchPage() {
        NLogger.i(TAG, "启动SearchFragment...");
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();

        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }
        if(!isAddSearchFragment){
            searchFragment = newSearchFragment();
            tx.add(R.id.id_lines_container, searchFragment, "search_line");
            isAddSearchFragment = true;
        }else {
            tx.show(searchFragment);
        }
        tx.commit();
    }

    abstract protected SearchFragment newSearchFragment();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void doDataHandle() {

    }

    @Override
    public void doLoadingHandle() {

    }

    @Override
    public void doNoLoadingHandle() {

    }

    @Override
    public void doLoadingSuccessHandle() {

    }

    @Override
    public void doLoadingErrorHandle() {

    }
}
