package com.dhy.coffeesecret.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CuppingInfo implements Parcelable {
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
        float feelScore = 0;
        float flawScore = 0;
        float[] feelScores = getFeelArr();
        float[] flawScores = getFlawArr();
        feelScores[0] /= 2;
        feelScores[1] /= 2;
        feelScores[3] *= 2;
        feelScores[4] *= 2;
        feelScores[5] *= 2;
        for (float score : feelScores) {
            feelScore += score;
        }
        for (float score : flawScores) {
            flawScore += score;
        }
        score = feelScore - flawScore;
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
    public float[] getFeelArr(){
        return new float[]{dryAndFragrant, flavor, afterTaste, acidity, taste, sweetness, balance, overall};
    }

    public float[] getFlawArr(){
        // underdevelopment + overdevelopment + baked + tipped + faced+ scorched;
        return new float[]{underdevelopment, overdevelopment, baked, tipped, faced, scorched};
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CuppingInfo){
            return id == ((CuppingInfo)obj).getId();
        }else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeFloat(this.score);
        dest.writeFloat(this.feel);
        dest.writeFloat(this.flaw);
        dest.writeFloat(this.dryAndFragrant);
        dest.writeFloat(this.flavor);
        dest.writeFloat(this.afterTaste);
        dest.writeFloat(this.acidity);
        dest.writeFloat(this.taste);
        dest.writeFloat(this.sweetness);
        dest.writeFloat(this.balance);
        dest.writeFloat(this.overall);
        dest.writeInt(this.underdevelopment);
        dest.writeInt(this.overdevelopment);
        dest.writeInt(this.baked);
        dest.writeInt(this.scorched);
        dest.writeInt(this.tipped);
        dest.writeInt(this.faced);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeParcelable(this.bakeReport, flags);
    }

    public CuppingInfo() {
    }

    protected CuppingInfo(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.score = in.readFloat();
        this.feel = in.readFloat();
        this.flaw = in.readFloat();
        this.dryAndFragrant = in.readFloat();
        this.flavor = in.readFloat();
        this.afterTaste = in.readFloat();
        this.acidity = in.readFloat();
        this.taste = in.readFloat();
        this.sweetness = in.readFloat();
        this.balance = in.readFloat();
        this.overall = in.readFloat();
        this.underdevelopment = in.readInt();
        this.overdevelopment = in.readInt();
        this.baked = in.readInt();
        this.scorched = in.readInt();
        this.tipped = in.readInt();
        this.faced = in.readInt();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.bakeReport = in.readParcelable(BakeReport.class.getClassLoader());
    }

    public static final Creator<CuppingInfo> CREATOR = new Creator<CuppingInfo>() {
        @Override
        public CuppingInfo createFromParcel(Parcel source) {
            return new CuppingInfo(source);
        }

        @Override
        public CuppingInfo[] newArray(int size) {
            return new CuppingInfo[size];
        }
    };
}
