package com.dhy.coffeesecret.ui.container;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.UIUtils;
import com.dhy.coffeesecret.utils.UnitConvert;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BeanInfoActivity extends AppCompatActivity {

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

    private static final String TAG = "BeanInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean_info);
        ButterKnife.bind(this);

        UIUtils.steepToolBar(this);

        beanInfo = (BeanInfo) getIntent().getSerializableExtra("beanInfo");

        init();
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
                finish();
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
                Intent intent = new Intent(BeanInfoActivity.this, LinesSelectedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        if (beanInfo == null) {
            T.showShort(BeanInfoActivity.this, "获取豆种信息失败");
            return;
        }

        infoArea.setText(beanInfo.getArea());
        infoManor.setText(beanInfo.getManor());
        infoAltitude.setText(beanInfo.getAltitude());
        infoSpecies.setText(beanInfo.getSpecies());
        infoLevel.setText(beanInfo.getLevel());
        infoWaterContent.setText(beanInfo.getWaterContent() * 100 + "%");
        infoHandler.setText(beanInfo.getHandler());
        infoSupplier.setText(beanInfo.getSupplier());
        infoPrice.setText("" + beanInfo.getPrice());
        infoWeight.setText(beanInfo.getWeight() + "g");
        infoBuyDate.setText(String.format("%1$tY-%1$tm-%1$te", beanInfo.getDate()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                BeanInfo beanInfo = (BeanInfo) data.getSerializableExtra("new_bean_info");
                Log.i(TAG, "onActivityResult: " + beanInfo);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
