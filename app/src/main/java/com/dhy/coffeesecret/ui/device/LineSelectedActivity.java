package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.mine.HistoryLineActivity;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.dhy.coffeesecret.views.SearchEditText;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_ADD;

/**
 * Created by CoDeleven on 17-3-12.
 */

public class LineSelectedActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener {
    private RecyclerView listView;
    private ImageView back;
    private SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private boolean isAddSearchFragment = false;
    private List<BakeReport> bakeReportList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_lines);
        listView = (RecyclerView) findViewById(R.id.id_lines_list);
        back = (ImageView) findViewById(R.id.id_back);
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
        // TODO 校赛视频，暂时注释
        // Map<String, ? extends BakeReport> bakeReports = ((MyApplication) getApplication()).getBakeReports();
        Map<String, ? extends BakeReport> bakeReports = TestData.getBakeReports(this);

        bakeReportList.addAll(bakeReports.values());
        Log.e("codelevex", bakeReports.size() + "");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setLayoutManager(layoutManager);
        HistoryLineAdapter adapter = new HistoryLineAdapter(this, bakeReportList, new HistoryLineAdapter.OnLineClickedListener() {
            @Override
            public void onLineClicked(BakeReport bakeReport) {
                // TODO 校赛专用
                // ((MyApplication)(getApplication())).setBakeReport(report);

                TestData.setBakeReport(bakeReport);
                Intent intent = new Intent();
                intent.putExtra("report",bakeReport);
                setResult(RESULT_CODE_ADD,intent);
                finish();
            }
        });
        listView.setAdapter(adapter);
        StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(adapter);
        listView.addItemDecoration(decoration);
        listView.addItemDecoration(new DividerDecoration(this));

/*        View searchBarLayout = View.inflate(this, R.layout.conta_lines_searchbar_part, null);
        searchBar = (SearchEditText) searchBarLayout.findViewById(R.id.lines_selected_srh);
        searchBar.setSearchBarListener(this);
        ImageButton imgBtn = (ImageButton) searchBarLayout.findViewById(R.id.lines_selected_del);

        imgBtn.setOnClickListener(this);*/
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
}
