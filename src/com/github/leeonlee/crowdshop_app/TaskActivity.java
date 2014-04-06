package com.github.leeonlee.crowdshop_app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.leeonlee.crowdshop_app.models.TaskInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TaskActivity extends Activity {
	Button submit;
	EditText title, desc, budget, reward;
	TextView startText;
	ProgressDialog pd;
	Activity mActivity;
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_layout);

		mActivity = this;
		Typeface type = Typeface.createFromAsset(getAssets(),
				"fonts/Hey_Pretty_Girl.ttf");

		submit = (Button) findViewById(R.id.submit);
		submit.setTypeface(type);
		title = (EditText) findViewById(R.id.title);
		title.setTypeface(type);
		desc = (EditText) findViewById(R.id.desc);
		desc.setTypeface(type);
		;
		reward = (EditText) findViewById(R.id.reward);
		reward.setTypeface(type);
		budget = (EditText) findViewById(R.id.budget);
		budget.setTypeface(type);
		startText = (TextView) findViewById(R.id.startText);
		startText.setTypeface(type);

		username = this.getIntent().getStringExtra("username");

		submit.setClickable(true);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				String rewardString = reward.getText().toString();
				String budgetString = budget.getText().toString();
				String titleString = title.getText().toString();
				String descString = desc.getText().toString();

				if (titleString.equals("")) {
					Toast toast = Toast.makeText(context,
							"Please tell us what you need!", duration);
					toast.show();
				} else if (!rewardString.equals("")
						&& Integer.parseInt(rewardString) < 0) {
					Toast toast = Toast.makeText(context,
							"You cannot have a negative reward!", duration);
					toast.show();
				} else if (!budgetString.equals("")
						&& Integer.parseInt(budgetString) < 0) {
					Toast toast = Toast.makeText(context,
							"You cannot have a negative budget!", duration);
					toast.show();
				} else {
					new CreateTask().execute(username, titleString, descString,
							budgetString, rewardString);
				}
			}
		});
	}

	private static String convertInputStream(InputStream in) throws IOException {
		int bytesRead;
		byte[] contents = new byte[1024];
		String string = null;
		while ((bytesRead = in.read(contents)) != -1) {
			string = new String(contents, 0, bytesRead);
		}
		return string;
	}

	private class CreateTask extends AsyncTask<String, Void, JSONObject> {
		public void onPreExecute() {
			pd = new ProgressDialog(mActivity);
			pd.setCancelable(true);
			pd.setMessage("Submitting..");
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String urlString = "http://crowdshop-server.herokuapp.com/createtask/";
			JSONObject result = null;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(urlString);
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("username", params[0]));
				pairs.add(new BasicNameValuePair("title", params[1]));
				pairs.add(new BasicNameValuePair("desc", params[2]));
				pairs.add(new BasicNameValuePair("threshold", params[3]));
				pairs.add(new BasicNameValuePair("reward", params[4]));
				httppostreq.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpresponse = httpclient.execute(httppostreq);
				HttpEntity resultentity = httpresponse.getEntity();
				InputStream inputstream = resultentity.getContent();
				String stuff = convertInputStream(inputstream);
				result = new JSONObject(stuff);
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException(e1);
			} catch (ClientProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			return result;
		}

		protected void onPostExecute(JSONObject result) {
			pd.cancel();
			CrowdShopApplication app = (CrowdShopApplication)getApplicationContext();
			try
			{
				Log.d("", result.toString());
				app.requestTask(result.getLong("id"),
					TaskInfo.make(app.getThisUserId(), null, result));
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(app, "Submission complete!",
						duration);
				toast.show();
				mActivity.finish();
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
