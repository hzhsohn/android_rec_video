package com.cjt2325.cameralibrary;

import android.content.Context;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.cjt2325.cameralibrary.listener.CaptureListener;
import com.cjt2325.cameralibrary.util.LogUtil;

import static com.cjt2325.cameralibrary.JCameraView.BUTTON_STATE_BOTH;


/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.1.4
 * 创建日期：2017/4/25
 * 描    述：拍照按钮
 * =====================================
 */
public class CaptureButton extends View {

    private int state;              //当前按钮状态
    private int button_state;       //按钮可执行的功能状态（拍照,录制,两者）

    public static final int STATE_IDLE = 0x001;        //空闲状态
    public static final int STATE_PRESS = 0x002;       //按下状态
    public static final int STATE_LONG_PRESS = 0x003;  //长按状态
    public static final int STATE_RECORDERING = 0x004; //录制状态
    public static final int STATE_BAN = 0x005;         //禁止状态

    private int progress_color = 0xEE16AE16;            //进度条颜色
    private int outside_color = 0xEEDCDCDC;             //外圆背景色
    private int inside_color = 0xFFFFFFFF;              //内圆背景色


    private float event_Y;  //Touch_Event_Down时候记录的Y值


    private Paint mPaint;

    private float strokeWidth;          //进度条宽度
    private int outside_add_size;       //长按外圆半径变大的Size
    private int inside_reduce_size;     //长安内圆缩小的Size

    //中心坐标
    private float center_X;
    private float center_Y;

    private float button_radius;            //按钮半径
    private float button_outside_radius;    //外圆半径
    private float button_inside_radius;     //内圆半径
    private int button_size;                //按钮大小

    private float progress;         //录制视频的进度
    private int duration;           //录制视频最大时间长度
    private int recorded_time;      //记录当前录制的时间

    private CaptureListener captureLisenter;        //按钮回调接口
    private RecordCountDownTimer timer;             //计时器

    Context mContext;

    public CaptureButton(Context context) {
        super(context);
    }

    public CaptureButton(Context context, int size) {
        super(context);
        mContext=context;
        this.button_size = size;
        button_radius = size / 2.0f;

        button_outside_radius = button_radius;
        button_inside_radius = button_radius * 0.75f;

        strokeWidth = size / 15;
        outside_add_size = size / 5;
        inside_reduce_size = size / 8;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        progress = 0;

        state = STATE_IDLE;                //初始化为空闲状态
        button_state = BUTTON_STATE_BOTH;  //初始化按钮为可录制可拍照
        LogUtil.i("CaptureButtom start");
        duration = 20 * 1000;              //默认最长录制时间为20s
        LogUtil.i("CaptureButtom end");

        center_X = (button_size + outside_add_size * 2) / 2;
        center_Y = (button_size + outside_add_size * 2) / 2;


        timer = new RecordCountDownTimer(duration, duration / 360);    //录制定时器

        //
        Toast.makeText(mContext,"开始录制视频", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(button_size + outside_add_size * 2, button_size + outside_add_size * 2);
    }

    //录制结束
    private void recordEnd() {
        if (captureLisenter != null) {
                captureLisenter.recordEnd(recorded_time);  //回调录制结束
        }
    }

    //开始录像
    public  void buttonStartRecVideo()
    {
        Toast.makeText(mContext,"开始录制视频", Toast.LENGTH_SHORT).show();

        if (captureLisenter != null)
            captureLisenter.recordStart();

        state = STATE_RECORDERING;
        timer.start();
    }

    //更新进度条
    private void updateProgress(long millisUntilFinished) {
        recorded_time = (int) (duration - millisUntilFinished);
        progress = 360f - millisUntilFinished / (float) duration * 360f;
        invalidate();
    }

    //录制视频计时器
    private class RecordCountDownTimer extends CountDownTimer {
        RecordCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateProgress(millisUntilFinished);
        }

        @Override
        public void onFinish() {

            updateProgress(0);
            recordEnd();

            //已在主线程中，可以更新UI
            buttonStartRecVideo();
        }
    }


    /**************************************************
     * 对外提供的API                     *
     **************************************************/

    //设置回调接口
    public void setCaptureLisenter(CaptureListener captureLisenter) {
        this.captureLisenter = captureLisenter;
    }

    //是否空闲状态
    public boolean isIdle() {
        return state == STATE_IDLE ? true : false;
    }

    //设置状态
    public void resetState() {
        state = STATE_IDLE;
    }
}
