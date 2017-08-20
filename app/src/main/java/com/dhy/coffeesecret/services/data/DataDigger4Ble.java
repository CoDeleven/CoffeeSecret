package com.dhy.coffeesecret.services.data;

import com.dhy.coffeesecret.services.BluetoothService;

/**
 * Created by CoDeleven on 17-8-19.
 */

public abstract class DataDigger4Ble {
    abstract void doWrite(IBleWROperator mOperator);

    /**
     * Created by CoDeleven on 17-8-19.
     * 用于读写的接口类，用于NewBleService
     */

    public static interface IBleWROperator {
        void writeData2Device(String command);

    }
}
class SwitchChannelToOne extends DataDigger4Ble{

    @Override
    void doWrite(IBleWROperator mOperator) {
        mOperator.writeData2Device(BluetoothService.FIRST_CHANNEL);
    }
}
class SwitchChannelToTwo extends DataDigger4Ble{

    @Override
    void doWrite(IBleWROperator mOperator) {
        mOperator.writeData2Device(BluetoothService.SECOND_CHANNEL);
    }
}
class ReadDataFromChannelOne extends DataDigger4Ble{

    @Override
    void doWrite(IBleWROperator mOperator) {
        mOperator.writeData2Device(BluetoothService.READ_TEMP_COMMAND);
    }
}
class ReadDataFromChannelTwo extends DataDigger4Ble{

    @Override
    void doWrite(IBleWROperator mOperator) {
        mOperator.writeData2Device(BluetoothService.READ_TEMP_COMMAND);
    }
}