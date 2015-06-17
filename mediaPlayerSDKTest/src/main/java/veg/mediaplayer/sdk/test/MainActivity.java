/*
 *
 * Mischa Hondius, 6053017.
 * University of Amsterdam
 * SDK Used by Video Experts Group
 *
 */

package veg.mediaplayer.sdk.test;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.File;
import java.nio.ByteBuffer;
import android.preference.PreferenceManager;
import EQuicamApp.R;
import veg.mediaplayer.sdk.MediaPlayer;
import veg.mediaplayer.sdk.MediaPlayer.PlayerModes;
import veg.mediaplayer.sdk.MediaPlayer.PlayerNotifyCodes;
import veg.mediaplayer.sdk.MediaPlayer.PlayerRecordFlags;
import veg.mediaplayer.sdk.MediaPlayerConfig;

public class MainActivity extends ActionBarActivity implements OnClickListener, MediaPlayer.MediaPlayerCallback
{

	//Equicam URL
//	public String camUrl = "rtsp://live:6mxNfzAG@equicam.noip.me:554/?inst=1/?audio_mode=0/?enableaudio=1/?h26x=4";
	public String camUrl;


    //Record path
    public String videoDirectory;

    //tag voor logs
    private static final String TAG 	 = "EQuicamAPP";

	//Record split time (meer dan de maximale opname tijd)
	int rec_split_time = 240;

	//Buttons MAINActivity
    private Button						btnConnect;
	private FrameLayout					recordCntrlsArea;
	private ImageButton 				btnHighlight;
	private ImageButton					btnRecord;
	private Chronometer					timer;
	private ImageView					playIcon;
	private ProgressBar					progressBar;

	//Drawer
	private ListView 					mDrawerList;
	private DrawerLayout 				mDrawerLayout;
	private ArrayAdapter<String> 		mAdapter;
	private ActionBarDrawerToggle 		mDrawerToggle;
	private String 						mActivityTitle;

//    public Bitmap tmpThumbNail;
//    public String tmpRecordFileName;

	//Is Playing/Is Recording checks
	private boolean						is_record = false;
	private boolean 					playing = false;

	private StatusProgressTask 			mProgressTask = null;
	private SharedPreferences 			settings;
    private SharedPreferences.Editor 	editor;
    private MediaPlayer 				player = null;
    private MainActivity 				mthis = null;
    private TextView 					playerStatusText = null;
    private MulticastLock 				multicastLock = null;
	private enum PlayerStates
	{
	  	Busy,
	  	ReadyForUse
	}

    private enum PlayerConnectType
	{
	  	Normal,
	  	Reconnecting
	}
    
	private PlayerStates player_state = PlayerStates.ReadyForUse;
	private PlayerConnectType reconnect_type = PlayerConnectType.Normal;
	private int mOldMsg = 0;

	// Event handler
    private Handler handler = new Handler()
    {
        String strText = "Verbinden";

        @Override
        public void handleMessage(Message msg)
        {
            PlayerNotifyCodes status = (PlayerNotifyCodes) msg.obj;
            switch (status)
            {
                case CP_CONNECT_STARTING:
                    if (reconnect_type == PlayerConnectType.Reconnecting)
                        strText = "Opnieuw aan het verbinden";
                    else
                        strText = "Verbinden";

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

	// All events are sent to event handlers
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

    //RecordPath Ophalen
    public String getRecordPath()
    {
    	File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
    		      Environment.DIRECTORY_DCIM), "EQuicam Clips");
    	
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

		camUrl = Camera.getCurrentCameraUrl();

		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifi.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();

		setContentView(R.layout.live);
		mthis = this;

		//CREATE HAMBURGER MENU
		mDrawerList = (ListView)findViewById(R.id.navList);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mActivityTitle = getTitle().toString();

		addDrawerItems();
		setupDrawer();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		//Get SharedPrefs
		settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		SharedSettings.getInstance(this).loadPrefSettings();
		SharedSettings.getInstance().savePrefSettings();

		//Get Player status textview
		playerStatusText 	= (TextView)findViewById(R.id.playerStatusText);

		//Get Player
		player = (MediaPlayer)findViewById(R.id.playerView);

        player.getSurfaceView().setZOrderOnTop(false);
        SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

		//Get BtnConnect button
		btnConnect = (Button) findViewById(R.id.button_connect);
		btnConnect.setOnClickListener(this);

		//Get recordCntrlsArea layout area
		recordCntrlsArea = (FrameLayout) findViewById(R.id.recordCntrlsArea);

		//Get Highlight flash button
		btnHighlight = (ImageButton) findViewById(R.id.button_record_flash);

        //Get and save Record path
        videoDirectory = getRecordPath();

		//Get timer
		timer = (Chronometer) findViewById(R.id.timerView);

		//Get player PLAY icon
		playIcon = (ImageView) findViewById(R.id.playIcon);

		//Get progressbar icon
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Recordbuttonlistener
        btnRecord = (ImageButton) findViewById(R.id.button_record);
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


						//knipper rec button
						Animation mAnimation = new AlphaAnimation(0, 1);
						mAnimation.setDuration(1000);
						mAnimation.setInterpolator(new LinearInterpolator());
						mAnimation.setRepeatCount(Animation.INFINITE);
						mAnimation.setRepeatMode(Animation.REVERSE);
						btnHighlight.startAnimation(mAnimation);

						//make visible knopje
						btnHighlight.setVisibility(View.VISIBLE);

						//start timer
						timer.setBase(SystemClock.elapsedRealtime());
						timer.start();

                    }
				}else{

					//stop opname
					if(player != null){
						player.RecordStop();
                        String tmpRecordFileName = player.RecordGetFileName(1);

                        Log.v(TAG, "Record filename=" + tmpRecordFileName);

                        Toast.makeText(getApplicationContext(),getString(R.string.OpnameGestoptString), Toast.LENGTH_SHORT).show();
						btnHighlight.clearAnimation();
						btnHighlight.setVisibility(View.INVISIBLE);

                        //Call make thumbnail functie
//                        saveThumbnail(tmpRecordFileName);

						//stop timer
						timer.stop();
						timer.getBase();
					}
				}
			}

		        });

		//TODO dubbele code
		//Highlightbuttonlistener
		btnHighlight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				is_record = !is_record;

				if (is_record) {

					//start opname
					if (player != null) {
						int record_flags = PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_AUTO_START) | PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_SPLIT_BY_TIME); //1 - auto start
						player.RecordSetup(getRecordPath(), record_flags, rec_split_time, 0, "");
						player.RecordStart();
						Toast.makeText(getApplicationContext(), getString(R.string.OpnameGestartString), Toast.LENGTH_SHORT).show();


						//knipper rec button
						//make visible knopje

						Animation mAnimation = new AlphaAnimation(0, 1);
						mAnimation.setDuration(1000);
						mAnimation.setInterpolator(new LinearInterpolator());
						mAnimation.setRepeatCount(Animation.INFINITE);
						mAnimation.setRepeatMode(Animation.REVERSE);
						btnHighlight.startAnimation(mAnimation);
						btnHighlight.setVisibility(View.VISIBLE);

						//start timer
						timer.setBase(SystemClock.elapsedRealtime());
						timer.start();
					}
				} else {

					//stop opname
					if (player != null) {
						player.RecordStop();
						String tmpRecordFileName = player.RecordGetFileName(1);

						Log.v(TAG, "Record filename=" + tmpRecordFileName);

						Toast.makeText(getApplicationContext(), getString(R.string.OpnameGestoptString), Toast.LENGTH_SHORT).show();
						btnHighlight.clearAnimation();
						btnHighlight.setVisibility(View.INVISIBLE);

						//Call make thumbnail functie
//                        saveThumbnail(tmpRecordFileName);

						//stop timer
						timer.stop();
						timer.getBase();

					}
				}

			}
		});
        
        //Dit moet blijven staan om te kunnen klikken op scherm
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_view);
        layout.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getWindow() != null && getWindow().getCurrentFocus() != null && getWindow().getCurrentFocus().getWindowToken() != null)
					inputManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				return true;
			}
		});

		setShowControls();
        
    }

//    //Thumbnail opslaan
//    public void saveThumbnail(String filename)
//    {
//
////        //TODO sleep for 5 seconds test
////        try {
////            Thread.sleep(10000);
////        } catch(InterruptedException ex) {
////            Thread.currentThread().interrupt();
////        }
////
////
////        //TODO Create thumbnail
////        tmpThumbNail = ThumbnailUtils.createVideoThumbnail(filename, MediaStore.Video.Thumbnails.MINI_KIND);
////        Log.d("Yo", "" + tmpThumbNail);
////        Log.d("Filename", "" + filename);
////
////
////        //replace .mp4 with .jpg
////        if (filename.endsWith(".mp4")) {
////            filename = filename.substring(0, filename.length() - 4) + ".jpg";
////        }
////
////		//TODO Save file to thumbnails folder
////        try {
////            FileOutputStream out = new FileOutputStream (filename);
////            tmpThumbNail.compress(Bitmap.CompressFormat.JPEG, 60, out);
////            out.flush();
////            out.close();
////        } catch(Exception e) {
////
////            Log.v(TAG, "EXEPTION=" + e);
////
////        }
//
//    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// Activate the navigation drawer toggle
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//TODO icoontjes toevoegen?
	//Hamburger menu items toevoegen
	private void addDrawerItems() {
		String[] osArray = { getString(R.string.liveDrawerStr), getString(R.string.clipsDrawerStr), getString(R.string.cameraDrawerStr)};
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
		mDrawerList.setAdapter(mAdapter);

		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position)
				{
					//LIVE
					case 0:

						//Openen van Live View
						Intent b = new Intent(getApplicationContext(), MainActivity.class);

						startActivity(b);
						finish();
						break;

					//CLIPS
					case 1:

						//Openen van Clips View
						Intent a = new Intent(getApplicationContext(), Clips.class);

						//Put recordpath
						a.putExtra("Record Path", getRecordPath());

						startActivity(a);
						break;


					//CAMERA's
					case 2:
						//Openen van Camera View
						Intent c = new Intent(getApplicationContext(), Camera.class);

						startActivity(c);
						break;
				}
				return;

			}
		});
	}

	//Hamburger menu opstarten
	private void setupDrawer() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawerOpenStr, R.string.drawerDichtStr) {

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(getString(R.string.hoofMenuStr));
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mActivityTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

    public void onClick(View v) 
	{
		SharedSettings.getInstance().loadPrefSettings();
		if (player != null)
		{

			camUrl = Camera.getCurrentCameraUrl();

			player.getConfig().setConnectionUrl(camUrl);
			Log.d("Camurl =", "" + camUrl);

			if (player.getConfig().getConnectionUrl().isEmpty())
				return;


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

				//record only
				conf.setMode(PlayerModes.PP_MODE_RECORD);
				
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
		camUrl = Camera.getCurrentCameraUrl();

		Log.e("SDL", "onResume()");
		super.onResume();
		if (player != null)
			player.onResume();
  	}

  	@Override
	protected void onStart() 
  	{

		camUrl = Camera.getCurrentCameraUrl();

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

		camUrl = Camera.getCurrentCameraUrl();

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
	
	protected void setUIDisconnected()
	{
		setTitle(R.string.app_name);
		btnConnect.setText(getString(R.string.VerbindenString));
		playing = false;
	}

	//Tijdens verbinden
	protected void setHideControls()
	{
		btnConnect.setVisibility(View.GONE);
		playIcon.setVisibility(View.GONE);
		recordCntrlsArea.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}

	//Wanneer nog niet verbonden
	protected void setShowControls()
	{
		setTitle(R.string.app_name);
		progressBar.setVisibility(View.GONE);
		btnConnect.setVisibility(View.VISIBLE);
		playIcon.setVisibility(View.VISIBLE);
		recordCntrlsArea.setVisibility(View.INVISIBLE);

	}

	private void showStatusView() 
	{
		player.setVisibility(View.INVISIBLE);
		playerStatusText.setVisibility(View.INVISIBLE);
		//player.setAlpha(0.0f);
		playerStatusText.setVisibility(View.VISIBLE);


	}
	
	private void showVideoView() 
	{
        playerStatusText.setVisibility(View.INVISIBLE);
 		player.setVisibility(View.VISIBLE);
		playerStatusText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);


		SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
		
		setTitle("");
	}
    
	private void startProgressTask(String text)
	{
		stopProgressTask();
	    
	    mProgressTask = new StatusProgressTask(text);
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
                
            	Runnable uiRunnable;
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
//    	            	playerStatusText.setGravity(Gravity.NO_GRAVITY);
    	            	
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

}