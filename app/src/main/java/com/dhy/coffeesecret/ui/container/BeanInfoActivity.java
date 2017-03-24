package com.dhy.coffeesecret.ui.container;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.mine.HistoryLineActivity;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.UIUtils;
import com.dhy.coffeesecret.utils.UnitConvert;
import com.dhy.coffeesecret.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BeanInfoActivity extends AppCompatActivity {

    private static final String TAG = "BeanInfoActivity";
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.btn_back)
    ImageView btnBack;
    @Bind(R.id.bean_name)
    TextView beanName;
    @Bind(R.id.header)
    RelativeLayout header;
    @Bind(R.id.info_area)
    TextView infoArea;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.info_manor)
    TextView infoManor;
    @Bind(R.id.info_altitude)
    TextView infoAltitude;
    @Bind(R.id.info_species)
    TextView infoSpecies;
    @Bind(R.id.info_level)
    TextView infoLevel;
    @Bind(R.id.info_water_content)
    TextView infoWaterContent;
    @Bind(R.id.info_handler)
    TextView infoHandler;
    @Bind(R.id.info_supplier)
    TextView infoSupplier;
    @Bind(R.id.info_price)
    TextView infoPrice;
    @Bind(R.id.info_weight)
    TextView infoWeight;
    @Bind(R.id.info_buy_date)
    TextView infoBuyDate;
    @Bind(R.id.btn_curve)
    Button btnCurve;
    @Bind(R.id.sv)
    ScrollView sv;
    @Bind(R.id.btn_edit)
    ImageView btnEdit;
    private float editButtonTrans;
    private BeanInfo beanInfo;
    private static BeanInfoActivity infoActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean_info);
        ButterKnife.bind(this);

        UIUtils.steepToolBar(this);
        beanInfo = (BeanInfo) getIntent().getSerializableExtra("beanInfo");
        init();
        infoActivity = this;
    }

    @Override
    protected void onResume() {
        ObjectAnimator.ofFloat(btnEdit, "alpha", 0f, 1f)
                .setDuration(1000).start();
        ObjectAnimator.ofFloat(btnEdit, "translationY", 0f, editButtonTrans)
                .setDuration(1000).start();

        super.onResume();
    }

    private void init() {
        editButtonTrans = UnitConvert.dp2px(getResources(), 25);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeanInfoActivity.this, EditBeanActivity.class);
                intent.putExtra("beanInfo", beanInfo);
                startActivityForResult(intent, 111);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        btnCurve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BeanInfoActivity.this, HistoryLineActivity.class);
                intent.putExtra("bakeReports",(ArrayList<BakeReport>)beanInfo.getBakeReports());
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        if (beanInfo == null) {
            T.showShort(BeanInfoActivity.this, "获取豆种信息失败");
            return;
        }

        // beanName.setText(beanInfo.getCountry() + beanInfo.getSpecies());
        // TODO 可以自定义豆名，默认豆名以国家+豆种形式呈现
        beanName.setText(beanInfo.getName());
        infoArea.setText(beanInfo.getArea());
        infoManor.setText(beanInfo.getManor());
        infoAltitude.setText(beanInfo.getAltitude());
        infoSpecies.setText(beanInfo.getSpecies());
        infoLevel.setText(beanInfo.getLevel());
        infoWaterContent.setText(beanInfo.getWaterContent() + "%");
        infoHandler.setText(beanInfo.getProcess());
        infoSupplier.setText(beanInfo.getSupplier());
        infoPrice.setText("" + beanInfo.getPrice());
        // 数值的单位kg转换为当前单位
        infoWeight.setText(Utils.getCrspWeightValue(beanInfo.getStockWeight() + "") + MyApplication.weightUnit);
        infoBuyDate.setText(String.format("%1$tY-%1$tm-%1$te", beanInfo.getDate()));
        btnCurve.setEnabled(beanInfo.hasBakeReports());
    }
    private BeanInfo newBeanInfo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                newBeanInfo = (BeanInfo) data.getSerializableExtra("new_bean_info");
                beanName.setText(newBeanInfo.getName());
                infoArea.setText(newBeanInfo.getArea());
                infoManor.setText(newBeanInfo.getManor());
                infoAltitude.setText(newBeanInfo.getAltitude());
                infoSpecies.setText(newBeanInfo.getSpecies());
                infoLevel.setText(newBeanInfo.getLevel());
                infoWaterContent.setText(newBeanInfo.getWaterContent() + "%");
                infoHandler.setText(newBeanInfo.getProcess());
                infoSupplier.setText(newBeanInfo.getSupplier());
                infoPrice.setText("" + newBeanInfo.getPrice());
                infoWeight.setText(Utils.getCrspWeightValue(newBeanInfo.getStockWeight() + "") + MyApplication.weightUnit);
                infoBuyDate.setText(String.format("%1$tY-%1$tm-%1$te", newBeanInfo.getDate()));
                btnCurve.setEnabled(newBeanInfo.hasBakeReports());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (newBeanInfo == null) {
            setResult(RESULT_CANCELED);
        } else {
            Intent intent = new Intent();
            intent.putExtra("new_bean_info", newBeanInfo);
            setResult(RESULT_OK, intent);
        }
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    /**
     * 方便删除时关闭本activity
     * @return
     */
    public static BeanInfoActivity getInstance(){
        return infoActivity;
    }
}
