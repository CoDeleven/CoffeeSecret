package com.dhy.coffeesecret.model;

/**
 * Created by CoDeleven on 17-8-26.
 */

public enum UniExtraKey {
    EXTRA_BAKE_REPORT("GET_BAKE_REPORT_EXTRAS"), EXTRA_BAKE_REPORT_LIST("GET_LIST_BAKE_REPORT_EXTRAS"),
    EXTRA_BEAN_INFO("GET_BEAN_INFO_EXTRAS"), EXTRA_CUP_INFO("GET_CUP_INFO_EXTRAS"), EXTRA_CUP_INFO_LIST("GET_CUP_INFO_LIST");
    private String key;
    UniExtraKey(String keyStr){
        this.key = keyStr;
    }

    public String getKey() {
        return key;
    }
}
