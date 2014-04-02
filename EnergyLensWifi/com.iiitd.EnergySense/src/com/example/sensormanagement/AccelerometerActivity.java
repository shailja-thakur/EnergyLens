package com.example.sensormanagement;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;



import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.iiitd.EnergySenseWifi.R;

public class AccelerometerActivity extends TabActivity implements OnTabChangeListener {
	TabHost tabHost;
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.accl);

		
			tabHost = getTabHost(); 
			tabHost.setOnTabChangedListener(this);
			// Android tab
			Intent intentGraph = new Intent().setClass(this, AccelerometerGraphActivity.class);
			Intent intentData = new Intent().setClass(this, AcclDataActivity.class);
			Intent intentHardware = new Intent().setClass(this, AcclHwActivity.class);
			TabSpec tabSpecData = tabHost
					.newTabSpec("Data")
					.setIndicator("",getResources().getDrawable(R.drawable.data2))
					.setContent(intentData);

			// Apple tab
		
			TabSpec tabSpecGraph = tabHost
					.newTabSpec("Graph")
					.setIndicator("",getResources().getDrawable(R.drawable.graph4))
					.setContent(intentGraph);
		
			// Windows tab
		
			TabSpec tabSpecHardware = tabHost
					.newTabSpec("Hardware")
					.setIndicator("",getResources().getDrawable(R.drawable.info4))
					.setContent(intentHardware);
		
		
	
			// add all tabs 
			tabHost.addTab(tabSpecData);
			tabHost.addTab(tabSpecGraph);
			tabHost.addTab(tabSpecHardware);
			for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
			{
				tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ff355689"));
        	
			}
			tabHost.getTabWidget().setCurrentTab(0);
			tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#27408B"));
    
		
	
	}

public void onTabChanged(String arg0) {
	for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
	{
		tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ff355689"));
		
	}

	tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#27408B"));
}
		
	
	
	
		

	}