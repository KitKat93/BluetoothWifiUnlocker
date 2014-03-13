package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.ArrayList;
import java.util.List;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.sax.StartElementListener;
import android.util.Log;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class WifiReceiver extends BroadcastReceiver {

	private Utils utils;
	private List<ScanResult> scanResults;

	@Override
	public void onReceive(Context context, Intent intent) {

		utils = new Utils();
		
		Log.i("WIFI Receiver", "onReceiver METHOD");

		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				setLockState(context);
			}
		} else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			scanResults = wifi.getScanResults();

			ArrayList<String> networks = utils.convertToArrayList(utils
					.loadArray(context, "all_wifi_networks"));

			for (int j = 0; j < scanResults.size(); j++) {
				if (!networks.contains(scanResults.get(j).SSID)) {
					Log.i("WifiReceiver", "ADDED NETWORK: " + scanResults.get(j).SSID);
					networks.add(scanResults.get(j).SSID);
				}
			}
			
			utils.saveArray(context, utils.convertToStringArray(networks), "all_wifi_networks");
		}
	}

	private void setLockState(Context context) {

		Log.i("WIFI Receiver", "DISPLAY WIFI STATE METHOD");

		ConnectivityManager myConnManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo myNetworkInfo = myConnManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		WifiManager myWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();

		utils.enableAdmin(context);

		if (myNetworkInfo.isConnected()) {

			ArrayList<String> networks = utils.convertToArrayList(utils
					.loadArray(context, "all_wifi_networks"));

			Log.i("WIFI Receiver", "IS CONNECTED");

			ArrayList<String> savedDevices = utils.convertToArrayList(utils
					.loadArray(context, "saved_wifi_networks"));

			Log.i("WIFI Receiver", "SAVED DEVICES SIZE: " + savedDevices.size());


			for (int i = 0; i < savedDevices.size(); i++) {

				Log.i("WIFI Receiver", "SAVED DEVICE: "
						+ savedDevices.get(i).toString());
				Log.i("WIFI Receiver",
						"CURRENT SSID: "
								+ myWifiInfo.getSSID().substring(1,
										myWifiInfo.getSSID().length() - 1));
				if (savedDevices.get(i).compareTo(
						myWifiInfo.getSSID().substring(1,
								myWifiInfo.getSSID().length() - 1)) == 0) {


					Log.i("WIFI Receiver", "WIFI: " + myWifiInfo.getSSID());

					KeyguardManager km = (KeyguardManager) context
							.getSystemService(Context.KEYGUARD_SERVICE);
					final KeyguardManager.KeyguardLock kl = km
							.newKeyguardLock("MyKeyguardLock");
					kl.disableKeyguard();

					SharedPreferences settings = context.getSharedPreferences(
							"BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "unlocked");
					e.putString("network", savedDevices.get(i));
					e.remove("device");
					e.commit();
					
					utils.showNotification(true, context, "Wifi");
				}
			}
		} else {
			Log.i("WifiReceiver", "DISCONNECTED");
			DevicePolicyManager deviceManger = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);

			ComponentName compName = new ComponentName(context, MyAdmin.class);

			boolean active = deviceManger.isAdminActive(compName);
			Log.i("WIFI RECEIVER", "IS ACTIVE: " + active);

			if (active) {
				Log.i("WIFI RECEIVER", "LOCK NOW");
				deviceManger.lockNow();
				Log.i("WIFI RECEIVER", "ADMINS: "
						+ deviceManger.getActiveAdmins().toString());

				KeyguardManager mKeyguardManager = (KeyguardManager) context
						.getSystemService(Context.KEYGUARD_SERVICE);
				Log.i("WIFI RECEIVER",
						"IS LOCKED? "
								+ mKeyguardManager
										.inKeyguardRestrictedInputMode());
				SharedPreferences settings = context.getSharedPreferences(
						"BT_WIFI", 0);
				Editor e = settings.edit();
				e.putString("lockState", "locked");
				e.commit();
				utils.showNotification(false, context, "Wifi");
			}
		}
	}
}
