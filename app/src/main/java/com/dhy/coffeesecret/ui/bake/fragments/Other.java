package com.dhy.coffeesecret.ui.bake.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.SettingTool;
import com.facebook.rebound.ui.Util;
import com.github.mikephil.charting.data.Event;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by CoDeleven on 17-2-23.
 */

public class Other extends DialogFragment {
    private OnOtherAddListener onOtherAddListener;
    @Bind(R.id.id_quick_event_container)
    LinearLayout eventContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.bake_other_dialog, container, false);
        ButterKnife.bind(this, view);

        List<String> temp = SettingTool.parse2List(SettingTool.getConfig().getQuickEvents());
        generateQuickEvent(temp);

        return view;
    }

    private void generateQuickEvent(List<String> events){
        for (String event : events) {
            TextView textView = new TextView(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT);
            params.setMargins(0, Util.dpToPx(2.25f, getResources()), 0, 0);
            textView.setLayoutParams(params);

            textView.setPadding(Util.dpToPx(20.1285f, getResources()), Util.dpToPx(6.75f, getResources()), 0, Util.dpToPx(6.75f, getResources()));
            textView.setTextColor(Color.parseColor("#9ea3a8"));
            textView.setText(event);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String info = ((TextView)v).getText().toString();
                    Event event = new Event(Event.OTHER);
                    event.setDescription(info);
                    onOtherAddListener.onDataChanged(event);
                    dismiss();
                }
            });
            eventContainer.addView(textView);

            View view = new View(getContext());
            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dpToPx(1f, getResources()));
            viewParams.setMargins(Util.dpToPx(5.625f, getResources()), 0, Util.dpToPx(5.625f, getResources()), 0);
            view.setBackgroundColor(Color.parseColor("#F1F2F0"));
            view.setLayoutParams(viewParams);

            eventContainer.addView(view);
        }
        Button btn = new Button(getContext());
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setText("取消");
        btn.setMinHeight(Util.dpToPx(15f, getResources()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        // params.setMargins(0, 0, 0, Util.dpToPx(3f, getResources()));
        btn.setPadding(0, Util.dpToPx(1f, getResources()), 0, Util.dpToPx(1f, getResources()));
        btn.setTextColor(Color.parseColor("#936743"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn.setLayoutParams(params);

        eventContainer.addView(btn);
    }

    public void setOnOtherAddListener(OnOtherAddListener onOtherAddListener){
        this.onOtherAddListener = onOtherAddListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(Util.dpToPx(200, getResources()), WRAP_CONTENT);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    public interface OnOtherAddListener {
        void onDataChanged(Event event);
    }
}
