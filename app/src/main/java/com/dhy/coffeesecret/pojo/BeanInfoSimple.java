package com.dhy.coffeesecret.pojo;

import java.io.Serializable;

public class BeanInfoSimple implements Serializable {

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

}
