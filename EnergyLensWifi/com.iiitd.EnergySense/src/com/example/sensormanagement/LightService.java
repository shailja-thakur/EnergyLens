package com.example.sensormanagement;


import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.sensormanagement.AcclData.UnregisterTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Binder;

import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import android.util.Log;


public class LightService extends Service {
Context c;
public static String state;
public static String response;
Context Cnxt;
static final String Position="position";
public static String data;
SensorManager mySensorManager;
Sensor myLightSensor;
int collectstate;
String label;
Timer timer;
  @Override
  
  public void onCreate() {
	  
	SharedPreferences data =getApplicationContext().getSharedPreferences(Position,Context.MODE_PRIVATE);
	label=data.getString("wifilabel", "none");
    Thread thr = new Thread(null, mTask, "AlarmService_Service");
    thr.start();
	
  }
  
  public LightService(){
	
  }

  @Override
  public void onDestroy() {
    // Cancel the notification -- we use the same ID that we had used to
    // start it
    
    stopSelf();
    
    Log.i("","stopping alarmservice5");
    // Tell the user we stopped.
   
  }

  /**
   * The function that runs in our worker thread
   */
  Runnable mTask = new Runnable() {
    public void run() {
    	
        synchronized (mBinder) {
          try {
       
            runLightTask();
        		}

           catch (Exception e) {
          }
        }
         

      // Done with our work... stop the service!
      LightService.this.stopSelf();
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      //  Logger.logInfo("Service Started onStartCommand");
        return Service.START_NOT_STICKY;
    }
    
    private void runLightTask()
	{
	 
    	 
    	  mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
          myLightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
          
          mySensorManager.registerListener(LightSensorEventListener,
          			myLightSensor,
          SensorManager.SENSOR_DELAY_NORMAL);
          Log.i("","Registered  Light service ");
          
          int sampletime=CommonFunctions.SampleTime;
			Log.i("sample time",String.valueOf(sampletime));
			timer = new Timer();
			timer.schedule(new UnregisterTask(), sampletime*1000);
         
          
	}
    
    
    class UnregisterTask extends TimerTask {
		public void run() {
			mySensorManager.unregisterListener(LightSensorEventListener);
			timer.cancel(); 


		}
	}
	
    
    SensorEventListener LightSensorEventListener
    = new SensorEventListener(){

	
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			//System.out.println("on accuraacy changed light service");
			
		}

		
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			//System.out.println("on sensor changed light service");
			String log;
			log = "";
			
			if(event.sensor.getType()==Sensor.TYPE_LIGHT){
				
				
				//if(event.values[0]==0){
				long epoch = System.currentTimeMillis();
        		
        		 SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 
        		 System.out.println(log);
         		collectstate=app_preferences1.getInt("state", 0);
         		SharedPreferences app_preferences =getSharedPreferences("settings_data",MODE_PRIVATE); 

				CommonFunctions.setType(app_preferences.getString("type", "none"));

				CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));
         		if(collectstate==1){
         			
         			log = log + epoch + "," + event.values[0] + "," + label;
         			System.out.println(log);
         			Logger.lightLogger(log);
         		//}
				}
				
				
				
				
			}
		}
    };
    
    
    
   
  }
