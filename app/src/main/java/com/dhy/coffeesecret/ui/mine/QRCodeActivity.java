package com.dhy.coffeesecret.ui.mine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.bake.ReportActivity;
import com.dhy.coffeesecret.url.UrlBake;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private ZXingView zv;
    private String token;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        token = ((MyApplication) getApplication()).getToken();
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
    public void onScanQRCodeSuccess(final String result) {
        // 0为类型，1为分享条目id，2为条目拥有者
        mProgress = ProgressDialog.show(this, "正在加载烘焙报告", "正在努力获取资源，请稍后....", false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = UrlBake.scanSuccess(token, result);
                    String response = HttpUtils.getStringFromServer(url, token, QRCodeActivity.this);
                    Log.d("QRCodeActivity", response);
                    if (!"null".equals(response)) {
                        final BakeReport report = new Gson().fromJson(response, BakeReport.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.cancel();
                                toNextActivity(report);
                                QRCodeActivity.this.finish();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        // TODO 需要跳转
/*        Map<String, BakeReport> bakeReports = TestData.getBakeReports(this);
        // 根据id获取bakereport
        BakeReportProxy proxy = new BakeReportProxy(bakeReports.get(TestData.RAW_ID[7] + ""));
        // 设置bakereport
        TestData.setBakeReport(proxy);*/


    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        finish();
    }

    public void onBack(View view) {
        finish();
    }

    private void toNextActivity(final BakeReport bakeReport) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(QRCodeActivity.this, ReportActivity.class);
                intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), bakeReport);
                startActivity(intent);
            }
        });
    }


}
