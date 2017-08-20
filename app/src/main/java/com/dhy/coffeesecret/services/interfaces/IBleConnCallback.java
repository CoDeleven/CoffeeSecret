package com.dhy.coffeesecret.services.interfaces;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleConnCallback {
    /**
     * 开始准备连接
     */
    void toPreConnect();

    /**
     * 去连接
     */
    void toConnecting();

    /**
     * 连接完成
     */
    void toConnected();

    /**
     * 断开连接
     */
    void toDisconnected();

    /**
     * 正在断开连接
     */
    void toDisconnecting();

    /**
     * 蓝牙不可用
     */
    void toDisable();
}
