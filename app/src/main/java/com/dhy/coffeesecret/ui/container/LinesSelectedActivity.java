package com.dhy.coffeesecret.ui.container;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.adapters.LinesAdapter;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.device.ReportActivity;
import com.dhy.coffeesecret.views.SearchEditText;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LinesSelectedActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener {

    private ListView listView;
    private SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private boolean isAddSearchFragment = false;
    private List<BakeReport> bakeReportList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conta_lines);
        listView = (ListView) findViewById(R.id.id_lines_list);
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
        Map<String, ? extends BakeReport> bakeReports = ((MyApplication) getApplication()).getBakeReports();

        bakeReportList.addAll(bakeReports.values());
        Log.e("codelevex", bakeReports.size() + "");

        listView.setAdapter(new LinesAdapter(bakeReportList, this));
        View searchBarLayout = View.inflate(this, R.layout.conta_lines_searchbar_part, null);
        listView.addHeaderView(searchBarLayout);
        listView.setHeaderDividersEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BakeReport report = (BakeReport) adapterView.getItemAtPosition(i);
                ((MyApplication)(getApplication())).setBakeReport(report);
                Intent intent = new Intent(LinesSelectedActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        searchBar = (SearchEditText) searchBarLayout.findViewById(R.id.lines_selected_srh);
        searchBar.setSearchBarListener(this);
        ImageButton imgBtn = (ImageButton) searchBarLayout.findViewById(R.id.lines_selected_del);

        imgBtn.setOnClickListener(this);
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
