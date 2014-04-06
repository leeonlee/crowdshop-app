package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MoneyActivity extends Activity {
	Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.money_layout);

		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");
		// login.setTypeface(type);

		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// call some api that marks the price
				// remember to check for empty values
			}
		});
	}
}