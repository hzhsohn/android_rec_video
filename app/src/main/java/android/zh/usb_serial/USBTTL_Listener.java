package android.zh.usb_serial;

import java.util.EventListener;

/**
 * 事件监听接口
 */
public interface USBTTL_Listener extends EventListener
{
    void usbUartRecvEvent(byte[] buf);
}
