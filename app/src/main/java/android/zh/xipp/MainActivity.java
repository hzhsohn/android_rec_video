package android.zh.xipp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.zh.service.ProcessMonitorService;
import android.zh.uart_serial.SerialListener;
import android.zh.uart_serial.SerialMagr;
import android.zh.usb_serial.USBSerial;
import android.zh.usb_serial.USBTTL_Listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private SerialMagr uart=null;
    private USBSerial usbuart=null;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //启用后台服务
        //Intent intent = new Intent(MainActivity.this,ProcessMonitorService.class);
        //startService(intent) ;

        //做其它界面初始化
        getPermissions();
/*
        //打开UART1串口,需要chmod 777 /dev/ttyS0
        uart=new SerialMagr(uartRecv);
        //如果没有提权会阻塞
        if(uart.initSerialPort("/dev/ttyS0",115200))
        {
            Toast.makeText(this, "串口打开成功", Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(this, "串口打开失败", Toast.LENGTH_LONG).show();
        }
*/
        //打开USBSerial
        usbuart=new USBSerial();
        usbuart.initUSB_TTL(this,9600,usbttl);

        //
        //////////////////////////////////////////////////
        // 添加一个Timer，可以让发送运行起来了
        Timer tim = new Timer();
        tim.schedule(taskClock, 5000, 10000);
    }

    USBTTL_Listener usbttl=new USBTTL_Listener() {
        @Override
        public void usbUartRecvEvent(byte[] buf) {
            Log.d("MainActivity USB-TTL","Recv len="+buf.length +" buf="+new String(buf));
        }
    };

    SerialListener uartRecv=new SerialListener() {
        @Override
        public void uartRecvEvent(byte[] buf) {
            Log.d("MainActivity UART","Recv len="+buf.length +" buf="+new String(buf));
        }
    };

    private TimerTask taskClock = new TimerTask() {
        public void run() {
            //操作UI
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(uart!=null) {
                        //串口发送数据
                        int ret=uart.sendSerialData(new String("xipp - fuck - you!!").getBytes());
                        if (ret > 0) {
                            Log.i("serial", "send size=" + ret);
                        } else {
                            Toast.makeText(getApplicationContext(), "serial send fail..",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(usbuart!=null) {
                        //usb串口发送
                        int ret= usbuart.sendData(new String("xipp - fuck - you2!!").getBytes());
                        if(ret>0)
                        {
                            Log.i("usbserial", "send size=" + ret);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "usbserial send fail..",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
    };

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED ) {
                startActivityForResult(new Intent(MainActivity.this, Frm1Activity.class), 100);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
            }
        } else {
            startActivityForResult(new Intent(MainActivity.this, Frm1Activity.class), 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    startActivityForResult(new Intent(MainActivity.this, Frm1Activity.class), 100);
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
