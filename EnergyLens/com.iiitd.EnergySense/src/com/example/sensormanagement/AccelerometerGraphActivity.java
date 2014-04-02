package com.example.sensormanagement;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;




import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.*;
import com.iiitd.EnegySense.R;


import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;


public class AccelerometerGraphActivity extends Activity implements SensorEventListener
{
	public int i=0;
	ArrayList<Float> value = new ArrayList<Float>();
	ArrayList<Float> value1 = new ArrayList<Float>();
	ArrayList<Float> value2 = new ArrayList<Float>();
	
	private static final int HISTORY_SIZE = 15;  
	private SensorManager sensorMgr = null;
	private Sensor orSensor = null;

	private XYPlot aprLevelsPlot = null;
	private SimpleXYSeries aprLevelsSeries = null;


		{


			aprLevelsSeries = new SimpleXYSeries("X");

		}
	
		private SimpleXYSeries aprLevelsSeries1 = null;


	{


		aprLevelsSeries1 = new SimpleXYSeries("Y");

	}
	
	private SimpleXYSeries aprLevelsSeries2 = null;


	{


		aprLevelsSeries2 = new SimpleXYSeries("Z");

	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometergraph);


		// setup the APR Levels plot:
		aprLevelsPlot = (XYPlot) findViewById(R.id.aprLevelsPlot);
	
		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.RED,                   // line color
				Color.TRANSPARENT,               // point color
             Color.TRANSPARENT);        
	
	        
		LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE,Color.TRANSPARENT,
				Color.TRANSPARENT);              // fill color (optional)
		LineAndPointFormatter series3Format = new LineAndPointFormatter(Color.rgb(0,128,0),Color.TRANSPARENT,
				Color.TRANSPARENT);
	  
	  
		Paint paint = series2Format.getLinePaint();
		paint.setStrokeWidth(2);
	    series2Format.setLinePaint(paint);
	    
	    Paint paint1 = series1Format.getLinePaint();
	    paint1.setStrokeWidth(2);
	    series1Format.setLinePaint(paint1);
	    
	    
	    Paint paint2 = series3Format.getLinePaint();
	    paint2.setStrokeWidth(2);
	    series3Format.setLinePaint(paint2);
	 
	    aprLevelsPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
	    aprLevelsPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.rgb(255,255,0));
	    aprLevelsPlot.getGraphWidget().getGridLinePaint().setColor(Color.rgb(255,255,0));
     
	    aprLevelsPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	    aprLevelsPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
	
	 
	  
	    aprLevelsPlot.setDomainLabel(" ");
	    aprLevelsPlot.setRangeLabel(" ");
	  
	    aprLevelsPlot.addSeries(aprLevelsSeries2,LineAndPointRenderer.class,series3Format);
	    aprLevelsPlot.addSeries(aprLevelsSeries1,LineAndPointRenderer.class,series2Format);
	    aprLevelsPlot.addSeries(aprLevelsSeries,LineAndPointRenderer.class,series1Format);
	    aprLevelsPlot.setTicksPerRangeLabel(3);
	    aprLevelsPlot.setTicksPerDomainLabel(5);
    
	    aprLevelsPlot.setRangeBoundaries(-20, 20,BoundaryMode.FIXED);
	    //aprLevelsPlot.setRangeBoundaries(0, 30,BoundaryMode.FIXED);
	    //aprLevelsPlot.setDomainBoundaries(i+aprLevelsPlot.getX(),200,BoundaryMode.FIXED);
	    // use our custom domain value formatter:
	    // aprLevelsPlot.setDomainStepMode(XYStepMode.SUBDIVIDE);
     

  
	    //aprLevelsPlot.setGridPadding(15, 0, 15, 0);
	    aprLevelsPlot.disableAllMarkup();

	    // register for orientation sensor events:
	    sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
	    for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER)) {
	    	if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    		orSensor = sensor;
	    	}
	    }

	    // if we can't access the orientation sensor then exit:
	    if (orSensor == null) {
	    	System.out.println("Failed to attach to TYPE_ACCELEROMETER.");
	    	cleanup();
			}

	    sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);

	}

	private void cleanup() {
		// aunregister with the orientation sensor before exiting:
		sensorMgr.unregisterListener(this);
		//aprLevelsPlot=null;
		
		finish();
	}


	// Called whenever a new orSensor reading is taken.
	
	public synchronized void onSensorChanged(SensorEvent sensorEvent) {

		float[] values = sensorEvent.values;
		float x = values[0];
		float y = values[1];
		float z = values[2];
	
		i=i+1;

		if (value.size() > HISTORY_SIZE) {
			value.remove(0);
			value1.remove(0);
			value2.remove(0);
  
		}

		// update instantaneous data:
		value.add(sensorEvent.values[0]);
		value1.add(sensorEvent.values[1]);
		value2.add(sensorEvent.values[2]);
		
		aprLevelsSeries1.setModel(value1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		aprLevelsSeries2.setModel(value2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		aprLevelsSeries.setModel(value, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

		aprLevelsPlot.redraw();
		

			}


		
public void onAccuracyChanged(Sensor sensor, int i) {
	// Not interested in this event
}
		

}
