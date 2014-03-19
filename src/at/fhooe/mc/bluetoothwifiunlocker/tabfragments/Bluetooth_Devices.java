/**
 * Holds the Bluetooth-devices list and manages the list which holds the ones which
 * unlock the phone.
 */
package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import at.fhooe.mc.bluetoothwifiunlocker.MyListAdapter;
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

		if (isChecked) { // save
			if (!savedDevices.contains(arg0.getText())) {

				BluetoothAdapter bluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();

				if (!bluetoothAdapter.isEnabled()) {
					bluetoothAdapter.enable();
				}

				bluetoothAdapter.disable();
				bluetoothAdapter.enable();

				savedDevices.add(arg0.getText().toString());
				utils.saveArray(getActivity(),
						utils.convertToStringArray(savedDevices),
						"saved_bluetooth_devices");
			}
		} else { // delete from list
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();

			if (!bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.enable();
			}

			bluetoothAdapter.disable();
			bluetoothAdapter.enable();

			savedDevices.remove(arg0.getText().toString());
			utils.saveArray(getActivity(),
					utils.convertToStringArray(savedDevices),
					"saved_bluetooth_devices");
		}
	}
}
