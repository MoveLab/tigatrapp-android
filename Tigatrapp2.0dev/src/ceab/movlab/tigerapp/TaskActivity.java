package ceab.movlab.tigerapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;
import ceab.movlab.tigerapp.ContentProviderContractPhotos.TigaPhotos;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;

public class TaskActivity extends Activity {
	Context context = this;
	String lang;
	ListView lv;
	int missionId;
	TextView taskTitle;
	TextView taskDetail;
	LinearLayout taskHeader;
	ImageView helpIcon;
	Button buttonLeft;
	Button buttonMiddle;
	Button buttonRight;
	public JSONObject responses;
	String currentResponses;
	Resources res;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

		res = getResources();

		setContentView(R.layout.task);
		View header = getLayoutInflater().inflate(R.layout.task_head, null);
		View footer = getLayoutInflater().inflate(R.layout.task_foot, null);
		lv = (ListView) findViewById(R.id.listview);
		lv.addHeaderView(header);
		lv.addFooterView(footer);

		responses = new JSONObject();

		taskTitle = (TextView) findViewById(R.id.taskTitle);
		taskDetail = (TextView) findViewById(R.id.taskDetail);
		helpIcon = (ImageView) findViewById(R.id.helpIcon);
		taskHeader = (LinearLayout) findViewById(R.id.header);
		buttonLeft = (Button) findViewById(R.id.buttonLeft);
		buttonMiddle = (Button) findViewById(R.id.buttonMiddle);
		buttonRight = (Button) findViewById(R.id.buttonRight);

		ArrayList<TaskItemModel> list = new ArrayList<TaskItemModel>();

		final Bundle b = getIntent().getExtras();

		String taskJson = b.getString(Tasks.KEY_TASK_JSON);

		if (b.containsKey(Tasks.KEY_RESPONSES_JSON)) {
			currentResponses = b.getString(Tasks.KEY_RESPONSES_JSON);

		}

		if (b.containsKey(Tasks.KEY_ID)) {
			missionId = b.getInt(Tasks.KEY_ID);

		}

		final JSONObject thisTask;

		try {

			String currentLang = PropertyHolder.getLanguage();

			thisTask = new JSONObject(taskJson);

			if (thisTask.has(TaskModel.KEY_TITLE)
					&& Util.getString(thisTask, TaskModel.KEY_TITLE).length() > 0) {
				taskTitle
						.setText(Util.getString(thisTask, TaskModel.KEY_TITLE));
			} else if (thisTask.has(TaskModel.KEY_TITLE_CATALAN)
					&& thisTask.has(TaskModel.KEY_TITLE_SPANISH)
					&& thisTask.has(TaskModel.KEY_TITLE_ENGLISH)) {
				if (currentLang.equals("ca"))
					taskTitle.setText(Util.getString(thisTask,
							TaskModel.KEY_TITLE_CATALAN));
				else if (currentLang.equals("es"))
					taskTitle.setText(Util.getString(thisTask,
							TaskModel.KEY_TITLE_SPANISH));
				else if (currentLang.equals("en"))
					taskTitle.setText(Util.getString(thisTask,
							TaskModel.KEY_TITLE_ENGLISH));
			} else
				taskTitle.setVisibility(View.GONE);

			if (thisTask.has(TaskModel.KEY_LONG_DESCRIPTION_CATALAN)
					&& thisTask.has(TaskModel.KEY_LONG_DESCRIPTION_SPANISH)
					&& thisTask.has(TaskModel.KEY_LONG_DESCRIPTION_ENGLISH)) {

				if (currentLang.equals("ca"))
					taskDetail.setText(Util.getString(thisTask,
							TaskModel.KEY_LONG_DESCRIPTION_CATALAN));
				else if (currentLang.equals("es"))
					taskDetail.setText(Util.getString(thisTask,
							TaskModel.KEY_LONG_DESCRIPTION_SPANISH));
				else if (currentLang.equals("en"))
					taskDetail.setText(Util.getString(thisTask,
							TaskModel.KEY_LONG_DESCRIPTION_ENGLISH));
			} else
				taskDetail.setVisibility(View.GONE);
			if (thisTask.has(TaskModel.KEY_HELP_TEXT_CATALAN)
					&& thisTask.has(TaskModel.KEY_HELP_TEXT_SPANISH)
					&& thisTask.has(TaskModel.KEY_HELP_TEXT_ENGLISH)) {
				String helpTextTemp = "";
				if (currentLang.equals("ca"))
					helpTextTemp = thisTask
							.getString(TaskModel.KEY_HELP_TEXT_CATALAN);
				else if (currentLang.equals("es"))
					helpTextTemp = thisTask
							.getString(TaskModel.KEY_HELP_TEXT_SPANISH);
				else if (currentLang.equals("en"))
					helpTextTemp = thisTask
							.getString(TaskModel.KEY_HELP_TEXT_ENGLISH);

				final String helpText = helpTextTemp;

				helpIcon.setVisibility(View.VISIBLE);
				taskHeader.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Util.showHelp(context, helpText);
						return;
					}
				});
			} else
				helpIcon.setVisibility(View.GONE);

			if (thisTask.has(TaskModel.KEY_ITEMS)) {
				JSONArray theseItems = new JSONArray(
						thisTask.getString(TaskModel.KEY_ITEMS));
				for (int i = 0; i < theseItems.length(); i++) {
					TaskItemModel thisTaskItem = new TaskItemModel(context,
							new JSONObject(theseItems.getString(i)));

					if (currentResponses != null) {
						JSONObject cr = new JSONObject(currentResponses);
						if (cr.has(thisTaskItem.getItemId())) {
							String thisItemResponse = cr.getString(thisTaskItem
									.getItemId());

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

			if (thisTask.has(TaskModel.KEY_PRESET_CONFIGURATION)) {

				// / PRECONFIGURE MODELS
				if (thisTask.getInt(TaskModel.KEY_PRESET_CONFIGURATION) == TaskModel.PRECONFIRUATION_ADULTS) {
					// / ADULTS

					buttonLeft.setVisibility(View.GONE);
					buttonMiddle.setVisibility(View.GONE);
					buttonRight.setVisibility(View.VISIBLE);
					buttonRight.setText(getResources().getString(R.string.ok));
					buttonRight.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent dataForReport = new Intent();
							if (responses.length() > 0) {
								dataForReport.putExtra(
										Tasks.KEY_RESPONSES_JSON,
										responses.toString());

								// For adult report, user must select something
								// for
								// each question to send it. So testing that
								// here:
								int responseCount = 0;
								Iterator<String> iter = responses.keys();
								while (iter.hasNext()) {
									String key = iter.next();
									try {
										JSONObject thisItem = responses
												.getJSONObject(key);

										if (!thisItem
												.getString(
														TaskItemModel.KEY_ITEM_RESPONSE)
												.equals(getResources()
														.getString(
																R.string.spinner_nothing_selected)))
											responseCount++;
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if (responseCount == 3) {

									dataForReport.putExtra(
											Reports.KEY_CONFIRMATION_CODE,
											Report.CONFIRMATION_CODE_POSITIVE);
									setResult(RESULT_OK, dataForReport);
								}
							}
							finish();
						}
					});

				} else if (thisTask.getInt(TaskModel.KEY_PRESET_CONFIGURATION) == TaskModel.PRECONFIRUATION_SITES) {

					buttonLeft.setVisibility(View.GONE);
					buttonMiddle.setVisibility(View.GONE);
					buttonRight.setVisibility(View.VISIBLE);
					buttonRight.setText(getResources().getString(R.string.ok));
					buttonRight.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent dataForReport = new Intent();
							if (responses.length() > 0) {
								dataForReport.putExtra(
										Tasks.KEY_RESPONSES_JSON,
										responses.toString());

								// For site report, user must select something
								// for
								// each question to send it. So testing that
								// here:
								int responseCount = 0;
								Iterator<String> iter = responses.keys();
								while (iter.hasNext()) {
									String key = iter.next();
									try {
										JSONObject thisItem = responses
												.getJSONObject(key);

										if (!thisItem
												.getString(
														TaskItemModel.KEY_ITEM_RESPONSE)
												.equals(getResources()
														.getString(
																R.string.spinner_nothing_selected)))
											responseCount++;
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
								if (responseCount == 3) {

									dataForReport.putExtra(
											Reports.KEY_CONFIRMATION_CODE,
											Report.CONFIRMATION_CODE_POSITIVE);
									setResult(RESULT_OK, dataForReport);
								}
							}
							finish();
						}
					});
				}
			} else {

				// LEFT BUTTON
				buttonLeft.setVisibility(View.VISIBLE);

				if (thisTask.has(TaskModel.KEY_TASK_BUTTON_LEFT_TEXT)) {
					int buttonCode = thisTask
							.getInt(TaskModel.KEY_TASK_BUTTON_LEFT_TEXT);
					buttonLeft
							.setText(buttonCode == TaskModel.BUTTONTEXT_MARK_COMPLETE ? getResources()
									.getString(
											R.string.mission_button_mark_complete)
									: (buttonCode == TaskModel.BUTTONTEXT_URL_TASK ? getResources()
											.getString(
													R.string.mission_button_left_url)
											: getResources()
													.getString(
															R.string.mission_button_left_survey)));
				} else {
					buttonLeft.setText(getResources().getString(
							R.string.mission_button_left_survey));
				}
				if (thisTask.has(TaskModel.KEY_TASK_BUTTON_LEFT_ACTION)) {
					String leftUrl = thisTask
							.has(TaskModel.KEY_TASK_BUTTON_LEFT_URL) ? thisTask
							.getString(TaskModel.KEY_TASK_BUTTON_LEFT_URL)
							: null;
					buttonLeft.setOnClickListener(makeOnClickListener(thisTask
							.getInt(TaskModel.KEY_TASK_BUTTON_LEFT_ACTION), b,
							leftUrl));
				}

				// MIDDLE BUTTON
				if (!thisTask.has(TaskModel.KEY_TASK_BUTTON_MIDDLE_VISIBLE)
						|| thisTask
								.getInt(TaskModel.KEY_TASK_BUTTON_MIDDLE_VISIBLE) == 0)
					buttonMiddle.setVisibility(View.GONE);
				else {
					buttonMiddle.setVisibility(View.VISIBLE);

					// FOR NOW ONLY ONE OPTION FOR MIDDLE AND RIGHT TEXT
					buttonMiddle.setText(getResources().getString(
							R.string.mission_button_middle));
					if (thisTask.has(TaskModel.KEY_TASK_BUTTON_MIDDLE_ACTION)) {
						String middleUrl = thisTask
								.has(TaskModel.KEY_TASK_BUTTON_MIDDLE_URL) ? thisTask
								.getString(TaskModel.KEY_TASK_BUTTON_MIDDLE_URL)
								: null;
						buttonMiddle
								.setOnClickListener(makeOnClickListener(
										thisTask.getInt(TaskModel.KEY_TASK_BUTTON_MIDDLE_ACTION),
										b, middleUrl));
					}
				}

				// RIGHT BUTTON
				if (!thisTask.has(TaskModel.KEY_TASK_BUTTON_RIGHT_VISIBLE)
						|| thisTask
								.getInt(TaskModel.KEY_TASK_BUTTON_RIGHT_VISIBLE) == 0)
					buttonRight.setVisibility(View.GONE);
				else {
					buttonRight.setVisibility(View.VISIBLE);

					// FOR NOW ONLY ONE OPTION FOR MIDDLE AND RIGHT TEXT
					buttonRight.setText(getResources().getString(
							R.string.mission_button_right));

					if (thisTask.has(TaskModel.KEY_TASK_BUTTON_RIGHT_ACTION)) {
						String rightUrl = thisTask
								.has(TaskModel.KEY_TASK_BUTTON_RIGHT_URL) ? thisTask
								.getString(TaskModel.KEY_TASK_BUTTON_RIGHT_URL)
								: null;
						buttonRight
								.setOnClickListener(makeOnClickListener(
										thisTask.getInt(TaskModel.KEY_TASK_BUTTON_RIGHT_ACTION),
										b, rightUrl));
					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
	}

	public OnClickListener makeOnClickListener(int action,
			Bundle taskInfoBundle, String url) {
		final int thisAction = action;
		final Bundle thisBundle = taskInfoBundle;
		final String thisUrl = url;
		OnClickListener result = null;
		switch (thisAction) {
		case (TaskModel.BUTTONACTIONS_DO_TASK): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentResolver cr = getContentResolver();
					ContentValues cv = new ContentValues();
					int rowId = thisBundle.getInt(Tasks.KEY_ROW_ID);
					String sc = Tasks.KEY_ROW_ID + " = " + rowId;
					if (responses.length() > 0) {
						cv.put(Tasks.KEY_RESPONSES_JSON, responses.toString());
					}
					cv.put(Tasks.KEY_DONE, 1);
					cr.update(Tasks.CONTENT_URI, cv, sc, null);
					// CHECK IF NOTIFICATIONS SHOULD BE REMOVED
					sc = Tasks.KEY_DONE + " = " + "0 AND "
							+ Tasks.KEY_EXPIRATION_TIME + " <="
							+ System.currentTimeMillis();
					Cursor c = cr.query(Tasks.CONTENT_URI, Tasks.KEYS_DONE, sc,
							null, null);
					if (c.getCount() == 0) {
						Util.internalBroadcast(context,
								Messages.REMOVE_TASK_NOTIFICATION);
					}

					c.close();

					long currentTime = System.currentTimeMillis();
					String currentTimeString = Util.ecma262(currentTime);

					String packageName = "";
					int packageVersion = Report.MISSING;
					try {
						PackageInfo pInfo = getPackageManager().getPackageInfo(
								getPackageName(), 0);
						packageName = pInfo.packageName;
						packageVersion = pInfo.versionCode;
					} catch (NameNotFoundException e) {
					}

					String phoneManufacturer = Build.MANUFACTURER;
					String phoneModel = Build.MODEL;

					String os = "Android";
					String osversion = Integer.toString(Build.VERSION.SDK_INT);
					String osLanguage = Locale.getDefault().getLanguage();
					String appLanguage = PropertyHolder.getLanguage();

					Report missionReport = new Report(UUID.randomUUID()
							.toString(), PropertyHolder.getUserId(),
							Util.makeReportId(), 0, currentTime,
							currentTimeString, currentTimeString,
							Report.TYPE_MISSION, responses.toString(),
							Report.CONFIRMATION_CODE_MISSING,
							Report.LOCATION_CHOICE_MISSING, Report.MISSING,
							Report.MISSING, Report.MISSING, Report.MISSING,
							Report.NO, null, null, Report.UPLOADED_NONE,
							Report.MISSING, Report.NO, Report.YES, packageName,
							packageVersion, phoneManufacturer, phoneModel, os,
							osversion, osLanguage, appLanguage, missionId);

					// First save report to internal DB
					cr.insert(Reports.CONTENT_URI, ContentProviderValuesReports
							.createReport(missionReport));

					if (thisUrl != null) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(thisUrl));
						startActivity(i);
					}

					// take 1 fix for this task
					Util.internalBroadcast(context, Messages.START_TASK_FIX);

					// Now trigger sync task
					Intent syncIntent = new Intent(TaskActivity.this,
							SyncData.class);
					startService(syncIntent);

					finish();
				}
			};
			break;
		}
		case (TaskModel.BUTTONACTIONS_DO_TASK_LATER): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			};
			break;
		}
		case (TaskModel.BUTTONACTIONS_DELETE_TASK): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentResolver cr = getContentResolver();
					ContentValues cv = new ContentValues();
					int rowId = thisBundle.getInt(Tasks.KEY_ROW_ID);
					String sc = Tasks.KEY_ROW_ID + " = " + rowId;
					cr.delete(Tasks.CONTENT_URI, sc, null);
					// CHECK IF NOTIFICATIONS SHOULD BE REMOVED
					sc = Tasks.KEY_DONE + " = " + "0 AND "
							+ Tasks.KEY_EXPIRATION_TIME + " <="
							+ System.currentTimeMillis();
					Cursor c = cr.query(Tasks.CONTENT_URI, Tasks.KEYS_DONE, sc,
							null, null);
					if (c.getCount() == 0) {
						Util.internalBroadcast(context,
								Messages.REMOVE_TASK_NOTIFICATION);
					}
					c.close();
					finish();
					// TODO mark task as completed and/or delete from
					// phone's database
				}
			};
			break;
		}
		}
		return result;
	}

}
