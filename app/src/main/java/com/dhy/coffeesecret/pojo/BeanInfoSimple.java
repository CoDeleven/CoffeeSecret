package com.dhy.coffeesecret.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class BeanInfoSimple implements Parcelable {

    private long singleBeanId;
    private String beanName;
    // 豆种
    private String species;
    // 咖啡国别
    private String country;
    // 咖啡等级
    private String level;
    // 咖啡产区
    private String area;
    // 产地海拔
    private String altitude;
    // 咖啡庄园
    private String manor;
    // 处理方式
    private String process;
    // 含水量
    private String waterContent;
    // 使用量
    private String usage;

    public BeanInfoSimple() {

    }


    public BeanInfoSimple(BeanInfo beanInfo, String usage) {
        this.altitude = beanInfo.getAltitude();
        this.area = beanInfo.getArea();
        this.beanName = beanInfo.getName();
        this.country = beanInfo.getCountry();
        this.level = beanInfo.getLevel();
        this.manor = beanInfo.getManor();
        this.process = beanInfo.getProcess();
        this.species = beanInfo.getSpecies();
        this.usage = usage;
        this.waterContent = beanInfo.getWaterContent() + "";
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getManor() {
        return manor;
    }

    public void setManor(String manor) {
        this.manor = manor;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getWaterContent() {
        return waterContent;
    }

    public void setWaterContent(String waterContent) {
        this.waterContent = waterContent;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public long getSingleBeanId() {
        return singleBeanId;
    }

    public void setSingleBeanId(long singleBeanId) {
        this.singleBeanId = singleBeanId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.singleBeanId);
        dest.writeString(this.beanName);
        dest.writeString(this.species);
        dest.writeString(this.country);
        dest.writeString(this.level);
        dest.writeString(this.area);
        dest.writeString(this.altitude);
        dest.writeString(this.manor);
        dest.writeString(this.process);
        dest.writeString(this.waterContent);
        dest.writeString(this.usage);
    }

    protected BeanInfoSimple(Parcel in) {
        this.singleBeanId = in.readLong();
        this.beanName = in.readString();
        this.species = in.readString();
        this.country = in.readString();
        this.level = in.readString();
        this.area = in.readString();
        this.altitude = in.readString();
        this.manor = in.readString();
        this.process = in.readString();
        this.waterContent = in.readString();
        this.usage = in.readString();
    }

    public static final Creator<BeanInfoSimple> CREATOR = new Creator<BeanInfoSimple>() {
        @Override
        public BeanInfoSimple createFromParcel(Parcel source) {
            return new BeanInfoSimple(source);
        }

        @Override
        public BeanInfoSimple[] newArray(int size) {
            return new BeanInfoSimple[size];
        }
    };
}
