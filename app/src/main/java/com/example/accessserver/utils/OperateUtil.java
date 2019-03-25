package com.example.accessserver.utils;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * 辅助功能/无障碍相关工具
 */
public class OperateUtil {

    /**
     * 模拟下滑操作
     */
    public static void performScrollBackward(AccessibilityNodeInfo scrollableNode) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (scrollableNode != null) {
            scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
    }

    /**
     * 模拟上滑操作
     */
    public static void performScrollForward(AccessibilityNodeInfo scrollableNode) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (scrollableNode != null) {
            scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
    }

    /**
     * 点击相应Id的view
     */
    public static void performClick(AccessibilityNodeInfo clickableNode) {
        if (clickableNode != null) {
            clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 长点击相应Id的view
     */
    public static void performLongClick(AccessibilityNodeInfo clickableNode) {
        if (clickableNode != null) {
            clickableNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        }
    }

    // 查找第一个 id 节点
    public static AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo nodeInfo, String resId) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    // 查找第一个 文本 节点
    public static AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * @param root         root node
     * @param viewTypeName @example: android.widget.EditText
     * @return
     */
    public static void findFirstView(List<AccessibilityNodeInfo> nodeList, AccessibilityNodeInfo root, String viewTypeName) {
        if (viewTypeName.equals(root.getClassName().toString())) {
            nodeList.add(root);
        } else {
            for (int i = 0; i < root.getChildCount(); i++) {
                findFirstView(nodeList, root.getChild(i), viewTypeName);
            }
        }
    }

    /**
     * @param root root node
     * @return
     */
    public static void findFirstScrollableView(List<AccessibilityNodeInfo> nodeList, AccessibilityNodeInfo root) {
        String clzName = root.getClassName().toString();
        if ("android.widget.ScrollView".equals(clzName)
                || "android.widget.ListView".equals(clzName)) {
            nodeList.add(root);
        } else {
            for (int i = 0; i < root.getChildCount(); i++) {
                findFirstScrollableView(nodeList, root.getChild(i));
            }
        }
    }
}