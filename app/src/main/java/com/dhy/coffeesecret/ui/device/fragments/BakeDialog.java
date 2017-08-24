package com.dhy.coffeesecret.ui.device.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.ui.device.DialogBeanSelected;
import com.dhy.coffeesecret.ui.device.LineSelectedActivity;
import com.dhy.coffeesecret.ui.device.adapter.BeanSelectAdapter;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.views.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by CoDeleven on 17-2-18.
 */

public class BakeDialog extends DialogFragment implements BeanSelectAdapter.MyButtonOnClick {
    public static final int GET_HISTORY = 1;
    public static final int GET_COLLECTION = 2;
    public static final int SELECT_BEAN = 111;

    // private static List<DialogBeanInfo> dialogBeanInfos;
    private List<BeanInfoSimple> beanInfos;
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
    RecyclerView mListView;
    @Bind(R.id.id_bake_dialog_add)
    Button mAdd;
    private OnBeaninfosConfirmListener beaninfosConfirmListener;
    private BakeReport referTempratures;
    private String unit;
    
    public BakeDialog(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.bake_dialog, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if(bundle != null){
            beanInfos = (ArrayList<BeanInfoSimple>)bundle.getSerializable("beanInfos");
            unit = bundle.getString("appWeightUnit");
        }
        if(beanInfos == null){
            beanInfos = new ArrayList<>();
        }
        initDefaultItem();
        // 初始化确认和取消
        initConfirmCancel();
        initLinesSelect();
        initAddDelButton();


        return view;
    }

    private void initDefaultItem() {
        if(beanInfos.size() == 0){
            BeanInfoSimple testBean = new BeanInfoSimple();
            testBean.setBeanName("样品豆");
            testBean.setSingleBeanId(-1);
            testBean.setUsage("" + getDefaultWeight());
            beanInfos.add(testBean);
        }
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
                beaninfosConfirmListener.setBeanInfos(beanInfos);
                for(BeanInfoSimple info:beanInfos){
                    try{
                        Float.parseFloat(info.getUsage());
                    }catch (Exception e){
                        T.showShort(getContext(), info.getBeanName() + "使用量异常，请检查...");
                        return;
                    }
                }
                beaninfosConfirmListener.setTemperatures(referTempratures);
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
                BeanInfoSimple simpleBean = new BeanInfoSimple();
                simpleBean.setBeanName("样品豆");
                simpleBean.setUsage(getDefaultWeight() + "");
                simpleBean.setSingleBeanId(-1);

                beanInfos.add(simpleBean);
                mListView.getAdapter().notifyDataSetChanged();
                mListView.scrollToPosition(mListView.getAdapter().getItemCount() - 1);
            }
        });
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        BeanSelectAdapter adapter = new BeanSelectAdapter(getContext(), beanInfos);
        adapter.setMyButtonOnClick(this);
        mListView.setAdapter(adapter);
        mListView.addItemDecoration(new DividerDecoration(getContext()));
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
                BeanInfoSimple simple = beanInfos.get(curItem);
                simple.setSingleBeanId(beanInfo.getId());
                simple.setBeanName(beanInfo.getName());
                simple.setAltitude(beanInfo.getAltitude());
                simple.setArea(beanInfo.getArea());
                simple.setCountry(beanInfo.getCountry());
                simple.setLevel(beanInfo.getLevel());
                simple.setManor(beanInfo.getManor());
                simple.setProcess(beanInfo.getProcess());
                simple.setWaterContent(beanInfo.getWaterContent() + "");
                simple.setSpecies(beanInfo.getSpecies());
            }
            if (bakeReport != null) {
                referTempratures = bakeReport;
            }
             mListView.getAdapter().notifyDataSetChanged();
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

    /**
     * 获取默认的单位 默认500g,0.5kg,1.1lb
     *
     * @return
     */
    private float getDefaultWeight() {
        // unit = SettingTool.getConfig().getWeightUnit();
        //默认5,根据单位乘1000或者500
        float defaultWeight = -1;
        if (unit.equals("g")) {
            unit = "g";
            defaultWeight = 500;
        } else if (unit.equals("kg")) {
            unit = "kg";
            defaultWeight = 0.5f;
        } else if (unit.equals("lb")) {
            unit = "lb";
            defaultWeight = 1.1f;
        }
        return defaultWeight;
    }

    @Override
    public void onNameClick(int position) {
        curItem = position;
        Intent intent = new Intent(mContext, DialogBeanSelected.class);
        startActivityForResult(intent, SELECT_BEAN);
    }

    @Override
    public void onDeleteClick(int position) {
        curItem = position;
        beanInfos.remove(curItem);
        mListView.getAdapter().notifyDataSetChanged();
    }

    public interface OnBeaninfosConfirmListener {
        void setBeanInfos(List<BeanInfoSimple> beanInfos);

        void setTemperatures(BakeReport temperatures);
    }

    class ViewHolder {
        public Button beanName;
        public EditText beanWeight;
        public ImageView beanDel;
        public TextView weightUnit;
    }
}
