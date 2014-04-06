package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private TextView stuff;
	private Button claim;
	private long mTaskId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.detail_layout);
		setContentView(R.layout.login_layout);
		mTaskId = getIntent().getLongExtra(CrowdShopApplication.TASK_ID, 0l);

		// populates textviews and stuff with stuff from the application class

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		// login.setTypeface(type);

		/*
		claim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// call some api that marks it as claimed
			}
		});
		*/
	}
}
