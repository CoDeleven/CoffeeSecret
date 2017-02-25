package com.dhy.coffeesecret.pojo;

import java.util.List;

//import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * @author mxf
 * 
 */
public class BakeReport {
	
	private int id;
	// �決��������
	private String name;
	// �豸
	private String device;
	// �춹����
	private String cookedBeanWeight;
	// ��������
	private String rawBeanWeight;
	// �決��
	private String roastDegree;
	// �����ļ�·��
	private String curveFilePath;
	// ��չʱ��
	private String developmentTime;
	// ��չ��
	private String developmentRate;
	// �����¶�
	private String ambientTemperature;
	// �����¶�
	private String endTemperature;
	// �붹�¶�
	private String beanTemperature;
	
	//����������Ϣ
	private List<BeanInfoSimple> beanInfoSimples;
	//����Ҫ�־û�
	//@JsonIgnore
	private CuppingInfo cuppingInfo;
	
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

	public String getRawBeanWeight() {
		return rawBeanWeight;
	}

	public void setRawBeanWeight(String rawBeanWeight) {
		this.rawBeanWeight = rawBeanWeight;
	}

	public String getRoastDegree() {
		return roastDegree;
	}

	public void setRoastDegree(String roastDegree) {
		this.roastDegree = roastDegree;
	}

	public String getCurveFilePath() {
		return curveFilePath;
	}

	public void setCurveFilePath(String curveFilePath) {
		this.curveFilePath = curveFilePath;
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

	public String getBeanTemperature() {
		return beanTemperature;
	}

	public void setBeanTemperature(String beanTemperature) {
		this.beanTemperature = beanTemperature;
	}

	public CuppingInfo getCuppingInfo() {
		return cuppingInfo;
	}

	public void setCuppingInfo(CuppingInfo cuppingInfo) {
		this.cuppingInfo = cuppingInfo;
	}

	public List<BeanInfoSimple> getBeanInfoSimples() {
		return beanInfoSimples;
	}

	public void setBeanInfoSimples(List<BeanInfoSimple> beanInfoSimples) {
		this.beanInfoSimples = beanInfoSimples;
	}

}
