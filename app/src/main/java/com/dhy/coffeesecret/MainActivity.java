package com.dhy.coffeesecret;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dhy.coffeesecret.ui.container.ContainerFragment;
import com.dhy.coffeesecret.ui.cup.CupFragment;
import com.dhy.coffeesecret.ui.device.DeviceFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceFragment.OnDeviceInteractionListener,
        ContainerFragment.OnContainerInteractionListener, CupFragment.OnCupInteractionListener{

    // 标签页
    private TabLayout mTabLayout;
    // 默认图标id
    private int[] imgSelectorIds = {R.drawable.nav_device_selector, R.drawable.nav_container_selector, R.drawable.nav_cup_selector};
    // 滑动页面视图
    private ViewPager mViewPager;
    // fragment集合 //
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParam();
    }

    /**
     * 初始化默认参数
     */
    private void initParam(){
        // 初始化fragment视图

        mFragments =new Fragment[] {new DeviceFragment(),new ContainerFragment(),new CupFragment()};
//        mFragments.add(new DeviceFragment());
//        mFragments.add(new ContainerFragment());
//        mFragments.add(new CupFragment());

        // 获取id
        mTabLayout = (TabLayout)findViewById(R.id.id_fragment_tabLayout);
        mViewPager = (ViewPager)findViewById(R.id.id_fragment_viewPager);

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

        mTabLayout.getTabAt(0).setCustomView(getCustomerView(0));
        mTabLayout.getTabAt(1).setCustomView(getCustomerView(1));
        mTabLayout.getTabAt(2).setCustomView(getCustomerView(2));
        mViewPager.setCurrentItem(0);
        // 设置tablayout固定
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

//        // 默认页面滑动监听器
//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
//        // 设置首页
//        mViewPager.setCurrentItem(0);
//
//        // 设置tablayout固定
//        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
//        // 设置tablayout布局布满
//        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        // 添加新的三个标签页
//        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getCustomerView(0)));
//        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getCustomerView(1)));
//        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getCustomerView(2)));
//
//        // 监听标签页单击事件
//        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition(), true);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }

    /**
     * 获取标签的自定义视图
     * @param position
     * @return
     */
    private View getCustomerView(int position){
        View view = getLayoutInflater().inflate(R.layout.tab_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_tab_img);
        imageView.setImageResource(imgSelectorIds[position]);
        return view;
    }

    /**
     * MainActivity和MyDeviceFragment交互的方法
     * @param uri
     */
    @Override
    public void onDeviceInteraction(Uri uri) {

    }

    /**
     * MainActivity和MyContainerFragment交互的方法
     * @param uri
     */
    @Override
    public void onContainerInteraction(Uri uri) {

    }

    /**
     * MainActivity和MyCupFragment交互的方法
     * @param uri
     */
    @Override
    public void onCupInteraction(Uri uri) {

    }
}
