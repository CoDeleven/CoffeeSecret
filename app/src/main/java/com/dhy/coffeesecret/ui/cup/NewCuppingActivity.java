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
import android.widget.RadioGroup;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.CuppingInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.EditToolBar;
import com.dhy.coffeesecret.ui.cup.fragment.InputNameDialog;
import com.dhy.coffeesecret.ui.cup.fragment.NormalToolBar;
import com.dhy.coffeesecret.utils.T;

import java.io.Console;
import java.util.Date;
import java.util.Map;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.dhy.coffeesecret.R.string.acidity;
import static com.dhy.coffeesecret.R.string.after_taste;
import static com.dhy.coffeesecret.R.string.baked;
import static com.dhy.coffeesecret.R.string.balance;
import static com.dhy.coffeesecret.R.string.dry_and_frag;
import static com.dhy.coffeesecret.R.string.faced;
import static com.dhy.coffeesecret.R.string.flavor;
import static com.dhy.coffeesecret.R.string.overdev;
import static com.dhy.coffeesecret.R.string.scorched;
import static com.dhy.coffeesecret.R.string.sweet;
import static com.dhy.coffeesecret.R.string.taste;
import static com.dhy.coffeesecret.R.string.tipped;
import static com.dhy.coffeesecret.R.string.underdev;

public class NewCuppingActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener, CuppingInfoFragment.OnDeleteListener,
        EditToolBar.OnSaveListener {

    public static final String NEW_CUPPING = "newCupping";
    public static final String EDIT_INFO = "editInfo";
    public static final String SHOW_INFO = "showInfo";
    public static final String VIEW_TYPE = "viewType";
    public static final String TARGET = "target";

    private CuppingInfo mCuppingInfo;
    private BakeReport mBakeReport;

    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private InputNameDialog mInputNameDialog;

    private AlertDialog dialog;


    private String viewType;

    private Fragment[] fragments;
    private CuppingInfoFragment cuppingInfoFragment;
    private BakeInfoFragment bakeInfoFragment;
    private EditToolBar editToolBar;
    private NormalToolBar normalToolBar;

    private int mResultCode = CupFragment.RESULT_CODE_NONE;
    private Intent mResultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cupping);
        Intent intent = getIntent();
        viewType = intent.getStringExtra(VIEW_TYPE);

        if (SHOW_INFO.equals(viewType)) {
            mCuppingInfo = (CuppingInfo) intent.getSerializableExtra(TARGET);
            mBakeReport = mCuppingInfo.getBakeReport();
            loadShowInfoView();
        } else if (NEW_CUPPING.equals(viewType)) {
            loadNewCuppingView();
        } else {
            // TODO: 2017/2/23
        }
        initParam();
    }

    /****************************
     * 在添加杯测时加载添加对应的view
     */
    private void loadNewCuppingView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (editToolBar == null) {
            editToolBar = new EditToolBar();
            editToolBar.setTitle("点击添加杯测名称");
            editToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            editToolBar.setTitleClickListener(new EditToolBar.OnTitleClickListener() {
                @Override
                public void onTitleClick(String name) {
                    mInputNameDialog.show(getSupportFragmentManager(), "");
                }
            });
        }
        transaction.replace(R.id.line, editToolBar);
        transaction.commitNow();

        mInputNameDialog = InputNameDialog.newInstance("");
        mInputNameDialog.setOnConfirmClickListener(new InputNameDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String name) {
                if (mCuppingInfo != null) {
                    mCuppingInfo.setName(name);
                    editToolBar.setTitle(name);
                }
            }
        });
    }

    public void loadShowInfoView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (normalToolBar == null) {
            normalToolBar = new NormalToolBar();
        }
        transaction.replace(R.id.line, normalToolBar);
        normalToolBar.setTitle(mCuppingInfo.getName());
        transaction.commitNow();
        System.out.println("");
        normalToolBar.setEditBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEditView();
            }
        });
    }

    //加载编辑的view
    private void loadEditView() {
        viewType = EDIT_INFO;
        cuppingInfoFragment.setEditable(true);

        mInputNameDialog = InputNameDialog.newInstance(mCuppingInfo.getName());

        mInputNameDialog.setOnConfirmClickListener(new InputNameDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String name) {
                mCuppingInfo.setName(name);
                editToolBar.setTitle(name);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (editToolBar == null) {
            editToolBar = new EditToolBar();
            editToolBar.setTitle(mCuppingInfo.getName());
            editToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            editToolBar.setTitleClickListener(new EditToolBar.OnTitleClickListener() {
                @Override
                public void onTitleClick(String name) {
                    mInputNameDialog.show(getSupportFragmentManager(), "");
                }
            });
        }
        transaction.replace(R.id.line, editToolBar);
        transaction.commit();
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
            cuppingInfoFragment = CuppingInfoFragment.newInstance(null, null);
            cuppingInfoFragment.initEditable(true);
        } else {
            float[] feelScores = new float[]{mCuppingInfo.getDryAndFragrant(), mCuppingInfo.getFlavor(), mCuppingInfo.getAfterTaste(),
                    mCuppingInfo.getAcidity(), mCuppingInfo.getTaste(), mCuppingInfo.getSweetness(), mCuppingInfo.getBalance(), mCuppingInfo.getOverall()};
            float[] flawScores = new float[]{mCuppingInfo.getUnderdevelopment(), mCuppingInfo.getOverdevelopment(), mCuppingInfo.getBaked(), mCuppingInfo.getScorched(), mCuppingInfo.getTipped(), mCuppingInfo.getFaced()};
            cuppingInfoFragment = CuppingInfoFragment.newInstance(flawScores, feelScores);
        }

        bakeInfoFragment = BakeInfoFragment.newInstance(mBakeReport);

        bakeInfoFragment.setOnBakeInfoLoadedListener(new BakeInfoFragment.OnBakeInfoLoadedListener() {
            @Override
            public void onLoaded(BakeReport report) {
                mBakeReport = report;
            }
        });

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
        getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(FLAG_TRANSLUCENT_NAVIGATION);
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
            dialog = new AlertDialog.Builder(this).setTitle("您未编辑完成,是否退出？")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).setPositiveButton("取消", null).create();
        }
        dialog.show();
    }

    /******************
     * 点击保存按钮时调用
     */
    @Override
    public void onSave() {

        if (mCuppingInfo == null) {
            mCuppingInfo = new CuppingInfo();
            mCuppingInfo.setDate(new Date());
        }

        final Map<String, Float> data = cuppingInfoFragment.getData();

        map2Bean(data, mCuppingInfo);
        if (NEW_CUPPING.equals(viewType)) {
            mCuppingInfo.setBakeReport(mBakeReport);
            if (mCuppingInfo.getName() == null) {
                T.showShort(this, "请输入杯测名称");
                mInputNameDialog.show(getSupportFragmentManager(), "");
            } else {
                System.out.println("发送到服务器：" + mCuppingInfo); // TODO: 2017/2/25  发送到服务器
                loadShowInfoView();
                cuppingInfoFragment.setEditable(false);
                viewType = SHOW_INFO;
                mResultCode = CupFragment.RESULT_CODE_ADD;
                mResultIntent = new Intent();
                mResultIntent.putExtra(TARGET, mCuppingInfo);
            }
//          mInputNameDialog.show(getSupportFragmentManager(), "");
        } else {
            System.out.println("发送到服务器：" + mCuppingInfo); // TODO: 2017/2/25  发送到服务器
            loadShowInfoView();
            cuppingInfoFragment.setEditable(false);
            viewType = SHOW_INFO;
            mResultCode = CupFragment.RESULT_CODE_UPDATE;
            mResultIntent = new Intent();
            mResultIntent.putExtra(TARGET, mCuppingInfo);
        }
    }

    @Override
    public void onDelete() {
        mResultCode = CupFragment.RESULT_CODE_DElETE;
        mResultIntent = new Intent();
        mResultIntent.putExtra(TARGET, mCuppingInfo);
        System.out.println("服务器发送删除请求：" + mCuppingInfo.getId());
        finish();
    }

    @Override
    public void finish() {
        setResult(mResultCode, mResultIntent);
        super.finish();
    }

    private void map2Bean(Map<String, Float> data, CuppingInfo cuppingInfo) {
        MapUtils utils = new MapUtils(data);

        cuppingInfo.setAcidity(utils.getScore(acidity));
        cuppingInfo.setAfterTaste(utils.getScore(after_taste));
        cuppingInfo.setBalance(utils.getScore(balance));
        cuppingInfo.setDryAndFragrant(utils.getScore(dry_and_frag));
        cuppingInfo.setFlavor(utils.getScore(flavor));
        cuppingInfo.setTaste(utils.getScore(taste));
        cuppingInfo.setBalance(utils.getScore(balance));
        cuppingInfo.setSweetness(utils.getScore(sweet));
        cuppingInfo.setBaked((int) utils.getScore(baked));
        cuppingInfo.setFaced((int) utils.getScore(faced));
        cuppingInfo.setOverall(utils.getScore(R.string.overall));
        cuppingInfo.setScorched((int) utils.getScore(scorched));
        cuppingInfo.setUnderdevelopment((int) utils.getScore(underdev));
        cuppingInfo.setTipped((int) utils.getScore(tipped));
        cuppingInfo.setOverdevelopment((int) utils.getScore(overdev));
    }

    private class MapUtils {

        private Map<String, Float> data;

        private MapUtils(Map<String, Float> data) {
            this.data = data;
        }

        private float getScore(int id) {
            return data.get(getString(id));
        }
    }
}
