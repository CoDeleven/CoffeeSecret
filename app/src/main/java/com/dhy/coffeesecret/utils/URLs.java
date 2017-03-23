package com.dhy.coffeesecret.utils;

/**
 * Created by mxf on 2017/3/4.
 */
public final class URLs {
   private static final String URL_COMMON = "http://182.254.137.212:8080/CoffeeSecret";
//    private static final String URL_COMMON = "http://192.168.191.1:8080/CoffeeSecret";
//     private static final String URL_COMMON = "http://139.196.90.97:1234/CoffeeSecret";
    // private static final String URL_COMMON = "http://10.151.29.58:8080/CoffeeSecret";

    public static final String ADD_CUPPING = URL_COMMON + "/cupping/add";
    public static final String UPDATE_CUPPING = URL_COMMON + "/cupping/update";
    public static final String GET_ALL_CUPPING = URL_COMMON + "/cupping/all";

    public static final String ADD_BEAN_INFO = URL_COMMON + "/bean/add";
    public static final String GET_ALL_BEAN_INFO = URL_COMMON + "/bean/all";
    public static final String UPDATE_BEAN_INFO = URL_COMMON + "/bean/update";

    public static final String GET_ALL_BAKE_REPORT = URL_COMMON + "/bake/all";
    public static final String ADD_BAKE_REPORT = URL_COMMON + "/bake/add";
    public static final String UPDATE_BAKE_REPORT = URL_COMMON + "/bake/update";

    private static final String DELETE_BAKE_REPORT = "/bake/";
    private static final String DELETE_BEAN_INFO = "/bean/";
    private static final String DELETE_CUPPING = "/cupping/";
    private static final String DELETE = "/delete";

    public static final String GET_LIVE_PUBLISH_PATH = URL_COMMON + "/live/brodcast";
    public static final String GET_LIVE_PLAY_PATH = URL_COMMON + "/live/getAll";

    public static String getDeleteBeanInfo(long id) {
        return URL_COMMON + DELETE_BEAN_INFO + id + DELETE;
    }

    public static String getDeleteBakeReport(long id) {
        return URL_COMMON + DELETE_BAKE_REPORT + id + DELETE;
    }

    public static String getDeleteCupping(long id) {
        return URL_COMMON + DELETE_CUPPING + id + DELETE;
    }
}
