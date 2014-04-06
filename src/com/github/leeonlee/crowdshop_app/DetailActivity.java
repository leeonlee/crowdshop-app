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

	Long task_id;
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);

		task_id = this.getIntent().getLongExtra("task_id", 0);
		username = this.getIntent().getStringExtra("username");

		// call some rest api that fetches the information of the task_id and
		// populates textviews and stuff

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		// login.setTypeface(type);

		claim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// call some api that marks it as claimed
			}
		});
	}
}
