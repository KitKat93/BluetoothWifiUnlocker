/**
 * This class holds all generally needed methods which are called 
 * from several classes.
 */
package at.fhooe.mc.bluetoothwifiunlocker.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.tabfragments.MainActivity;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class Utils {

	/**
	 * This method saves a given array in the SharedPreferences.
	 * 
	 * @param mContext
	 *            The application context.
	 * @param array
	 *            The array which should be stored.
	 * @param arrayName
	 *            The name of the array which should be stored.
	 * @return True if the array was saved successfully, false otherwise.
	 */
	public boolean saveArray(Context mContext, String[] array, String arrayName) {
		SharedPreferences prefs = mContext.getSharedPreferences(arrayName, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(arrayName + "_size", array.length);
		for (int i = 0; i < array.length; i++) {
			editor.putString(arrayName + "_" + i, array[i]);
		}
		return editor.commit();
	}

	/**
	 * This method loads an array by its name from the SharedPreferences.
	 * 
	 * @param mContext
	 *            The application context.
	 * @param arrayName
	 *            The name of the array which should be loaded.
	 * @return The array which was loaded or null if this wasn't possible.
	 */
	public String[] loadArray(Context mContext, String arrayName) {
		SharedPreferences prefs = mContext.getSharedPreferences(arrayName, 0);
		int size = prefs.getInt(arrayName + "_size", 0);
		String[] arr = new String[size];
		for (int i = 0; i < size; i++) {
			arr[i] = prefs.getString(arrayName + "_" + i, null);
		}
		return arr;
	}

	/**
	 * Converts a given ArrayList into a String-Array.
	 * 
	 * @param _arr
	 *            The array which should be converted.
	 * @return The new String-Array from the arraylist.
	 */
	public String[] convertToStringArray(ArrayList<String> _arr) {
		String[] stringArr = new String[_arr.size()];
		for (int i = 0; i < _arr.size(); i++) {
			stringArr[i] = _arr.get(i);
		}
		return stringArr;
	}

	/**
	 * Converts a given String-Array into an Arraylist.
	 * 
	 * @param _arr
	 *            The String-Array which should be converted to an Arraylist.
	 * @return The new Arraylist from the String-Array.
	 */
	public ArrayList<String> convertToArrayList(String[] _arr) {
		ArrayList<String> stringArr = new ArrayList<String>();
		for (int i = 0; i < _arr.length; i++) {
			stringArr.add(_arr[i]);
		}
		return stringArr;
	}

	/**
	 * This method enables the DeviceAdmin by opening the
	 * Settings-->Security-->Device Manager Site for the user at which he can
	 * activate the admin.
	 * 
	 * @param context
	 *            The Application-context.
	 */
	public void enableAdmin(Context context) {

		DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mAdminName = new ComponentName(context, MyAdmin.class);

		ActivityInfo ai = null;
		try {
			ai = context.getPackageManager().getReceiverInfo(mAdminName,
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		if (!mDevicePolicyManager.isAdminActive(mAdminName)) {
			List<ResolveInfo> avail = context
					.getPackageManager()
					.queryBroadcastReceivers(
							new Intent(
									DeviceAdminReceiver.ACTION_DEVICE_ADMIN_ENABLED),
							0);
			int count = avail == null ? 0 : avail.size();
			boolean found = false;
			for (int i = 0; i < count; i++) {
				ResolveInfo ri = avail.get(i);
				if (ai.packageName.equals(ri.activityInfo.packageName)
						&& ai.name.equals(ri.activityInfo.name)) {
					try {
						ri.activityInfo = ai;
						@SuppressWarnings("unused")
						DeviceAdminInfo dpi = new DeviceAdminInfo(context, ri);
						found = true;
					} catch (XmlPullParserException e) {
						Log.w("MainActivity", "Bad " + ri.activityInfo, e);
					} catch (IOException e) {
						Log.w("MainActivity", "Bad " + ri.activityInfo, e);
					}
					break;
				}
			}
			if (!found) {
				Log.w("MainActivity", "Request to add invalid device admin: "
						+ mAdminName);
				return;
			}
		}
	}

	/**
	 * This method is called after every status-change (locked/unlocked) of the
	 * application, generates the appropriate Notification and shows it.
	 * 
	 * @param running
	 *            True if the device is currently unlocked, false otherwise.
	 * @param context
	 *            The current Application-context.
	 * @param _callingClass
	 *            The name of the class which called this method.
	 */
	@SuppressLint("NewApi")
	public void showNotification(boolean running, Context context,
			String _callingClass) {

		Notification.Builder notification = new Notification.Builder(context);
		notification
				.setContentTitle("BluetoothWifiUnlocker")
				.setContentText(
						context.getString(R.string.noti_lockstate_locked))
				.setSmallIcon(R.drawable.main_small).setOngoing(true);

		if (_callingClass.equals("Wifi") && running) {
			notification
					.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(
							context.getString(R.string.noti_lockstate_unlocked))
					.setSmallIcon(R.drawable.wifi_small).setOngoing(true);
		} else if (_callingClass.equals("Bluetooth") && running) {
			notification
					.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(
							context.getString(R.string.noti_lockstate_unlocked))
					.setSmallIcon(R.drawable.bt_small).setOngoing(true);

		} else if (!_callingClass.equals("Bluetooth")
				&& !_callingClass.equals("Wifi") && running) {
			notification
					.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(
							context.getString(R.string.noti_lockstate_unlocked))
					.setSmallIcon(R.drawable.noti_small).setOngoing(true);
		} else {
			notification
					.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(
							context.getString(R.string.noti_lockstate_locked))
					.setSmallIcon(R.drawable.noti_small_grey).setOngoing(true);
		}

		Intent resultIntent = new Intent(context, MainActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setContentIntent(resultPendingIntent);

		NotificationManager mNotifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(100, notification.build());
	}
}
