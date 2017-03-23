package com.dhy.coffeesecret.pojo;

import java.io.Serializable;
import java.util.Date;

public class CuppingInfo implements Serializable {
    // id
    private long id;

    private String name;
    // 咖啡得分
    private float score;

    private float feel;
    // 烘焙瑕疵
    private float flaw;
    // 干湿香
    private float dryAndFragrant;
    // 风味
    private float flavor;
    // 余韵
    private float afterTaste;
    // 酸质
    private float acidity;
    // 口感
    private float taste;
    // 甜度
    private float sweetness;
    // 均衡
    private float balance;
    // 整体感
    private float overall;
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

    private Date date;

    private BakeReport bakeReport;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getAcidity() {
        return acidity;
    }

    public void setAcidity(float acidity) {
        this.acidity = acidity;
    }

    public float getAfterTaste() {
        return afterTaste;
    }

    public void setAfterTaste(float afterTaste) {
        this.afterTaste = afterTaste;
    }

    public int getBaked() {
        return baked;
    }

    public void setBaked(int baked) {
        this.baked = baked;
    }

    public BakeReport getBakeReport() {
        return bakeReport;
    }

    public void setBakeReport(BakeReport bakeReport) {
        this.bakeReport = bakeReport;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getDryAndFragrant() {
        return dryAndFragrant;
    }

    public void setDryAndFragrant(float dryAndFragrant) {
        this.dryAndFragrant = dryAndFragrant;
    }

    public int getFaced() {
        return faced;
    }

    public void setFaced(int faced) {
        this.faced = faced;
    }

    public float getFlavor() {
        return flavor;
    }

    public void setFlavor(float flavor) {
        this.flavor = flavor;
    }

    public float getFlaw() {
        flaw = underdevelopment + overdevelopment + baked + tipped + faced+ scorched;
        return flaw;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getOverall() {
        return overall;
    }

    public void setOverall(float overall) {
        this.overall = overall;
    }

    public int getOverdevelopment() {
        return overdevelopment;
    }

    public void setOverdevelopment(int overdevelopment) {
        this.overdevelopment = overdevelopment;
    }

    public int getScorched() {
        return scorched;
    }

    public void setScorched(int scorched) {
        this.scorched = scorched;
    }

    public float getScore() {
        score = getFeel() - getFlaw();
        return score;
    }

    public float getSweetness() {
        return sweetness;
    }

    public void setSweetness(float sweetness) {
        this.sweetness = sweetness;
    }

    public float getTaste() {
        return taste;
    }

    public void setTaste(float taste) {
        this.taste = taste;
    }

    public int getTipped() {
        return tipped;
    }

    public void setTipped(int tipped) {
        this.tipped = tipped;
    }

    public int getUnderdevelopment() {
        return underdevelopment;
    }

    public void setUnderdevelopment(int underdevelopment) {
        this.underdevelopment = underdevelopment;
    }

    public float getFeel() {
        feel = dryAndFragrant + flavor + acidity
                + afterTaste + taste + sweetness + balance + overall;
        return feel;
    }

    @Override
    public String toString() {
        return "CuppingInfo{" +
                "acidity=" + acidity +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", feel=" + feel +
                ", flaw=" + flaw +
                ", dryAndFragrant=" + dryAndFragrant +
                ", flavor=" + flavor +
                ", afterTaste=" + afterTaste +
                ", taste=" + taste +
                ", sweetness=" + sweetness +
                ", balance=" + balance +
                ", overall=" + overall +
                ", underdevelopment=" + underdevelopment +
                ", overdevelopment=" + overdevelopment +
                ", baked=" + baked +
                ", scorched=" + scorched +
                ", tipped=" + tipped +
                ", faced=" + faced +
                ", date=" + date +
                ", bakeReport=" + bakeReport +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CuppingInfo){
            return id == ((CuppingInfo)obj).getId();
        }else {
            return false;
        }
    }
}
