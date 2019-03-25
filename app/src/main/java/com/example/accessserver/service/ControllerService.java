package com.example.accessserver.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;

import java.lang.ref.WeakReference;

@RestController
@RequestMapping
public class ControllerService extends Service {
    private static WeakReference<Context> weakReferenceContext;

    private ControllerBinder mBinder = new ControllerBinder();

    public class ControllerBinder extends Binder {

        public void onGetContext(Context context) {
            ControllerService.weakReferenceContext = new WeakReference<>(context);
        }
    }

    @GetMapping("/click/text")
    public void clickByText(@RequestParam("text") String text,
                            @RequestParam(value = "isLongClick", required = false, defaultValue = "false") boolean isLongClick) {
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_CLICK_TEXT);
        intent.putExtra("text", text);
        intent.putExtra("isLongClick", isLongClick);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/click/id")
    public void clickById(@RequestParam("viewId") String viewId,
                          @RequestParam(value = "isLongClick", required = false, defaultValue = "false") boolean isLongClick) {
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_CLICK_ID);
        intent.putExtra("viewId", viewId);
        intent.putExtra("isLongClick", isLongClick);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/input")
    public void input(@RequestParam(value = "text") String text,
                      @RequestParam(value = "viewId", required = false) String viewId) {
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_INPUT);
        intent.putExtra("viewId", viewId);
        intent.putExtra("text", text);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/global/back")
    public void globalBack() {
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_GLOBAL_BACK);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/global/recent")
    public void globalRecent() {
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_GLOBAL_RECENTS);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/global/home")
    public void globalHome() {
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_GLOBAL_HOME);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/scroll/backward")
    public void scrollBackward(@RequestParam(value = "viewId", required = false) String viewId) {
        // 向上滚动
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_SCROLL_BACKWARD);
        intent.putExtra("viewId", viewId);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    @GetMapping("/scroll/forward")
    public void scrollForward(@RequestParam(value = "viewId", required = false) String viewId) {
        // 向下滚动
        Intent intent = getAccessReceiverIntent();
        intent.putExtra("operate", AccessService.OPERATE_SCROLL_FORWARD);
        intent.putExtra("viewId", viewId);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    private Intent getAccessReceiverIntent() {
        return new Intent(AccessService.ACCESS_RECEIVER_ACTION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
