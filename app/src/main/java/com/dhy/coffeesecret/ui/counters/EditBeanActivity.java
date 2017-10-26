package com.dhy.coffeesecret.ui.counters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.url.UrlBean;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SystemStatusBarUtils;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jesse.nativelogger.NLogger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditBeanActivity extends AppCompatActivity {
    private static final String TAG = "EditBeanActivity";
    private static final int COUNTRY = 1234;
    private static final int AREA = 2345;
    private static final int MANOR = 3456;
    private static final int SPECIES = 4567;
    private static final int BEAN_NAME = 5678;
    private static final int BEAN_ICON = 6789;
    private static final int TOAST_1 = 7890;
    private static final int TOAST_2 = 7899;
    private static final int TOAST_3 = 7889;
    private static final int AREA_NONE = 7755;
    private static final int SPECIES_NONE = 6644;
    private static final int LEVEL_NONE = 7425;
    private static List<String> species;
    @Bind(R.id.btn_cancel)
    TextView btnCancel;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.btn_save)
    TextView btnSave;
    @Bind(R.id.edit_icon)
    ImageView editIcon;
    @Bind(R.id.edit_name)
    EditText editName;
    @Bind(R.id.edit_country)
    TextView editCountry;
    @Bind(R.id.edit_layout_country)
    RelativeLayout editLayoutCountry;
    @Bind(R.id.edit_area)
    EditText editArea;
    @Bind(R.id.edit_manor)
    EditText editManor;
    @Bind(R.id.edit_altitude)
    EditText editAltitude;
    @Bind(R.id.edit_species)
    TextView editSpecies;
    @Bind(R.id.edit_layout_species)
    RelativeLayout editLayoutSpecies;
    @Bind(R.id.edit_level)
    Spinner editLevel;
    @Bind(R.id.edit_water_content)
    EditText editWaterContent;
    @Bind(R.id.edit_handler)
    Spinner editHandler;
    /*    @Bind(R.id.edit_another_handler)
        EditText editAnotherHandler;*/
    @Bind(R.id.edit_handler_temp)
    TextView editHandlerTemp;
    @Bind(R.id.edit_supplier)
    EditText editSupplier;
    @Bind(R.id.edit_price)
    EditText editPrice;
    @Bind(R.id.edit_weight)
    EditText editWeight;
    @Bind(R.id.edit_buy_date)
    TextView editBuyDate;
    @Bind(R.id.edit_weight_unit)
    TextView editWeightUnit;
    @Bind(R.id.id_bean_delete)
    Button beanDelete;
    @Bind(R.id.edit_level_temp)
    TextView editLevelTemp;
    private TimePickerView pvTime;
    // 格式化器
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    // 等级
    private String[] levelArray;
    // 处理方式
    private String[] handlerArray;
    // 当前选择等级
    private String currentLevel;
    // 当前选择处理方式
    private String currentHandler;
    // 当前的图片
    private String drawPath;
    private Context mContext;
    private int id; //bean的id 如果新添加的豆子 id = 0 否则等于原本的id
    private EditBeanHandler mHandler = new EditBeanHandler(EditBeanActivity.this);
    private BeanInfo beanInfo;
    private int count = 0;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bean);
        application = ((MyApplication) getApplication());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        SystemStatusBarUtils.steepToolBar(this);
        mContext = EditBeanActivity.this;
        initParams();
        init();
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        // 获取等级列表
        levelArray = getResources().getStringArray(R.array.level);
        // 获取处理列表
        handlerArray = getResources().getStringArray(R.array.handler);
        // 默认当前选择是第一个
        currentLevel = levelArray[0];
        // 默认当前选择是第一个
        currentHandler = handlerArray[0];

        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                editBuyDate.setText(format.format(date));
            }
        });
    }

    /**
     * 初始化
     */
    private void init() {
        // 豆子信息
        beanInfo = (BeanInfo) getIntent().getParcelableExtra("beanInfo");

        // 如果当前豆子信息为空，那么是添加豆子，如果豆子信息不为空，那么是编辑豆子
        if (beanInfo == null) {
            titleText.setText("添加豆子");
            beanInfo = new BeanInfo();
            count = 2;
        } else {
            Log.i(TAG, "init: beanInfo" + beanInfo.toString());
            titleText.setText("编辑豆子");
            beanDelete.setVisibility(View.VISIBLE);
        }

        if (beanInfo.getDrawablePath() == null || beanInfo.getDrawablePath().trim().equals("")) {
            beanInfo.setDrawablePath("default");
            drawPath = "default";
        } else {
            drawPath = beanInfo.getDrawablePath();
            editIcon.setImageResource(Utils.getContainerIcon(drawPath));
        }
        id = beanInfo.getId();

        editName.setText(beanInfo.getName());
        editCountry.setText(beanInfo.getCountry());
        if ("".equals(editCountry.getText().toString().trim())) {
            editCountry.setText("未知");
        }
        editArea.setText(beanInfo.getArea());
        editManor.setText(beanInfo.getManor());
        editAltitude.setText(beanInfo.getAltitude());
        editSpecies.setText(beanInfo.getSpecies());
        // editLevel.setSelection(getLevelSelection(beanInfo.getLevel()), true);
        /*
         * 因为有其他选项的存在所以选择直接显示在遮罩上面
         */
        editLevelTemp.setText(beanInfo.getLevel());
        currentLevel = beanInfo.getLevel();
        editWaterContent.setText(beanInfo.getWaterContent() + "");
        if ("0.0".equals(editWaterContent.getText().toString().trim())) {
            editWaterContent.setText("");
        }
        // editHandler.setSelection(getHandlerSelection(beanInfo.getProcess()), true);
        editHandlerTemp.setText(beanInfo.getProcess());
        currentHandler = beanInfo.getProcess();

        editSupplier.setText(beanInfo.getSupplier());
        editPrice.setText(beanInfo.getPrice() + "");
        if ("0.0".equals(editPrice.getText().toString().trim())) {
            editPrice.setText("");
        }
        editWeight.setText(Utils.get2PrecisionFloat((float) beanInfo.getStockWeight()) + "");
        if ("0.0".equals(editWeight.getText().toString().trim())) {
            editWeight.setText("");
        }
        // editWeightUnit.setText(SettingTool.getConfig().getWeightUnit());
        editBuyDate.setText(formatDate(beanInfo.getDate()));

        editLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (count < 2) {
                    ++count;
                    return;
                }
                currentLevel = levelArray[position];
                if (position == levelArray.length - 1) {
                    final EditText editText = new EditText(EditBeanActivity.this);
                    AlertDialog dialog = new AlertDialog.Builder(EditBeanActivity.this).setTitle("其他...")
                            .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 设置当前等级
                                    currentLevel = editText.getText().toString();
                            /*
                             * 因为spinner中的其他选项不能显示在选完后的界面上（选完后就是显示其他）
                             * 固采用一个TextView作为内容，其中设置spinner的alpha为0
                             * 用editLevelTemp遮住spinner
                             */
                                    editLevelTemp.setText(currentLevel);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    editLevel.setSelection(0);
                                    return;
                                }
                            }).setCancelable(false).create();
                    dialog.show();
                    ((TextView) view).setText(currentLevel);
                } else {
                    editLevelTemp.setText(levelArray[position]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        editHandler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NLogger.i(TAG, "点击处理方式-> 选中第" + position + "个位置, 值为:" + handlerArray[position]);
                if (count < 2) {
                    ++count;
                    return;
                }
                currentHandler = handlerArray[position];
                // 表示点击的时最后一个
                if (position == handlerArray.length - 1) {
                    final EditText editText = new EditText(EditBeanActivity.this);
                    AlertDialog dialog = new AlertDialog.Builder(EditBeanActivity.this).setTitle("其他...")
                            .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 设置当前等级
                                    currentHandler = editText.getText().toString();
                            /*
                             * 因为spinner中的其他选项不能显示在选完后的界面上（选完后就是显示其他）
                             * 固采用一个TextView作为内容，其中设置spinner的alpha为0
                             * 用editLevelTemp遮住spinner
                             */
                                    editHandlerTemp.setText(currentHandler);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    editHandler.setSelection(0);
                                    return;
                                }
                            }).setCancelable(false).create();
                    dialog.show();
                    ((TextView) view).setText(currentHandler);
                } else {
                    editHandlerTemp.setText(handlerArray[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    /**
     * 格式化日期
     *
     * @param date 日期
     * @return
     */
    private String formatDate(Date date) {
        if (date == null) {
            return format.format(new Date());
        }
        return format.format(date);
    }

    /**
     * 解析日期
     *
     * @param dateString yyyy-MM-dd HH:ss
     * @return
     */
    private Date parseDate(String dateString) {
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取显示的处理方式
     *
     * @param handler 豆子的处理方式
     * @return 该处理方式在 spinner 中的位置
     */
    private int getHandlerSelection(String handler) {

        if (handler != null) {
            for (int i = 0; i < handlerArray.length; i++) {
                if (handler.equals(handlerArray[i])) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 获取初始被选择的等级
     *
     * @param level 豆子的等级
     * @return 该等级在 spinner 的位置
     */
    private int getLevelSelection(String level) {

        if (level != null) {
            for (int i = 0; i < levelArray.length; i++) {
                if (level.equals(levelArray[i])) {
                    return i;
                }
            }
        }

        return 0;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_save, R.id.edit_layout_country, R.id.edit_layout_species, R.id.edit_buy_date, R.id.id_bean_delete})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                exitToRight();
                break;
            case R.id.btn_save:
                btnSave.setClickable(false);
                saveBeanInfo();
                break;
            case R.id.edit_layout_country:
                intent = new Intent(EditBeanActivity.this, SelectInfoActivity.class);
                intent.putExtra("info_type", "country");
                startActivityForResult(intent, COUNTRY);
                exitToLeft();
                break;
            case R.id.edit_layout_species:
                intent = new Intent(EditBeanActivity.this, SelectInfoActivity.class);
                intent.putExtra("info_type", "species");
                startActivityForResult(intent, SPECIES);
                exitToLeft();
                break;
            case R.id.edit_buy_date:
                pvTime.show();
                break;
            case R.id.id_bean_delete:
                Dialog dialog = new AlertDialog.Builder(mContext).setMessage("是否确认删除").setTitle("删除").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBeanInfo();
                        BeanInfoActivity.getInstance().finish();
                        EditBeanActivity.this.finish();

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();
                break;
            default:
                break;
        }
    }

    private void deleteBeanInfo() {
        // 添加新的BeanInfo,因为删除只需要id
        BeanInfo beanInfo = new BeanInfo();
        // 单纯添加其id
        beanInfo.setId(id);

        String token = application.getToken();
        Log.e(TAG, UrlBean.delete(token, id));
        HttpUtils.enqueue(this, token, UrlBean.delete(token, id), null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "输出错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.sendEmptyMessage(TOAST_3);
            }
        });

    }

    private void saveBeanInfo() {
        // TODO 判断必填值是否输入
        /*if (editArea == null || editManor == null || editWeight.getText().toString().trim().equals("")) {
            mHandler.sendEmptyMessage(TOAST_3);
            return;
        }*/
        if (!checkValidation()) {
            btnSave.setClickable(true);
            return;
        }
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setId(id);
        beanInfo.setDrawablePath(drawPath);
        // TODO
        String country = editCountry.getText().toString();
        String continent = ((MyApplication) getApplicationContext()).getContinent(country);
        beanInfo.setContinent(continent);
        Log.e("EditBeanActivity", beanInfo.getContinent());
        beanInfo.setCountry(country);

        // 如果用户没填该项则默认给予一个未知
        beanInfo.setArea(editArea.getText().toString());

        beanInfo.setManor(editManor.getText().toString());
        beanInfo.setAltitude(editAltitude.getText().toString());
        beanInfo.setSpecies(editSpecies.getText().toString());
        beanInfo.setLevel(currentLevel);
        beanInfo.setWaterContent(Float.parseFloat(editWaterContent.getText().toString()));
        beanInfo.setSupplier(editSupplier.getText().toString());
        beanInfo.setPrice(Double.parseDouble(editPrice.getText().toString()));
        try {
            beanInfo.setStockWeight(Float.parseFloat(editWeight.getText().toString() + ""));
        } catch (NumberFormatException e) {
            beanInfo.setStockWeight(Float.parseFloat("0.0"));
        }
        beanInfo.setDate(parseDate(editBuyDate.getText().toString()));
        // TODO 如果豆名为空字串或者为null，则默认给予国家+豆种格式
        beanInfo.setName(editName.getText().toString());
        if ("".equals(beanInfo.getName().trim())) {
            beanInfo.setName(editCountry.getText() + "" + editSpecies.getText());
        }

        beanInfo.setProcess(currentHandler);

        updateBeanInfo(beanInfo);
        Log.i(TAG, "saveBeanInfo: " + beanInfo.toString());
    }

    private void updateBeanInfo(final BeanInfo beanInfo) {

        String token = application.getToken();
        String url = UrlBean.add(token);
        HttpUtils.enqueue(EditBeanActivity.this, token, url, beanInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NLogger.e(TAG, "更新豆种信息错误：" + e);
                mHandler.sendEmptyMessage(TOAST_2);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.sendEmptyMessage(TOAST_1);
                exitToRight(beanInfo);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Message msg = new Message();
            String info = data.getStringExtra("info");
            switch (requestCode) {
                case COUNTRY:
                    msg.what = COUNTRY;
                    break;
                case SPECIES:
                    msg.what = SPECIES;
                    break;
            }
            if (info != null) {
                msg.obj = info;
                mHandler.sendMessage(msg);
            }
        }
    }

    private void exitToRight(BeanInfo beanInfo) {
        Intent intent = new Intent();
        intent.putExtra("new_bean_info", beanInfo);
        setResult(RESULT_OK, intent);
        exitToRight();
    }

    private void exitToRight() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    private void exitToLeft() {
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitToRight();
    }

    /**
     * 在保存或更新信息之前检测输入栏中的数值是否符合要求
     *
     * @return
     */
    private boolean checkValidation() {
        if ("".equals(editArea.getText().toString().trim())) {
            // mHandler.sendEmptyMessage(AREA_NONE);
            editArea.setText("未知");
        }
        if ("".equals(editPrice.getText().toString().trim())) {
            editPrice.setText("0.0");
        }
        if ("".equals(editWaterContent.getText().toString().trim())) {
            editWaterContent.setText("0.0");
        }
        if ("".equals(editWeight.getText().toString().trim())) {
            editWeight.setText("0.0");
        }
        if ("".equals(editSpecies.getText().toString().trim())) {
            mHandler.sendEmptyMessage(SPECIES_NONE);
            return false;
        }
        return true;
    }

    class EditBeanHandler extends Handler {

        private final WeakReference<EditBeanActivity> mActivity;
        String country = "";
        String species = "";

        public EditBeanHandler(EditBeanActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = null;
            final EditBeanActivity activity = mActivity.get();
            switch (msg.what) {
                case COUNTRY:
                    activity.editCountry.setText((String) msg.obj);
                    mHandler.sendEmptyMessage(BEAN_NAME);
                    break;
                case AREA:
                    activity.editArea.setText((String) msg.obj);
                    break;
                case MANOR:
                    activity.editManor.setText((String) msg.obj);
                    break;
                case SPECIES:
                    str = (String) msg.obj;
                    // 取出除第一个字符之后的小类
                    activity.editSpecies.setText(str);
                    // 将这个字符串发给BEAN_ICON进行处理
                    msg = new Message();
                    msg.what = BEAN_ICON;
                    msg.obj = str;
                    mHandler.sendMessage(msg);
                    break;
                case BEAN_NAME:
                    country = activity.editCountry.getText().toString();
                    species = activity.editSpecies.getText().toString();
                    break;
                case BEAN_ICON:
                    str = ((String) msg.obj).substring(0, 2);
                    drawPath = str.toLowerCase();
//                    if (str.toLowerCase().contains("aa")) {
//                        drawPath = R.drawable.ic_container_aa + "";
//                    } else if (str.toLowerCase().contains("ac")) {
//                        drawPath = R.drawable.ic_container_ac + "";
//                    } else if (str.toLowerCase().contains("ae")) {
//                        drawPath = R.drawable.ic_container_ae + "";
//                    } else {
//                        drawPath = R.drawable.ic_container_al + "";
//                    }
                    activity.editIcon.setImageResource(Utils.getContainerIcon(drawPath));
                    break;
                case TOAST_1:
                    T.showShort(mContext, "保存成功");
                    break;
                case TOAST_2:
                    activity.btnSave.setClickable(true);
                    T.showShort(mContext, "保存失败，请稍后再试");
                    break;
                case TOAST_3:
                    T.showShort(mContext, "删除成功");
                    break;
                case AREA_NONE:
                    T.showShort(mContext, "产地不能为空");
                    break;
                case SPECIES_NONE:
                    T.showShort(mContext, "豆种不能为空");
                    break;
                default:
                    break;
            }
        }
    }
}
