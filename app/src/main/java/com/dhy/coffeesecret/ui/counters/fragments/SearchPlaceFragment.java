package com.dhy.coffeesecret.ui.counters.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.counters.adapters.InfoListAdapter;

import java.util.ArrayList;

/**
 * Created by CoDeleven on 17-8-25.
 */

public class SearchPlaceFragment extends SearchFragment {
    private InfoListAdapter infoListAdapter;
    private ArrayList<String> infos;
    private OnSearchCallBack onSearchCallBack;
    private ArrayList<String> infoTemp;
    @Override
    protected void initData(Bundle bundle) {
        infos = new ArrayList<>();
        infoTemp = bundle.getStringArrayList("infoList");

        /*if (infoTemp != null) {
            for (String info : infoTemp) {
                Log.i(TAG, "initData: " + info);
            }
        }*/

        infoListAdapter = new InfoListAdapter(getContext(), infos, new InfoListAdapter.OnInfoListClickListener() {
            @Override
            public void onInfoClicked(String item) {
                onSearchCallBack.onSearchCallBack(item);
            }
        });
    }

    public void addOnSearchCallBack(OnSearchCallBack onSearchCallBack) {
        this.onSearchCallBack = onSearchCallBack;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSearchList(infoListAdapter);
    }

    @Override
    protected void handleDataBySearchKey(String searchKey) {
        if (infoTemp != null) {
            infos.clear();

            for (String string : infoTemp) {
                if (string.toLowerCase().contains(searchKey)) {
                    infos.add(string);
                }
            }
            infoListAdapter.notifyDataSetChanged();
        }
    }
}
