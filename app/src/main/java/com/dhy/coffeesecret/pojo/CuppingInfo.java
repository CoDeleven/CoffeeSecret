package com.dhy.coffeesecret.pojo;

import java.io.Serializable;
import java.util.Date;

public class CuppingInfo implements Serializable {
    // id
    private int id;
    // 咖啡得分
    private int score;
    // 烘焙瑕疵
    private int flaw;
    // 干湿香
    private int dryAndFragrant;
    // 干香
    private int dryFragrance;
    // 湿香
    private int wetAroma;
    // 干湿香描述
    private String dryWetDescription;
    // 风味
    private int flavor;
    // 酸质
    private int acidity;
    // 强度
    private int strength;
    // 醇厚
    private int mellow;
    // 厚重
    private int level;
    // 甜度
    private int sweetness;
    // 发展程度
    private int development;
    // 均衡
    private int balance;
    // 余韵
    private int afterTaste;
    // 整体感
    private int overall;
    // 评价
    private String evaluate;
    // 发展不充分
    private int underdevelopment;
    // 发展过度
    private int overdevelopment;
    // 烘焙味道
    private int baked;
    // 自焙烫伤
    private int scorched;
    // 胚芽烫伤
    private int tipped;
    // 豆表烫伤
    private int faced;

    // TODO: 2017/2/22  
    private Date date;

    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFlaw() {
        return flaw;
    }

    public void setFlaw(int flaw) {
        this.flaw = flaw;
    }

    public int getDryAndFragrant() {
        return dryAndFragrant;
    }

    public void setDryAndFragrant(int dryAndFragrant) {
        this.dryAndFragrant = dryAndFragrant;
    }

    public int getDryFragrance() {
        return dryFragrance;
    }

    public void setDryFragrance(int dryFragrance) {
        this.dryFragrance = dryFragrance;
    }

    public int getWetAroma() {
        return wetAroma;
    }

    public void setWetAroma(int wetAroma) {
        this.wetAroma = wetAroma;
    }

    public String getDryWetDescription() {
        return dryWetDescription;
    }

    public void setDryWetDescription(String dryWetDescription) {
        this.dryWetDescription = dryWetDescription;
    }

    public int getFlavor() {
        return flavor;
    }

    public void setFlavor(int flavor) {
        this.flavor = flavor;
    }

    public int getAcidity() {
        return acidity;
    }

    public void setAcidity(int acidity) {
        this.acidity = acidity;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getMellow() {
        return mellow;
    }

    public void setMellow(int mellow) {
        this.mellow = mellow;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSweetness() {
        return sweetness;
    }

    public void setSweetness(int sweetness) {
        this.sweetness = sweetness;
    }

    public int getDevelopment() {
        return development;
    }

    public void setDevelopment(int development) {
        this.development = development;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getAfterTaste() {
        return afterTaste;
    }

    public void setAfterTaste(int afterTaste) {
        this.afterTaste = afterTaste;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public int getUnderdevelopment() {
        return underdevelopment;
    }

    public void setUnderdevelopment(int underdevelopment) {
        this.underdevelopment = underdevelopment;
    }

    public int getOverdevelopment() {
        return overdevelopment;
    }

    public void setOverdevelopment(int overdevelopment) {
        this.overdevelopment = overdevelopment;
    }

    public int getBaked() {
        return baked;
    }

    public void setBaked(int baked) {
        this.baked = baked;
    }

    public int getScorched() {
        return scorched;
    }

    public void setScorched(int scorched) {
        this.scorched = scorched;
    }

    public int getTipped() {
        return tipped;
    }

    public void setTipped(int tipped) {
        this.tipped = tipped;
    }

    public int getFaced() {
        return faced;
    }

    public void setFaced(int faced) {
        this.faced = faced;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
