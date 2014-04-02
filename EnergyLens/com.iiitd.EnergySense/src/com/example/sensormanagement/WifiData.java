package com.example.sensormanagement;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.iiitd.EnegySense.R;
import com.example.sensormanagement.WifiCellAcc.WifiReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

public class WifiData extends Service {
	NotificationManager mNM;
	Context c;

	File FileHandle,FileHandle1;
	FileOutputStream AcclWriter,AcclWriter1;
	public static int Sample;
	public static int wake;
	public static int wake_upload;
	public static String state;
	public static String response;
	ArrayList<Integer> value=new ArrayList<Integer>();
	public static ArrayList<Integer> finalvalue=new ArrayList<Integer>();
	public static ArrayList<Integer> finalvalue1=new ArrayList<Integer>();
	static DataOutputStream SDWriter,SDWriter1;
	int lineCount = 0;
	Timer timer;
	Context Cnxt;
	static File myFile;
	int k,count;
	String ssid;
	public static String data;
	WifiManager mainWifi;
	WifiReceiver receiverWifi;
	public static   List<ScanResult> wifiList;
	State mobile;
	StringBuilder sb = new StringBuilder();

	SensorManager SensorHandle;
	Sensor AcclHandle;

	ArrayList<Integer> values = new ArrayList<Integer>();
	public static	ArrayList<String> wifi = new ArrayList<String>();
	@Override

	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	
		Thread thr = new Thread(null, mTask, "AlarmService_Service");
		thr.start();
	}

	public WifiData(){

	}

	@Override
	public void onDestroy() {
		
		mNM.cancel(0);


	}

	/**
	 * The function that runs in our worker thread
	 */
	Runnable mTask = new Runnable() {
		public void run() {

			synchronized (mBinder) {
				try {


					runWiFiTask();

				} catch (Exception e) {
				}
			}


			// Done with our work... stop the service!
			WifiData.this.stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = "alarm_service_started";

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher,
				text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, AcclData.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,
				"alarm_service_label", text, contentIntent);

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		mNM.notify(0, notification);
	}

	/**
	 * This is the object that receives interactions from clients. See
	 * RemoteService for a more complete example.
	 */
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	private void runWiFiTask()
	{


		mainWifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver();

		getApplicationContext().registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifi.startScan();
		Log.i("","Registered broadcast WiFi service and starting WiFi scan");


	}

	

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			int p;

			JSONObject ob = new JSONObject();
			StringBuilder sb = new StringBuilder();
			int info = mainWifi.getConnectionInfo().getRssi();
			
			wifiList = mainWifi.getScanResults();
			SharedPreferences prefs4 = c.getSharedPreferences("scan", Context.MODE_PRIVATE);
			SharedPreferences.Editor editorscan = prefs4.edit();
			editorscan.clear().commit();

			for(ScanResult result:wifiList){

				SharedPreferences prefs = c.getSharedPreferences("NAME", Context.MODE_PRIVATE);
				count = prefs.getInt("Count", 15);
				k=prefs.getInt("valuecount", 0);



				SharedPreferences prefswifi = c.getSharedPreferences("WifiSSID", Context.MODE_PRIVATE);
				ssid=prefswifi.getString("ssid", WifiCellAcc.valueAtPosition);


				SharedPreferences prefs3 = c.getSharedPreferences("scan", Context.MODE_PRIVATE);

				p=prefs3.getInt("scancount", 0);

				try {

			
					wifi.add(result.SSID);


					SharedPreferences prefs2 = c.getSharedPreferences("scan", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor1 = prefs2.edit();


					//   editor1.clear().commit();
					if(result.SSID != null){
						p++;

						editor1.putString("scan_"+p,result.SSID);
						editor1.putInt("scancount", p);
					}


					if(k==count){
						k=0;
						//	finalvalue.clear();

					}

					SharedPreferences prefs1 = c.getSharedPreferences("NAME", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs1.edit();
					if((result.SSID.equalsIgnoreCase(ssid))&&k<=count){

						editor1.putString("ssid", result.SSID);
						editor1.putString("bssid",result.BSSID);
						editor1.putInt("level", result.level);
						k++;
						//	editor.putString("ssid",WifiCellAcc.valueAtPosition );
						editor.putInt("IntValue_"+k,result.level);
						editor.putInt("valuecount", k);
					
					}

					editor1.commit();
					editor.commit();

					//	System.out.println(finalvalue);


				} catch (Exception e) {

					e.printStackTrace();
				}

			}


		}}



}

