package android.zh.xipp;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Frm1Activity extends AppCompatActivity {

    //
    private JCameraView jCameraView;

    //显示时间
    Calendar ctime = null;//可以对每个时间域单独修改
    //
    TextView tim1=null;
    TextView tim2=null;
    ImageView imgv=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_frm1);
        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setTip("JCameraView Tip");
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.i("CJT", "camera error");
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(Frm1Activity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });

        //////////////////////////////////////////////////
        // 添加一个Timer，可以让程序运行起来了
        Timer tim = new Timer();
        tim.schedule(taskClock, 0, 1000); //1000ms执行一次
        //
        tim1=((TextView)findViewById(R.id.textView));
        tim2=((TextView)findViewById(R.id.textView4));
        imgv=(ImageView)findViewById(R.id.imageView);
    }


    private TimerTask taskClock = new TimerTask() {
        public void run() {
            //操作UI
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ctime = Calendar.getInstance();
                    int year = ctime.get(Calendar.YEAR);
                    int month = ctime.get(Calendar.MONTH);
                    int date = ctime.get(Calendar.DATE);
                    int hour = ctime.get(Calendar.HOUR_OF_DAY);
                    int minute = ctime.get(Calendar.MINUTE);
                    int second = ctime.get(Calendar.SECOND);
                    System.out.println(year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second);
                    tim1.setText(year + "/" + month + "/" + date );
                    tim2.setText(hour + ":" +minute + ":" + second);
                    ctime.clear();
                    ctime=null;

                    //显示小心跳
                    if(imgv.getVisibility()==View.VISIBLE)
                    {
                        imgv.setVisibility(View.INVISIBLE);
                    }
                    else {
                        imgv.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    /*
     *
     * 串口
     *
     * */
    //
    public void btn1_click(View v)
    {

    }
    //
    public void btn2_click(View v)
    {

    }
    //
    public void btn3_click(View v)
    {

    }

}
