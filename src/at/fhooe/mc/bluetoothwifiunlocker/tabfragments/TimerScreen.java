/**
 * Holds the layout for the Timer-tab.
 */
package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class TimerScreen extends SherlockFragment implements
		android.view.View.OnClickListener {

	private EditText hours;
	private EditText minutes;
	private EditText seconds;
	private Button startCountdown;
	private Utils utils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		return inflater.inflate(R.layout.timer, container, false);
	}

	/**
	 * Initializes the layout.
	 */
	@Override
	public void onStart() {
		super.onStart();

		hours = (EditText) getActivity().findViewById(R.id.et_hours);
		minutes = (EditText) getActivity().findViewById(R.id.et_minutes);
		seconds = (EditText) getActivity().findViewById(R.id.et_seconds);

		startCountdown = (Button) getActivity()
				.findViewById(R.id.b_start_timer);
		startCountdown.setOnClickListener(this);
	}

	/**
	 * Initializes the layout.
	 */
	@Override
	public void onResume() {
		super.onResume();

		hours = (EditText) getActivity().findViewById(R.id.et_hours);
		minutes = (EditText) getActivity().findViewById(R.id.et_minutes);
		seconds = (EditText) getActivity().findViewById(R.id.et_seconds);

		startCountdown = (Button) getActivity()
				.findViewById(R.id.b_start_timer);
		startCountdown.setOnClickListener(this);
	}

	/**
	 * This method is called by the onClick()-method and is capable of the
	 * Countdown-Timer. This timer is set and refreshes on every tick (every
	 * second) the layout by counting down the time which was originally set. If
	 * the countdown is finished, the lockscreen is enabled again and the
	 * current status (=locked) is saved in the SharedPreferences. Additionally,
	 * the notification is updated.
	 * 
	 * @param _hours
	 *            The number of hours.
	 * @param _minutes
	 *            The number of minutes.
	 * @param _seconds
	 *            The number of seconds.
	 */
	public void startTimer(int _hours, int _minutes, int _seconds) {

		new CountDownTimer((_seconds + _minutes * 60 + _hours * 3600) * 1000,
				1000) {

			public void onTick(long millisUntilFinished) {

				long remainingSecs = millisUntilFinished / 1000;

				int h = (int) (remainingSecs / 3600);
				remainingSecs = remainingSecs - h * 3600;
				int min = (int) (remainingSecs / 60);
				remainingSecs = remainingSecs - min * 60;
				int s = (int) remainingSecs;

				hours.setText(String.valueOf(h));
				minutes.setText(String.valueOf(min));
				seconds.setText(String.valueOf(s));

			}

			public void onFinish() {

				hours.setText(String.valueOf(0));
				minutes.setText(String.valueOf(0));
				seconds.setText(String.valueOf(0));

				DevicePolicyManager deviceManger = (DevicePolicyManager) getActivity()
						.getSystemService(Context.DEVICE_POLICY_SERVICE);

				ComponentName compName = new ComponentName(getActivity(),
						MyAdmin.class);

				boolean active = deviceManger.isAdminActive(compName);

				if (active) {
					deviceManger.lockNow();

					SharedPreferences settings = getActivity()
							.getSharedPreferences("BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "locked");
					e.commit();

					utils = new Utils();
					utils.showNotification(false, getActivity(), "Timer");
				}
			}
		}.start();
	}

	/**
	 * This method overrides the onClick()-method of the OnClickListener. If the
	 * button "startCountdown" is clicked, the currently typed-in values of the
	 * textfields are checked if they are correct and if so the
	 * startTimer()-method is called with the appropriate values.
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == startCountdown.getId()) {

			String h = "0";
			String min = "0";
			String s = "0";

			if (!hours.getText().toString().equals("")) {
				h = hours.getText().toString();
			}

			if (!minutes.getText().toString().equals("")) {
				min = minutes.getText().toString();
			}

			if (!seconds.getText().toString().equals("")) {
				s = seconds.getText().toString();
			}

			if (Integer.valueOf(s) >= 59 || Integer.valueOf(min) >= 59) {
				AlertDialog falseInput = new AlertDialog.Builder(getActivity()).create();
				falseInput.setTitle(R.string.timer_falseInput_title);
				falseInput
						.setMessage(getString(R.string.timer_falseInput));
				falseInput.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				falseInput.show();
			} else {
				startTimer(Integer.valueOf(h), Integer.valueOf(min),
						Integer.valueOf(s));
			}
		}
	}
}