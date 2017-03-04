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
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.URLs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Console;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    private boolean isNew;
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

    public void loadShowInfoView() {
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

        final Map<String, Float> data = cuppingInfoFragment.getData();

        map2Bean(data, mCuppingInfo);
        if (NEW_CUPPING.equals(viewType) || isNew) {
            mCuppingInfo.setBakeReport(mBakeReport);
            if (mCuppingInfo.getName() == null) {
                T.showShort(this, "请输入杯测名称");
                mInputNameDialog.show(getSupportFragmentManager(), "");
            } else {
                new Thread() {
                    @Override
                    public void run() {

                        HttpUtils.enqueue(URLs.ADD_CUPPING, mCuppingInfo, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                // TODO: 2017/3/4
                            }   

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                        });
                        
                    }
                }.start();

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
            new Thread() {
                @Override
                public void run() {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(mCuppingInfo);
                    MediaType type = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(type, json);
                    String url = "http://192.168.191.1:8080/CoffeeSecret/cupping/update";
                    Request request = new Request.Builder().url(url).post(body).build();
                    OkHttpClient client = new OkHttpClient();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.showShort(NewCuppingActivity.this, "修改成功");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.showShort(NewCuppingActivity.this, "修改失败");
                                }
                            });
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
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
        if (isNew) {
            mResultCode = CupFragment.RESULT_CODE_NONE;
            if (EDIT_INFO.equals(viewType)) {
                new Thread() {
                    @Override
                    public void run() {
                        String url = "http://192.168.191.1:8080/CoffeeSecret/cupping/" + mCuppingInfo.getId() + "/delete";
                        Request request = new Request.Builder().url(url).build();
                        OkHttpClient client = new OkHttpClient();
                        try {
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T.showShort(NewCuppingActivity.this, "删除成功");
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T.showShort(NewCuppingActivity.this, "删除失败");
                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
                System.out.println("发送到服务器，删除：" + mCuppingInfo.getId());// TODO: 2017/2/25  发送到服务器
            } else {
                System.out.println("不需要：" + mCuppingInfo.getId());
            }
        } else {
            mResultCode = CupFragment.RESULT_CODE_DElETE;
            mResultIntent = new Intent();
            mResultIntent.putExtra(TARGET, mCuppingInfo);
            new Thread() {
                @Override
                public void run() {
                    String url = "http://192.168.191.1:8080/CoffeeSecret/cupping/" + mCuppingInfo.getId() + "/delete";
                    Request request = new Request.Builder().url(url).build();
                    OkHttpClient client = new OkHttpClient();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.showShort(NewCuppingActivity.this, "删除成功");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.showShort(NewCuppingActivity.this, "删除失败");
                                }
                            });
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            System.out.println("服务器发送删除请求：" + mCuppingInfo.getId());// TODO: 2017/2/25  发送到服务器
        }
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
}
