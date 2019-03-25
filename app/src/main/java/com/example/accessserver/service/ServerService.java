package com.example.accessserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.accessserver.receiver.ServerReceiver;
import com.example.accessserver.utils.LogUtil;
import com.example.accessserver.utils.NetUtils;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

public class ServerService extends Service {

    private Server mServer;

    @Override
    public void onCreate() {
        mServer = AndServer.serverBuilder()
            .inetAddress(NetUtils.getLocalIPAddress())
            .port(6789)
            .timeout(10, TimeUnit.SECONDS)
            .listener(new Server.ServerListener() {
                @Override
                public void onStarted() {
                    String hostAddress = mServer.getInetAddress().getHostAddress();
                    LogUtil.i("server start on ip: " + hostAddress);
                    ServerReceiver.onServerStart(ServerService.this, hostAddress);
                }

                @Override
                public void onStopped() {
                    ServerReceiver.onServerStop(ServerService.this);
                }

                @Override
                public void onException(Exception e) {
                    LogUtil.i("server stopped!");
                    ServerReceiver.onServerError(ServerService.this, e.getMessage());
                }
            })
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    private void startServer() {
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerReceiver.onServerStart(ServerService.this, hostAddress);
        } else {
            mServer.startup();
        }
    }


    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}