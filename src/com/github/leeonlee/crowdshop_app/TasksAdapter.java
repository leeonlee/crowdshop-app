package com.github.leeonlee.crowdshop_app;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TasksAdapter extends ArrayAdapter<Long> {

	public TasksAdapter(Context context) {
		super(context, R.layout.open_task);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
}
