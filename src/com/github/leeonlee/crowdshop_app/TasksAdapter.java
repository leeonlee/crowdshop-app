package com.github.leeonlee.crowdshop_app;

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.models.UserInfo;

public class TasksAdapter extends ArrayAdapter<Long> {

	private static final String TAG = TasksAdapter.class.getSimpleName();
	private final CrowdShopApplication mApp;

	public TasksAdapter(CrowdShopApplication app) {
		super(app, R.layout.task_list_item);
		mApp = app;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mApp
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.task_list_item, null);
		long taskId = getItem(position);
		TaskInfo taskInfo = mApp.getTaskInfo(taskId);

		TextView title = (TextView) layout
				.findViewById(R.id.task_list_item_title);
		title.setText(taskInfo.name);
		title.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
				"fonts/Hey_Pretty_Girl.ttf"));
		title.setTextColor(Color.BLACK);

		TextView dateView = (TextView) layout
				.findViewById(R.id.task_list_item_date);
		dateView.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
				"fonts/Hey_Pretty_Girl.ttf"));
		dateView.setTextColor(Color.BLACK);
		Time time = new Time();
		time.parse3339(taskInfo.timestamp);
		Date date = new Date(time.toMillis(false));
		dateView.setText(DateFormat.getLongDateFormat(mApp).format(date));

		long thisUserId = mApp.getThisUserId();
		Long userId = taskInfo.creatorUserId == thisUserId ? taskInfo.claimerUserId
				: Long.valueOf(taskInfo.creatorUserId);
		if (userId != null) {
			UserInfo userInfo = mApp.getUserInfo(userId);
			TextView user = (TextView) layout
					.findViewById(R.id.task_list_item_user);
			user.setText(userInfo.firstName + " " + userInfo.lastName);
			user.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Hey_Pretty_Girl.ttf"));
			user.setTextColor(Color.BLACK);
		}
		return layout;
	}
}
