package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.ArrayList;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class BluetoothReceiver extends BroadcastReceiver {
	
	private Utils utils;
	private int NOTIFICATION = R.string.noti;
	private NotificationManager mNM;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		utils = new Utils();
		
		String action = intent.getAction();
		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		
		utils.enableAdmin(context);

		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Device found

			Log.i("BluetoothReceiver", "DEVICE FOUND");
			Toast.makeText(context, "DEVICE FOUND", Toast.LENGTH_SHORT).show();

		} else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			// Device is now connected
			Log.i("BluetoothReceiver", "CONNECTED TO DEVICE");
			
			ArrayList<String> savedDevices = utils.convertToArrayList(utils
					.loadArray(context, "saved_bluetooth_devices"));
			
			Toast.makeText(context, "CONNECTED TO DEVICE: " + device.getName(), Toast.LENGTH_SHORT).show();
			Toast.makeText(context, "SAVED DEVICES SIZE: " + savedDevices.size(), Toast.LENGTH_SHORT).show();
			
			for (int i = 0; i < savedDevices.size(); i++) {
				
				Toast.makeText(context, "saved Device = " + savedDevices.get(i), Toast.LENGTH_SHORT).show();
				
				if (savedDevices.get(i).equals(device.getName())) {
					
					KeyguardManager km = (KeyguardManager) context
							.getSystemService(Context.KEYGUARD_SERVICE);
					final KeyguardManager.KeyguardLock kl = km
							.newKeyguardLock("MyKeyguardLock");
					kl.disableKeyguard();
					
					Toast.makeText(context, "DEVICE UNLOCKED", Toast.LENGTH_SHORT).show();
					
					SharedPreferences settings = context.getSharedPreferences("BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "unlocked");
					e.putString("device", device.getName());
					e.remove("network");
					e.commit();	
					
					utils.showNotification(true, context, "Bluetooth");
				}
			}
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			// Done searching
			Log.i("BluetoothReceiver", "DONE SEARCHING");
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			// Device has disconnected
			Log.i("BLUETOOTH RECEIVER", "DISCONNECTED FROM DEVICE");
			Toast.makeText(context, "DISCONNECTED FROM DEVICE", Toast.LENGTH_SHORT).show();

			DevicePolicyManager deviceManger = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);

			ComponentName compName = new ComponentName(context, MyAdmin.class);

			boolean active = deviceManger.isAdminActive(compName);
			Log.i("BLUETOOTH RECEIVER", "IS ACTIVE: " + active);

			if (active) {
				deviceManger.lockNow();
				Log.i("BLUETOOTH RECEIVER", "LOCK NOW");
				Toast.makeText(context, "LOCK NOW", Toast.LENGTH_SHORT).show();
				
				SharedPreferences settings = context.getSharedPreferences("BT_WIFI", 0);
				Editor e = settings.edit();
				e.putString("lockState", "locked");
				e.commit();
				utils.showNotification(false, context, "Bluetooth");
			}
		}
	}
}
