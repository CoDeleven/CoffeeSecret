package com.dhy.coffeesecret.ui.container.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.adapters.BeanListAdapter;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/12.
 */

public class BeanListFragment extends Fragment {

    private static final String TAG = "BeanListFragment";
    private final String[] beanLst = {"All", "Asia", "Africa", "Baby", "Central American", "Death", "Destroy"
            , "E", "Fate", "Great", "Grand", "Handsome", "I", "Joker", "King", "Luna", "Morning", "North American"
            , "Oceania", "Other", "Person", "Queen", "Read", "Real", "Strange", "T"};
    private View beanListView;
    private RecyclerView beanListRecycler;
    private Context context;
    private String title;

    public BeanListFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        beanListRecycler = (RecyclerView) beanListView.findViewById(R.id.bean_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        beanListRecycler.setLayoutManager(layoutManager);

        ArrayList<BeanInfo> coffeeBeanInfos = new ArrayList<>();
        for (String name : beanLst) {
            BeanInfo beanInfo = new BeanInfo();
            beanInfo.setName(name);
            coffeeBeanInfos.add(beanInfo);
        }
        BeanListAdapter adapter = new BeanListAdapter(context, coffeeBeanInfos);

        beanListRecycler.setAdapter(adapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
        beanListRecycler.addItemDecoration(headersDecoration);
        beanListRecycler.addItemDecoration(new DividerDecoration(context));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beanListView = inflater.inflate(R.layout.fragment_bean_list, container, false);
        Log.d(TAG, "onCreateView: " + getActivity());
        this.context = getContext();
        return beanListView;
    }
}
