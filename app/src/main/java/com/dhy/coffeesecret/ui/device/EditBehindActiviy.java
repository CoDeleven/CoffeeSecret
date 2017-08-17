package com.dhy.coffeesecret.ui.device;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.url.UrlBake;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.github.mikephil.charting.data.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static com.dhy.coffeesecret.ui.device.fragments.BakeDialog.SELECT_BEAN;
import static com.dhy.coffeesecret.utils.DensityUtils.dp2px;

public class EditBehindActiviy extends AppCompatActivity implements CircleSeekBar.OnSeekBarChangeListener {
    private static final int RERANGE_BEAN_INFO = 111;
    @Bind(R.id.id_bake_degree)
    CircleSeekBar mSeekBar;
    @Bind(R.id.id_bake_behind_save)
    Button save;
    @Bind(R.id.id_bake_behind_cookedWeight)
    EditText cookedWeight;
    @Bind(R.id.id_score)
    TextView score;
    @Bind(R.id.id_editor_scroll)
    ScrollView scrollView;
    @Bind(R.id.id_event_container)
    LinearLayout eventContainer;
    @Bind(R.id.id_report_delete)
    Button reportDelete;
    @Bind(R.id.id_bean_container)
    LinearLayout beanContainer;

    private float mCurValue;
    private List<String> content = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();
    private List<BeanInfoSimple> beanInfoSimples = new ArrayList<>();
    private BeanInfoSimple curBeanInfoSimple;
    private Button curBeanButton;
    private BakeReportProxy proxy;
    private String unit;
    private Dialog dialog;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RERANGE_BEAN_INFO:
                    String newName = (String) msg.obj;
                    curBeanButton.setText(newName);
                    break;
            }
            return false;
        }
    });
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit_behind_activiy);

        application = (MyApplication) getApplication();
        ButterKnife.bind(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        init();
        unit = SettingTool.getConfig(this).getWeightUnit();
        cookedWeight.setHint("当前生豆为"+ proxy.getRawBeanWeight() + MyApplication.weightUnit);

        generateItem();
        generateBean();

        int status = getIntent().getIntExtra("status", -1);
        if (status != BakeActivity.I_AM_BAKEACTIVITY) {
            reportDelete.setVisibility(View.VISIBLE);
            cookedWeight.setText(proxy.getBakeReport().getCookedBeanWeight());
            mSeekBar.setCurProcess((int) Float.parseFloat(proxy.getBakeDegree()));
            reportDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String delete = UrlBake.delete(application.getToken(), proxy.getBakeReport().getId());
                    HttpUtils.enqueue(delete, null, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ReportActivity.getInstance().finish();
                            finish();
                        }
                    });
                }
            });
        }
    }

    private void init() {
        proxy = ((MyApplication) getApplication()).getBakeReport();
        entries = proxy.getEntriesWithEvents();
        beanInfoSimples = proxy.getBeanInfos();


        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    ((ViewGroup) scrollView.getChildAt(0)).requestDisallowInterceptTouchEvent(false);
                } else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    ((ViewGroup) scrollView.getChildAt(0)).requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
    }

    @OnClick(R.id.id_bake_behind_save)
    protected void onSave() {

        String weight = cookedWeight.getText().toString();
        if (!"".equals(weight) && weight != null) {
            float defaultWeight = Utils.getReversed2DefaultWeight(Float.parseFloat(weight) + "");
            if(defaultWeight > proxy.getRawBeanWeight()){
                T.showShort(this, "填写不大于生豆重量的数值...");
                return;
            }
            proxy.setCookedBeanWeight(defaultWeight);
        } else {
            proxy.setCookedBeanWeight(0);
        }
        if (proxy.getBeanInfos().size() != 1) {
            proxy.setSingleBeanId(-1);
        }

        proxy.setBakeDegree(mCurValue);
        Intent other = new Intent(this, ReportActivity.class);
        sendJsonData(proxy.getBakeReport());
        startActivity(other);
        finish();
    }

    private void sendJsonData(final BakeReport proxy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String add = UrlBake.add(application.getToken());
                    HttpUtils.execute(add, proxy);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onChanged(CircleSeekBar seekbar, int curValue) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putFloat("curValue", curValue);
        msg.setData(bundle);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                score.setText(((int) msg.getData().getFloat("curValue")) + "");
                return false;
            }
        }).sendMessage(msg);
        mCurValue = curValue;
    }

    private void generateItem() {


        for (final Entry entry : entries) {
            // LinearLayout的设置
            LinearLayout linearLayout = new LinearLayout(this);
            // 设置LinearLayout的上下padding各位20dp
            linearLayout.setPadding(0, dp2px(this, 20), 0, dp2px(this, 20));
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            linearLayout.setOrientation(HORIZONTAL);

            // 设置imageView
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            // 为其设置图片
            imageView.setImageResource(R.drawable.ic_baking_behind_time);
            params.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(params);

            // 设置标题
            TextView textView = new TextView(this);
            params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            // textView.setId(id_edit_event_item_timeNode);
            textView.setPadding(dp2px(this, 20), 0, 0, 0);
            // 设置时间
            int time = (int) entry.getX();
            textView.setText(Utils.getTimeWithFormat(time));

            // 设置事件
            final TextView eventView = new TextView(this);
            // eventView.setId(id_edit_event_item_event);
            params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            // eventView.setId(id_edit_event_item_timeNode);
            eventView.setPadding(dp2px(this, 20), 0, 0, 0);
            // 设置时间
            eventView.setText(entry.getEvent().getDescription());

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditBehindActiviy.this);
                    builder.setTitle("事件补充");
                    final EditText editText = new EditText(EditBehindActiviy.this);
                    editText.setHint(entry.getEvent().getDescription());
                    builder.setView(editText);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eventView.setText(editText.getText().toString());
                            entry.getEvent().setDescription(editText.getText().toString());
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.show();
                }
            });

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            linearLayout.addView(eventView);

            eventContainer.addView(linearLayout);
        }


    }

    private void generateBean() {
        int beanCount = 1;


        for (final BeanInfoSimple temp : beanInfoSimples) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            linearLayout.setOrientation(HORIZONTAL);
            linearLayout.setPadding(0, dp2px(this, 5), 0, dp2px(this, 5));

            TextView index = new TextView(this);
            index.setText(beanCount++ + "");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.rightMargin = dp2px(this, 12);
            index.setLayoutParams(params);


            // 设置图片
            ImageView imageView = new ImageView(this);
            params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(params);
            params.leftMargin = dp2px(this, 12);
            imageView.setImageResource(R.drawable.ic_bake_dialog_singlebean);

            // 设置豆名
            final Button beanName = new Button(this);
            params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = dp2px(this, 12);
            beanName.setMinHeight(dp2px(this, 25));
            beanName.setMinimumHeight(dp2px(this, 25));
            beanName.setText(temp.getBeanName());
            beanName.setBackgroundColor(Color.TRANSPARENT);
            beanName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 设置当前改变的beanInfo
                    curBeanInfoSimple = temp;
                    curBeanButton = beanName;
                    Intent intent = new Intent(EditBehindActiviy.this, DialogBeanSelected.class);
                    startActivityForResult(intent, SELECT_BEAN);
                }
            });

            // 设置重量
            EditText editText = new EditText(this);
            params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            // 设置已添加豆子的重量
            editText.setHint(Utils.getCrspWeightValue(temp.getUsage()) + unit);
            // 设置不允许进行修改
            editText.setEnabled(false);
            params.leftMargin = dp2px(this, 12);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setSingleLine(true);
            editText.setSingleLine();

            linearLayout.addView(index);
            linearLayout.addView(imageView);
            linearLayout.addView(beanName);
            linearLayout.addView(editText);

            beanContainer.addView(linearLayout);

            View view = new View(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 1));
            view.setBackgroundColor(Color.GRAY);

            //添加分割线
            beanContainer.addView(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SELECT_BEAN && curBeanInfoSimple != null) {
            BeanInfo beanInfo = (BeanInfo) data.getSerializableExtra("beanInfo");
            curBeanInfoSimple.setBeanName(beanInfo.getName());
            curBeanInfoSimple.setWaterContent(beanInfo.getWaterContent() + "");
            curBeanInfoSimple.setManor(beanInfo.getManor());
            curBeanInfoSimple.setProcess(beanInfo.getProcess());
            curBeanInfoSimple.setLevel(beanInfo.getLevel());
            curBeanInfoSimple.setAltitude(beanInfo.getAltitude());
            curBeanInfoSimple.setCountry(beanInfo.getCountry());
            curBeanInfoSimple.setArea(beanInfo.getArea());
            curBeanInfoSimple.setSpecies(beanInfo.getSpecies());
            Message msg = new Message();
            msg.obj = beanInfo.getName();
            msg.what = RERANGE_BEAN_INFO;
            mHandler.sendMessage(msg);
        }
    }
}
