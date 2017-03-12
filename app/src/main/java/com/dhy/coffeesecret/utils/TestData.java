package com.dhy.coffeesecret.utils;

import android.content.Context;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/15.
 */

public class TestData {

    public static String[] beanList1 = {"All", "Asia", "Asia", "Africa", "Baby", "Central American", "Death", "Destroy"
            , "E", "Fate", "Great", "Grand", "Handsome", "I", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker"
            , "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker"
            , "Joker", "Joker", "King", "Luna", "Morning", "North American"
            , "Oceania", "Other", "Person", "Queen", "Read", "Real", "Strange", "Trouble"};
    public static String[] beanList2 = {"All", "Asia", "Asia", "Africa", "Baby", "Central American", "Death", "Destroy"
            , "E", "Fate", "Great", "Grand", "Handsome", "I", "Joker", "Other", "Person", "Queen", "Read", "Real", "Strange", "Trouble"};
    public static String[] beanList3 = {"All", "Asia", "Asia", "Africa", "Baby", "Central American", "Death", "Destroy"
            , "E", "Fate", "Great", "Grand", "Handsome", "I", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker"
            , "Joker", "Joker", "King", "Luna", "Morning", "North American"
            , "Oceania", "Other", "Person", "Queen", "Read", "Real"};
    public static String[] beanList4 = {"Arabica", "Robusta", "Liberica"};
    public static String[] beanList5 = {"Java", "Dilla", "Deiga", "Dalle", "Alghe", "Agaro", "Gesha", "Rume  Sudan", "Kaffa", "Ennarea", "Gimma", "Tafari-kela", "Ranbung", "S.4"};
    public static String[] beanList6 = {"Catiomors", "Tupi", "Tupi", "Obata", "Catisic", "Lempira", "IAPAR59", "Colombia", "Sarchimor"};
    public static String[] beanList7 = {"S288","ST95"};
    public static String[] countryList1 = {"全部", "墨西哥", "牙买加", "古巴", "海地", "多明尼加", "波多黎各", "多明尼克", "格瑞纳达", "贝里斯"
            , "哥斯达黎加", "危地马拉", "宏都拉斯", "尼加拉瓜", "巴拿马", "萨尔瓦多", "巴西", "玻利维亚", "哥伦比亚", "厄瓜多尔", "秘鲁"
            , "盖亚那", "夏威夷", "斐济", "巴布亚新几内亚", "澳大利亚", "中国", "菲律宾", "印度", "泰国", "越南", "印尼", "刚果", "象牙海岸"
            , "安哥拉", "马达加斯加", "肯尼亚", "尚比亚", "美国", "英国", "日本", "荷兰"};
    public static String[] countryList2 = {"全部", "墨西哥", "牙买加", "古巴", "海地", "多明尼加", "波多黎各", "多明尼克", "格瑞纳达", "贝里斯"
            , "哥斯达黎加", "危地马拉", "宏都拉斯", "尼加拉瓜", "巴拿马", "萨尔瓦多"};
    public static String[] countryList3 = {"全部", "巴西", "玻利维亚", "哥伦比亚", "厄瓜多尔", "秘鲁", "盖亚那"};
    public static String[] countryList4 = {"全部", "夏威夷", "斐济", "巴布亚新几内亚", "澳大利亚"};
    public static String[] countryList5 = {"全部", "中国", "菲律宾", "印度", "泰国", "越南", "印尼"};
    public static String[] countryList6 = {"全部", "刚果", "象牙海岸", "安哥拉", "马达加斯加", "肯尼亚", "尚比亚"};
    public static String[] countryList7 = {"全部", "美国", "英国", "日本", "荷兰"};

    public static String quickEvents = "[\"咖啡豆开始变黄\",\"咖啡豆颜色变深\",\"爆豆声变得强烈\",\"咖啡豆成爆米花了\",\"咖啡豆变成碳了\",\"烘咖啡机发出了强烈的悲鸣\",\"咖啡店发生了爆炸，上了今日头条\"]";
//    public static Integer[] linesColor = {Color.parseColor("#91A7FF"), Color.parseColor("#42BD41"), Color.parseColor("#F36C60"),
//            Color.parseColor("#FFF176"), Color.parseColor("#BA68C8"), Color.parseColor("#FFB74D"), Color.parseColor("#A1887F"),
//            Color.parseColor("#D500F9"), Color.parseColor("#00BCD4"), Color.parseColor("#757575"), Color.parseColor("#DCE775")};

    public static String beaninfos =
            "[{\"id\":0,\"name\":\"琥珀冰晶\",\"manor\":\"Finca EL Carmen卡门庄园\",\"drawablePath\":\"" + R.drawable.ic_container_ac + "\",\"stockWeight\":10.0,\"country\":\"尼加拉瓜\",\"area\":\"圣胡安\",\"altitude\":\"1550\",\"species\":\"Heirloom\",\"level\":\"AA\",\"process\":\"日晒\",\"waterContent\":0.12,\"supplier\":\"Global Vision Trading S.A\",\"price\":98,\"date\":\"Mar 8, 2017 5:00:36 PM\",\"continent\":\"中美\"}" +
            ",{\"id\":1,\"name\":\"爱果蜜\",\"manor\":\"EL Naranjo Dipilto庄园\",\"drawablePath\":\"" + R.drawable.ic_container_ae + "\",\"stockWeight\":2.2,\"country\":\"尼加拉瓜\",\"area\":\"Dipilto\",\"altitude\":\"1600\",\"species\":\"Maragogype\",\"level\":\"AA\",\"process\":\"蜜处理\",\"waterContent\":0.01,\"supplier\":\"Global Vision Trading S.A\",\"price\":218,\"date\":\"Mar 9, 2017 5:33:36 AM\",\"continent\":\"中美\"}" +
            ",{\"id\":2,\"name\":\"巨型象豆\",\"manor\":\"橙果庄园\",\"drawablePath\":\"" + R.drawable.ic_container_ae + "\",\"stockWeight\":2.0,\"country\":\"尼加拉瓜\",\"area\":\"Dipilto\",\"altitude\":\"1600\",\"species\":\"Maragogype\",\"level\":\"SHB\",\"process\":\"日晒\",\"waterContent\":0.04,\"supplier\":\"Aroma\",\"price\":246.0,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"中美\"}" +
            ",{\"id\":3,\"name\":\"Kenya\",\"manor\":\"ASINI\",\"drawablePath\":\"" + R.drawable.ic_container_aa + "\",\"stockWeight\":2.3,\"country\":\"肯尼亚\",\"area\":\"Neyri\",\"altitude\":\"1700~1800\",\"species\":\"Maragogype\",\"level\":\"AB\",\"process\":\"水洗\",\"waterContent\":0.13,\"supplier\":\"杭州北啡食品有限公司\",\"price\":89,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"非洲\"}" +
            ",{\"id\":4,\"name\":\"Caturra\",\"manor\":\"茵赫特庄园\",\"drawablePath\":\"" + R.drawable.ic_container_al + "\",\"stockWeight\":4.0,\"country\":\"危地马拉\",\"area\":\"微微特南果高原\",\"altitude\":\"1004~1040\",\"species\":\"Maragogype\",\"level\":\"Grade1\",\"process\":\"蜜处理\",\"waterContent\":0.04,\"supplier\":\"晨宇咖啡\",\"price\":492.0,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"亚洲\"}" +
            ",{\"id\":5,\"name\":\"巴西黄波旁\",\"manor\":\"Chacal\",\"drawablePath\":\"" + R.drawable.ic_container_aa + "\",\"stockWeight\":5.0,\"country\":\"巴西\",\"area\":\"米纳斯\",\"altitude\":\"1300~1500\",\"species\":\"黄波旁\",\"level\":\"AA\",\"process\":\"半日晒\",\"waterContent\":0.22,\"supplier\":\"明谦咖啡\",\"price\":88.5,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"南美\"}" +
            ",{\"id\":6,\"name\":\"爱果蜜\",\"manor\":\"赫摩莎庄园\",\"drawablePath\":\"" + R.drawable.ic_container_ae + "\",\"stockWeight\":6.0,\"country\":\"危地马拉\",\"area\":\"胡蒂亚帕\",\"altitude\":\"1006~1060\",\"species\":\"Borubon\",\"level\":\"SHB\",\"process\":\"半水洗\",\"waterContent\":0.06,\"supplier\":\"保山市隆阳区天力咖啡厂\",\"price\":78.0,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"中美\"}" +
            ",{\"id\":7,\"name\":\"巨型象豆\",\"manor\":\"蒂维萨庄园\",\"drawablePath\":\"" + R.drawable.ic_container_aa + "\",\"stockWeight\":7.0,\"country\":\"哥伦比亚\",\"area\":\"安蒂奥基亚\",\"altitude\":\"1007~1070\",\"species\":\"Caturra\",\"level\":\"AA\",\"process\":\"半日晒\",\"waterContent\":0.07,\"supplier\":\"质馆咖啡\",\"price\":81.0,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"南美\"}" +
            ",{\"id\":8,\"name\":\"爱果蜜\",\"manor\":\"春天庄园\",\"drawablePath\":\"" + R.drawable.ic_container_al + "\",\"stockWeight\":8.0,\"country\":\"哥伦比亚\",\"area\":\"安蒂奥基亚\",\"altitude\":\"1008~1080\",\"species\":\"Kona\",\"level\":\"B\",\"process\":\"日晒\",\"waterContent\":0.08,\"supplier\":\"晨宇咖啡\",\"price\":984.0,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"南美\"}" +
            ",{\"id\":9,\"name\":\"Caturra\",\"manor\":\"珍珠庄园\",\"drawablePath\":\"" + R.drawable.ic_container_ac + "\",\"stockWeight\":9.0,\"country\":\"中国\",\"area\":\"云南\",\"altitude\":\"1009~1090\",\"species\":\"Typica\",\"level\":\"GHB\",\"process\":\"蜜处理\",\"waterContent\":0.09,\"supplier\":\"保山市新寨咖啡有限公司\",\"price\":42.0,\"date\":\"Mar 8, 2017 5:33:36 PM\",\"continent\":\"亚洲\"}]";

    public static Integer[] linesColor = {R.color.simo_blue300, R.color.simo_green300, R.color.simo_red300, R.color.simo_yellow300,
            R.color.simo_purple700, R.color.simo_pink700, R.color.simo_limea400, R.color.simo_orange300,
            R.color.simo_brown300, R.color.simo_grey500, R.color.simo_lightgreena400, R.color.simo_reda700};



    public static String cuppingInfos =
            "[{\"id\":1,\"name\":\"我的第一次杯测\",\"score\":9.0,\"feel\":9.0,\"flaw\":8.0,\"dryAndFragrant\":8.0,\"flavor\":9.0,\"afterTaste\":7.5,\"acidity\":7.5,\"taste\":8,\"sweetness\":8.0,\"balance\":7.0,\"overall\":8.0,\"underdevelopment\":1,\"overdevelopment\":1,\"baked\":1,\"scorched\":1,\"tipped\":2,\"faced\":1,\"date\":\"2017-03-08\",\"bakeReport\":null}," +
//                    "{\"id\":2,\"name\":\"盖友山 G1\",\"score\":9.0,\"feel\":9.0,\"flaw\":8.0,\"dryAndFragrant\":8.0,\"flavor\":9.0,\"afterTaste\":8.0,\"acidity\":8.0,\"taste\":8.0,\"sweetness\":8.0,\"balance\":8.0,\"overall\":8.0,\"underdevelopment\":2,\"overdevelopment\":2,\"baked\":1,\"scorched\":2,\"tipped\":1,\"faced\":1,\"date\":\"2017-03-09\",\"bakeReport\":null}," +
//                    "{\"id\":3,\"name\":\"云南咖啡\",\"score\":9.0,\"feel\":9.0,\"flaw\":8.0,\"dryAndFragrant\":8.0,\"flavor\":9.0,\"afterTaste\":8.0,\"acidity\":8.0,\"taste\":8.0,\"sweetness\":8.0,\"balance\":8.0,\"overall\":8.0,\"underdevelopment\":,\"overdevelopment\":1,\"baked\":1,\"scorched\":1,\"tipped\":2,\"faced\":2,\"date\":\"2017-03-11\",\"bakeReport\":null}," +
//                    "{\"id\":4,\"name\":\"陈年黄金曼特宁\",\"score\":9.0,\"feel\":9.0,\"flaw\":8.0,\"dryAndFragrant\":8.0,\"flavor\":9.0,\"afterTaste\":8.0,\"acidity\":8.0,\"taste\":8.0,\"sweetness\":8.0,\"balance\":8.0,\"overall\":8.0,\"underdevelopment\":2,\"overdevelopment\":1,\"baked\":1,\"scorched\":1,\"tipped\":3,\"faced\":1,\"date\":\"2017-03-11\",\"bakeReport\":null}," +
//                    "{\"id\":5,\"name\":\"曼特宁 G1\",\"date\":\"2017-03-12\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"108.0\",\"waterContent\":\"8.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 8.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":2,\"balance\":7.0,\"afterTaste\":6.0,\"dryAndFragrant\":8.0,\"faced\":0,\"feel\":7.0,\"flavor\":6.0,\"flaw\":8.0,\"id\":0,\"acidity\":9.0,\"overall\":8.0,\"overdevelopment\":1,\"scorched\":1,\"score\":8.0,\"sweetness\":6.0,\"taste\":8.0,\"tipped\":2,\"underdevelopment\":2}," +
//                    "{\"id\":6,\"name\":\"牙买加蓝山\",\"date\":\"2017-03-13\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"7.5\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":8.0,\"faced\":0,\"feel\":8.0,\"flavor\":8.0,\"flaw\":8.0,\"id\":0,\"acidity\":8.0,\"overall\":8.0,\"overdevelopment\":2,\"scorched\":1,\"score\":8.0,\"sweetness\":8.0,\"taste\":8.0,\"tipped\":1,\"underdevelopment\":1}," +
//                    "{\"id\":7,\"name\":\"巴西山多士\",\"date\":\"2017-03-15\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"6.25\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 7.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":0.0,\"dryAndFragrant\":0.0,\"faced\":0,\"feel\":8.0,\"flavor\":8.0,\"flaw\":8.0,\"id\":0,\"acidity\":8.0,\"overall\":8.0,\"overdevelopment\":1,\"scorched\":1,\"score\":8.0,\"sweetness\":8.0,\"taste\":8.0,\"tipped\":2,\"underdevelopment\":3}," +
                    "{\"id\":8,\"name\":\"博尔坎巴鲁\",\"date\":\"2017-03-15\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"0.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":8.0,\"faced\":0,\"feel\":9.0,\"flavor\":5.0,\"flaw\":8.0,\"id\":0,\"acidity\":8.0,\"overall\":5.0,\"overdevelopment\":2,\"scorched\":1,\"score\":8.0,\"sweetness\":8.0,\"taste\":8.0,\"tipped\":1,\"underdevelopment\":1}," +
                    "{\"id\":9,\"name\":\"乌干达\",\"date\":\"2017-03-15\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"0.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.5,\"dryAndFragrant\":8.5,\"faced\":0,\"feel\":8.5,\"flavor\":8.5,\"flaw\":8.5,\"id\":0,\"acidity\":8.5,\"overall\":8.5,\"overdevelopment\":10,\"scorched\":10,\"score\":8.5,\"sweetness\":8.5,\"taste\":8.5,\"tipped\":1,\"underdevelopment\":3}," +
//                    "{\"id\":11,\"name\":\"摩卡\",\"date\":\"2017-03-17\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"8.5\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.5,\"dryAndFragrant\":8.5,\"faced\":0,\"feel\":8.5,\"flavor\":8.5,\"flaw\":8.5,\"id\":0,\"acidity\":8.5,\"overall\":8.5,\"overdevelopment\":1,\"scorched\"2,\"score\":8.5,\"sweetness\":8.5,\"taste\":8.5,\"tipped\":2,\"underdevelopment\":2}," +
                    "{\"id\":12,\"name\":\"山多士\",\"date\":\"2017-03-17\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"8.5\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 8.50%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":2,\"balance\":7.0,\"afterTaste\":8.5,\"dryAndFragrant\":8.5,\"faced\":1,\"feel\":8.5,\"flavor\":5.5,\"flaw\":8.5,\"id\":0,\"acidity\":8.5,\"overall\":8.5,\"overdevelopment\":1,\"scorched\":1,\"score\":8.5,\"sweetness\":8.5,\"taste\":8.5,\"tipped\":1,\"underdevelopment\":1}," +
                    "{\"id\":13,\"name\":\"鲁哇克\",\"date\":\"2017-03-17\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"0.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":5.0,\"afterTaste\":7.0,\"dryAndFragrant\":7.5,\"faced\":0,\"feel\":6.5,\"flavor\":7.5,\"flaw\":7.5,\"id\":0,\"acidity\":5,\"overall\":7.5,\"overdevelopment\":1,\"scorched\":1,\"score\":0.0,\"sweetness\":6.5,\"taste\":5,\"tipped\":2,\"underdevelopment\":1}," +
                    "{\"id\":14,\"name\":\"茵赫特\",\"date\":\"2017-03-17\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"0.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":2,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":7.0,\"faced\":0,\"feel\":6.5,\"flavor\":7.5,\"flaw\":7.5,\"id\":0,\"acidity\":7.5,\"overall\":7.5,\"overdevelopment\":1,\"scorched\":2,\"score\":0.0,\"sweetness\":6.5,\"taste\":6.5,\"tipped\":1,\"underdevelopment\":1}," +
                    "{\"id\":15,\"name\":\"哈拉尔 G4\",\"date\":\"2017-03-18\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"7.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":7.5,\"faced\":0,\"feel\":6.5,\"flavor\":7.5,\"flaw\":7.5,\"id\":0,\"acidity\":7.5,\"overall\":5.5,\"overdevelopment\":1,\"scorched\":1,\"score\":0.0,\"sweetness\":6.5,\"taste\":6.5,\"tipped\":1,\"underdevelopment\":2}," +
                    "{\"id\":16,\"name\":\"肯尼亚 基布 AA\",\"date\":\"2017-03-18\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"7.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":7.5,\"faced\":0,\"feel\":6.50,\"flavor\":7.5,\"flaw\":7.5,\"id\":0,\"acidity\":7.5,\"overall\":7.5,\"overdevelopment\":1,\"scorched\":2,\"score\":0.0,\"sweetness\":6.5,\"taste\":6.5,\"tipped\":2,\"underdevelopment\":1}," +
                    "{\"id\":17,\"name\":\"耶加雪菲 G2\",\"date\":\"2017-03-19\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"7.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":7.5,\"faced\":0,\"feel\":6.5,\"flavor\":7.5,\"flaw\":5.5,\"id\":0,\"acidity\":7.5,\"overall\":5.5,\"overdevelopment\":2,\"scorched\":1,\"score\":0.0,\"sweetness\":6.5,\"taste\":6.5,\"tipped\":1,\"underdevelopment\":2}," +
                    "{\"id\":18,\"name\":\"西达摩\",\"score\":9.0,\"feel\":9.0,\"flaw\":7.0,\"dryAndFragrant\":7.0,\"flavor\":9.0,\"afterTaste\":7.0,\"acidity\":7.0,\"taste\":7.0,\"sweetness\":7.0,\"balance\":7.0,\"overall\":7.0,\"underdevelopment\":2,\"overdevelopment\":1,\"baked\":1,\"scorched\":1,\"tipped\":1,\"faced\":2,\"date\":\"2017-03-11\",\"bakeReport\":null}," +
                    "{\"id\":19,\"name\":\"巴西黄波旁\",\"date\":\"2017-03-120\",\"bakeReport\":{\"ambientTemperature\":\"21.57\",\"beanInfoSimples\":[{\"beanName\":\"样品豆\",\"usage\":\"100.0\",\"waterContent\":\"7.0\"}],\"cookedBeanWeight\":\"123.0\",\"date\":\"2017-03-11 07:56\",\"developmentRate\":\"发展率: 0.00%\",\"developmentTime\":\"00:00\",\"device\":\"HB-coffee\",\"endTemperature\":\"21.52\",\"tempratureSet\":{\"accBeanTemps\":[7.2,-7.2,3.6,0.0,-3.0,0.0,0.0,3.6],\"accInwindTemps\":[7.2,-7.2,3.6,0.0,0.0,3.0,0.0,3.6],\"accOutwindTemps\":[7.2,-7.2,3.6,0.0,0.0,0.0,0.0,3.6],\"beanTemps\":[21.58,21.46,21.52,21.52,21.47,21.47,21.47,21.53],\"events\":{\"3.95\":\"结束:4\"},\"inwindTemps\":[21.53,21.41,21.47,21.47,21.47,21.52,21.52,21.58],\"outwindTemps\":[21.38,21.26,21.32,21.32,21.32,21.32,21.32,21.38],\"timex\":[1.01,1.97,2.95,3.95,5.01,6.03,6.99,7.97]},\"roastDegree\":\"28.0\",\"startTemperature\":\"21.52\",\"id\":13},\"baked\":1,\"balance\":7.0,\"afterTaste\":8.0,\"dryAndFragrant\":7.5,\"faced\":0,\"feel\":6.5,\"flavor\":5.5,\"flaw\":7.5,\"id\":0,\"acidity\":7.5,\"overall\":5,\"overdevelopment\":1,\"scorched\":2,\"score\":0.0,\"sweetness\":6.5,\"taste\":6.5,\"tipped\":1,\"underdevelopment\":1}," +
                    "{\"id\":20,\"name\":\"巴西黄波旁（2）\",\"score\":9.0,\"feel\":9.0,\"flaw\":7.0,\"dryAndFragrant\":7.0,\"flavor\":9.0,\"afterTaste\":7.0,\"acidity\":7.0,\"taste\":7.0,\"sweetness\":7.0,\"balance\":7.0,\"overall\":7.0,\"underdevelopment\":2,\"overdevelopment\":1,\"baked\":2,\"scorched\":2,\"tipped\":1,\"faced\":0,\"date\":\"2017-03-11\",\"bakeReport\":null}" + "]";

    /**
     * 获取豆子的处理方式
     * @return 处理方式的列表
     */
    public static ArrayList<String> getHandlerList() {
        ArrayList<String> handlerList = new ArrayList<>();

        handlerList.add("全部");
        handlerList.add("蜜处理");
        handlerList.add("日晒");
        handlerList.add("半日晒");
        handlerList.add("水洗");
        handlerList.add("半水洗");
        handlerList.add("其它");

        return handlerList;
    }

    public static final int[] RAW_ID = {R.raw.br_0, R.raw.br_1, R.raw.br_2, R.raw.br_3, R.raw.br_4, R.raw.br_5, R.raw.br_6, R.raw.br_7, R.raw.br_8, };
    public static Gson gson = new Gson();
    /**
     * 获取本地BakeReport
     */
    public static Map<String, BakeReport> getBakeReports(Context context) {
        if(objs == null){
            objs = new HashMap<>();
            for(int id : RAW_ID){
                objs.put(id + "", getBakeReport(context, id));
            }
        }

        File file = new File(context.getCacheDir(), "br_9.json");
        if(file.exists()){
            try {
                FileReader fileReader = new FileReader(file);
                objs.put("br_9.json", gson.fromJson(fileReader, BakeReport.class));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return objs;
    }
    private static Map<String, BakeReport> objs;
    private static BakeReportProxy BAKE_REPORT;

    public static BakeReport getBakeReport(Context context, int id){
        InputStream is = context.getResources().openRawResource(id);
        return gson.fromJson(new InputStreamReader(is), BakeReport.class);
    }

    public static void saveBakeReports(Context context, BakeReport bakeReport) {
        bakeReport.setId(9);
        File file = new File(context.getCacheDir(), "br_9.json");
        Gson gson = new Gson();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(bakeReport));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void setBakeReport(BakeReport bakeReport) {
        BAKE_REPORT = new BakeReportProxy(bakeReport);
    }

    public static BakeReportProxy getBakeReport() {
        return BAKE_REPORT;
    }

    public static void setBakeReport(BakeReportProxy proxy) {
        BAKE_REPORT = proxy;
    }
}
