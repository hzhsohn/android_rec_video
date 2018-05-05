package android.zh.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import hxkong.msd.MSDService;

public class ProcessMonitorService extends Service {

	private Context mContext;
	private Handler handler = new Handler();

	//
	AlarmManager manager;
	PendingIntent pi;
	//
	MSDService msd;
	//后台线程
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			//延时
			handler.postDelayed(mRunnable, 100);
			//录像

		}
	};

	// 注册监听
	@SuppressWarnings("deprecation")
	private void registerApkInstallListener() {
		//初始化内容
		msd=new MSDService(8181);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.cancel(pi);
	}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*进程被移除*/
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		//1秒激活一次线程
		long triggerAtTime = SystemClock.elapsedRealtime()+1000;
		Intent i = new Intent(mContext,AlarmReceiver.class);
		pi = PendingIntent.getBroadcast(this,0,i,0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = ProcessMonitorService.this;
		Toast.makeText(mContext,"hx-kong后台服务启动", Toast.LENGTH_SHORT).show();
		handler.post(mRunnable);
		registerApkInstallListener() ;
	}
}
