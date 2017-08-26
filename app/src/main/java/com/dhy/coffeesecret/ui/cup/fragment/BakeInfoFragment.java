package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayoutItem;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;
import com.dhy.coffeesecret.ui.device.ReportActivity;
import com.dhy.coffeesecret.utils.ConvertUtils;
import com.dhy.coffeesecret.views.BaseChart4Coffee;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCBEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCINWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.ACCOUTWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.BEANLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.INWINDLINE;
import static com.dhy.coffeesecret.model.chart.Model4Chart.OUTWINDLINE;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.EDIT_INFO;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.NEW_CUPPING;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.SELECT_LINE;

public class BakeInfoFragment extends Fragment implements View.OnClickListener {

    public final static String TARGET = "target";
    public final static int RESULT_CODE_ADD = 0x123;
    public final static int RESULT_CODE_NONE = 0x0;
    public final static int RESULT_CODE_EXIT = 0x999;

    private TextView mDevTime;
    private TextView mStartTemp;
    private TextView mEndTemp;
    private TextView mDevRate;
    private TextView mEnvTime;
    private BaseChart4Coffee mChart;
    private View mView;
    private ExpandableLayoutListView mListView;
    private Button mButtonBake;
    private BakeReport mBakeReport;
    private OnBakeInfoLoadedListener mOnBakeInfoLoadedListener;
    private boolean isNewCupping;
    private BaseAdapter mAdapter;

    public BakeInfoFragment() {

    }

    public static BakeInfoFragment newInstance(BakeReport report, boolean isNewCupping) {
        BakeInfoFragment fragment = new BakeInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(TARGET, report);
        args.putBoolean("isNewCupping", isNewCupping);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBakeReport = (BakeReport) getArguments().getSerializable(TARGET);
            isNewCupping = getArguments().getBoolean("isNewCupping");
        }

        if (isNewCupping) {
            // Intent i = new Intent(getActivity(), LinesSelectedActivity.class);
            Intent i = new Intent(getActivity(), CupRelatedReportSelectActivity.class);
            // i.putExtra(LINE_PARAMETER_KEY, SET_BAKE_REPORT);
            startActivityForResult(i, SELECT_LINE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SELECT_LINE == requestCode) {
            if (RESULT_OK == resultCode) {
                mBakeReport = (BakeReport) data.getSerializableExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey());
                mOnBakeInfoLoadedListener.onLoaded(mBakeReport);
                updateUI();
            } else if (RESULT_CANCELED == resultCode) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_fade, R.anim.out_to_right);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bake_info, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mButtonBake = (Button) mView.findViewById(R.id.btn_bake);
        mButtonBake.setOnClickListener(this);

        mDevTime = (TextView) mView.findViewById(R.id.dev_time);
        mDevRate = (TextView) mView.findViewById(R.id.dev_rate);
        mEndTemp = (TextView) mView.findViewById(R.id.end_temp);
        mStartTemp = (TextView) mView.findViewById(R.id.start_temp);
        mEnvTime = (TextView) mView.findViewById(R.id.env_time);
        mChart = (BaseChart4Coffee) mView.findViewById(R.id.chart);
        mChart.initLine();


        if (!isNewCupping && mBakeReport == null) {
            mButtonBake.setEnabled(false);
//            mChart.setTemperatureSet(mBakeReport.getTemperatureSet());
        }

        mListView = (ExpandableLayoutListView) mView.findViewById(R.id.beanInfo);
        mAdapter = new BaseAdapter() {
            List<BeanInfoSimple> beanInfoSimples;

            @Override
            public int getCount() {
                if (mBakeReport == null || mBakeReport.getBeanInfoSimples() == null) {
                    return 0;
                }
                beanInfoSimples = mBakeReport.getBeanInfoSimples();
                return beanInfoSimples.size();
            }

            @Override
            public void notifyDataSetChanged() {
                beanInfoSimples = mBakeReport.getBeanInfoSimples();
                super.notifyDataSetChanged();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.expande_item, viewGroup, false);
                BeanInfoSimple simple = beanInfoSimples.get(i);
                if (i == 0) {
                    ExpandableLayoutItem layout =
                            (ExpandableLayoutItem) inflate.findViewById(R.id.eli);
                    layout.show();
                }

                TextView tv_name = (TextView) inflate.findViewById(R.id.tv_name);
                TextView tv_use = (TextView) inflate.findViewById(R.id.tv_use);
                TextView tv_country = (TextView) inflate.findViewById(R.id.tv_country);
                TextView tv_manor = (TextView) inflate.findViewById(R.id.tv_manor);
                TextView tv_altitude = (TextView) inflate.findViewById(R.id.tv_altitude);
                TextView tv_area = (TextView) inflate.findViewById(R.id.tv_area);
                TextView tv_level = (TextView) inflate.findViewById(R.id.tv_level);
                TextView tv_process = (TextView) inflate.findViewById(R.id.tv_process);

                tv_name.setText(simple.getBeanName());
                tv_use.setText(ConvertUtils.getCrspWeightValue(simple.getUsage()) + MyApplication.weightUnit);
                tv_country.setText(simple.getCountry());
                tv_manor.setText(simple.getManor());
                tv_altitude.setText(simple.getAltitude());
                tv_area.setText(simple.getArea());
                tv_level.setText(simple.getLevel());
                tv_process.setText(simple.getProcess());

                return inflate; //TODO 待优化
            }
        };
        mListView.setAdapter(mAdapter);
        updateUI();
    }

    private void addNewDatas(BakeReportProxy proxy, int index) {
        mChart.addNewDatas(proxy.getLineDataSetByIndex(index).getValues(), index);
    }

    public void updateUI() {
        if (mBakeReport != null && mDevTime != null) {
            mDevTime.setText(mBakeReport.getDevelopmentTime());
            mDevRate.setText(mBakeReport.getDevelopmentRate() + "%");
            mEndTemp.setText(ConvertUtils.getCrspTemperatureValue(mBakeReport.getEndTemperature()) + MyApplication.temperatureUnit);
            mStartTemp.setText(ConvertUtils.getCrspTemperatureValue(mBakeReport.getStartTemperature()) + MyApplication.temperatureUnit);
            mEnvTime.setText(ConvertUtils.getCrspTemperatureValue(mBakeReport.getAmbientTemperature()) + MyApplication.temperatureUnit);

            BakeReportProxy proxy = new BakeReportProxy(mBakeReport);
            mChart.setTemperatureSet(proxy.getBakeReport().getTemperatureSet());

            int[] temp = {BEANLINE, ACCBEANLINE, INWINDLINE, OUTWINDLINE, ACCINWINDLINE, ACCOUTWINDLINE};
            for (int i : temp) {
                addNewDatas(proxy, i);
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    public void setOnBakeInfoLoadedListener(OnBakeInfoLoadedListener mOnBakeInfoLoadedListener) {
        this.mOnBakeInfoLoadedListener = mOnBakeInfoLoadedListener;
    }

    @Override
    public void onClick(View view) {
        if (isNewCupping && !EDIT_INFO.equals(NEW_CUPPING)) {
            Intent intent = new Intent(getActivity(), CupRelatedReportSelectActivity.class);
            // intent.putExtra(SelectedParameterActivity.LINE_PARAMETER_KEY, SelectedParameterActivity.SET_BAKE_REPORT);
            startActivityForResult(intent, SELECT_LINE);
        } else {
            // TODO 校赛专用
            // ((MyApplication) (getActivity().getApplication())).setBakeReport(mBakeReport);
            Intent intent = new Intent(getActivity(), ReportActivity.class);
            intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), mBakeReport);
            startActivity(intent);
        }
    }

    public interface OnBakeInfoLoadedListener {
        void onLoaded(BakeReport report);
    }
}
