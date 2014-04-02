package com.example.sensormanagement;

import android.app.Activity;
import android.os.Bundle;



import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;


import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.app.ActionBar;
import com.iiitd.EnergySenseWifi.R;
public class WiFiActivity extends TabActivity implements OnTabChangeListener {
	TabHost tabHost;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi);


		tabHost = getTabHost(); 
		tabHost.setOnTabChangedListener(this);
		// Android tab
		Intent intentGraph = new Intent().setClass(this, WifiGraphActivity.class);
		Intent intentData = new Intent().setClass(this, WifiDataActivity.class);
		
		TabSpec tabSpecData = tabHost
				.newTabSpec("Data")
				.setIndicator("",getResources().getDrawable(R.drawable.data2))
				.setContent(intentData);

		// Apple tab

		TabSpec tabSpecGraph = tabHost
				.newTabSpec("Graph")
				.setIndicator("",getResources().getDrawable(R.drawable.graph4))
				.setContent(intentGraph);


		tabHost.addTab(tabSpecData);
		tabHost.addTab(tabSpecGraph);

		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		{
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ff355689"));

		}
		tabHost.getTabWidget().setCurrentTab(0);
		tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#27408B"));


		//set Windows tab as default (zero based)
		//	tabHost.setCurrentTab(0);
	}

	public void onTabChanged(String arg0) {
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		{
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ff355689"));

		}

		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#27408B"));
	}


}