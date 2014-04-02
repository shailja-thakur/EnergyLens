
package com.example.sensormanagement;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.NotificationManager;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Environment;
import android.os.IBinder;

import android.util.Log;

public class AlarmService1 extends Service {
	NotificationManager mNM;
	Context c;
	static final String Position="position";
	
	public static int Sample;
	public static int wake;
	Date date;
	int hours,min,days;
	public static int wake_upload;

	long current =0;

	Context Cnxt;
	static File myFile;
	int k,count;
	String ssid;
	int collectstate;
	public static String data;
	WifiManager mainWifi;
	String log;
	boolean state;
	WifiReceiver receiverWifi;
	public static   List<ScanResult> wifiList;
	Boolean present=false;


	//ArrayList<Integer> values = new ArrayList<Integer>();
	public static	ArrayList<String> wifi = new ArrayList<String>();
	static	String label;
	@Override

	public void onCreate() {
		System.out.println("entere wifi service");


	}

	public AlarmService1(){

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		SharedPreferences data =getApplicationContext().getSharedPreferences(Position,Context.MODE_PRIVATE);
		//	 Log.i("wifilabel",data.getString("wifilabel", "none"));
		label=data.getString("wifilabel", "none");

		Thread thr = new Thread(null, mTask, "AlarmService_Service");
		thr.start();
		//runWiFiTask();


		return START_NOT_STICKY;
	}

	
	 
	    @Override
		public void onDestroy() {
			
			stopSelf();
			mTask=null;
			unregisterReceiver(receiverWifi);
			Log.i("","stopped alarmservice1");
			// Tell the user we stopped.
			//if (mainWifi.isWifiEnabled()==true) 
			//	mainWifi.setWifiEnabled(false);

		}

	    
	    
	    Runnable mTask = new Runnable() {
		public void run() {
			
			try{
				synchronized(this){
			mainWifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			if (mainWifi.isWifiEnabled()==false) 
				mainWifi.setWifiEnabled(true);
			receiverWifi = new WifiReceiver();

			getApplicationContext().registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			
				current=System.currentTimeMillis();
				mainWifi.startScan();
				}
			
		}catch(Exception e){

			System.out.println(e.toString());
		}
		Log.i("","Registered broadcast WiFi service and starting WiFi scan");
		
		
		

	}

	};

	

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {



			wifiList = mainWifi.getScanResults();
			//Log.i("AlarmService1", Boolean.toString(wifiList.isEmpty()));

			if (wifiList != null){
				
			
			for(ScanResult result:wifiList){


				try {

					long epoch = System.currentTimeMillis();

					wifi.add(result.SSID);


					SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 
					collectstate=app_preferences1.getInt("state", 0);

					
					log=epoch+","+result.BSSID+","+result.SSID+","+result.level+","+label;

					SharedPreferences app_preferences =getSharedPreferences("settings_data",MODE_PRIVATE); 

					CommonFunctions.setType(app_preferences.getString("type", "none"));

					CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));

					if(collectstate==1){   

						synchronized(this){



							Logger.wifiLogger(log);


						}
					}

				} catch (Exception e) {

					e.printStackTrace();
				}

			}
			
			}
			else{
				long epoch = System.currentTimeMillis();
				log=epoch+"," + "00:00:00:00:00" + ","+ "None" +","+ 1 +","+label;
				Logger.wifiLogger(log);
			}
		}

	}


	




}

