/*
 *
 * Copyright (c) 2010-2014 EVE GROUP PTE. LTD.
 *
 */


package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import android.preference.PreferenceManager;
import veg.mediaplayer.sdk.MediaPlayer;
import veg.mediaplayer.sdk.MediaPlayer.MediaPlayerCallback;
import veg.mediaplayer.sdk.MediaPlayer.PlayerModes;
import veg.mediaplayer.sdk.MediaPlayer.PlayerNotifyCodes;
import veg.mediaplayer.sdk.MediaPlayer.PlayerRecordFlags;
import veg.mediaplayer.sdk.MediaPlayer.PlayerState;
import veg.mediaplayer.sdk.MediaPlayerConfig;

class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
{
	public static final float MIN_ZOOM = 0.7f;
	public static final float MAX_ZOOM = 1.0f;
	public float scaleFactor = 1.0f;
	public boolean zoom = false;
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) 
	{
		scaleFactor *= detector.getScaleFactor();
		scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
		Log.e("Player", "onScale " + scaleFactor);
		return true;
	}
	@Override
	 public boolean onScaleBegin(ScaleGestureDetector detector) 
	{
		Log.e("Player", "onScaleBegin");
		zoom = true;
		return true;
	 }

	 @Override
	 public void onScaleEnd(ScaleGestureDetector detector) 
	 {
		Log.e("Player", "onScaleEnd");
		zoom = false;
	 }
	
}

class ViewSizes
{
	public float dx = 0;
	public float dy = 0;
	
	public float orig_width = 0;
	public float orig_height = 0;
	
	public ScaleListener listnrr = null;
	
}

public class MainActivity extends Activity implements OnClickListener, MediaPlayer.MediaPlayerCallback, View.OnTouchListener
{

	private static final String strUrl = "rtsp://equicam.noip.me:554/?inst=1/?audio_mode=0/?enableaudio=1/?h26x=4";

    private static final String TAG 	 = "EQuicamAPP";

	//Record split time
	int rec_split_time = 240;

    public  static ArrayAdapter<String> edtIpAddressAdapter;
    private Button						btnConnect;
	private Button						btnRecord;
	private boolean						is_record = false;

	private StatusProgressTask 			mProgressTask = null;
	
	private SharedPreferences 			settings;
    private SharedPreferences.Editor 	editor;

    private boolean 					playing = false;
    private MediaPlayer 				player = null;
    //private MediaPlayer 				player_record = null;
    private MainActivity 				mthis = null;

    private RelativeLayout 				playerStatus = null;
    private TextView 					playerStatusText = null;
    private TextView 					playerHwStatus = null;
    
	public ScaleGestureDetector 		detectors = null;	
	public ViewSizes 					mSurfaceSizes 	= null;
    
    private MulticastLock multicastLock = null;
    
	private enum PlayerStates
	{
	  	Busy,
	  	ReadyForUse
	};

    private enum PlayerConnectType
	{
	  	Normal,
	  	Reconnecting
	};
    
	private Object waitOnMe = new Object();
	private PlayerStates player_state = PlayerStates.ReadyForUse; 
	private PlayerConnectType reconnect_type = PlayerConnectType.Normal;
	private int mOldMsg = 0;


	// Event handler

// Event handler

    private Handler handler = new Handler()
    {
        String strText = "Connecting";

        @Override
        public void handleMessage(Message msg)
        {
            PlayerNotifyCodes status = (PlayerNotifyCodes) msg.obj;
            switch (status)
            {
                case CP_CONNECT_STARTING:
                    if (reconnect_type == PlayerConnectType.Reconnecting)
                        strText = "Reconnecting";
                    else
                        strText = "Connecting";

                    startProgressTask(strText);
	        		
	        		player_state = PlayerStates.Busy;
	    			showStatusView();
	    			
	    			reconnect_type = PlayerConnectType.Normal;
	    			setHideControls();
	    			break;
	                
		    	case VRP_NEED_SURFACE:
		    		player_state = PlayerStates.Busy;
		    		showVideoView();
					break;
	
		    	case PLP_PLAY_SUCCESSFUL:
		    		player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
	    			playerStatusText.setText("");
		    		setTitle(R.string.app_name);
			        break;
	                
	        	case PLP_CLOSE_STARTING:
	        		player_state = PlayerStates.Busy;
	        		stopProgressTask();
	    			playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setUIDisconnected();
	    			break;
	                
	        	case PLP_CLOSE_SUCCESSFUL:
	        		player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			System.gc();
	    			setShowControls();
	    			setUIDisconnected();
	                break;
	                
	        	case PLP_CLOSE_FAILED:
	        		player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setShowControls();
	    			setUIDisconnected();
	   			break;
	               
	        	case CP_CONNECT_FAILED:
	        		player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setShowControls();
	    			setUIDisconnected();
	    			break;
	                
	            case PLP_BUILD_FAILED:
	            	player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setShowControls();
	    			setUIDisconnected();
	    			break;
	                
	            case PLP_PLAY_FAILED:
	            	player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setShowControls();
	    			setUIDisconnected();
	    			break;
	                
	            case PLP_ERROR:
	            	player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setShowControls();
	    			setUIDisconnected();
	    			break;
	                
	            case CP_INTERRUPTED:
	            	player_state = PlayerStates.ReadyForUse;
	        		stopProgressTask();
                    playerStatusText.setText(getString(R.string.GeenVerbindingString));
	    			showStatusView();
	    			setShowControls();
	    			setUIDisconnected();
	    			break;

	            case CP_RECORD_STARTED:
	            	Log.v(TAG, "=handleMessage CP_RECORD_STARTED");
	            	{
	            		String sFile = player.RecordGetFileName(1);
	            	}
	            	break;

	            case CP_RECORD_STOPPED:
	            	Log.v(TAG, "=handleMessage CP_RECORD_STOPPED");
	            	{
	            		String sFile = player.RecordGetFileName(0);
	            	}
	            	break;

	            //case CONTENT_PROVIDER_ERROR_DISCONNECTED:
	            case CP_STOPPED:
	            case VDP_STOPPED:
	            case VRP_STOPPED:
	            case ADP_STOPPED:
	            case ARP_STOPPED:
	            	if (player_state != PlayerStates.Busy)
	            	{
		        		stopProgressTask();
	            		player_state = PlayerStates.Busy;
	            		player.Close();
                        playerStatusText.setText(getString(R.string.GeenVerbindingString));
		    			showStatusView();
		    			player_state = PlayerStates.ReadyForUse;
		    			setShowControls();
		    			setUIDisconnected();
	            	}
	                break;
	
	            case CP_ERROR_DISCONNECTED:
	            	if (player_state != PlayerStates.Busy)
	            	{
	            		player_state = PlayerStates.Busy;
	            		player.Close();

                        playerStatusText.setText(getString(R.string.GeenVerbindingString));
		    			showStatusView();
		    			player_state = PlayerStates.ReadyForUse;
		    			setUIDisconnected();
	            		
						Toast.makeText(getApplicationContext(), getString(R.string.Slechts2MinutenOpnameString),
								   Toast.LENGTH_SHORT).show();
						
	            	}
	                break;
	            default:
	            	player_state = PlayerStates.Busy;
	        }
	    }
	};

	// callback from Native Player 
	@Override
	public int OnReceiveData(ByteBuffer buffer, int size, long pts) 
	{
		Log.e(TAG, "Form Native Player OnReceiveData: size: " + size + ", pts: " + pts);
		return 0;
	}
    

	// All event are sent to event handlers    
	@Override
	public int Status(int arg)
	{
		
		PlayerNotifyCodes status = PlayerNotifyCodes.forValue(arg);
		if (handler == null || status == null)
			return 0;
		
		Log.e(TAG, "Form Native Player status: " + arg);
	    switch (PlayerNotifyCodes.forValue(arg)) 
	    {
	        default:     
				Message msg = new Message();
				msg.obj = status;
				handler.removeMessages(mOldMsg);
				mOldMsg = msg.what;
				handler.sendMessage(msg);
	    }
	    
		return 0;
	}
	
    public String getRecordPath()
    {
    	File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
    		      Environment.DIRECTORY_DCIM), "RecordsMediaPlayer");
    	
	    if (! mediaStorageDir.exists()){
	        if (!(mediaStorageDir.mkdirs() || mediaStorageDir.isDirectory())){
	            Log.e(TAG, "<=getRecordPath() failed to create directory path="+mediaStorageDir.getPath());
	            return "";
	        }
	    }
	    return mediaStorageDir.getPath();
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{

		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifi.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
		
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		
		setContentView(R.layout.main);
		mthis = this;
		
		settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

		SharedSettings.getInstance(this).loadPrefSettings();
		SharedSettings.getInstance().savePrefSettings();
		
		playerStatus 		= (RelativeLayout)findViewById(R.id.playerStatus);
		playerStatusText 	= (TextView)findViewById(R.id.playerStatusText);
		playerHwStatus 		= (TextView)findViewById(R.id.playerHwStatus);
		
		player = (MediaPlayer)findViewById(R.id.playerView);

        player.getSurfaceView().setZOrderOnTop(true);    // necessary
        SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

		HashSet < String > tempHistory = new HashSet<String>();
        tempHistory.add("rtsp://equicam.noip.me:554/?inst=1/?audio_mode=0/?enableaudio=1/?h26x=4");

        player.setOnTouchListener(new View.OnTouchListener() {
                                      @Override
                                      public boolean onTouch(View view, MotionEvent motionEvent) {
                                          switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                                              case MotionEvent.ACTION_DOWN: {
                                                  if (player.getState() == PlayerState.Paused)
                                                      player.Play();
                                                  else if (player.getState() == PlayerState.Started)
                                                      player.Pause();
                                              }
                                          }

                                          return true;
                                      }
		});

		btnConnect = (Button)findViewById(R.id.button_connect);
        btnConnect.setOnClickListener(this);
        
        btnRecord = (Button) findViewById(R.id.button_record);
        btnRecord.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				is_record = !is_record;

				if(is_record){

					//start opname
					if(player != null){
						int record_flags = PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_AUTO_START) | PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_SPLIT_BY_TIME); //1 - auto start
						player.RecordSetup(getRecordPath(), record_flags, rec_split_time, 0, "");
						player.RecordStart();
                        Toast.makeText(getApplicationContext(),getString(R.string.OpnameGestartString), Toast.LENGTH_SHORT).show();
                    }
				}else{

					//stop opname
					if(player != null){
						player.RecordStop();
                        Toast.makeText(getApplicationContext(),getString(R.string.OpnameGestoptString), Toast.LENGTH_SHORT).show();
                    }
				}
				
				ImageView ivLed  = (ImageView)findViewById(R.id.led);
				if(ivLed != null)
					ivLed.setImageResource( ( is_record ? R.drawable.led_red : R.drawable.led_green) ); 
				btnRecord.setText( is_record? getString(R.string.StopOpnameString):getString(R.string.StartOpnameString) );
			}
        });
        
        
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_view);
        layout.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getWindow() != null && getWindow().getCurrentFocus() != null && getWindow().getCurrentFocus().getWindowToken() != null)
					inputManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				return true;
			}
		});
        
		playerStatusText.setText(getString(R.string.MaakVerbindingString));
		setShowControls();
        
    }

    private int[] mColorSwapBuf = null;                        // used by saveFrame()
    public Bitmap getFrameAsBitmap(ByteBuffer frame, int width, int height)
    {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(frame);
        return bmp;
    }
    
    public void onClick(View v) 
	{
		SharedSettings.getInstance().loadPrefSettings();
		if (player != null)
		{

			player.getConfig().setConnectionUrl(strUrl);
			if (player.getConfig().getConnectionUrl().isEmpty())
				return;


			//player_record.Close();
			
			player.Close();
			if (playing)
			{
    			setUIDisconnected();
			}
			else
			{
    	    	SharedSettings sett = SharedSettings.getInstance();
    			boolean bPort = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    	    	int aspect = bPort ? 1 : sett.rendererEnableAspectRatio;
    	    	
    	    	MediaPlayerConfig conf = new MediaPlayerConfig();
    	    	
    	    	player.setVisibility(View.INVISIBLE);
    	    	
    	    	conf.setConnectionUrl(player.getConfig().getConnectionUrl());
    			
    	    	conf.setConnectionNetworkProtocol(sett.connectionProtocol);
    	    	conf.setConnectionDetectionTime(sett.connectionDetectionTime);
    	    	conf.setConnectionBufferingTime(sett.connectionBufferingTime);
    	    	conf.setDecodingType(sett.decoderType);
    	    	conf.setRendererType(sett.rendererType);
    	    	conf.setSynchroEnable(sett.synchroEnable);
    	    	conf.setSynchroNeedDropVideoFrames(sett.synchroNeedDropVideoFrames);
    	    	conf.setEnableColorVideo(sett.rendererEnableColorVideo);
    	    	conf.setEnableAspectRatio(aspect);
    	    	conf.setDataReceiveTimeout(30000);
    	    	conf.setNumberOfCPUCores(0);
    	    	
    	    	//record config
    	    	if(is_record){
    	    		int record_flags = PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_AUTO_START); //1 - auto start    	    		
    	    		conf.setRecordPath(getRecordPath());
    	    		conf.setRecordFlags(record_flags);
    	    		conf.setRecordSplitTime(0);
    	    		conf.setRecordSplitSize(0);
    	    	}else{
    	    		conf.setRecordPath("");
    	    		conf.setRecordFlags(0);
    	    		conf.setRecordSplitTime(0);
    	    		conf.setRecordSplitSize(0);
    	    	}
    	    	Log.v(TAG, "conf record="+is_record);
    	    	
				// Open Player	
        	    player.Open(conf, mthis);

				btnConnect.setText("Disconnect");
				
				
				//record only
				conf.setMode(PlayerModes.PP_MODE_RECORD);
				//conf.setRecordTrimPosStart(10000); //from 10th sec
				//conf.setRecordTrimPosEnd(20000); //to 20th sec 
				/*player_record.Open(conf, new MediaPlayerCallback(){

					@Override
					public int Status(int arg) {
						Log.i(TAG, "=player_record Status arg="+arg);
						return 0;
					}

					@Override
					public int OnReceiveData(ByteBuffer buffer, int size,
							long pts) {
						// TODO Auto-generated method stub
						return 0;
					}
					
				});*/
				
				
				playing = true;
			}
		}
    }
 
	protected void onPause()
	{
		Log.e("SDL", "onPause()");
		super.onPause();

		editor = settings.edit();
		editor.commit();
		
		if (player != null)
			player.onPause();
	}

	@Override
  	protected void onResume() 
	{
		Log.e("SDL", "onResume()");
		super.onResume();
		if (player != null)
			player.onResume();
  	}

  	@Override
	protected void onStart() 
  	{
      	Log.e("SDL", "onStart()");
		super.onStart();
		if (player != null)
			player.onStart();
	}

  	@Override
	protected void onStop() 
  	{
  		Log.e("SDL", "onStop()");
		super.onStop();
		if (player != null)
			player.onStop();
		

	}

    @Override
    public void onBackPressed() 
    {

		player.Close();
		if (!playing)
		{
	  		super.onBackPressed();
	  		return;			
		}

		setUIDisconnected();
    }
  	
  	@Override
  	public void onWindowFocusChanged(boolean hasFocus) 
  	{
  		Log.e("SDL", "onWindowFocusChanged(): " + hasFocus);
  		super.onWindowFocusChanged(hasFocus);
		if (player != null)
			player.onWindowFocusChanged(hasFocus);
  	}

  	@Override
  	public void onLowMemory() 
  	{
  		Log.e("SDL", "onLowMemory()");
  		super.onLowMemory();
		if (player != null)
			player.onLowMemory();
  	}

  	@Override
  	protected void onDestroy() 
  	{
  		Log.e("SDL", "onDestroy()");

		if (player != null)
			player.onDestroy();
		
		stopProgressTask();
		System.gc();
		
		if (multicastLock != null) {
		    multicastLock.release();
		    multicastLock = null;
		}		
		super.onDestroy();
   	}	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
    }

    //MENU Listeners
	@Override
	public boolean onOptionsItemSelected(MenuItem item)  
	{
//		switch (item.getItemId())
//		{
//            case R.id.main_opt_exit:
//				finish();
//				break;
//
//		}
		return true;
	}

	protected void setUIDisconnected()
	{
		setTitle(R.string.app_name);
		btnConnect.setText(getString(R.string.VerbindenString));
		playing = false;
	}

	protected void setHideControls()
	{
		btnConnect.setVisibility(View.GONE);
	}

	protected void setShowControls()
	{
		setTitle(R.string.app_name);

		btnConnect.setVisibility(View.VISIBLE);
	}

	private void showStatusView() 
	{
		player.setVisibility(View.INVISIBLE);
		playerHwStatus.setVisibility(View.INVISIBLE);
		//player.setAlpha(0.0f);
		playerStatus.setVisibility(View.VISIBLE);
		
	}
	
	private void showVideoView() 
	{
        playerStatus.setVisibility(View.INVISIBLE);
 		player.setVisibility(View.VISIBLE);
		playerHwStatus.setVisibility(View.VISIBLE);

 		SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
		
		setTitle("");
	}
    
	private void startProgressTask(String text)
	{
		stopProgressTask();
	    
	    mProgressTask = new StatusProgressTask(text);
	    //mProgressTask.execute(text);
	    executeAsyncTask(mProgressTask, text);
	}
	
	private void stopProgressTask()
	{
		playerStatusText.setText("");
		setTitle(R.string.app_name);
		
       	if (mProgressTask != null)
	    {
       		mProgressTask.stopTask();
	    	mProgressTask.cancel(true);
	    }
	}

	private class StatusProgressTask extends AsyncTask<String, Void, Boolean> 
    {
       	String strProgressTextSrc;
       	String strProgressText;
        Rect bounds = new Rect();
    	boolean stop = false;
      	
       	public StatusProgressTask(String text)
       	{
        	stop = false;
       		strProgressTextSrc = text;
       	}
       	
       	public void stopTask() { stop = true; }
       	
        @Override
        protected void onPreExecute() 
        {
        	super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) 
        {
            try 
            {
                if (stop) return true;

                String maxText = getString(R.string.GeenVerbindingString) + ".....";
                int len = maxText.length();
            	playerStatusText.getPaint().getTextBounds(maxText, 0, len, bounds);

               	strProgressText = strProgressTextSrc + "...";
                
            	Runnable uiRunnable = null;
                uiRunnable = new Runnable()
                {
                    public void run()
                    {
                        if (stop) return;

    	                playerStatusText.setText(strProgressText);
    	            	
    	            	RelativeLayout.LayoutParams layoutParams = 
    	            		    (RelativeLayout.LayoutParams)playerStatusText.getLayoutParams();
    	           		
    	           		layoutParams.width = bounds.width();
    	           		playerStatusText.setLayoutParams(layoutParams);        	
    	            	playerStatusText.setGravity(Gravity.NO_GRAVITY);
    	            	
                        synchronized(this) { this.notify(); }
                    }
                };
                
               	int nCount = 4;
              	do
            	{
                    try
                    {
                    	Thread.sleep(300);
                    }
                    catch ( InterruptedException e ) { stop = true; }
                   
                    if (stop) break;
                    
                	if (nCount <= 3)
                	{
                		strProgressText = strProgressTextSrc;
                		for (int i = 0; i < nCount; i++)
                			strProgressText = strProgressText + ".";
                	}
                    
                    synchronized ( uiRunnable )
                    {
                    	runOnUiThread(uiRunnable);
                        try
                        {
                            uiRunnable.wait();
                        }
                        catch ( InterruptedException e ) { stop = true; }
                    }
                    
                    if (stop) break;
                    
                    nCount++;
                    if (nCount > 3)
                    {
                    	nCount = 1;
                    	strProgressText = strProgressTextSrc;
                    }
            	}
              	
            	while(!isCancelled());
            } 
            catch (Exception e) 
            {
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) 
        {
            super.onPostExecute(result);
            mProgressTask = null;
        }
        @Override
        protected void onCancelled() 
        {
            super.onCancelled();
        }
    }
	
    static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) 
    {
    	{
    		task.execute(params);
    	}
    }  
	
	@Override
	public boolean onTouch(View view, MotionEvent event) 
	{
		if (detectors != null)
			detectors.onTouchEvent(event);
		
	    switch (event.getAction()) 
	    {
	        case MotionEvent.ACTION_DOWN:
	        	mSurfaceSizes.dx =  event.getX();
	        	mSurfaceSizes.dy =  event.getY();
	            break;
	
	        case MotionEvent.ACTION_MOVE:
	            float x =  event.getX();
	            float y =  event.getY();
	            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            float left = lp.leftMargin + (x - mSurfaceSizes.dx); 
	            float top = lp.topMargin + (y - mSurfaceSizes.dy);
	            if (mSurfaceSizes.listnrr != null && mSurfaceSizes.listnrr.zoom)
	            {
	            	int srcw = lp.width;
	            	int srch = lp.height;
	            	
		    		int left_offset = (int) (mSurfaceSizes.orig_width - (mSurfaceSizes.orig_width * mSurfaceSizes.listnrr.scaleFactor));
		    		int top_offset = (int) (mSurfaceSizes.orig_height - (mSurfaceSizes.orig_height * mSurfaceSizes.listnrr.scaleFactor));
		    		Log.e("Player", "ACTION_MOVE2 " + left_offset + "," + top_offset);
		    		
	                lp.leftMargin = left_offset;
	                lp.topMargin  = top_offset;
	                lp.rightMargin = left_offset;
	                lp.bottomMargin  = top_offset;
	            }
	            view.setLayoutParams(lp);
	            break;
	    }	    
	    return true;
	}
	
}
