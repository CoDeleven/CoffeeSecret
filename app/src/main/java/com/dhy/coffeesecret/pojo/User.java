package com.dhy.coffeesecret.pojo;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{
	
	private long uid;
	private String username;
	private String password;
	private int sex;
	private int age;
	private String mobilePhone;
	private String email;
	private String avatar; //头像  Base64 uid.png 方式保存在本地文件夹
	private Date lastLogin; //上次登录时间
	private Date regDate; //注册时间	
	
	public User(){}
	
//	public User(UserInfo user){
//		this.username = user.getUsername();
//		this.password = user.getPassword();
//		this.sex = user.getSex();
//		this.age = user.getAge();
//		this.mobilePhone = user.getMobilePhone();
//		this.email = user.getEmail();
//		this.avatar = user.getAvatar();
//		this.lastLogin = user.getLastLogin();
//		this.regDate = user.getRegDate();
//	}
//	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}


	@Override
	public String toString() {
		return "User{" +
				"age=" + age +
				", uid=" + uid +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", sex=" + sex +
				", mobilePhone='" + mobilePhone + '\'' +
				", email='" + email + '\'' +
				", avatar='" + avatar + '\'' +
				", lastLogin=" + lastLogin +
				", regDate=" + regDate +
				'}';
	}
}

