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
		ListFragment fragment = null;
		TasksAdapter adapter = null;
		switch(index){
		case 0:
			fragment = new OpenTaskFragment();
			adapter = mApp.getOpenTaskIds();
			break;
		case 1:
			fragment = new ClaimedTaskFragment();
			adapter = mApp.getClaimedTaskIds();
			break;
		case 2:
			fragment = new RequestedTaskFragment();
			adapter = mApp.getRequestedTaskIds();
			break;
		}
		fragment.setListAdapter(adapter);
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}
}
