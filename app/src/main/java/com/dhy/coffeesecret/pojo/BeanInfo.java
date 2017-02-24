package com.dhy.coffeesecret.pojo;


import java.io.Serializable;
import java.util.Date;

public class BeanInfo implements Serializable {
    private int id;
    //咖啡名称
    private String name;
    //咖啡大洲
    private String continent;
    //咖啡国别
    private String country;
    //咖啡产区
    private String area;
    //咖啡庄园
    private String manor;
    //产地海拔
    private String altitude;
    //咖啡种属
    private String species;
    //咖啡大类
    private String oneSpecies;
    //咖啡等级
    private String level;
    //含水量
    private float waterContent;
    //处理方式
    private String handler;
    //库存重量
    private double weight;
    //供应商
    private String supplier;
    //价格
    private double price;
    //购买日期
    private Date date;

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

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getManor() {
        return manor;
    }

    public void setManor(String manor) {
        this.manor = manor;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getOneSpecies() {
        return oneSpecies;
    }

    public void setOneSpecies(String oneSpecies) {
        this.oneSpecies = oneSpecies;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public float getWaterContent() {
        return waterContent;
    }

    public void setWaterContent(float waterContent) {
        this.waterContent = waterContent;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BeanInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", continent='" + continent + '\'' +
                ", country='" + country + '\'' +
                ", area='" + area + '\'' +
                ", manor='" + manor + '\'' +
                ", altitude='" + altitude + '\'' +
                ", species='" + species + '\'' +
                ", oneSpecies='" + oneSpecies + '\'' +
                ", level='" + level + '\'' +
                ", waterContent=" + waterContent +
                ", handler='" + handler + '\'' +
                ", weight=" + weight +
                ", supplier='" + supplier + '\'' +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
