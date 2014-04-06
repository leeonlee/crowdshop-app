package com.github.leeonlee.crowdshop_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class OpenTaskFragment extends ListFragment {

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Activity a = getActivity();
		a.startActivity(new Intent(a, DetailActivity.class)
			.putExtra(CrowdShopApplication.TASK_ID, (long)l.getItemAtPosition(position))
		);
	}
}
