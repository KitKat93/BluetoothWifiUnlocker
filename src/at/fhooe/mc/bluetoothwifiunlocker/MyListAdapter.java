package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class MyListAdapter extends ArrayAdapter<String> {

	List<String> list;
	Context context;
	int customListItem;
	OnCheckedChangeListener listener;
	private Utils utils;
	private String callingClass;

	public MyListAdapter(Context _context, int _viewResourceId,
			List<String> _objs, OnCheckedChangeListener _listener,
			String _callingClass) {
		super(_context, _viewResourceId, _objs);
		context = _context;
		list = _objs;
		customListItem = _viewResourceId;
		listener = _listener;
		utils = new Utils();
		callingClass = _callingClass;
	}

	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {

		if (_convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			_convertView = inflater.inflate(customListItem, null);
		}

		String element = list.get(_position);
		
		if (element != null) {
			CheckBox b = (CheckBox) _convertView.findViewById(R.id.checkBox1);
			b.setText(element);

			// wifis
			if (callingClass.equals("Wifi")) {

				ArrayList<String> savedDevicesNetworks = utils
						.convertToArrayList(utils.loadArray(context,
								"saved_wifi_networks"));

				Log.i("WIFI_Networks", " LISTVIEW CHILD: " + element);
				if (savedDevicesNetworks.contains(element)) {
					Log.i("WIFI_Networks", "Select " + element);
					b.setChecked(true);
				} else {
					b.setChecked(false);
				}
			} else if (callingClass.equals("Bluetooth")) {
				// bluetooth
				ArrayList<String> savedDevices = utils.convertToArrayList(utils
						.loadArray(context, "saved_bluetooth_devices"));
				Log.i("BT_Devices", " LISTVIEW CHILD: " + element);

				if (savedDevices.contains(element)) {
					Log.i("BT_DEVICES", "Select " + element);
					b.setChecked(true);
				} else {
					b.setChecked(false);
				}
			}
			b.setOnCheckedChangeListener(listener);
		}

		return _convertView;
	}
}
