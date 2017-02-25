package com.dhy.coffeesecret.utils;

import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.google.gson.Gson;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class ObjectJsonConvert {
    public static String bakereport2Json(BakeReportImm imm) {
/*        BakeReportImm imm = new BakeReportImm();
        imm.setBaker("gg");
        imm.setCookedBeanWeight(320);
        imm.setDevelopRate(0.3f);
        imm.setDevice("lala");
        imm.setDevelopTime(300);
        imm.setStartTemp(160);
        imm.setEndTemp(230);
        imm.setEnvTemp(26);
        Map<Integer, Long> maps = new HashMap<Integer, Long>();
        maps.put(1, 360L);
        maps.put(2, 720L);
        imm.setRawBeanWeight(maps);
        LineData line = new LineData();
        for (int i = 0; i < 5; ++i) {
            line.addDataSet(new LineDataSet(Arrays.asList(new Entry(1, 1)), "haha:" + i));
        }
        imm.setTempratures(line);*/

        String jsonStr = new Gson().toJson(imm);
        return jsonStr;
    }

}
