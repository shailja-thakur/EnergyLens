package com.example.sensormanagement;



import static android.telephony.NeighboringCellInfo.UNKNOWN_RSSI;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;




import android.content.IntentFilter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;

import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import android.widget.Toast;
import com.iiitd.EnegySense.R;


public class WifiCellAcc  {
	File FileHandle,FileHandle1;
	FileOutputStream AcclWriter,AcclWriter1;
	public static int Sample;
	public static int wake;
	public static int wake_upload;
	public static String state;
	public static String response;
	public static int collect;
	public static int Sound;
	public static String valueAtPosition;
	ArrayList<Integer> value=new ArrayList<Integer>();
	public static ArrayList<Integer> finalvalue=new ArrayList<Integer>();
	public static ArrayList<Integer> finalvalue1=new ArrayList<Integer>();
	static DataOutputStream SDWriter,SDWriter1;
	String ssid;
	int lineCount = 0;
	Timer timer;
	Context Cnxt;
	static File myFile;
	int k,count;
	public static String data;
	WifiManager mainWifi;
	WifiReceiver receiverWifi;

	TelephonyManager telephonyManager;
	public static   List<ScanResult> wifiList;
	State mobile;
	StringBuilder sb = new StringBuilder();

	SensorManager SensorHandle;
	Sensor AcclHandle;

	ArrayList<Integer> values = new ArrayList<Integer>();
	public static	ArrayList<String> wifi = new ArrayList<String>();

	JSONObject ob=new JSONObject();
	SensorManager mySensorManager;
	Sensor myProximitySensor;


	public WifiCellAcc(Context Cnxt) {
		this.Cnxt = Cnxt;


	}


	public static void getSampleWakeTime(int w,int s, int soundv){

		wake=w;
		Sample=s;
		Sound=soundv;

	}


	public static void getUploadTime(int wakev) {
		// TODO Auto-generated method stub
		wake_upload=wakev;



	}

	public static void getData(ArrayList<Integer> value){

		if(finalvalue1.size()==15){
			finalvalue1.clear();

		}

		WifiCellAcc.finalvalue1=value;
	
		System.out.println("getdata values:\n");
		System.out.println(WifiCellAcc.finalvalue1);

	}

	public void runWiFiTask()
	{

		mainWifi = (WifiManager) Cnxt.getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver();

		Cnxt.getApplicationContext().registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifi.startScan();
		Log.i("","Registered broadcast WiFi service and starting WiFi scan");


	}



	public void runCellTask() {
		telephonyManager = (TelephonyManager)Cnxt.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(rssiListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS|PhoneStateListener.LISTEN_CELL_LOCATION|PhoneStateListener.LISTEN_DATA_CONNECTION_STATE|PhoneStateListener.LISTEN_SERVICE_STATE);



	}




	PhoneStateListener rssiListener= new PhoneStateListener(){

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength)
		{
			super.onSignalStrengthsChanged(signalStrength);

			int strength1;


			if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
				int asu = signalStrength.getGsmSignalStrength();

				strength1=(asu > 0) && (asu != UNKNOWN_RSSI) ? (2 * asu - 113) : asu;


				String  networkOperator = telephonyManager.getNetworkOperator();
				String  tmcc = networkOperator.substring(0, 3);
				String  tmnc = networkOperator.substring(3);

				try {
					ob.put("rssi", strength1);
					ob.put("mcc", tmcc);
					ob.put("mnc",tmnc);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
				strength1=signalStrength.getCdmaDbm() < signalStrength.getEvdoDbm() ? signalStrength.getCdmaDbm() : signalStrength.getEvdoDbm();

				try {
					ob.put("rssi", strength1);
				} catch (JSONException e) {

					e.printStackTrace();
				}

			} 

			log_cell(ob);
			super.onSignalStrengthsChanged(signalStrength);
		}

		public void onSignalStrengthChanged(int asu) {
			// add cdma support, convert signal from gsm

			int strength1;
			if ((telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) || (telephonyManager.getPhoneType() == telephonyManager.PHONE_TYPE_CDMA)) 
			{
				strength1=(asu > 0) && (asu !=UNKNOWN_RSSI) ? (2 * asu - 113) : asu;
				try {
					ob.put("rssi", strength1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}


		@Override
		public void onCellLocationChanged(CellLocation location)
		{
			super.onCellLocationChanged(location);
			String networkOperator,tmcc,tmnc;
			int textcid,textlac;
			GsmCellLocation cellLocation = (GsmCellLocation) location;
			textcid = cellLocation.getCid();
			textlac = cellLocation.getLac();

			try {
				ob.put("cellid", textcid);
				ob.put("lac", textlac);


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}







	};










	public  void log_cell(JSONObject ob){




		try {
			ob.put("networkstatus", "UNKNOWN");
			ob.put("neighcellids", "");
			ob.put("networktype", String.valueOf(telephonyManager.getNetworkType()));

			long epoch = System.currentTimeMillis();
			ob.put("timestamp", epoch);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//    }
		//    Log.i("",ob.toString());
		myFile = new File(Environment.getExternalStorageDirectory()
				+File.separator
				+"mydir" //folder name
				+File.separator
				+"mobiShareLocationLog.txt"); //file name

		if(!myFile.exists()){

			try {
				myFile.createNewFile();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}


		try {

			BufferedWriter buf = new BufferedWriter(new FileWriter(myFile, true)); 

			buf.append(ob.toString());


			buf.newLine();
			buf.close();
			//    fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}


	public void Start(int s){


		try{     
			SensorHandle = (SensorManager) Cnxt.getSystemService(Context.SENSOR_SERVICE);
			AcclHandle = SensorHandle.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			SensorHandle.registerListener( AcclSensorListener , AcclHandle , SensorManager.SENSOR_DELAY_NORMAL );

			try
			{	
				File StorageDir = new File(Environment.getExternalStorageDirectory(), "mydir");

				// Create the storage directory if it does not exist
				if (! StorageDir.exists()){
					if (! StorageDir.mkdirs()){
						Log.d("MobiShare", "Failed to create Directory !");
						return;
					}
				}

				if( StorageDir.canWrite() )
				{	
					FileHandle = new File(StorageDir, "Accelerometer.acl");
					AcclWriter = new FileOutputStream(FileHandle,true);
					SDWriter = new DataOutputStream(AcclWriter);
				}
			}
			catch( IOException Error){}



			int sample=s;
			int SECONDS = sample;


			timer = new Timer();
			timer.schedule(new UnregisterTask(), SECONDS*1000);

		}catch(Exception e){
			Log.i("",e.toString());
		}
	}

	class UnregisterTask extends TimerTask {
		public void run() {
			SensorHandle.unregisterListener(AcclSensorListener);
			try
			{
				SDWriter.close();
				AcclWriter.close();
			}
			catch(IOException Error){}
			timer.cancel(); //Terminate the timer thread
		}
	}


	SensorEventListener AcclSensorListener = new SensorEventListener() {

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
					j.put("X", X);
					j.put("Y", Y);
					j.put("Z", Z);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try
			{WifiCellAcc.SDWriter.writeLong(System.currentTimeMillis());
			WifiCellAcc.SDWriter.writeFloat(X);
			WifiCellAcc.SDWriter.writeFloat(Y);
			WifiCellAcc.SDWriter.writeFloat(Z);
			WifiCellAcc.SDWriter.flush();


			System.out.println(X);
			System.out.println(Y);
			System.out.println(Z);
			}
			catch( Exception Error){}
			log_acc(j);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	};



	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			int p;

			JSONObject ob = new JSONObject();
			StringBuilder sb = new StringBuilder();
			int info = mainWifi.getConnectionInfo().getRssi();

			//  System.out.println(info);

			wifiList = mainWifi.getScanResults();
			
			for(ScanResult result:wifiList){

				
				try {

					long epoch = System.currentTimeMillis();
					ob.put("timestamp", epoch);
					ob.put("bssid",result.BSSID);

					ob.put("ssid",result.SSID);


					wifi.add(result.SSID);


					ob.put("rssi", result.level);
					//	Log.i("Wifi",ob.toString());


					log_wifi(ob);


					sb.append( epoch);
					sb.append(result.BSSID);

					sb.append(result.SSID);

					sb.append( result.level);

					


				} catch (Exception e) {

					e.printStackTrace();
				}

			}


		}}
	
	public  void log_wifi(JSONObject ob){
		myFile = new File(Environment.getExternalStorageDirectory()
				+File.separator
				+"mydir" //folder name
				+File.separator
				+"mobiShareWifiLog.txt"); 

		if(!myFile.exists()){

			try {
				myFile.createNewFile();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}


		try {

			BufferedWriter buf = new BufferedWriter(new FileWriter(myFile, true)); 

			buf.append(ob.toString());


			buf.newLine();
			buf.close();
			//    fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}



	public  void log_acc(JSONObject ob){
		myFile = new File(Environment.getExternalStorageDirectory()
				+File.separator
				+"mydir" //folder name
				+File.separator
				+"mobiShareAccLog.txt"); //file name

		if(!myFile.exists()){

			try {
				myFile.createNewFile();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		try {

			BufferedWriter buf = new BufferedWriter(new FileWriter(myFile, true)); 

			buf.append(ob.toString());


			buf.newLine();
			buf.close();
			//    fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}







	public void runProximityTask()
	{


		mySensorManager = (SensorManager)Cnxt.getSystemService(Context.SENSOR_SERVICE);
		myProximitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);



		mySensorManager.registerListener(proximitySensorEventListener,
				myProximitySensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		Log.i("","Registered Proximity sensor");


	}




	SensorEventListener proximitySensorEventListener
	= new SensorEventListener(){


		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}


		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			JSONObject ob = new JSONObject();
			if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){


				if(event.values[0]==0){
					long epoch = System.currentTimeMillis();
					try {
						ob.put("timestamp", epoch);
					} catch (JSONException e) {

						e.printStackTrace();
					}
					log_prox(ob);
				}




			}
		}
	};



	private  void log_prox(JSONObject ob){
		myFile = new File(Environment.getExternalStorageDirectory()
				+File.separator
				+"mydir" //folder name
				+File.separator
				+"ProximityLog.txt"); 
		System.out.println(myFile);

		if(!myFile.exists()){

			try {





				myFile.createNewFile();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		else{
			try {

				BufferedWriter buf = new BufferedWriter(new FileWriter(myFile, true)); 

				buf.append(ob.toString());


				buf.newLine();
				buf.close();
				//    fOut.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




		}

	}






	public void runLightTask()
	{


		mySensorManager = (SensorManager)Cnxt.getSystemService(Context.SENSOR_SERVICE);
		myProximitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);



		mySensorManager.registerListener(LightSensorEventListener,
				myProximitySensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		Log.i("","Registered  Light service ");


	}




	SensorEventListener LightSensorEventListener
	= new SensorEventListener(){


		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}


		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			JSONObject ob = new JSONObject();
			if(event.sensor.getType()==Sensor.TYPE_LIGHT){


				if(event.values[0]==0){
					long epoch = System.currentTimeMillis();
					try {
						ob.put("timestamp", epoch);
					} catch (JSONException e) {

						e.printStackTrace();
					}
					log_light(ob);
				}




			}
		}
	};



	public  void log_light(JSONObject ob){
		myFile = new File(Environment.getExternalStorageDirectory()
				+File.separator
				+"mydir" //folder name
				+File.separator
				+"LightLog.txt"); 
		System.out.println(myFile);

		if(!myFile.exists()){

			try {





				myFile.createNewFile();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		else{
			try {

				BufferedWriter buf = new BufferedWriter(new FileWriter(myFile, true)); 

				buf.append(ob.toString());


				buf.newLine();
				buf.close();
				//    fOut.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




		}

	}





	public static void getResponse(String res) {
		// TODO Auto-generated method stub

		response=res;

	}
	public static void putState(String string) {
		state=string;

	}

	public static void getvalueAtPosition(String string){
		valueAtPosition=string;

	}

}

