package com.dhy.coffeesecret.ui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.Utils;

import java.lang.ref.WeakReference;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int SHOW_TEXT_DIALOG = 111;
    private static final int SHOW_WHEEL_DIALOG = 222;
    private static final int INTENT_TO_QUICK_EVENT = 333;
    private static final int INTENT_TO_LINES_COLOR = 444;
    @Bind(R.id.check_quick_start)
    CheckBox checkQuickStart;
    @Bind(R.id.quick_start)
    RelativeLayout quickStart;
    @Bind(R.id.check_confirm_again)
    CheckBox checkConfirmAgain;
    @Bind(R.id.confirm_again)
    RelativeLayout confirmAgain;
    @Bind(R.id.text_weight_unit)
    TextView textWeightUnit;
    @Bind(R.id.weight_unit)
    RelativeLayout weightUnit;
    @Bind(R.id.text_temperature_unit)
    TextView textTemperatureUnit;
    @Bind(R.id.temperature_unit)
    RelativeLayout temperatureUnit;
    @Bind(R.id.text_refer_degree)
    TextView textReferDegree;
    @Bind(R.id.refer_degree)
    RelativeLayout referDegree;
    @Bind(R.id.text_time_shaft)
    TextView textTimeShaft;
    @Bind(R.id.time_shaft)
    RelativeLayout timeShaft;
    @Bind(R.id.text_temperature_shaft)
    TextView textTemperatureShaft;
    @Bind(R.id.temperature_shaft)
    RelativeLayout temperatureShaft;
    @Bind(R.id.text_temperature_line)
    TextView textTemperatureLine;
    @Bind(R.id.temperature_line)
    RelativeLayout temperatureLine;
    @Bind(R.id.text_heating_line)
    TextView textHeatingLine;
    @Bind(R.id.heating_line)
    RelativeLayout heatingLine;
    @Bind(R.id.activity_settings)
    LinearLayout activitySettings;
    @Bind(R.id.btn_back)
    ImageView btnBack;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.line_color)
    RelativeLayout lineColor;
    private Context mContext;
    private UniversalConfiguration config;
    private SettingsHandler mHandler = new SettingsHandler(this);

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mContext = SettingsActivity.this;

        init();
    }

    private void init() {
        titleText.setText("通用设置");

        config = SettingTool.getConfig(this);
        checkQuickStart.setChecked(config.isQuickStart());
        checkConfirmAgain.setChecked(config.isDoubleClick());
        textWeightUnit.setText(config.getWeightUnit());
        textTemperatureUnit.setText(config.getTempratureUnit());
        textReferDegree.setText(config.getReferDegree());
        textTimeShaft.setText(config.getMaxX() + " 分钟");
        textTemperatureShaft.setText("最高" + config.getMaxLeftY() + config.getTempratureUnit());
        textTemperatureLine.setText(config.getTempratureSmooth() + "级");
        textHeatingLine.setText(config.getTempratureAccSmooth() + "级");
    }

    @OnClick({R.id.btn_back, R.id.quick_start, R.id.confirm_again, R.id.quick_event, R.id.weight_unit
            , R.id.temperature_unit, R.id.refer_degree, R.id.time_shaft, R.id.temperature_shaft
            , R.id.temperature_line, R.id.heating_line, R.id.line_color})
    public void onClick(View view) {
        Message msg;
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            case R.id.quick_start:
                checkQuickStart.setChecked(!checkQuickStart.isChecked());
                config.setQuickStart(checkQuickStart.isChecked());
                break;
            case R.id.confirm_again:
                checkConfirmAgain.setChecked(!checkConfirmAgain.isChecked());
                config.setDoubleClick(checkConfirmAgain.isChecked());
                break;
            case R.id.quick_event:
                msg = new Message();
                msg.what = INTENT_TO_QUICK_EVENT;
                msg.arg1 = R.id.quick_event;
                mHandler.sendMessage(msg);
                break;
            case R.id.weight_unit:
                msg = new Message();
                msg.what = SHOW_TEXT_DIALOG;
                msg.arg1 = R.id.weight_unit;
                msg.obj = new String[]{"克", "千克", "磅"};
                mHandler.sendMessage(msg);
                break;
            case R.id.temperature_unit:
                msg = new Message();
                msg.what = SHOW_TEXT_DIALOG;
                msg.arg1 = R.id.temperature_unit;
                msg.obj = new String[]{"℃", "℉"};
                mHandler.sendMessage(msg);
                break;
            case R.id.refer_degree:
                msg = new Message();
                msg.what = SHOW_TEXT_DIALOG;
                msg.arg1 = R.id.refer_degree;
                msg.obj = new String[]{"Agtron", "Brother", "Candy", "Death"};
                mHandler.sendMessage(msg);
                break;
            case R.id.time_shaft:
                msg = new Message();
                msg.what = SHOW_WHEEL_DIALOG;
                msg.arg1 = R.id.time_shaft;
                msg.arg2 = R.layout.item_setting_dialog_2;
                mHandler.sendMessage(msg);
                break;
            case R.id.temperature_shaft:
                msg = new Message();
                msg.what = SHOW_WHEEL_DIALOG;
                msg.arg1 = R.id.temperature_shaft;
                msg.arg2 = R.layout.item_setting_dialog_3;
                mHandler.sendMessage(msg);
                break;
            case R.id.temperature_line:
                msg = new Message();
                msg.what = SHOW_WHEEL_DIALOG;
                msg.arg1 = R.id.temperature_line;
                msg.arg2 = R.layout.item_setting_dialog_2;
                mHandler.sendMessage(msg);
                break;
            case R.id.heating_line:
                msg = new Message();
                msg.what = SHOW_WHEEL_DIALOG;
                msg.arg1 = R.id.heating_line;
                msg.arg2 = R.layout.item_setting_dialog_2;
                mHandler.sendMessage(msg);
                break;
            case R.id.line_color:
                msg = new Message();
                msg.what = INTENT_TO_LINES_COLOR;
                msg.arg1 = R.id.line_color;
                mHandler.sendMessage(msg);
                break;
        }
    }

    private void showTextDialog(final String[] dialogItems, final int viewId) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setItems(dialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String whichItem = dialogItems[which];
                        switch (viewId) {
                            case R.id.weight_unit:
                                textWeightUnit.setText(whichItem);
                                config.setWeightUnit(Utils.convertUnitChineses2Eng(whichItem));
                                ((MyApplication) getApplicationContext()).weightUnit = Utils.convertUnitChineses2Eng(whichItem);
                                break;
                            case R.id.temperature_unit:
                                textTemperatureUnit.setText(whichItem);
                                if (!whichItem.equals(config.getTempratureUnit())) {
                                    int maxLeftY = 0;
                                    int maxRightY = 0;
                                    switch (whichItem) {
                                        case "℃":
                                            maxLeftY = (int) ((config.getMaxLeftY() - 32) / 1.8);
                                            maxRightY = (int) ((config.getMaxRightY() - 32) / 1.8);
                                            ((MyApplication) getApplicationContext()).tempratureUnit = "℃";
                                            break;
                                        case "℉":
                                            maxLeftY = (int) (config.getMaxLeftY() * 1.8 + 32);
                                            maxRightY = (int) (config.getMaxRightY() * 1.8 + 32);
                                            ((MyApplication) getApplicationContext()).tempratureUnit = "℉";
                                            break;
                                    }
                                    textTemperatureShaft.setText("最高" + maxLeftY
                                            + whichItem);
                                    config.setMaxLeftY(maxLeftY);
                                    config.setMaxRightY(maxRightY);
                                }
                                config.setTempratureUnit(whichItem);
                                break;
                            case R.id.refer_degree:
                                textReferDegree.setText(whichItem);
                                config.setReferDegree(whichItem);
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
        saveAll();
    }

    private void showWheelDialog(final int viewId, int layoutResources) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();

        View dialogView = LayoutInflater.from(mContext).inflate(layoutResources, null);
        TextView titleText = (TextView) dialogView.findViewById(R.id.title_text);
        TextView itemUnit = (TextView) dialogView.findViewById(R.id.item_unit);
        final MaterialNumberPicker numberHundred = layoutResources == R.layout.item_setting_dialog_3 ?
                (MaterialNumberPicker) dialogView.findViewById(R.id.number_hundred) : null;
        final MaterialNumberPicker numberTen = (MaterialNumberPicker) dialogView.findViewById(R.id.number_ten);
        final MaterialNumberPicker numberSingle = (MaterialNumberPicker) dialogView.findViewById(R.id.number_single);
        Button btnConfirm = (Button) dialogView.findViewById(R.id.btn_confirm);

        titleText.setText("时间轴");
        switch (viewId) {
            case R.id.time_shaft:
                numberTen.setValue(config.getMaxX() / 10);
                numberSingle.setValue(config.getMaxX() % 10);
                itemUnit.setText("分");
                break;
            case R.id.temperature_shaft:
                numberHundred.setValue(config.getMaxLeftY() / 100);
                numberTen.setValue(config.getMaxLeftY() / 10 % 10);
                numberSingle.setValue(config.getMaxLeftY() % 10);
                itemUnit.setText(config.getTempratureUnit());
                break;
            case R.id.temperature_line:
                numberTen.setValue(config.getTempratureSmooth() / 10);
                numberSingle.setValue(config.getTempratureSmooth() % 10);
                itemUnit.setText("");
                break;
            case R.id.heating_line:
                numberTen.setValue(config.getTempratureAccSmooth() / 10);
                numberSingle.setValue(config.getTempratureAccSmooth() % 10);
                itemUnit.setText("");
                break;
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max;
                switch (viewId) {
                    case R.id.time_shaft:
                        max = numberTen.getValue() * 10 + numberSingle.getValue();
                        textTimeShaft.setText("最长" + max + "分钟");
                        config.setMaxX(max);
                        break;
                    case R.id.temperature_shaft:
                        max = numberHundred.getValue() * 100 + numberTen.getValue() * 10 + numberSingle.getValue();
                        textTemperatureShaft.setText("最高" + max + textTemperatureUnit.getText());
                        config.setMaxLeftY(max);
                        break;
                    case R.id.temperature_line:
                        max = numberTen.getValue() * 10 + numberSingle.getValue();
                        textTemperatureLine.setText("每秒的平均数为: " + max);
                        config.setTempratureSmooth(max);
                        break;
                    case R.id.heating_line:
                        max = numberTen.getValue() * 10 + numberSingle.getValue();
                        textHeatingLine.setText("每秒的平均数为: " + max);
                        config.setTempratureAccSmooth(max);
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(dialogView);
        }
        saveAll();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    public void saveAll() {
        SettingTool.saveConfig(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAll();
    }

    class SettingsHandler extends Handler {
        private final WeakReference<SettingsActivity> mActivity;

        public SettingsHandler(SettingsActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SettingsActivity activity = mActivity.get();
            switch (msg.what) {
                case SHOW_TEXT_DIALOG:
                    activity.showTextDialog((String[]) msg.obj, msg.arg1);
                    break;
                case SHOW_WHEEL_DIALOG:
                    activity.showWheelDialog(msg.arg1, msg.arg2);
                    break;
                case INTENT_TO_QUICK_EVENT:
                    Intent quickEvent = new Intent(activity, QuickEventActivity.class);
                    startActivity(quickEvent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    break;
                case INTENT_TO_LINES_COLOR:
                    Intent linesColor = new Intent(activity, LinesColorActivity.class);
                    startActivity(linesColor);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    break;
            }
        }
    }
}
