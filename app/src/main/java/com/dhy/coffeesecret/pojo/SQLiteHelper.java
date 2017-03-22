package com.dhy.coffeesecret.pojo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 必要添加，等待好方法直接导入sql
 * <p>
 * Created by CoDeleven on 17-3-21.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String CREATE_CONTINENT =
            "CREATE TABLE `continent` (" +
            "  `id` int(11) NOT NULL," +
            "  `continent` varchar(255) NOT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ;";
    private static final String CREATE_COUNTRY =
            "CREATE TABLE `countries` (" +
            "  `id` int(11) NOT NULL," +
            "  `country` varchar(255) NOT NULL," +
            "  `child` int(11) DEFAULT NULL," +
            "  `parent` int(11) NOT NULL," +
            "  PRIMARY KEY (`country`)," +
            " FOREIGN KEY (`parent`) REFERENCES `continent` (`id`)" +
            ") ;";
    private static final String INSERT_CONTINENT = "INSERT INTO `continent` VALUES ('1', '亚洲');" +
            "INSERT INTO `continent` VALUES ('2', '非洲');" +
            "INSERT INTO `continent` VALUES ('3', '中美');" +
            "INSERT INTO `continent` VALUES ('4', '南美');" +
            "INSERT INTO `continent` VALUES ('5', '大洋');";

    private static final String INSERT_COUNTRY = "INSERT INTO `countries` VALUES ('3', '中国', null, '1');" +
            "INSERT INTO `countries` VALUES ('6', '刚果', null, '2');" +
            "INSERT INTO `countries` VALUES ('2', '印度 ', null, '1');" +
            "INSERT INTO `countries` VALUES ('22', '危地马拉', null, '3');" +
            "INSERT INTO `countries` VALUES ('30', '厄瓜多尔', null, '4');" +
            "INSERT INTO `countries` VALUES ('14', '古巴', null, '3');" +
            "INSERT INTO `countries` VALUES ('29', '哥伦比亚', null, '4');" +
            "INSERT INTO `countries` VALUES ('21', '哥斯达黎加', null, '3');" +
            "INSERT INTO `countries` VALUES ('12', '墨西哥', null, '3');" +
            "INSERT INTO `countries` VALUES ('33', '夏威夷', null, '5');" +
            "INSERT INTO `countries` VALUES ('18', '多明尼克', null, '3');" +
            "INSERT INTO `countries` VALUES ('16', '多明尼加', null, '3');" +
            "INSERT INTO `countries` VALUES ('8', '安哥拉', null, '2');" +
            "INSERT INTO `countries` VALUES ('23', '宏都拉斯', null, '3');" +
            "INSERT INTO `countries` VALUES ('11', '尚比亚', null, '2');" +
            "INSERT INTO `countries` VALUES ('24', '尼加拉瓜', null, '3');" +
            "INSERT INTO `countries` VALUES ('35', '巴布亚新几内亚', null, '5');" +
            "INSERT INTO `countries` VALUES ('25', '巴拿马', null, '3');" +
            "INSERT INTO `countries` VALUES ('27', '巴西', null, '4');" +
            "INSERT INTO `countries` VALUES ('34', '斐济', null, '5');" +
            "INSERT INTO `countries` VALUES ('19', '格瑞纳达', null, '3');" +
            "INSERT INTO `countries` VALUES ('17', '波多黎各', null, '3');" +
            "INSERT INTO `countries` VALUES ('4', '泰国', null, '1');" +
            "INSERT INTO `countries` VALUES ('15', '海地', null, '3');" +
            "INSERT INTO `countries` VALUES ('36', '澳大利亚', null, '5');" +
            "INSERT INTO `countries` VALUES ('13', '牙买加', null, '3');" +
            "INSERT INTO `countries` VALUES ('28', '玻利维亚', null, '4');" +
            "INSERT INTO `countries` VALUES ('32', '盖亚那', null, '4');" +
            "INSERT INTO `countries` VALUES ('31', '秘鲁', null, '4');" +
            "INSERT INTO `countries` VALUES ('10', '肯尼亚', null, '2');" +
            "INSERT INTO `countries` VALUES ('1', '菲律宾', null, '1');" +
            "INSERT INTO `countries` VALUES ('26', '萨尔瓦多', null, '3');" +
            "INSERT INTO `countries` VALUES ('7', '象牙海岸', null, '2');" +
            "INSERT INTO `countries` VALUES ('20', '贝里斯', null, '3');" +
            "INSERT INTO `countries` VALUES ('5', '越南', null, '1');" +
            "INSERT INTO `countries` VALUES ('9', '马达加斯拉', null, '2');";

    private static final String dbName = "test.db";

    public SQLiteHelper(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTINENT);
        db.execSQL(CREATE_COUNTRY);
        for(String t: INSERT_CONTINENT.split(";")){
            db.execSQL(t);
        }
        for(String t: INSERT_COUNTRY.split(";")){
            db.execSQL(t);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 不会存在更新的情况
    }
}
