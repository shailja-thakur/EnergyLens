package com.example.sensormanagement;





import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Set;


import android.annotation.TargetApi;
import android.app.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

import android.graphics.drawable.LevelListDrawable;

import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;

import android.view.InflateException;

import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iiitd.EnergySenseWifi.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;


public class MainActivity extends Activity implements OnItemClickListener {
	private NumberFormat numberFormat;
	int k=0;
	boolean delete=false;
	SharedPreferences collect;
	private static final String TAG = "App ";
	Date date;
	public static final String[] titles = new String[] { "Accelerometer",
	"Wifi" };

	public static final String[] titles_Label = new String[] { "Accelerometer Label",
		"Audio Label","Wifi Label"};

	public static final String[] descriptions = new String[] {
		"It gives location coordinates with respect to gravity",
		"It gives signal strength of the neighbouring APs along with their MAC"
	};
	static final String Position="position";


	public static final Integer[] images = { R.drawable.accl0,
		R.drawable.wifi0};

	ListView listView,listView1;
	List<RowItem> rowItems;
	List<RowItemLabel> rowItemsLabel;


	Set<String> audio = new HashSet<String>();
	private PowerManager.WakeLock wakeLock = null;
	String[] Type = {"1BHK", "3BHK","room"};
	String[] Labels_3 = {"Kitchen","Dining Room","Bedroom1","Bedroom2","Bedroom3","Study","None"};
	String[] Labels_1 = {"Kitchen","Dining Room","Bedroom","None"};
	String[] dorms = {"Corridor","Inside","Outside","None"};

	String[] audio_label={"Fan","AC","Microwave","TV","Computer","Printer","Washing Machine","Fan+AC","None"};
	PendingIntent pendingIntent1,pendingIntent2,pendingIntent3,pendingIntent4,pendingIntent5,pendingIntent6;
	AlarmManager alarmManager1,alarmManager2,alarmManager3,alarmManager4,alarmManager5,alarmManager6;

	String Annotation="Labels";
	String typeTitle="Type";
	/** Called when the activity is first created. */
	private int[] ids = new int[] { R.drawable.start3, R.drawable.pause3 };  
	TextView batteryLevel;
	TextView charging,collectstate;
	EditText label;
	BroadcastReceiver batteryLevelReceiver;
	boolean isCharging;
	Context context=this;
	int level;
	Button log;
	int state;
	String get_label;
	ImageView imageView;
	ImageButton btn;
	Intent myIntent,myIntent1,myIntent2,myIntent3,myIntent4, myIntent5;
	
	final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	AppKeyPair appKeys = new AppKeyPair("ucgfioq154q1r6n", "45ivxkuvvczhh28");
	AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
	DropboxAPI<AndroidAuthSession>  mDBApi = new DropboxAPI<AndroidAuthSession>(session);


	@Override
	public void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.list_main);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.windows_title);

		

		updatePreferences();
		Log.i("performuploadtask","");

		//  System.out.println(date.getDate());
		String currentTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());



		System.out.println(currentTimeString);
		//  this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		registerReceiver(mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));


		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < titles.length; i++) {
			RowItem item = new RowItem(images[i], titles[i], descriptions[i]);
			rowItems.add(item);
		}


		listView = (ListView) findViewById(R.id.list);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this,
				R.layout.list_item, rowItems);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);


		rowItemsLabel = new ArrayList<RowItemLabel>();
		for (int i = 0; i < titles_Label.length; i++) {
			RowItemLabel item = new RowItemLabel( titles_Label[i]);
			rowItemsLabel.add(item);
		}


		listView1 = (ListView) findViewById(R.id.list2);
		CustomListViewAdapterLabel adapter1 = new CustomListViewAdapterLabel(this,
				R.layout.list_label, rowItemsLabel);



		View headerView1 = View.inflate(this, R.layout.header_label, null);
		listView1.addHeaderView(headerView1);

		View footer1 = View.inflate(this, R.layout.footer_layout, null);
		listView1.addFooterView(footer1);

		listView1.setAdapter(adapter1);
		listView1.setOnItemClickListener(this);






		listView1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				System.out.println(arg2);
				if(arg2==1){

					Accl_Label();


				}
				else if(arg2==2){

					Audio_Label();

				}
				else if(arg2==3){

					Wifi_Label();

				}

			}
		});









		imageView = (ImageView) findViewById(R.id.button);  

		BitmapFactory.Options opts = new BitmapFactory.Options();  
		opts.inJustDecodeBounds = true;  

		opts.inSampleSize = computeSampleSize(opts, -1, 500 * 500);  
		opts.inJustDecodeBounds = false;  

		LevelListDrawable levelListDrawable = new LevelListDrawable();
		try {  
			for (int i = 0; i < ids.length; i++) {
				Bitmap  bmp = BitmapFactory.decodeResource(getResources(),ids[i], opts);  
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);  
				levelListDrawable.addLevel(i, i+1, bitmapDrawable); 
			}  
			imageView.setImageDrawable(levelListDrawable); 
		} catch (OutOfMemoryError err) {  
			err.printStackTrace();  
		}  


		collect = getSharedPreferences("CollectState",MODE_PRIVATE); 

		if(CommonFunctions.UniqueNo.equals(null) || CommonFunctions.UniqueNo.length() == 0){
			Toast.makeText(getApplicationContext(), "Enter apartment/room no and type in settings",Toast.LENGTH_SHORT).show();
			imageView.getDrawable().setLevel(1);	

		}
		
		/*      else if(collect.getInt("state",0)==1){ 
		            imageView.getDrawable().setLevel(0); 
		      }
		      else if(collect.getInt("state",0)==0){

		    	  imageView.getDrawable().setLevel(1);
		        }     	

		 */
		SharedPreferences btnstate = getSharedPreferences("buttonstate",MODE_PRIVATE); 


		imageView.setImageLevel(btnstate.getInt("state", 1));

		btn = (ImageButton) findViewById(R.id.button);  
		btn.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {  

				updatePreferences();

				int i = imageView.getDrawable().getLevel();  
				System.out.println("position:"+i);

				System.out.println("unique no:"+CommonFunctions.UniqueNo.length());
			
				
				if(CommonFunctions.UniqueNo.equals(null) || CommonFunctions.UniqueNo.length() == 0 || CommonFunctions.UniqueNo.equals("none")){
					Toast.makeText(getApplicationContext(), "Enter apartment/room no and type in settings",Toast.LENGTH_SHORT).show();
				}
				
				else {

					if (i >=2) 

						i = 0;  

					if(i==1){
						SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

						SharedPreferences.Editor editor1 = app_preferences1.edit();
						editor1.putInt("state",1 );
						//		editor1.putString("label", label.getText().toString());
						editor1.commit(); 	

						collect = getSharedPreferences("CollectState",MODE_PRIVATE); 
						if((collect.getInt("state",0)==1)){


							System.out.println("unique no:"+CommonFunctions.UniqueNo);
							//  	 imageView.getDrawable().setLevel(2);
							//  	new PerformTask(context).execute("");
							//    new PerformUploadTask(context).execute("");
							
							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(System.currentTimeMillis());
							calendar.add(Calendar.SECOND, 10);

							long interval = CommonFunctions.WakeTime* 1000; 

							/*
							// Alarm for Microphone 
							
							myIntent = new Intent( context,com.example.sensormanagement.CallAudio.class);
							//		startService(myIntent);

							pendingIntent1 = PendingIntent.getService(context,
									12345, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
							//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

							AlarmManager alarmManager1= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

							

							alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), interval, pendingIntent1);
							*/
							
							// ALarm for Wifi
							
							myIntent1 = new Intent( context,com.example.sensormanagement.AlarmService1.class);
							//	startService(myIntent);
						
							pendingIntent2 = PendingIntent.getService(context,
									12346, myIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
							//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

							AlarmManager alarmManager2= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

							alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), interval, pendingIntent2);
							
							/*
							
							// Alarm for Light Service


							myIntent4 = new Intent( context,com.example.sensormanagement.LightService.class);
							//       		 	startService(myIntent);

							pendingIntent5= PendingIntent.getService(context,
									12348, myIntent4, PendingIntent.FLAG_CANCEL_CURRENT);
							//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

							AlarmManager alarmManager5= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

							alarmManager5.setRepeating(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), interval, pendingIntent5);
							
							
							
							// Alarm for Magnetometer Service
							
							myIntent5 = new Intent( context,com.example.sensormanagement.MagService.class);
							//       		 	startService(myIntent);

							pendingIntent6= PendingIntent.getService(context,
									12350, myIntent5, PendingIntent.FLAG_CANCEL_CURRENT);
							//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

							AlarmManager alarmManager6= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

							alarmManager6.setRepeating(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), interval, pendingIntent6);

							

							*/
							/*

							myIntent2 = new Intent( context,com.example.sensormanagement.AcclData.class);
							//       		 	startService(myIntent);

							pendingIntent3= PendingIntent.getService(context,
									12347, myIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
							//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

							AlarmManager alarmManager3= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

							Calendar calendar2 = Calendar.getInstance();
							calendar2.setTimeInMillis(System.currentTimeMillis());
							calendar2.add(Calendar.SECOND, 10);

							//long interval = CommonFunctions.WakeTime* 1000; 

							alarmManager3.setRepeating(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), interval, pendingIntent3);

							*/
							


							/*
							myIntent3 = new Intent( context,com.example.sensormanagement.BatterActivity.class);
							//    		 	startService(myIntent);

							pendingIntent4= PendingIntent.getService(context,
									12349, myIntent3, PendingIntent.FLAG_CANCEL_CURRENT);
							//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

							AlarmManager alarmManager4= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

							Calendar calendar3 = Calendar.getInstance();
							calendar3.setTimeInMillis(System.currentTimeMillis());
							calendar3.add(Calendar.SECOND, 10);

							//long interval = CommonFunctions.WakeTime* 1000; 

							alarmManager4.setRepeating(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), interval, pendingIntent4);

							*/




						}



						Toast.makeText(getApplicationContext(), "Accl,Wifi,Audio data collection started",Toast.LENGTH_SHORT).show();

					}
					else if(i==0){
						SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

						SharedPreferences.Editor editor1 = app_preferences1.edit();
						editor1.putInt("state",0 );
						Toast.makeText(getApplicationContext(), "Stopped",Toast.LENGTH_SHORT).show();
						editor1.commit(); 	

						/*
						
						myIntent = new Intent( context,com.example.sensormanagement.CallAudio.class);

						AlarmManager alarmManager1= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
						myIntent = new Intent( context,com.example.sensormanagement.CallAudio.class);
						pendingIntent1 = PendingIntent.getService(context,
								12345, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						// pendingIntent1.cancel();
						//stopService(myIntent);
						alarmManager1.cancel(pendingIntent1);

						
						
						*/
						AlarmManager alarmManager2= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
						myIntent1 = new Intent( context,com.example.sensormanagement.AlarmService1.class);
						
						pendingIntent2 = PendingIntent.getService(context,
								12346, myIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
						// pendingIntent2.cancel();
						alarmManager2.cancel(pendingIntent2);
						
						/*
						
						AlarmManager alarmManager5= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
						myIntent4 = new Intent( context,com.example.sensormanagement.LightService.class);
						
						pendingIntent5 = PendingIntent.getService(context,
								12348, myIntent4, PendingIntent.FLAG_CANCEL_CURRENT);
						// pendingIntent2.cancel();
						alarmManager5.cancel(pendingIntent5);
						
						
						
						AlarmManager alarmManager6= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
						myIntent5 = new Intent( context,com.example.sensormanagement.MagService.class);
						
						pendingIntent6 = PendingIntent.getService(context,
								12350, myIntent5, PendingIntent.FLAG_CANCEL_CURRENT);
						// pendingIntent2.cancel();
						alarmManager6.cancel(pendingIntent6);
						*/
						
						/*
						AlarmManager alarmManager3= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
						myIntent2 = new Intent( context,com.example.sensormanagement.AcclData.class);
						pendingIntent3 = PendingIntent.getService(context,
								12347, myIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
						// pendingIntent3.cancel();
						alarmManager3.cancel(pendingIntent3);
						*/

						
						/*

						AlarmManager alarmManager4= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
						myIntent3 = new Intent( context,com.example.sensormanagement.BatterActivity.class);
						pendingIntent4 = PendingIntent.getService(context,
								12349, myIntent3, PendingIntent.FLAG_CANCEL_CURRENT);
						// pendingIntent3.cancel();
						alarmManager4.cancel(pendingIntent4);


						*/



					}

					imageView.getDrawable().setLevel(++i);  

					SharedPreferences app_preferences1 = getSharedPreferences("buttonstate",MODE_PRIVATE); 

					SharedPreferences.Editor editor1 = app_preferences1.edit();
					editor1.putInt("state", i);
					editor1.commit();
				}
			}  
		});  




	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		updatePreferences();
		//isMyServiceRunning();
		System.out.println("unique no:"+CommonFunctions.UniqueNo.length());
		if(CommonFunctions.UniqueNo.equals(null) || CommonFunctions.UniqueNo.length() == 0 || CommonFunctions.UniqueNo.equals("none")){
			Toast.makeText(getApplicationContext(), "Enter apartment/room no and type in settings",Toast.LENGTH_SHORT).show();
			imageView.getDrawable().setLevel(1);

		}

		else if(collect.getInt("state",0)==1){ 
			imageView.getDrawable().setLevel(2); 
			//   new PerformTask(context).execute("");
			
		//	MyReceiver m=new MyReceiver();
		//	getApplicationContext().registerReceiver(m, null);
			
			
			
		}





		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// Required to complete auth, sets the access token on the session
				mDBApi.getSession().finishAuthentication();

				AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}

		



	}

	@Override
	protected void onStart() {
		//the activity is become visible.
		super.onStart();
		updatePreferences();

		System.out.println("unique no:"+CommonFunctions.UniqueNo.length());
		if(CommonFunctions.UniqueNo.equals(null) || CommonFunctions.UniqueNo.length() == 0 || CommonFunctions.UniqueNo.equals("none")){
			Toast.makeText(getApplicationContext(), "Enter apartment/room no and type in settings",Toast.LENGTH_SHORT).show();
			imageView.getDrawable().setLevel(1);

		}



		else if(collect.getInt("state",0)==1){ 
			SharedPreferences btnstate = getSharedPreferences("buttonstate",MODE_PRIVATE); 


			imageView.setImageLevel(btnstate.getInt("state", 1));

		}
	}
	
	
	@TargetApi(14)
	@Override
	protected void onStop() {
		//the activity is become visible.
		super.onStop();
		
		//onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
		
	}


	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println(position);

		if(position==0){
			Intent intent = new Intent(this, AccelerometerActivity.class);
			startActivity(intent);

		}
		else if(position==1){
			Intent intent = new Intent(this, WiFiActivity.class);
			startActivity(intent);
		}
		/*    else if(position==2){
        	Intent intent = new Intent(this, SoundActivity.class);
			startActivity(intent);
        }
		 */
	}



	





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// We must call through to the base implementation.
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		setMenuBackground();
		return true;
	}

	protected void setMenuBackground() {
		getLayoutInflater().setFactory(new Factory() {


			public View onCreateView(final String name, final Context context,
					final AttributeSet attrs) {

				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {

					try { // Ask our inflater to create the view
						final LayoutInflater f = getLayoutInflater();
						final View[] view = new View[1];
						try {
							view[0] = f.createView(name, null, attrs);

						} catch (InflateException e) {
							hackAndroid23(name, attrs, f, view);
						}
						// Kind of apply our own background
						new Handler().post(new Runnable() {
							public void run() {
								view[0].setBackgroundColor(Color.WHITE);
								((TextView) view[0]).setTextColor(0xff355689);
							}
						});
						return view[0];
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {

					}
				}
				return null;
			}
		});
	}

	static void hackAndroid23(final String name,
			final android.util.AttributeSet attrs, final LayoutInflater f,
			final View[] view) {

		try {
			f.inflate(new XmlPullParser() {

				public int next() throws XmlPullParserException, IOException {
					try {
						view[0] = (TextView) f.createView(name, null, attrs);

					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {
					}
					throw new XmlPullParserException("exit");
				}

				public void defineEntityReplacementText(String arg0, String arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public int getAttributeCount() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getAttributeName(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeNamespace(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributePrefix(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeType(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeValue(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeValue(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getColumnNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				public int getDepth() {
					// TODO Auto-generated method stub
					return 0;
				}

				public int getEventType() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				public boolean getFeature(String arg0) {
					// TODO Auto-generated method stub
					return false;
				}

				public String getInputEncoding() {
					// TODO Auto-generated method stub
					return null;
				}

				public int getLineNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getName() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespace() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespace(String arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getNamespaceCount(int arg0)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getNamespacePrefix(int arg0)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespaceUri(int arg0)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPositionDescription() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPrefix() {
					// TODO Auto-generated method stub
					return null;
				}

				public Object getProperty(String arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getText() {
					// TODO Auto-generated method stub
					return null;
				}

				public char[] getTextCharacters(int[] arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean isAttributeDefault(int arg0) {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isEmptyElementTag()
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isWhitespace() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				public int nextTag() throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String nextText() throws XmlPullParserException,
				IOException {
					// TODO Auto-generated method stub
					return null;
				}

				public int nextToken() throws XmlPullParserException,
				IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				public void require(int arg0, String arg1, String arg2)
						throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub

				}

				public void setFeature(String arg0, boolean arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public void setInput(Reader arg0) throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public void setInput(InputStream arg0, String arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public void setProperty(String arg0, Object arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}
			}, null, false);
		} catch (InflateException e1) {
			// "exit" ignored
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {


		case R.id.settings:


		{
			Intent intent = new Intent(MainActivity.this,
					Settings.class);

			startActivity(intent);
			updatePreferences();






		}

		break;




		case R.id.upload:
		{
			//	Intent u=new Intent(this,DropBoxUpload.class);
			//	startActivity(u);

			SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

			SharedPreferences.Editor editor1 = app_preferences1.edit();
			
			Toast.makeText(getApplicationContext(), "Upload started", Toast.LENGTH_SHORT).show();
			myIntent4 = new Intent( context,com.example.sensormanagement.BoxUpload.class);
			//	startService(myIntent3);



			pendingIntent5= PendingIntent.getService(context,
					12350, myIntent4, PendingIntent.FLAG_CANCEL_CURRENT);
			//pendingIntent1= PendingIntent.getService(context,1, myIntent,0);

			AlarmManager alarmManager5= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

			Calendar calendar3 = Calendar.getInstance();
			calendar3.setTimeInMillis(System.currentTimeMillis());
			calendar3.add(Calendar.SECOND, 10);

			long interval = CommonFunctions.UploadTime* 1000; 
			Logger.logger("uploading");
			alarmManager5.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar3.getTimeInMillis(), interval, pendingIntent5);

			editor1.putInt("UploadState", 1);
			editor1.commit();


			//	File url=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Wifi_"+"room"+"_"+"room"+".csv");

			//	uploadDropbox(url);

		}

		break;

		
		case R.id.stop:
			
		{
			
			Toast.makeText(getApplicationContext(), "Upload stopped", Toast.LENGTH_SHORT).show();
			AlarmManager alarmManager5= (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
			myIntent4 = new Intent( context,com.example.sensormanagement.BoxUpload.class);
			pendingIntent5 = PendingIntent.getService(context,
					12350, myIntent4, PendingIntent.FLAG_CANCEL_CURRENT);
			// pendingIntent2.cancel();
			alarmManager5.cancel(pendingIntent2);
			SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

			SharedPreferences.Editor editor1 = app_preferences1.edit();
			editor1.putInt("UploadState", 0);
			editor1.commit();
		}
		
		break;
		case R.id.status:
			//Intent hIntent = new Intent();
			//hIntent.setClass(this, Help.class);
			//startActivity(hIntent);
			
		{
			help();
			System.out.println("after help");
		}
			
			break;
			
			
		case R.id.help:
			
			
			Intent hIntent = new Intent();
			hIntent.setClass(this, Help.class);
			startActivity(hIntent);
			
		
			
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	
	void help(){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	LayoutInflater inflater = getLayoutInflater();
    	
    	View promptsview=inflater.inflate(R.layout.helpbody, null);
    	View view=inflater.inflate(R.layout.help, null);
    	final TextView collect_state=(TextView)promptsview.findViewById(R.id.textView1);
    	final TextView file_size=(TextView)promptsview.findViewById(R.id.textView2);
    	final TextView upload_state=(TextView)promptsview.findViewById(R.id.textView3);
    	
    	alert.setCustomTitle(view);
    	alert.setIcon(R.drawable.devinfo);
  
    	alert.setView(promptsview);
    	alert.setInverseBackgroundForced(true);
    	 AlertDialog dialog =alert.create();
    	 dialog. getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
   
    	 collect_state.setTextColor(Color.parseColor("#ff355689"));
    	 upload_state.setTextColor(Color.parseColor("#ff355689"));
    	 file_size.setTextColor(Color.parseColor("#ff355689"));
    	
    	 collect_state.append("Data Collection    ");
    	 file_size.append("Folder Size        ");
    	 upload_state.append("Upload             ");
    	 
    	 SharedPreferences app_preferences1 = getSharedPreferences("CollectState",MODE_PRIVATE); 

    		final int c=app_preferences1.getInt("state", 0);
    		final int u=app_preferences1.getInt("UploadState", 0);
    		System.out.println("4");
    		
    		collect_state.post(new Runnable() {
				public void run() {

					String v;
					if(c==0)
						v="OFF";
					else
						v="ON";
						collect_state.append(v);

				}
			});
    		
    		
    		System.out.println("5");
    		upload_state.post(new Runnable() {
				public void run() {

					String v;
					if(u==0)
						v="OFF";
					else
						v="ON";
					
						upload_state.append(v);

				}
			});
    		System.out.println("6");
    		numberFormat = NumberFormat.getInstance();
         	//disable grouping
         	numberFormat.setGroupingUsed(false);
         	//display numbers with two decimal places
         	numberFormat.setMaximumFractionDigits(2); 
    		file_size.post(new Runnable() {
				public void run() {
					
					
					File file =new File( Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MobiStatSense");
			         if (file.exists()){
			       
			        	 File[] children = file.listFiles();
			        	  double sum=0;
			        	 for(File child:children){
			        	
			        		
			        		 sum+=(child.length() / (double)1048576);
			        		 
			        	 }
			        	 file_size.append((numberFormat.format(sum)+" MB  "));
			        
			         }
			         
			         else{
			        	 file_size.append("Empty");
			        	 
			         }

					
						

				}
			});
    		System.out.println("8");
    		
    		
    	alert.show();
    
	
	}

	public void Wifi_Label(){
		SharedPreferences app_preferences = getSharedPreferences("settings_data",Context.MODE_PRIVATE); 

		String t=app_preferences.getString("type", "room");
		System.out.println("t:"+t);
		if(t!=null){
			System.out.println("type:"+CommonFunctions.Type);

			if(t.equals("1BHK"))
			{
				System.out.println("1st type");
				AlertLabel(Labels_1,"Label 1BHK");
			}
			else if(t.equals("3BHK")){
				AlertLabel(Labels_3,"Label 3BHK");
				System.out.println("2nd type"); 
			}


			else
				AlertLabel(dorms,"Label hostel");
		}
		else{

			Toast.makeText(getApplicationContext(),"Select type from Settings to create radio map", Toast.LENGTH_SHORT).show();
		}



	}


	public void AlertLabel(final String[] data,String title){


		LayoutInflater li=LayoutInflater.from(this);
		View promptsview=li.inflate(R.layout.map, null);
		View view=li.inflate(R.layout.wifi_label, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCustomTitle(view);
		alert.setView(promptsview);
		// alert.setTitle(title);
		alert.setInverseBackgroundForced(true);
		AlertDialog dialog =alert.create();
		dialog. getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// alert.setIcon(R.drawable.netowrk);
		TextView label=(TextView)view.findViewById(R.id.wifi_label);

		label.setText(title);

		SharedPreferences app_preferences1 =getApplicationContext().getSharedPreferences(Position,Context.MODE_PRIVATE);
		int position=app_preferences1.getInt("wifi", 0);

		alert.setSingleChoiceItems(data, position, new DialogInterface

				.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {


				// String label=data[item] .toString();
				SharedPreferences app_preferences1 =getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE); 

				SharedPreferences.Editor editor = app_preferences1.edit();
				editor.putInt("wifi",item );

				editor.putString("wifilabel",data[item]);

				editor.commit(); 

				dialog.dismiss();
			}
		});	 


		alert.setPositiveButton("Reset type", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				System.out.println("reset type");
				AlertType(Type,typeTitle);

			}
		});

		alert.show();	



	}


	public void Audio_Label(){




		LayoutInflater li=LayoutInflater.from(this);
		View promptsview=li.inflate(R.layout.map, null);
		View view=li.inflate(R.layout.audio_label, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCustomTitle(view);
		alert.setView(promptsview);
		//	 alert.setTitle("Audio Labels");
		alert.setInverseBackgroundForced(true);
		AlertDialog dialog =alert.create();

		int c;
		SharedPreferences audio_pref1 = getSharedPreferences("audiolist",Context.MODE_PRIVATE); 
		c=audio_pref1.getInt("count", 0);
		audio.clear();
		Log.i("count",""+c);





		SharedPreferences audio_pref = getSharedPreferences("audiolist",Context.MODE_PRIVATE); 

		SharedPreferences.Editor editor1 = audio_pref.edit();
		//System.out.println("audio_label.length"+audio_label.length);
		int len;

		if(c!=0){
			len=c;

			try{
				for(int i=0;i<c;i++)
				{
					System.out.println("in preference audio");
					audio.add(audio_pref1.getString("audio"+i, ""));
					//	System.out.println("i"+i);
					//	Log.i("audio:",audio_pref1.getString("audio"+i, ""));
					editor1.putString("audio"+i, audio_label[i]);

				}
				editor1.putInt("count", c);
				editor1.commit(); 

			}catch(Exception e){
				Log.i("exception",e.toString());

			}
		}
		else{
			System.out.println("in audio_label charsequence audio");
			len=audio_label.length;
			for(int i=0;i<len;i++)
			{

				audio.add(audio_label[i]);

				editor1.putString("audio"+i, audio_label[i]);

			}
			editor1.putInt("count", audio_label.length);
			editor1.commit();

		}

		audio_label = audio.toArray(new String[audio.size()]);

		dialog. getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// alert.setIcon(R.drawable.netowrk);

		SharedPreferences app_preferences1 = getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE);
		int position=app_preferences1.getInt("audio", 0);
		String audiolabel1=app_preferences1.getString("audiolabel", "Fan");
		//String label;
		System.out.println(audiolabel1);
		for(int i=0;i<audio_label.length;i++)
		{
			if(audio_label[i]==audiolabel1)
			{
				position=i;
				System.out.println("position is:"+position);

			}

		}
		alert.setSingleChoiceItems(audio_label, position, new DialogInterface

				.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				String label=audio_label[item] ;

				SharedPreferences app_preferences1 = getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE); 

				SharedPreferences.Editor editor = app_preferences1.edit();
				editor.putInt("audio",item );


				editor.putString("audiolabel",label );


				editor.commit();  





				dialog.dismiss();
			}
		});	 


		alert.setPositiveButton("Add", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int whichButton) { 

				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				alert.setTitle("Enter Label");
				alert.setInverseBackgroundForced(true);
				final EditText input = new EditText(context);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = input.getText();

						//	  if(value!=null || value.length()>0){
						if(input.getText().equals(null) || input.getText().length()==0){

							//	Toast.makeText(context, "Label cannot be Null!", Toast.LENGTH_SHORT).show();

							AlertDialog.Builder alert = new AlertDialog.Builder(context);

							alert.setTitle("Enter Label");
							alert.setMessage("Label must be non-null value !");
							alert.setInverseBackgroundForced(true);


							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
								public void onClick(DialogInterface dialog, int whichButton) { 

									//do nothing
								}
							});
							alert.show();
						}
						else{
							SharedPreferences audio_pref = getSharedPreferences("audiolist",Context.MODE_PRIVATE); 

							int count=audio_pref.getInt("count", 0);

							audio.clear();

							for(int i=0;i<count;i++){

								audio.add(audio_pref.getString("audio"+i,""));
							}

							audio.add(value.toString());

							System.out.println(audio.toString());

							k=count+1;

							SharedPreferences audio_pref1 = context.getSharedPreferences("audiolist",Context.MODE_PRIVATE); 

							SharedPreferences.Editor editor1 = audio_pref1.edit();

							editor1.clear().commit();
							int i=0;
							for(String s : audio){	    			
								editor1.putString("audio"+i,s);
								// Log.i("k",""+k);
								Log.i("audio","audio"+i+"-"+s);


								i++;

							}
							editor1.putInt("count", audio.size());
							Log.i("audio size",""+audio.size());
							editor1.commit();



						}
					}
				});

				alert.show();

			}

		});

		/*
    		  alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() { 
    	    		public void onClick(DialogInterface dialog, int whichButton) { 

    	    			AlertDialog.Builder alert = new AlertDialog.Builder(context);

   	    			alert.setTitle("Enter Label");

   	    			final EditText input = new EditText(context);
   	    			alert.setView(input);

   	    			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
   	    			public void onClick(DialogInterface dialog, int whichButton) {


   	    				Editable value = input.getText();
	    	    			  System.out.println(value.toString());

	    	    			  if(value!=null && value.length()>0){

	    	    			  SharedPreferences audio_pref = getSharedPreferences("audiolist",Context.MODE_PRIVATE); 

	  					  	int count=audio_pref.getInt("count", 0);
	  					  	Log.i("count",""+count);
	  					  audio.clear();
	  					  	for(int i=0;i<count;i++){
	  					  		String val=audio_pref.getString("audio"+i, "");
	  					  		System.out.println(val);
	  					  //	if(val!=value.toString())
	  					  		audio.add(val);



	  					  	}

	  					Set<String> set = new HashSet<String>();
	  					  	int i=0;
	  					  for(String s:audio)
	  					  {
	  						  if(s.equals(value)){

	  						  }
	  						  else{
	  						  set.add(s);
	  						  }

	  					  }




	  					  	System.out.println("audio size"+set.size());
	  					  System.out.println("audio "+set);

	  					  SharedPreferences audio_pref1 = context.getSharedPreferences("audiolist",Context.MODE_PRIVATE); 

	    					SharedPreferences.Editor editor1 = audio_pref1.edit();

	    					editor1.clear().commit();

	    					int i1=0;
	    					for (String s : set) {
	    					    System.out.println(s);

	    					    editor1.putString("audio"+i1,s);
	    					    Log.i("audio","audio"+i1+"-"+s);
	    					    i1++;


	    					}
	    					 editor1.putInt("count", audio.size());

	    		    		 editor1.commit();





	    	    			  }



   	    			}


   	    			});

   	    			alert.show(); 


    	    		}




   			 });

		 */

		alert.show();	



	}




	public void Accl_Label(){



		final String[] accl_label={"On User","On Table","None"};


		LayoutInflater li=LayoutInflater.from(this);
		View promptsview=li.inflate(R.layout.map, null);
		View view=li.inflate(R.layout.accl_label, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCustomTitle(view);
		alert.setView(promptsview);
		// alert.setTitle("Accelerometer Labels");
		alert.setInverseBackgroundForced(true);
		AlertDialog dialog =alert.create();
		dialog. getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// alert.setIcon(R.drawable.netowrk);

		SharedPreferences app_preferences1 = getSharedPreferences(Position,Context.MODE_PRIVATE);
		int position=app_preferences1.getInt("accl", 0);

		alert.setSingleChoiceItems(accl_label, position, new DialogInterface

				.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				

				SharedPreferences app_preferences1 = getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE); 

				SharedPreferences.Editor editor = app_preferences1.edit();
				editor.putInt("accl",item );


				editor.putString("accllabel",accl_label[item] );

				editor.commit(); 

				SharedPreferences accl =getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE); 
				System.out.println(accl.getString("accllabel", "none"));




				dialog.dismiss();
			}
		});	





		alert.show();	

	}





	public void AlertType(final String[] data,String title){




		LayoutInflater li=LayoutInflater.from(this);
		View promptsview=li.inflate(R.layout.map, null);
		View view=li.inflate(R.layout.type, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCustomTitle(view);
		alert.setView(promptsview);
		// alert.setTitle("Type");
		alert.setInverseBackgroundForced(true);
		AlertDialog dialog =alert.create();
		dialog. getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// alert.setIcon(R.drawable.netowrk);

		

		SharedPreferences app_preferences1 = getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE);
		int position=app_preferences1.getInt("typepos", 0);

		alert.setSingleChoiceItems(data, position, new DialogInterface

				.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				
				SharedPreferences settings = getSharedPreferences("settings_data",Context.MODE_PRIVATE); 
				SharedPreferences.Editor editor1 = settings.edit();

				SharedPreferences app_preferences1 = getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE); 

				SharedPreferences.Editor editor = app_preferences1.edit();
				editor.putInt("typepos",item );




				editor.putString("type",data[item] );
				editor1.putString("type", data[item]);
				editor1.commit();

				editor.commit(); 


				SharedPreferences app_preferences = getApplicationContext().getSharedPreferences(Position,MODE_PRIVATE); 
				if(app_preferences.getString("type", null)==Type[0]){
					AlertLabel(Labels_1,"Label 1BHK");

					System.out.println("1st type");
				}
				else if(app_preferences.getString("type", null)==Type[1]){
					AlertLabel(Labels_3,"Label 3BHK");
					System.out.println("2nd type") ;
				}
				else
					AlertLabel(dorms,"Label hostel");




				dialog.dismiss();
			}
		});	 





		alert.show();	


	}


	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,  
			int maxNumOfPixels) {  
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  

		int roundedSize;  
		if (initialSize <= 8) {  
			roundedSize = 1;  
			while (roundedSize < initialSize) {  
				roundedSize <<= 1;  
			}  
		} else {  
			roundedSize = (initialSize + 7) / 8 * 8;  
		}  

		return roundedSize;  
	}  

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,  
			int maxNumOfPixels) {  
		double w = options.outWidth;  
		double h = options.outHeight;  

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h  
				/ maxNumOfPixels));  
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(  
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));  

		if (upperBound < lowerBound) {  
			// return the larger one when there is no overlapping zone.  
			return lowerBound;  
		}  

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
			return 1;  
		} else if (minSideLength == -1) {  
			return lowerBound;  
		} else {  
			return upperBound;  
		}  
	}    


	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			level = i.getIntExtra("level", 0);

			int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_FULL;

			System.out.println(isCharging);

			unregisterReceiver(this);

		}

	};



	void updatePreferences() {
		SharedPreferences prefs =
				PreferenceManager.getDefaultSharedPreferences(this);

		// Get the desired sample rate.
		SharedPreferences app_preferences = getSharedPreferences("settings_data",Context.MODE_PRIVATE); 



		SharedPreferences.Editor editor = app_preferences.edit(); 

		editor.putBoolean("state", false);
		
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



		String uniqueno="none";
		try {
			String Unique= prefs.getString("uniqueno", "none");
			uniqueno=Unique;
		} catch (Exception e) {
			Log.e(TAG, "Pref: bad unique no");
		}
		Log.i(TAG, "Prefs: unique no " + uniqueno);
		editor.putString("uniqueno",uniqueno);
		CommonFunctions.setUniqueNo(app_preferences.getString("uniqueno", "none"));



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




		int sample=10;
		try {
			int stime = prefs.getInt("sample", 10);
			sample=stime;

		} catch (Exception e) {
			Log.e(TAG, "Pref: bad Sample time");
		}
		Log.i(TAG, "Prefs: sampletime " + sample);

		editor.putInt("sample", sample);
		CommonFunctions.setSampleTime(app_preferences.getInt("sample", 10)); 




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


		editor.commit();
	}


	
	
	
}

