package com.dhy.coffeesecret.ui.bake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.dhy.coffeesecret.BaseActivity;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.bake.fragments.BakeBeanListFragment;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;
import com.dhy.coffeesecret.ui.common.views.SearchEditText;
import com.dhy.coffeesecret.ui.counters.EditBeanActivity;
import com.dhy.coffeesecret.ui.counters.fragments.SearchBeanInfoFragment;

import java.util.ArrayList;
import java.util.List;

import static com.dhy.coffeesecret.ui.bake.fragments.BakeDialog.SELECT_BEAN;

public class BeanChoiceActivity extends BaseActivity implements
        BakeBeanListFragment.OnBeanSelected, OnItemClickListener {
    @Override
    public void onItemClick(Parcelable parcelable) {
        Intent intent = new Intent();
        intent.putExtra(UniExtraKey.EXTRA_BEAN_INFO.getKey(), parcelable);
        setResult(RESULT_OK, intent);
        finish();
    }

    private static final String TAG = "BeanChoiceActivity";
    private static final int ADD_BEAN = 111;
    private final String[] TITLES = {"全部", "中美", "南美", "大洋", "亚洲", "非洲", "其它"};

    private SearchEditText searchBeanET = null;
    private LinearLayout btnAddBean = null;
    private ViewPager containerPager = null;
    private PagerSlidingTabStrip containerTabs = null;

    private List<BakeBeanListFragment> fragments = null;
    private boolean isAddSearchFragment = false;
    private SearchFragment searchFragment;

    public BeanChoiceActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_bean_selected);

        searchBeanET = (SearchEditText) findViewById(R.id.search_bean);
        btnAddBean = (LinearLayout) findViewById(R.id.btn_add_bean);
        containerTabs = (PagerSlidingTabStrip) findViewById(R.id.container_tabs);
        containerPager = (ViewPager) findViewById(R.id.container_pager);
        initView();

        initPagerData();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void initView() {
        searchBeanET.setSearchBarListener(new SearchEditText.SearchBarListener() {
            @Override
            public void startSearchPage() {
                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                if (searchFragment != null) {
                    isAddSearchFragment = !searchFragment.isRemoved();
                }
                if (!isAddSearchFragment) {
                    searchFragment = new SearchBeanInfoFragment();
                    searchFragment.setOnResultClickListenr(BeanChoiceActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey(), new ArrayList<Parcelable>(fragments.get(0).getBeaninfoList()));
                    searchFragment.setArguments(bundle);
                    tx.add(R.id.activity_dialog_bean_selected, searchFragment, "search_bean");
                    isAddSearchFragment = true;
                } else {
                    tx.show(searchFragment);
                }
                tx.commit();
            }
        });

        btnAddBean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeanChoiceActivity.this, EditBeanActivity.class);
                startActivityForResult(intent, ADD_BEAN);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        containerPager.setOffscreenPageLimit(6);
        containerPager.setAdapter(new BeanChoiceActivity.MyPagerAdapter((this).getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        containerTabs.setTextColor(getResources().getColor(R.color.white));
        containerTabs.setViewPager(containerPager);
    }

    private void initPagerData() {
        fragments = new ArrayList<>();

        for (String TITLE : TITLES) {
            BakeBeanListFragment fragment = new BakeBeanListFragment();
            fragment.setOnBeanSelected(BeanChoiceActivity.this);
            fragment.setTitle(TITLE);
            fragments.add(fragment);
        }

    }

    public void onBackPressed() {
        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }
        if (isAddSearchFragment) {
            searchFragment.remove();
            isAddSearchFragment = false;
        }
        finish();
    }

    public boolean isAddSearch() {
        return isAddSearchFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                BeanInfo beanInfo = data.getParcelableExtra("new_bean_info");
                for (int i = 0; i < TITLES.length; i++) {

                    if (TITLES[i].equals(beanInfo.getContinent())) {
                        fragments.get(i).setTitle(TITLES[i]);
                    }
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
            default:
                break;
        }
    }

/*    @Override
    public void onSearchCallBack(String info) {

    }

    @Override
    public void onSearchCallBack(BeanInfo info) {
        Intent intent = new Intent();
        intent.putExtra("beanInfo", info);
        setResult(SELECT_BEAN, intent);
        finish();
    }*/



    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

    }

    @Override
    public void onBeanSelected(BeanInfo beanInfo) {
        Intent intent = new Intent();
        intent.putExtra(UniExtraKey.EXTRA_BEAN_INFO.getKey(), beanInfo);
        setResult(SELECT_BEAN, intent);
        finish();
    }
}
