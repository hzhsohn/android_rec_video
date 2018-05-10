package android.zh.uart_serial;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class SerialMagr {
    public static FileOutputStream mOutputStream=null;
    public static FileInputStream mInputStream=null;
    public static SerialPort sp;
    private Vector eventListener = new Vector();
    boolean isRun=false;

    public void addListener(SerialListener listener)
    {
        eventListener.add(listener);
    }
    public void removeListener(SerialListener listener) {
        eventListener.remove(listener);
    }
    private void recv_notify(byte[] buf)
    {
        Iterator iterator = eventListener.iterator();
        while (iterator.hasNext()) {
            SerialListener sl = (SerialListener) iterator.next();
            sl.uartRecvEvent(buf);
        }
    }

    public SerialMagr(SerialListener listener)
    {
        isRun=true;
        addListener(listener);
        new Thread(recvThread).start();
    }

    //"/dev/ttyS2"
    public boolean initSerialPort(String path,int baudrate)
    {

        try {
            sp=new SerialPort(new File(path),baudrate);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(sp!=null) {
            mOutputStream = (FileOutputStream) sp.getOutputStream();
            mInputStream = (FileInputStream) sp.getInputStream();
            return true;
        }
        return false;
    }

    //sendSerialData(new String("send").getBytes());
    public int sendSerialData(byte[] data)
    {
        int ret=-1;
        if(mOutputStream!=null) {
            try {
                mOutputStream.write(data);
                ret=data.length;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public Runnable recvThread=new Runnable(){
        public void run(){
            int size;
            while(isRun) {
                try {
                    if (mInputStream != null) {
                        byte[] buffer = new byte[128];
                        size = mInputStream.read(buffer);
                        if (size > 0) {
                            Log.i("serial port", "recv size=" + size);
                            recv_notify(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
