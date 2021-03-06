package com.dhy.coffeesecret.ui.device.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.DialogBeanInfo;
import com.dhy.coffeesecret.ui.device.DialogBeanSelected;
import com.dhy.coffeesecret.ui.device.LineSelectedActivity;
import com.dhy.coffeesecret.utils.SettingTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by CoDeleven on 17-2-18.
 */

public class BakeDialog extends DialogFragment {
    public static final int GET_HISTORY = 1;
    public static final int GET_COLLECTION = 2;
    private static List<DialogBeanInfo> dialogBeanInfos;
    private static int curItem;
    @Bind(R.id.id_bake_dialog_refer_collection)
    ImageView referCollection;
    @Bind(R.id.id_bake_dialog_refer_history)
    ImageView referHistory;
    Context mContext;
    @Bind(R.id.id_bake_dialog_cancel)
    Button mCancel;
    @Bind(R.id.id_bake_dialog_confirm)
    Button mConfirm;
    @Bind(R.id.id_bake_dialog_checkbox)
    CheckBox mCheckBox;
    @Bind(R.id.id_bake_dialog_refer_selector)
    View mLinesSelector;
    @Bind(R.id.id_bake_dialog_scroll)
    ListView mListView;
    @Bind(R.id.id_bake_dialog_add)
    Button mAdd;
    private OnBeaninfosConfirmListener beaninfosConfirmListener;
    private ArrayList<Float> referTempratures;
    private String unit;

    public BakeDialog() {
        dialogBeanInfos = new ArrayList<>();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.bake_dialog, container, false);
        ButterKnife.bind(this, view);

        initDefaultItem();
        // 初始化确认和取消
        initConfirmCancel();
        initLinesSelect();
        initAddDelButton();


        return view;
    }

    private void initDefaultItem() {
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setName("样品豆");
        DialogBeanInfo dialogBeanInfo = new DialogBeanInfo();
        dialogBeanInfo.setBeanInfo(beanInfo);
        unit = SettingTool.getConfig(getContext()).getWeightUnit();
        //默认5,根据单位乘1000或者500
        float defaultWeight = -1;
        if (unit.equals("g")) {
            unit = "g";
            defaultWeight = 500;
        } else if (unit.equals("kg")) {
            unit = "kg";
            defaultWeight = 0.5f;
        } else if (unit.equals("bl")) {
            unit = "lb";
            defaultWeight = 5 * 1.1f;
        }
        dialogBeanInfo.setWeight(defaultWeight);
        dialogBeanInfos.add(dialogBeanInfo);
    }

    private void initConfirmCancel() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理确定情况
                beaninfosConfirmListener.setBeanInfos(dialogBeanInfos);
                beaninfosConfirmListener.setTempratures(referTempratures);
                dismiss();
            }
        });
    }

    private void initLinesSelect() {

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLinesSelector.setVisibility(View.VISIBLE);
                } else {
                    mLinesSelector.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initAddDelButton() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBeanInfo dialogBeanInfo = new DialogBeanInfo();
                BeanInfo beanInfo = new BeanInfo();
                beanInfo.setName("样品豆");
                dialogBeanInfo.setBeanInfo(beanInfo);

                dialogBeanInfos.add(dialogBeanInfo);
                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                mListView.setSelection(dialogBeanInfos.size() - 1);
            }
        });

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return dialogBeanInfos.size();
            }

            @Override
            public Object getItem(int position) {
                return dialogBeanInfos.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.bake_dialog_item, parent, false);
                    holder.beanName = (Button) convertView.findViewById(R.id.id_bake_dialog_beanName);
                    holder.beanWeight = (EditText) convertView.findViewById(R.id.id_bake_dialog_beanWeight);
                    holder.beanDel = (ImageView) convertView.findViewById(R.id.id_bake_dialog_delete);
                    holder.weightUnit = (TextView) convertView.findViewById(R.id.id_bake_dialog_unit);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.beanName.setText(dialogBeanInfos.get(position).getBeanInfo().getName());
                holder.beanWeight.setText(dialogBeanInfos.get(position).getWeight() + "");

                holder.beanWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        EditText editText = (EditText) v;
                        String result = editText.getText().toString();
                        if (dialogBeanInfos.size() > 0 && !hasFocus && editText.getText().length() > 0) {
                            for(int i = 0; i < result.length(); ++i){
                                if(!Character.isDigit(result.charAt(i))){
                                    return;
                                }
                            }
                            dialogBeanInfos.get(position).setWeight(Float.parseFloat(result));
                        }
                    }
                });

                holder.beanName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curItem = position;
                        Intent intent = new Intent(mContext, DialogBeanSelected.class);
                        startActivityForResult(intent, 7);
                    }
                });

                holder.beanDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curItem = position;
                        Log.e("codelevex", "position:" + position);
                        dialogBeanInfos.remove(curItem);
                        notifyDataSetChanged();
                    }
                });
                holder.weightUnit.setText(unit);
                return convertView;
            }
        });


    }

    public void setBeanInfosListener(OnBeaninfosConfirmListener beanInfosListener) {
        this.beaninfosConfirmListener = beanInfosListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            BeanInfo beanInfo = (BeanInfo) data.getSerializableExtra("beanInfo");
            BakeReport bakeReport = (BakeReport) data.getSerializableExtra("report");
            if (beanInfo != null) {
                dialogBeanInfos.get(curItem).setBeanInfo(beanInfo);
            }
            if(bakeReport != null){
                referTempratures = (ArrayList)bakeReport.getTempratureSet().getBeanTemps();
            }
            ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
        }
    }

    @OnClick({R.id.id_bake_dialog_refer_history, R.id.id_bake_dialog_refer_collection})
    void click(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.id_bake_dialog_refer_history:
                //TODO 完成历史参考曲线
                intent = new Intent(getContext(), LineSelectedActivity.class);
                startActivityForResult(intent, GET_HISTORY);

                break;
            case R.id.id_bake_dialog_refer_collection:
                //TODO 完成收藏参考曲线
                intent = new Intent(getContext(), LineSelectedActivity.class);
                startActivityForResult(intent, GET_COLLECTION);
        }
    }

    public interface OnBeaninfosConfirmListener {
        void setBeanInfos(List<DialogBeanInfo> beanInfos);

        void setTempratures(ArrayList<Float> tempratures);
    }

    class ViewHolder {
        public Button beanName;
        public EditText beanWeight;
        public ImageView beanDel;
        public TextView weightUnit;
    }
}
