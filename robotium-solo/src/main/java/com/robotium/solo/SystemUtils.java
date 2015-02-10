package com.robotium.solo;


import java.lang.reflect.Method;
import android.app.Instrumentation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;


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
	 * Sets if wifi data should be turned on or off. Requires android.permission.CHANGE_WIFI_STATE in the AndroidManifest.xml of the application under test. 
	 *  
	 * 
	 * @param turnedOn true if mobile wifi is to be turned on and false if not
	 */

	public void setWiFiData(Boolean turnedOn){
		WifiManager wifiManager = (WifiManager)instrumentation.getTargetContext().getSystemService(Context.WIFI_SERVICE);
		try{
			wifiManager.setWifiEnabled(turnedOn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
