package com.standard.bluetoothdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.standard.bluetoothdemo.R;
import com.standard.bluetoothdemo.bean.BaseMessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/4/21 13:15
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context mContext;
    private List<BaseMessageInfo> mBaseMessageInfos;
    private View.OnClickListener mOnClickListener;

    public ChatListAdapter(Context context) {
        mContext = context;
        mBaseMessageInfos = new ArrayList<>();
    }

    public void addMessage(BaseMessageInfo baseMessageInfo) {
        mBaseMessageInfos.add(baseMessageInfo);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BaseMessageInfo.TYPE_RECEIVE) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chatting_item_msg_text_left, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chatting_item_msg_text_right, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mBaseMessageInfos.get(position), position);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mBaseMessageInfos.get(position).getMessageType();
    }

    @Override
    public int getItemCount() {
        return mBaseMessageInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageTime;
        private TextView tvUserName;
        private TextView tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvContent = itemView.findViewById(R.id.tvContent);
        }

        public void setData(BaseMessageInfo data, int position) {
            tvMessageTime.setText(data.getMessageTime());
            tvUserName.setText(data.getUserName());
            tvContent.setText(data.getMessageContent());
        }


    }
}