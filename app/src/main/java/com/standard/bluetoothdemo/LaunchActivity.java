package com.standard.bluetoothdemo;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.standard.bluetoothdemo.util.PermissionUtil;


//启动页：做登录判断等
public class LaunchActivity extends AppCompatActivity {
    private PermissionUtil.PermissionTool permissionTool;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (Build.VERSION.SDK_INT >= 23) { //针对6.0以后的版本加权限判断
            permissionTool = new PermissionUtil.PermissionTool(new PermissionUtil.PermissionListener() {
                @Override
                public void allGranted() {
                    initView();
                }
            });
            permissionTool.checkAndRequestPermission(this, permissionTool.requestPermissions);
        } else {
            initView();
        }
    }


    private void initView() {
        Intent intent = new Intent(this, BlueToothActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionTool.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }


}
