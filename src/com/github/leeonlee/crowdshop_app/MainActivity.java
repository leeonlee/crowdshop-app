package com.github.leeonlee.crowdshop_app;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends CrowdShopActivity implements
		ActionBar.TabListener {

	public static enum RequestCode {
		CREATE_TASK, CLAIM_TASK, FINISH_TASK, CONFIRM_TASK;
	}

	public static enum TabIndex {
		OPEN_TASKS, CLAIMED_TASKS, REQUESTED_TASKS;

		private static final TabIndex[] TAB_INDICES = TabIndex.values();
		public static TabIndex fromInt(int i) {
			return TAB_INDICES[i];
		}
	}

	private static final String TAG = MainActivity.class.getSimpleName();

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private static final int[] TAB_IDS = {R.string.friends, R.string.tasks, R.string.requests};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (int tabId : TAB_IDS)
			actionBar.addTab(actionBar.newTab().setText(tabId)
					.setTabListener(this));

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_task:
			Intent i = new Intent(getApplicationContext(), TaskActivity.class);
			i.putExtra("username", mApp.getUsername());
			startActivityForResult(i, RequestCode.CREATE_TASK.ordinal());

			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RequestCode.CREATE_TASK.ordinal()) {
			refreshTab(TabIndex.REQUESTED_TASKS);
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void refreshTab(TabIndex tabIndex) {
		mAdapter.refreshItem(tabIndex);
	}

	public static class TabsPagerAdapter extends FragmentPagerAdapter {

		private final Map<TabIndex, TaskListFragment> mFragments
				= new EnumMap<TabIndex, TaskListFragment>(TabIndex.class);

		public TabsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int index){
			TabIndex tabIndex = TabIndex.fromInt(index);
			TaskListFragment fragment = null;
			switch (tabIndex) {
				case OPEN_TASKS:
					fragment = new OpenTaskListFragment();
					break;
				case CLAIMED_TASKS:
					fragment = new ClaimedTaskListFragment();
					break;
				case REQUESTED_TASKS:
					fragment = new RequestedTaskListFragment();
					break;
			}
			Log.d(TAG, "Retrieved fragment index " + index);
			mFragments.put(tabIndex, fragment);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		public void refreshItem(TabIndex tabIndex) {
			TaskListFragment fragment = mFragments.get(tabIndex);
			if (fragment != null) {
				Log.d(TAG, "Refreshing " + tabIndex);
				fragment.getTasks();
			}
		}
	}
}
