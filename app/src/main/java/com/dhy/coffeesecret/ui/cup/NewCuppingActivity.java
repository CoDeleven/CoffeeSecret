package com.dhy.coffeesecret.ui.cup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.cup.fragment.BakeInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.CuppingInfoFragment;
import com.dhy.coffeesecret.ui.cup.fragment.EditToolBar;
import com.dhy.coffeesecret.ui.cup.fragment.InputNameDialog;
import com.dhy.coffeesecret.ui.cup.fragment.NormalToolBar;
import com.dhy.coffeesecret.url.UrlCupping;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.dhy.coffeesecret.R.string.acidity;
import static com.dhy.coffeesecret.R.string.after_taste;
import static com.dhy.coffeesecret.R.string.baked;
import static com.dhy.coffeesecret.R.string.balance;
import static com.dhy.coffeesecret.R.string.dry_and_frag;
import static com.dhy.coffeesecret.R.string.faced;
import static com.dhy.coffeesecret.R.string.flavor;
import static com.dhy.coffeesecret.R.string.overall;
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
    public static final String EMPTY_SINGLE_BEANNAME = "没有豆子";

    private static final int UPDATE = 0x3;
    private static final int DELETE = 0x2;
    private static final int SAVING = 0x1;
    private static final int CANCEL = 0x0;
    private static final int SUCCESS = 0x666;
    private static final int ERROR = 0x2333;
    public static final int SELECT_LINE = 0x111;

    private boolean isNew;
    private CuppingInfo mCuppingInfo;
    private BakeReport mBakeReport;
    private Handler mHandler;

    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private InputNameDialog mInputNameDialog;

    private AlertDialog dialog;
    private ProgressDialog progress;

    private String viewType;

    private Fragment[] fragments;
    private CuppingInfoFragment cuppingInfoFragment;
    private BakeInfoFragment bakeInfoFragment;
    private EditToolBar editToolBar;
    private NormalToolBar normalToolBar;

    private int mResultCode = CupFragment.RESULT_CODE_NONE;
    private Intent mResultIntent;
    private MyApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cupping);

        application = (MyApplication) getApplication();
        mHandler = new NewCuppingHandler();
        Intent intent = getIntent();
        viewType = intent.getStringExtra(VIEW_TYPE);
        if (SHOW_INFO.equals(viewType)) {
            mCuppingInfo = intent.getParcelableExtra(TARGET);
            mBakeReport = mCuppingInfo.getBakeReport();
            loadShowInfoView();
        } else if (NEW_CUPPING.equals(viewType)) {
            loadNewCuppingView();
        }
        initParam();
    }

    /****************************
     * 在添加杯测时加载添加对应的view
     */
    private void loadNewCuppingView() {
        if (mCuppingInfo == null) {
            mCuppingInfo = new CuppingInfo();
            mCuppingInfo.setDate(new Date());
        }

        isNew = true;
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (editToolBar == null) {
            editToolBar = new EditToolBar();
            editToolBar.setTitle("添加标题");
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
    }

    private void loadShowViewInMain() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadShowInfoView();
            }
        });
    }

    public void loadShowInfoView() {

        if (cuppingInfoFragment != null) {
            cuppingInfoFragment.setEditable(false);
            cuppingInfoFragment.updateProgressBar(mCuppingInfo.getFeelArr(), mCuppingInfo.getFlawArr());
        }
        viewType = SHOW_INFO;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (normalToolBar == null) {
            normalToolBar = new NormalToolBar();
        }
        transaction.replace(R.id.line, normalToolBar);
        normalToolBar.setTitle(mCuppingInfo.getName());
        normalToolBar.setEditBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEditView();
            }
        });
        transaction.commitNow();
    }

    //加载编辑的view
    private void loadEditView() {
        viewType = EDIT_INFO;
        cuppingInfoFragment.setEditable(true);
        mRadioGroup.check(R.id.rd_cup);
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
                    final Map<String, Float> data = cuppingInfoFragment.getData();
                    map2Bean(data, mCuppingInfo);
                    updateCommit();
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
        if (keyCode == 4 && (EDIT_INFO.equals(viewType) || NEW_CUPPING.equals(viewType))) {
//            showDialog(); // TODO: 2017/5/9
            final Map<String, Float> data = cuppingInfoFragment.getData();
            map2Bean(data, mCuppingInfo);
            updateCommit();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateCommit() {
        new Thread() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(UPDATE);

                String update = UrlCupping.update(application.getToken());
                HttpUtils.enqueue(update, mCuppingInfo, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mHandler.sendEmptyMessage(ERROR);
                        finish();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        mResultCode = CupFragment.RESULT_CODE_UPDATE;
                        mResultIntent = new Intent();
                        mResultIntent.putExtra(TARGET, mCuppingInfo);
                        mHandler.sendEmptyMessage(SUCCESS);
                        finish();
                    }
                });
            }
        }.start();
    }

    private void initParam() {
        if (NEW_CUPPING.equals(viewType)) {
            cuppingInfoFragment = CuppingInfoFragment.newInstance(null, null, 0);
            cuppingInfoFragment.initEditable(true);
        } else {
            float[] feelScores = new float[]{mCuppingInfo.getDryAndFragrant(), mCuppingInfo.getFlavor(), mCuppingInfo.getAfterTaste(),
                    mCuppingInfo.getAcidity(), mCuppingInfo.getTaste(), mCuppingInfo.getSweetness(), mCuppingInfo.getBalance(), mCuppingInfo.getOverall()};
            float[] flawScores = new float[]{mCuppingInfo.getUnderdevelopment(), mCuppingInfo.getOverdevelopment(), mCuppingInfo.getBaked(), mCuppingInfo.getScorched(), mCuppingInfo.getTipped(), mCuppingInfo.getFaced()};
            int roastDegree = 0;
            try{
                roastDegree = (int)Float.parseFloat(mBakeReport.getRoastDegree());
            }catch (Exception e){
                e.printStackTrace();
            }
            cuppingInfoFragment = CuppingInfoFragment.newInstance(flawScores, feelScores, roastDegree);
        }

        bakeInfoFragment = BakeInfoFragment.newInstance(mBakeReport, NEW_CUPPING.equals(viewType));

        bakeInfoFragment.setOnBakeInfoLoadedListener(new BakeInfoFragment.OnBakeInfoLoadedListener() {
            @Override
            public void onLoaded(BakeReport report) {
                if(report != null){
                    String beanName = report.getSingleBeanName();
                    if(beanName.trim().isEmpty()){
                        beanName = EMPTY_SINGLE_BEANNAME;
                    }
                    editToolBar.setTitle(beanName);
                }
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

    /**
     * 点击保存按钮时调用
     */
    @Override
    public void onSave() {

        final Map<String, Float> data = cuppingInfoFragment.getData();
        map2Bean(data, mCuppingInfo);
        cuppingInfoFragment.updateProgressBar(mCuppingInfo.getFeelArr(), mCuppingInfo.getFlawArr());

        if (NEW_CUPPING.equals(viewType) || isNew) {
            mCuppingInfo.setBakeReport(mBakeReport);
            if (mCuppingInfo.getName() == null) {
                T.showShort(this, "请输入杯测名称");
                mInputNameDialog.show(getSupportFragmentManager(), "");
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(SAVING);
                        String update = UrlCupping.add(application.getToken());
                        HttpUtils.enqueue(update, mCuppingInfo, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                mHandler.sendEmptyMessage(ERROR);
                                loadShowViewInMain();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                mHandler.sendEmptyMessage(SUCCESS);
                                Long aLong = Long.valueOf(response.body().string().trim());
                                mCuppingInfo.setId(aLong);
                                loadShowViewInMain();
                                mResultCode = CupFragment.RESULT_CODE_ADD;
                                mResultIntent = new Intent();
                                mResultIntent.putExtra(TARGET, mCuppingInfo);
                            }
                        });

                    }
                }.start();
            }
//          mInputNameDialog.show(getSupportFragmentManager(), "");
        } else {
            new Thread() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(UPDATE);
                    String update = UrlCupping.add(application.getToken());

                    HttpUtils.enqueue(update, mCuppingInfo, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mHandler.sendEmptyMessage(ERROR);
                            loadShowViewInMain();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            mResultCode = CupFragment.RESULT_CODE_UPDATE;
                            mResultIntent = new Intent();
                            mResultIntent.putExtra(TARGET, mCuppingInfo);
                            mHandler.sendEmptyMessage(SUCCESS);
                            loadShowViewInMain();
                        }
                    });
                }
            }.start();
        }
    }

    private void delete() {

        String url = UrlCupping.delete(application.getToken(),mCuppingInfo.getId());
        mHandler.sendEmptyMessage(DELETE);
        HttpUtils.enqueue(url, mCuppingInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                finish();
                mHandler.sendEmptyMessage(ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.sendEmptyMessage(SUCCESS);
                finish();
            }
        });
    }

    @Override
    public void onDelete() {
        if (isNew) {
            mResultCode = CupFragment.RESULT_CODE_NONE;
            if (EDIT_INFO.equals(viewType)) {
                new Thread() {
                    @Override
                    public void run() {
                        delete();
                    }
                }.start();
            } else {
                finish();
            }
        } else {
            mResultCode = CupFragment.RESULT_CODE_DElETE;
            mResultIntent = new Intent();
            mResultIntent.putExtra(TARGET, mCuppingInfo);
            new Thread() {
                @Override
                public void run() {
                    delete();
                }
            }.start();
        }
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
        cuppingInfo.setOverall(utils.getScore(overall));
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

    class NewCuppingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAVING:
                    progress = ProgressDialog.show(NewCuppingActivity.this, null, "保存中...", true);
                    break;
                case UPDATE:
                    progress = ProgressDialog.show(NewCuppingActivity.this, null, "更新中...", true);
                    break;
                case CANCEL:
                    if (progress != null && progress.isShowing()) {
                        progress.cancel();
                    }
                    break;
                case DELETE:
                    progress = ProgressDialog.show(NewCuppingActivity.this, null, "正在删除...", true);
                    break;
                case SUCCESS:
                    sendEmptyMessage(CANCEL);
                    break;
                case ERROR:
                    sendEmptyMessage(CANCEL);
                    T.showShort(getApplicationContext(), "网络错误");
                    break;
            }
        }
    }
}
