package com.dhy.coffeesecret.ui.device.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dhy.coffeesecret.R;

public class ReportLineChartPart extends Fragment {
    private ViewPager mViewPager;
    private Fragment[] fragments = new Fragment[2];
    private ImageButton mLeftBtn;
    private ImageButton mRightBtn;
    private int lastPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.conta_report_chart_nav, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.id_report_viewpager);
        mLeftBtn = (ImageButton) view.findViewById(R.id.id_report_leftbtn);
        mRightBtn = (ImageButton) view.findViewById(R.id.id_report_rightbtn);
        mLeftBtn.setVisibility(View.INVISIBLE);

        initViewPager();
        initNav();

        return view;
    }

    private void initViewPager() {
        fragments[0] = new LineChartPart();
        ((LineChartPart) fragments[0]).setMode(LineChartPart.MODE1);
        fragments[1] = new LineChartPart();
        ((LineChartPart) fragments[1]).setMode(LineChartPart.MODE2);
        mViewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position - lastPosition > 0) {
                    mRightBtn.setVisibility(View.INVISIBLE);
                    mLeftBtn.setVisibility(View.VISIBLE);
                } else {
                    mRightBtn.setVisibility(View.VISIBLE);
                    mLeftBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initNav() {
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.arrowScroll(View.FOCUS_LEFT);
                mLeftBtn.setVisibility(View.INVISIBLE);
                mRightBtn.setVisibility(View.VISIBLE);
            }
        });
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                mRightBtn.setVisibility(View.INVISIBLE);
                mLeftBtn.setVisibility(View.VISIBLE);
            }
        });
    }
}
