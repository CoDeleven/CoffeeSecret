package com.dhy.coffeesecret.ui.cup.comparator;

/**
 * Created by mxf on 2017/3/8.
 */
public enum OrderBy {
    ASC(1),
    DESC(-1);

    private int value;


    private OrderBy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
