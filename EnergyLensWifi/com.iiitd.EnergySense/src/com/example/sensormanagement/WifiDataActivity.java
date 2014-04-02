package com.example.sensormanagement;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidplot.xy.SimpleXYSeries;
import com.iiitd.EnergySenseWifi.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.os.Environment;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.TableRow.LayoutParams;

public class WifiDataActivity extends Activity{
	String code;
	Context c=this;
	File myFile,myFile1,myFile2;
	int lineCount=0;
	int lineCount1=0;
	int lineCount2=0;

	TextView t1,t2,t3,t4,t5,t6;
	StringBuilder sb=new StringBuilder();
	StringBuilder sb1=new StringBuilder();
	StringBuilder sb2=new StringBuilder();
	// CharSequence[] wifi1;
	int position;
	Set<String> wifi = new HashSet<String>();
	Number[] finalval ;
	ArrayList<Integer> value = new ArrayList<Integer>();
	ArrayList<String> value1 = new ArrayList<String>();
	private SimpleXYSeries aprLevelsSeries = null;
	String ssid;
	TextView t;
	Button b;


	String[] devicetype = new String[]{"Settings", "Upload","Data Visualization"};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.wifidata);

		t1=(TextView)findViewById(R.id.textView1);

		t3=(TextView)findViewById(R.id.textView3);

		t4=(TextView)findViewById(R.id.textView5);


		t1.setText("AccessPoint     : ");
		t3.setText("BSSID                : ");
		t4.setText("Strength            : ");



		t2=(TextView)findViewById(R.id.textView2); 
		b=(Button)findViewById(R.id.button1);



		SharedPreferences prefs = getSharedPreferences("scan", MODE_PRIVATE);
		//   String data = prefs.getString("data",s);
		final String ssid=prefs.getString("ssid","NA");


		setssid(ssid);			       
		runnable.run();



	}


	@Override
	public void onResume() {
		super.onResume();



		Intent o=new Intent(WifiDataActivity.this,WifiData.class);
		startService(o);
		runnable.run();


	}


	@Override
	public void onStart() {
		super.onStart();


		Intent o=new Intent(WifiDataActivity.this,WifiData.class);
		startService(o);
		runnable.run();

	}


	final Handler handler = new Handler();


	Runnable runnable = new Runnable() {

		public void run() {


			wifi.clear();
			b.setOnClickListener(new View.OnClickListener() {

				@TargetApi(11)
				public void onClick(View arg0) {
					LayoutInflater li=LayoutInflater.from(c);
					final AlertDialog.Builder builder = new AlertDialog.Builder(c);
					View view=li.inflate(R.layout.accesspointtitle, null);
					builder.setCustomTitle(view);
				
					builder.setInverseBackgroundForced(true);
					ListView modeList = new ListView(c);
				
					int k,count;
					String s;
					SharedPreferences prefs2 = c.getSharedPreferences("scan", Context.MODE_PRIVATE);
					count=prefs2.getInt("scancount", 0);

					for ( k = 1; k <=count; k++){
						s=prefs2.getString("scan_"+k,"");

						wifi.add(s);


					}


					final CharSequence[] wifi1 = wifi.toArray(new CharSequence[wifi.size()]);

					SharedPreferences app_preferences1 = getSharedPreferences("wifiposition",Context.MODE_PRIVATE);
					String point=app_preferences1.getString("position", "");


					for(int i=0;i<wifi1.length;i++){

						if(wifi1[i].equals(point)){
							position=i;
						}

					}
					//	 System.out.println("position:"+position);
					builder.setSingleChoiceItems(wifi1, position, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							SharedPreferences app_preferences1 = getSharedPreferences("wifiposition",Context.MODE_PRIVATE); 

							SharedPreferences.Editor editor1 = app_preferences1.edit();
							editor1.putString("position",wifi1[item].toString() );

							editor1.commit();   

							String  ssid=wifi1[item].toString();
							WifiCellAcc.getvalueAtPosition(wifi1[item].toString());
							setssid(ssid);
							SharedPreferences prefs1 = c.getSharedPreferences("WifiSSID", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = prefs1.edit();

							editor.putString("ssid", ssid);


							editor.commit();




							dialog.dismiss();





						}
					});     






					final Dialog dialog = builder.create();

					dialog.show();

				}
			});

			t6=(TextView)findViewById(R.id.textView6);
			t4=(TextView)findViewById(R.id.textView4);
			SharedPreferences prefs = getSharedPreferences("scan", MODE_PRIVATE);
			final String ssid=prefs.getString("ssid","NA");
			final String bssid=prefs.getString("bssid","NA");
			final String level=String.valueOf(prefs.getInt("level",0));

			t6.post(new Runnable() {
				public void run() {


					if(level!=null)
						t6.setText(level);

				}
			});
			t4.post(new Runnable() {
				public void run() {
					if(bssid!=null)
						t4.setText(bssid);

				}
			});


			handler.postDelayed(this, 10);

		}
	};




	void setssid(final String ssid){


		t2.post(new Runnable() {
			public void run() {
				if(ssid!=null)
					t2.setText(ssid);



			}
		});	



	}








}