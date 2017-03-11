package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.ui.device.adapter.EditEventListAdapter;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.utils.Utils;
import com.dhy.coffeesecret.views.CircleSeekBar;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditBehindActiviy extends AppCompatActivity implements CircleSeekBar.OnSeekBarChangeListener {

    @Bind(R.id.id_bake_degree)
    CircleSeekBar mSeekBar;
    @Bind(R.id.id_edit_list)
    RecyclerView recyclerView;
    @Bind(R.id.id_bake_behind_save)
    Button save;
    @Bind(R.id.id_bake_behind_cookedWeight)
    EditText cookedWeight;
    @Bind(R.id.id_score)
    TextView score;
    private float mCurValue;
    private List<String> content = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();
    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit_behind_activiy);
        ButterKnife.bind(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        init();
        unit = Utils.convertUnitChineses2Eng(SettingTool.getConfig(this).getWeightUnit());
        cookedWeight.setHint("请填写熟豆重量，此处单位为" + unit);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new EditEventListAdapter(this, entries));
        recyclerView.addItemDecoration(new DividerDecoration(this));
    }

    private void init() {
        // TODO 校赛专用
        // entries = ((MyApplication) getApplication()).getBakeReport().getEntriesWithEvents();
        entries = TestData.getBakeReport().getEntriesWithEvents();

        for (Entry entry : entries) {
            int time = (int) entry.getX();
            int minutes = time / 60;
            int seconds = time % 60;
            content.add(String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds));
        }
    }

    @OnClick(R.id.id_bake_behind_save)
    protected void onSave() {
        // TODO 校赛专用
        // BakeReportProxy proxy = ((MyApplication) getApplication()).getBakeReport();
        BakeReportProxy proxy = TestData.getBakeReport();

        String weight = cookedWeight.getText().toString();
        if(!"".equals(weight)){
            proxy.setCookedBeanWeight(Float.parseFloat(weight));
        }else{
            proxy.setCookedBeanWeight(0);
        }


        proxy.setBakeDegree(mCurValue);
        Intent other = new Intent(this, ReportActivity.class);
        sendJsonData(proxy.getBakeReport());
        startActivity(other);
        finish();
    }

    private void sendJsonData(final BakeReport proxy) {
        //TODO 校赛视频注释
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtils.execute(URLs.ADD_BAKE_REPORT, proxy);
                    // 先如此使用着,id这个问题需要得到解决
                    ((MyApplication) getApplication()).setUrl(URLs.GET_ALL_BAKE_REPORT);
                    ((MyApplication) getApplication()).initMapFromServer(BakeReport.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        TestData.saveBakeReports(this, proxy);
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
