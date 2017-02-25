package com.dhy.coffeesecret.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BeanInfo {

	private int id;
	// ��������
	private String name;
	// ����ׯ԰
	private String manor;
	// ͼƬ·��
	private String drawablePath;
	// �������
	private double stockWeight;
	// ���ȹ���
	private String country;
	// ���Ȳ���
	private String area;
	// ���غ���
	private String altitude;
	// ��������
	private String species;
	// ���ȵȼ�
	private String level;
	// ����ʽ
	private String process;
	// ��ˮ��
	private float waterContent;
	// ��Ӧ��
	private String supplier;
	// �۸�
	private double price;
	// ��������
	private Date date;
	
	//����
	private String continent;
	
	// ����Ҫ�־û�
	private List<BakeReport> bakeReports;
	
	// �Ƿ��к決����
	private boolean hasBakeReports;

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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public double getStockWeight() {
		return stockWeight;
	}

	public void setStockWeight(double stockWeight) {
		this.stockWeight = stockWeight;
	}
	public float getWaterContent() {
		return waterContent;
	}

	public void setWaterContent(float waterContent) {
		this.waterContent = waterContent;
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

	public List<BakeReport> getBakeReports() {
		return bakeReports;
	}

	@Deprecated
	public void setBakeReports(List<BakeReport> bakeReports) {
		this.bakeReports = bakeReports;
	}

	public void addBakeReport(BakeReport bakeReport) {
		if (bakeReport != null) {
			if (bakeReports == null) {
				bakeReports = new ArrayList<>();
			}
			bakeReports.add(bakeReport);
		}
	}

	public void removeBakeReport(BakeReport bakeReport) {
		if (bakeReport != null) {
			if (bakeReports == null) {
				bakeReports = new ArrayList<>();
			}

			if (bakeReports.contains(bakeReport)) {
				bakeReports.remove(bakeReport);
			}
		}
	}

	public String getDrawablePath() {
		return drawablePath;
	}

	public void setDrawablePath(String drawablePath) {
		this.drawablePath = drawablePath;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public boolean isHasBakeReports() {
		return hasBakeReports;
	}

	public void setHasBakeReports(boolean hasBakeReports) {
		this.hasBakeReports = hasBakeReports;
	}

}
