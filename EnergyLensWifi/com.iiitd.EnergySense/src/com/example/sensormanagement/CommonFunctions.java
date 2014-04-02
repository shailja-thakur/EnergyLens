package com.example.sensormanagement;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.iiitd.EnergySenseWifi.R;


//import android.R.integer;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class CommonFunctions extends Application{
	private static String TAG="CommonFunctions";
	public static final String TAG_APINAME = "apiname";
	public static final String TAG_STATUSCODE ="statuscode";
	public static final String TAG_MESSAGE ="message";
	public static String TAG_DEVNAME="devicename";
	public static String TAG_NAME="name";
	public static String TAG_IP="IP";
	public static String TAG_LATITUDE="latitude";
	public static String TAG_LONGITUDE="longitude";
	public static String TAG_LOACTION="location";
	public static String TAG_TAG="tags";
	public static String TAG_SENSORs="sensors";
	public static String TAG_CHANNELS="channels";
	public static String TAG_SAMPLINGPERIOD="samplingperiod";
	public static String TAG_TYPE="type";
	public static String TAG_UNIT="unit";
	public static String IP;
	public static String userID;
	public static short[] sound;
	public static int count;
	public static int ch_index=0;
	public static String add="http://";
	public static String TAG_URL=add+"192.168.1.40";
	public static String key;
	public static String Ownerkey;
	public static String TAG_username;
	public static String UserName;
	public static String response;
	public static String network;
	public static String user;
	public static String list;
	public static String sharedlist;
	public static int status;
	public static String URLresponse;
	
	public static String name;
	public static long upload;
	public static int s=0;
	public static int w=0;
	public static int a=0;
	
	public static  ArrayList<Float> graphdata = new ArrayList<Float>();

	public static  ArrayList<Float> dataset = new ArrayList<Float>();//public static String username;


	//Settings Parameters

	public static int SampleTime,WakeTime,UploadTime,SampleRate;
	public static String NetworkMode,AcclLabel,Wifi1Label,Wifi3Label,AudioLabel,AcclRate,UniqueNo,Type;

	public static void uploadTime(long t){

		upload=t;

	}

	public static void audio(int val){

		s=val;
	}


	public static void accl(int val){

		a=val;
	}

	public static void wifi(int val){

		w=val;
	}

	public static void setType(String type){

		Type=type;
	}


	public static void setUniqueNo(String unique){

		UniqueNo=unique;
	}



	public static void setAcclRate(String acclrate){

		AcclRate=acclrate;
	}


	public static void  setSampleRate(int samplerate)
	{
		SampleRate=samplerate;

	}

	public static void setSampleTime(int sample){

		SampleTime=sample;
	}

	public static void setWakeTime(int wakeup){

		WakeTime=wakeup;
	}

	public static void setUploadTime(int upload){

		UploadTime=upload;
	}



	public static void setNetworkMode(String networkmode){

		NetworkMode=networkmode;

	}

	public static void setAcclLabel(String accelerometer){

		AcclLabel=accelerometer;

	}

	public static void setWifi1Label(String wifi1){

		Wifi1Label=wifi1;

	}

	public static void setWifi3Label(String wifi3){

		Wifi3Label=wifi3;

	}

	public static void setAudioLabel(String audio){

		AudioLabel=audio;

	}

	public static void getName(String user){

		name=user;
	}
	public static String convertStreamToString(InputStream is) 
	{
		//int s = is.available();
		//byte buf[] = new byte[s];
		//is.read(buf);

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	public static String sendHttpRequest(String url,String json)
	{ 



		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000000);
		HttpConnectionParams.setSoTimeout(httpParameters, 100000000);

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		HttpResponse response;
		HttpPost post = new HttpPost(url);
		StringEntity se;


		JSONObject jsent;
		try {
			jsent = new JSONObject(json);
			TAG_username=jsent.getString("username");
			Log.i(TAG,TAG_username);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			se = new StringEntity(json);
			se.setContentType((Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

			post.setEntity(se);

			response = client.execute(post);

			System.out.println("response:->"+response);
			System.out.println(" SENT----");
			//    System.out.println(response);

			if(response!=null){
				InputStream in = response.getEntity().getContent(); //Get the data in the entity
				String ans = CommonFunctions.convertStreamToString(in);

				return ans;
			}
			else
			{
				Log.e(TAG,"no response");
			}

		}
		catch(ConnectTimeoutException e){
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;  

	}
	public static void getIP(String ip) {





		IP=ip;

	}
	public static void fetchKey(String key2,int code) {

		String part=key2.substring(0,2);

		if(key2.charAt(0)=='O')
		{
			user=key2.substring(0,5);
			key=key2.substring(7);	

		}
		else if(key2.charAt(0)=='U'){

			user=key2.substring(0,4);
			key=key2.substring(6);
		}


	}



	public static void fetchOwnerKey(String key2) {
		Ownerkey=key2;




	}

	public static void graphValues(ArrayList<Float> vals) {
		graphdata=vals;




	}

	public static void soundData(short[] buffer,int soundcount){
		sound=buffer;
		count=soundcount;
	}


	public static void putResponse(String ans) {

		response=ans;

	}
	public static void networkMode(String string) {

		network=string;

	}

	

	public static void putSharedList(String ans) {
		// TODO Auto-generated method stub

		sharedlist=ans;



	}
	public static void putArrayList(ArrayList<Float> numberList) {

		dataset=numberList;

	}
	public static void getuserID(String u) {
		userID=u;

	}
	public static void putName(String name2) {
		UserName=name2;

	}

}


