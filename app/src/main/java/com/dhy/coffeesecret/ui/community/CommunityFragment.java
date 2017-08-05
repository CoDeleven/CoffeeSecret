package com.dhy.coffeesecret.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.community.live.player.LiveListActivity;
import com.dhy.coffeesecret.ui.community.live.visitor.HWCameraStreamingActivity;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;

import java.io.IOException;



public class CommunityFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Intent intent;

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
        mView.findViewById(R.id.iv_live).setOnClickListener(this);
        mView.findViewById(R.id.iv_watch).setOnClickListener(this);
        mView.findViewById(R.id.iv_customer).setOnClickListener(this);
        mView.findViewById(R.id.iv_bbs).setOnClickListener(this);
        mView.findViewById(R.id.iv_watch).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_customer:
//                intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);//TODO
                break;
            case R.id.iv_business:

                intent = new Intent(getActivity(), SellActivity.class);
                startActivity(intent);//TODO
                break;
            case R.id.iv_bbs:
                intent = new Intent(getActivity(), CommActivity.class);
                startActivity(intent);//TODO
                break;
            case R.id.iv_live:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String videoUrl = "";
                        try {
                            videoUrl = HttpUtils.getStringFromServer(URLs.GET_LIVE_PUBLISH_PATH);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        intent = new Intent(getActivity(), HWCameraStreamingActivity.class);
                        intent.putExtra("video_url", videoUrl);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_fade);
                    }
                }).start();
                break;
            case R.id.iv_watch:
                intent = new Intent(getActivity(), LiveListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_fade);
                break;
        }
    }
}
