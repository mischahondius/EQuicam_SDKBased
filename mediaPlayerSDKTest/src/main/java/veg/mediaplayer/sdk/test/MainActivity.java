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
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.app.ProgressDialog;

public class MainActivity extends ActionBarActivity implements OnClickListener, MediaPlayer.MediaPlayerCallback {

	//Opname map locatie
	public String 						opnameMap;

	//tag voor logs
	private static final String 		TAG = "EQuicamAPP";

	//Record split time (meer dan de maximale opname tijd, dus geen split)
	int 								rec_split_time = 240;

	//Buttons MainActivity
	private Button 						btnConnect;
	private FrameLayout 				recordCntrlsArea;
	private ImageButton					btnHighlight;
	private Chronometer 				timer;
	private ImageView 					playIcon;
	private ProgressBar 				progressBar;

	//Sharedprefs
	private static final int 			PREFERENCE_MODE_PRIVATE = 0;
	private SharedPreferences           sharedPrefs;
	private SharedPreferences.Editor 	sharedPrefsEditor;

	//Hamburger Menu
	private ListView					hamBurgerOptiesLijst;
	private DrawerLayout				hamBurgerLayout;
	private ActionBarDrawerToggle 		hamBurgerActionBarToggle;

	//Bools
	private boolean 					opnameAangevraagd = false;
	private boolean 					aanHetAfspelen = false;
	private boolean						aanHetOpnemen = false;

	private StatusProgressTask 			mProgressTask = null;
	private MediaPlayer 				player = null;
	private MainActivity 				mthis = null;
	private TextView 					playerStatusText = null;
	private MulticastLock 				multicastLock = null;

	//SDK instellingen
	private enum PlayerStates {
		Busy,
		ReadyForUse
	}
	private enum PlayerConnectType {
		Normal,
		Reconnecting
	}
	private PlayerStates 				player_state = PlayerStates.ReadyForUse;
	private PlayerConnectType 			reconnect_type = PlayerConnectType.Normal;
	private int 						mOldMsg = 0;

	//Bool voor check of het inladen van clips klaar is
	public static boolean 				doneLoadingClips = false;

	// Event handler voor de player
	private Handler handler = new Handler() {
		String strText = "Verbinden";

		@Override
		public void handleMessage(Message msg) {
			PlayerNotifyCodes status = (PlayerNotifyCodes) msg.obj;
			switch (status) {
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
					Log.v(TAG, "Opname gestart");
				{
					String sFile = player.RecordGetFileName(1);
				}
				break;

				case CP_RECORD_STOPPED:
					Log.v(TAG, "Opname gestopt");
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
					if (player_state != PlayerStates.Busy) {
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
					if (player_state != PlayerStates.Busy) {
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

	// callback van de Native Player
	@Override
	public int OnReceiveData(ByteBuffer buffer, int size, long pts) {
		Log.e(TAG, "Form Native Player OnReceiveData: size: " + size + ", pts: " + pts);
		return 0;
	}

	// Alle feedback van player gaat via de handler
	@Override
	public int Status(int arg) {

		PlayerNotifyCodes status = PlayerNotifyCodes.forValue(arg);
		if (handler == null || status == null)
			return 0;

		Log.e(TAG, "Form Native Player status: " + arg);
		switch (PlayerNotifyCodes.forValue(arg)) {
			default:
				Message msg = new Message();
				msg.obj = status;
				handler.removeMessages(mOldMsg);
				mOldMsg = msg.what;
				handler.sendMessage(msg);
		}

		return 0;
	}

	//Opname map aanmaken indien nodig, en locatie ophalen
	public static String getOpnameMap() {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM), "EQuicam Clips");

		if (!mediaStorageDir.exists()) {
			if (!(mediaStorageDir.mkdirs() || mediaStorageDir.isDirectory())) {
				Log.e(TAG, "Niet gelukt om EQuicam Clips Map aan te maken" + mediaStorageDir.getPath());
				return "";
			}
		}
		return mediaStorageDir.getPath();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//Get camera url van SharedPrefs
		getCamUrlfromSharedPrefs();

		//Wifi gedoe van SDK ??
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifi.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();

		//View laden
		setContentView(R.layout.live);
		mthis = this;

		//Hamburger Drawer Menu opzetten
		hamBurgerOptiesLijst = (ListView) findViewById(R.id.navList);
		hamBurgerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		addDrawerItems();
		setupDrawer();
		getSupportActionBar().setTitle(R.string.liveDrawerStr);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		//Get SharedPrefs, waarin alle Player instellingen zich bevinden
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		SharedSettings.getInstance(this).loadPrefSettings();
		SharedSettings.getInstance().savePrefSettings();

		//Get Player status textview
		playerStatusText = (TextView) findViewById(R.id.playerStatusText);

		//Get Player
		player = (MediaPlayer) findViewById(R.id.playerView);

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
		opnameMap = getOpnameMap();

		//Get timer
		timer = (Chronometer) findViewById(R.id.timerView);

		//Get player PLAY icon
		playIcon = (ImageView) findViewById(R.id.playIcon);

		//Get progressbar icon
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		//Progressbar kleur aanpassen naar rood
		progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		hamBurgerActionBarToggle.syncState();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		hamBurgerActionBarToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// Toggle inschakelen van het Hamburger Menu
		if (hamBurgerActionBarToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//Hamburger menu items toevoegen
	private void addDrawerItems() {
		String[] osArray = {getString(R.string.liveDrawerStr), getString(R.string.clipsDrawerStr), getString(R.string.cameraDrawerStr)};
		ArrayAdapter<String> hamBurgerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
		hamBurgerOptiesLijst.setAdapter(hamBurgerArrayAdapter);

		hamBurgerOptiesLijst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
					//LIVE
					case 0:

						//Openen van Live View
						Intent b = new Intent(getApplicationContext(), MainActivity.class);

						startActivity(b);
						finish();
						break;

					//CLIPS
					case 1:

						//als aan het opnemen, geef melding
						if (aanHetOpnemen){
							Toast.makeText(getApplicationContext(), "Stop eerst de opname.", Toast.LENGTH_SHORT).show();
							break;
						}

						else {
							//Start dialoog venster op nieuwe thread
							if (launchRingDialog()) {

								//Openen van Clips View
								Intent a = new Intent(getApplicationContext(), ClipsActivity.class);

								//Put recordpath
								a.putExtra("Record Path", getOpnameMap());

								startActivity(a);
								break;
							}
						}


						//CAMERA's
					case 2:

						//als aan het opnemen, geef melding
						if (aanHetOpnemen){
							Toast.makeText(getApplicationContext(), "Stop eerst de opname.", Toast.LENGTH_SHORT).show();
							break;
						}

						else {
							//Openen van CameraActivity View
							Intent c = new Intent(getApplicationContext(), CameraActivity.class);

							startActivity(c);
							break;
						}
				}
				return;

			}
		});
	}

	//Hamburger menu set up
	private void setupDrawer() {
		hamBurgerActionBarToggle = new ActionBarDrawerToggle(this, hamBurgerLayout, R.string.drawerOpenStr, R.string.drawerDichtStr) {

			//Wanneer Drawer volledig open is
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(getString(R.string.hoofdMenuStr));

				// Update menubalk
				invalidateOptionsMenu();
			}

			//Wanneer Drawer volledig dicht is
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(R.string.liveDrawerStr);

				// Update menubalk
				invalidateOptionsMenu();
			}
		};

		hamBurgerActionBarToggle.setDrawerIndicatorEnabled(true);
		hamBurgerLayout.setDrawerListener(hamBurgerActionBarToggle);
	}

	//Verbinden met camera en beeld weergeven in player
	public void onClick(View v) {

		//Indien url 1x is afgespeeld, opslaan naar sharedprefs
		setCamUrltoSharedPrefs();

		//Timer op nul
		timer.setBase(SystemClock.elapsedRealtime());

		SharedSettings.getInstance().loadPrefSettings();
		if (player != null) {

			player.getConfig().setConnectionUrl(CameraActivity.getCurrentCameraUrl());
			Log.d("Camurl =", "" + CameraActivity.getCurrentCameraUrl());

			//Check of Cameraadres niet leeg is
			if (player.getConfig().getConnectionUrl().isEmpty())
				return;

			//Wanneer player wordt afgesloten
			player.Close();
			if (aanHetAfspelen) {
				setUIDisconnected();
			} else {

				SharedSettings sett = SharedSettings.getInstance();
				boolean bPort = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
				int aspect = bPort ? 1 : sett.rendererEnableAspectRatio;
				MediaPlayerConfig conf = new MediaPlayerConfig();

				//Verberg player
				player.setVisibility(View.INVISIBLE);

				//Player instellen
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

				//Recorder instellen
				if (opnameAangevraagd) {
					int record_flags = PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_AUTO_START);
					conf.setRecordPath(getOpnameMap());
					conf.setRecordFlags(record_flags);
					conf.setRecordSplitTime(0);
					conf.setRecordSplitSize(0);
				} else {
					conf.setRecordPath("");
					conf.setRecordFlags(0);
					conf.setRecordSplitTime(0);
					conf.setRecordSplitSize(0);
				}

				// Player openen
				player.Open(conf, mthis);

				//Niet kijken, wel recorden
				conf.setMode(PlayerModes.PP_MODE_RECORD);

				//Boolean setten: aan het afspelen momenteel
				aanHetAfspelen = true;
			}
		}
	}

	protected void onPause() {

		Log.e("SDL", "onPause()");
		super.onPause();

		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.commit();

		if (player != null)
			player.onPause();
	}

	@Override
	protected void onResume() {

		Log.e("SDL", "onResume()");
		super.onResume();
		if (player != null)
			player.onResume();
	}

	@Override
	protected void onStart() {

		Log.e("SDL", "onStart()");
		super.onStart();
		if (player != null)
			player.onStart();

	}

	@Override
	protected void onStop() {

		Log.e("SDL", "onStop()");
		super.onStop();
		if (player != null)
			player.onStop();

		//als aan het opnemen, stop opname
		if (aanHetOpnemen){
			stopOpname();
		}
	}

	@Override
	protected void onDestroy() {
		Log.e("SDL", "onDestroy()");

		if (player != null)
			player.onDestroy();

		stopProgressTask();
		System.gc();

		if (multicastLock != null) {
			multicastLock.release();
			multicastLock = null;
		}

		//als aan het opnemen, stop opname
		if (aanHetOpnemen){
			stopOpname();
		}

		SharedSettings.getInstance().savePrefSettings();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

		player.Close();
		if (!aanHetAfspelen) {
			super.onBackPressed();
			return;
		}

		//als aan het opnemen, stop opname
		if (aanHetOpnemen){
			stopOpname();
		}

		setUIDisconnected();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		Log.e("SDL", "onWindowFocusChanged(): " + hasFocus);
		super.onWindowFocusChanged(hasFocus);
		if (player != null)
			player.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onLowMemory() {
		Log.e("SDL", "onLowMemory()");
		super.onLowMemory();
		if (player != null)
			player.onLowMemory();
	}

	protected void setUIDisconnected() {
		btnConnect.setText(getString(R.string.VerbindenString));
		aanHetAfspelen = false;
	}

	//Tijdens verbinden: wat te laten zien
	protected void setHideControls() {
		btnConnect.setVisibility(View.GONE);
		playIcon.setVisibility(View.GONE);
		recordCntrlsArea.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}

	//Wanneer nog niet verbonden: wat te laten zien
	protected void setShowControls() {
		progressBar.setVisibility(View.GONE);
		btnConnect.setVisibility(View.VISIBLE);
		playIcon.setVisibility(View.VISIBLE);
		recordCntrlsArea.setVisibility(View.INVISIBLE);
	}

	private void showStatusView() {

		player.setVisibility(View.INVISIBLE);
		playerStatusText.setVisibility(View.INVISIBLE);
		playerStatusText.setVisibility(View.VISIBLE);
	}

	private void showVideoView() {

		playerStatusText.setVisibility(View.INVISIBLE);
		player.setVisibility(View.VISIBLE);
		playerStatusText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
	}

	private void startProgressTask(String text) {

		stopProgressTask();
		mProgressTask = new StatusProgressTask(text);
		executeAsyncTask(mProgressTask, text);
	}

	private void stopProgressTask() {

		playerStatusText.setText("");

		if (mProgressTask != null) {
			mProgressTask.stopTask();
			mProgressTask.cancel(true);
		}
	}

	private class StatusProgressTask extends AsyncTask<String, Void, Boolean> {

		String strProgressTextSrc;
		String strProgressText;
		Rect bounds = new Rect();
		boolean stop = false;

		public StatusProgressTask(String text) {
			stop = false;
			strProgressTextSrc = text;
		}

		public void stopTask() {
			stop = true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		//Verbinden tekst maken met wisselende puntjes
		@Override
		protected Boolean doInBackground(String... params) {

			try {
				if (stop) return true;

				String maxText = getString(R.string.GeenVerbindingString) + ".....";
				int len = maxText.length();
				playerStatusText.getPaint().getTextBounds(maxText, 0, len, bounds);

				strProgressText = strProgressTextSrc + "...";

				Runnable uiRunnable;
				uiRunnable = new Runnable() {
					public void run() {
						if (stop) return;

						playerStatusText.setText(strProgressText);

						RelativeLayout.LayoutParams layoutParams =
								(RelativeLayout.LayoutParams) playerStatusText.getLayoutParams();

						layoutParams.width = bounds.width();
						playerStatusText.setLayoutParams(layoutParams);

						synchronized (this) {
							this.notify();
						}
					}
				};

				int nCount = 4;
				do {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						stop = true;
					}

					if (stop) break;

					if (nCount <= 3) {
						strProgressText = strProgressTextSrc;
						for (int i = 0; i < nCount; i++)
							strProgressText = strProgressText + ".";
					}

					synchronized (uiRunnable) {
						runOnUiThread(uiRunnable);
						try {
							uiRunnable.wait();
						} catch (InterruptedException e) {
							stop = true;
						}
					}

					if (stop) break;

					nCount++;
					if (nCount > 3) {
						nCount = 1;
						strProgressText = strProgressTextSrc;
					}
				}

				while (!isCancelled());
			} catch (Exception e) {
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			mProgressTask = null;
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
		}
	}

	static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) {
		{
			task.execute(params);
		}
	}

	//Progress dialoogvenster functie
	public boolean launchRingDialog() {

		final ProgressDialog pd = ProgressDialog.show(this, "Clips laden", "Even geduld s.v.p.");

		Thread progressThread = new Thread() {

			@Override
			public void run() {

				while (!doneLoadingClips) {
					//Wachten
				}

				//Reset doneloading
				doneLoadingReSet();

				pd.dismiss();
			}
		};
		progressThread.start();

		return  true;
	}

	//Start opname functie
	public void startOpname(){

		int record_flags = PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_AUTO_START) | PlayerRecordFlags.forType(PlayerRecordFlags.PP_RECORD_SPLIT_BY_TIME); //1 - auto start
		player.RecordSetup(getOpnameMap(), record_flags, rec_split_time, 0, "");
		player.RecordStart();

		//Aan het opnemen boolean setten
		aanHetOpnemen = true;

		Toast.makeText(getApplicationContext(), getString(R.string.OpnameGestartString), Toast.LENGTH_SHORT).show();

		//Start knipperen van rec button
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

	//Stop opname functie
	public void stopOpname(){

		player.RecordStop();
		aanHetOpnemen = false;

		Toast.makeText(getApplicationContext(), getString(R.string.OpnameGestoptString), Toast.LENGTH_SHORT).show();
		btnHighlight.clearAnimation();
		btnHighlight.setVisibility(View.INVISIBLE);

		//stop timer
		timer.stop();
	}

	//Recbutn Listener
	public void recordBtnonClickListener(View view) {
		opnameAangevraagd = !opnameAangevraagd;

		if (opnameAangevraagd) {

			//start opname, als de player bestaat
			if (player != null) {
				startOpname();
			}

		} else {

			//stop opname, als de player bestaat
			if (player != null) {
				stopOpname();
			}
		}
	}

	//Camera url opslaan naar sharedprefs
	public void setCamUrltoSharedPrefs(){

		//Voorbereiden van editor
		sharedPrefs = getPreferences(PREFERENCE_MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();

		//commit prefs
		sharedPrefsEditor.putString("camUrl", CameraActivity.getCurrentCameraUrl());
		sharedPrefsEditor.commit();

		Log.d(TAG, "camurl GESET in sharedprefs:" + CameraActivity.getCurrentCameraUrl());

	}

	//Cam url ophalen uit sharedprefs
	public void getCamUrlfromSharedPrefs(){

		//Voorbereiden van editor
		sharedPrefs = getPreferences(PREFERENCE_MODE_PRIVATE);

		//log om te checken wat gebeurt
		Log.d(TAG, "camurl geget van sharedprefs:" + CameraActivity.getCurrentCameraUrl());

	    //set in CAmeraActivity
		CameraActivity.setCameraUrl(sharedPrefs.getString("camUrl", CameraActivity.getDefaultUrl()));
		Log.d(TAG, "camurl in Cameraactivity opgeslagen als:" + CameraActivity.getCurrentCameraUrl());

	}

	//Doneloadingsetter
	public static void doneLoadingSet (){
		doneLoadingClips = true;
		Log.d(TAG, "Klaar met clips laden!");

	}

	//Doneloadingresetter
	public static void doneLoadingReSet() {
		doneLoadingClips = false;
		Log.d(TAG, "DoneLoading is gereset");

	}
}