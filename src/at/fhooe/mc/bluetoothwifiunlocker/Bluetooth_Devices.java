package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.ArrayList;
import java.util.Set;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class Bluetooth_Devices extends SherlockFragment implements
		OnCheckedChangeListener {

	private ListView btDevices;
	private ArrayList<String> devices;
	private Utils utils = new Utils();
	private LinearLayout swiper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		return inflater.inflate(R.layout.bluetooth_devices, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		btDevices = (ListView) getActivity().findViewById(
				R.id.bluetooth_devices_list);
		devices = new ArrayList<String>();
//		swiper = (LinearLayout) getActivity().findViewById(R.id.lin_lay_bt);
//		swiper.setOnTouchListener(new ActivitySwipeDetector(getActivity(), "Bluetooth"));

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
				Log.i("AddProfile", "BTDevices: " + deviceBTName);
			}
			utils.saveArray(getActivity(), utils.convertToStringArray(devices),
					"all_bluetooth_devices");
		}

		MyListAdapter bt_adapter = new MyListAdapter(getActivity(),
				R.layout.list_item_layout, devices, this, "Bluetooth");
		btDevices.setAdapter(bt_adapter);
	}

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
				Log.i("BLUETOOTH DEVICES", "BTDevices: " + deviceBTName);
			}
			utils.saveArray(getActivity(), utils.convertToStringArray(devices),
					"all_bluetooth_devices");
		}

		MyListAdapter bt_adapter = new MyListAdapter(getActivity(),
				R.layout.list_item_layout, devices, this, "Bluetooth");
		btDevices.setAdapter(bt_adapter);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

		ArrayList<String> savedDevices = utils.convertToArrayList(utils
				.loadArray(getActivity(), "saved_bluetooth_devices"));

		// TODO aktivieren/deaktivieren wenn gerade verbunden, gelöscht wird!

		if (isChecked) { // save
			if (!savedDevices.contains(arg0.getText())) {

				Log.i("BT DEVICES", "SAVE DEVICE : "
						+ arg0.getText().toString());
				savedDevices.add(arg0.getText().toString());
				utils.saveArray(getActivity(),
						utils.convertToStringArray(savedDevices),
						"saved_bluetooth_devices");
			}
		} else { // delete from list
			Log.i("BT DEVICES", "Delete Device: " + arg0.getText());
			savedDevices.remove(arg0.getText().toString());
			utils.saveArray(getActivity(),
					utils.convertToStringArray(savedDevices),
					"saved_bluetooth_devices");
		}
	}
}
