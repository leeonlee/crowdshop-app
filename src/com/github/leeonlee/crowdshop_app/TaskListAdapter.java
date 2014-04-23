package com.github.leeonlee.crowdshop_app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.leeonlee.crowdshop_app.models.TaskInfo;
import com.github.leeonlee.crowdshop_app.models.UserInfo;

import java.util.Date;

public class TaskListAdapter extends ArrayAdapter<Long> {

	private static final String TAG = TaskListAdapter.class.getSimpleName();

	public TaskListAdapter(Context context) {
		super(context, R.layout.task_list_item);
	}

	public long[] getTaskIds() {
		long[] taskIds = new long[getCount()];
		for (int i = 0; i < taskIds.length; ++i)
			taskIds[i] = getItem(i);
		return taskIds;
	}

	public void setTaskIdsAndNotify(long... taskIds) {
		setNotifyOnChange(false);
		clear();
		for (long taskId : taskIds)
			add(taskId);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.task_list_item, null);
		long taskId = getItem(position);
		CrowdShopApplication app = (CrowdShopApplication)getContext().getApplicationContext();
		TaskInfo taskInfo = app.getTaskInfo(taskId);

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
		dateView.setText(DateFormat.getLongDateFormat(app).format(date));

		long thisUserId = app.getThisUserId();
		Long userId = taskInfo.creatorUserId == thisUserId ? taskInfo.claimerUserId
				: Long.valueOf(taskInfo.creatorUserId);
		if (userId != null) {
			UserInfo userInfo = app.getUserInfo(userId);
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
