package com.standard.bluetoothdemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.standard.bluetoothdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/4/20 17:05
 */

public class BluetoothAdapter
extends RecyclerView.Adapter<BluetoothAdapter.ViewHolder> {
private Context mContext;
private List<BluetoothDevice> mBluetoothDeviceList;
private View.OnClickListener mOnClickListener;

public BluetoothAdapter(Context context) {
        mContext = context;
        mBluetoothDeviceList = new ArrayList<>();
        }


@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_bluetooth, parent, false));
        }

public void addItem(BluetoothDevice bluetoothDevice) {
        if (!TextUtils.isEmpty(bluetoothDevice.getName())) {
        mBluetoothDeviceList.add(bluetoothDevice);
        notifyDataSetChanged();
        }
        }

public void clearData() {
        mBluetoothDeviceList.clear();
        notifyDataSetChanged();
        }

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mBluetoothDeviceList.get(position), position);
        }

public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        }

@Override
public int getItemCount() {
        return mBluetoothDeviceList.size();
        }

public void addAll(List<BluetoothDevice> bluetoothDevices) {
        mBluetoothDeviceList.clear();
        mBluetoothDeviceList.addAll(bluetoothDevices);
        notifyDataSetChanged();
        }


public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView tvBluetoothName;
    private TextView tvBluetoothAddress;

    public ViewHolder(View itemView) {
        super(itemView);
        tvBluetoothName = itemView.findViewById(R.id.tvBluetoothName);
        tvBluetoothAddress = itemView.findViewById(R.id.tvBluetoothAddress);
    }

    public void setData(final BluetoothDevice data, int position) {
        tvBluetoothName.setText("名称：" + data.getName());
        tvBluetoothAddress.setText("MAC：" + data.getAddress());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    v.setTag(data);
                    mOnClickListener.onClick(v);
                }
            }
        });
    }


}
}