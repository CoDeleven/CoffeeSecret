package com.dhy.coffeesecret;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.common.interfaces.OnWeightUnitChangedListener;
import com.dhy.coffeesecret.utils.SPPrivateUtils;
import com.dhy.coffeesecret.utils.SettingTool;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qiniu.pili.droid.streaming.StreamingEnv;

import java.util.HashSet;
import java.util.Set;

import cn.jesse.nativelogger.Logger;
import cn.jesse.nativelogger.NLogger;
import cn.jesse.nativelogger.formatter.SimpleFormatter;
import cn.jesse.nativelogger.logger.LoggerLevel;
import cn.jesse.nativelogger.util.CrashWatcher;

/**
 * Created by CoDeleven on 17-3-6.
 */
@Logger(tag = "coffeesecret", level = Logger.INFO)
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    public static String weightUnit;
    public static String temperatureUnit;
    private static BakeReportProxy BAKE_REPORT;
    private static SQLiteDatabase country2Continent;
    private String token;
    private String user;
    private Set<OnWeightUnitChangedListener> weightUnitListeners = new HashSet<>();

    public void addOnWeightUnitChangedListener(OnWeightUnitChangedListener listener){
        weightUnitListeners.add(listener);
    }

    public Set<OnWeightUnitChangedListener> getOnWeightUniChangedListener(){
        return weightUnitListeners;
    }


    public void removeListener(Object removedTarget){
        weightUnitListeners.remove(removedTarget);
    }

    public MyApplication() {
        super();
    }
    public String getToken() {
        if(token == null){
            token = SPPrivateUtils.getString(this,"token",null);
        }
        NLogger.i(TAG, "token:" + token);
        return token;
    }

    public void setToken(String token){
        this.token = token;
        NLogger.i(TAG,"token:"+token);
        if(token == null){
            SPPrivateUtils.remove(this,"token");
        }else {
            SPPrivateUtils.put(this,"token",token);
        }

    }
    public static void setCountry2Continent(SQLiteDatabase country2Continent) {
        MyApplication.country2Continent = country2Continent;
    }

    public static String getContinent(String country) {
        Cursor cursor = country2Continent.rawQuery("select continent from countries, continent where country='" + country + "' and countries.parent=continent.id", null);
        String str;
        if(cursor.moveToFirst()){
            str = cursor.getString(0);
        }else{
            str="其它";
        }
        return str;
    }

    private void initLogger(){
        NLogger.getInstance()
                .builder()
                .tag("CoffeeSecret")
                .loggerLevel(LoggerLevel.INFO)
                .fileLogger(true)
                .fileDirectory(getApplicationContext().getFilesDir().getPath() + "/logs")
                .fileFormatter(new SimpleFormatter())
                .expiredPeriod(3)
                .catchException(true, new CrashWatcher.UncaughtExceptionListener() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        NLogger.e("uncaughtException", ex);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //直播初始化
        StreamingEnv.init(getApplicationContext());
        // 初始化设置
        SettingTool.init(this);
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).       //是否获取位置，默认 true
                trackingCrashLog(true).       //是否收集闪退，默认 true
                trackingConsoleLog(true).     //是否收集控制台日志，默认 true
                trackingUserSteps(true).      //是否跟踪用户操作步骤，默认 true
                crashWithScreenshot(true).    //收集闪退是否附带截图，默认 true
                trackingAnr(true).              //收集 ANR，默认 false
                trackingBackgroundCrash(true).  //收集 独立进程 crash，默认 false
                versionName(BuildConfig.VERSION_NAME).         //自定义版本名称，默认 app versionName
                versionCode(BuildConfig.VERSION_CODE).              //自定义版本号，默认 app versionCode
                trackingNetworkURLFilter("(.*)").//自定义网络请求跟踪的 url 规则，默认 null
                enableUserSignIn(true).            //是否允许显示用户登录按钮，默认 true
                startAsync(false).    //设置 为 true 则 SDK 会在异步线程初始化，节
                remoteConfigDataMode(Bugtags.BTGDataModeProduction).//设置远程省主线程时间，默认 false
                startCallback(null).            //初始化成功回调，默认 null配置数据模式，默认Bugtags.BTGDataModeProduction 参见[文档](https://docs.bugtags.com/zh/remoteconfig/android/index.html)
                remoteConfigCallback(null).//设置远程配置的回调函数，详见[文档](https://docs.bugtags.com/zh/remoteconfig/android/index.html)
                enableCapturePlus(false).        //是否开启手动截屏监控，默认 false，参见[文档](https://docs.bugtags.com/zh/faq/android/capture-plus.html)
                extraOptions(Bugtags.BTGConsoleLogCapacityKey, 500).                //设置 log 记录的行数，详见下文
                build();
        //在这里初始化
        Bugtags.start("e71c5cd04eea2bf6fd7e179915935981", this, Bugtags.BTGInvocationEventBubble, options);

        initLogger();
        initImageLoader();
    }

    private void initImageLoader(){
        DisplayImageOptions _options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(_options).build();

        ImageLoader.getInstance().init(configuration);
    }

    public void setBakeReport(BakeReport bakeReport) {
        BAKE_REPORT = new BakeReportProxy(bakeReport);
    }

    public BakeReportProxy getBakeReport() {
        return BAKE_REPORT;
    }

    public void setBakeReport(BakeReportProxy bakeReport) {
        MyApplication.BAKE_REPORT = bakeReport;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent intent = new Intent(this, BluetoothService.class);
        stopService(intent);
        // TODO 关闭蓝牙
        // BluetoothService.BLUETOOTH_OPERATOR.disableBluetooth();
    }

    public void initUnit() {
        UniversalConfiguration config = SettingTool.getConfig();
        weightUnit = config.getWeightUnit();
        temperatureUnit = config.getTempratureUnit();
    }
}
