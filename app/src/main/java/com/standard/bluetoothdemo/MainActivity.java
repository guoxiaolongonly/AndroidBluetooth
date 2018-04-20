package com.standard.bluetoothdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout rlTitle;
    private TextView tvChatterName;
    private RecyclerView rvChatView;
    private Button btnSend;
    private EditText etMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        rlTitle = findViewById(R.id.rlTitle);
        tvChatterName = findViewById(R.id.tvChatterName);
        rvChatView = findViewById(R.id.rvChatView);
        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
    }
}
