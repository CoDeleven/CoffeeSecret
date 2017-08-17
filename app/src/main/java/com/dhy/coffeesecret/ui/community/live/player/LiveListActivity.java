package com.dhy.coffeesecret.ui.community.live.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.community.live.player.adapter.LiveListAdapter;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SPPrivateUtils;
import com.dhy.coffeesecret.url.UrlLogin;

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
    @Bind(R.id.btn_back)
    ImageView backBtn;


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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(LiveListActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        final ArrayList<String> list = new ArrayList<>();
        list.add("叶老板正在直播");
        list.add("磊少正在直播");
        list.add("杨大咖正在直播");

        liveListAdapter = new LiveListAdapter(context, list, new LiveListAdapter.OnLiveClickListener() {
            @Override
            public void onLiveClickListener(final int position) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String playUrl = HttpUtils.getStringFromServer(UrlLogin.GET_LIVE_PLAY_PATH);
                            Intent intent = new Intent(context, LivePlayActivity.class);
                            SPPrivateUtils.put(context, "liveName", list.get(position));
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
