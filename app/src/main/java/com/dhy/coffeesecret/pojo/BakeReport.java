package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * @author mxf
 * 
 */
public class BakeReport implements Serializable{

	private int id;
	// 烘焙报告名称
	private String name;
	// 设备
	private String device;
	// 熟豆重量
	private String cookedBeanWeight;
	// 烘焙度
	private String roastDegree;

    // 豆温
    private List<EntryTemp> beanTemps = new ArrayList<>();
    // 入风温
    private List<Float> inwindTemps = new ArrayList<>();
    // 出风温
    private List<Float> outwindTemps = new ArrayList<>();
    // 豆温升
    private List<Float> accBeanTemps = new ArrayList<>();
    // 入风温升
    private List<Float> accInwindTemps = new ArrayList<>();
    // 出风温升
    private List<Float> accOutwindTemps = new ArrayList<>();
    // 时间
    private List<Float> timex = new ArrayList<>();

	// 发展时间
	private String developmentTime;
	// 发展率
	private String developmentRate;
	// 环境温度
	private String ambientTemperature;
	// 结束温度
	private String endTemperature;
	// 入豆温度
	private String startTemperature;
	//烘焙日期
	private String date;
	//豆子缩略信息
	private List<BeanInfoSimple> beanInfoSimples = new ArrayList<>();


	// 用于测试单个豆种
	{
		beanInfoSimples.add(new BeanInfoSimple());
	}

	//不需要持久化
//	@JsonIgnore
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

	public String getRoastDegree() {
		return roastDegree;
	}

	public void setRoastDegree(String roastDegree) {
		this.roastDegree = roastDegree;
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

    public String getStartTemperature() {
        return startTemperature;
    }

    public void setStartTemperature(String startTemperature) {
        this.startTemperature = startTemperature;
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

    public List<Float> getAccBeanTemps() {
        return accBeanTemps;
    }

    public void setAccBeanTemps(List<Float> accBeanTemps) {
        this.accBeanTemps = accBeanTemps;
    }

    public List<Float> getAccInwindTemps() {
        return accInwindTemps;
    }

    public void setAccInwindTemps(List<Float> accInwindTemps) {
        this.accInwindTemps = accInwindTemps;
    }

    public List<Float> getAccOutwindTemps() {
        return accOutwindTemps;
    }

    public void setAccOutwindTemps(List<Float> accOutwindTemps) {
        this.accOutwindTemps = accOutwindTemps;
    }

    public List<EntryTemp> getBeanTemps() {
        return beanTemps;
    }

    public void setBeanTemps(List<EntryTemp> beanTemps) {
        this.beanTemps = beanTemps;
    }

    public List<Float> getInwindTemps() {
        return inwindTemps;
    }

    public void setInwindTemps(List<Float> inwindTemps) {
        this.inwindTemps = inwindTemps;
    }

    public List<Float> getOutwindTemps() {
        return outwindTemps;
    }

    public void setOutwindTemps(List<Float> outwindTemps) {
        this.outwindTemps = outwindTemps;
    }

    public List<Float> getTimex() {
        return timex;
    }

    public void setTimex(List<Float> timex) {
        this.timex = timex;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static class EntryTemp implements Serializable{
        float y;
        String event;
        int curStatus;
        public EntryTemp(){

        }
        public EntryTemp(float y, String event, int curStatus){
            this.y = y;
            this.event = event;
            this.curStatus = curStatus;
        }
    }

	// 用于测试
	public String getSingleBeanName(){
		return beanInfoSimples.get(0).getBeanName();
	}
	// 用于测试
	public void setSingleBeanName(String beanName){
		beanInfoSimples.get(0).setBeanName(beanName);
	}
}
