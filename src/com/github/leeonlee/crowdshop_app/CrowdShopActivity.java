package com.github.leeonlee.crowdshop_app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * The base class for all activities in CrowdShop.
 */
public abstract class CrowdShopActivity extends FragmentActivity {

	protected final SpiceManager mSpiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);
	protected CrowdShopApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (CrowdShopApplication)getApplication();
	}

	@Override
	protected void onStart() {
		mSpiceManager.start(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		mSpiceManager.shouldStop();
		super.onStop();
	}

}