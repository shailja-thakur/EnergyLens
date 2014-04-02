package com.example.sensormanagement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YLayoutStyle;
import com.iiitd.EnergySenseWifi.R;



public class WifiGraphActivity extends Activity
{
	Context c=this;

	private XYPlot mySimpleXYPlot;
	Set<String> wifi = new HashSet<String>();
	Number[] finalval ;
	ArrayList<Integer> value = new ArrayList<Integer>();
	private SimpleXYSeries aprLevelsSeries = null;
	String ssid;
	TextView t;

	{


		aprLevelsSeries = new SimpleXYSeries(" ");

	}



	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifigraph);

		// Initialize our XYPlot reference:
		mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		t=(TextView)findViewById(R.id.textView1);

		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				0xff355689, // line color
				Color.TRANSPARENT, // point color
				0xff355689); // fill color (optional)

		Paint paint = series1Format.getLinePaint();
		paint.setStrokeWidth(2);
		series1Format.setLinePaint(paint);
	
		mySimpleXYPlot.addSeries(aprLevelsSeries, LineAndPointRenderer.class,series1Format);
		mySimpleXYPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
		mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
		mySimpleXYPlot.getGraphWidget().getGridLinePaint().setColor(Color.GRAY);

		mySimpleXYPlot.setDomainLabel(" ");
		mySimpleXYPlot.setRangeLabel("strength");

		mySimpleXYPlot.setRangeBoundaries(-150,150,BoundaryMode.AUTO);

		Widget gw = mySimpleXYPlot.getGraphWidget();

		SizeMetrics sm = new SizeMetrics(0,SizeLayoutType.FILL,
				0,SizeLayoutType.FILL);
		gw.setSize(sm);

		LayoutManager lm = mySimpleXYPlot.getLayoutManager();

		lm.position(gw, 0, XLayoutStyle.ABSOLUTE_FROM_LEFT,
				0, YLayoutStyle.ABSOLUTE_FROM_TOP);

		mySimpleXYPlot.setTicksPerRangeLabel(3);

		mySimpleXYPlot.disableAllMarkup();




	}
	@Override
	public void onResume() {
		super.onResume();


		SharedPreferences prefs2 = c.getSharedPreferences("WifiSSID", Context.MODE_PRIVATE);
		String ssid=prefs2.getString("ssid","None Available");
		t.setText("AccessPoint Plotted"+"("+ssid+")");

		//  runnable1.run();
		runnable.run();


	}



	@Override 
	public void onStart() {
		super.onStart();
		Intent o=new Intent(WifiGraphActivity.this,WifiData.class);
		startService(o);

		SharedPreferences prefs2 = c.getSharedPreferences("WifiSSID", Context.MODE_PRIVATE);
		String ssid=prefs2.getString("ssid","None Available");
		t.setText("AccessPoint Plotted"+"("+ssid+")");

		//  runnable1.run();
		runnable.run();

	}


	private Handler handler = new Handler();


	private Runnable runnable = new Runnable() {

		public void run() {


			plot_data_wifi();

			handler.postDelayed(this, 1000);
		}


	};



	public void onPause(){
		super.onPause();

		handler.removeCallbacks(runnable);

	}



	private void plot_data_wifi(){




		int count;
		int val;
		int k=0;
		int c=0;

		SharedPreferences prefs = getSharedPreferences("NAME",MODE_PRIVATE);
		count = prefs.getInt("valuecount", 0);
		k=prefs.getInt("count", 0);
		
		if(k+1>value.size()){
			k=0;
			System.out.println("K"+k+"size"+value.size());	
		}

		if(value.size()>20){
			value.clear();
		}

		SharedPreferences prefs2 = getSharedPreferences("NAME",MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs2.edit();
		value.add(80);
		for(int i=k;i<k+1;i++)
		{
			val = prefs.getInt("IntValue_"+i,i);
			value.add(-val);
			c=k+1;
			editor.putInt("count",c); 


		}

		editor.commit();

		aprLevelsSeries.setModel(value, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		
		mySimpleXYPlot.redraw();

	}



}