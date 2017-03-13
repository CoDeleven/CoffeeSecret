package com.dhy.coffeesecret;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.adapter.BluetoothListAdapter;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.views.DividerDecoration;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstConnectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirstConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstConnectFragment extends Fragment implements BluetoothListAdapter.OnItemClickListener,
        BluetoothService.DeviceChangedListener, BluetoothService.ViewControllerListener {
    public static final int DEVICE_CONNECTING = 0, DEVICE_CONNECT_FAILED = 1, DEVICE_CONNECTED = 2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.id_first_bluetooth_list)
    RecyclerView recyclerView;
    BluetoothService.BluetoothOperator mBluetoothOperator;
    ImageView tick = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private BluetoothListAdapter mAdapter;
    private Map<String, BluetoothDevice> devices = new HashMap<>();
    private ProgressBar progressCircle = null;
    // 连接状态视图的处理器
    private Handler progressViewHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (tick != null && progressCircle != null) {
                switch (msg.what) {
                    case DEVICE_CONNECTING:
                        progressCircle.setVisibility(View.VISIBLE);
                        tick.setVisibility(View.GONE);
                        break;
                    case DEVICE_CONNECT_FAILED:
                        progressCircle.setVisibility(View.INVISIBLE);
                        tick.setVisibility(View.GONE);
                        break;
                    case DEVICE_CONNECTED:
                        progressCircle.setVisibility(View.GONE);
                        tick.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }
    });
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothOperator = (BluetoothService.BluetoothOperator) service;
            mBluetoothOperator.setDeviceChangedListener(FirstConnectFragment.this);
            mBluetoothOperator.setViewControllerListener(FirstConnectFragment.this);
            mBluetoothOperator.startScanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public FirstConnectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstConnectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstConnectFragment newInstance(String param1, String param2) {
        FirstConnectFragment fragment = new FirstConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Intent intent = new Intent(getContext().getApplicationContext(), BluetoothService.class);
        getContext().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_connect, container, false);
        ButterKnife.bind(this, view);
        mAdapter = new BluetoothListAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerDecoration(getContext()));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(BluetoothDevice device, View view) {
        BluetoothDevice hasConnectedDevice = mBluetoothOperator.getBluetoothDevice();
        if (hasConnectedDevice != null && device != null && device.getAddress().equals(hasConnectedDevice.getAddress())) {
            return;
        }
        // 如果之前存在一个设备在连接,设置上一个视图隐藏
        if (progressCircle != null && progressCircle.isShown()) {
            progressCircle.setVisibility(View.GONE);
        }

        // 更新progressCircle和tick两个view的引用
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        // progressCircle.setVisibility(View.VISIBLE);
        tick = (ImageView) view.findViewById(R.id.id_bluetooth_list_right);
        // 当前状态是处于正在连接的状态
        progressViewHandler.sendEmptyMessage(DEVICE_CONNECTING);
        // 开始正式请求连接,因为连接是异步回调，不是及时消息,如果此时false，一定不能连接成功
        if (!mBluetoothOperator.connect(device)) {
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECT_FAILED);
            return;
        }
    }

    @Override
    public void notifyDeviceConnectStatus(boolean isConnected, BluetoothDevice device) {
        if (isConnected) {
            // 设置已连接设备
            mAdapter.lastConnectedAddress = device.getAddress();
            // 设置已经连接状态
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECTED);
            // 保存连接设备地址到配置文件,方便启动时读取并直接连接
            // SettingTool.saveAddress(device.getAddress());

            // 连接成功，结束activity
            getFragmentManager().beginTransaction().replace(R.id.id_test, new ConnectedFragment(), "").commit();

        } else {
            // 确保连接设备为null
            mAdapter.lastConnectedAddress = null;
            // 设置连接失败状态
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECT_FAILED);
        }

    }

    @Override
    public void notifyNewDevice(BluetoothDevice device, int rssi) {
        Log.e("codelevex", "发现新设备：" + device.getAddress());
        // 如果可连接设备里包含里新设备，则只更新rssi,而不添加至adapter
        if (!devices.containsKey(device.getAddress())) {
            mAdapter.addDevice(device);
            devices.put(device.getAddress(), device);
        }
        // 每次发现新设备刷新列表
        refreshListView();

        // 通过设备地址，更新对应设备的rssi
        mAdapter.setRssi(device.getAddress(), rssi);
    }

    @Override
    public void handleViewBeforeStartRead() {
    }

    /**
     * 刷新列表
     */
    public void refreshListView() {
        mAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
