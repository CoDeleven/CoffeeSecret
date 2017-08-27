package com.dhy.coffeesecret.ui.counters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.MainActivity;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.common.views.SearchEditText;
import com.dhy.coffeesecret.ui.counters.fragments.BeanListFragment;
import com.dhy.coffeesecret.ui.counters.fragments.SearchBeanInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class ContainerFragment extends Fragment {

    private static final String TAG = "ContainerFragment";
    private static final int ADD_BEAN = 111;
    private final String[] TITLES = {"全部", "中美", "南美", "大洋", "亚洲", "非洲", "其它"};

    private View containerView;
    private SearchEditText searchBeanET = null;
    private LinearLayout btnAddBean = null;
    private ViewPager containerPager = null;
    private PagerSlidingTabStrip containerTabs = null;

    private List<BeanListFragment> fragments = null;
    private Context context;
    private boolean isAddSearchFragment = false;
    private SearchFragment searchFragment;

    public ContainerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.fragment_container, container, false);
        context = getActivity();
        initPagerData();
        return containerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchBeanET = (SearchEditText) containerView.findViewById(R.id.search_bean);
        btnAddBean = (LinearLayout) containerView.findViewById(R.id.btn_add_bean);
        containerTabs = (PagerSlidingTabStrip) containerView.findViewById(R.id.container_tabs);
        containerPager = (ViewPager) containerView.findViewById(R.id.container_pager);
        initView();
    }

    public void initView() {
        searchBeanET.setSearchBarListener(new SearchEditText.SearchBarListener() {
            @Override
            public void startSearchPage() {
                FragmentTransaction tx = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                if (searchFragment != null) {
                    isAddSearchFragment = !searchFragment.isRemoved();
                }
                if (!isAddSearchFragment) {
                    searchFragment = new SearchBeanInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey(), new ArrayList<Parcelable>(fragments.get(0).getBeaninfoList()));
                    searchFragment.setArguments(bundle);
                    tx.add(R.id.activity_main, searchFragment, "search_bean");
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
                Intent intent = new Intent(context, EditBeanActivity.class);
                startActivityForResult(intent, ADD_BEAN);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        containerPager.setOffscreenPageLimit(6);
        containerPager.setAdapter(new MyPagerAdapter(((MainActivity) context).getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        containerTabs.setTextColor(getResources().getColor(R.color.white));
        containerTabs.setViewPager(containerPager);
    }

    private void initPagerData() {
        fragments = new ArrayList<>();

        for (String TITLE : TITLES) {
            BeanListFragment fragment = new BeanListFragment();
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
    }

    public boolean isAddSearch() {
        return isAddSearchFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                BeanInfo beanInfo =  data.getParcelableExtra("new_bean_info");
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
}
