package com.dhy.coffeesecret;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.pojo.SQLiteHelper;
import com.dhy.coffeesecret.utils.SPPrivateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

/**
 * 引导界面
 * Created by Simo on 2016/2/25.
 */
public class GuidanceActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GuidanceActivity";
    private ViewPager viewPager = null;
    private MyPagerAdapter pagerAdapter = null;
    private CircleIndicator indicator = null;
    private int[] imgs = new int[]{
            R.mipmap.guidance_1, R.mipmap.guidance_2, R.mipmap.guidance_3, R.mipmap.guidance_4, R.mipmap.guidance_5
    };

    private List<Map<String, Integer>> changeList = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_guidance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);



        viewPager = (ViewPager) super.findViewById(R.id.view_pager);
        indicator = (CircleIndicator) super.findViewById(R.id.indicator);
        context = GuidanceActivity.this;

        /*Button startBtn = (Button) super.findViewById(R.id.start_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPPrivateUtils.put(context, State.isFirstTime, true);
                Intent intent = new Intent(GuidanceActivity.this, PairingActivity.class);
                GuidanceActivity.this.startActivity(intent);
                GuidanceActivity.this.finish();
            }
        });*/

        initChangeList();   //初始化viewpager需要用到的图片
        pagerAdapter = new MyPagerAdapter(GuidanceActivity.this, changeList);   //创建一个adapter
        viewPager.setAdapter(pagerAdapter);     //设置适配器
        indicator.setViewPager(viewPager);      //设置viewpager指示器，底部小圆点
    }

    private void initChangeList() {
        changeList = new ArrayList<>();

        Map<String, Integer> imgsMap = new HashMap<>();
        for (int i = 0; i < imgs.length; i++) {
            imgsMap.put("imgs_" + i, imgs[i]);
        }
        Log.i(TAG, imgsMap.toString());
        changeList.add(imgsMap);
    }

    //点击跳过，跳转到配对界面
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
                Log.i(TAG, "skip");
                SPPrivateUtils.put(context, Global.IS_FIRST_TIME, false);
                Intent intent = new Intent(this, FirstConnectedActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private List<View> views = null;
        private List<Map<String, Integer>> list = null;
        private Context context = null;
        private LayoutInflater mLayoutInflater = null;

        public MyPagerAdapter(Context context, List<Map<String, Integer>> list) {
            this.context = context;
            this.list = list;
            this.mLayoutInflater = LayoutInflater.from(context);
            initViewPager();
        }

        private void initViewPager() {
            views = new ArrayList<>();
            for (int i = 0; i < list.get(0).size(); i++) {
                View view = mLayoutInflater.inflate(R.layout.item_pager_img, null);
                ImageView pagerImg = (ImageView) view.findViewById(R.id.pager_img);
                pagerImg.setImageResource(list.get(0).get("imgs_" + i));
                views.add(view);
            }
            View view = mLayoutInflater.inflate(R.layout.item_guidance_last, null);
            Button startBtn = (Button) view.findViewById(R.id.start_btn);
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SPPrivateUtils.put(context, Global.IS_FIRST_TIME, false);
                    // TODO: 2017/3/13 这边需要把MainActivity改为连接蓝牙的界面
                    Intent intent = new Intent(GuidanceActivity.this, FirstConnectedActivity.class);
                    GuidanceActivity.this.startActivity(intent);
                    GuidanceActivity.this.finish();
                }
            });
            views.add(view);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));

            return views.get(position);
        }
    }
}
