package com.standard.bluetoothdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.standard.bluetoothdemo.adapter.ChatListAdapter;
import com.standard.bluetoothdemo.bean.BaseMessageInfo;
import com.standard.bluetoothmodule.BtManager;
import com.standard.bluetoothmodule.callback.IDataTransferCallBack;
import com.standard.bluetoothmodule.dataprocess.ResponseData;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private TextView tvChatterName;
    private RecyclerView rvChatView;
    private Button btnSend;
    private EditText etMessage;
    private ChatListAdapter chatListAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String mChatterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etMessage.getText())) {
                    return;
                }
                BtManager.getInstance().sendStringData(etMessage.getText().toString());
            }
        });
        BtManager.getInstance().setDataTransferCallBack(new IDataTransferCallBack() {
            @Override
            public void sendDataSuccess(byte[] data) {
                BaseMessageInfo baseMessageInfo = new BaseMessageInfo();
                baseMessageInfo.setMessageTime(simpleDateFormat.format(System.currentTimeMillis()));
                baseMessageInfo.setMessageContent(etMessage.getText().toString());
                baseMessageInfo.setUserName("我");
                etMessage.setText("");
                baseMessageInfo.setMessageType(BaseMessageInfo.TYPE_SEND);
                chatListAdapter.addMessage(baseMessageInfo);
            }

            @Override
            public void receiveData(ResponseData result) {
                BaseMessageInfo baseMessageInfo = new BaseMessageInfo();
                baseMessageInfo.setMessageTime(simpleDateFormat.format(System.currentTimeMillis()));
                baseMessageInfo.setMessageContent(new String(((byte[]) result.data)));
                baseMessageInfo.setMessageType(BaseMessageInfo.TYPE_RECEIVE);
                baseMessageInfo.setUserName(mChatterName);
                chatListAdapter.addMessage(baseMessageInfo);
            }

            @Override
            public void onReceiving(int current, int all) {

            }

            @Override
            public void onReceiveOutOfTime() {

            }
        });
    }

    private void initData() {
        mChatterName = getIntent().getStringExtra("deviceName");
        tvChatterName.setText(mChatterName);
    }

    private void initView() {
        tvChatterName = findViewById(R.id.tvChatterName);
        rvChatView = findViewById(R.id.rvChatView);
        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
        rvChatView.setAdapter(chatListAdapter = new ChatListAdapter(this));
        rvChatView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
