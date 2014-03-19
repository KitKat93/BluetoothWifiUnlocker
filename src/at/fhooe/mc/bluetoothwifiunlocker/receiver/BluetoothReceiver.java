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
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;

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
	 * is active, the screen gets locked by the DevicePolicyManager and
	 * again, the current status in the SharedPreferences and the Notification
	 * is updated. If the admin isn't active, it gets activated by calling the
	 * method enableAdmin() from the @class Utils and then the screen gets
	 * locked as described above.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

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
					e.remove("network");
					e.commit();

					utils.showNotification(true, context, "Bluetooth");
				}
			}
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			// Device has disconnected
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
				utils.showNotification(false, context, "Bluetooth");
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
					utils.showNotification(false, context, "Bluetooth");
				}
			}
		}
	}
}
