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
		switch(index){
		case 0:
			ListFragment fragment = new ListFragment();
			fragment.setListAdapter(mApp.getOpenTaskIds());
			return fragment;
		case 1:
			return new TasksFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
