package com.dhy.coffeesecret.ui.container.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.interfaces.ToolbarOperation;
import com.dhy.coffeesecret.model.BakeReport;
import com.dhy.coffeesecret.ui.container.adapters.LinesAdapter;
import com.dhy.coffeesecret.ui.device.DeviceChildActivity;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.views.SearchEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LinesSelectedFragment extends Fragment implements View.OnClickListener, SearchEditText.SearchBarListener {

    private ToolbarOperation toolbarOperation;
    private ListView listView;
    private SearchEditText searchBar;
    private SearchFragment searchFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.conta_lines, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listView = (ListView) getActivity().findViewById(R.id.id_lines_list);
        init();
        searchFragment = (SearchFragment) FragmentTool.getFragmentToolInstance(getActivity()).getFragment(DeviceChildActivity.FRAGMENT_TAG[2]);
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
    }

    public void setToolbarOperation(ToolbarOperation toolbarOperation) {
        this.toolbarOperation = toolbarOperation;
    }

    private void init() {
        listView.setAdapter(new LinesAdapter(getDatas(), getContext()));
        View searchBarLayout = View.inflate(getContext(), R.layout.conta_lines_searchbar_part, null);
        listView.addHeaderView(searchBarLayout);
        listView.setHeaderDividersEnabled(false);

        searchBar = (SearchEditText) searchBarLayout.findViewById(R.id.lines_selected_srh);
        searchBar.setSearchBarListener(this);
        ImageButton imgBtn = (ImageButton) searchBarLayout.findViewById(R.id.lines_selected_del);

        imgBtn.setOnClickListener(this);
    }

    private List<BakeReport> getDatas() {
        List<BakeReport> reportList = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            BakeReport report = new BakeReport();
            report.setName(i + " -> " + Math.random() * 100);
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

        searchFragment.setToolbarOperation(toolbarOperation);
        FragmentTool.getFragmentToolInstance(getActivity()).add(R.id.id_device_child, searchFragment, false, DeviceChildActivity.FRAGMENT_TAG[2]);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
