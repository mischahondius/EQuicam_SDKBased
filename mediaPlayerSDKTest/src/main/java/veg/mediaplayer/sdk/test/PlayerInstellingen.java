/*
 *
 * Mischa Hondius, 6053017.
 * University of Amsterdam
 * SDK Used by Video Experts Group
 *
 */

package veg.mediaplayer.sdk.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PlayerInstellingen {

	// misc
	public boolean 							ShowNetworkStausControl = false;
	public boolean 							AllowFullscreenMode = true;
	public int 								LockPlayerViewOrientation = 0; 		// 0 - unlock, 1 - Landscape, 2 - Portrait, 3 - current

	// ad control
	public boolean 							AdShowForever = true;
	public boolean 							AdShowWithCloseButton = false;
	public boolean 							AdShowClickAndClose = false;

	// connection
	public int 								connectionProtocol = -1;    		// 0 - udp, 1 - tcp, 2 - http, -1 - AUTO
	public int 								connectionDetectionTime = 5000;		// in milliseconds
	public int			 					connectionBufferingTime = 3000;    	// in milliseconds

	// decoder
	public int 								decoderType = 1;                	// 0 - soft, 1 - hard stagefright

	// renderer
	public int 								rendererType = 1;                	// 0 - SDL, 1 - pure OpenGL
	public int 								rendererEnableColorVideo = 1;    	// 0 - grayscale, 1 - color
	public int 								rendererEnableAspectRatio = 1;    	// 0 - resize, 1 - aspect

	// synchro
	public int 								synchroEnable = 1;                	// enable audio video synchro
	public int 								synchroNeedDropVideoFrames = 1;    	// drop video frames if it older

	public long 							OpenAdLastTime = 0;
	private Context 						m_Context = null;
	private SharedPreferences 				settings = null;
	private SharedPreferences.Editor 		editor = null;
	private static volatile 				PlayerInstellingen _inst = null;

	private PlayerInstellingen(final Context mContext) {

		//Get context
		m_Context = mContext;
	}

	public static synchronized PlayerInstellingen getInstance(final Context mContext) {

		if (_inst == null) {
			_inst = new PlayerInstellingen(mContext);
			_inst.loadPrefSettings();
			_inst.savePrefSettings();
		}

		return _inst;
	}

	public static synchronized PlayerInstellingen getInstance() {

		return _inst;
	}

	//Playerinstellingen laden
	public void loadPrefSettings() {

			// load preferences settings to local variables
		if (settings == null)
			settings = PreferenceManager.getDefaultSharedPreferences(m_Context);

		ShowNetworkStausControl = settings.getBoolean("ShowNetworkStausControl", false);
		AllowFullscreenMode = settings.getBoolean("AllowFullscreenMode", true);
		LockPlayerViewOrientation = settings.getInt("LockPlayerViewOrientation", 0);

		connectionProtocol = settings.getInt("connectionProtocol", -1);
		connectionDetectionTime = settings.getInt("connectionDetectionTime", 5000);
		connectionBufferingTime = settings.getInt("connectionBufferingTime", 3000);

		AdShowForever = settings.getBoolean("AdShowForever", true);
		AdShowWithCloseButton = settings.getBoolean("AdShowWithCloseButton", false);
		AdShowClickAndClose = settings.getBoolean("AdShowClickAndClose", false);

		decoderType = settings.getInt("decoderType", 1);
		rendererType = settings.getInt("rendererType", 1);
		rendererEnableColorVideo = settings.getInt("rendererEnableColorVideo", 1);
		rendererEnableAspectRatio = settings.getInt("rendererEnableAspectRatio", 1);
		synchroEnable = settings.getInt("synchroEnable", 1);
		synchroNeedDropVideoFrames = settings.getInt("synchroNeedDropVideoFrames", 1);

		OpenAdLastTime = settings.getLong("OpenAdLastTime", 0);
	}

	//Playerinstellingen opslaan
	public void savePrefSettings() {

		if (settings == null)
			settings = PreferenceManager.getDefaultSharedPreferences(m_Context);

		// save preferences settings
		if (editor == null)
			editor = settings.edit();


		editor.putBoolean("ShowNetworkStausControl", ShowNetworkStausControl);
		editor.putBoolean("AllowFullscreenMode", AllowFullscreenMode);
		editor.putInt("LockPlayerViewOrientation", LockPlayerViewOrientation);

		editor.putInt("connectionProtocol", connectionProtocol);
		editor.putInt("connectionDetectionTime", connectionDetectionTime);
		editor.putInt("connectionBufferingTime", connectionBufferingTime);

		editor.putBoolean("AdShowForever", AdShowForever);
		editor.putBoolean("AdShowWithCloseButton", AdShowWithCloseButton);
		editor.putBoolean("AdShowClickAndClose", AdShowClickAndClose);

		editor.putInt("decoderType", decoderType);
		editor.putInt("rendererType", rendererType);
		editor.putInt("rendererEnableColorVideo", rendererEnableColorVideo);
		editor.putInt("rendererEnableAspectRatio", rendererEnableAspectRatio);
		editor.putInt("synchroEnable", synchroEnable);
		editor.putInt("synchroNeedDropVideoFrames", synchroNeedDropVideoFrames);

		editor.putLong("OpenAdLastTime", OpenAdLastTime);

		editor.commit();
	}

}
