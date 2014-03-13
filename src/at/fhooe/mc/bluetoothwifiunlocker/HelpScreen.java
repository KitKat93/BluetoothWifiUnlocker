package at.fhooe.mc.bluetoothwifiunlocker;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class HelpScreen extends SherlockFragment implements OnClickListener{
	
	Button deactivateAdmin;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		return inflater.inflate(R.layout.help_screen, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		deactivateAdmin = (Button) getActivity().findViewById(R.id.deactivate_admin);
		deactivateAdmin.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		deactivateAdmin = (Button) getActivity().findViewById(R.id.deactivate_admin);
		deactivateAdmin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == deactivateAdmin.getId()) {
			DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getActivity()
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			ComponentName adminName = new ComponentName(getActivity(), MyAdmin.class);
			mDevicePolicyManager.removeActiveAdmin(adminName);
		}
	}
}