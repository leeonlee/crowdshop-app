package com.github.leeonlee.crowdshop_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter{

	private final CrowdShopApplication mApp;

	public TabsPagerAdapter(FragmentManager fm, CrowdShopApplication app) {
		super(fm);
		mApp = app;
	}
	
	public Fragment getItem(int index){
		TasksAdapter adapter = null;
		switch(index){
		case 0:
			adapter = mApp.getOpenTaskIds();
			break;
		case 1:
			adapter = mApp.getClaimedTaskIds();
			break;
		case 2:
			adapter = mApp.getRequestedTaskIds();
			break;
		}
		ListFragment fragment = new ListFragment();
		fragment.setListAdapter(adapter);
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}
}
