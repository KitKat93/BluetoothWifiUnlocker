package at.fhooe.mc.bluetoothwifiunlocker;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class TimerScreen extends SherlockFragment implements
		android.view.View.OnClickListener {

	private EditText hours;
	private EditText minutes;
	private EditText seconds;
	private Button startCountdown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		return inflater.inflate(R.layout.timer, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		hours = (EditText) getActivity().findViewById(R.id.et_hours);
		minutes = (EditText) getActivity().findViewById(R.id.et_minutes);
		seconds = (EditText) getActivity().findViewById(R.id.et_seconds);

		startCountdown = (Button) getActivity().findViewById(R.id.b_start_timer);
		startCountdown.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		hours = (EditText) getActivity().findViewById(R.id.et_hours);
		minutes = (EditText) getActivity().findViewById(R.id.et_minutes);
		seconds = (EditText) getActivity().findViewById(R.id.et_seconds);

		startCountdown = (Button) getActivity().findViewById(R.id.b_start_timer);
		startCountdown.setOnClickListener(this);
	}

	public void startTimer(int _hours, int _minutes, int _seconds) {

		CountDownTimer t = new CountDownTimer(
				(_seconds + _minutes * 60 + _hours * 3600) * 1000, 1000) {

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
				Log.i("TIMER", "IS ACTIVE: " + active);

				if (active) {
					Log.i("TIMER", "LOCK NOW");
					deviceManger.lockNow();

					KeyguardManager mKeyguardManager = (KeyguardManager) getActivity()
							.getSystemService(Context.KEYGUARD_SERVICE);

					SharedPreferences settings = getActivity()
							.getSharedPreferences("BT_WIFI", 0);
					Editor e = settings.edit();
					e.putString("lockState", "locked");
					e.commit();
				}
			}
		}.start();
	}

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

			startTimer(Integer.valueOf(h), Integer.valueOf(min),
					Integer.valueOf(s));
		}
	}
}