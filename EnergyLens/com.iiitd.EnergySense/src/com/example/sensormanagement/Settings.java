package com.example.sensormanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import com.iiitd.EnegySense.R;

public class Settings extends PreferenceActivity{


	String TAG="Settings";	
	Context c=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		//	UpdatePreference update=new UpdatePreference(this);

		UpdatePreference update=new UpdatePreference();
		update.setParameters();

	}


	@Override
	protected void onStart() {
		//the activity is become visible.
		super.onStart();
		UpdatePreference update=new UpdatePreference();
		update.setParameters();
	}
	@Override
	protected void onPause() {
		super.onPause();
		UpdatePreference update=new UpdatePreference();
		update.setParameters();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		UpdatePreference update=new UpdatePreference();
		update.setParameters();
	}
	@Override
	protected void onStop() {
		//the activity is no longer visible.
		super.onStop();
		UpdatePreference update=new UpdatePreference();
		update.setParameters();
	}
	@Override
	protected void onDestroy() {
		//The activity about to be destroyed.
		super.onDestroy();
		UpdatePreference update=new UpdatePreference();
		update.setParameters();
	}

	public class UpdatePreference{

		public void setParameters(){
			System.out.println("update Prepference called");
			SharedPreferences prefs =
					PreferenceManager.getDefaultSharedPreferences(c);




			SharedPreferences app_preferences = getSharedPreferences("settings_data",c.MODE_PRIVATE); 



			SharedPreferences.Editor editor = app_preferences.edit(); 
			String type="none";
			try {
				String Type = prefs.getString("type", "none");
				type=Type;
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad type value");
			}

			Log.i(TAG, "Prefs: type " + type);

			editor.putString("type",type );

			CommonFunctions.setType(app_preferences.getString("type", "none"));





			String uniqueno="";
			try {
				String Unique= prefs.getString("uniqueno", "");
				uniqueno=Unique;
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad unique no");
			}
			Log.i(TAG, "Prefs: unique no " + uniqueno);
			editor.putString("uniqueno",uniqueno);
			CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));



			int wakeup=30;
			try {
				int wake = prefs.getInt("wakeup", 30);
				wakeup=wake;
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad Wakeup time");
			}
			Log.i(TAG, "Prefs: Wakeup " + wakeup);
			editor.putInt("wakeup", wakeup);
			CommonFunctions.setWakeTime(app_preferences.getInt("wakeup", 1));




			int sample=3;
			try {
				int stime = prefs.getInt("sample", 3);
				sample=stime;

			} catch (Exception e) {
				Log.e(TAG, "Pref: bad Sample time");
			}
			Log.i(TAG, "Prefs: sampletime " + sample);

			editor.putInt("sample", sample);
			CommonFunctions.setSampleTime(app_preferences.getInt("sample", 3)); 




			int upload=50;
			try {
				int  utime = prefs.getInt("upload", 50);
				upload=utime;
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad upload time");
			} 
			Log.i(TAG, "Prefs: Upload time " + upload);
			editor.putInt("upload", upload);
			CommonFunctions.setUploadTime(app_preferences.getInt("upload", 50));





			String networkmode="Enable Wifi";
			try {
				String nmode = prefs.getString("networkmode", "Wifi");
				networkmode=nmode;
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad network mode");
			}
			Log.i(TAG, "Prefs: networkmode " + networkmode);
			editor.putString("networkmode", networkmode);
			CommonFunctions.setNetworkMode(app_preferences.getString("networkmode", "Wifi"));



			String accelerometer="Stationary";
			try {
				String accl = prefs.getString("accelerometer", null);
				accelerometer=accl;
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad accelerometer label");
			}



			String acclrate="SENSOR_DELAY_NORMAL";
			try {
				acclrate = prefs.getString("acclrate", "SENSOR_DELAY_NORMAL");

			} catch (Exception e) {
				Log.e(TAG, "Pref: bad accelerometer rate(speed)");
			}
			Log.i(TAG, "Prefs: accelerometer speed " + acclrate);
			editor.putString("acclrate", acclrate);
			CommonFunctions.setAcclRate(app_preferences.getString("acclrate", "SENSOR_DELAY_NORMAL"));


			int sampleRate = 8000;
			try {
				String srate = prefs.getString("sampleRate", null);
				sampleRate = Integer.valueOf(srate);
			} catch (Exception e) {
				Log.e(TAG, "Pref: bad sampleRate");
			}
			if (sampleRate < 8000)
				sampleRate = 8000;
			Log.i(TAG, "Prefs: sampleRate " + sampleRate);

			CommonFunctions.setSampleRate(sampleRate);
			editor.putInt("samplerate",sampleRate );

			editor.putString("audiolabel", "none");
			editor.putString("wifilabel", "none");

			editor.putString("accllabel", "none");




			editor.commit();

		}
	}



}



