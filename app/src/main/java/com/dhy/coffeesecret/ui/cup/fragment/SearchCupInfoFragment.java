package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.cup.NewCuppingActivity;
import com.dhy.coffeesecret.ui.cup.adapter.CuppingListAdapter;
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;

import java.util.ArrayList;

import static com.dhy.coffeesecret.ui.cup.CupFragment.REQ_CODE_EDIT;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.SHOW_INFO;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.TARGET;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.VIEW_TYPE;

/**
 * Created by CoDeleven on 17-8-25.
 */

public class SearchCupInfoFragment extends SearchFragment implements OnItemClickListener{
    private ArrayList<CuppingInfo> cuppingInfos;
    private ArrayList<CuppingInfo> cuppingInfosTemp;
    private CuppingListAdapter cuppingListAdapter;

    @Override
    public void onItemClick(Parcelable parcelable) {
        Intent intent = new Intent(getContext(), NewCuppingActivity.class);
        intent.putExtra(TARGET, parcelable);
        intent.putExtra(VIEW_TYPE, SHOW_INFO);
        startActivityForResult(intent, REQ_CODE_EDIT);
    }

    @Override
    protected void initData(Bundle bundle) {
        cuppingInfos = new ArrayList<>();
        cuppingInfosTemp =  bundle.getParcelableArrayList(UniExtraKey.EXTRA_CUP_INFO_LIST.getKey());
        if(getResultClickListenr() == null){
            setOnResultClickListenr(this);
        }
        cuppingListAdapter = new CuppingListAdapter(getContext(), cuppingInfos, getResultClickListenr());
    }

    @Override
    protected void handleDataBySearchKey(String searchKey) {
        if (cuppingInfosTemp != null) {
            cuppingInfos.clear();
            for (CuppingInfo cuppingInfo : cuppingInfosTemp) {
                if (cuppingInfo.getName().toLowerCase().contains(searchKey)) {
                    cuppingInfos.add(cuppingInfo);
                }
            }
            cuppingListAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSearchList(cuppingListAdapter);
    }
}
