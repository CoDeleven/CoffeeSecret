package com.dhy.coffeesecret.ui.device.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-3-7.
 */

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private Map<String, TextView> rssiTextView = new HashMap<>();
    private Map<String, Integer> rssiMac = new HashMap<>();
    private OnItemClickListener onItemClickListener;
    private static BluetoothListAdapter adapter = null;
    private BluetoothDevice lastConnectedDevice = null;

    public BluetoothListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BluetoothViewHolder(layoutInflater.inflate(R.layout.bluetooth_item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(final BluetoothViewHolder holder, int position) {
        final BluetoothDevice device = devices.get(position);
        holder.machine.setText(device.getName());
        rssiTextView.put(device.getAddress(), holder.rssi);
        int rssiValue = rssiMac.get(device.getAddress());
        if (rssiValue != 1) {
            holder.rssi.setText(rssiValue + "");
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(device, holder.layout);
                Log.e("codelevex", "已点击");
            }
        });
    }

    public void setRssi(String macAddr, int rssi) {
        rssiMac.put(macAddr, rssi);
    }

    public void addDevice(BluetoothDevice device) {
        devices.add(device);
    }

    public interface OnItemClickListener{
        void onItemClick(BluetoothDevice device, View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setLastConnectedDevice(BluetoothDevice bluetoothDevice){
        lastConnectedDevice = bluetoothDevice;
    }

    public void clearDevices(){
        devices.clear();
        rssiMac.clear();
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder {
        private TextView rssi;
        private TextView machine;
        private View layout;
        public BluetoothViewHolder(View itemView) {
            super(itemView);
            this.layout = itemView;
            machine = (TextView) itemView.findViewById(R.id.text_name);
            rssi = (TextView) itemView.findViewById(R.id.id_device_rssi);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static BluetoothListAdapter getAdapterInstance(Context context){
        if(adapter == null){
            adapter = new BluetoothListAdapter(context);
            return adapter;
        }
        return adapter;
    }

}
