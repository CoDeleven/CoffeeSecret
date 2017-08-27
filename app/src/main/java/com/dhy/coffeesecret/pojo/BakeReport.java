package com.dhy.coffeesecret.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mxf
 */
public class BakeReport implements Parcelable {
    private int id;
    // 烘焙报告名称
    private String name;
    // 设备
    private String device;
    // 熟豆重量
    private String cookedBeanWeight;
    // 烘焙度, 如果用户没有移动SeekBar导致这里为空,会引发错误
    private String roastDegree = "0";
    @SerializedName("tempratureSet")
    private TemperatureSet temperatureSet;
    private float breakPointerTime;
    private float breakPointerTemperature;
    private float avgDryTemperature;
    private float avgDryTime;
    private float avgFirstBurstTime;
    private float avgFirstBurstTemperature;
    private float avgEndTime;
    private float avgEndTemperature;
    private float avgAccBeanTemperature;
    private float avgGlobalBeanTemperature;
    // 发展时间
    private String developmentTime;
    // 发展率
    private String developmentRate;
    // 环境温度
    private String ambientTemperature;
    // 结束温度
    private String endTemperature;
    // 入豆温度
    private String startTemperature;
    //烘焙日期
    private String date;
    //豆子缩略信息
    private List<BeanInfoSimple> beanInfoSimples = new ArrayList<>();
    //不需要持久化
//	@JsonIgnore
//     private CuppingInfo cuppingInfo;
    //当烘焙报告中只有一种豆子的话 烘焙报告上添加 bean的ID
    private long beanId = -1;

    public BakeReport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getCookedBeanWeight() {
        return cookedBeanWeight;
    }

    public void setCookedBeanWeight(String cookedBeanWeight) {
        this.cookedBeanWeight = cookedBeanWeight;
    }

    public String getRoastDegree() {
        return roastDegree;
    }

    public void setRoastDegree(String roastDegree) {
        this.roastDegree = roastDegree;
    }

    public String getDevelopmentTime() {
        return developmentTime;
    }

    public void setDevelopmentTime(String developmentTime) {
        this.developmentTime = developmentTime;
    }

    public String getDevelopmentRate() {
        return developmentRate;
    }

    public void setDevelopmentRate(String developmentRate) {
        this.developmentRate = developmentRate;
    }

    public String getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(String ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public String getEndTemperature() {
        return endTemperature;
    }

    public void setEndTemperature(String endTemperature) {
        this.endTemperature = endTemperature;
    }

    public String getStartTemperature() {
        return startTemperature;
    }

    public void setStartTemperature(String startTemperature) {
        this.startTemperature = startTemperature;
    }

    public List<BeanInfoSimple> getBeanInfoSimples() {
        return beanInfoSimples;
    }

    public void setBeanInfoSimples(List<BeanInfoSimple> beanInfoSimples) {
        this.beanInfoSimples = beanInfoSimples;
    }

    public TemperatureSet getTemperatureSet() {
        return temperatureSet;
    }

    public void setTemperatureSet(TemperatureSet temperatureSet) {
        this.temperatureSet = temperatureSet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // 用于测试
    public String getSingleBeanName() {
        return beanInfoSimples.get(0).getBeanName();
    }

    // 用于测试
    public void setSingleBeanName(String beanName) {
        beanInfoSimples.get(0).setBeanName(beanName);
    }

    public Long getBeanId() {
        return beanId;
    }

    public void setBeanId(Long beanId) {
        this.beanId = beanId;
    }

    public void setBeanId(long beanId) {
        this.beanId = beanId;
    }

    public Map<String, String> getEvents() {
        return temperatureSet.getEvents();
    }

    public float getBreakPointerTime() {
        return breakPointerTime;
    }

    public void setBreakPointerTime(float breakPointerTime) {
        this.breakPointerTime = breakPointerTime;
    }

    public float getBreakPointerTemperature() {
        return breakPointerTemperature;
    }

    public void setBreakPointerTemperature(float breakPointerTemperature) {
        this.breakPointerTemperature = breakPointerTemperature;
    }

    public float getAvgDryTemperature() {
        return avgDryTemperature;
    }

    public void setAvgDryTemperature(float avgDryTemperature) {
        this.avgDryTemperature = avgDryTemperature;
    }

    public float getAvgDryTime() {
        return avgDryTime;
    }

    public void setAvgDryTime(float avgDryTime) {
        this.avgDryTime = avgDryTime;
    }

    public float getAvgFirstBurstTime() {
        return avgFirstBurstTime;
    }

    public void setAvgFirstBurstTime(float avgFirstBurstTime) {
        this.avgFirstBurstTime = avgFirstBurstTime;
    }

    public float getAvgFirstBurstTemperature() {
        return avgFirstBurstTemperature;
    }

    public void setAvgFirstBurstTemperature(float avgFirstBurstTemperature) {
        this.avgFirstBurstTemperature = avgFirstBurstTemperature;
    }

    public float getAvgEndTime() {
        return avgEndTime;
    }

    public void setAvgEndTime(float avgEndTime) {
        this.avgEndTime = avgEndTime;
    }

    public float getAvgEndTemperature() {
        return avgEndTemperature;
    }

    public void setAvgEndTemperature(float avgEndTemperature) {
        this.avgEndTemperature = avgEndTemperature;
    }

    public float getAvgAccBeanTemperature() {
        return avgAccBeanTemperature;
    }

    public void setAvgAccBeanTemperature(float avgAccBeanTemperature) {
        this.avgAccBeanTemperature = avgAccBeanTemperature;
    }

    public float getAvgGlobalBeanTemperature() {
        return avgGlobalBeanTemperature;
    }

    public void setAvgGlobalBeanTemperature(float avgGlobalBeanTemperature) {
        this.avgGlobalBeanTemperature = avgGlobalBeanTemperature;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.device);
        dest.writeString(this.cookedBeanWeight);
        dest.writeString(this.roastDegree);
        dest.writeParcelable(this.temperatureSet, flags);
        dest.writeFloat(this.breakPointerTime);
        dest.writeFloat(this.breakPointerTemperature);
        dest.writeFloat(this.avgDryTemperature);
        dest.writeFloat(this.avgDryTime);
        dest.writeFloat(this.avgFirstBurstTime);
        dest.writeFloat(this.avgFirstBurstTemperature);
        dest.writeFloat(this.avgEndTime);
        dest.writeFloat(this.avgEndTemperature);
        dest.writeFloat(this.avgAccBeanTemperature);
        dest.writeFloat(this.avgGlobalBeanTemperature);
        dest.writeString(this.developmentTime);
        dest.writeString(this.developmentRate);
        dest.writeString(this.ambientTemperature);
        dest.writeString(this.endTemperature);
        dest.writeString(this.startTemperature);
        dest.writeString(this.date);
        dest.writeTypedList(this.beanInfoSimples);
        dest.writeLong(this.beanId);
    }

    protected BakeReport(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.device = in.readString();
        this.cookedBeanWeight = in.readString();
        this.roastDegree = in.readString();
        this.temperatureSet = in.readParcelable(TemperatureSet.class.getClassLoader());
        this.breakPointerTime = in.readFloat();
        this.breakPointerTemperature = in.readFloat();
        this.avgDryTemperature = in.readFloat();
        this.avgDryTime = in.readFloat();
        this.avgFirstBurstTime = in.readFloat();
        this.avgFirstBurstTemperature = in.readFloat();
        this.avgEndTime = in.readFloat();
        this.avgEndTemperature = in.readFloat();
        this.avgAccBeanTemperature = in.readFloat();
        this.avgGlobalBeanTemperature = in.readFloat();
        this.developmentTime = in.readString();
        this.developmentRate = in.readString();
        this.ambientTemperature = in.readString();
        this.endTemperature = in.readString();
        this.startTemperature = in.readString();
        this.date = in.readString();
        this.beanInfoSimples = in.createTypedArrayList(BeanInfoSimple.CREATOR);
        this.beanId = in.readLong();
    }

    public static final Creator<BakeReport> CREATOR = new Creator<BakeReport>() {
        @Override
        public BakeReport createFromParcel(Parcel source) {
            return new BakeReport(source);
        }

        @Override
        public BakeReport[] newArray(int size) {
            return new BakeReport[size];
        }
    };
}
