package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.Fragment;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class WIFI_Networks extends SherlockFragment implements OnCheckedChangeListener {

	private WifiManager wifi;
	private ListView list;
	private Utils utils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		utils = new Utils();

		wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();

		return inflater.inflate(R.layout.wifi_networks, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();
		showWifis();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();
		showWifis();
	}

	public void showWifis() {

		ArrayList<String> allNetworks = utils.convertToArrayList(utils
				.loadArray(getActivity(), "all_wifi_networks"));

		ArrayList<String> savedNetworks = utils.convertToArrayList(utils
				.loadArray(getActivity(), "saved_wifi_networks"));

		ArrayList<String> listNetworks = savedNetworks;

		for (int i = 0; i < allNetworks.size(); i++) {
			if (!listNetworks.contains(allNetworks.get(i))) {
				listNetworks.add(allNetworks.get(i));
			}
		}
		list = (ListView) getActivity().findViewById(R.id.wifi_networks_list);

		MyListAdapter ad = new MyListAdapter(getActivity(), R.layout.list_item_layout,
				listNetworks, this, "Wifi");
		list.setAdapter(ad);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		ArrayList<String> savedDevices = utils.convertToArrayList(utils
				.loadArray(getActivity(), "saved_wifi_networks"));

		Log.i("WIFI_Networks", "onCheckedChanged-Method()");

		if (isChecked) { // save
			if (!savedDevices.contains(arg0.getText())) {
				Log.i("WIFI_Networks", "SAVE DEVICE : "
						+ arg0.getText().toString());
				savedDevices.add(arg0.getText().toString());
				utils.saveArray(getActivity(), utils.convertToStringArray(savedDevices),
						"saved_wifi_networks");

				// check if currently connected to that network
				ConnectivityManager myConnManager = (ConnectivityManager) getActivity()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo myNetworkInfo = myConnManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				WifiManager myWifiManager = (WifiManager) getActivity()
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();

				utils.enableAdmin(getActivity());

				if (myNetworkInfo.isConnected()
						&& arg0.getText()
								.toString()
								.equals(myWifiInfo.getSSID().substring(1,
										myWifiInfo.getSSID().length() - 1))) {

					Log.i("WIFI Receiver", "IS CONNECTED");

					Log.i("WIFI Receiver",
							"CURRENT SSID: "
									+ myWifiInfo.getSSID().substring(1,
											myWifiInfo.getSSID().length() - 1));

					Log.i("WIFI Receiver", "WIFI: " + myWifiInfo.getSSID());

					KeyguardManager km = (KeyguardManager) getActivity()
							.getSystemService(Context.KEYGUARD_SERVICE);
					final KeyguardManager.KeyguardLock kl = km
							.newKeyguardLock("MyKeyguardLock");
					kl.disableKeyguard();

					SharedPreferences settings = getActivity().getSharedPreferences(
							"BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "unlocked");
					e.putString("network", arg0.getText().toString());
					e.remove("device");
					e.commit();
				}
			}
		} else { // delete from list
			if (savedDevices.contains(arg0.getText().toString())) {

				Log.i("WIFI_Networks", "REMOVE: " + arg0.getText());

				// check if currently connected
				SharedPreferences settings = getActivity().getSharedPreferences(
						"BT_WIFI", 0);
				if (settings.getString("lockState", "-1").equals("unlocked")
						&& settings.getString("network", "-1").equals(
								arg0.getText().toString())) { // check if
																// currently
																// connected to

					DevicePolicyManager deviceManger = (DevicePolicyManager) getActivity()
							.getSystemService(Context.DEVICE_POLICY_SERVICE);

					ComponentName compName = new ComponentName(getActivity(),
							MyAdmin.class);

					boolean active = deviceManger.isAdminActive(compName);

					if (active) {
						Log.i("WIFI RECEIVER", "LOCK NOW");
						deviceManger.lockNow();
					}
				}
				Editor e = settings.edit();
				e.putString("lockState", "locked");
				e.commit();

				savedDevices.remove(arg0.getText().toString());

				utils.saveArray(getActivity(), utils.convertToStringArray(savedDevices),
						"saved_wifi_networks");
			}
		}
	}
}
