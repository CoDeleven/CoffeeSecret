package com.dhy.coffeesecret.model.base;

import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;

/**
 * Created by CoDeleven on 17-8-1.
 */

interface IBaseModel {
    /**
     * 用于清除已经结束的烘焙报告
     */
    void clearBakeReport();

    /**
     * 获取当前正在烘焙的报告
     * @return 正在烘焙的报告
     */
    BakeReportProxy getCurBakingReport();

    /**
     * 初始化烘焙报告
     * 如果curBakingReport不为空，则无法正常初始化
     * 请确保curBakingReport为空
     */
    void initBakeReport();

    UniversalConfiguration getAppConfig();

    void updateAppConfig(UniversalConfiguration appConfig);
}
