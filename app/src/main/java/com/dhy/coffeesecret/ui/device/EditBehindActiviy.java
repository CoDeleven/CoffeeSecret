package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportBeanFactory;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.dhy.coffeesecret.views.WheelView;
import com.github.mikephil.charting.data.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        entries = ((MyApplication)getApplication()).getBakeReport().getEntriesWithEvents();
        for (Entry entry : entries) {
            int time = (int) entry.getX();
            int minutes = time / 60;
            int seconds = time % 60;
            content.add(String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds));
        }
    }

    @OnClick(R.id.id_bake_behind_save)
    protected void onSave() {
        BakeReportProxy proxy = BakeReportBeanFactory.getInstance();
        proxy.setCookedBeanWeight(Float.parseFloat(cookedWeight.getText().toString()));
        proxy.setBakeDegree(mCurValue);
        Intent other = new Intent(this, ReportActivity.class);
        sendJsonData(proxy.getBakeReport());
        startActivity(other);
        finish();
    }

    private void sendJsonData(final BakeReport proxy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtils.execute(URLs.ADD_BAKE_REPORT, proxy);
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
                score.setText(((int) msg.getData().getFloat("curValue")) + "");
                return false;
            }
        }).sendMessage(msg);
        mCurValue = curValue;
    }
}
