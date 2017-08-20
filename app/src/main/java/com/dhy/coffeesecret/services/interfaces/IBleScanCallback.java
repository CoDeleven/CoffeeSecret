package com.dhy.coffeesecret.services.interfaces;

import com.clj.fastble.data.ScanResult;


/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleScanCallback {
    void onScanning(ScanResult result);
    void onScanningComplete(ScanResult... results);
}
