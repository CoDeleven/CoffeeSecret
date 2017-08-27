package com.dhy.coffeesecret.ui.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.bake.ReportActivity;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private ZXingView zv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        zv = (ZXingView) findViewById(R.id.zv);
        zv.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        zv.startCamera();
        zv.startSpot();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Intent intent = new Intent(this, ReportActivity.class);
        // TODO 需要跳转
/*        Map<String, BakeReport> bakeReports = TestData.getBakeReports(this);
        // 根据id获取bakereport
        BakeReportProxy proxy = new BakeReportProxy(bakeReports.get(TestData.RAW_ID[7] + ""));
        // 设置bakereport
        TestData.setBakeReport(proxy);*/

        startActivity(intent);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        finish();
    }

    public void onBack(View view) {
        finish();
    }
}
