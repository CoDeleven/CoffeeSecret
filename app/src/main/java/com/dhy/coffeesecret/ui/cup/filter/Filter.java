package com.dhy.coffeesecret.ui.cup.filter;

import com.dhy.coffeesecret.pojo.CuppingInfo;

import java.util.Date;

/**
 * Created by mxf on 2017/3/22.
 * 过滤器 用于筛选
 */
public class Filter {
    public final static int ALL = 0;
    public final static int NEARLY_3_DAY = 1;
    public final static int NEARLY_1_MONTH = 2;
    public final static int NEARLY_3_MONTH = 3;
    public final static int NEARLY_1_YEAR = 4;
    public final static int MORE = 5;
    public final static long DAY = 24 * 60;

    public int min = 0;
    public int max = 100;
    public int dateSelection;

    public boolean doFilter(CuppingInfo info) {

        float score = info.getScore();

        if (score >= min && score <= max) {
            long time = info.getDate().getTime() / (1000 * 60);
            long today = new Date().getTime() / (1000 * 60);
            long timeCount = today - time;
            switch (dateSelection) {
                case NEARLY_3_DAY:
                    return timeCount <= 3 * DAY;
                case NEARLY_1_MONTH:
                    return timeCount <= 30 * DAY;
                case NEARLY_3_MONTH:
                    return timeCount <= (90 * DAY);
                case NEARLY_1_YEAR:
                    return timeCount <= 365 * DAY;
                case MORE:
                    return timeCount > 365 * DAY;
                case ALL:
                default:
                    return true;
            }
        }
        return false;
    }
}

