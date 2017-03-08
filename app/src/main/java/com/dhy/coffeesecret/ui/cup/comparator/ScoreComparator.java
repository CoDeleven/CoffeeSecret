package com.dhy.coffeesecret.ui.cup.comparator;

import com.dhy.coffeesecret.pojo.CuppingInfo;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by mxf on 2017/3/8.
 */
public class ScoreComparator extends BaseComparator {

    public ScoreComparator() {
        this(OrderBy.DESC);
    }

    public ScoreComparator(OrderBy orderBy) {
        super(orderBy);
    }

    @Override
    public int compare(CuppingInfo info, CuppingInfo t) {
        float score = info.getScore();
        float tScore = t.getScore();
        return (int) (tScore*1000-score*1000)*order;
    }
}
