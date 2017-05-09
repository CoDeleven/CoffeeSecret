package com.dhy.coffeesecret.ui.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.EditBeanActivity;
import com.dhy.coffeesecret.ui.device.fragments.BakeBeanListFragment;
import com.dhy.coffeesecret.ui.device.fragments.SearchFragment;
import com.dhy.coffeesecret.views.SearchEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.dhy.coffeesecret.ui.device.fragments.BakeDialog.SELECT_BEAN;

public class DialogBeanSelected extends AppCompatActivity implements BakeBeanListFragment.OnBeanSelected, SearchFragment.OnSearchCallBack {

    private static final String TAG = "DialogBeanSelected";
    private static final int ADD_BEAN = 111;
    private final String[] TITLES = {"全部", "中美", "南美", "大洋", "亚洲", "非洲", "其它"};

    private SearchEditText searchBeanET = null;
    private LinearLayout btnAddBean = null;
    private ViewPager containerPager = null;
    private PagerSlidingTabStrip containerTabs = null;

    private List<BakeBeanListFragment> fragments = null;
    private boolean isAddSearchFragment = false;
    private SearchFragment searchFragment;

    public DialogBeanSelected() {
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
            public void starSearchPage() {
                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                if (searchFragment != null) {
                    isAddSearchFragment = !searchFragment.isRemoved();
                }
                if (!isAddSearchFragment) {
                    searchFragment = new SearchFragment();
                    searchFragment.addOnSearchCallBack(DialogBeanSelected.this);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("beanList", (Serializable) fragments.get(0).getBeaninfoList());
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
                Intent intent = new Intent(DialogBeanSelected.this, EditBeanActivity.class);
                startActivityForResult(intent, ADD_BEAN);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        containerPager.setOffscreenPageLimit(6);
        containerPager.setAdapter(new DialogBeanSelected.MyPagerAdapter((this).getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        containerTabs.setTextColor(getResources().getColor(R.color.white));
        containerTabs.setViewPager(containerPager);
    }

    private void initPagerData() {
        fragments = new ArrayList<>();

        for (String TITLE : TITLES) {
            BakeBeanListFragment fragment = new BakeBeanListFragment();
            fragment.setOnBeanSelected(DialogBeanSelected.this);
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
                BeanInfo beanInfo = (BeanInfo) data.getSerializableExtra("new_bean_info");
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

    @Override
    public void onSearchCallBack(String info) {

    }

    @Override
    public void onSearchCallBack(BeanInfo info) {
        Intent intent = new Intent();
        intent.putExtra("beanInfo", info);
        setResult(SELECT_BEAN, intent);
        finish();
    }

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
        intent.putExtra("beanInfo", beanInfo);
        setResult(SELECT_BEAN, intent);
        finish();
    }
}
