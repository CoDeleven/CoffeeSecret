package com.dhy.coffeesecret.ui.community.live.player;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhy.coffeesecret.R;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;


public class LivePlayActivity extends AppCompatActivity implements
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnCompletionListener {


    private PLVideoView mVideoView;
    private ImageView liveplayCloseIv;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play);

        mVideoView = (PLVideoView) findViewById(R.id.pl_video_view);
        liveplayCloseIv = (ImageView) findViewById(R.id.liveplay_close_iv);

        liveplayCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        MediaController mMediaController = new MediaController(this);

        mVideoView.setMediaController(mMediaController);

        View loadingView = findViewById(R.id.loading_view);
        mVideoView.setBufferingIndicator(loadingView);

        // 设置监听
        mVideoView.setOnPreparedListener(this);     // 预备状态监听
        mVideoView.setOnInfoListener(this);         // 播放过程中信息监听
        mVideoView.setOnCompletionListener(this);   // 播放完成监听

        // 设置画面预览方式
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_16_9);
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_4_3);

        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

        url = getIntent().getStringExtra("play_url");
        if (url == null) {
            url = "rtmp://pili-live-rtmp.cloudself.cn/coffeesecret/javasdkexample1489147454866BB";
        }

        mVideoView.setVideoPath(url);
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        mVideoView.start();
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        Toast.makeText(this, "播放结束", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
        switch (what) {
            case 702:
                Toast.makeText(this, "缓冲结束", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.stopPlayback();
    }

    private void exit() {
        this.finish();
        overridePendingTransition(R.anim.in_fade, R.anim.out_to_bottom);
    }
}
