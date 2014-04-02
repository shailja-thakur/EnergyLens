package com.example.sensormanagement;


import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Binder;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import android.util.Log;

public class CallAudio extends Service {

	AudioData audio;

	int collectstate;
	static String Position="position";
	short[] recordedAudioBuffer,buffer,bufferval;
	int bufferRead;
	int sample;
	Context c=this;
	@Override

	public void onCreate() {



	}



	public CallAudio(){

	}

	@Override
	public void onDestroy() {

		CallAudio.this.stopSelf();
		audio.stopReader();

		Log.i("","stopped alarmservice3");

	}


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};




	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		System.out.println("Service Started onStartCommand");

		SharedPreferences app_preferences = getSharedPreferences("settings_data",Context.MODE_PRIVATE); 
		sample=app_preferences.getInt("sample", 0);

		SharedPreferences data1 =getApplicationContext().getSharedPreferences(Position,Context.MODE_PRIVATE);

		String label=data1.getString("audiolabel", "none");

		SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

		collectstate=app_preferences1.getInt("state", 0);


		CommonFunctions.setType(app_preferences.getString("type", "none"));

		CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));
		CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));

		recordAudio(label);





		return Service.START_NOT_STICKY;
	}



	private void recordAudio(String l){

		synchronized(this){



			SharedPreferences app_preferences = getSharedPreferences("settings_data",Context.MODE_PRIVATE); 

			String  type=app_preferences.getString("type", "");

			String  unique=app_preferences.getString("uniqueno", "");



			//    CommonFunctions.uploadTime(time);

			audio=new AudioData();
			audio.start(l,collectstate,sample,type,unique,c);



		}

		Log.i("recordaudio","reached end of recordaudio");

	}







}
