package com.dhy.coffeesecret.ui.device.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.SettingTool;
import com.facebook.rebound.ui.Util;
import com.github.mikephil.charting.data.Event;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by CoDeleven on 17-2-23.
 */

public class Other extends DialogFragment {
    private OnOtherAddListener onOtherAddListener;
    @Bind(R.id.id_baking_quick1)
    TextView quick1;
    @Bind(R.id.id_baking_quick2)
    TextView quick2;
    @Bind(R.id.id_baking_quick3)
    TextView quick3;
    @Bind(R.id.id_baking_quick4)
    TextView quick4;
    @Bind(R.id.id_baking_quick5)
    TextView quick5;
    @Bind(R.id.id_baking_cancel)
    Button mCancel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.bake_other_dialog, container, false);
        ButterKnife.bind(this, view);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        List<String> temp = SettingTool.parse2List(SettingTool.getConfig().getQuickEvents());
        quick1.setText(temp.get(0));
        quick2.setText(temp.get(1));
        quick3.setText(temp.get(2));
        quick4.setText(temp.get(3));
        quick5.setText(temp.get(4));



        return view;
    }

    public void setOnOtherAddListener(OnOtherAddListener onOtherAddListener){
        this.onOtherAddListener = onOtherAddListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(Util.dpToPx(200, getResources()), Util.dpToPx(195, getResources()));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    public interface OnOtherAddListener {
        void onDataChanged(Event event);
    }

    @OnClick({R.id.id_baking_quick1, R.id.id_baking_quick2, R.id.id_baking_quick3, R.id.id_baking_quick4, R.id.id_baking_quick5})
    void quickSelected(View view){
        String info = ((TextView)view).getText().toString();
        Event event = new Event(Event.OTHER);
        event.setDescription(info);
        onOtherAddListener.onDataChanged(event);
        dismiss();
    }
    @OnClick(R.id.id_baking_cancel)
    void exitOtherDialog(){
        dismiss();
    }
}
