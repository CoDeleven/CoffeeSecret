package com.dhy.coffeesecret.url;

import com.dhy.coffeesecret.url.base.UrlBase;

/**
 * Created by mxf on 2017/3/4.
 */
public final class UrlLogin extends UrlBase {

    private static final String U = "/u";

    public final static String online(String token) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("online")
                .toString();
    }

    public final static String loginByTel(String tel) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(tel)
                .append(URL_SEPARATOR).append("lgt")
                .toString();
    }

    public final static String loginByPw(String tel, String pw) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(tel)
                .append(URL_SEPARATOR).append(pw)
                .append(URL_SEPARATOR).append("lg")
                .toString();
    }

    public final static String loginByToken(String token) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("lg")
                .toString();
    }

    public final static String updatePassword(String token, String pw) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append(pw)
                .append(URL_SEPARATOR).append("pw")
                .toString();
    }

    public final static String updateUsername(String token, String username) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append(username)
                .append(URL_SEPARATOR).append("un")
                .toString();
    }

    public final static String updateMobilePhone(String token, String phone) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append(phone)
                .append(URL_SEPARATOR).append("phone")
                .toString();
    }

    public final static String updateAge(String token, String age) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append(age)
                .append(URL_SEPARATOR).append("age")
                .toString();
    }

    public final static String updateSex(String token, String sex) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append(sex)
                .append(URL_SEPARATOR).append("sex")
                .toString();
    }

    public final static String updateAvatar(String token) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("avatar")
                .toString();
    }

    public final static String getAvatar(String token) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("avatar/get")
                .toString();
    }

    public final static String getUserInfo(String token) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("info")
                .toString();
    }

    public final static String register(String tel,String pw) {
        return new StringBuffer(URL_COMMON).append(U)
                .append(URL_SEPARATOR).append(tel)
                .append(URL_SEPARATOR).append(pw)
                .append(URL_SEPARATOR).append("reg")
                .toString();
    }


    public static String GET_LIVE_PUBLISH_PATH = "";//没什么用
    public static String GET_LIVE_PLAY_PATH = "";//没什么用
}
