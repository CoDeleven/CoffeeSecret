package com.dhy.coffeesecret;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.dhy.coffeesecret.ui.community.CommunityFragment;
import com.dhy.coffeesecret.ui.container.ContainerFragment;
import com.dhy.coffeesecret.ui.cup.CupFragment;
import com.dhy.coffeesecret.ui.device.DeviceFragment;
import com.dhy.coffeesecret.ui.mine.MineFragment;
import com.dhy.coffeesecret.utils.UIUtils;

public class MainActivity extends AppCompatActivity implements CupFragment.OnCupInteractionListener {

    // 默认图标id
    private static final int[] IMG_SELECTOR_IDS = {R.drawable.nav_container_selector, R.drawable.nav_cup_selector, R.drawable.nav_device_selector, R.drawable.nav_community_selector, R.drawable.nav_mine_selector};
    // 标签页
    private TabLayout mTabLayout;
    // 滑动页面视图
    private ViewPager mViewPager;
    // fragment集合 //
    private Fragment[] mFragments;
    private DeviceFragment deviceFragment;
    private ContainerFragment containerFragment;
    private CupFragment cupFragment;
    private CommunityFragment communityFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParam();

        UIUtils.steepToolBar(this);
    }

    /**
     * 初始化默认参数
     */
    private void initParam() {

        deviceFragment = new DeviceFragment();
        containerFragment = new ContainerFragment();
        cupFragment = new CupFragment();
        communityFragment = new CommunityFragment();
        mineFragment = new MineFragment();

        // 初始化fragment视图
        mFragments = new Fragment[]{containerFragment, cupFragment, deviceFragment, communityFragment, mineFragment};

        // 获取id
        mTabLayout = (TabLayout) findViewById(R.id.id_fragment_tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.id_fragment_viewPager);
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


        mViewPager.setCurrentItem(0);
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

    /**
     * MainActivity和MyCupFragment交互的方法
     *
     * @param uri
     */
    @Override
    public void onCupInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (containerFragment != null && containerFragment.isAddSearch()) {
            containerFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
