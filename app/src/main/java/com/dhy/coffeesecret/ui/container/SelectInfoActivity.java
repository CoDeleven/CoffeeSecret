package com.dhy.coffeesecret.ui.container;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.T;

import java.lang.ref.WeakReference;

public class SelectInfoActivity extends AppCompatActivity {

    private String infoType;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_info);

        context = SelectInfoActivity.this;

        initInfoList();
    }

    private void initInfoList() {
        infoType = getIntent().getStringExtra("info_type");

        switch (infoType) {
            case "area":
                mHandler.sendEmptyMessage(GET_AREA_LIST);
                break;
            case "manor":
                mHandler.sendEmptyMessage(GET_MANOR_LIST);
                break;
            case "species":
                mHandler.sendEmptyMessage(GET_SPECIES_LIST);
                break;
            default:
                mHandler.sendEmptyMessage(SHOW_WRONG_TOAST);
                break;
        }
    }

    private static final int GET_AREA_LIST = 111;
    private static final int GET_MANOR_LIST = 222;
    private static final int GET_SPECIES_LIST = 333;
    private static final int SHOW_WRONG_TOAST = 444;
    private SelectHandler mHandler = new SelectHandler(SelectInfoActivity.this);

    private class SelectHandler extends Handler {
        private final WeakReference<SelectInfoActivity> mActivity;

        public SelectHandler(SelectInfoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SelectInfoActivity activity = mActivity.get();
            switch (msg.what) {
                case GET_AREA_LIST:
                    T.showShort(activity.context, "GET_AREA_LIST");
                    break;
                case GET_MANOR_LIST:
                    T.showShort(activity.context, "GET_MANOR_LIST");
                    break;
                case GET_SPECIES_LIST:
                    T.showShort(activity.context, "GET_SPECIES_LIST");
                    break;
                case SHOW_WRONG_TOAST:
                    T.showShort(activity.context, "网络连接失败");
                default:
                    break;
            }
        }
    }
}
