package com.dhy.coffeesecret.ui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.LinesColor;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.views.ColorChooseDialog;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomPackageActivity extends AppCompatActivity {
    private static final String TAG = "CustomPackageActivity";
    private static final int SHOW_COLORS_DIALOG = 111;
    private static final int SHOW_EDIT_DIALOG = 222;
    private static final int CHANGE_PREVIEW_COLOR = 333;
    private static final int SHOW_TOAST = 444;
    public static Integer[] linesColor = {
            R.color.default_0, R.color.default_1, R.color.default_2
            , R.color.default_3, R.color.default_4, R.color.default_5, R.color.default_6,
            R.color.simo_blue300, R.color.simo_green300, R.color.simo_red300, R.color.simo_yellow300,
            R.color.simo_purple700, R.color.simo_pink700, R.color.simo_limea400, R.color.simo_orange300,
            R.color.simo_brown300, R.color.simo_grey500, R.color.simo_lightgreena400, R.color.simo_reda700};

    @Bind(R.id.btn_back)
    ImageView btnBack;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.color_preview_bean)
    RoundedImageView colorPreviewBean;
    @Bind(R.id.btn_choose_bean)
    Button btnChooseBean;
    @Bind(R.id.color_preview_in_wind)
    RoundedImageView colorPreviewInWind;
    @Bind(R.id.btn_choose_in_wind)
    Button btnChooseInWind;
    @Bind(R.id.color_preview_out_wind)
    RoundedImageView colorPreviewOutWind;
    @Bind(R.id.btn_choose_out_wind)
    Button btnChooseOutWind;
    @Bind(R.id.color_preview_acc_bean)
    RoundedImageView colorPreviewAccBean;
    @Bind(R.id.btn_choose_acc_bean)
    Button btnChooseAccBean;
    @Bind(R.id.color_preview_acc_in_wind)
    RoundedImageView colorPreviewAccInWind;
    @Bind(R.id.btn_choose_acc_in_wind)
    Button btnChooseAccInWind;
    @Bind(R.id.color_preview_acc_out_wind)
    RoundedImageView colorPreviewAccOutWind;
    @Bind(R.id.btn_choose_acc_out_wind)
    Button btnChooseAccOutWind;
    @Bind(R.id.color_preview_env)
    RoundedImageView colorPreviewEnv;
    @Bind(R.id.btn_choose_env)
    Button btnChooseEnv;
    @Bind(R.id.activity_custom_package)
    LinearLayout activityCustomPackage;
    private Context mContext;
    private List<Integer> colors;
    private Map<String, Integer> selectedColors;
    private boolean isSaved = false;    // 是否保存标记
    private boolean isEdit = false;     // 是否编辑过的标记
    private UniversalConfiguration config;
    private CustomPackageHandler mHandler = new CustomPackageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_package);
        ButterKnife.bind(this);

        mContext = CustomPackageActivity.this;
        colors = new ArrayList<>();
        selectedColors = new HashMap<>();
        config = SettingTool.getConfig();

        initParams();
        init();
    }

    private void initParams() {
        Collections.addAll(colors, linesColor);

        // 放入默认的前7个元素
        selectedColors.put("colorPreviewBean", R.color.default_0);
        selectedColors.put("colorPreviewInWind", R.color.default_1);
        selectedColors.put("colorPreviewOutWind", R.color.default_2);
        selectedColors.put("colorPreviewAccBean", R.color.default_3);
        selectedColors.put("colorPreviewAccInWind", R.color.default_4);
        selectedColors.put("colorPreviewAccOutWind", R.color.default_5);
        selectedColors.put("colorPreviewEnv", R.color.default_6);

        // 移除上面7个已用的颜色
        int count = 7;
        while (count-- != 0) {
            // 因为ArrayList，删除一个元素后面的元素移前
            colors.remove(0);
        }


    }

    private void init() {
        titleText.setText("自定义曲线");

    }

    @OnClick({R.id.btn_back, R.id.btn_save, R.id.btn_choose_bean, R.id.btn_choose_in_wind, R.id.btn_choose_out_wind, R.id.btn_choose_acc_bean, R.id.btn_choose_acc_in_wind, R.id.btn_choose_acc_out_wind, R.id.btn_choose_env})
    public void onClick(View view) {
        Message msg = null;
        switch (view.getId()) {
            case R.id.btn_back:
                back();
                break;
            case R.id.btn_save:
                save();
                break;
            default:
                msg = new Message();
                msg.what = SHOW_COLORS_DIALOG;
                msg.arg1 = view.getId();
                mHandler.sendMessage(msg);
                break;
        }

    }

    /**
     * 保存所有颜色
     */
    private void save() {
        isSaved = true;

        LinesColor linesColor = getLinesColor();
        config.setBeanColor(Color.parseColor(linesColor.getBeanColor()));
        config.setInwindColor(Color.parseColor(linesColor.getInwindColor()));
        config.setOutwindColor(Color.parseColor(linesColor.getOutwindColor()));
        config.setAccBeanColor(Color.parseColor(linesColor.getAccBeanColor()));
        config.setAccInwindColor(Color.parseColor(linesColor.getAccInwindColor()));
        config.setAccOutwindColor(Color.parseColor(linesColor.getAccOutwindColor()));
        config.setEnvColor(Color.parseColor(linesColor.getEnvColor()));

        mHandler.sendEmptyMessage(SHOW_EDIT_DIALOG);
    }

    /**
     * 显示标题编辑窗口
     */
    private void showEditDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_edit_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);
        builder.setTitle("添加标题");
        editText.setHint("在这里输入标题");
        final AlertDialog dialog = builder.setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = editText.getText().toString();
                        if (!title.trim().equals("")) {
                            if (LinesColorActivity.names.contains(title)) {
                                Toast.makeText(CustomPackageActivity.this, "已存在相同的名字...", Toast.LENGTH_LONG).show();
                                mHandler.sendEmptyMessage(SHOW_EDIT_DIALOG);
                            } else {
                                config.setColorPackageName(title);
                                exit(RESULT_OK, getLinesColor());
                                SettingTool.saveConfig(config);
                            }
                        } else {
                            Toast.makeText(CustomPackageActivity.this, "您尚未输入套餐名称...", Toast.LENGTH_LONG).show();
                            mHandler.sendEmptyMessage(SHOW_EDIT_DIALOG);
                        }
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.show();
    }

    /**
     * 更改颜色
     *
     * @param btnId    圆形图片id
     * @param position 颜色的位置
     */
    private void changePreviewColor(int btnId, int position) {
        isEdit = true;
        switch (btnId) {
            case R.id.btn_choose_bean:
                colorPreviewBean.setImageResource(colors.get(position));
                notifyData("colorPreviewBean", position);
                break;
            case R.id.btn_choose_in_wind:
                colorPreviewInWind.setImageResource(colors.get(position));
                notifyData("colorPreviewInWind", position);
                break;
            case R.id.btn_choose_out_wind:
                colorPreviewOutWind.setImageResource(colors.get(position));
                notifyData("colorPreviewOutWind", position);
                break;
            case R.id.btn_choose_acc_bean:
                colorPreviewAccBean.setImageResource(colors.get(position));
                notifyData("colorPreviewAccBean", position);
                break;
            case R.id.btn_choose_acc_in_wind:
                colorPreviewAccInWind.setImageResource(colors.get(position));
                notifyData("colorPreviewAccInWind", position);
                break;
            case R.id.btn_choose_acc_out_wind:
                colorPreviewAccOutWind.setImageResource(colors.get(position));
                notifyData("colorPreviewAccOutWind", position);
                break;
            case R.id.btn_choose_env:
                colorPreviewEnv.setImageResource(colors.get(position));
                notifyData("colorPreviewEnv", position);
                break;
        }
    }

    // 更新list和map
    private void notifyData(String key, int position) {
        colors.add(selectedColors.get(key));

        // 更新 map 中的颜色
        selectedColors.remove(key);
        selectedColors.put(key, colors.get(position));

        // 移除已选择的颜色
        colors.remove(position);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {

        // 如果颜色没被改变过，那么允许直接返回，携带参数为null
        if (!isEdit) {
            exit(RESULT_CANCELED, null);
            return;
        }

        // 如果单击过保存，那么进入以下判断
        if (isSaved) {
            exit(RESULT_OK, getLinesColor());
        } else {
            showDialog("您还未保存设置，确定要退出吗？");
        }
    }

/*
    private boolean isAllSelected() {

        int i = 0;
        Set<Map.Entry<String, Integer>> colorSet = selectedColors.entrySet();
        Log.i(TAG, "isAllSelected: colorSet = " + colorSet);
        Log.i(TAG, "isAllSelected: selectedColor = " + selectedColors);
        for (Map.Entry<String, Integer> entry : colorSet) {
            if (entry.getValue() == 0) {
                i++;
            }
        }

        return i == 0;
    }
*/

    /**
     * 获取已选中的颜色
     *
     * @return 返回LineColor
     */
    private LinesColor getLinesColor() {
        LinesColor linesColor = new LinesColor();
        linesColor.setPackageName(config.getColorPackageName());
        try {
            linesColor.setBeanColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewBean"))));
            linesColor.setInwindColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewInWind"))));
            linesColor.setOutwindColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewOutWind"))));
            linesColor.setAccBeanColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewAccBean"))));
            linesColor.setAccInwindColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewAccInWind"))));
            linesColor.setAccOutwindColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewAccOutWind"))));
            linesColor.setEnvColor("#" + Integer.toHexString(getResources().getColor(selectedColors.get("colorPreviewEnv"))));
        } catch (Resources.NotFoundException e) {
            linesColor.setBeanColor("#1abc9c");
            linesColor.setInwindColor("#2ecc71");
            linesColor.setOutwindColor("#3498db");
            linesColor.setAccBeanColor("#9b59b6");
            linesColor.setAccInwindColor("#f1c40f");
            linesColor.setAccOutwindColor("#e67e22");
            linesColor.setEnvColor("#e74c3c");
        }
        return linesColor;
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("取消", null)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomPackageActivity.this.finish();
                        setResult(RESULT_CANCELED);
                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    }
                }).create().show();
    }

    private void exit(int resultCode, LinesColor data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("linesColor", data);
            setResult(resultCode, intent);
        } else {
            setResult(resultCode);
        }

        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    class CustomPackageHandler extends Handler {
        private final WeakReference<CustomPackageActivity> mActivity;
        private ColorChooseDialog dialog;
        private int btnId = 0;

        public CustomPackageHandler(CustomPackageActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final CustomPackageActivity activity = mActivity.get();
            switch (msg.what) {
                case SHOW_COLORS_DIALOG:
                    btnId = msg.arg1;
                    Log.e("CustomPackageActivity", "colors:" + colors.size());
                    dialog = new ColorChooseDialog.Builder(activity.mContext)
                            .setView(colors, new ColorChooseDialog.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    dialog.dismiss();
                                    Message message = new Message();
                                    message.what = CHANGE_PREVIEW_COLOR;
                                    message.arg1 = btnId;
                                    message.arg2 = position;
                                    mHandler.sendMessage(message);
                                }
                            })
                            .createDialog();

                    dialog.show();
                    break;
                case SHOW_EDIT_DIALOG:
                    activity.showEditDialog();
                    break;
                case CHANGE_PREVIEW_COLOR:
                    activity.changePreviewColor(msg.arg1, msg.arg2);
                    break;
                case SHOW_TOAST:
                    T.showShort(CustomPackageActivity.this, "已经存在相同的名字");
            }
        }
    }
}
