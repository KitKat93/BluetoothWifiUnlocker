/**
 * Holds the Bluetooth-devices list and manages the list which holds the ones which
 * unlock the phone.
 */
package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import java.util.ArrayList;
import java.util.Set;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.MyListAdapter;
import at.fhooe.mc.bluetoothwifiunlocker.receiver.BluetoothReceiver;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

import com.actionbarsherlock.app.SherlockFragment;

public class Bluetooth_Devices extends SherlockFragment implements
		OnCheckedChangeListener {

	private ListView btDevices;
	private ArrayList<String> devices;
	private Utils utils = new Utils();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		return inflater.inflate(R.layout.bluetooth_devices, container, false);
	}

	/**
	 * This method initializes the list with the currently paired
	 * bluetooth-devices, therefore the Bluetooth from the device has to be
	 * enabled. If it is not enabled, it gets enabled too.
	 */
	@Override
	public void onResume() {
		super.onResume();

		ComponentName compName = new ComponentName(getActivity(), MyAdmin.class);
		DevicePolicyManager deviceManger = (DevicePolicyManager) getActivity()
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		boolean active = deviceManger.isAdminActive(compName);

		if (!active) {
			Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
			i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					R.string.devAdmin_explanation);
			getActivity().startActivityForResult(i, 5);
		}

		btDevices = (ListView) getActivity().findViewById(
				R.id.bluetooth_devices_list);
		devices = new ArrayList<String>();

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				String deviceBTName = device.getName();
				if (!devices.contains(deviceBTName)) {
					devices.add(deviceBTName);
				}
			}
			utils.saveArray(getActivity(), utils.convertToStringArray(devices),
					"all_bluetooth_devices");
		}

		MyListAdapter bt_adapter = new MyListAdapter(getActivity(),
				R.layout.list_item_layout, devices, this, "Bluetooth");
		btDevices.setAdapter(bt_adapter);
	}

	/**
	 * This method initializes the list with the currently paired
	 * bluetooth-devices, therefore the Bluetooth from the device has to be
	 * enabled. If it is not enabled, it gets enabled too.
	 */
	@Override
	public void onStart() {
		super.onStart();

		ComponentName compName = new ComponentName(getActivity(), MyAdmin.class);
		DevicePolicyManager deviceManger = (DevicePolicyManager) getActivity()
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		boolean active = deviceManger.isAdminActive(compName);

		if (!active) {
			Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
			i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					R.string.devAdmin_explanation);
			getActivity().startActivityForResult(i, 5);
		}

		btDevices = (ListView) getActivity().findViewById(
				R.id.bluetooth_devices_list);
		devices = new ArrayList<String>();

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				String deviceBTName = device.getName();
				if (!devices.contains(deviceBTName)) {
					devices.add(deviceBTName);
				}
			}
			utils.saveArray(getActivity(), utils.convertToStringArray(devices),
					"all_bluetooth_devices");
		}

		MyListAdapter bt_adapter = new MyListAdapter(getActivity(),
				R.layout.list_item_layout, devices, this, "Bluetooth");
		btDevices.setAdapter(bt_adapter);
	}

	/**
	 * Overrides the onCheckedChangeListener's method. If the CheckBox is
	 * currently checked, the bluetooth-device is added to the list to unlock
	 * the phone. If the clicked CheckBox is currently unchecked, it is removed
	 * from the list. For both options, the new status is saved in the
	 * SharedPreferences and the Notification is updated. To update the current
	 * status of the app, the bluetooth gets disabled and enabled again.
	 * 
	 * @param arg0
	 *            The clicked CheckBox.
	 * @param isChecked
	 *            True if the given Checkbox is currently checked, false
	 *            otherwise.
	 */
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

		ArrayList<String> savedDevices = utils.convertToArrayList(utils
				.loadArray(getActivity(), "saved_bluetooth_devices"));

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		if (isChecked) { // save
			if (!savedDevices.contains(arg0.getText())) {

				bluetoothAdapter.disable();

				savedDevices.add(arg0.getText().toString());
				utils.saveArray(getActivity(),
						utils.convertToStringArray(savedDevices),
						"saved_bluetooth_devices");

				bluetoothAdapter.enable();
			}
		} else { // delete from list
			if (savedDevices.contains(arg0.getText().toString())) {
				savedDevices.remove(arg0.getText().toString());
				utils.saveArray(getActivity(),
						utils.convertToStringArray(savedDevices),
						"saved_bluetooth_devices");
				enableKeyguard(getActivity());
			}
		}

		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				String deviceBTName = device.getName();
				if (!devices.contains(deviceBTName)) {
					devices.add(deviceBTName);
				}
			}
			utils.saveArray(getActivity(), utils.convertToStringArray(devices),
					"all_bluetooth_devices");
		}

		MyListAdapter bt_adapter = new MyListAdapter(getActivity(),
				R.layout.list_item_layout, devices, this, "Bluetooth");
		btDevices.setAdapter(bt_adapter);
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
