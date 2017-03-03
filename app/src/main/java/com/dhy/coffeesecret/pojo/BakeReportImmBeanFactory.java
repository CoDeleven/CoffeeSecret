/*
package com.dhy.coffeesecret.pojo;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.dhy.coffeesecret.pojo.BakeReportImm.EntryPojo;
import java.util.List;

*/
/**
 * Created by CoDeleven on 17-2-26.
 *//*


public class BakeReportImmBeanFactory {
    private static BakeReportImm bakeReportImm;

    public static BakeReportImm getBakeReportImm() {
        if (bakeReportImm == null) {
            bakeReportImm = new BakeReportImm();
        }
        return bakeReportImm;
    }

    public static BakeReportImm getBakeReportImm(LineData lineData) {
        if (bakeReportImm == null) {
            bakeReportImm = new BakeReportImm();
        }
        setLineDate2BakeReport(lineData);
        return bakeReportImm;
    }

    public static void setLineDate2BakeReport(LineData lineData) {
        bakeReportImm.setLineData(lineData);
        for (ILineDataSet dataSet : lineData.getDataSets()) {
            DataSet<Entry> temp = (DataSet<Entry>) dataSet;
            switch (dataSet.getLabel()) {
                case "豆温":
                    List<EntryPojo> beanTemps = bakeReportImm.getBeanTemps();
                    List<Float> timex = bakeReportImm.getTimex();
                    for (Entry entry : temp.getValues()) {

                        beanTemps.add(bakeReportImm.new EntryPojo(entry));
                        timex.add(entry.getX());
                    }
                    break;
                case "豆升温":
                    List<Float> accBeanTemps = bakeReportImm.getAccBeanTemps();
                    for (Entry entry : temp.getValues()) {
                        accBeanTemps.add(entry.getY());
                    }
                    break;
                case "进风温":
                    List<Float> inwindTemps = bakeReportImm.getInwindTemps();
                    for (Entry entry : temp.getValues()) {
                        inwindTemps.add(entry.getY());
                    }
                    break;
                case "进风升温":
                    List<Float> accInwindTemps = bakeReportImm.getAccInwindTemps();
                    for (Entry entry : temp.getValues()) {
                        accInwindTemps.add(entry.getY());
                    }
                    break;
                case "出风温":
                    List<Float> outwindTemps = bakeReportImm.getOutwindTemps();
                    for (Entry entry : temp.getValues()) {
                        outwindTemps.add(entry.getY());
                    }
                    break;
                case "出风升温":
                    List<Float> accOutwindTemps = bakeReportImm.getAccOutwindTemps();
                    for (Entry entry : temp.getValues()) {
                        accOutwindTemps.add(entry.getY());
                    }
                    break;

            }
        }

    }
}
*/
