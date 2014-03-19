/**
 * Holds the wifi-networks list and manages the list which holds the ones which
 * unlock the phone.
 */
package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.MyListAdapter;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

import com.actionbarsherlock.app.SherlockFragment;

public class WIFI_Networks extends SherlockFragment implements
		OnCheckedChangeListener {

	private WifiManager wifi;
	private ListView list;
	private Utils utils;

	/**
	 * Initializes the variables, enables the wifi (if not already enabled) and
	 * starts the scan to find the currently available networks.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		utils = new Utils();

		wifi = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);

		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();

		return inflater.inflate(R.layout.wifi_networks, container, false);
	}

	/**
	 * Starts the wifi-scan and calls the showWifis()-method to refresh the
	 * list.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();
		showWifis();
	}

	/**
	 * Starts the wifi-scan and calls the showWifis()-method to refresh the
	 * list.
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();
		showWifis();
	}

	/**
	 * This method initializes the list with the currently available wifi
	 * networks + the currently saved wifi networks which are selected to unlock
	 * the phone.
	 */
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

		MyListAdapter ad = new MyListAdapter(getActivity(),
				R.layout.list_item_layout, listNetworks, this, "Wifi");
		list.setAdapter(ad);
	}

	/**
	 * Overrides the onCheckedChangeListener's method. If the CheckBox is
	 * currently checked, the network is added to the list to unlock the phone.
	 * Additionally it is checked whether the phone currently has an active
	 * wifi-connection with this network. If so, the phone gets unlocked
	 * immediately. If the clicked CheckBox is currently unchecked, it is
	 * removed from the list, checked whether there is an active connection to
	 * this network and if so, the phone is locked again. For both options, the
	 * new status is saved in the SharedPreferences and the Notification is
	 * updated.
	 * 
	 * @param arg0
	 *            The clicked CheckBox.
	 * @param isChecked
	 *            True if the given Checkbox is currently checked, false
	 *            otherwise.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		ArrayList<String> savedDevices = utils.convertToArrayList(utils
				.loadArray(getActivity(), "saved_wifi_networks"));

		if (isChecked) {
			if (!savedDevices.contains(arg0.getText())) {
				savedDevices.add(arg0.getText().toString());
				utils.saveArray(getActivity(),
						utils.convertToStringArray(savedDevices),
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

					KeyguardManager km = (KeyguardManager) getActivity()
							.getSystemService(Context.KEYGUARD_SERVICE);
					final KeyguardManager.KeyguardLock kl = km
							.newKeyguardLock("MyKeyguardLock");
					kl.disableKeyguard();

					SharedPreferences settings = getActivity()
							.getSharedPreferences("BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "unlocked");
					e.putString("network", arg0.getText().toString());
					e.remove("device");
					e.commit();

					utils.showNotification(true, getActivity(), "Wifi");
				}
			}
		} else {
			if (savedDevices.contains(arg0.getText().toString())) {

				// check if currently connected
				SharedPreferences settings = getActivity()
						.getSharedPreferences("BT_WIFI", 0);
				if (settings.getString("lockState", "-1").equals("unlocked")
						&& settings.getString("network", "-1").equals(
								arg0.getText().toString())) {
					DevicePolicyManager deviceManger = (DevicePolicyManager) getActivity()
							.getSystemService(Context.DEVICE_POLICY_SERVICE);

					ComponentName compName = new ComponentName(getActivity(),
							MyAdmin.class);

					boolean active = deviceManger.isAdminActive(compName);

					if (active) {
						deviceManger.lockNow();
						utils.showNotification(false, getActivity(), "Wifi");
						Editor e = settings.edit();
						e.putString("lockState", "locked");
						e.commit();
					}
				}
				savedDevices.remove(arg0.getText().toString());

				utils.saveArray(getActivity(),
						utils.convertToStringArray(savedDevices),
						"saved_wifi_networks");
			}
		}
	}
}
