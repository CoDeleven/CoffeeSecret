package com.dhy.coffeesecret.ui.device;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.report_edit.IEditView;
import com.dhy.coffeesecret.model.report_edit.Presenter4Editor;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.github.mikephil.charting.data.Entry;

import java.io.IOException;
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
import static com.dhy.coffeesecret.views.BakeDegreeCircleSeekBar.positions;

public class EditBehindActivity extends AppCompatActivity implements CircleSeekBar.OnSeekBarChangeListener, IEditView {
    private static final String TAG = EditBehindActivity.class.getSimpleName();
    public static final int RERANGE_BEAN_INFO = 111;
    public static final int GENERATE_BEAN_INFO = 222;
    public static final int GENERATE_ENTRY_EVENTS = 333;
    public static final int INVALIDATE_COOKED_WEIGHT = 444;
    public static final int BAKE_DEGREE = 555;
    public static final int BUTTON_NAME = 666;
    public static final String MODE_KEY = "mEditorMode";
    public static final int MODE_EDITOR = 0x123, MODE_GENERATE = 0x456;
    @Bind(R.id.id_bake_degree)
    CircleSeekBar mSeekBar;
    @Bind(R.id.id_bake_behind_save)
    ImageButton save;
    @Bind(R.id.id_bake_behind_cookedWeight)
    EditText cookedWeight;
    @Bind(R.id.id_rl_score)
    RelativeLayout scoreLayout;
    @Bind(R.id.id_editor_scroll)
    ScrollView scrollView;
    @Bind(R.id.id_score)
    TextView score;
    @Bind(R.id.id_score_descriptor)
    TextView scoreDescriptor;
    @Bind(R.id.id_event_container)
    LinearLayout eventContainer;
    @Bind(R.id.id_report_delete)
    Button reportDelete;
    @Bind(R.id.id_bean_container)
    LinearLayout beanContainer;
    private Presenter4Editor mPresenter = Presenter4Editor.newInstance();
    private Button curBeanButton;
    private String unit;
    private Dialog dialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 在这个界面点击后退 视为 放弃此次烘焙
        mPresenter.clearBakeReport();
        finish();
    }

    private int mEditorMode;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RERANGE_BEAN_INFO:
                    String newName = (String) msg.obj;
                    curBeanButton.setText(newName);
                    break;
                case GENERATE_BEAN_INFO:
                    List<BeanInfoSimple> simples = (List<BeanInfoSimple>) msg.obj;
                    generateBeanInfoView(simples);
                    break;
                case GENERATE_ENTRY_EVENTS:
                    List<Entry> entryEvents = (List<Entry>) msg.obj;
                    generateItemView(entryEvents);
                    break;
                case INVALIDATE_COOKED_WEIGHT:
                    T.showShort(EditBehindActivity.this, (String) msg.obj);
                    break;
                case BAKE_DEGREE:
                    Bundle bundle = msg.getData();
                    int toastValue = bundle.getInt("toast_value");
                    String colorBlock = bundle.getString("color_block_str");
                    int colorValue = bundle.getInt("color_block_val");

                    GradientDrawable drawable = (GradientDrawable)getResources().getDrawable(R.drawable.bg_circle_edit_behind);
                    drawable.setColor(colorValue);
                    scoreLayout.setBackground(drawable);

                    score.setText((toastValue == Integer.MAX_VALUE ? "N/A" : (toastValue + "")));

                    scoreDescriptor.setText(colorBlock);
                    break;
                case BUTTON_NAME:
                    curBeanButton.setText((String) msg.obj);
                    break;
            }
            return false;
        }
    });

    /**
     * 用于计算烘焙都颜色块的方法
     * @param angle
     */
    private String computeColorBlock4Toast(double angle){
        if(angle < 45 && angle >= 0){
            return "Green";
        }else if(angle >= 45 && angle < 90){
            return "Light";
        }else if(angle >= 90 && angle <135){
            return "Cinnamon";
        }else if(angle >= 135 && angle < 180){
            return "Medium";
        }else if(angle >= 180 && angle < 225){
            return "High";
        }else if(angle >= 225 && angle < 270){
            return "City";
        }else if(angle >= 270 && angle < 315){
            return "Full City";
        }else if(angle >= 315 && angle < 360){
            return "French";
        }
        return "";
    }

    private int computeToastValue(double angle){
        int len = positions.length;
        int index = ((int)angle) / 45;
        switch (index){
            case 0:
                return Integer.MAX_VALUE;
            case 1:
                return 80;
            case 2:
                return (-(int)Math.floor(angle % 45 / 45f * 10)) + 70;
            case 3:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 55;
            case 4:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 50;
            case 5:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 45;
            case 6:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 40;
            case 7:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 35;
            default:
                return Integer.MAX_VALUE;
        }
    }

    @Override
    public void updateBeanInfos(List<BeanInfoSimple> infoSimples) {
        Message msg = new Message();
        msg.what = GENERATE_BEAN_INFO;
        msg.obj = infoSimples;
        mHandler.sendMessage(msg);
    }

    @Override
    public void updateEntryEvents(List<Entry> entryEevents) {
        Message msg = new Message();
        msg.what = GENERATE_ENTRY_EVENTS;
        msg.obj = entryEevents;
        mHandler.sendMessage(msg);
    }

    @Override
    public void updateText(int index, Object updateContent) {
        Message msg = new Message();
        msg.obj = updateContent;
        msg.what = index;
        mHandler.sendMessage(msg);
    }

    @Override
    public void showToast(int index, String toastContent) {
        Message msg = new Message();
        msg.what = index;
        msg.obj = toastContent;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit_behind_activiy);
        ButterKnife.bind(this);
        unit = SettingTool.getConfig(this).getWeightUnit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditorMode = getIntent().getIntExtra(MODE_KEY, MODE_GENERATE);

        // 设置视图
        mPresenter.setView(this);
        // 初始化带有prxoy参数的试图
        mPresenter.initViewWithProxy();
        // 设置圆形seekbar
        mSeekBar.setOnSeekBarChangeListener(this);
        mPresenter.generateItem();
        mPresenter.generateBean();

    }

    /**
     * 初始化视图，由Presenter进行回调
     *
     * @param proxy BakeReportProxy
     */
    @Override
    public void init(final BakeReportProxy proxy) {

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
        cookedWeight.setHint("当前生豆为" + proxy.getRawBeanWeight() + MyApplication.weightUnit);
        if (mEditorMode == MODE_EDITOR) {
            reportDelete.setVisibility(View.VISIBLE);
            cookedWeight.setText(proxy.getBakeReport().getCookedBeanWeight());
            // 因为实际上就40的区间，保存是保存30-70的值
            mSeekBar.setCurProcess((int) Float.parseFloat(proxy.getBakeDegree()) - 30);
            // TODO 需要设置颜色块的内容

            reportDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HttpUtils.enqueue(URLs.getDeleteBakeReport(proxy.getBakeReport().getId()), null, new Callback() {
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

    @OnClick(R.id.id_bake_behind_save)
    protected void onSave() {
        String weight = cookedWeight.getText().toString();
        mPresenter.setCookedWeight4BakeReport(weight);

        // 保存
        mPresenter.save();

        Intent other = new Intent(this, ReportActivity.class);
        startActivity(other);
        finish();
    }

    @Override
    public void onChanged(CircleSeekBar seekbar, int curValue, double angle) {
        Message msg = new Message();
        msg.what = BAKE_DEGREE;
        String tip = computeColorBlock4Toast(angle);
        int toastValue = computeToastValue(angle);
        int colorValue = Utils.getColor((float)angle);

        Bundle bundle = new Bundle();
        bundle.putInt("toast_value", toastValue);
        bundle.putString("color_block_str", tip);
        bundle.putInt("color_block_val", colorValue);

        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // FIXME 这里收到的是int类型，需要变成float类型
        mPresenter.setBakeDegree(toastValue);
    }

    private void generateItemView(List<Entry> entries) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditBehindActivity.this);
                    builder.setTitle("事件补充");
                    final EditText editText = new EditText(EditBehindActivity.this);
                    editText.setHint(entry.getEvent().getDescription());
                    builder.setView(editText);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.supplyEventInfo(entry, editText.getText().toString());
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

    private void generateBeanInfoView(List<BeanInfoSimple> beanInfoSimples) {
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
                    mPresenter.setCurUpdateBeanInfo(temp);
                    curBeanButton = beanName;
                    Intent intent = new Intent(EditBehindActivity.this, DialogBeanSelected.class);
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
        if (resultCode == SELECT_BEAN) {
            BeanInfo beanInfo = (BeanInfo) data.getSerializableExtra("beanInfo");
            // 更新BeanInfo
            if (beanInfo != null) {
                mPresenter.updateBeanInfo(beanInfo);
            }
        }
    }
}
