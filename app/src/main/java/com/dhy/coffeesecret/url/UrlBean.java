package com.dhy.coffeesecret.url;

import com.dhy.coffeesecret.url.base.UrlBase;

/**
 * Created by mxf on 2017/3/4.
 */
public final class UrlBean extends UrlBase {

    private static String root = "/bean";

    public static String getAll(String token){
        return new StringBuffer(URL_COMMON).append(root)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("all")
                .toString();
    }

    public final static String add(String token){
        return new StringBuffer(URL_COMMON).append(root)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("add")
                .toString();
    }

    public final static String delete(String token,long id){
        return new StringBuffer(URL_COMMON).append(root)
                .append(URL_SEPARATOR).append(id)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("delete")
                .toString();
    }

    public final static String update(String token){
        return new StringBuffer(URL_COMMON).append(root)
                .append(URL_SEPARATOR).append(token)
                .append(URL_SEPARATOR).append("update")
                .toString();
    }
}
