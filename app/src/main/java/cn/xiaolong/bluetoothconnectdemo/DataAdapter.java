package cn.xiaolong.bluetoothconnectdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/16 17:13
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<BluetoothDevice> mBluetoothDeviceList;
    private Context mContext;
    private int bondSize;//已配对设备
    private View.OnClickListener mOnClickListener;

    public DataAdapter(Context context, List<BluetoothDevice> bluetoothDeviceList) {
        mContext = context;
        mBluetoothDeviceList = bluetoothDeviceList;
        bondSize = bluetoothDeviceList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_bluetooth, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mBluetoothDeviceList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mBluetoothDeviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvType;
        private TextView tvBluetoothName;
        private TextView tvBluetoothAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvBluetoothName = (TextView) itemView.findViewById(R.id.tvBluetoothName);
            tvBluetoothAddress = (TextView) itemView.findViewById(R.id.tvBluetoothAddress);
        }

        public void setData(final BluetoothDevice bluetoothDevice, int position) {
            if (position == 0) {
                tvType.setVisibility(View.VISIBLE);
                tvType.setText("已配对设备");
            } else if (position == bondSize) {
                tvType.setVisibility(View.VISIBLE);
                tvType.setText("附近的蓝牙设备");
            } else {
                tvType.setVisibility(View.GONE);
            }
            tvBluetoothName.setText("设备名称：" + bluetoothDevice.getName());
            tvBluetoothAddress.setText("设备地址：" + bluetoothDevice.getAddress());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        v.setTag(bluetoothDevice);
                        mOnClickListener.onClick(v);
                    }
                }
            });
        }
    }

    public void setOnItemCLickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }
}
