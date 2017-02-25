package com.dhy.coffeesecret.pojo;

import java.io.Serializable;
import java.util.Date;

public class CuppingInfo implements Serializable {
	// id
	private int id;

	private String name;
	// 咖啡得分
	private int score;
	// 烘焙瑕疵
	private int flaw;
	// 干湿香
	private int dryAndFragrant;
	// 风味
	private int flavor;
	// 余韵
	private int afterTaste;
	// 酸质
	private int acidity;
	// 口感
	private int taste;
	// 甜度
	private int sweetness;
	// 均衡
	private int balance;
	// 整体感
	private int overall;
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

	public int getSweetness() {
		return sweetness;
	}

	public void setSweetness(int sweetness) {
		this.sweetness = sweetness;
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

	public BakeReport getBakeReport() {
		return bakeReport;
	}

	public void setBakeReport(BakeReport bakeReport) {
		bakeReport.setCuppingInfo(this);
		this.bakeReport = bakeReport;
	}

	public int getTaste() {
		return taste;
	}

	public void setTaste(int taste) {
		this.taste = taste;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
