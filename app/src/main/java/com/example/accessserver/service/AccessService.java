package com.example.accessserver.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.accessserver.R;
import com.example.accessserver.utils.AccessUtil;
import com.example.accessserver.utils.LogUtil;
import com.example.accessserver.utils.OperateUtil;
import com.example.accessserver.utils.ShowUtil;

import java.util.ArrayList;
import java.util.List;

public class AccessService extends AccessibilityService {
    private AccessibilityNodeInfo rootNode;
    public static final String ACCESS_RECEIVER_ACTION = "Access_receiver_action";
    public static final String OPERATE_GLOBAL_BACK = "operate_global_back";
    public static final String OPERATE_GLOBAL_RECENTS = "operate_global_recent";
    public static final String OPERATE_GLOBAL_HOME = "operate_global_home";
    public static final String OPERATE_CLICK_TEXT = "operate_click_text";
    public static final String OPERATE_CLICK_ID = "operate_click_id";
    public static final String OPERATE_INPUT = "operate_input";
    public static final String OPERATE_SCROLL_BACKWARD = "operate_scroll_backward";
    public static final String OPERATE_SCROLL_FORWARD = "operate_scroll_forward";


    private BroadcastReceiver accessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            produceOperation(intent);
        }
    };

    private void produceOperation(Intent intent) {
        String operate = intent.getStringExtra("operate");
        String text = intent.getStringExtra("text");
        String viewId = intent.getStringExtra("viewId");
        LogUtil.i("operate = " + operate + ", text = " + text + ", viewId = " + viewId);
        AccessibilityNodeInfo node = null;
        boolean isLongClick = intent.getBooleanExtra("isLongClick", false);
        switch (operate) {
            case OPERATE_GLOBAL_BACK:
                initRootNode();
                performGlobalAction(GLOBAL_ACTION_BACK);
                recycleRootNode();
                break;
            case OPERATE_GLOBAL_RECENTS:
                initRootNode();
                performGlobalAction(GLOBAL_ACTION_RECENTS);
                recycleRootNode();
                break;
            case OPERATE_GLOBAL_HOME:
                initRootNode();
                performGlobalAction(GLOBAL_ACTION_HOME);
                recycleRootNode();
                break;
            case OPERATE_CLICK_TEXT:
                clickByText(text, isLongClick);
                break;
            case OPERATE_CLICK_ID:
                clickById(viewId, isLongClick);
                break;
            case OPERATE_SCROLL_BACKWARD:
                initRootNode();
                if (!TextUtils.isEmpty(viewId) && !"null".equals(viewId)) {
                    node = OperateUtil.findNodeById(rootNode, viewId);
                } else {
                    List<AccessibilityNodeInfo> list = new ArrayList<>();
                    OperateUtil.findFirstView(list, rootNode, "android.widget.ScrollView");
                    LogUtil.i("list size : " + list.size());
                    if (!list.isEmpty()) {
                        node = list.get(0);
                    }
                }
                if (node != null) {
                    OperateUtil.performScrollBackward(node);
                }

                recycleRootNode();
                break;
            case OPERATE_SCROLL_FORWARD:
                initRootNode();
                if (!TextUtils.isEmpty(viewId) && !"null".equals(viewId)) {
                    node = OperateUtil.findNodeById(rootNode, viewId);
                } else {
                    List<AccessibilityNodeInfo> list = new ArrayList<>();
                    OperateUtil.findFirstView(list, rootNode, "android.widget.ScrollView");
                    if (!list.isEmpty()) {
                        node = list.get(0);
                    }
                }
                if (node != null) {
                    OperateUtil.performScrollForward(node);
                }

                recycleRootNode();
                break;
            case OPERATE_INPUT:
                inputById(viewId, text);
                break;
        }
    }

    public void clickById(String viewId, boolean isLongClick) {
        try {
            LogUtil.i("/click/id");
            initRootNode();
            if (rootNode == null) {
                return;
            }
            AccessibilityNodeInfo node = OperateUtil.findNodeById(rootNode, viewId);
            if (node != null) {
                if (isLongClick) {
                    OperateUtil.performLongClick(node);
                } else {
                    OperateUtil.performClick(node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recycleRootNode();
        }
    }

    public void clickByText(String text, boolean isLongClick) {
        try {
            LogUtil.i("/click/text");
            initRootNode();
            if (rootNode == null) {
                return;
            }
            AccessibilityNodeInfo node = OperateUtil.findNodeByText(rootNode, text);
            if (node != null) {
                if (isLongClick) {
                    OperateUtil.performLongClick(node);
                } else {
                    OperateUtil.performClick(node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recycleRootNode();
        }
    }

    private void inputById(String viewId, String inputStr) {
        initRootNode();
        AccessibilityNodeInfo node = null;
        if (!TextUtils.isEmpty(viewId) && !"null".equals(viewId)) {
            node = OperateUtil.findNodeById(rootNode, viewId);
        } else {
            List<AccessibilityNodeInfo> list = new ArrayList<>();
            OperateUtil.findFirstView(list, rootNode, "android.widget.EditText");
            if (list.size() > 0) {
                node = list.get(0);
            }
        }

        if(node == null){
            LogUtil.i("can not find target node!");
            recycleRootNode();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, inputStr);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

        } else {
            Bundle arguments = new Bundle();
            arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
            arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, inputStr.length());
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
        recycleRootNode();
    }

    @Override
    protected void onServiceConnected() {
        ShowUtil.showToast(this, getString(R.string.app_name) + "辅助服务开启了");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        //不写完整包名，是因为某些手机(如小米)安装器包名是自定义的
        //        if (event == null) {
        ////            return;
        ////        }
        //         LogUtil.i( " ---> onAccessibilityEvent: " + event.toString());

        //        AccessibilityNodeInfo eventNode = event.getSource();
        //        if (eventNode == null) {
        //             LogUtil.i( "eventNode: null, 重新获取eventNode...");
        //            performGlobalAction(GLOBAL_ACTION_RECENTS); // 打开最近页面
        //            performGlobalAction(GLOBAL_ACTION_BACK); // 返回安装页面
        //            return;
        //        }
        //        eventNode.recycle();
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(accessReceiver, new IntentFilter(ACCESS_RECEIVER_ACTION));
    }

    @Override
    public void onDestroy() {
        ShowUtil.showToast(this, getString(R.string.app_name) + "辅助服务停止了，请重新开启");
        unregisterReceiver(accessReceiver);
        // 服务停止，重新进入系统设置界面
        AccessUtil.jumpToSetting(this);
    }

    private void initRootNode() {
        rootNode = getRootInActiveWindow();
        LogUtil.i("rootNode: " + rootNode);
    }

    private void recycleRootNode() {
        if (rootNode != null) {
            rootNode.recycle();
        }
    }

}
