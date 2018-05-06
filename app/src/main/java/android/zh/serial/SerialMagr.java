package android.zh.serial;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialMagr {
    public static FileOutputStream mOutputStream=null;
    public static FileInputStream mInputStream=null;
    public static SerialPort sp;

    //"/dev/ttyS2"
    public static boolean initSerialPort(String path,int baudrate)
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
    public static int sendSerialData(byte[] data)
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

    //new Thread(SerialMagr.recvThread).start();
    public static Runnable recvThread=new Runnable(){
        public void run(){
            int size;

            try {
                byte[] buffer = new byte[64];
                if (mInputStream == null) return;
                size = mInputStream.read(buffer);
                if (size > 0) {
                    Log.i("serial port","recv size="+size);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    };
}
