package com.dhy.coffeesecret.ui.community.live.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.community.live.player.adapter.LiveListAdapter;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LiveListActivity extends AppCompatActivity {

    @Bind(R.id.live_list)
    RecyclerView liveList;
    @Bind(R.id.activity_live_list)
    RelativeLayout activityLiveList;
    @Bind(R.id.title_text)
    TextView titleText;

    private Context context;
    private LiveListAdapter liveListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);
        ButterKnife.bind(this);

        context = LiveListActivity.this;

        init();
    }

    private void init() {
        titleText.setText("直播列表");

        LinearLayoutManager manager = new LinearLayoutManager(LiveListActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        ArrayList<String> list = new ArrayList<String>();
        list.add("陈灏正在直播");

        liveListAdapter = new LiveListAdapter(context, list, new LiveListAdapter.OnLiveClickListener() {
            @Override
            public void onLiveClickListener(int position) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String playUrl = HttpUtils.getStringFromServer(URLs.GET_LIVE_PLAY_PATH);
                            Intent intent = new Intent(context, LivePlayActivity.class);
                            intent.putExtra("play_url", playUrl);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_fade);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        liveList.setLayoutManager(manager);
        liveList.setAdapter(liveListAdapter);
    }
}
