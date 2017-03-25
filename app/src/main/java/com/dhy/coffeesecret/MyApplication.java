package com.dhy.coffeesecret;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.utils.CacheUtils;
import com.dhy.coffeesecret.utils.HttpParser;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.SPPrivateUtils;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.URLs;
import com.google.gson.Gson;
import com.qiniu.pili.droid.streaming.StreamingEnv;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dhy.coffeesecret.utils.HttpUtils.getRequest;

/**
 * Created by CoDeleven on 17-3-6.
 */

public class MyApplication extends Application {
    public static String weightUnit;
    public static String tempratureUnit;
    private static Map<String, BeanInfo> beanInfos = new HashMap<>();
    private static Map<String, CuppingInfo> cupInfos = new HashMap<>();
    private static Map<String, BakeReport> bakeReports = new HashMap<>();
    private static String url = "-1";
    private static CacheUtils cacheUtils;
    private static BakeReportProxy BAKE_REPORT;
    private static SQLiteDatabase country2Continent;
    private String user;

    public MyApplication() {
        super();
    }

    public static void setUrl(String temp) {
        url = temp;
    }

    public static SQLiteDatabase getCountry2Continent() {
        return country2Continent;
    }

    public static void setCountry2Continent(SQLiteDatabase country2Continent) {
        MyApplication.country2Continent = country2Continent;
    }

    public static String getContinent(String country) {
        Cursor cursor = country2Continent.rawQuery("select continent from countries, continent where country='" + country + "' and countries.parent=continent.id", null);
        cursor.moveToFirst();
        String str = cursor.getString(0);
        return str;
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
    }

    // 每次进入应用时进行校验
    private void init() {
        user = SPPrivateUtils.getString(this, "user", "-1");
        int version = SPPrivateUtils.getInt(this, "version", -1);

        // 模拟请求（此时没有用户类）
        String json = "{user:\"test\", version:\"" + version + "}";
        RequestBody requestBody = RequestBody.create(HttpUtils.TYPE, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        // TODO 从服务器获取版本
/*        HttpUtils.enqueue(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO 获取失败的请求
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // TODO 获取成功
                if()
            }
        });*/
    }

    /**
     * 获取所有的bakeReports
     *
     * @return
     */
    public Map<String, ? extends BakeReport> getBakeReports() {
        if (bakeReports.isEmpty()) {
            // 设置获取所有烘焙报告的url
            url = URLs.GET_ALL_BAKE_REPORT;
            // 进行初始化
            initMap(BakeReport.class);
        }
        return bakeReports;
    }

    /**
     * 获取所有豆子信息
     *
     * @return
     */
    public Map<String, ? extends BeanInfo> getBeanInfos() {
        if (beanInfos.isEmpty()) {
            url = URLs.GET_ALL_BEAN_INFO;
            initMap(BeanInfo.class);
        }
        return beanInfos;
    }

    /**
     * 获取所有杯测信息
     *
     * @return
     */
    public Map<String, ? extends CuppingInfo> getCupInfos() {
        if (cupInfos.isEmpty()) {
            url = URLs.GET_ALL_CUPPING;
            initMap(CuppingInfo.class);
        }
        return cupInfos;
    }

    /**
     * 初始化需要的文件
     */
    private void initMap(Class clazz) {
        boolean status = false;
        if (cacheUtils == null) {
            cacheUtils = CacheUtils.getCacheUtils(this);
        }
        // TODO 这里应该进行版本校验
        if (false) {

        }
        if (clazz == BakeReport.class) {
            bakeReports.putAll(cacheUtils.getListObjectFromCache(clazz));
            if (bakeReports.size() == 0) {
                status = true;
            }
        } else if (clazz == CuppingInfo.class) {
            cupInfos.putAll(cacheUtils.getListObjectFromCache(clazz));
            if (cupInfos.size() == 0) {
                status = true;
            }
        } else if (clazz == BeanInfo.class) {
            beanInfos.putAll(cacheUtils.getListObjectFromCache(clazz));
            if (beanInfos.size() == 0) {
                status = true;
            }
        }
        if (status) {
            initMapFromServer(clazz);
        }
    }

    /**
     * 从服务器获取对应类型的信息
     *
     * @param clazz
     */
    public void initMapFromServer(final Class clazz) {
        HttpUtils.enqueue(getRequest(url), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String temp = response.body().string();
                Map<String, Object> maps = new HashMap<>();
                if (clazz == BakeReport.class) {
                    bakeReports.putAll(HttpParser.getBakeReports(temp));
                    maps.putAll(bakeReports);
                } else if (clazz == BeanInfo.class) {
                    beanInfos.putAll(HttpParser.getBeanInfos(temp));
                    maps.putAll(beanInfos);
                } else if (clazz == CuppingInfo.class) {
                    cupInfos.putAll(HttpParser.getCuppingInfos(temp));
                    maps.putAll(cupInfos);
                }
                if (cacheUtils == null) {
                    cacheUtils = CacheUtils.getCacheUtils(getApplicationContext());
                }
                for (String key : maps.keySet()) {
                    cacheUtils.saveObject(key, new Gson().toJson(maps.get(key)), clazz);
                }
            }
        });
    }

    /**
     * 获取该类的前缀
     *
     * @param clazz
     * @return
     */
    private String getPrefix(Class clazz) {
        if (clazz == BakeReport.class) {
            url = URLs.GET_ALL_BAKE_REPORT;
            return CacheUtils.BAKE_REPORT_PREFIX;
        } else if (clazz == BeanInfo.class) {
            url = URLs.GET_ALL_BEAN_INFO;
            return CacheUtils.BEAN_INFO_PREFIX;
        } else if (clazz == CuppingInfo.class) {
            url = URLs.GET_ALL_CUPPING;
            return CacheUtils.CUP_INFO_PREFEX;
        }
        return null;
    }

    public void setBakeReport(BakeReport bakeReport) {
        BAKE_REPORT = new BakeReportProxy(bakeReport);
    }

    public BakeReportProxy getBakeReport() {
        return BAKE_REPORT;
    }

    public void setBakeReport(BakeReportProxy bakeReport) {
        this.BAKE_REPORT = bakeReport;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent intent = new Intent(this, BluetoothService.class);
        stopService(intent);
        BluetoothService.BLUETOOTH_OPERATOR.disableBluetooth();
    }

    public void initUnit() {
        UniversalConfiguration config = SettingTool.getConfig(this);
        weightUnit = config.getWeightUnit();
        tempratureUnit = config.getTempratureUnit();
    }
}
