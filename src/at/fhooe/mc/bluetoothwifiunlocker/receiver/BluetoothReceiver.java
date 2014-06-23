/**
 * This @class handles the BroadcastReceiver for the Bluetooth-Connections.
 */
package at.fhooe.mc.bluetoothwifiunlocker.receiver;

import java.util.ArrayList;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class BluetoothReceiver extends BroadcastReceiver {

	private Utils utils;

	/**
	 * Receives the CONNECTED and DISCONNECTED Action from the
	 * BroadcastReceiver. If a bluetooth-device was connected successful, it
	 * gets looked up if it is stored (in the array where the chosen bluetooth
	 * devices are stored). If so, the device gets unlocked by calling the
	 * KeyguardManager's function disableKeyguard(). Additionally, the new
	 * status (=display unlocked) is saved in the SharedPreferences and the
	 * currently showing notification gets updated. If the DISCONNECTED-Action
	 * is called, it is checked whether the deviceadmin is active or not. If it
	 * is active, the screen gets locked by the DevicePolicyManager and again,
	 * the current status in the SharedPreferences and the Notification is
	 * updated. If the admin isn't active, it gets activated by calling the
	 * method enableAdmin() from the @class Utils and then the screen gets
	 * locked as described above.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("Bt Receiver", "onReceive");

		utils = new Utils();

		String action = intent.getAction();
		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		utils.enableAdmin(context);

		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			ArrayList<String> savedDevices = utils.convertToArrayList(utils
					.loadArray(context, "saved_bluetooth_devices"));

			for (int i = 0; i < savedDevices.size(); i++) {

				if (savedDevices.get(i).equals(device.getName())) {

					KeyguardManager km = (KeyguardManager) context
							.getSystemService(Context.KEYGUARD_SERVICE);
					final KeyguardManager.KeyguardLock kl = km
							.newKeyguardLock("MyKeyguardLock");
					kl.disableKeyguard();

					SharedPreferences settings = context.getSharedPreferences(
							"BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "unlocked");
					e.putString("device", device.getName());
					e.commit();

					Toast.makeText(context, "BT: UNLOCKED", Toast.LENGTH_SHORT).show();

					utils.showNotification(true, context, "Bluetooth");
				}
			}
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			enableKeyguard(context);
		} 
	}

	public void disableKeyguard(Context context, String device) {
		ArrayList<String> savedDevices = utils.convertToArrayList(utils
				.loadArray(context, "saved_bluetooth_devices"));

		for (int i = 0; i < savedDevices.size(); i++) {

			if (savedDevices.get(i).equals(device)) {

				KeyguardManager km = (KeyguardManager) context
						.getSystemService(Context.KEYGUARD_SERVICE);
				final KeyguardManager.KeyguardLock kl = km
						.newKeyguardLock("MyKeyguardLock");
				kl.disableKeyguard();

				SharedPreferences settings = context.getSharedPreferences(
						"BT_WIFI", 0);
				Editor e = settings.edit();
				e.putString("lockState", "unlocked");
				e.putString("device", device);
				e.commit();

				Log.i("BluetoothReceiver", "Keyguard disabled");

				utils.showNotification(true, context, "Bluetooth");
			}
		}
	}

	public void enableKeyguard(Context context) {
		// Device has disconnected
		DevicePolicyManager deviceManger = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);

		ComponentName compName = new ComponentName(context, MyAdmin.class);

		boolean active = deviceManger.isAdminActive(compName);

		ConnectivityManager myConnManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo myNetworkInfo = myConnManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		WifiManager myWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();

		utils.enableAdmin(context);

		ArrayList<String> savedDevices = utils.convertToArrayList(utils
				.loadArray(context, "saved_wifi_networks"));

		boolean correctConnection = false;
		if (myNetworkInfo.isConnected()) {
			// check if additionally connected to saved network
			for (int i = 0; i < savedDevices.size(); i++) {
				if (savedDevices.get(i).compareTo(
						myWifiInfo.getSSID().substring(1,
								myWifiInfo.getSSID().length() - 1)) == 0) {
					correctConnection = true;
				}
			}
		}

		if (!correctConnection) {
			if (active) {
				deviceManger.lockNow();
				SharedPreferences settings = context.getSharedPreferences(
						"BT_WIFI", 0);
				Editor e = settings.edit();
				e.putString("lockState", "locked");
				e.putString("device", "not connected");
				Log.i("BluetoothReceiver", "Keyguard enabled");
				Toast.makeText(context, "BT: LOCKED", Toast.LENGTH_SHORT).show();
				e.commit();
				utils.showNotification(false, context, "Bluetooth");
			} else {

				Intent i = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
				i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						R.string.devAdmin_explanation);
				context.startActivity(i);

				try {
					wait(1000 * 10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				utils.enableAdmin(context);
				active = deviceManger.isAdminActive(compName);
				if (active) {
					deviceManger.lockNow();

					SharedPreferences settings = context.getSharedPreferences(
							"BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "locked");
					e.putString("device", "not connected");
					e.commit();
					Log.i("BluetoothReceiver", "Keyguard enabled");
					utils.showNotification(false, context, "Bluetooth");
				}
			}
		}
	}
}
