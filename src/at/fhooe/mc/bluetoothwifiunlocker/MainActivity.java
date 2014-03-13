package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.Locale;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {

	private BluetoothAdapter mBluetoothAdapter;
	private WifiReceiver wifiReceiver;
	private ComponentName mAdminName;
	private Utils utils;
	private WifiManager wifiMan;
	private TabHost mTabHost;
	private TabManager mTabManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().hide();

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
		mTabHost.setOnTouchListener(new ActivitySwipeDetector(this, mTabHost));
		
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

		utils = new Utils();

		wifiReceiver = new WifiReceiver();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(wifiReceiver, intentFilter);

		mAdminName = new ComponentName(MainActivity.this, MyAdmin.class);

		utils.enableAdmin(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 3) { // Bluetooth Enabled
			if (resultCode == RESULT_OK) {
				Log.i("MainActivity", "RESULT OK");
			} else {
				Log.i("MainActivity", "RESULT NOT OK");
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mAdminName = new ComponentName(this, MyAdmin.class);

		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				R.string.devAdmin_explanation);
		startActivityForResult(intent, 5);

		utils.enableAdmin(this);
	}
}
