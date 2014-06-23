/**
 * This class holds the initializes the given list with its items.
 */
package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class MyListAdapter extends ArrayAdapter<String> {

	private List<String> list;
	private Context context;
	private int customListItem;
	private OnCheckedChangeListener listener;
	private Utils utils;
	private String callingClass;

	/**
	 * Initializes the variables with the given values.
	 * 
	 * @param _context
	 *            The Application-context.
	 * @param _viewResourceId
	 *            The list item layout.
	 * @param _objs
	 *            The List of objects to initialize the list with.
	 * @param _listener
	 *            The listener to listen at the Checkbox-changes.
	 * @param _callingClass
	 *            The name of the class which initialized this adapter.
	 */
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

	/**
	 * Overrides the getView()-method from the ArrayAdapter<String>-class.
	 * First, it gets checked whether the callingClass is the Wifi-class or
	 * Bluetooth-class and according to that, the Checkbox-text is set and, if
	 * this bluetooth/wifi is saved in the settings which unlock the screen, the
	 * checkbox gets checked.
	 */
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

				if (savedDevicesNetworks.contains(element)) {
					b.setChecked(true);
				} else {
					b.setChecked(false);
				}
				b.setOnCheckedChangeListener(listener);
			} else if (callingClass.equals("Bluetooth")) {
				// bluetooth
				ArrayList<String> savedDevices = utils.convertToArrayList(utils
						.loadArray(context, "saved_bluetooth_devices"));

				if (savedDevices.contains(element)) {
					b.setChecked(true);
				} else {
					b.setChecked(false);
				}
				b.setOnCheckedChangeListener(listener);
			}
		}
		return _convertView;
	}
}
