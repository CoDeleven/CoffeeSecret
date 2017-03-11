package com.dhy.coffeesecret.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.community.live.visitor.HWCameraStreamingActivity;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.UIUtils;


public class CommunityFragment extends Fragment implements View.OnClickListener {

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_community, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mView.findViewById(R.id.iv_business).setOnClickListener(this);
        mView.findViewById(R.id.iv_com).setOnClickListener(this);
        mView.findViewById(R.id.iv_live).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent ;
        switch (view.getId()) {
            case R.id.iv_com:
                intent = new Intent(getActivity(),CommActivity.class);
                startActivity(intent);//TODO
                break;
            case R.id.iv_business:
                T.showShort(getActivity(),"这是个商铺"); //TODO
                break;
            case R.id.iv_live:
                intent = new Intent(getActivity(), HWCameraStreamingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_fade);
                break;
        }
    }
}
