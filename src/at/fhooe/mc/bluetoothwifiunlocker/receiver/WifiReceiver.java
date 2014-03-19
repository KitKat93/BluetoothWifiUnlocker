/**
 * This @class handles the BroadcastReceiver for the Wifi-Connections.
 */
package at.fhooe.mc.bluetoothwifiunlocker.receiver;

import java.util.ArrayList;
import java.util.List;

import android.app.KeyguardManager;
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
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;

public class WifiReceiver extends BroadcastReceiver {

	private Utils utils;
	private List<ScanResult> scanResults;

	/**
	 * Receives the SCANRESULTS and CONNECTIVITY Action from the
	 * BroadcastReceiver. If the scan-results are available, they found wifis
	 * are added to the appropriate list. If the Connectivity-Action is called,
	 * it is checked if it comes from the Wifi-Service. If so, the method
	 * setLockState() is called. looked up if it is stored (in the array where
	 * the chosen bluetooth devices are stored).
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

		utils = new Utils();

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
					networks.add(scanResults.get(j).SSID);
				}
			}

			utils.saveArray(context, utils.convertToStringArray(networks),
					"all_wifi_networks");
		}
	}

	/**
	 * If the device is connected to a wifi-network, it is checked whether this
	 * network was chosen by the user to unlock the phone and therefore stored
	 * in an array. If it was found, the device-display gets unlocked by calling
	 * the KeyguardManager's function disableKeyguard(). Additionally, the new
	 * status (=display unlocked) is saved in the SharedPreferences and the
	 * currently showing notification gets updated. If the connection was lost,
	 * it is checked whether the deviceadmin is active or not. If it is active,
	 * the screen gets locked by the DevicePolicyManager and again, the
	 * current status in the SharedPreferences and the Notification is updated.
	 * If the admin isn't active, it gets activated by calling the method
	 * enableAdmin() from the @class Utils and then the screen gets locked as
	 * described above.
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	private void setLockState(Context context) {

		ConnectivityManager myConnManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo myNetworkInfo = myConnManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		WifiManager myWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();

		utils.enableAdmin(context);

		if (myNetworkInfo.isConnected()) {


			ArrayList<String> savedDevices = utils.convertToArrayList(utils
					.loadArray(context, "saved_wifi_networks"));

			for (int i = 0; i < savedDevices.size(); i++) {
				if (savedDevices.get(i).compareTo(
						myWifiInfo.getSSID().substring(1,
								myWifiInfo.getSSID().length() - 1)) == 0) {

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
			DevicePolicyManager deviceManger = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);

			ComponentName compName = new ComponentName(context, MyAdmin.class);

			boolean active = deviceManger.isAdminActive(compName);
			if (active) {
				deviceManger.lockNow();
				SharedPreferences settings = context.getSharedPreferences(
						"BT_WIFI", 0);
				Editor e = settings.edit();
				e.putString("lockState", "locked");
				e.commit();
				utils.showNotification(false, context, "Wifi");
			} else {
				utils.enableAdmin(context);
				active = deviceManger.isAdminActive(compName);
				if (active) {
					deviceManger.lockNow();

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
}
