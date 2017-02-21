package com.dhy.coffeesecret.ui.container;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditBeanActivity extends AppCompatActivity {

    private static final String TAG = "EditBeanActivity";

    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.btn_cancel)
    TextView btnCancel;
    @Bind(R.id.btn_save)
    TextView btnSave;
    @Bind(R.id.edit_icon)
    ImageView editIcon;
    @Bind(R.id.edit_name)
    TextView editName;
    @Bind(R.id.edit_area)
    TextView editArea;
    @Bind(R.id.edit_manor)
    TextView editManor;
    @Bind(R.id.edit_altitude)
    EditText editAltitude;
    @Bind(R.id.edit_species)
    TextView editSpecies;
    @Bind(R.id.edit_level)
    Spinner editLevel;
    @Bind(R.id.edit_water_content)
    EditText editWaterContent;
    @Bind(R.id.edit_handler)
    Spinner editHandler;
    @Bind(R.id.edit_supplier)
    EditText editSupplier;
    @Bind(R.id.edit_price)
    EditText editPrice;
    @Bind(R.id.edit_weight)
    EditText editWeight;
    @Bind(R.id.edit_buy_date)
    TextView editBuyDate;
    @Bind(R.id.edit_layout_area)
    RelativeLayout editLayoutArea;
    @Bind(R.id.edit_layout_manor)
    RelativeLayout editLayoutManor;
    @Bind(R.id.edit_layout_species)
    RelativeLayout editLayoutSpecies;

    private TimePickerView pvTime;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String[] levelArray;
    private String[] handlerArray;
    private String currentLevel;
    private String currentHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bean);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

        init();
        initDatePicker();
    }

    private void initDatePicker() {
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

        levelArray = getResources().getStringArray(R.array.level);
        handlerArray = getResources().getStringArray(R.array.handler);

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
        editArea.setText(beanInfo.getArea());
        editManor.setText(beanInfo.getManor());
        editAltitude.setText(beanInfo.getAltitude());
        editSpecies.setText(beanInfo.getSpecies());
        editLevel.setSelection(getLevelSelection(beanInfo.getLevel()), true);
        editWaterContent.setText(beanInfo.getWaterContent() * 100 + "");
        editHandler.setSelection(getHandlerSelection(beanInfo.getHandler()), true);
        editSupplier.setText(beanInfo.getSupplier());
        editPrice.setText(beanInfo.getPrice() + "");
        editWeight.setText(beanInfo.getWeight() + "");
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

    @OnClick({R.id.btn_cancel, R.id.btn_save, R.id.edit_layout_area, R.id.edit_layout_manor, R.id.edit_layout_species, R.id.edit_buy_date})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_cancel:
                exitToRight();
                break;
            case R.id.btn_save:
                saveBeanInfo();
                break;
            case R.id.edit_layout_area:
                intent = new Intent(EditBeanActivity.this, SelectInfoActivity.class);
                intent.putExtra("info_type", "area");
                startActivityForResult(intent, AREA);
                exitToLeft();
                break;
            case R.id.edit_layout_manor:
                intent = new Intent(EditBeanActivity.this, SelectInfoActivity.class);
                intent.putExtra("info_type", "manor");
                startActivityForResult(intent, MANOR);
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

        BeanInfo beanInfo = new BeanInfo();

        beanInfo.setName(editName.getText().toString());
        beanInfo.setArea(editArea.getText().toString());
        beanInfo.setManor(editManor.getText().toString());
        beanInfo.setAltitude(editAltitude.getText().toString());
        beanInfo.setSpecies(editSpecies.getText().toString());
        beanInfo.setLevel(currentLevel);
        beanInfo.setWaterContent(Float.parseFloat(editWaterContent.getText().toString()));
        beanInfo.setHandler(currentHandler);
        beanInfo.setSupplier(editSupplier.getText().toString());
        beanInfo.setPrice(Double.parseDouble(editPrice.getText().toString()));
        beanInfo.setWeight(Double.parseDouble(editWeight.getText().toString()));
        beanInfo.setDate(parseDate(editBuyDate.getText().toString()));

        Log.i(TAG, "saveBeanInfo: " + beanInfo.toString());
        exitToRight(beanInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Message msg = new Message();
            String info = data.getStringExtra("info");
            switch (requestCode) {
                case AREA:
                    msg.what = AREA;
                    break;
                case MANOR:
                    msg.what = MANOR;
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
    private static final int AREA = 1234;
    private static final int MANOR = 2345;
    private static final int SPECIES = 3456;
    private EditBeanHandler mHandler = new EditBeanHandler(EditBeanActivity.this);

    class EditBeanHandler extends Handler {
        private final WeakReference<EditBeanActivity> mActivity;

        public EditBeanHandler(EditBeanActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final EditBeanActivity activity = mActivity.get();
            switch (msg.what) {
                case AREA:
                    activity.editArea.setText((String)msg.obj);
                    break;
                case MANOR:
                    activity.editManor.setText((String)msg.obj);
                    break;
                case SPECIES:
                    activity.editSpecies.setText((String)msg.obj);
                    break;
                default:
                    break;
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
}
