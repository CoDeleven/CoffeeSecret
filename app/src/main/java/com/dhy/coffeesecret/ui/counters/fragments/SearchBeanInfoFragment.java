package com.dhy.coffeesecret.ui.counters.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;
import com.dhy.coffeesecret.ui.counters.BeanInfoActivity;
import com.dhy.coffeesecret.ui.counters.adapters.BeanListAdapter;

import java.util.ArrayList;

import cn.jesse.nativelogger.NLogger;

import static com.dhy.coffeesecret.model.UniExtraKey.EXTRA_BAKE_REPORT_LIST;
import static com.dhy.coffeesecret.model.UniExtraKey.EXTRA_BEAN_INFO;

/**
 * Created by CoDeleven on 17-8-25.
 */

public class SearchBeanInfoFragment extends SearchFragment implements OnItemClickListener{
    private static final String TAG = SearchBeanInfoFragment.class.getSimpleName();
    private BeanListAdapter beanListAdapter;
    private ArrayList<BeanInfo> beanInfos;
    private ArrayList<BeanInfo> beanInfoTemp;
    @Override
    public void onItemClick(Parcelable parcelable) {
        Intent intent = new Intent(getContext(), BeanInfoActivity.class);
        intent.putExtra(EXTRA_BEAN_INFO.getKey(), parcelable);
        startActivity(intent);
        remove();
    }

    @Override
    protected void initData(Bundle bundle) {
        beanInfos = new ArrayList<>();
        beanInfoTemp = bundle.getParcelableArrayList(EXTRA_BAKE_REPORT_LIST.getKey());

        if(getResultClickListenr() == null){
            setOnResultClickListenr(this);
        }
        beanListAdapter = new BeanListAdapter(getContext(), beanInfos, getResultClickListenr());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSearchList(beanListAdapter);
    }

    @Override
    protected void handleDataBySearchKey(String searchKey) {
        NLogger.i(TAG, "搜索豆种的关键词为：" + searchKey);
        if (beanInfoTemp != null) {
            beanInfos.clear();
            if(!"".equals(searchKey)){
                for (BeanInfo beanInfo : beanInfoTemp) {
                    if (beanInfo.getArea().toLowerCase().contains(searchKey)) {
                        beanInfos.add(beanInfo);
                    }
                }
            }
            beanListAdapter.notifyDataSetChanged();
        }
        showNoResultTips(beanInfos != null && beanInfos.size() == 0);
    }
}
