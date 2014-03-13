package ceab.movlab.tigerapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movelab.tigerapp.R;

public class TaskActivity extends Activity {
	Context context = this;
	String lang;
	ListView lv;
	TextView taskTitle;
	TextView taskDetail;
	LinearLayout taskHeader;
	ImageView helpIcon;
	Button buttonLeft;
	Button buttonRight;
	public JSONObject responses;
	String currentResponses;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(!PropertyHolder.isInit())
			PropertyHolder.init(context);
		
		lang = PropertyHolder.getLanguage();
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		setContentView(R.layout.task);
		View header = getLayoutInflater().inflate(R.layout.task_head, null);
		View footer = getLayoutInflater().inflate(R.layout.task_foot, null);
		lv = (ListView) findViewById(R.id.listview);
		lv.addHeaderView(header);
		lv.addFooterView(footer);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		responses = new JSONObject();

		taskTitle = (TextView) findViewById(R.id.taskTitle);
		taskDetail = (TextView) findViewById(R.id.taskDetail);
		helpIcon = (ImageView) findViewById(R.id.helpIcon);
		taskHeader = (LinearLayout) findViewById(R.id.header);
		buttonLeft = (Button) findViewById(R.id.buttonLeft);
		buttonRight = (Button) findViewById(R.id.buttonRight);

		ArrayList<TaskItemModel> list = new ArrayList<TaskItemModel>();

		final Bundle b = getIntent().getExtras();

		String taskJson = b.getString(Tasks.KEY_TASK_JSON);
		Log.d("TA1", taskJson);
		if (b.containsKey(Tasks.KEY_RESPONSES_JSON)) {
			currentResponses = b.getString(Tasks.KEY_RESPONSES_JSON);
			Log.d("TA2", currentResponses);

		}
		try {
			final JSONObject thisTask;
			thisTask = new JSONObject(taskJson);

			if (thisTask.has(TaskModel.KEY_TASK_TITLE))
				taskTitle.setText(Util.getString(thisTask,
						TaskModel.KEY_TASK_TITLE));
			else
				taskTitle.setVisibility(View.GONE);

			if (thisTask.has(TaskModel.KEY_TASK_DETAIL))
				taskDetail.setText(Util.getString(thisTask,
						TaskModel.KEY_TASK_DETAIL));
			else
				taskDetail.setVisibility(View.GONE);
			if (thisTask.has(TaskModel.KEY_TASK_HELP)) {
				final String helpText = thisTask
						.getString(TaskModel.KEY_TASK_HELP);
				helpIcon.setVisibility(View.VISIBLE);
				taskHeader.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						Util.showHelp(context, helpText);
						return true;
					}
				});
			} else
				helpIcon.setVisibility(View.GONE);

			if (thisTask.has(TaskModel.KEY_TASK_ITEMS)) {
				JSONArray theseItems;
				theseItems = thisTask.getJSONArray(TaskModel.KEY_TASK_ITEMS);
				for (int i = 0; i < theseItems.length(); i++) {
					TaskItemModel thisTaskItem = new TaskItemModel(context,
							theseItems.getJSONObject(i));

					if (currentResponses != null) {
						JSONObject cr = new JSONObject(currentResponses);
						if (cr.has(thisTaskItem.getItemId())) {
							String thisItemResponse = cr.getString(thisTaskItem
									.getItemId());
							Log.d("TA3", thisItemResponse);
							Log.d("TA4", thisTaskItem.getItemId());

							thisTaskItem.setItemResponse(thisItemResponse);
						}
					}
					list.add(thisTaskItem);
				}
			} else {
				lv.setDivider(getResources().getDrawable(
						R.drawable.divider_invisible));
			}
			final TaskAdapter adapter = new TaskAdapter(this, list, res);
			lv.setAdapter(adapter);

			/************ BUTTON ACTION SWITCH **************/

			switch (thisTask.getInt(TaskModel.KEY_TASK_BUTTON_LEFT_ACTION)) {

			case (TaskModel.TASK_CONFIGURATION_SIMPLE): {
				buttonLeft.setVisibility(View.GONE);
				buttonRight.setText(getResources().getString(R.string.ok));
				buttonRight.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ContentResolver cr = getContentResolver();
						ContentValues cv = new ContentValues();
						int rowId = b.getInt(Tasks.KEY_ROW_ID);
						String sc = Tasks.KEY_ROW_ID + " = " + rowId;
						if (responses.length() > 0) {
							cv.put(Tasks.KEY_RESPONSES_JSON,
									responses.toString());
						}
						cv.put(Tasks.KEY_DONE, 1);
						cr.update(Tasks.CONTENT_URI, cv, sc, null);

						Cursor c = cr.query(Tasks.CONTENT_URI, Tasks.KEYS_DONE,
								Tasks.KEY_DONE + " = " + "0 AND "
										+ Tasks.KEY_EXPIRATION_DATE + " <="
										+ System.currentTimeMillis(), null,
								null);

						if (c.getCount() == 0) {
							Intent i = new Intent(
									TigerBroadcastReceiver.TIGER_TASK_CLEAR);
							context.sendBroadcast(i);
						}
						finish();
					}
				});

				break;

			}
			case (TaskModel.TASK_CONFIGURATION_SURVEY_TASK): {

				buttonLeft.setText("Submit");
				buttonLeft.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						ContentResolver cr = getContentResolver();
						ContentValues cv = new ContentValues();
						int rowId = b.getInt(Tasks.KEY_ROW_ID);
						String sc = Tasks.KEY_ROW_ID + " = " + rowId;
						if (responses.length() > 0) {
							cv.put(Tasks.KEY_RESPONSES_JSON,
									responses.toString());
						}
						cv.put(Tasks.KEY_DONE, 1);
						cr.update(Tasks.CONTENT_URI, cv, sc, null);

						Cursor c = cr.query(Tasks.CONTENT_URI, Tasks.KEYS_DONE,
								Tasks.KEY_DONE + " = " + "0 AND "
										+ Tasks.KEY_EXPIRATION_DATE + " <="
										+ System.currentTimeMillis(), null,
								null);

						if (c.getCount() == 0) {
							Intent i = new Intent(
									TigerBroadcastReceiver.TIGER_TASK_CLEAR);
							context.sendBroadcast(i);
						}

						// TODO
						/*
						 * upload
						 */
						Util.toast(context,
								"Uploading responses: " + responses.toString());

						finish();

						// TODO mark task as completed and/or delete from
						// phone's database

					}
				});

				buttonRight.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

				break;

			}

			case (TaskModel.TASK_CONFIGURATION_ADULT_TASK): {

				buttonLeft.setText("Start Report");
				buttonLeft.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent i = new Intent(TaskActivity.this,
								ReportTool.class);
						Bundle b = new Bundle();
						b.putInt("type", Report.TYPE_ADULT);
						i.putExtras(b);
						startActivity(i);
						finish();

						// TODO mark task as completed and/or delete from
						// phone's database

					}
				});

				buttonRight.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

				break;

			}
			case (TaskModel.TASK_CONFIGURATION_SITE_TASK): {

				buttonLeft.setText("Start Report");
				buttonLeft.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent i = new Intent(TaskActivity.this,
								ReportTool.class);
						Bundle b = new Bundle();
						b.putInt("type", Report.TYPE_BREEDING_SITE);
						i.putExtras(b);
						startActivity(i);

						finish();

						// TODO mark task as completed and/or delete from
						// phone's database

					}
				});
				buttonRight.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

				break;

			}

			case (TaskModel.TASK_CONFIGURATION_WEBLINK): {

				buttonLeft.setText("Go to website");
				buttonLeft.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {

							String url;
							url = thisTask
									.getString(TaskModel.KEY_TASK_BUTTON_LEFT_URL);

							ContentResolver cr = getContentResolver();
							ContentValues cv = new ContentValues();
							int rowId = b.getInt(Tasks.KEY_ROW_ID);
							String sc = Tasks.KEY_ROW_ID + " = " + rowId;
							if (responses.length() > 0) {
								cv.put(Tasks.KEY_RESPONSES_JSON,
										responses.toString());
							}
							cv.put(Tasks.KEY_DONE, 1);
							cr.update(Tasks.CONTENT_URI, cv, sc, null);

							Cursor c = cr.query(Tasks.CONTENT_URI,
									Tasks.KEYS_DONE, Tasks.KEY_DONE + " = "
											+ "0 AND "
											+ Tasks.KEY_EXPIRATION_DATE + " <="
											+ System.currentTimeMillis(), null,
									null);

							if (c.getCount() == 0) {
								Intent i = new Intent(
										TigerBroadcastReceiver.TIGER_TASK_CLEAR);
								context.sendBroadcast(i);
							}

							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(url));
							startActivity(i);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						finish();

					}
				});

				buttonRight.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

				break;

			}

			case (TaskModel.TASK_CONFIGURATION_REPORT_SITE): {

				buttonLeft.setVisibility(View.GONE);
				buttonRight.setText(getResources().getString(R.string.ok));
				buttonRight.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent dataForReport = new Intent();
						if (responses.length() > 0) {
							dataForReport.putExtra(Tasks.KEY_RESPONSES_JSON,
									responses.toString());

							Iterator<String> iter = responses.keys();
							while (iter.hasNext()) {
								String key = iter.next();
								try {
									JSONObject thisItem = responses
											.getJSONObject(key);
									if (thisItem.getString(
											TaskItemModel.KEY_ITEM_RESPONSE)
											.length() > 0
											&& !thisItem
													.getString(
															TaskItemModel.KEY_ITEM_RESPONSE)
													.equals(getResources()
															.getString(
																	R.string.spinner_nothing_selected))) {
										dataForReport
												.putExtra(
														Reports.KEY_CONFIRMATION_CODE,
														Report.CONFIRMATION_CODE_POSITIVE);
										setResult(RESULT_OK, dataForReport);
										break;
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
						finish();
					}
				});

				break;

			}

			case (TaskModel.TASK_CONFIGURATION_REPORT_ADULT): {

				buttonLeft.setVisibility(View.GONE);
				buttonRight.setText(getResources().getString(R.string.ok));
				buttonRight.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent dataForReport = new Intent();
						if (responses.length() > 0) {
							dataForReport.putExtra(Tasks.KEY_RESPONSES_JSON,
									responses.toString());

							Iterator<String> iter = responses.keys();
							while (iter.hasNext()) {
								String key = iter.next();
								try {
									JSONObject thisItem = responses
											.getJSONObject(key);
									// This is a bit ugly -- should figure out a
									// better way,
									// but I am doing it for now to save time
									if (thisItem.getString(
											TaskItemModel.KEY_ITEM_RESPONSE)
											.equals(getResources().getString(
													R.string.yes))) {
										dataForReport
												.putExtra(
														Reports.KEY_CONFIRMATION_CODE,
														Report.CONFIRMATION_CODE_POSITIVE);
										setResult(RESULT_OK, dataForReport);
										break;
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
						finish();
					}
				});

				break;

			}

			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
