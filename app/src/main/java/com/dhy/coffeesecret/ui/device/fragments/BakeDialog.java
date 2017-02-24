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
import android.widget.ListView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.DialogBeanInfo;
import com.dhy.coffeesecret.ui.device.DialogBeanSelectedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CoDeleven on 17-2-18.
 */

public class BakeDialog extends DialogFragment {
    private static List<DialogBeanInfo> dialogBeanInfos = new ArrayList<>();
    private static int curItem;
    private Context mContext;
    private Button mCancel;
    private Button mConfirm;
    private CheckBox mCheckBox;
    private View mLinesSelector;
    private ListView mListView;
    private Button mAdd;
    private OnBeaninfosConfirmListener beaninfosConfirmListener;

    public BakeDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.bake_dialog, container, false);

        initComp(view);
        // 初始化确认和取消
        initConfirmCancel();
        initLinesSelect();
        initAddButton();


        return view;
    }

    private void initComp(View view) {

        mCancel = (Button) view.findViewById(R.id.id_bake_dialog_cancel);
        mConfirm = (Button) view.findViewById(R.id.id_bake_dialog_confirm);
        mCheckBox = (CheckBox) view.findViewById(R.id.id_bake_dialog_checkbox);
        mLinesSelector = view.findViewById(R.id.id_bake_dialog_refer_selector);
        mAdd = (Button) view.findViewById(R.id.id_bake_dialog_add);
        mListView = (ListView) view.findViewById(R.id.id_bake_dialog_scroll);
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

    private void initAddButton() {
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
                        if (!hasFocus && editText.getText().length() > 0) {
                            dialogBeanInfos.get(position).setWeight(Float.parseFloat(editText.getText().toString()));
                        }
                    }
                });

                holder.beanName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curItem = position;
                        Log.e("codelevex", "curItem:" + position);
                        Intent intent = new Intent(mContext, DialogBeanSelectedActivity.class);
                        startActivityForResult(intent, 7);
                    }
                });
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
            dialogBeanInfos.get(curItem).setBeanInfo(beanInfo);
            ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
        }
    }

    public interface OnBeaninfosConfirmListener {
        void setBeanInfos(List<DialogBeanInfo> beanInfos);
    }

    class ViewHolder {
        public Button beanName;
        public EditText beanWeight;
    }
}
