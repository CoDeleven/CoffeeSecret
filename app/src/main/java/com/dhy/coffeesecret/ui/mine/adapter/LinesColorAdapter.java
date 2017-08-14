package com.dhy.coffeesecret.ui.mine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.chart.Model4Chart;
import com.dhy.coffeesecret.pojo.LinesColor;
import com.dhy.coffeesecret.pojo.TemperatureSet;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * CoffeeSecret
 * Created by Simo on 2017/3/3.
 */

public class LinesColorAdapter extends FootViewAdapter {


    private static final String TAG = "LinesColorAdapter";
    private static final int SHOW_DIALOG = 111;
    private static final int CHANGE_CHECKED = 222;
    private Context context;
    private View footView;
    private UniversalConfiguration config;
    private List<LinesColor> linesColorList;
    private LayoutInflater mLayoutInflater;
    private LCHandler mHandler = new LCHandler();
    private TemperatureSet temperatureSet = new TemperatureSet();
    public LinesColorAdapter(Context context, List<LinesColor> linesColorList, View footView) {
        this.context = context;
        this.footView = footView;
        this.linesColorList = linesColorList;


        config = SettingTool.getConfig(context);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LinesColorViewHolder(mLayoutInflater.inflate(R.layout.item_lines_color, null));
    }

    @Override
    public RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType) {
        return new FootViewHolder(footView);
    }

    @Override
    public void onBindViewHolderWithoutFoot(RecyclerView.ViewHolder holder, int position) {
        final LinesColorViewHolder lcHolder = (LinesColorViewHolder) holder;

        final LinesColor linesColor = linesColorList.get(position);
        lcHolder.colorPackageName.setText(linesColor.getPackageName());
        lcHolder.btnUse.setChecked(config.getColorPackageName().equals(linesColor.getPackageName()));

        lcHolder.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = SHOW_DIALOG;
                msg.arg1 = lcHolder.getAdapterPosition();
                Log.e("LinesColorAdapter", "position:" + lcHolder.getAdapterPosition());
                // mHandler.sendMessage(SHOW_DIALOG);
                mHandler.sendMessage(msg);
            }
        });

        lcHolder.btnUse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Message message = new Message();
                    message.what = CHANGE_CHECKED;
                    message.arg1 = lcHolder.getAdapterPosition();
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public int getItemWithFootCount() {
        return linesColorList.size();
    }

    private List<Entry> initVirtualData(int line) {

        List<Entry> entries = new ArrayList<>();
        List<Float> foo = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if(line == Model4Chart.BEANLINE){
                temperatureSet.addTimex(i * 50);
            }
            entries.add(new Entry(i * 50, i * line));
            foo.add(new Float(i * line));
        }
        temperatureSet.addTempratureByIndex(line, foo);
        return entries;
    }

    private class LCHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final int index = msg.arg1;
            final LinesColor linesColor = linesColorList.get(index);
            switch (msg.what) {
                case SHOW_DIALOG:
                    View view = mLayoutInflater.inflate(R.layout.dialog_lines_preview, null);
                    BaseChart4Coffee previewChart = (BaseChart4Coffee) view.findViewById(R.id.preview_chart);
                    Button btnUseDialog = (Button) view.findViewById(R.id.btn_use_dialog);
                    previewChart.initLine();

                    previewChart.setTemperatureSet(temperatureSet);

                    previewChart.changeColorByIndex(linesColor.getBeanColor(), Model4Chart.BEANLINE);
                    previewChart.changeColorByIndex(linesColor.getAccBeanColor(), Model4Chart.ACCBEANLINE);
                    previewChart.changeColorByIndex(linesColor.getInwindColor(), Model4Chart.INWINDLINE);
                    previewChart.changeColorByIndex(linesColor.getOutwindColor(), Model4Chart.OUTWINDLINE);
                    previewChart.changeColorByIndex(linesColor.getAccInwindColor(), Model4Chart.ACCINWINDLINE);
                    previewChart.changeColorByIndex(linesColor.getAccOutwindColor(), Model4Chart.ACCOUTWINDLINE);

                    previewChart.addNewDatas(initVirtualData(Model4Chart.BEANLINE), Model4Chart.BEANLINE);
                    previewChart.addNewDatas(initVirtualData(Model4Chart.ACCBEANLINE), Model4Chart.ACCBEANLINE);
                    previewChart.addNewDatas(initVirtualData(Model4Chart.INWINDLINE), Model4Chart.INWINDLINE);
                    previewChart.addNewDatas(initVirtualData(Model4Chart.OUTWINDLINE), Model4Chart.OUTWINDLINE);
                    previewChart.addNewDatas(initVirtualData(Model4Chart.ACCINWINDLINE), Model4Chart.ACCINWINDLINE);
                    previewChart.addNewDatas(initVirtualData(Model4Chart.ACCOUTWINDLINE), Model4Chart.ACCOUTWINDLINE);
                    final AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle(linesColor.getPackageName())
                            .setView(view)
                            .create();
                    btnUseDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Message message = new Message();
                            message.what = CHANGE_CHECKED;
                            message.arg1 = index;
                            mHandler.sendMessage(message);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                case CHANGE_CHECKED:
                    config.setColorPackageName(linesColor.getPackageName());
                    config.setBeanColor(Color.parseColor(linesColor.getBeanColor()));
                    config.setInwindColor(Color.parseColor(linesColor.getInwindColor()));
                    config.setOutwindColor(Color.parseColor(linesColor.getOutwindColor()));
                    config.setAccBeanColor(Color.parseColor(linesColor.getAccBeanColor()));
                    config.setAccInwindColor(Color.parseColor(linesColor.getAccInwindColor()));
                    config.setAccOutwindColor(Color.parseColor(linesColor.getAccOutwindColor()));
                    config.setEnvColor(Color.parseColor(linesColor.getEnvColor()));
                    SettingTool.saveConfig(config);
                    LinesColorAdapter.this.notifyDataSetChanged();
                    break;
            }
        }
    }

    class LinesColorViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.color_package_name)
        TextView colorPackageName;
        @Bind(R.id.btn_use)
        RadioButton btnUse;
        @Bind(R.id.btn_preview)
        Button btnPreview;

        public LinesColorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
