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
import at.fhooe.mc.bluetoothwifiunlocker.MainActivity;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class Utils {

	public boolean saveArray(Context mContext, String[] array, String arrayName) {
		SharedPreferences prefs = mContext.getSharedPreferences(arrayName, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(arrayName + "_size", array.length);
		for (int i = 0; i < array.length; i++) {
			editor.putString(arrayName + "_" + i, array[i]);
		}
		return editor.commit();
	}

	public String[] loadArray(Context mContext, String arrayName) {
		SharedPreferences prefs = mContext.getSharedPreferences(arrayName, 0);
		int size = prefs.getInt(arrayName + "_size", 0);
		String[] arr = new String[size];
		for (int i = 0; i < size; i++) {
			arr[i] = prefs.getString(arrayName + "_" + i, null);
		}
		return arr;
	}

	public String[] convertToStringArray(ArrayList<String> _arr) {
		String[] stringArr = new String[_arr.size()];
		for (int i = 0; i < _arr.size(); i++) {
			stringArr[i] = _arr.get(i);
		}
		return stringArr;
	}

	public ArrayList<String> convertToArrayList(String[] _arr) {
		ArrayList<String> stringArr = new ArrayList<String>();
		for (int i = 0; i < _arr.length; i++) {
			stringArr.add(_arr[i]);
		}
		return stringArr;
	}

	public void enableAdmin(Context context) {

		Log.i("Wifi Receiver", "EnableAdmin");

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
						// We didn't retrieve the meta data for all possible
						// matches, so
						// need to use the activity info of this specific one
						// that was retrieved.
						ri.activityInfo = ai;
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

	@SuppressLint("NewApi")
	public void showNotification(boolean running, Context context,
			String _callingClass) {

		Notification.Builder notification = new Notification.Builder(context);
		notification.setContentTitle("BluetoothWifiUnlocker")
				.setContentText(context.getString(R.string.noti_lockstate_locked))
				.setSmallIcon(R.drawable.main_small);

		if (_callingClass.equals("Wifi") && running) {
			Log.i("Utils", "showNotification! - Wifi");

			notification.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(context.getString(R.string.noti_lockstate_unlocked))
					.setSmallIcon(R.drawable.wifi_small);
		} else if (_callingClass.equals("Bluetooth") && running) {
			Log.i("Utils", "showNotification! - Bluetooth");

			notification.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(context.getString(R.string.noti_lockstate_unlocked))
					.setSmallIcon(R.drawable.bt_small);

		} else {
			notification.setContentTitle("BluetoothWifiUnlocker")
					.setContentText(context.getString(R.string.noti_lockstate_locked))
					.setSmallIcon(R.drawable.noti_small_grey);

		}

		Intent resultIntent = new Intent(context, MainActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setContentIntent(resultPendingIntent);
		
		NotificationManager mNotifyMgr = 
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(100, notification.build());
	}
}
