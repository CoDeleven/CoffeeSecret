package com.dhy.coffeesecret.ui.launcher;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.dhy.coffeesecret.ui.MainActivity;
import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.pojo.SQLiteHelper;
import com.dhy.coffeesecret.utils.SPPrivateUtils;
import com.dhy.coffeesecret.utils.T;

import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LauncherActivity extends AppCompatActivity{
    private static final String TAG = LauncherActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 0x777;
    private static String[] permissions = new String[]{
            ACCESS_COARSE_LOCATION,
            CAMERA,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION,
            RECORD_AUDIO
    };
    private Handler waitMinutes2StartMainActivity = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent intent;
            if (SPPrivateUtils.getBoolean(LauncherActivity.this, Global.IS_FIRST_TIME, true)) {
                intent = new Intent(LauncherActivity.this, GuidanceActivity.class);
            } else if ("".equals(SPPrivateUtils.getString(LauncherActivity.this, "address", ""))) {
                intent = new Intent(LauncherActivity.this, FirstConnectedActivity.class);
            } else {
                intent = new Intent(LauncherActivity.this, MainActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.in_fade, R.anim.out_fade);
            finish();
            return false;
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StringBuilder sb = new StringBuilder();
        boolean hasDeniedPermission = false;
        for (int i = 0; i < grantResults.length; i++) {
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                sb.append(permissions[i]);
                hasDeniedPermission = true;
            }
        }
        if(hasDeniedPermission){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("没有相应权限...");
            builder.setMessage("抱歉，没有" + sb.toString() + "的权限。\n若需要恢复使用，请前往 应用设置-CoffeeSecret-权限 进行开启");
            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    T.showShort(LauncherActivity.this, "即将退出程序...");
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }).create().show();
        }
        EasyPermissions.onRequestPermissionsResult(REQUEST_CODE, permissions, grantResults, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //TODO 初始化数据库
        SQLiteOpenHelper helper = new SQLiteHelper(this);
        ((MyApplication) getApplicationContext()).setCountry2Continent(helper.getReadableDatabase());
        ((MyApplication) getApplicationContext()).initUnit();

        checkPermission();
    }

    @Override
    public void onBackPressed() {

    }

    @AfterPermissionGranted(REQUEST_CODE)
    private void checkPermission() {
        Log.d(TAG, "checkPermission: 调用一遍");
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.request_permission_rationale), REQUEST_CODE, permissions);
        }else{
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    waitMinutes2StartMainActivity.sendEmptyMessage(0);
                }
            }, 1500);
        }
    }
}
