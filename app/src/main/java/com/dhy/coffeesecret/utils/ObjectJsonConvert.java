package com.dhy.coffeesecret.utils;

import android.util.JsonReader;

import com.dhy.coffeesecret.pojo.BakeReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class ObjectJsonConvert {
    public static JSONObject bakereport2Json(BakeReport report) {
        JSONObject jsonObject = new JSONObject();
        String curveFilePath = report.getCurveFilePath();
        String eventFilePath = report.getEventFilePath();
        File file = new File(curveFilePath, report.getName());
        List<Double> beanTemprature = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            JsonReader jsonReader = new JsonReader(isr);
            jsonReader.beginObject();
            String name = jsonReader.nextName();
            if ("beanTemp".equals(name)) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    beanTemprature.add(jsonReader.nextDouble());
                }
            }
            jsonReader.endArray();
            jsonReader.endObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("name", report.getName());
            jsonObject.put("baker", report.getBaker());
            jsonObject.put("device", report.getDevice());
            jsonObject.put("beanName", report.getBeanName());
            jsonObject.put("cookedBeanWeight", report.getCookedBeanWeight());
            jsonObject.put("rawBeanWeight", report.getRawBeanWeight());
            jsonObject.put("roastDegree", report.getRoastDegree());
            jsonObject.put("developmentTime", report.getDevelopmentTime());
            jsonObject.put("developmentRate", report.getDevelopmentRate());
            jsonObject.put("ambientTemperature", report.getAmbientTemperature());
            jsonObject.put("endTemperature", report.getEndTemperature());
            jsonObject.put("beanTemperature", report.getBeanTemperature());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;
    }
}
