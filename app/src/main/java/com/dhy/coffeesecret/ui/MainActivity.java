package com.dhy.coffeesecret.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.bake.PreparationFragment;
import com.dhy.coffeesecret.ui.counters.ContainerFragment;
import com.dhy.coffeesecret.ui.cup.CupFragment;
import com.dhy.coffeesecret.ui.bake.BakeActivity;
import com.dhy.coffeesecret.ui.mine.MineFragment;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.SystemStatusBarUtils;

public class MainActivity extends AppCompatActivity {

    // 默认图标id  删除R.drawable.nav_community_selector,
    private static final int[] IMG_SELECTOR_IDS = {R.drawable.nav_container_selector, R.drawable.nav_cup_selector, R.drawable.nav_device_selector, R.drawable.nav_mine_selector};
    // 标签页
    private TabLayout mTabLayout;
    // 滑动页面视图
    private ViewPager mViewPager;
    // fragment集合 //
    private Fragment[] mFragments;
    private PreparationFragment deviceFragment;
    private ContainerFragment containerFragment;
    private CupFragment cupFragment;
    // private CommunityFragment communityFragment;
    private MineFragment mineFragment;
    private ImageView mBakingNow;
    // 上一次点击推退出的时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParam();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SystemStatusBarUtils.steepToolBar(this);
    }

    public void changeBakingTab() {
        mViewPager.setCurrentItem(0);
        mBakingNow.setVisibility(View.VISIBLE);
    }

    public void revertBakingTab(){
        // mViewPager.setCurrentItem(2);
        mBakingNow.setVisibility(View.GONE);
    }

    /**
     * 初始化默认参数
     */
    private void initParam() {

        deviceFragment = new PreparationFragment();
        containerFragment = new ContainerFragment();
        cupFragment = new CupFragment();
        // communityFragment = new CommunityFragment();
        mineFragment = new MineFragment();

        // 初始化fragment视图
        mFragments = new Fragment[]{containerFragment, cupFragment, deviceFragment, mineFragment};

        // 获取id
        mTabLayout = (TabLayout) findViewById(R.id.id_fragment_tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.id_fragment_viewPager);
        mBakingNow = (ImageView) findViewById(R.id.id_baking_now);

        mBakingNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BakeActivity.class);
                startActivity(intent);
                mViewPager.setCurrentItem(2);
                return;
            }
        });
        mBakingNow.setVisibility(View.GONE);


        mViewPager.setOffscreenPageLimit(4);
        // 为viewPager设置Adapter
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(getCustomerView(i));
        }

        mViewPager.setCurrentItem(2);
        // 设置tablayout固定
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 获取标签的自定义视图
     *
     * @param position
     * @return
     */
    private View getCustomerView(int position) {
        View view = getLayoutInflater().inflate(R.layout.tab_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_tab_img);

        imageView.setImageResource(IMG_SELECTOR_IDS[position]);
        return view;
    }

    @Override
    public void onBackPressed() {
        if ((containerFragment != null && containerFragment.isAddSearch())
                || (cupFragment != null && cupFragment.isAddSearch())) {

            if (containerFragment.isAddSearch()) {
                containerFragment.onBackPressed();
            }

            if (cupFragment.isAddSearch()) {
                cupFragment.onBackPressed();
            }

        } else {
            exitApp();
        }
    }

    /**
     * 双击退出事件
     */
    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            T.showShort(MainActivity.this, getResources().getString(R.string.exit_app));
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        }
    }
}
