package com.robotium.solo;


import java.lang.reflect.Method;
import java.util.Locale;
import android.app.Instrumentation;
import android.content.Context;
import android.net.ConnectivityManager;


/**
 * Contains System methods. Examples are: setDeviceLocale(String language, String country),
 * setMobileData(Boolean turnedOn).
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

public class SystemUtils {
	private Instrumentation instrumentation;

	public SystemUtils(Instrumentation instrumentation){
		this.instrumentation = instrumentation;
	}

	/**
	 * Sets the device locale. Requires android.permission.CHANGE_CONFIGURATION in the AndroidManifest.xml of the application under test.
	 * 
	 * @param language the language e.g. "en"
	 * @param country the country e.g. "US"
	 */

	public void setDeviceLocale(String language, String country){

		try { 
			Locale locale = new Locale(language, country);
			Locale.setDefault(locale);

			Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");                
			Object am=activityManagerNative.getMethod("getDefault").invoke(activityManagerNative); 
			Object config=am.getClass().getMethod("getConfiguration").invoke(am); 
			config.getClass().getDeclaredField("locale").set(config, locale); 
			config.getClass().getDeclaredField("userSetLocale").setBoolean(config, true); 

			am.getClass().getMethod("updateConfiguration",android.content.res.Configuration.class).invoke(am,config); 

		}catch (Exception e) { 
			e.printStackTrace(); 
		}
	}

	/**
	 * Sets if mobile data should be turned on or off. Requires android.permission.CHANGE_NETWORK_STATE in the AndroidManifest.xml of the application under test.
	 * 
	 * @param turnedOn true if mobile data is to be turned on and false if not
	 */

	public void setMobileData(Boolean turnedOn){
		ConnectivityManager dataManager=(ConnectivityManager)instrumentation.getTargetContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		Method dataClass = null;
		try {
			dataClass = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			dataClass.setAccessible(true);
			dataClass.invoke(dataManager, turnedOn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets if mobile wifi should be turned on or off. Requires following permissions in the AndroidManifest.xml of the application under test. 
	 * 1. android.permission.ACCESS_WIFI_STATE 
	 * 2. android.permission.CHANGE_WIFI_STATE 
	 * 
	 * @param turnedOn true if mobile wifi is to be turned on and false if not
	 */
	static void setWiFiData(Boolean turnedOn){
			WifiManager wifiManager = (WifiManager)instrumentation.getTargetContext().getSystemService(Context.WIFI_SERVICE);
			if (turnedOn == true && !wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			} else if (turnedOn == false && wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
		}
	
}
