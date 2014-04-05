package com.github.leeonlee.crowdshop_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter{

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public Fragment getItem(int index){
		switch(index){
		case 0:
			return new FriendFragment();
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
