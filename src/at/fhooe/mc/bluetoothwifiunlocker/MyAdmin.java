/**
 * This method holds the information about the DeviceAdmin.
 */
package at.fhooe.mc.bluetoothwifiunlocker;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class MyAdmin extends DeviceAdminReceiver {
	
	Context context;
	
	@Override
	public void onEnabled(Context _context, Intent intent) {
		context = _context;
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return context.getString(R.string.devAdmin_disable_warning);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
	}
}
