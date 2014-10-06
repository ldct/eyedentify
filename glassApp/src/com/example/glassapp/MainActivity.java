	/**
	 * OpenGlass Voice Example 
	 * Github - https://github.com/jaredsburrows/OpenQuartz
	 * @author Jared Burrows
	 * 
	 * Copyright (C) 2013 OpenQuartz
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */
	
	package com.example.glassapp;
	
	//********************************************************************************************
	//EE368 Digital Image Processing
	//Android Tutorial #3: Server-Client Communication
	//Author: Derek Pang (dcypang@stanford.edu), David Chen (dmchen@stanford.edu)
	//********************************************************************************************/
	
	import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
	
	
	import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.GestureDetector;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
	
	public class MainActivity extends Activity implements GestureDetector.OnGestureListener {
		
		private static final String TAG = "SIFTExampleActivity";
		
		Preview mPreview; 
		ResultView mResultView;
		private Context mContext = this;
		TextToSpeech ttobj;
		 String text=null;
		 String URL_MESSAGE="You have received a url message";
		 String MESSAGE="You have received a  message";
		 boolean isURL=false;
		 boolean isTxt=false;
		 boolean isNTH=true;
		 boolean isCOM=false;
		 boolean isBAR=false;
		 

		
		/** PLEASE PUT YOUR SERVER URL **/
		private final String SERVERURL = "http://162.243.208.97/analyse";
		
		Button button;
	
		
		private final static String INPUT_IMG_FILENAME = "/temp.jpg"; //name for storing image captured by camera view
		
		//flag to check if camera is ready for capture
		private boolean mCameraReadyFlag = true;
			 
	  // Called when the activity is first created. 
	  @Override
	  public void onCreate(Bundle savedInstanceState){
	      super.onCreate(savedInstanceState);
	      
	      //make the screen full screen
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	  			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	      //remove the title bar
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      
	      mResultView=new ResultView(this);
	      mPreview = new Preview(this);
	      
	      //set Content View as the preview
	      setContentView(mPreview);
	      
	      //addListenerOnButton();
	
	      
	      //add result view  to the content View
	      addContentView(mResultView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	      
	      //set the orientation as landscape
	     // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);   
	      
	      ttobj=new TextToSpeech(getApplicationContext(), 
	    	      new TextToSpeech.OnInitListener() {
	    	      @Override
	    	      public void onInit(int status) {
	    	         if(status != TextToSpeech.ERROR){
	    	             ttobj.setLanguage(Locale.UK);
	    	            }				
	    	         }
	    	      });
	      
	  }
	//  public void addListenerOnButton() {
	//  	 
	//		button = (Button) findViewById(R.id.button1);
	//
	//		button.setOnClickListener(new OnClickListener() {
	//
	//			@Override
	//			public void onClick(View arg0) {
	//
	//			  //
	//				Log.i(TAG, "HAAAA");
	//
	//			}
	//
	//		});
	//
	//	}
	
	  @Override
	   public void onPause(){
	      if(ttobj !=null){
	         ttobj.stop();
	         ttobj.shutdown();
	      }
	      super.onPause();
	   }
	  // Called when shutter is opened
	  ShutterCallback shutterCallback = new ShutterCallback() { 
	    public void onShutter() {       
	    }
	  };
	
	  // Handles data for raw picture
	  PictureCallback rawCallback = new PictureCallback() { 
	 	@Override
		public void onPictureTaken(byte[] arg0, android.hardware.Camera arg1) {
		}
	  };
	  
	  //store the image as a jpeg image
	  public  boolean compressByteImage(Context mContext, byte[] imageData,
				int quality) {
	  	File sdCard = Environment.getExternalStorageDirectory();
			FileOutputStream fileOutputStream = null;
	
			try {  			 			
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 1;  	//no downsampling		
				Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
						imageData.length,options);  			
				fileOutputStream = new FileOutputStream(
						sdCard.toString() +INPUT_IMG_FILENAME);							
	
				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);
				
				//compress image to jpeg
				myImage.compress(CompressFormat.JPEG, quality, bos);
	
				bos.flush();
				bos.close();  			
				fileOutputStream.close();  			
	
			} catch (FileNotFoundException e) {
	 			Log.e(TAG, "FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "IOException");
				e.printStackTrace();
			}
			return true;
		}
	  
	  // Handles data for jpeg picture
	  PictureCallback jpegCallback = new PictureCallback() { 
		@Override
		public void onPictureTaken(byte[] imageData, android.hardware.Camera camera) {
				if (imageData != null) {
	
					Intent mIntent = new Intent();
					//compress image
					compressByteImage(mContext, imageData, 75);  				
					setResult(0, mIntent);
					
					//** Send image and offload image processing task  to server by starting async task ** 
					ServerTask task = new ServerTask();
					task.execute( Environment.getExternalStorageDirectory().toString() +INPUT_IMG_FILENAME);
					
					//start the camera view again .
					camera.startPreview();  				
				}		
		}
	  };
	  
		//*******************************************************************************
		//UI
		//*******************************************************************************
	  
	  //onKeyDown is used to monitor button pressed and facilitate the switching of views
	//  @Override
	  public boolean onKeyDown(int keycode,KeyEvent event)
	  {
		 //check if the camera button is pressed
		 if(keycode==KeyEvent.KEYCODE_CAMERA)
		 {
			 //if result
			 if (mResultView.IsShowingResult)
			 {
				mResultView.IsShowingResult = false;
			 }
			 else if (mCameraReadyFlag == true)//switch to camera view
			 {
				mCameraReadyFlag = false;
				mPreview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			 }
			 return true;
		 }
		 return super.onKeyDown(keycode, event);
	  }
		
		//*******************************************************************************
		//Push image processing task to server
		//*******************************************************************************
		
		public class ServerTask  extends AsyncTask<String, Integer , Void>
		{
			public byte[] dataToServer;
					
			//Task state
			private final int UPLOADING_PHOTO_STATE  = 0;
			private final int SERVER_PROC_STATE  = 1;
			
			private ProgressDialog dialog;
			
			//upload photo to server
			HttpURLConnection uploadPhoto(FileInputStream fileInputStream)
			{
				
				final String serverFileName = "test"+ (int) Math.round(Math.random()*1000) + ".jpg";		
				final String lineEnd = "\r\n";
				final String twoHyphens = "--";
				final String boundary = "*****";
				
				try
				{
					URL url = new URL(SERVERURL);
					// Open a HTTP connection to the URL
					final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					// Allow Inputs
					conn.setDoInput(true);				
					// Allow Outputs
					conn.setDoOutput(true);				
					// Don't use a cached copy.
					conn.setUseCaches(false);
					
					// Use a post method.
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
					
					DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
					
					dos.writeBytes(twoHyphens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + serverFileName +"\"" + lineEnd);
					dos.writeBytes(lineEnd);
	
					// create a buffer of maximum size
					int bytesAvailable = fileInputStream.available();
					int maxBufferSize = 1024;
					int bufferSize = Math.min(bytesAvailable, maxBufferSize);
					byte[] buffer = new byte[bufferSize];
					
					// read file and write it into form...
					int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					
					while (bytesRead > 0)
					{
						dos.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					}
					
					// send multipart form data after file data...
					dos.writeBytes(lineEnd);
					dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
					publishProgress(SERVER_PROC_STATE);
					// close streams
					fileInputStream.close();
					dos.flush();
	
					
					return conn;
				}
				catch (MalformedURLException ex){
					Log.e(TAG, "error: " + ex.getMessage(), ex);
					return null;
				}
				catch (IOException ioe){
					Log.e(TAG, "error: " + ioe.getMessage(), ioe);
					return null;
				}
			}
			
		    //get image result from server and display it in result view
			void getResultImage(HttpURLConnection conn){		
				// retrieve the response from server
			      Log.i(TAG,"receiving 2");
	
				InputStream is;
				  try {
				      is = conn.getInputStream();
				      int ch;
				      StringBuffer sb = new StringBuffer();
				      while ((ch = is.read()) != -1) {
				        sb.append((char) ch);
				      }
				      //return sb.toString();
				      //can do text to speech here
				      String texty=sb.toString();
				     String check= texty.substring(0,texty.indexOf(' '));
				      Log.i(TAG,"Check "+check);

				      if(!check.isEmpty()){
				    	  isNTH=false;
				    	  // "72"
				      }
				       if(check.equals("txt")){
				    	  Log.i(TAG,"CHECKED!");
				    	  isTxt=true;
				    	  // "72"
				      }
				       if(check.equals("url")){
				    	  isURL=true;
				    	  // "72"
				      }
				       
				        if(check.equals("com")){
					    	  isCOM=true;
					    	  // "72"
					      }
				        if(check.equals("bar")){
					    	  isBAR=true;
					    	  // "72"
					      }
				        if(check.equals("nth")){
					    	  isNTH=true;
					    	  // "72"
					      }
				      
				      String result=texty.substring(texty.indexOf(' ')+1); // "tocirah sneab"
				      text=result;
				      Log.i(TAG,"receiving"+result);
				      //speakText(result);
				      is.close();
	
				      //Toast.makeText(this, text, Toast.LENGTH_LONG).show();
				      
				    } catch (IOException e) {
				    	Log.e(TAG,e.toString());
				    	e.printStackTrace();
				    } 
				    
	//			try {
	//				is = conn.getInputStream();
	//				//get result image from server
	//		        mResultView.resultImage = BitmapFactory.decodeStream(is);
	//		        is.close();		        
	//		        mResultView.IsShowingResult = true;	        
	//			} catch (IOException e) {
	//				Log.e(TAG,e.toString());
	//				e.printStackTrace();
	//			}
			}
			
			//Main code for processing image algorithm on the server
			
			void processImage(String inputImageFilePath){			
				publishProgress(UPLOADING_PHOTO_STATE);
				File inputFile = new File(inputImageFilePath);
				try {
					
					//create file stream for captured image file
					FileInputStream fileInputStream  = new FileInputStream(inputFile);
			    	
					//upload photo
			    	final HttpURLConnection  conn = uploadPhoto(fileInputStream);
			    	
			    	//get processed photo from server
			    	if (conn != null){
			    		Log.i(TAG,"receiving");
			    	getResultImage(conn);}
					fileInputStream.close();
				}
		        catch (FileNotFoundException ex){
		        	Log.e(TAG, ex.toString());
		        }
		        catch (IOException ex){
		        	Log.e(TAG, ex.toString());
		        }
			}
			
		    public ServerTask() {
		        dialog = new ProgressDialog(mContext);
		    }		
			
		    protected void onPreExecute() {
		        this.dialog.setMessage("Photo captured");
		        this.dialog.show();
		    }
			@Override
			protected Void doInBackground(String... params) {			//background operation 
				String uploadFilePath = params[0];
				processImage(uploadFilePath);
				//release camera when previous image is processed
				mCameraReadyFlag = true; 
				return null;
			}		
			//progress update, display dialogs
			@Override
		     protected void onProgressUpdate(Integer... progress) {
		    	 if(progress[0] == UPLOADING_PHOTO_STATE){
		    		 dialog.setMessage("Uploading");
		    		 dialog.show();
		    	 }
		    	 else if (progress[0] == SERVER_PROC_STATE){
			           if (dialog.isShowing()) {
			               dialog.dismiss();
			           }	    	 
		    		 dialog.setMessage("Processing");
		    		 dialog.show();
		    	 }	         
		     }		
		       @Override
		       protected void onPostExecute(Void param) {
		           if (dialog.isShowing()) {
		               dialog.dismiss();
		           }
					//speakText(URL_MESSAGE);
		           checkText(text);
		           
		       }
		       
		}
	
		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
	//		// TODO Auto-generated method stub
	//		 if (mResultView.IsShowingResult)
	//		 {
	//			mResultView.IsShowingResult = false;
	//		 }
	//		 else if (mCameraReadyFlag == true)//switch to camera view
	//		 {
	//			mCameraReadyFlag = false;
	//			mPreview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	//		 }
			return true;
		}
	
		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			 //if result
			
			// TODO Auto-generated method stub
			 if (mResultView.IsShowingResult)
			 {
				mResultView.IsShowingResult = false;
			 }
			 else if (mCameraReadyFlag == true)//switch to camera view
			 {
			mCameraReadyFlag = false;
				mPreview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			 }
		}
	
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}
	
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
		public void displayCompletion(String s)
	    {
	    	Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	    }
		public void recivingMessage()
	    {
	    	Toast.makeText(this, "receiving", Toast.LENGTH_SHORT).show();
	    }
		
		
		public void speakText(String s){
			if(!s.isEmpty()){
			
		      String toSpeak = s;
		      Toast.makeText(getApplicationContext(), toSpeak, 
		      Toast.LENGTH_SHORT).show();
		      ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
			}

		   }
		
		public void checkText(String s){
			
				
			 
			  if(isURL){
				speakText(URL_MESSAGE);
				//displayCompletion(URL_MESSAGE);
				//speakText(s);
				displayCompletion(s);
				isURL=false;
				
				
			}
			
			else if(isTxt){
				//speakText(MESSAGE);
				//displayCompletion(MESSAGE);
				speakText(s);
				
				displayCompletion(s);
				isTxt=false;


				
			}
			
			
			
			else if(isCOM){
				speakText("I recognize a company");
				//displayCompletion("I recognize a company");
				//speakText(s);
				displayCompletion(s);
				isCOM=false;

				
			}
			else if(isBAR){
				speakText("I recognize a barcode");
				//displayCompletion("I recognize a barcode");

				//speakText(s);
				displayCompletion(s);
				isBAR=false;

				
			}
			
		
			else if(isNTH){
				speakText("TRY Again");
				//displayCompletion("TRY Again");
				

				//speakText(s);
				//displayCompletion(s);
				
			}
			
			
			
		}
		
	}
	
	