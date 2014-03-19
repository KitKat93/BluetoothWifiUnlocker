/**
 * This code was written as a part of my practical Bachelorthesis. 
 * Feel free to use it in your own application, but please make sure to 
 * mark it properly! If you have any questions, feel free to ask  and 
 * write to: kathkefer@gmail.com.
 * 
 * © Copyright 2014 Kathrin Kefer
 * 
 */

package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import java.util.Locale;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import at.fhooe.mc.bluetoothwifiunlocker.ActivitySwipeDetector;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.TabManager;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {

	private Utils utils;
	private WifiManager wifiMan;
	private TabHost mTabHost;
	private TabManager mTabManager;

	/**
	 * This onCreate-method initializes the Tab-Layout, enables wifi and
	 * bluetooth and starts a wifi-scan to get the currently available
	 * wifi-networks. Additionally, the method to enable the DeviceAdmin is
	 * called.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().hide();

		utils = new Utils();

		wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!wifiMan.isWifiEnabled()) {
			wifiMan.setWifiEnabled(true);
		}
		wifiMan.startScan();

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

		String language = Locale.getDefault().getLanguage();

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.setOnTouchListener(new ActivitySwipeDetector(mTabHost));

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		View v = new View(this);
		if (language.equals("de")) {
			v.setBackgroundResource(R.drawable.main_tab_de);
		} else {
			v.setBackgroundResource(R.drawable.main_tab_en);
		}
		mTabManager.addTab(mTabHost.newTabSpec("main_tab").setIndicator(v),
				MainScreen.class, null);

		View v1 = new View(this);
		v1.setBackgroundResource(R.drawable.wifi_tab);

		mTabManager.addTab(mTabHost.newTabSpec("wifi_tab").setIndicator(v1),
				WIFI_Networks.class, null);

		View v2 = new View(this);
		v2.setBackgroundResource(R.drawable.bt_tab_en);

		mTabManager.addTab(mTabHost.newTabSpec("bt").setIndicator(v2),
				Bluetooth_Devices.class, null);

		View v3 = new View(this);
		v3.setBackgroundResource(R.drawable.timer_tab);
		mTabManager.addTab(mTabHost.newTabSpec("timer").setIndicator(v3),
				TimerScreen.class, null);

		View v4 = new View(this);
		if (language.equals("de")) {
			v4.setBackgroundResource(R.drawable.help_tab_de);
		} else {
			v4.setBackgroundResource(R.drawable.help_tab_en);
		}
		mTabManager.addTab(mTabHost.newTabSpec("help").setIndicator(v4),
				HelpScreen.class, null);

		utils.enableAdmin(this);
	}

	/**
	 * Calls the enableAdmin()-method to check whether it is already activated
	 * or not.
	 */
	@Override
	protected void onStart() {
		super.onStart();

		ComponentName mAdminName = new ComponentName(this, MyAdmin.class);

		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				R.string.devAdmin_explanation);
		startActivityForResult(intent, 5);

		utils.enableAdmin(this);
	}
}
