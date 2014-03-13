package at.fhooe.mc.bluetoothwifiunlocker;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {
	public void onEnabled(Context context, Intent intent) {
		showToast(context, "Sample Device Admin: enabled");
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return "This is an optional message to warn the user about disabling.";
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		showToast(context, "Sample Device Admin: disabled");
	}

	void showToast(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

	}

}
