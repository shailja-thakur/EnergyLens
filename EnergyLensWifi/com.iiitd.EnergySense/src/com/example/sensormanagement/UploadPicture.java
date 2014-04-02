package com.example.sensormanagement;

/*
 * Copyright (c) 2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/**
 * Here we show uploading a file in a background thread, trying to show
 * typical exception handling and flow of control for an app that uploads a
 * file from Dropbox.
 */
public class UploadPicture extends AsyncTask<Void, Long, Boolean> {

	private DropboxAPI<?> mApi;
	private String mPath;
	private File mFile;

	private long mFileLen;
	private UploadRequest mRequest;
	private Entry req;
	private Context mContext;
	// private final ProgressDialog mDialog;
	WifiManager wifiManager;
	private String mErrorMsg;


	public UploadPicture(Context context, DropboxAPI<?> api, String dropboxPath,
			File file) {
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();

		mFileLen = file.length();
		mApi = api;
		mPath = dropboxPath;
		mFile = file;
		
		//   mDialog = new ProgressDialog(context);
		//   mDialog.setMax(100);
		System.out.println("uploading");
		//   mDialog.setMessage("Uploading " + file.getName());
		//   mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//    mDialog.setProgress(0);
		/*   mDialog.setButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // This will cancel the putFile operation
                mRequest.abort();
            }
        });

		 */
		//     mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			// By creating a request, we get a handle to the putFile operation,
			// so we can cancel it later if we want to
			System.out.println("in do background");
			
			
			String name=mFile.getName().toString();
			String f=name.substring(0, name.lastIndexOf("."));
			String currentTimeString = new SimpleDateFormat("dd-MM-yyy_HH-mm-ss").format(new Date());
			//System.out.println(currentTimeString);
			System.out.println("file name:"+f);
			FileInputStream fis = new FileInputStream(mFile);
			String path = mPath + f+"-"+currentTimeString+".csv";
			//System.out.println("in do background");
			
			
			
			
		//	mApi.putFile(path, fis, mFile.length(),path, new ProgressListener() {
			mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(),
					new ProgressListener() {
				@Override
				public long progressInterval() {
					// Update the progress bar every half-second or so
					return 500;
				}

				@Override
				public void onProgress(long bytes, long total) {
					publishProgress(bytes);
				}
			});

			
			
			if (mRequest != null) {
				System.out.println("uploading file");
				mRequest.upload();
				System.out.println("returning true");
				return true;
			}
			System.out.println("10");

		} catch (DropboxUnlinkedException e) {
			// This session wasn't authenticated properly or user unlinked
			mErrorMsg = "This app wasn't authenticated properly.";
		} catch (DropboxFileSizeException e) {
			// File size too big to upload via the API
			mErrorMsg = "This file is too big to upload";
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = "Upload canceled";
		} catch (DropboxServerException e) {
			// Server-side exception.  These are examples of what could happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._401_UNAUTHORIZED) {

				// Unauthorized, so we should unlink them.  You may want to
				// automatically log the user out in this case.

				mErrorMsg = "Unauthorised";
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
				mErrorMsg = "Not allowed to access";
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
				mErrorMsg = "Path not found";

			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
				mErrorMsg = "Insufficient memory in your account";
			} else {
				// Something else
			}
			// This gets the Dropbox error, translated into the user's language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			//mErrorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = "Unknown error.  Try again.";
		} catch (FileNotFoundException e) {
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
		//    mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		//  mDialog.dismiss();
		//wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (result) {
			//showToast(result+"");
			System.out.println("upload result : "+result);
			
			//showToast(mFile.toString()+" : successfully uploaded");
			/*
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
			*/
			mFile.delete();
		} else {
			showToast(mErrorMsg);
			
			//showToast(mFile.toString()+" : successfully uploaded");
			/*
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
			*/
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}
}
