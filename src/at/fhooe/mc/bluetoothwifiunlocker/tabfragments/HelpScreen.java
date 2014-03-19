/**
 * Displays the Help-Informations and holds the Button to deactivate the DeviceAdmin.
 */
package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class HelpScreen extends SherlockFragment implements OnClickListener {

	Button deactivateAdmin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		return inflater.inflate(R.layout.help_screen, container, false);
	}

	/**
	 * Initializes the Layout-Button.
	 */
	@Override
	public void onStart() {
		super.onStart();

		deactivateAdmin = (Button) getActivity().findViewById(
				R.id.deactivate_admin);
		deactivateAdmin.setOnClickListener(this);
	}

	/**
	 * Initializes the Layout-Button.
	 */
	@Override
	public void onResume() {
		super.onResume();
		deactivateAdmin = (Button) getActivity().findViewById(
				R.id.deactivate_admin);
		deactivateAdmin.setOnClickListener(this);
	}

	/**
	 * Overrides the onClick()-method from the OnClickListener. If the
	 * deactivateAdmin-Button was clicked, the currently activated DeviceAdmin
	 * is removed from the DevicePolicyManager.
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == deactivateAdmin.getId()) {
			DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getActivity()
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			ComponentName adminName = new ComponentName(getActivity(),
					MyAdmin.class);
			mDevicePolicyManager.removeActiveAdmin(adminName);
			
			AlertDialog adminDeactivated = new AlertDialog.Builder(getActivity()).create();
			adminDeactivated
					.setMessage(getString(R.string.devAdmin_deactivated));
			adminDeactivated.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					});
			adminDeactivated.show();
		}
	}
}