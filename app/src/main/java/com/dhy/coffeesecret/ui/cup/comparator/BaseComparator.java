package com.dhy.coffeesecret.ui.cup.comparator;

import com.dhy.coffeesecret.pojo.CuppingInfo;

import java.util.Comparator;

/**
 * Created by mxf on 2017/3/8.
 */
public abstract class BaseComparator implements Comparator<CuppingInfo> {
    protected int order = 1;

    public BaseComparator() {
        this(OrderBy.DESC);
    }

    public BaseComparator(OrderBy orderBy) {
        this.order = orderBy.getValue();
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
