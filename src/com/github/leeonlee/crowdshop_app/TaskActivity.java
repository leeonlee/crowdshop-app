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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TaskActivity extends Activity {
	Button submit;
	EditText title, desc, budget, reward;
	ProgressDialog pd;
	Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_layout);

		mActivity = this;

		submit = (Button) findViewById(R.id.submit);
		title = (EditText) findViewById(R.id.title);
		desc = (EditText) findViewById(R.id.desc);
		reward = (EditText) findViewById(R.id.reward);
		budget = (EditText) findViewById(R.id.budget);

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
					new CreateTask().execute("a", titleString, descString,
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

	private class CreateTask extends AsyncTask<String, String, String> {
		public void onPreExecute() {
			pd = new ProgressDialog(mActivity);
			pd.setCancelable(true);
			pd.setMessage("Submitting..");
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String urlString = "http://crowdshop-server.herokuapp.com/createtask/";

			String result = "";

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
				System.out.println(stuff);

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(result);
			System.out.println("wtf");
			return result;
		}

		protected void onPostExecute(String result) {
			pd.cancel();
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context,
					"Submission complete!", duration);
			toast.show();
			mActivity.finish();
		}

	}

}