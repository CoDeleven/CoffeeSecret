package com.dhy.coffeesecret.ui.device.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    public String lastConnectedAddress = null;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private Map<String, ImageView> rssiTextView = new HashMap<>();
    private Map<String, Integer> rssiMac = new HashMap<>();
    private OnItemClickListener onItemClickListener;
    private int curIndex;

    public BluetoothListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        Log.e("BluetoothListAdapter", "获取item" + devices.size());
        return devices.size();
    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BluetoothViewHolder(layoutInflater.inflate(R.layout.bluetooth_item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(final BluetoothViewHolder holder, final int position) {
        final BluetoothDevice device = devices.get(position);
        /*
            因ListActivity重新生成，所以判断上一次因连接成功关闭activity时，所设置地址是否和本次地址一致，true则直接默认该设备tick可见
          */
        if (device.getAddress().equals(lastConnectedAddress)) {
            // 默认设置tick可见
            holder.tick.setVisibility(View.VISIBLE);
            // 重置该属性,防止重复连接时出现勾圈同存情况
            // lastConnectedAddress = null;
        }else{
            // 默认设置tick可见
            holder.tick.setVisibility(View.GONE);
        }
        holder.machine.setText(device.getName());
        rssiTextView.put(device.getAddress(), holder.rssi);
        // 从map中获取rssi的值
        Integer rssiValue = rssiMac.get(device.getAddress());
        // 如果rssiValue不为空，则重新设置rssi的文本
/*        if (rssiValue != null) {
            if(rssiValue < 0 && rssiValue >= -60){
                holder.rssi.setImageResource(R.drawable.ic_bluetooth_stronger_4);
            }else if(rssiValue < -60 && rssiValue >= -70){
                holder.rssi.setImageResource(R.drawable.ic_bluetooth_stronger_3);
            }else if(rssiValue < -70 && rssiValue >= -80){
                holder.rssi.setImageResource(R.drawable.ic_bluetooth_stronger_2);
            }else if(rssiValue < -80 && rssiValue >= -90){
                holder.rssi.setImageResource(R.drawable.ic_bluetooth_stronger_1);
            }else{
                holder.rssi.setImageResource(R.drawable.ic_bluetooth_stronger_0);
            }
        }*/
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curIndex = position;
                onItemClickListener.onItemClick(device, holder.layout);
            }
        });
    }

    public void setRssi(String macAddr, int rssi) {
        rssiMac.put(macAddr, rssi);
    }

    public void addDevice(BluetoothDevice device) {
        devices.add(device);
        Log.e("BluetoothListAdapter", "addDevice:" + device.getAddress());
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void clearDevices() {
        devices.clear();
    }

    /**
     * 移除当前连接着的设备，并设置连接地址为null，
     */
    public void clearConnectedDevice(){
        devices.remove(curIndex);
        lastConnectedAddress = null;
    }
    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice device, View view);
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder {
        private ImageView rssi;
        private TextView machine;
        private View layout;
        private ProgressBar progressCircle;
        private ImageView tick;

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            this.layout = itemView;
            machine = (TextView) itemView.findViewById(R.id.text_name);
            // rssi = (ImageView) itemView.findViewById(R.id.id_device_rssi);
            progressCircle = (ProgressBar) itemView.findViewById(R.id.circle_progress);
            tick = (ImageView) itemView.findViewById(R.id.id_bluetooth_list_right);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
