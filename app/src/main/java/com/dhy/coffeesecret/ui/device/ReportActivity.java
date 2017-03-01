package com.dhy.coffeesecret.ui.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.dhy.coffeesecret.pojo.BakeReportImmBeanFactory;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.ui.device.formatter.XAxisFormatter4Time;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.dhy.coffeesecret.views.ReportMarker;
import com.dhy.coffeesecret.views.ScrollViewContainer;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.LinearLayout.LayoutParams;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;


public class ReportActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.id_report_chart)
    BaseChart4Coffee mChart;
    @Bind(R.id.id_report_lineOperator)
    TextView mLineOperator;
    @Bind(R.id.id_report_scrollContainer)
    ScrollViewContainer scrollViewContainer;
    @Bind(R.id.id_envTemp)
    TextView envTemp;
    @Bind(R.id.id_inputBeanTemp)
    TextView startTemp;
    @Bind(R.id.id_endTemp)
    TextView endTemp;
    @Bind(R.id.id_developTime)
    TextView developTime;
    @Bind(R.id.id_developRate)
    TextView developRate;
    @Bind(R.id.id_baking_bakeDate)
    TextView date;
    @Bind(R.id.id_baking_deviceName)
    TextView device;
    @Bind(R.id.id_score)
    TextView score;
    @Bind(R.id.id_report_home)
    TextView home;
    private TableLayout tableLayout;
    private List<BeanInfoSimple> beanInfos = new ArrayList<>();
    private LinearLayout beanContainer;
    private List<LinearLayout> beanContent;
    private PopupWindow popupWindow;
    private BakeReportImm imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_device_activtiy);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        initParam();
        init();

    }

    private void initParam() {
        imm = BakeReportImmBeanFactory.getBakeReportImm();

        mChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollViewContainer.requestDisallowInterceptTouchEvent(false);
                    ((ScrollView) scrollViewContainer.getChildAt(0)).requestDisallowInterceptTouchEvent(false);
                    scrollViewContainer.setCanPullup(true);
                } else {
                    scrollViewContainer.requestDisallowInterceptTouchEvent(true);
                    ((ScrollView) scrollViewContainer.getChildAt(0)).requestDisallowInterceptTouchEvent(true);
                    scrollViewContainer.setCanPullup(false);
                }
                return false;
            }
        });
        mChart.setDrawMarkers(true);
        mChart.setMarker(new ReportMarker(this, R.layout.report_marker));
        mChart.initLine();

        for(ILineDataSet lineDataSet: imm.getLineData().getDataSets()){
            LineDataSet lineData = (LineDataSet)lineDataSet;
            mChart.addNewDatas(lineData.getValues(), getIndexByLabels(lineData.getLabel()));
        }

        envTemp.setText("环境温度:" + imm.getEnvTemp());
        startTemp.setText("入豆温度:" + imm.getStartTemp());
        endTemp.setText("结束温度:" + imm.getEndTemp());
        developTime.setText("发展时间:" + XAxisFormatter4Time.formatString2Time(imm.getDevelopTime()));
        developRate.setText("发展率:" + imm.getDevelopRate() * 100 + "%");
        beanInfos = imm.getBeanInfos();
        date.setText("烘焙日期：" + imm.getBakeDate());
        device.setText("设备：" + imm.getDevice());


        score.setText(imm.getBakeDegree() + "");

        tableLayout = (TableLayout) findViewById(R.id.id_report_table);
        beanContainer = (LinearLayout) findViewById(R.id.id_bean_container);
        beanContent = getNewInstance();
        for (LinearLayout linearLayout : beanContent) {
            beanContainer.addView(linearLayout);
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLineOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("codelevex", "被点击了");
                getPopupwindow().showAsDropDown(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private PopupWindow getPopupwindow() {
        if (popupWindow == null) {
            final View view = getLayoutInflater().inflate(R.layout.bake_lines_operator, null, false);
            popupWindow = new PopupWindow(view, UnitConvert.dp2px(getResources(), 86), UnitConvert.dp2px(getResources(), 145), true);
            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (view != null && view.isShown()) {
                        popupWindow.dismiss();
                    }
                    return false;
                }
            });
            view.setBackgroundResource(R.drawable.bg_round_black_border);

            ((CheckBox) view.findViewById(R.id.id_baking_line_bean)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_inwind)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_outwind)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_accBean)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_accInwind)).setOnCheckedChangeListener(this);
            ((CheckBox) view.findViewById(R.id.id_baking_line_accOutwind)).setOnCheckedChangeListener(this);

        }
        return popupWindow;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        int id = buttonView.getId();
        int curIndex = 0;
        switch (id) {
            case R.id.id_baking_line_bean:
                curIndex = BaseChart4Coffee.BEANLINE;
                break;
            case R.id.id_baking_line_inwind:
                curIndex = BaseChart4Coffee.INWINDLINE;
                break;
            case R.id.id_baking_line_outwind:
                curIndex = BaseChart4Coffee.OUTWINDLINE;
                break;
            case R.id.id_baking_line_accBean:
                curIndex = BaseChart4Coffee.ACCBEANLINE;
                break;
            case R.id.id_baking_line_accInwind:
                curIndex = BaseChart4Coffee.ACCINWINDLINE;
                break;
            case R.id.id_baking_line_accOutwind:
                curIndex = BaseChart4Coffee.ACCOUTWINDLINE;
                break;
        }
        final int temp = curIndex;
        if (isChecked) {
            mChart.showLine(temp);
        } else {
            mChart.hideLine(temp);
        }
    }

    private void init() {
        for (int i = 0; i < 40; ++i) {
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(10, 10, 10, 10);
            TableLayout.LayoutParams p = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(p);
            for (int j = 0; j < 5; ++j) {
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setText("ggggg");
                tableRow.addView(textView, lp);
            }
            tableRow.setGravity(Gravity.CENTER);
            tableLayout.addView(tableRow, p);
        }
    }


    private List<LinearLayout> getNewInstance() {
        List<LinearLayout> linearLayouts = new ArrayList<>();
        for (BeanInfoSimple beanInfo : beanInfos) {
            LinearLayout outter = new LinearLayout(this);
            LinearLayout[] content = new LinearLayout[2];

            for (int i = 0; i < 2; ++i) {
                content[i] = new LinearLayout(this);
                outter.addView(content[i]);
            }

            // 设置五行的格式
            LayoutParams contentLayout = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            contentLayout.weight = 1;
            content[0].setLayoutParams(contentLayout);
            content[0].setOrientation(OrientationHelper.VERTICAL);
            content[1].setLayoutParams(contentLayout);
            content[1].setOrientation(OrientationHelper.VERTICAL);

            LayoutParams temp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

            // 设置豆名
            TextView beanName = new TextView(this);
            beanName.setText("名称：" + beanInfo.getBeanName());
            beanName.setLayoutParams(temp);

            // 设置海拔
            TextView beanAltitude = new TextView(this);
            beanAltitude.setText("海拔：" + beanInfo.getAltitude());
            beanAltitude.setLayoutParams(temp);

            // 设置豆种
            TextView beanSpecies = new TextView(this);
            beanSpecies.setText("豆种：" + beanInfo.getSpecies());
            beanSpecies.setLayoutParams(temp);

            // 设置处理方式
            TextView beanHandler = new TextView(this);
            beanHandler.setText("处理方式：" + beanInfo.getProcess());
            beanHandler.setLayoutParams(temp);

            // 设置国家
            TextView beanCountry = new TextView(this);
            beanCountry.setText("国家：" + beanInfo.getCountry());
            beanCountry.setLayoutParams(temp);

            // 设置含水量
            TextView beanWaterContent = new TextView(this);
            beanWaterContent.setText("含水量：" + beanInfo.getWaterContent());
            beanWaterContent.setLayoutParams(temp);

            // 设置等级
            TextView beanLevel = new TextView(this);
            beanLevel.setText("等级：" + beanInfo.getLevel());
            beanLevel.setLayoutParams(temp);

            // 设置庄园
            TextView beanManor = new TextView(this);
            beanManor.setText("庄园：" + beanInfo.getManor());
            beanManor.setLayoutParams(temp);

            // 设置地区
            TextView beanArea = new TextView(this);
            beanArea.setText("地区：" + beanInfo.getArea());
            beanArea.setLayoutParams(temp);

            content[0].addView(beanName);
            content[0].addView(beanSpecies);
            content[0].addView(beanCountry);
            content[0].addView(beanLevel);
            content[0].addView(beanArea);

            content[1].addView(beanAltitude);
            content[1].addView(beanHandler);
            content[1].addView(beanWaterContent);
            content[1].addView(beanManor);


            // 设置外linearlayout
            LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
            // 设置margins
            lp.setMargins(0, 0, 0, UnitConvert.dp2px(getResources(), 16.3125f));
            // 设置方向
            outter.setOrientation(OrientationHelper.HORIZONTAL);
            // 给outter设置
            outter.setLayoutParams(lp);

            linearLayouts.add(outter);
        }
        return linearLayouts;
    }

    private int getIndexByLabels(String labels){
        switch (labels){
            case "豆温":return 0;
            case "豆升温": return 1;
            case "进风温": return 2;
            case "进风升温": return 3;
            case "出风温": return 4;
            case "出风升温": return 5;
            default:
                return -1;
        }
    }
}