package com.dhy.coffeesecret.ui.bake;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.model.chart.Model4Chart;
import com.dhy.coffeesecret.model.report.IReportView;
import com.dhy.coffeesecret.model.report.Presenter4Report;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.ui.bake.fragments.SharedFragment;
import com.dhy.coffeesecret.ui.common.views.BaseChart4Coffee;
import com.dhy.coffeesecret.ui.common.views.ReportMarker;
import com.dhy.coffeesecret.ui.common.views.ScrollViewContainer;
import com.dhy.coffeesecret.utils.ConvertUtils;
import com.dhy.coffeesecret.utils.FormatUtils;
import com.dhy.coffeesecret.utils.FragmentTool;
import com.dhy.coffeesecret.utils.Utils;
import com.facebook.rebound.ui.Util;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Event;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jesse.nativelogger.NLogger;

import static android.widget.LinearLayout.LayoutParams;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCBEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCINWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.BEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.INWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.OUTWINDLINE;


public class ReportActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, IReportView {
    private static final String TAG = ReportActivity.class.getSimpleName();
    private static ReportActivity REPORT_ACTIVITY;
    @Bind(R.id.id_event_list)
    TableLayout mTlEventList;
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
    @Bind(R.id.id_share)
    ImageView barcode;
    @Bind(R.id.id_edit)
    ImageView more;
    @Bind(R.id.id_report_species)
    TextView species;
    @Bind(R.id.id_globalAcc)
    TextView globalAccTemp;
    /*@Bind(R.id.id_avgDry)
    TextView avgDry;
    @Bind(R.id.id_avgFirbu)
    TextView avgFirbu;
    @Bind(R.id.id_avgEnd)
    TextView avgEnd;*/
    @Bind(R.id.id_total_bake_time)
    TextView totalBakeTime;
    @Bind(R.id.id_breakPointer_temp)
    TextView breakPointerTemp;
    @Bind(R.id.id_breakPointer_time)
    TextView breakPointerTime;

    // 校园专用单一豆名
    String _bean_;
    String _species_;
    String _bakeDegree_;
    String _developRate_;
    @Bind(R.id.id_avgDry_temperature)
    TextView idAvgDryTemperature;
    @Bind(R.id.id_avgDry_time)
    TextView idAvgDryTime;
    @Bind(R.id.id_avgFirbu_temperature)
    TextView idAvgFirbuTemperature;
    @Bind(R.id.id_avgFirbu_time)
    TextView idAvgFirbuTime;
    @Bind(R.id.id_avgEnd_temperature)
    TextView idAvgEndTemperature;
    @Bind(R.id.id_avgEnd_time)
    TextView idAvgEndTime;
    @Bind(R.id.toolbar_device_activtiy)
    Toolbar toolbar;
    @Bind(R.id.id_rl_score)
    RelativeLayout scoreLayout;

    private Presenter4Report mPresenter = Presenter4Report.newInstance();
    private String weightUnit;
    private String tempratureUnit;
    private TableLayout tableLayout;
    private List<BeanInfoSimple> beanInfos = new ArrayList<>();
    private LinearLayout beanContainer;
    private List<LinearLayout> beanContent;
    private PopupWindow popupWindow;
    // private BakeReportProxy proxy;

    public static ReportActivity getInstance() {
        return REPORT_ACTIVITY;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mPresenter.setView(this);
        initParam();
        // init();
        REPORT_ACTIVITY = this;
    }


    private void initParam() {
        weightUnit = mPresenter.getAppWeightUnit();
        tempratureUnit = mPresenter.getAppTemperatureUnit();
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

        tableLayout = (TableLayout) findViewById(R.id.id_report_table);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLineOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopupwindow().showAsDropDown(v);
            }
        });

        // 检测是否存在该参数
        BakeReport mLocalBakeReport = getIntent().getParcelableExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey());
        Log.d(TAG, mLocalBakeReport.toString());
        mPresenter.setLocalBakeReport(mLocalBakeReport);

        mPresenter.initViewWithProxy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO 校赛专用
        // ((MyApplication) getApplication()).setBakeReport((BakeReport) null);
        // TestData.setBakeReport((BakeReport) null);
        // 不好意思，用完即删
        if(mPresenter != null){
            mPresenter.clearBakeReport();
            mPresenter.setLocalBakeReport(null);
        }
        finish();
    }

    private PopupWindow getPopupwindow() {
        if (popupWindow == null) {
            final View view = getLayoutInflater().inflate(R.layout.bake_lines_operator, null, false);
            popupWindow = new PopupWindow(view, Util.dpToPx(86, getResources()), Util.dpToPx(150, getResources()), true);
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

    private int computeToastValue(double angle){
        int index = ((int)angle) / 45;
        switch (index){
            case 0:
                return Integer.MAX_VALUE;
            case 1:
                return 80;
            case 2:
                return (-(int)Math.floor(angle % 45 / 45f * 10)) + 70;
            case 3:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 55;
            case 4:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 50;
            case 5:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 45;
            case 6:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 40;
            case 7:
                return (-(int)Math.floor(angle % 45 / 45f * 5)) + 35;
            default:
                return Integer.MAX_VALUE;
        }
    }

    @OnClick(R.id.id_share)
    public void onBarcodeClick() {
        Bundle bundle = new Bundle();
        SharedFragment shared = new SharedFragment();
        bundle.putString(SharedFragment.BEAN_NAME, _bean_);
        bundle.putString(SharedFragment.SPECIES, _species_);
        bundle.putString(SharedFragment.BAKE_DEGREE, _bakeDegree_);
        bundle.putString(SharedFragment.DEVELOP_RATE, _developRate_);

        shared.setArguments(bundle);
        FragmentTool.getFragmentToolInstance(this).showDialogFragmen("dialogFragment", shared);
    }

    @OnClick(R.id.id_edit)
    public void onMoreClick() {
        Intent intent = new Intent(this, EditBehindActivity.class);
        intent.putExtra(EditBehindActivity.MODE_KEY, EditBehindActivity.MODE_EDITOR);
        if(mPresenter.getCurBakingReport() == null){
            intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), mPresenter.gettLocalBakeReport().getBakeReport());
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        int id = buttonView.getId();
        int curIndex = 0;
        switch (id) {
            case R.id.id_baking_line_bean:
                curIndex = Model4Chart.BEANLINE;
                break;
            case R.id.id_baking_line_inwind:
                curIndex = Model4Chart.INWINDLINE;
                break;
            case R.id.id_baking_line_outwind:
                curIndex = Model4Chart.OUTWINDLINE;
                break;
            case R.id.id_baking_line_accBean:
                curIndex = Model4Chart.ACCBEANLINE;
                break;
            case R.id.id_baking_line_accInwind:
                curIndex = Model4Chart.ACCINWINDLINE;
                break;
            case R.id.id_baking_line_accOutwind:
                curIndex = Model4Chart.ACCOUTWINDLINE;
                break;
        }
        final int temp = curIndex;
        if (isChecked) {
            mChart.showLine(temp);
        } else {
            mChart.hideLine(temp);
        }
    }

    /**
     * 矫正时间间隔，正常的时间间隔会有一两秒误差
     * @param timex 时间
     * @return
     */
    private int correct30InternalTime(List<Float> timex, int index4Loop, int nextTargetTime){
        NLogger.i(TAG, "期待时间点为：" + nextTargetTime);
        // 先获取当前循环下标的时间time1
        // 将time1 - index * 30s， 获取到距离上一个时间点的时间间隔time2
        // 将time2 进行 求余，判断余数是否为0
        int surplus = getDistanceFromTargetTime(timex, index4Loop, nextTargetTime);
        // 如果余数为0，那么直接返回循环下标，表示这个下标间隔正好为30s
        if(surplus == 0){
            return index4Loop;
        }
        return searchClosestIndex(timex, index4Loop, surplus, nextTargetTime);
    }

    private int searchClosestIndex(List<Float> timex, int index4Loop, int distance, int target){
        int targetIndex = Integer.MAX_VALUE;
        int minValue = Integer.MAX_VALUE;
        // 如果distance是正数，说明获取到的数值过大，需要递减下标；相反，则需要递增下标
        for (int i = 0; i <= Math.abs(distance); ++i){
            int foo = index4Loop - (i * distance / Math.abs(distance));
            if(foo > timex.size() || foo < 0){
                continue;
            }
            if(getDistanceFromTargetTime(timex, foo, target) < minValue){
                targetIndex = foo;
            }
        }
        NLogger.i(TAG, "最终list下标为：" + Math.round(targetIndex) + ", 时间点为：" + Math.round(timex.get(targetIndex)));
        return targetIndex;
    }

    private int getDistanceFromTargetTime(List<Float> timex, int index4Loop, int nextTargetTime){
        return (Math.round(timex.get(index4Loop)) - nextTargetTime) % 30;
    }

    private void generateProxyDetails(final BakeReportProxy proxy) {
        List<Entry> beanTemps = proxy.getLineDataSetByIndex(BEANLINE).getValues();
        List<Entry> inwindTemps = proxy.getLineDataSetByIndex(INWINDLINE).getValues();
        List<Entry> outwindTemps = proxy.getLineDataSetByIndex(OUTWINDLINE).getValues();
        List<Entry> accBeanTemps = proxy.getLineDataSetByIndex(ACCBEANLINE).getValues();
        List<Float> timex = proxy.getTimex();
        int expectedNextTime = 0;
        for(int i = 0; i < beanTemps.size(); i += 29){
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(10, 10, 10, 10);
            TableLayout.LayoutParams p = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(p);
            String[] content = new String[5];
            int timeIndex = correct30InternalTime(timex, i, expectedNextTime);

            // if(Math.round(timex.get(timeIndex)) != expectedNextTime){
            content[0] = FormatUtils.getTimeWithFormat(Math.round(expectedNextTime));
            // }
            /*if(timeIndex != -1){
                content[0] = FormatUtils.getTimeWithFormat(Math.round(timeIndex));
            }else{
                // TODO 如果timeIndex 为-1
            }*/
            // 如果假数据和期待数据的时间相差6s以上，视为数据丢失
            /*if(Math.abs(timex.get(timeIndex) - expectedNextTime) > 6){
                content[1] = "数据丢失";
                content[2] = "数据丢失";
                content[3] = "数据丢失";
                content[4] = "数据丢失";
            }else{*/
                content[1] = ConvertUtils.getCrspTemperatureValue(beanTemps.get(timeIndex).getY() + "") + tempratureUnit;
                content[2] = ConvertUtils.getCrspTemperatureValue(inwindTemps.get(timeIndex).getY() + "") + tempratureUnit;
                content[3] = ConvertUtils.getCrspTemperatureValue(outwindTemps.get(timeIndex).getY() + "") + tempratureUnit;
                content[4] = ConvertUtils.getCrspTemperatureValue(accBeanTemps.get(timeIndex).getY() + "") + tempratureUnit;
            // }


            for (int j = 0; j < 5; ++j) {
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(content[j]);
                tableRow.addView(textView, lp);
            }
            tableRow.setGravity(Gravity.CENTER);
            tableLayout.addView(tableRow, p);
            expectedNextTime += 30;
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

            // 校园专用
            _bean_ = beanInfo.getBeanName();
            _species_ = beanInfo.getSpecies();

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

            TextView beanRawWeight = new TextView(this);
            beanRawWeight.setText("生豆重量：" + ConvertUtils.getCrspWeightValue(beanInfo.getUsage()) + weightUnit);
            beanRawWeight.setLayoutParams(temp);

            content[0].addView(beanName);
            content[0].addView(beanSpecies);
            content[0].addView(beanCountry);
            content[0].addView(beanLevel);
            content[0].addView(beanArea);

            content[1].addView(beanAltitude);
            content[1].addView(beanHandler);
            content[1].addView(beanWaterContent);
            content[1].addView(beanManor);
            content[1].addView(beanRawWeight);

            // 设置外linearlayout
            LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
            // 设置margins
            lp.setMargins(0, 0, 0, Util.dpToPx(16.3125f, getResources()));
            // 设置方向
            outter.setOrientation(OrientationHelper.HORIZONTAL);
            // 给outter设置
            outter.setLayoutParams(lp);

            linearLayouts.add(outter);
        }
        return linearLayouts;
    }

    private List<TableRow> generateEventList(List<Entry> entryWithEvents){
        List<TableRow> list = new LinkedList<>();
        for (Entry entry : entryWithEvents) {
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.BLACK);

            TextView type = new TextView(this);
            type.setTextSize(12);
            type.setBackgroundColor(Color.WHITE);

            TextView time = new TextView(this);
            time.setBackgroundColor(Color.WHITE);
            time.setTextSize(12);

            TextView supplyContent = new TextView(this);
            supplyContent.setBackgroundColor(Color.WHITE);
            supplyContent.setTextSize(12);

            Event event = entry.getEvent();

            String eventDescription = event.getDescription();
            type.setText(ConvertUtils.getEventType(ConvertUtils.separateStrByChar(eventDescription, ":", false)));
            supplyContent.setText(ConvertUtils.separateStrByChar(eventDescription, ":", true));

            time.setText("" + FormatUtils.getTimeWithFormat(entry.getX()));


            type.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            time.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            supplyContent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, MATCH_PARENT, 1.0f);
            // 左右下各留一个像素的位置，用于显示黑色的背景以达到黑色线条的目的
            params.setMargins(1, 0, 1, 1);
            params.weight = 1;

            tableRow.addView(type, params);

            TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, MATCH_PARENT, 1.0f);
            params2.setMargins(0, 0, 1, 1);
            params2.weight = 1;

            tableRow.addView(time, params2);

            tableRow.addView(supplyContent, params2);

            list.add(tableRow);
        }
        return list;
    }

    @Override
    public void updateText(int index, Object updateContent) {

    }

    @Override
    public void showToast(int index, String toastContent) {

    }

    @Override
    public void showWarnDialog(int index) {

    }

    @Override
    public void init(final BakeReportProxy proxy) {
        NLogger.i(TAG, proxy.getBakeReport().toString());
        // 设置tempratureset，进行转换
        mChart.setTemperatureSet(proxy.getBakeReport().getTemperatureSet());

        mChart.addNewDatas(proxy.getLineDataSetByIndex(BEANLINE).getValues(), BEANLINE);
        mChart.addNewDatas(proxy.getLineDataSetByIndex(INWINDLINE).getValues(), INWINDLINE);
        mChart.addNewDatas(proxy.getLineDataSetByIndex(OUTWINDLINE).getValues(), OUTWINDLINE);
        mChart.addNewDatas(proxy.getLineDataSetByIndex(ACCBEANLINE).getValues(), ACCBEANLINE);
        mChart.addNewDatas(proxy.getLineDataSetByIndex(ACCINWINDLINE).getValues(), ACCINWINDLINE);
        mChart.addNewDatas(proxy.getLineDataSetByIndex(ACCOUTWINDLINE).getValues(), ACCOUTWINDLINE);

        envTemp.setText(ConvertUtils.getCrspTemperatureValue(proxy.getEnvTemp() + "") + tempratureUnit);
        startTemp.setText(ConvertUtils.getCrspTemperatureValue(proxy.getStartTemp() + "") + tempratureUnit);
        endTemp.setText(ConvertUtils.getCrspTemperatureValue(proxy.getEndTemp()) + tempratureUnit);
        developTime.setText(proxy.getDevelopTime());
        developRate.setText(proxy.getDevelopRate() + "%");
        beanInfos = proxy.getBeanInfos();
        date.setText("烘焙日期：" + proxy.getBakeDate());
        device.setText("设备：" + proxy.getDevice());
        Log.d(TAG, "init: deviceName -> " + proxy.getDevice());


        // FIXME
        int toastValue = computeToastValue((Float.parseFloat(proxy.getBakeDegree()) / 50f ) * 360);
        score.setText((toastValue == Integer.MAX_VALUE ? "N/A" : (toastValue + "")));
        GradientDrawable drawable = (GradientDrawable)getResources().getDrawable(R.drawable.bg_circle_edit_behind);
        drawable.setColor(Utils.getColor(Float.parseFloat(proxy.getBakeDegree()) / 50f));
        scoreLayout.setBackground(drawable);


        globalAccTemp.setText(ConvertUtils.getCrspTemperatureValue(proxy.getGlobalAccBeanTemp() + "") + tempratureUnit);
        idAvgDryTemperature.setText(ConvertUtils.getCrspTemperatureValue(proxy.getAvgDryTemprature() + "") + tempratureUnit);
        idAvgDryTime.setText(FormatUtils.getTimeWithFormat(proxy.getAvgDryTime()));

        idAvgFirbuTemperature.setText(ConvertUtils.getCrspTemperatureValue(proxy.getAvgFirstBurstTemprature() + "") + tempratureUnit);
        idAvgFirbuTime.setText(FormatUtils.getTimeWithFormat(proxy.getAvgFirstBurstTime()));

        idAvgEndTemperature.setText(ConvertUtils.getCrspTemperatureValue(proxy.getAvgEndTemprature() + "") + tempratureUnit);
        idAvgEndTime.setText(FormatUtils.getTimeWithFormat(proxy.getAvgEndTime()));

        breakPointerTemp.setText(ConvertUtils.getCrspTemperatureValue(proxy.getBreakPointerTemprature() + "") + tempratureUnit);
        breakPointerTime.setText(FormatUtils.getTimeWithFormat(proxy.getBreakPointerTime()));

        totalBakeTime.setText(FormatUtils.getTimeWithFormat(proxy.getTotalBakeTime() / 1000));

        float cooked = Float.parseFloat(proxy.getBakeReport().getCookedBeanWeight());
        float raw = proxy.getRawBeanWeight();
        // TODO 处理BakeReport中不包含生豆重量属性的替代方案(后续要改)
        if (raw <= 0.0f) {
            raw = 0f;
            for (BeanInfoSimple simple : proxy.getBeanInfos()) {
                raw += Float.parseFloat(simple.getUsage());
            }
        }
        species.setText("品种 （" + "熟豆重量：" + ConvertUtils.getCrspWeightValue(cooked + "") + weightUnit + "，" + "脱水率：" + Utils.get2PrecisionFloat((cooked * 100) / raw) + "% ）");

        generateProxyDetails(proxy);

        beanContainer = (LinearLayout) findViewById(R.id.id_bean_container);
        beanContent = getNewInstance();
        for (LinearLayout linearLayout : beanContent) {
            beanContainer.addView(linearLayout);
        }
        for (TableRow tableRow : generateEventList(proxy.getEntriesWithEvents())) {
            mTlEventList.addView(tableRow);
        }
    }

}