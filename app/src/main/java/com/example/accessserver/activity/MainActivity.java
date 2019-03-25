package com.example.accessserver.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.accessserver.R;
import com.example.accessserver.receiver.ServerReceiver;
import com.example.accessserver.service.AccessService;
import com.example.accessserver.service.ControllerService;
import com.example.accessserver.utils.AccessUtil;
import com.example.accessserver.utils.ShowUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ServerReceiver mServerReceiver;
    private int count = 0;

    private ServiceConnection controllerServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ControllerService.ControllerBinder controllerBinder = (ControllerService.ControllerBinder) service;
            controllerBinder.onGetContext(getApplicationContext());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.tv_test_01).setOnClickListener(this);

        mServerReceiver = new ServerReceiver(this);
        mServerReceiver.register();

        bindControllerService();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // "辅助功能"设置
        AccessUtil.checkSetting(MainActivity.this, AccessService.class);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                mServerReceiver.startServer();
                break;
            case R.id.btn_stop:
                mServerReceiver.stopServer();
                break;
            case R.id.tv_test_01:
                count++;
                ShowUtil.showToast(this, "clicked test_01 " + count + " 次！");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerReceiver.unRegister();
        unBindControllerService();
    }

    private void bindControllerService() {
        Intent intent = new Intent(this, ControllerService.class);
        bindService(intent, controllerServiceConn, BIND_AUTO_CREATE);
    }

    private void unBindControllerService() {
        unbindService(controllerServiceConn);
    }

    public void onServerStart(String ip) {
        ShowUtil.showToast(this, "server start! ip = " + ip);
    }

    public void onServerError(String message) {
        ShowUtil.showToast(this, "server error! msg  =  " + message);
    }

    public void onServerStop() {
        ShowUtil.showToast(this, "server stopped!");
    }

}
