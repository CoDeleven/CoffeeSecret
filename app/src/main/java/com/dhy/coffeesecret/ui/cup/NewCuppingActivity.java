package com.dhy.coffeesecret.ui.cup;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.CuppingInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.EditToolBar;
import com.dhy.coffeesecret.ui.cup.fragment.NormalToolBar;

public class NewCuppingActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener, CuppingInfoFragment.OnFragmentInteractionListener,
                        EditToolBar.OnSaveListener {

    public static final String NEW_CUPPING = "newCupping";
    public static final String EDIT_INFO = "editInfo";
    public static final String SHOW_INFO = "showInfo";
    public static final String VIEW_TYPE = "viewType";
    public static final String TARGET = "target";

    private CuppingInfo mCuppingInfo;

    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private AlertDialog dialog;

    private String viewType;

    private Fragment[] fragments;
    private CuppingInfoFragment cuppingInfoFragment;
    private BakeInfoFragment bakeInfoFragment;
    private EditToolBar editToolBar;
    private NormalToolBar normalToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cupping);

        Intent intent = getIntent();
        viewType = intent.getStringExtra(VIEW_TYPE);

        if (SHOW_INFO.equals(viewType)) {
            mCuppingInfo = (CuppingInfo) intent.getSerializableExtra(TARGET);
            loadShowInfoView();
        } else if (NEW_CUPPING.equals(viewType)) {
            loadNewCuppingView();
        } else {
            // TODO: 2017/2/23
        }
        initParam();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadNewCuppingView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (editToolBar == null) {
            editToolBar = new EditToolBar();
            editToolBar.setmTitle("添加杯测纪录");
            editToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        }
        transaction.replace(R.id.line, editToolBar);
        transaction.commitNow();
    }

    public void loadShowInfoView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (normalToolBar == null) {
            normalToolBar = new NormalToolBar();
        }
        transaction.replace(R.id.line, normalToolBar);
        transaction.commitNow();
        normalToolBar.setEditBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewType = EDIT_INFO;
                cuppingInfoFragment.setEditable(true);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (editToolBar == null) {
                    editToolBar = new EditToolBar();
                    editToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog();
                        }
                    });
                }
                transaction.replace(R.id.line, editToolBar);
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && EDIT_INFO.equals(viewType)) {
            showDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initParam() {
        if (NEW_CUPPING.equals(viewType)) {
            cuppingInfoFragment = CuppingInfoFragment.newInstance(null,null);
            cuppingInfoFragment.initEditable(true);
        }else {
            float[] feelScores = new float[]{3.01f, 0, 0, 0, 0, 0, 0, 0};
            float[] flawScores = new float[]{0, 0, 0, 0, 0, 0};
            cuppingInfoFragment = CuppingInfoFragment.newInstance(flawScores,feelScores);
        }

        bakeInfoFragment = new BakeInfoFragment();
        fragments = new Fragment[]{cuppingInfoFragment, bakeInfoFragment};

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
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mRadioGroup.check(R.id.rd_cup);
                } else {
                    mRadioGroup.check(R.id.rd_bake);
                }
            }
        });
        mRadioGroup.check(R.id.rd_cup);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (R.id.rd_cup == id) {
            mViewPager.setCurrentItem(0);
        } else {
            mViewPager.setCurrentItem(1);
        }
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this).setTitle("您未编辑完成是否退出？")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).setPositiveButton("取消", null).create();
        }
        dialog.show();
    }

    @Override
    public void onCuppingFragmentInteraction(Uri uri) {

    }


    @Override
    public void onSave() {
        loadShowInfoView();
        viewType = SHOW_INFO;
        cuppingInfoFragment.setEditable(false);
        // TODO: 2017/2/23
        System.out.println("onSave");
    }
}
