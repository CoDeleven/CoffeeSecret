package com.dhy.coffeesecret.ui.cup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.adapters.LinesAdapter;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.views.SearchEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_ADD;
import static com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment.RESULT_CODE_NONE;

public class LinesSelectedActivity extends AppCompatActivity implements View.OnClickListener, SearchEditText.SearchBarListener {

    private ListView listView;
    private SearchEditText searchBar;
    private SearchFragment searchFragment = new SearchFragment();
    private Toolbar toolbar;
    private boolean isAddSearchFragment = false;
    private List<BakeReport> bakeReportList = new ArrayList<>();
    private BakeReport currentReport;
    private ImageView backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cupping_lines);
        listView = (ListView) findViewById(R.id.id_lines_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar_device_activtiy);
        backButton = (ImageView) findViewById(R.id.iv_back);
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
            setResult(RESULT_CODE_NONE);
            finish();
        }
    }

    private void init() {
        // TODO 校赛视频，暂时注释
        Map<String, ? extends BakeReport> bakeReports = ((MyApplication) getApplication()).getBakeReports();
        // Map<String, ? extends BakeReport> bakeReports = TestData.getBakeReports(this);

        bakeReportList.addAll(bakeReports.values());// FIXME: 2017/3/9

        listView.setAdapter(new LinesAdapter(bakeReportList, this));
        View searchBarLayout = View.inflate(this, R.layout.conta_lines_searchbar_part, null);
        listView.addHeaderView(searchBarLayout);
        listView.setHeaderDividersEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentReport = (BakeReport) adapterView.getItemAtPosition(i);
                Intent intent = new Intent();
                intent.putExtra("report",currentReport);
                setResult(RESULT_CODE_ADD,intent);
                finish();
            }
        });

        searchBar = (SearchEditText) searchBarLayout.findViewById(R.id.lines_selected_srh);
        searchBar.setSearchBarListener(this);
        ImageButton imgBtn = (ImageButton) searchBarLayout.findViewById(R.id.lines_selected_del);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CODE_NONE);
                finish();
            }
        });
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

}
