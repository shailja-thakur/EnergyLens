package com.example.sensormanagement;

import java.util.Arrays;



import mfcc.FFT;
import mfcc.MFCC;
import mfcc.Window;


import android.content.Context;
import android.content.SharedPreferences;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.Handler;
import android.util.Log;

public class AudioData {
	private static int RECORDER_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
	private static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static int RECORDER_SAMPLERATE = 8000;

	private static int FFT_SIZE = 8192;
	private static int MFCCS_VALUE = 13;
	private static int MEL_BANDS = 20;
	int stopstate = 0;
	private FFT featureFFT = null;
	private MFCC featureMFCC = null;
	private Window featureWin = null;

	short[] recordedAudioBuffer;
	int recorderBufferSize;
	int recorderBufferSamples;
	int bufferRead;
	boolean running=false;
	long t=0;

	AudioRecord recorder;
	String l;
	int  collectstate;
	int sample;
	
	double[] toTransform;
	String type,unique;
	Context c;
	boolean state;

	public void start(String label,int s,int spl,String t,String u,Context ctxt){
		sample=spl;
		l=label;
		collectstate=s;
		type=t;
		unique=u;
		c=ctxt;

		Log.i("AudioData","in Start()");
		try{
			recorderBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
					RECORDER_CHANNELS,
					RECORDER_AUDIO_ENCODING) ;

			recorderBufferSize = Math.max(recorderBufferSize, RECORDER_SAMPLERATE);
			recorderBufferSamples = recorderBufferSize/2;


			featureFFT = new FFT(FFT_SIZE);
			featureWin = new Window(recorderBufferSamples);
			featureMFCC = new MFCC(FFT_SIZE, MFCCS_VALUE, MEL_BANDS, RECORDER_SAMPLERATE);

			




			recorder = new AudioRecord(RECORDER_SOURCE,
					RECORDER_SAMPLERATE,
					RECORDER_CHANNELS,
					RECORDER_AUDIO_ENCODING,
					recorderBufferSize);

			if(recorderBufferSize>0){

				Thread thr = new Thread(null, mTask, "AlarmService_Service");
				thr.start();
			}

		}catch(Exception e){

			System.out.println(e.toString());
		}

	}


	Handler handler = new Handler();

	Runnable mTask = new Runnable() {
		public void run() {

			String data  = "";
			try {
				int timeout=50;

				while (timeout>0 && recorder.getState() != AudioRecord.STATE_INITIALIZED) {

					Thread.sleep(5);
					timeout-=5;

					Log.i("AudioData", "Audio reader failed to initialize");

				}

				if(recorder.getState() != AudioRecord.STATE_INITIALIZED)

					running=true;

				else
					running=false;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			t=0;
			while(t<sample)
			{
				t++;
			*/
			long before = System.currentTimeMillis();
			t = (System.currentTimeMillis()/1000) - (before/1000) ;
			recorder.startRecording();
			while(t <= sample)
				
			{
				
				SharedPreferences app_preferences1 = c.getSharedPreferences("CollectState",c.MODE_PRIVATE); 
				
				stopstate = app_preferences1.getInt("state", 0);
				if (stopstate == 1){
					
				
				try{

					byte data8bit[] = new byte[recorderBufferSize];
					double fftBufferR[] = new double[FFT_SIZE];
					double fftBufferI[] = new double[FFT_SIZE];
					double featureCepstrum[] = new double[MFCCS_VALUE];
					short data16bit[] = new short[recorderBufferSamples];

					
					//long samples_prev=System.currentTimeMillis();
					
					bufferRead=recorder.read(data16bit, 0,recorderBufferSamples);

					//long samples_current=System.currentTimeMillis();


					//long samples_diff=samples_current-samples_prev;


					//System.out.println("recorderBufferSize:"+data16bit);

					//System.out.println("buffer length:"+data16bit.length);
					t= (System.currentTimeMillis()/1000) - (before/1000) ;
					long now=System.currentTimeMillis();
					//double currentPower = SignalPower.calculatePowerDb(recordedAudioBuffer, 0, recordedAudioBuffer.length);
					
					String raw_data=String.valueOf(now);
					
					raw_data+=","+l+",\"";

					for(int i=0;i<data16bit.length;i++){

						synchronized(this){

							raw_data+=data16bit[i];
							if(i==(data16bit.length)/2-1)
								break;
							else
								raw_data+=",";
						}


					}


					raw_data+="\""+"\n";

					 
					// Convert shorts to 8-bit bytes for raw audio output
		
					
					for (int i = 0; i < recorderBufferSamples; i ++)
					{
						data8bit[i*2] = (byte)data16bit[i];
						data8bit[i*2+1] = (byte)(data16bit[i] >> 8);
					}


					// Frequency analysis
					Arrays.fill(fftBufferR, 0);
					Arrays.fill(fftBufferI, 0);

					// Convert audio buffer to doubles
					for (int i = 0; i < bufferRead; i++)
					{
						fftBufferR[i] = data16bit[i];
					}

					long fft_prev=System.currentTimeMillis();

					// In-place windowing
					featureWin.applyWindow(fftBufferR);

					long fft_current=System.currentTimeMillis();

					long fft_diff=fft_current-fft_prev;

					long win_prev=System.currentTimeMillis();


					// In-place FFT
					featureFFT.fft(fftBufferR, fftBufferI);

					long win_current=System.currentTimeMillis();

					long win_diff=win_current-win_prev;

					long mfcc_prev=System.currentTimeMillis();

					// Get MFCCs
					data+= String.valueOf(now);
					featureCepstrum = featureMFCC.cepstrum(fftBufferR, fftBufferI);

					long mfcc_current=System.currentTimeMillis();

					long mfcc_diff=mfcc_current-mfcc_prev;

					data+=",";

					for(int i=0;i<featureCepstrum.length;i++){

						synchronized(this){

							data+=featureCepstrum[i];
							if(i==(featureCepstrum.length)-1)
								break;
							else
								data+=",";
						}

					}

					data+=","+l+"\n";

					synchronized(this){
						Logger.Path();
						Logger.SoundPath();


						Logger.soundLogger(data);
						//Logger.soundRawLogger(raw_data);

					}


				}catch(IllegalStateException e){
					//	Logger.logger(e.toString());

				}
				
				}
				else{
					
					System.out.println("stopreader state" + stopstate);
					stopReader();
					synchronized(this){
						Logger.Path();
						Logger.SoundPath();


						Logger.soundLogger(data);

					}
					break;
				}
			} 




			

			stopReader();

		}

	};






	public void stopReader(){


		System.out.println("realeasing audioRecorder");
		try{
			recorder.stop();
			recorder.release();
			running=false;
			 mTask=null;
			featureWin=null;
			featureFFT=null;
			featureMFCC=null;
		}catch(Exception e){

			System.out.println(e.toString());
		}

	}

	public boolean state(){

		return running;
	}


}




