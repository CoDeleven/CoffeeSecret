package com.dhy.coffeesecret.ui.container.fragments;

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

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.adapters.LinesAdapter;
import com.dhy.coffeesecret.ui.device.fragments.ReportActivity;
import com.dhy.coffeesecret.views.SearchEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LinesSelectedActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener {

    private ListView listView;
    private SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private boolean isAddSearchFragment = false;

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
        Log.e("codelevex", "被按下");
    }

    private void init() {
        listView.setAdapter(new LinesAdapter(getDatas(), this));
        View searchBarLayout = View.inflate(this, R.layout.conta_lines_searchbar_part, null);
        listView.addHeaderView(searchBarLayout);
        listView.setHeaderDividersEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BakeReport report = (BakeReport) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(LinesSelectedActivity.this, ReportActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bakeReport", report);
                startActivity(intent);
            }
        });

        searchBar = (SearchEditText) searchBarLayout.findViewById(R.id.lines_selected_srh);
        searchBar.setSearchBarListener(this);
        ImageButton imgBtn = (ImageButton) searchBarLayout.findViewById(R.id.lines_selected_del);

        imgBtn.setOnClickListener(this);
    }

    private List<BakeReport> getDatas() {
        List<BakeReport> reportList = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            BakeReport report = new BakeReport();
            report.setBeanName(i + " -> " + Math.random() * 100);
            report.setBakeDate(new Date());
            reportList.add(report);
        }
        return reportList;
    }

    @Override
    public void onClick(View view) {
        searchBar.setText("");
    }

    @Override
    public void starSearchPage() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if (!isAddSearchFragment) {
            tx.add(R.id.id_lines_container, searchFragment, "search_line");
            isAddSearchFragment = true;
        } else {
            tx.show(searchFragment);
        }
        tx.commit();
    }

}
