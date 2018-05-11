package android.zh.usb_serial;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.zh.uart_serial.SerialListener;
import android.zh.xipp.R;

import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class USBSerial {

    private Context mcnt=null;
    private int mbaudrate=0;

    private Vector eventListener = new Vector();

    public void addListener(USBTTL_Listener listener)
    {
        eventListener.add(listener);
    }
    public void removeListener(USBTTL_Listener listener) {
        eventListener.remove(listener);
    }
    private void recv_notify(byte[] buf)
    {
        Iterator iterator = eventListener.iterator();
        while (iterator.hasNext()) {
            USBTTL_Listener sl = (USBTTL_Listener) iterator.next();
            sl.usbUartRecvEvent(buf);
        }
    }

    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    public void initUSB_TTL(Context cnt,int baudrate,USBTTL_Listener listener) {
        mcnt=cnt;
        mbaudrate=baudrate;
        mHandler = new MyHandler(this);
        addListener(listener);

        //
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        mcnt.registerReceiver(mUsbReceiver, filter);

        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it

        //  usbService.changeBaudRate(mbaudrate);
    }

    public void freeUSBTTL() {
        mcnt.unregisterReceiver(mUsbReceiver);
        mcnt.unbindService(usbConnection);
    }

    public void changeBaudRate(int baudrate) {
        usbService.changeBaudRate(baudrate);
    }

    public int sendData(byte[] buf)
    {
        if (usbService != null) { // if UsbService was correctly binded, Send data
            int trueLen=usbService.write(buf);
            return trueLen;
        }
        return -1;
    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent sserv = new Intent(mcnt, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    sserv.putExtra(key, extra);
                }
            }
            mcnt.startService(sserv);
        }
        Intent bindingIntent = new Intent(mcnt, service);
        mcnt.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {

        private USBSerial pus=null;

        public MyHandler(USBSerial us) {
            pus=us;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                        String data = (String) msg.obj;
                        Log.i( "UART TTL","SYNC_READ buffer="+data);
                    break;
                case UsbService.CTS_CHANGE:
                        Log.i( "UART TTL","CTS_CHANGE");
                    break;
                case UsbService.DSR_CHANGE:
                        Log.i( "UART TTL","DSR_CHANGE");
                    break;
                case UsbService.SYNC_READ:
                        String buffer = (String) msg.obj;
                        Log.i( "UART TTL","SYNC_READ buffer="+buffer);
                        pus.recv_notify(buffer.getBytes());
                    break;
            }
        }
    }



}
