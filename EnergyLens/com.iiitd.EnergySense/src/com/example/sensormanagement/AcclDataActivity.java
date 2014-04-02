package com.example.sensormanagement;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.androidplot.xy.*;
import com.iiitd.EnegySense.R;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;



public class AcclDataActivity extends Activity implements SensorEventListener
{

	TextView xcord;
	TextView ycord;
	TextView zcord;
	static File myFile;
	/**
	 * A simple formatter to convert bar indexes into sensor names.
	 */
	private class APRIndexFormat extends Format {
		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
			Number num = (Number) obj;

			// using num.intValue() will floor the value, so we add 0.5 to round instead:
			int roundNum = (int) (num.floatValue() + 0.5f);
			switch(roundNum) {
			case 0:
				toAppendTo.append("x accel");
				break;
			case 1:
				toAppendTo.append("y accel");
				break;
			case 2:
				toAppendTo.append("z accel");
				break;
			default:
				toAppendTo.append("Unknown");
			}
			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return null; // We don't use this so just return null for now.
		}
	}

	private SensorManager sensorMgr = null;
	private Sensor orSensor = null;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accldata);


		xcord = (TextView) findViewById(R.id.textView4);
		ycord = (TextView) findViewById(R.id.textView5);
		zcord = (TextView) findViewById(R.id.textView6);

		xcord.setTextSize(20);	
		ycord.setTextSize(20);
		zcord.setTextSize(20);

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

		sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_NORMAL);


	}

	private void cleanup() {
		// aunregister with the orientation sensor before exiting:
		sensorMgr.unregisterListener(this);
		finish();
	}


	// Called whenever a new orSensor reading is taken.

	public synchronized void onSensorChanged(SensorEvent sensorEvent) {

		float[] values = sensorEvent.values;


		float x = values[0];
		float y = values[1];
		float z = values[2];
		xcord.setText(String.valueOf(String.format("%.2f", x)));
		ycord.setText(String.valueOf(String.format("%.2f", y)));
		zcord.setText(String.valueOf(String.format("%.2f", z)));



	}



	public void onAccuracyChanged(Sensor sensor, int i) {
		// Not interested in this event
	}




}
