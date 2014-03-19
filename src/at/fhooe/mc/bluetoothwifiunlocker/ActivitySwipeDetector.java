/**
 * This class manages the swipe-directions and actions according to them.
 */
package at.fhooe.mc.bluetoothwifiunlocker;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TabHost;

public class ActivitySwipeDetector implements OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	static final int MIN_DISTANCE = 150;
	static final int MAX_UP = 200;
	private float downX, downY, upX, upY;
	private TabHost tabs;

	/**
	 * Initializes the local variable tabs.
	 * 
	 * @param _tabs
	 *            The given TabHost.
	 */
	public ActivitySwipeDetector(TabHost _tabs) {
		tabs = _tabs;
	}

	/**
	 * This method is called from the onTouch()-method and sets the tab to the
	 * left one.
	 */
	public void onLeftToRightSwipe() {
		int pos = tabs.getCurrentTab();

		if (pos >= 1) {
			tabs.setCurrentTab(--pos);
		} else {
			tabs.setCurrentTab(3);
		}

	}

	/**
	 * This method is called from the onTouch()-method and sets the tab to the
	 * right one.
	 */
	public void onRightToLeftSwipe() {
		int pos = tabs.getCurrentTab();

		if (pos <= 2) {
			tabs.setCurrentTab(++pos);
		} else {
			tabs.setCurrentTab(0);
		}
	}

	/**
	 * This method catches the onTouch-Event from the OnTouchListener, checks at
	 * which position the user touched and released the screen. According to
	 * that, the swipe direction and length is computed and the
	 * onLeftToRightSwipe()-method or onRightToLeftSwipe()-method is called.
	 */
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

			// horizontal swipe
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