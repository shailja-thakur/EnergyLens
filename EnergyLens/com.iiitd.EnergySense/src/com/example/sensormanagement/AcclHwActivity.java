package com.example.sensormanagement;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.iiitd.EnegySense.R;




public class AcclHwActivity extends Activity 
{

	TextView xcord;
	TextView ycord;
	TextView zcord;
	TextView acord;
	TextView bcord;
	TextView ccord;

	TextView xcord1;
	TextView ycord1;
	TextView zcord1;
	TextView acord1;
	TextView bcord1;
	TextView ccord1;
	/**
	 * A simple formatter to convert bar indexes into sensor names.
	 */



	SensorManager sensorMgr;
	Sensor orSensor ;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acclhardware);


		xcord = (TextView) findViewById(R.id.textView1);
		ycord = (TextView) findViewById(R.id.textView3);
		zcord = (TextView) findViewById(R.id.textView5);
		acord = (TextView) findViewById(R.id.textView7);
		bcord = (TextView) findViewById(R.id.textView9);
		ccord = (TextView) findViewById(R.id.textView11);

		xcord1 = (TextView) findViewById(R.id.textView2);
		ycord1 = (TextView) findViewById(R.id.textView4);
		zcord1 = (TextView) findViewById(R.id.textView6);
		acord1 = (TextView) findViewById(R.id.textView8);
		bcord1 = (TextView) findViewById(R.id.textView10);
		ccord1 = (TextView) findViewById(R.id.textView12);


		// register for orientation sensor events:
		sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);

		for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER)) {

			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				orSensor = sensor;
			}
		}

		xcord.setText("Version:");
		ycord.setText("Name:");
		zcord.setText("Vendor:");
		acord.setText("Power:");
		bcord.setText("Maximum Range:");
		ccord.setText("Resolution:");


		xcord1.setText(String.valueOf(orSensor.getVersion()));
		ycord1.setText(String.valueOf(orSensor.getName()));
		zcord1.setText(String.valueOf(orSensor.getVendor()));
		acord1.setText(String.valueOf(orSensor.getPower()));
		bcord1.setText(String.valueOf(orSensor.getMaximumRange()));
		ccord1.setText(String.valueOf(orSensor.getResolution()));
		// if we can't access the orientation sensor then exit:





	}





}
