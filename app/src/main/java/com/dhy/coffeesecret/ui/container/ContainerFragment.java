package com.dhy.coffeesecret.ui.container;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.dhy.coffeesecret.MainActivity;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.container.fragments.BeanListFragment;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.cup.CupFragment;
import com.dhy.coffeesecret.views.SearchEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContainerFragment extends Fragment implements ViewPager.OnPageChangeListener{

    private static final String TAG = "ContainerFragment";
    private final String[] TITLES = {"全部", "中美", "南美", "大洋", "亚洲", "非洲", "其它"};

    private View containerView;
    private SearchEditText searchBeanET = null;
    private ViewPager containerPager = null;
    private PagerSlidingTabStrip containerTabs = null;
    private List<BeanListFragment> fragments = null;
    private Context context;

    public ContainerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        containerView = inflater.inflate(R.layout.fragment_container, container, false);
        context = getActivity();

        searchBeanET = (SearchEditText) containerView.findViewById(R.id.search_bean);
        containerPager = (ViewPager) containerView.findViewById(R.id.container_pager);

        initView();
        initPager();

        return containerView;
    }

    private boolean isAddSearchFragment = false;
    private SearchFragment searchFragment = new SearchFragment();

    public void initView() {
        searchBeanET.setSearchBarListener(new SearchEditText.SearchBarListener() {
            @Override
            public void starSearchPage() {
                FragmentTransaction tx = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);

                if (!isAddSearchFragment) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("beanList", (Serializable) fragments.get(0).getBeaninfoList());
                    searchFragment.setArguments(bundle);
                    tx.add(R.id.activity_main, searchFragment, "search_bean");
                    isAddSearchFragment = true;
                } else {
                    tx.show(searchFragment);
                }
                tx.commit();
            }
        });

        containerPager.setAdapter(new MyPagerAdapter(((MainActivity) context).getSupportFragmentManager()));
        containerPager.setOffscreenPageLimit(6);
        // Bind the tabs to the ViewPager
        containerTabs = (PagerSlidingTabStrip) containerView.findViewById(R.id.container_tabs);
        containerTabs.setTextColor(getResources().getColor(R.color.white));
        containerTabs.setViewPager(containerPager);
    }

    private void initPager() {

        fragments = new ArrayList<>();

        for (int i = 0; i < TITLES.length; i++) {
            BeanListFragment fragment = new BeanListFragment();
            fragment.setTitle(TITLES[i]);
            fragments.add(fragment);
        }
        containerPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //当前page的位置
    private int position;

    @Override
    public void onPageSelected(int position) {
        this.position = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    public void onBackPressed() {
        if (searchFragment != null && !searchFragment.isHidden()) {
            FragmentTransaction tx = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
            tx.remove(searchFragment);
            tx.commit();
            isAddSearchFragment = false;
        }
    }
}
