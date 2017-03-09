package com.dhy.coffeesecret.ui.cup.comparator;

import com.dhy.coffeesecret.pojo.CuppingInfo;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by mxf on 2017/3/8.
 */
public class DateComparator extends BaseComparator {


    public DateComparator() {
        this(OrderBy.DESC);
    }

    public DateComparator(OrderBy orderBy) {
        super(orderBy);
    }


    @Override
    public int compare(CuppingInfo info, CuppingInfo t) {
        Date date = info.getDate();
        Date tDate = t.getDate();
        return order * date.compareTo(tDate);
    }

    public void setOrder(OrderBy order) {
        this.order = order.getValue();
    }
}
