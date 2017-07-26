package whatsappautovoice.ohadcn.whatsappautovoiceplay;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ListView;

import java.util.List;

public class WhatsAppVoicePlayService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        if (BuildConfig.DEBUG) {
            System.out.println("onAccessibilityEvent connected " + getServiceInfo());
        }
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (BuildConfig.DEBUG) {
            System.out.println("onAccessibilityEvent " + event.getEventType() + " " + event.getClassName());
        }
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                reRegisterEvents(event);
                return;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                considerPlaying(event);
                return;
            default:
                if (BuildConfig.DEBUG) {
                    System.out.println("onAccessibilityEvent default " + event);
                }
        }
    }

    private void reRegisterEvents(AccessibilityEvent event) {
        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        if (BuildConfig.DEBUG) {
            System.out.println("onAccessibilityEvent " + event.getClassName());
        }
        if (event.getClassName().equals("com.whatsapp.Conversation")) {
            if (BuildConfig.DEBUG) {
                System.out.println("onAccessibilityEvent conversation");
                info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            }
            info.eventTypes |= AccessibilityEvent.TYPE_VIEW_SELECTED; //AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;  AccessibilityEvent.TYPE_VIEW_FOCUSED;
        }
        setServiceInfo(info);
    }

    private void considerPlaying(AccessibilityEvent event) {
        if (BuildConfig.DEBUG) {
            System.out.println(event);
        }
        try {
            AccessibilityNodeInfo source = event.getSource();
            if(!source.isFocused()) {
                if (BuildConfig.DEBUG) {
                    System.out.println("not focused");
                }
                return;
            }
            List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText("play");
            if (BuildConfig.DEBUG) {
                System.out.println(nodes);
            }
            for (AccessibilityNodeInfo info:nodes) {
                if (info.isSelected()) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
        System.out.println("onAccessibilityEvent interrupt");
    }

}
