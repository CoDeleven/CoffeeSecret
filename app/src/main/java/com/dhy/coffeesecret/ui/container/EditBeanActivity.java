package com.dhy.coffeesecret.ui.container;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.URLs;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditBeanActivity extends AppCompatActivity {

    @Bind(R.id.btn_cancel)
    TextView btnCancel;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.btn_save)
    TextView btnSave;
    @Bind(R.id.edit_icon)
    ImageView editIcon;
    @Bind(R.id.edit_name)
    TextView editName;
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
    @Bind(R.id.edit_another_handler)
    EditText editAnotherHandler;
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
    private TimePickerView pvTime;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String[] levelArray;
    private String[] handlerArray;
    private String currentLevel;
    private String currentHandler;
    private String drawPath;
    private Context mContext;

    private static final String TAG = "EditBeanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bean);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

        mContext = EditBeanActivity.this;

        initParams();
        init();
    }

    private void initParams() {
        levelArray = getResources().getStringArray(R.array.level);
        handlerArray = getResources().getStringArray(R.array.handler);
        currentLevel = levelArray[0];
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

    private void init() {
        BeanInfo beanInfo = (BeanInfo) getIntent().getSerializableExtra("beanInfo");

        if (beanInfo == null) {
            titleText.setText("添加豆种");
            beanInfo = new BeanInfo();
        } else {
            Log.i(TAG, "init: beanInfo" + beanInfo.toString());
            titleText.setText("编辑豆种");
        }

        editIcon.setImageResource(R.drawable.ic_container_add_bean);
        editName.setText(beanInfo.getName());
        editCountry.setText(beanInfo.getCountry());
        editArea.setText(beanInfo.getArea());
        editManor.setText(beanInfo.getManor());
        editAltitude.setText(beanInfo.getAltitude());
        editSpecies.setText(beanInfo.getSpecies());
        editLevel.setSelection(getLevelSelection(beanInfo.getLevel()), true);
        editWaterContent.setText(beanInfo.getWaterContent() * 100 + "");
        editHandler.setSelection(getHandlerSelection(beanInfo.getProcess()), true);
        editAnotherHandler.setEnabled(false);
        editSupplier.setText(beanInfo.getSupplier());
        editPrice.setText(beanInfo.getPrice() + "");
        editWeight.setText(beanInfo.getStockWeight() + "");
        editWeightUnit.setText(SettingTool.getConfig(mContext).getWeightUnit());
        editBuyDate.setText(formatDate(beanInfo.getDate()));

        editLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentLevel = levelArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editHandler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentHandler = handlerArray[position];
                if (currentHandler.equals("其它")) {
                    editAnotherHandler.setEnabled(true);
                } else {
                    editAnotherHandler.setText("");
                    editAnotherHandler.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String formatDate(Date date) {
        if (date == null) {
            return format.format(new Date());
        }
        return format.format(date);
    }

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
     * @param handler   豆子的处理方式
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

    @OnClick({R.id.btn_cancel, R.id.btn_save, R.id.edit_layout_country, R.id.edit_layout_species, R.id.edit_buy_date})
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
            default:
                break;
        }
    }

    private void saveBeanInfo() {
        // TODO
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setDrawablePath(drawPath);
        beanInfo.setName(editName.getText().toString());
        beanInfo.setContinent("");
        beanInfo.setCountry(editCountry.getText().toString());
        beanInfo.setArea(editArea.getText().toString());
        beanInfo.setManor(editManor.getText().toString());
        beanInfo.setAltitude(editAltitude.getText().toString());
        beanInfo.setSpecies(editSpecies.getText().toString());
        beanInfo.setLevel(currentLevel);
        beanInfo.setWaterContent(Float.parseFloat(editWaterContent.getText().toString()));
        beanInfo.setSupplier(editSupplier.getText().toString());
        beanInfo.setPrice(Double.parseDouble(editPrice.getText().toString()));
        beanInfo.setStockWeight(Double.parseDouble(editWeight.getText().toString()));
        beanInfo.setDate(parseDate(editBuyDate.getText().toString()));

        if (currentHandler.equals("其它")) {
            beanInfo.setProcess(editAnotherHandler.getText().toString());
        } else {
            beanInfo.setProcess(currentHandler);
        }
//        Log.i(TAG, "saveBeanInfo: " + beanInfo.toString());
        updateBeanInfo(beanInfo);
        Log.i(TAG, "saveBeanInfo: " + beanInfo.toString());
    }

    private void updateBeanInfo(final BeanInfo beanInfo) {

        HttpUtils.enqueue(URLs.ADD_BEAN_INFO, beanInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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

    private static final int COUNTRY = 1234;
    private static final int AREA = 2345;
    private static final int MANOR = 3456;
    private static final int SPECIES = 4567;
    private static final int BEAN_NAME = 5678;
    private static final int BEAN_ICON = 6789;
    private static final int TOAST_1 = 7890;
    private static final int TOAST_2 = 7899;
    private EditBeanHandler mHandler = new EditBeanHandler(EditBeanActivity.this);

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
                    activity.editSpecies.setText((String) msg.obj);
                    mHandler.sendEmptyMessage(BEAN_NAME);
                    mHandler.sendEmptyMessage(BEAN_ICON);
                    break;
                case BEAN_NAME:
                    country = activity.editCountry.getText().toString();
                    species = activity.editSpecies.getText().toString();
                    if (!TextUtils.isEmpty(country.trim()) &&
                            !TextUtils.isEmpty(species.trim())) {
                        activity.editName.setText(country + species);
                    }
                    break;
                case BEAN_ICON:
                    if (species.toLowerCase().contains("a")) {
                        activity.editIcon.setImageResource(R.drawable.ic_container_aa);
                    } else if (species.toLowerCase().contains("c")) {
                        activity.editIcon.setImageResource(R.drawable.ic_container_ac);
                    } else if (species.toLowerCase().contains("e")) {
                        activity.editIcon.setImageResource(R.drawable.ic_container_ae);
                    } else {
                        activity.editIcon.setImageResource(R.drawable.ic_container_al);
                    }
                    break;
                case TOAST_1:
                    T.showShort(mContext, "保存成功");
                    break;
                case TOAST_2:
                    activity.btnSave.setClickable(true);
                    T.showShort(mContext, "保存失败，请稍后再试");
                    break;
                default:
                    break;
            }
        }
    }
}
