package com.github.leeonlee.crowdshop_app;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TasksAdapter extends ArrayAdapter<Long> {

	private static final String TAG = TasksAdapter.class.getSimpleName();
	private final CrowdShopApplication mApp;

	public TasksAdapter(CrowdShopApplication app) {
		super(app, R.layout.open_task);
		mApp = app;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)mApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView view = (TextView)inflater.inflate(R.layout.open_task, null);
		long taskId = getItem(position);
		view.setText(mApp.getTaskInfo(taskId).toString());
		return view;
	}
}
