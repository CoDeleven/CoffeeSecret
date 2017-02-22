package com.dhy.coffeesecret.ui.cup;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.CuppingInfoFragment;
import com.dhy.coffeesecret.ui.cup.listener.FinishListener;

public class NewCuppingActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener,
        CuppingInfoFragment.OnFragmentInteractionListener,BakeInfoFragment.OnFragmentInteractionListener {

    private Toolbar mToolbar;
    private Button saveButton;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;

    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cupping);
        initParam();
        mRadioGroup.check(R.id.rd_cup);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void initParam() {

        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new FinishListener(this));

        fragments = new Fragment[]{new CuppingInfoFragment(),new BakeInfoFragment()};
        mRadioGroup = (RadioGroup) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

        });

        mRadioGroup.setOnCheckedChangeListener(this);
        mViewPager.setOnPageChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if(R.id.rd_cup == id){
            mViewPager.setCurrentItem(0);
        }else {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 0){
            mRadioGroup.check(R.id.rd_cup);
        }else {
            mRadioGroup.check(R.id.rd_bake);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCuppingFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBakeInfoFragmentInteraction(Uri uri) {

    }
}
