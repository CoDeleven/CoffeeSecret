package com.dhy.coffeesecret.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.User;
import com.dhy.coffeesecret.url.UrlLogin;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MineFragment extends Fragment {

    private static final int SUCCESS = 0x1;
    private static final String TAG = "MineFragment";

    @Bind(R.id.title_text)
    TextView titleText;
    //    @Bind(R.id.attention_number)
//    TextView attentionNumber;
//    @Bind(R.id.attention_layout)
//    LinearLayout attentionLayout;
//    @Bind(R.id.fans_number)
//    TextView fansNumber;
//    @Bind(R.id.fans_layout)
//    LinearLayout fansLayout;
//    @Bind(R.id.collection_number)
//    TextView collectionNumber;
//    @Bind(R.id.collection_layout)
//    LinearLayout collectionLayout;
    @Bind(R.id.mine_my_privacy)
    LinearLayout mineMyRecent;
    @Bind(R.id.mine_history_line)
    LinearLayout mineHistoryLine;
    @Bind(R.id.mine_settings)
    LinearLayout mineSettings;
    @Bind(R.id.mine_my_device)
    LinearLayout mineFeedBack;
    @Bind(R.id.mine_about_us)
    LinearLayout mineAboutUs;
    @Bind(R.id.fragment_mine)
    LinearLayout fragmentMine;
    @Bind(R.id.mine_head_img)
    RoundedImageView mineHeadImg;
    @Bind(R.id.mine_nick_name)
    TextView mineNickName;
    @Bind(R.id.mine_qr_code)
    ImageView qrCode;

    private Context mContext;
    private User user;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    mineNickName.setText(user.getUsername());
                    String token = application.getToken();
                    String avatar = UrlLogin.getAvatar(token);
                    ImageLoader.getInstance().displayImage(avatar, mineHeadImg);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private MyApplication application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        return view;
    }


    public void getUserInfo() {
        new Thread() {
            @Override
            public void run() {
                String token = ((MyApplication) getActivity().getApplication()).getToken();
                String info = UrlLogin.getUserInfo(token);
                try {
                    String userInfo = HttpUtils.getStringFromServer(info);
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd").setExclusionStrategies().create();
                    user = gson.fromJson(userInfo, User.class);
                    Log.d(TAG, user+"");
                    if(user != null){
                        mHandler.sendEmptyMessage(SUCCESS);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleText.setText("我的");
        application = (MyApplication) getActivity().getApplication();
        getUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x123 && resultCode == UserInfoActivity.UPDATE) {
            getUserInfo();
        }
    }

    //R.id.attention_layout, R.id.fans_layout, R.id.collection_layout
    @OnClick({R.id.mine_my_privacy, R.id.mine_history_line, R.id.mine_settings, R.id.mine_my_device, R.id.mine_about_us, R.id.mine_qr_code, R.id.mine_head_img})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
//            case R.id.attention_layout:
//                break;
//            case R.id.fans_layout:
//                break;
//            case R.id.collection_layout:
//                break;
            case R.id.mine_my_privacy:
                break;
            case R.id.mine_history_line:
                intent = new Intent(mContext, HistoryLineActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_my_device:
                break;
            case R.id.mine_settings:
                intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_about_us:
                intent = new Intent(mContext, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_qr_code:
                intent = new Intent(mContext, QRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_head_img:
                intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 0x123);
        }
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
