package com.example.sensormanagement;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.iiitd.EnegySense.R;

/**
 * This is an example of implementing an application service that will run in
 * response to an alarm, allowing us to move long duration work out of an intent
 * receiver.
 * 
 * @see AlarmService
 * @see AlarmService_Alarm
 */
public class AcclData extends Service {
	static String Position="position";
	File FileHandle,FileHandle1;
	FileOutputStream AcclWriter,AcclWriter1;
	static DataOutputStream SDWriter,SDWriter1;
	static File myFile;
	NotificationManager mNM;
	Context c=this;
	Timer timer;
	SensorManager SensorHandle;
	Sensor AcclHandle;
	int collectstate;
	String value;
	String log;
	int days,hours,min;
	Date date;
	static String label;
	List<Float> x=new ArrayList<Float>();
	List<Float> y=new ArrayList<Float>();
	List<Float> z=new ArrayList<Float>();
	List<Double> avg=new ArrayList<Double>();
	@Override
	public void onCreate() {


	}

	public AcclData(){

	}

	@Override
	public void onDestroy() {

		//isMyServiceRunning();
		stopSelf();

		
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}




	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences data =getApplicationContext().getSharedPreferences(Position,Context.MODE_PRIVATE);
		//	 Log.i("accllabel",data.getString("accllabel", "none"));
		label=data.getString("accllabel", "none");


		Start();

		//  Logger.logInfo("Service Started onStartCommand");
		return START_NOT_STICKY;
	}

	public void Start(){
		int rate = SensorManager.SENSOR_DELAY_UI;
		SharedPreferences app_preferences =getSharedPreferences("settings_data",MODE_PRIVATE); 

		String speed=app_preferences.getString("acclrate", "none");
		if(speed.equals("SENSOR_DELAY_UI"))
		{
			rate=SensorManager.SENSOR_DELAY_UI;
			Log.i("acclrate",rate+"");
		}
		else if (speed.equals("SENSOR_DELAY_NORMAL"))
		{
			rate=SensorManager.SENSOR_DELAY_NORMAL;
			Log.i("acclrate",rate+"");
		}
		else if	(speed.equals("SENSOR_DELAY_GAME"))
		{
			rate=SensorManager.SENSOR_DELAY_GAME;
			Log.i("acclrate",rate+"");
		}
		else if	(speed.equals("SENSOR_DELAY_FASTEST"))
			rate=SensorManager.SENSOR_DELAY_FASTEST;

		Log.i("rate:",rate+"");

		try{     
			SensorHandle = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
			AcclHandle = SensorHandle.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			SensorHandle.registerListener( AcclSensorListener , AcclHandle , rate);


			SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

			collectstate=app_preferences1.getInt("state", 0);

			System.out.println("saccl state:"+collectstate);




			int sampletime=CommonFunctions.SampleTime;
			Log.i("sample time",String.valueOf(sampletime));
			timer = new Timer();
			timer.schedule(new UnregisterTask(), sampletime*1000);

		}catch(Exception e){
			Log.i("",e.toString());
		}
	}

	class UnregisterTask extends TimerTask {
		public void run() {
			SensorHandle.unregisterListener(AcclSensorListener);
			timer.cancel(); 		


		}
	}


	private final SensorEventListener AcclSensorListener = new SensorEventListener() {



		public void onSensorChanged(SensorEvent event) {
			float X, Y, Z;
			X = Y = Z = 0.0f;
			JSONObject j=new JSONObject();
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
			{	
				X = event.values[0];

				Y = event.values[1];
				Z = event.values[2];
				try {

					long epoch = System.currentTimeMillis();
					j.put("X", X);
					j.put("Y", Y);
					j.put("Z", Z);
					j.put("timestamp", epoch);
					log=epoch+","+X+","+Y+","+Z;


				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

			collectstate=app_preferences1.getInt("state", 0);



			if(collectstate==1){



				SharedPreferences app_preferences =getSharedPreferences("settings_data",MODE_PRIVATE); 

				CommonFunctions.setType(app_preferences.getString("type", "none"));

				CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));
				log=log+","+label;

				synchronized(this){	    

					Logger.acclLogger(log);	



				}


				/*  

    			    	SharedPreferences retrieve = getSharedPreferences("uploadTimeAccl",Context.MODE_PRIVATE); 

			        	long t=retrieve.getLong("time", 0);

    			        long difference=System.currentTimeMillis()-t;

    			        if(difference>=CommonFunctions.UploadTime*1000)


    			        {
    			        	 SharedPreferences update = getSharedPreferences("uploadTimeAccl",Context.MODE_PRIVATE); 
           			        SharedPreferences.Editor editor1 = update.edit(); 
           					editor1.putLong("time",System.currentTimeMillis());
           					editor1.commit();

           					Upload upload=new Upload(c);
           					new Thread(upload).start();


           		//	upload.performTask.run();;	
           			//	 new Thread().start();     





			        }


				 */

			}
		}


		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	};



 void isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (AcclData.class.getName().equals(service.service.getClassName())) {
				Logger.logger("Accelerometer Service Stopped");
			}

			if (AlarmService1.class.getName().equals(service.service.getClassName())) {
				Logger.logger("Wifi Service Stopped");
			}
			if (CallAudio.class.getName().equals(service.service.getClassName())) {
				Logger.logger("Audio Service Stopped");
			}
			if (BatterActivity.class.getName().equals(service.service.getClassName())) {
				Logger.logger("Battery Service Stopped");
			}

		}

	}


	

}

