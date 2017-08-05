package com.dhy.coffeesecret.services;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleConnectionCallback {
    /**
     * 开始准备连接
     * @param status
     */
    void toPreConnect(int status);

    /**
     * 去连接
     * @param status
     */
    void toConnecting(int status);

    /**
     * 连接完成
     * @param status
     */
    void toConnected(int status);

    /**
     * 断开连接
     * @param status
     */
    void toDisconnected(int status);

    /**
     * 正在断开连接
     * @param status
     */
    void toDisconnecting(int status);
}
