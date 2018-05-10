package android.zh.uart_serial;

import java.util.EventListener;

/**
 * 事件监听接口
 */
public interface SerialListener extends EventListener
{
    void uartRecvEvent(byte[] buf);
}
