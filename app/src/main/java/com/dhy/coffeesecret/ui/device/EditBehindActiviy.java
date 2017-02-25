package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.dhy.coffeesecret.utils.ObjectJsonConvert;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.dhy.coffeesecret.views.WheelView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

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

public class EditBehindActiviy extends AppCompatActivity {

    public static final String BEAN_EVENTS = "com.dhy.coffeesecret.ui.device.EditBehindActiviy.BEAN_EVENTS";
    public static final String BAKE_REPORT = "com.dhy.coffeesecret.ui.device.EditBehindActiviy.BAKE_REPORT";
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
    private List<String> content = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_behind_activiy);
        ButterKnife.bind(this);
        wheelView.setOffset(1);
        init();
        editText.setText(entries.get(0).getEvent().getDescription());
        wheelView.setItems(content);
        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                String eventDescriptor = entries.get(selectedIndex - 1).getEvent().getDescription();
                editText.setText(eventDescriptor);
            }
        });
    }

    private void init() {
        Intent intent = getIntent();
        Object[] objs = (Object[])intent.getSerializableExtra(BEAN_EVENTS);
        for(Object obj : objs){
            Entry entry = (Entry)obj;
            int time = (int) entry.getX();
            int minutes = time / 60;
            int seconds = time % 60;
            content.add(String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds));
            entries.add(entry);
        }
    }
    @OnClick(R.id.id_bake_behind_save)
    protected void onSave(){
        Intent intent = getIntent();
        BakeReportImm imm = (BakeReportImm)intent.getSerializableExtra(BAKE_REPORT);
        imm.setCookedBeanWeight(Float.parseFloat(cookedWeight.getText().toString()));
        sendJsonData(imm);
        imm.setBakeDegree(mSeekBar.getCurProcess());
        Intent other = new Intent(this, ReportActivity.class);
        other.putExtra(ReportActivity.REPORT_FINAL, imm);
        startActivity(other);
        finish();
    }

    private void sendJsonData(final BakeReportImm imm){
        final OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
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
}
