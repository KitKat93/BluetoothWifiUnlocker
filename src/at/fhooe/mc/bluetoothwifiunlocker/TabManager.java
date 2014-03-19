/**
 * This class holds the configurations for the TabHost.
 */
package at.fhooe.mc.bluetoothwifiunlocker;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

public class TabManager implements TabHost.OnTabChangeListener {
	private final FragmentActivity mActivity;
	private final TabHost mTabHost;
	private final int mContainerId;
	private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
	TabInfo mLastTab;

	/**
	 * Initializes the variables.
	 */
	static final class TabInfo {
		private final String tag;
		private final Class<?> clss;
		private final Bundle args;
		private Fragment fragment;

		TabInfo(String _tag, Class<?> _class, Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}

	/**
	 * Inner class which generates the View for each Tab.
	 */
	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	/**
	 * Initializes the variables with the given values.
	 * 
	 * @param activity
	 *            The Activity which holds the TabHost.
	 * @param tabHost
	 *            The given TabHost which holds the Tabs.
	 * @param containerId
	 *            The id for the tabcontent.
	 */
	public TabManager(FragmentActivity activity, TabHost tabHost,
			int containerId) {
		mActivity = activity;
		mTabHost = tabHost;
		mContainerId = containerId;
		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * Generates a new Tab and adds it to the TabHost.
	 * 
	 * @param tabSpec The given TabSpecification
	 * @param clss The class which contains the Tab
	 * @param args Null as default.
	 */
	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		tabSpec.setContent(new DummyTabFactory(mActivity));
		String tag = tabSpec.getTag();

		TabInfo info = new TabInfo(tag, clss, args);

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		info.fragment = mActivity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (info.fragment != null && !info.fragment.isDetached()) {
			FragmentTransaction ft = mActivity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(info.fragment);
			ft.commit();
		}

		mTabs.put(tag, info);
		mTabHost.addTab(tabSpec);
	}

	/**
	 * Overrides the onTabChanged()-method from the OnTabChangedListener 
	 * and is called each time the user clicks on a different Tab or swipes
	 * to the left or right. Loads the new Tab.
	 */
	@Override
	public void onTabChanged(String tabId) {
		TabInfo newTab = mTabs.get(tabId);
		if (mLastTab != newTab) {
			FragmentTransaction ft = mActivity.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(mActivity,
							newTab.clss.getName(), newTab.args);
					ft.add(mContainerId, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			mActivity.getSupportFragmentManager().executePendingTransactions();
		}
	}
}
