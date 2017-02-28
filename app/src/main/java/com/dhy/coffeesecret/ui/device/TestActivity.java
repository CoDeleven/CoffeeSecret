package com.dhy.coffeesecret.ui.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.dhy.coffeesecret.pojo.BakeReportImmBeanFactory;
import com.dhy.coffeesecret.utils.BluetoothHelper;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.dhy.coffeesecret.views.ReportMarker;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    @Bind(R.id.id_report_chart)
    BaseChart4Coffee mChart;
    private PopupWindow popupWindow;

    @Bind(R.id.id_report_lineOperator)
    TextView mLineOperator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        // mChart.setDrawMarkers(true);
        // mChart.setMarker(new ReportMarker(this, R.layout.report_marker));
        BakeReportImm imm = BakeReportImmBeanFactory.getBakeReportImm();
        // mChart.setData(new LineData(new LineDataSet(((LineDataSet)imm.getLineData().getDataSetByLabel("豆温", true)).getValues(), "豆温")));
        // mChart.setData(imm.getLineData());
        mChart.initLine();
        // mChart.addNewDatas(((LineDataSet)imm.getLineData().getDataSetByLabel("豆温", true)).getValues(), BaseChart4Coffee.BEANLINE);
        mChart.addOneDataToLine(new Entry(10, 100), BaseChart4Coffee.BEANLINE);
        mChart.addOneDataToLine(new Entry(20, 150), BaseChart4Coffee.BEANLINE);
        mChart.addOneDataToLine(new Entry(30, 200), BaseChart4Coffee.BEANLINE);
        mChart.addOneDataToLine(new Entry(40, 250), BaseChart4Coffee.BEANLINE);


        mLineOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("codelevex", "被点击了");
                getPopupwindow().showAsDropDown(v);
            }
        });
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
}
