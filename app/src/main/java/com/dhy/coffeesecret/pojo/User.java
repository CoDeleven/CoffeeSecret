package com.dhy.coffeesecret.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class User implements Parcelable{

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.uid);
		dest.writeString(this.username);
		dest.writeString(this.password);
		dest.writeInt(this.sex);
		dest.writeInt(this.age);
		dest.writeString(this.mobilePhone);
		dest.writeString(this.email);
		dest.writeString(this.avatar);
		dest.writeLong(this.lastLogin != null ? this.lastLogin.getTime() : -1);
		dest.writeLong(this.regDate != null ? this.regDate.getTime() : -1);
	}

	protected User(Parcel in) {
		this.uid = in.readLong();
		this.username = in.readString();
		this.password = in.readString();
		this.sex = in.readInt();
		this.age = in.readInt();
		this.mobilePhone = in.readString();
		this.email = in.readString();
		this.avatar = in.readString();
		long tmpLastLogin = in.readLong();
		this.lastLogin = tmpLastLogin == -1 ? null : new Date(tmpLastLogin);
		long tmpRegDate = in.readLong();
		this.regDate = tmpRegDate == -1 ? null : new Date(tmpRegDate);
	}

	public static final Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}

