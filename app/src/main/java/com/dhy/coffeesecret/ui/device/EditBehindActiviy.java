package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.dhy.coffeesecret.pojo.BakeReportImmBeanFactory;
import com.dhy.coffeesecret.utils.ObjectJsonConvert;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.dhy.coffeesecret.views.WheelView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import junit.framework.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditBehindActiviy extends AppCompatActivity implements CircleSeekBar.OnSeekBarChangeListener {

    @Bind(R.id.id_bake_degree)
    CircleSeekBar mSeekBar;
    @Bind(R.id.id_bake_behind_wheelView)
    WheelView wheelView;
    @Bind(R.id.id_bake_behind_event)
    EditText editText;
    @Bind(R.id.id_bake_behind_save)
    Button save;
    @Bind(R.id.id_bake_behind_cookedWeight)
    EditText cookedWeight;
    @Bind(R.id.id_score)
    TextView score;
    private float mCurValue;
    private List<String> content = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_behind_activiy);
        ButterKnife.bind(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        wheelView.setOffset(1);
        init();
        editText.setText(entries.get(0).getEvent().getDescription());
        wheelView.setItems(content);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int temp = wheelView.getSeletedIndex();
                entries.get(temp).getEvent().setDescription(s.toString());
            }
        });
        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(final int selectedIndex, String item) {
                String eventDescriptor = entries.get(selectedIndex - 1).getEvent().getDescription();
                editText.setText(eventDescriptor);
            }
        });

    }

    private void init() {
        entries = BakeReportImmBeanFactory.getBakeReportImm().getEntriesWithEvents();
        for(Entry entry : entries){
            int time = (int) entry.getX();
            int minutes = time / 60;
            int seconds = time % 60;
            content.add(String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds));
        }
    }
    @OnClick(R.id.id_bake_behind_save)
    protected void onSave(){
        BakeReportImm imm = BakeReportImmBeanFactory.getBakeReportImm();
        imm.setCookedBeanWeight(Float.parseFloat(cookedWeight.getText().toString()));
        imm.setBakeDegree(mSeekBar.getCurProcess());
        imm.setBakeDegree(mCurValue);
        Intent other = new Intent(this, ReportActivity.class);
        sendJsonData(imm);
        startActivity(other);
        finish();
    }

    private void sendJsonData(final BakeReportImm imm){
        final OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("codelevex", "currentThread:" + Thread.currentThread().toString());
                String url = "http://10.152.18.56:8080/CoffeeSecret/bake/add";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ObjectJsonConvert.bakereport2Json(imm));
                Log.e("codelevex", "wwwwww");
                Request request = new Request.Builder().url(url).post(requestBody).build();
                try{
                    Response response = okHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        Log.e("codelevex", "成功获取");
                    }else{
                        Log.e("codelevex", "失败啦！！！:" + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onChanged(CircleSeekBar seekbar, int curValue) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putFloat("curValue", curValue);
        msg.setData(bundle);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                score.setText(((int)msg.getData().getFloat("curValue")) + "");
                return false;
            }
        }).sendMessage(msg);
        mCurValue = curValue;
    }
}
