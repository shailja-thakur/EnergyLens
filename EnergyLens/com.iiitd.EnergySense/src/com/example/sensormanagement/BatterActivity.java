package com.example.sensormanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.iiitd.EnegySense.R;
import com.example.sensormanagement.WifiCellAcc.WifiReceiver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class BatterActivity extends Service {
	
	Context c;
	int collectstate;
	
	static int level;
	static boolean isCharging;

     public static int lvl;
     public static boolean state;
	int status;
	public static	ArrayList<String> wifi = new ArrayList<String>();
	@Override

	public void onCreate() {
	
		Thread thr = new Thread(null, mTask, "AlarmService_Service");
		thr.start();

	}

	public BatterActivity(){

	}

	@Override
	public void onDestroy() {
		
		stopSelf();
		mTask=null;
		Log.i("","stopped alarmservice1");
		// Tell the user we stopped.

	}

	/**
	 * The function that runs in our worker thread
	 */
	Runnable mTask = new Runnable() {
		public void run() {

			synchronized (mBinder) {
				try {

					System.out.println("runwifiTask()");
					Battery();


				} catch (Exception e) {
				}
			}


			// Done with our work... stop the service!
			BatterActivity.this.stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	/**
	 * Show a notification while this service is running.
	 */



	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//  Logger.logInfo("Service Started onStartCommand");
		return Service.START_NOT_STICKY;
	}

	private void Battery()
	{
/*
		 try {
				p = Runtime.getRuntime().exec("dumpsys cpuinfo com.iiitd.EnergySense");
				//usage.setText(p.toString());
				 in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			       
			      int i=0;
			        while (i<4) {
			            returnString = in.readLine()+"\n";
			            
			           
			            i++;
			        }
			        cpu=returnString.substring(0,6);
			       // usage.setText(returnString.substring(0,6));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
		registerReceiver(mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		
		
	}

	class UnregisterTask {
		public void run() {

			getApplicationContext().unregisterReceiver(mBatInfoReceiver);


		}
	}

	

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			level = i.getIntExtra("level", 0);

		 status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_FULL;
			long epoch = System.currentTimeMillis();
			
			

			float batteryPct = level / (float)status;
			
			String stat=epoch+","+level+","+isCharging+","+batteryPct;

			//    log_battery(stat);
			SharedPreferences app_preferences =getSharedPreferences("settings_data",MODE_PRIVATE); 

			CommonFunctions.setType(app_preferences.getString("type", "none"));

			CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));

			SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 
			collectstate=app_preferences1.getInt("state", 0);

			

			if(collectstate==1){ 
				Logger.batteryLogger(stat);
			}  
			
			unregisterReceiver(this);

		}

	};

	public static int battery_level(){
		
		lvl=level;
		return lvl;
		
	}
	
	public static boolean battery_state(){
		
		
		state=isCharging;
		return state;
		
	}

	

}

