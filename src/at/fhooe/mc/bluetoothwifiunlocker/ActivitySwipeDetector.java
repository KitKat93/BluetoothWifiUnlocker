package at.fhooe.mc.bluetoothwifiunlocker;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TabHost;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class ActivitySwipeDetector implements OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	static final int MIN_DISTANCE = 150;
	static final int MAX_UP = 200;
	private float downX, downY, upX, upY;
	private TabHost tabs;
	private Utils util;
	private SherlockFragmentActivity context;

	public ActivitySwipeDetector(SherlockFragmentActivity _context,
			TabHost _tabs) {
		Log.i(logTag, "SWIPE CONSTRUCTOR");
		tabs = _tabs;
		context = _context;
		util = new Utils();
	}

	public void onLeftToRightSwipe() {
		Log.i(logTag, "LeftToRightSwipe!");
		int pos = tabs.getCurrentTab();

		Log.i(logTag, "Tab POSITION:" + pos);

		if (pos >= 1) {
			tabs.setCurrentTab(--pos);
		} else {
			tabs.setCurrentTab(3);
		}

	}

	public void onRightToLeftSwipe() {
		Log.i(logTag, "onRightToLeftSwipe!");

		int pos = tabs.getCurrentTab();

		Log.i(logTag, "Tab POSITION:" + pos);

		if (pos <= 2) {
			tabs.setCurrentTab(++pos);
		} else {
			tabs.setCurrentTab(0);
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			// swipe horizontal?
			if (Math.abs(deltaX) > MIN_DISTANCE) {
				// left or right
				if (Math.abs(deltaY) > MAX_UP) {
					Log.i(logTag,
							"Horizontal swipe was to hight with "
									+ Math.abs(deltaY) + ". Must be below"
									+ MAX_UP);
					return false;
				}
				if (deltaX < 0) {
					this.onLeftToRightSwipe();
					return true;
				}
				if (deltaX > 0) {
					this.onRightToLeftSwipe();
					return true;
				}

			} else {
				Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
						+ " long, need at least " + MIN_DISTANCE);
				return false; 
			}

			return true;
		}
		}
		return false;
	}

}