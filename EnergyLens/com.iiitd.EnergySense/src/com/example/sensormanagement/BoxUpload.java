package com.example.sensormanagement;


/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.iiitd.EnegySense.R;


public class BoxUpload extends Service {
	private static final String TAG = "BoxUpload";


	final static private String APP_KEY = "ucgfioq154q1r6n";
	final static private String APP_SECRET = "45ivxkuvvczhh28";

	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;


	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";


	DropboxAPI<AndroidAuthSession> mApi;

	private boolean mLoggedIn;

	// Android widgets
	private Button mSubmit;
	private LinearLayout mDisplay;
	private Button mPhoto;
	private Button mRoulette;

	private ImageView mImage;
	Context c=this;
	private final String PHOTO_DIR = "/Accl_Data/";

	final static private int NEW_PICTURE = 1;
	private String mCameraFileName;
	WifiManager wifiManager;
	public void onCreate() {

		
		//   if (savedInstanceState != null) {
		//       mCameraFileName = savedInstanceState.getString("mCameraFileName");
		//   }

		// We create a new AuthSession so that we can use the Dropbox API.
		
			System.out.println("hello");
		
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		/*
		int timeout=10;

		while (timeout>0 && wifiManager.isWifiEnabled() == true) {

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeout-=1;

			Log.i("AudioData", "Audio reader failed to initialize");

		}
		*/
		//if (wifiManager.isWifiEnabled()){
		System.out.println("3");
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);
		setLoggedIn(mApi.getSession().isLinked());
		if (mLoggedIn) {
			//  logOut();
		} else {
			// Start the remote authentication
			
			mApi.getSession().startAuthentication(BoxUpload.this);

			//System.out.println("authenticating...");


		}

		
		//}
		//Toast.makeText(c, "uploading",Toast.LENGTH_SHORT).show();


		
	}

	@Override
	public void onDestroy() {


		stopSelf();

		Log.i("","stopped upload Service");


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//    	 SharedPreferences data =getApplicationContext().getSharedPreferences(Position,Context.MODE_PRIVATE);
		
		//if (wifiManager.isWifiEnabled() == true){
		//synchronized(this){
		/*
			wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			int timeout=10;

			while (timeout>0 && wifiManager.isWifiEnabled() == true) {

				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timeout-=1;

				Log.i("AudioData", "Audio reader failed to initialize");

			}
			*/
		//wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		//synchronized(this){
		/*
		if (!wifiManager.isWifiEnabled()) {
			System.out.println("9");
			wifiManager.setWifiEnabled(true);
		}
		*/
		AndroidAuthSession session = mApi.getSession();
		System.out.println("4");
		
		checkAppKeySetup();
		

		if (session.authenticationSuccessful()) {
			try {

				//Toast.makeText(getApplicationContext(), "authenticating", Toast.LENGTH_SHORT).show();
				// Mandatory call to complete the auth
				session.finishAuthentication();
				System.out.println("5");
				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);
			} catch (IllegalStateException e) {
				showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
		upload();

		// Display the proper UI state if logged in or not
		//  setLoggedIn(mApi.getSession().isLinked());
		//}
		//}
	    
		//}
		return START_NOT_STICKY;
	    
	}

	public void upload(){
		List<String> myList;

		File file;

		if(Logger.Path.exists()){
			//    File directory = Environment.getExternalStorageDirectory();
			//    file = new File( directory + "/Test" );
			File list[] = Logger.Path.listFiles();

			for( int i=0; i< list.length; i++)
			{
				synchronized(this){
					System.out.println(list[i].getAbsolutePath());
					//System.out.println("uploading");
					SharedPreferences app_preferences =getSharedPreferences("settings_data",MODE_PRIVATE); 

					CommonFunctions.setType(app_preferences.getString("type", "none"));

					CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", ""));

					//File outFile=new File(Logger.Path+File.separator+"Accl_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
					File file1=new File(list[i].getAbsolutePath());
					final String DIR = "/"+Logger.Path.getName()+"-"+CommonFunctions.UniqueNo+"/";

					UploadPicture upload = new UploadPicture(this, mApi, DIR, file1);
					upload.execute(); 
					
					
				}
				
			}
			

		}

	}










	private void logOut() {
		// Remove credentials from the session
		System.out.println("6");
		mApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		setLoggedIn(false);
	}

	/**
	 * Convenience function to change UI state based on being logged in
	 */
	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
		if (loggedIn) {
			//	mSubmit.setText("Unlink from Dropbox");
			//  mDisplay.setVisibility(View.VISIBLE);
			System.out.println("7");
		} else {
			//	mSubmit.setText("Link with Dropbox");
			//  mDisplay.setVisibility(View.GONE);
			//  mImage.setImageDrawable(null);
		}
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (APP_KEY.startsWith("CHANGE") ||
				APP_SECRET.startsWith("CHANGE")) {
			showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");

			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			showToast("URL scheme in your app's " +
					"manifest is not set up correctly. You should have a " +
					"com.dropbox.client2.android.AuthActivity with the " +
					"scheme: " + scheme);

		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).
	 *
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {

			System.out.println("key not null");
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			System.out.println("key null");
			return null;
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
