package com.example.sensormanagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;
import com.iiitd.EnergySenseWifi.R;


public class Logger{
	private static final String APP_TAG = "MobiStatSense";

	public static File Path=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense");
	public static File WifiPath,AcclPath,SoundPath,LogPath,BatteryPath,SoundRawPath, LightPath, MagPath;
	public static String WIFIHEADER="time"+","+"mac"+","+"ssid"+","+"rssi"+","+"label";
	public static String ACCLHEADER="time"+","+"x"+","+"y"+","+"z"+","+"label";
	public static String RAWSOUNDHEADER="time"+","+"label"+","+"values";
	public static String SOUNDHEADER="time"+","+"mfcc1"+","+"mfcc2"+","+"mfcc3"+","+"mfcc4"+","+"mfcc5"+","+"mfcc6"+","+"mfcc7"+","+"mfcc8"+","+"mfcc9"+","+"mfcc10"+","+"mfcc11"+","+"mfcc12"+","+"mfcc13"+","+"label";
	public static String LIGHTHEADER = "time" + "," + "value" + "," + "label" ;
	public static String BATTERYHEADER="time"+","+"value"+","+"charging state"+"scaled battery";
	public static String MAGHEADER = "time" + "," + "x" + "," + "y" + "," + "z" + "label"; 
	static int lock=0;
	
	public static int logString(String message){
		return Log.i(APP_TAG, message);
	}

	public static String getFormattedTime()
	{
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	public static void Path(){

		if(!Path.exists()) 
			Path.mkdir();
	}



	public static void BatteryPath(){

		try
		{

			if(CommonFunctions.Type!=null)

				BatteryPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Battery_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
			else

				BatteryPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Battery"+".csv");
			synchronized(BatteryPath){
				if(!Path.exists()) 
					Path.mkdir();

				if(!BatteryPath.exists()){
					BatteryPath.createNewFile();


					BufferedWriter buf = new BufferedWriter(new FileWriter(BatteryPath, true)); 


					buf.append(BATTERYHEADER);
					buf.newLine();
					buf.close();
				}
			}



		} 
		catch (IOException e)
		{
			System.out.println("Sound Creation Logging Error");        	
			Logger.logger(e.toString());
		}


	}






	public static void WiFiPath(){



		if(CommonFunctions.Type!=null)
			WifiPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Wifi_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
		else
			WifiPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Wifi"+".csv");

		synchronized(WifiPath){

			if(!Path.exists()) 
				Path();

			if(!WifiPath.exists()){

				try{
					WifiPath.createNewFile();
					BufferedWriter buf = new BufferedWriter(new FileWriter(WifiPath, true)); 


					buf.append(WIFIHEADER);
					buf.newLine();
					buf.close();


				}catch(IOException e){
					System.out.println("Wifi Creation Logging Error");       		 
					Logger.logger(e.toString());
				}
			}
		}

	}

	public static void AcclPath(){

		if (CommonFunctions.Type!=null)
			AcclPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Accl_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
		else
			AcclPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Accl"+".csv");
		synchronized(AcclPath){
			if(!Path.exists()) 
				Path.mkdir();

			if(!AcclPath.exists()){

				try{


					AcclPath.createNewFile();
					BufferedWriter buf = new BufferedWriter(new FileWriter(AcclPath, true)); 


					buf.append(ACCLHEADER);
					buf.newLine();
					buf.close();


				}catch(IOException e){
					System.out.println("Accl Creation Logging Error");        		
					Logger.logger(e.toString());
				}
			}
		}


	}

	public static void SoundPath(){

		try
		{

			if(CommonFunctions.Type!=null)

				SoundPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Sound_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
			else

				SoundPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Sound"+".csv");
			synchronized(SoundPath){
				if(!Path.exists()) 
					Path.mkdir();

				if(!SoundPath.exists()){
					SoundPath.createNewFile();


					BufferedWriter buf = new BufferedWriter(new FileWriter(SoundPath, true)); 


					buf.append(SOUNDHEADER);
					buf.newLine();
					buf.close();
				}
			}



		} 
		catch (IOException e)
		{
			System.out.println("Sound Creation Logging Error");        	
			Logger.logger(e.toString());
		}


	}



	public static void SoundRawPath(){

		try
		{

			if(CommonFunctions.Type!=null)

				SoundRawPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"AudioSamples_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
			else

				SoundRawPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"AudioSamples"+".csv");
			synchronized(SoundPath){
				if(!Path.exists()) 
					Path.mkdir();

				if(!SoundRawPath.exists()){
					SoundRawPath.createNewFile();


					BufferedWriter buf = new BufferedWriter(new FileWriter(SoundRawPath, true)); 


					buf.append(RAWSOUNDHEADER);
					buf.newLine();
					buf.close();
				}
			}



		} 
		catch (IOException e)
		{
			System.out.println("Sound Creation Logging Error");        	
			Logger.logger(e.toString());
		}


	}

	
	
	public static void LightPath(){

		try
		{

			if(CommonFunctions.Type!=null)

				LightPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Light_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
			else

				LightPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Light"+".csv");
			synchronized(LightPath){
				if(!Path.exists()) 
					Path.mkdir();

				if(!LightPath.exists()){
					LightPath.createNewFile();


					BufferedWriter buf = new BufferedWriter(new FileWriter(LightPath, true)); 


					buf.append(LIGHTHEADER);
					buf.newLine();
					buf.close();
				}
			}



		} 
		catch (IOException e)
		{
			System.out.println("Light Logging Error");        	
			Logger.logger(e.toString());
		}


	}



	public static void MagPath(){

		try
		{

			if(CommonFunctions.Type!=null)

				MagPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Mag_"+CommonFunctions.Type+"_"+CommonFunctions.UniqueNo+".csv");
			else

				MagPath=new File(Environment.getExternalStorageDirectory()+File.separator+"mobistatsense"+File.separator+"Mag"+".csv");
			synchronized(MagPath){
				if(!Path.exists()) 
					Path.mkdir();

				if(!MagPath.exists()){
					MagPath.createNewFile();


					BufferedWriter buf = new BufferedWriter(new FileWriter(MagPath, true)); 


					buf.append(MAGHEADER);
					buf.newLine();
					buf.close();
				}
			}



		} 
		catch (IOException e)
		{
			System.out.println("agnetometerM Logging Error");        	
			Logger.logger(e.toString());
		}


	}


	
	
	public static void logger(String text)
	{       
		//   Log.i(APP_TAG,text);

		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){
			//Toast.makeText(this, "External SD card not mounted", Toast.LENGTH_LONG).show();
		} 

		LogPath = new File(Path+File.separator+"AppLog.txt");

		if(!Path.exists())

			Path();

		if(!LogPath.exists()){   

			try
			{

				LogPath.createNewFile();
			} 
			catch (IOException e)
			{
				System.out.println("App Logging Error");
				e.printStackTrace();
			}
		}

		try
		{

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = df.format(c.getTime());

			BufferedWriter buf = new BufferedWriter(new FileWriter(LogPath, true)); 

			synchronized(buf){
				buf.append(formattedDate + ";" + text);
				buf.newLine();
				buf.close();

			}

		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}




	public static void batteryLogger(String text)
	{       


		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 
		synchronized(text){
			Logger.Path();
			Logger.BatteryPath();

			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(BatteryPath, true)); 

				buf.append(text);
				buf.newLine();
				buf.close();





			}
			catch (IOException e)
			{
				System.out.println("Battery Logging Error");
				e.printStackTrace();
			}


		}
		
	}









	public static void wifiLogger(String text)
	{       


		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 
		synchronized(text){
			Logger.Path();
			Logger.WiFiPath();

			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(WifiPath, true)); 

				buf.append(text);
				buf.newLine();
				buf.close();





			}
			catch (IOException e)
			{
				System.out.println("Wifi Logging Error");
				e.printStackTrace();
			}


		}
		
	}

	public static void acclLogger(String text)
	{       


		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 
		synchronized(text){
			Logger.Path();
			Logger.AcclPath();

			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(AcclPath, true)); 

				buf.append(text);
				buf.newLine();
				buf.close();




			}
			catch (IOException e)
			{
				System.out.println("Accl Logging Error");
				e.printStackTrace();
			}

		}
		
	}


	
	public static void lightLogger(String text)
	{       


		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 
		synchronized(text){
			Logger.Path();
			Logger.LightPath();

			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(LightPath, true)); 

				buf.append(text);
				buf.newLine();
				buf.close();




			}
			catch (IOException e)
			{
				System.out.println("Light Logging Error");
				e.printStackTrace();
			}

		}
		
	}
	
	public static void magLogger(String text)
	{       


		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 
		synchronized(text){
			Logger.Path();
			Logger.MagPath();

			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(MagPath, true)); 

				buf.append(text);
				buf.newLine();
				buf.close();




			}
			catch (IOException e)
			{
				System.out.println("Magnetometer Logging Error");
				e.printStackTrace();
			}

		}
		
	}


	public static void soundLogger(String text)
	{       
		//  Log.i(APP_TAG,text);

		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 

		lock=1;

		synchronized(text){
			Logger.Path();
			Logger.SoundPath();

			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(SoundPath, true)); 



				buf.append(text);
				//  buf.newLine();
				buf.close();




			}
			catch (IOException e)
			{
				System.out.println("Sound Logging Error");
				e.printStackTrace();
			}

		}
		
	}

	public static void soundRawLogger(String data) {

		//  Log.i(APP_TAG,text);

		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())){

		} 

		lock=1;

		synchronized(data){
			Logger.Path();
			Logger.SoundRawPath();
			
			try
			{

				BufferedWriter buf = new BufferedWriter(new FileWriter(SoundRawPath, true)); 



				buf.append(data);
				
				//  buf.newLine();
				buf.close();

			


			}
			catch (IOException e)
			{
				System.out.println("Sound Logging Error");
				e.printStackTrace();
			}

		}
		



	}




}