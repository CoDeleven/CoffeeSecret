package com.dhy.coffeesecret.pojo;

public class CuppingInfo {
	// id
	private int id;

	private String name;
	// ���ȵ÷�
	private int score;
	// �決覴�
	private int flaw;
	// ��ʪ��
	private int dryAndFragrant;
	// ��ζ
	private int flavor;
	// ����
	private int afterTaste;
	// ����
	private int acidity;
	// �ڸ�
	private int taste;
	// ���
	private int sweetness;
	// ����
	private int balance;
	// �����
	private int overall;
	// ��չ�����
	private int underdevelopment;
	// ��չ����
	private int overdevelopment;
	// �決ζ��
	private int baked;
	// �Ա�����
	private int scorched;
	// ��ѿ����
	private int tipped;
	// ��������
	private int faced;

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
}
