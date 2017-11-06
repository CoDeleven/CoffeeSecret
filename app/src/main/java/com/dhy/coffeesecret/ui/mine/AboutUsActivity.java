package com.dhy.coffeesecret.ui.mine;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.BaseActivity;
import com.dhy.coffeesecret.R;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.btn_back)
    ImageView btnBack;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.app_icon)
    RoundedImageView appIcon;
    @Bind(R.id.about_to_score)
    LinearLayout aboutToScore;
    @Bind(R.id.about_function_introduction)
    LinearLayout aboutFunctionIntroduction;
    @Bind(R.id.about_help)
    LinearLayout aboutHelp;
    @Bind(R.id.about_feed_back)
    LinearLayout aboutFeedBack;
    @Bind(R.id.about_check_update)
    LinearLayout aboutCheckUpdate;
    @Bind(R.id.activity_about_us)
    LinearLayout activityAboutUs;
    @Bind(R.id.about_version_code)
    TextView aboutVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleText.setText("关于我们");
        try {
            aboutVersionCode.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_back, R.id.about_to_score, R.id.about_function_introduction, R.id.about_help, R.id.about_feed_back, R.id.about_check_update, R.id.activity_about_us})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                overridePendingTransition(R.anim.out_to_left, R.anim.in_from_right);
                break;
            case R.id.about_to_score:
                break;
            case R.id.about_function_introduction:
                break;
            case R.id.about_help:
                break;
            case R.id.about_feed_back:
                break;
            case R.id.about_check_update:
                break;
            case R.id.activity_about_us:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
    }
}
