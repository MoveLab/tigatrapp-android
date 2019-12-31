package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;
import ceab.movelab.tigabib.ContProvContractReports.Reports;
import ceab.movelab.tigabib.adapters.MissionAdapter;
import ceab.movelab.tigabib.services.SyncData;

public class MissionActivity extends Activity {
	private static String TAG = "MissionActivity";
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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

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

		ArrayList<MissionItemModel> list = new ArrayList<MissionItemModel>();

		final Bundle b = getIntent().getExtras();

		String taskJson = b.getString(Tasks.KEY_TASK_JSON);

		if ( b.containsKey(Tasks.KEY_RESPONSES_JSON) ) {
			currentResponses = b.getString(Tasks.KEY_RESPONSES_JSON);
		}

		if ( b.containsKey(Tasks.KEY_ID) ) {
			missionId = b.getInt(Tasks.KEY_ID);
		}

		final JSONObject thisTask;

		try {
			String currentLang = PropertyHolder.getLanguage();
			thisTask = new JSONObject(taskJson);

			if (thisTask.has(MissionModel.KEY_TITLE)
					&& Util.getString(thisTask, MissionModel.KEY_TITLE).length() > 0) {
				taskTitle.setText(Util.getString(thisTask, MissionModel.KEY_TITLE));
			} else if (thisTask.has(MissionModel.KEY_TITLE_CATALAN)
					&& thisTask.has(MissionModel.KEY_TITLE_SPANISH)
					&& thisTask.has(MissionModel.KEY_TITLE_ENGLISH)) {
				if (currentLang.equals("ca"))
					taskTitle.setText(Util.getString(thisTask, MissionModel.KEY_TITLE_CATALAN));
				else if (currentLang.equals("es"))
					taskTitle.setText(Util.getString(thisTask, MissionModel.KEY_TITLE_SPANISH));
				else if (currentLang.equals("en"))
					taskTitle.setText(Util.getString(thisTask, MissionModel.KEY_TITLE_ENGLISH));
			} else
				taskTitle.setVisibility(View.GONE);

			if (thisTask.has(MissionModel.KEY_LONG_DESCRIPTION_CATALAN)
					&& thisTask.has(MissionModel.KEY_LONG_DESCRIPTION_SPANISH)
					&& thisTask.has(MissionModel.KEY_LONG_DESCRIPTION_ENGLISH)) {
				if (currentLang.equals("ca"))
					taskDetail.setText(Util.getString(thisTask, MissionModel.KEY_LONG_DESCRIPTION_CATALAN));
				else if (currentLang.equals("es"))
					taskDetail.setText(Util.getString(thisTask, MissionModel.KEY_LONG_DESCRIPTION_SPANISH));
				else if (currentLang.equals("en"))
					taskDetail.setText(Util.getString(thisTask, MissionModel.KEY_LONG_DESCRIPTION_ENGLISH));
			} else
				taskDetail.setVisibility(View.GONE);
			if (thisTask.has(MissionModel.KEY_HELP_TEXT_CATALAN)
					&& thisTask.has(MissionModel.KEY_HELP_TEXT_SPANISH)
					&& thisTask.has(MissionModel.KEY_HELP_TEXT_ENGLISH)) {
				String helpTextTemp = "";
				if (currentLang.equals("ca"))
					helpTextTemp = thisTask.getString(MissionModel.KEY_HELP_TEXT_CATALAN);
				else if (currentLang.equals("es"))
					helpTextTemp = thisTask.getString(MissionModel.KEY_HELP_TEXT_SPANISH);
				else if (currentLang.equals("en"))
					helpTextTemp = thisTask.getString(MissionModel.KEY_HELP_TEXT_ENGLISH);

				final String helpText = helpTextTemp;

				helpIcon.setVisibility(View.VISIBLE);
				taskHeader.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Util.showHelp(MissionActivity.this, helpText);
						return;
					}
				});
			} else
				helpIcon.setVisibility(View.GONE);

			if ( thisTask.has(MissionModel.KEY_ITEMS )) {
				JSONArray theseItems = new JSONArray(thisTask.getString(MissionModel.KEY_ITEMS));
				for (int i = 0; i < theseItems.length(); i++) {
					MissionItemModel thisTaskItem = new MissionItemModel(MissionActivity.this, new JSONObject(theseItems.getString(i)));

					if ( currentResponses != null ) {
						JSONObject cr = new JSONObject(currentResponses);
						if ( cr.has(thisTaskItem.getItemId()) ) {
							String thisItemResponse = cr.getString(thisTaskItem.getItemId());

							thisTaskItem.setItemResponse(thisItemResponse);
						}
					}
					list.add(thisTaskItem);
				}
			} else {
				lv.setDivider(getResources().getDrawable(R.drawable.divider_invisible));
			}
			final MissionAdapter adapter = new MissionAdapter(this, list, getResources());
			lv.setAdapter(adapter);

			if (thisTask.has(MissionModel.KEY_PRESET_CONFIGURATION)) {

				// / PRECONFIGURE MODELS
				if (thisTask.getInt(MissionModel.KEY_PRESET_CONFIGURATION) == MissionModel.PRECONFIRUATION_ADULTS) {
					// / ADULTS

					buttonLeft.setVisibility(View.GONE);
					buttonMiddle.setVisibility(View.GONE);
					buttonRight.setMinHeight(1);
					buttonRight.setMinimumHeight(1);
					buttonRight.setVisibility(View.VISIBLE);
					buttonRight.setText(getResources().getString(R.string.ok));
					buttonRight.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intentReport = new Intent();
							if ( responses.length() > 0 ) {
								intentReport.putExtra(Tasks.KEY_RESPONSES_JSON, responses.toString());

								// For adult report, user must select something
								// for each question to send it. So testing that here:
								int responseCount = 0;
								Iterator<String> iter = responses.keys();
								while (iter.hasNext()) {
									String key = iter.next();
									try {
										JSONObject thisItem = responses.getJSONObject(key);
										if (!thisItem.getString(MissionItemModel.KEY_ITEM_RESPONSE).equals(getResources()
														.getString(R.string.spinner_nothing_selected)))
											responseCount++;
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if (responseCount == 3) {
									intentReport.putExtra(Reports.KEY_CONFIRMATION_CODE, Report.CONFIRMATION_CODE_POSITIVE);
									setResult(RESULT_OK, intentReport);
								}
							}
							finish();
						}
					});

				} else if (thisTask.getInt(MissionModel.KEY_PRESET_CONFIGURATION) == MissionModel.PRECONFIRUATION_SITES) {

					buttonLeft.setVisibility(View.GONE);
					buttonMiddle.setVisibility(View.GONE);
					buttonRight.setVisibility(View.VISIBLE);
					buttonRight.setText(getResources().getString(R.string.ok));
					buttonRight.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent dataForReport = new Intent();
							if ( responses.length() > 0 ) {
								dataForReport.putExtra(Tasks.KEY_RESPONSES_JSON, responses.toString());

								// For site report, user must select something
								// for each question to send it. So testing that here:
								int responseCount = 0;
								Iterator<String> iter = responses.keys();
								while (iter.hasNext()) {
									String key = iter.next();
									try {
										JSONObject thisItem = responses.getJSONObject(key);
										if (!thisItem.getString(MissionItemModel.KEY_ITEM_RESPONSE).equals(getResources()
														.getString(R.string.spinner_nothing_selected)))
											responseCount++;
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if (responseCount == 4) {
									dataForReport.putExtra(Reports.KEY_CONFIRMATION_CODE, Report.CONFIRMATION_CODE_POSITIVE);
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

				if (thisTask.has(MissionModel.KEY_TASK_BUTTON_LEFT_TEXT)) {
					int buttonCode = thisTask.getInt(MissionModel.KEY_TASK_BUTTON_LEFT_TEXT);
					buttonLeft.setText(buttonCode == MissionModel.BUTTONTEXT_MARK_COMPLETE ? getResources()
									.getString(
											R.string.mission_button_mark_complete)
									: (buttonCode == MissionModel.BUTTONTEXT_URL_TASK ? getResources()
											.getString(
													R.string.mission_button_left_url)
											: getResources()
													.getString(
															R.string.mission_button_left_survey)));
				} else {
					buttonLeft.setText(getResources().getString(
							R.string.mission_button_left_survey));
				}
				if (thisTask.has(MissionModel.KEY_TASK_BUTTON_LEFT_ACTION)) {
					String leftUrl = thisTask.has(MissionModel.KEY_TASK_BUTTON_LEFT_URL) ? thisTask
							.getString(MissionModel.KEY_TASK_BUTTON_LEFT_URL)
							: null;
					buttonLeft.setOnClickListener(makeOnClickListener(thisTask
							.getInt(MissionModel.KEY_TASK_BUTTON_LEFT_ACTION),
							b, leftUrl));
				}

				// MIDDLE BUTTON
				if (!thisTask.has(MissionModel.KEY_TASK_BUTTON_MIDDLE_VISIBLE)
						|| thisTask.getInt(MissionModel.KEY_TASK_BUTTON_MIDDLE_VISIBLE) == 0)
					buttonMiddle.setVisibility(View.GONE);
				else {
					buttonMiddle.setVisibility(View.VISIBLE);

					// FOR NOW MIDDLE TEXT FIXED DEPENDING ON TASK TYPE
					if (buttonLeft.getText().equals(getResources().getString(R.string.mission_button_left_url))) {
						buttonMiddle.setText(getResources().getString(R.string.mission_button_mark_complete));
					} else {
						buttonMiddle.setText(getResources().getString(R.string.mission_button_middle));
					}
					if (thisTask.has(MissionModel.KEY_TASK_BUTTON_MIDDLE_ACTION)) {
						String middleUrl = thisTask.has(MissionModel.KEY_TASK_BUTTON_MIDDLE_URL) ? thisTask
								.getString(MissionModel.KEY_TASK_BUTTON_MIDDLE_URL)
								: null;
						buttonMiddle.setOnClickListener(makeOnClickListener(
										thisTask.getInt(MissionModel.KEY_TASK_BUTTON_MIDDLE_ACTION),
										b, middleUrl));
					}
				}

				// RIGHT BUTTON
				if (!thisTask.has(MissionModel.KEY_TASK_BUTTON_RIGHT_VISIBLE)
						|| thisTask.getInt(MissionModel.KEY_TASK_BUTTON_RIGHT_VISIBLE) == 0)
					buttonRight.setVisibility(View.GONE);
				else {
					buttonRight.setVisibility(View.VISIBLE);

					// FOR NOW ONLY ONE OPTION FOR RIGHT TEXT
					buttonRight.setText(getResources().getString(R.string.mission_button_right));

					if (thisTask.has(MissionModel.KEY_TASK_BUTTON_RIGHT_ACTION)) {
						String rightUrl = thisTask.has(MissionModel.KEY_TASK_BUTTON_RIGHT_URL) ? thisTask
								.getString(MissionModel.KEY_TASK_BUTTON_RIGHT_URL)
								: null;
						buttonRight.setOnClickListener(makeOnClickListener(
										thisTask.getInt(MissionModel.KEY_TASK_BUTTON_RIGHT_ACTION),
										b, rightUrl));
					}
				}
			}

		} catch (JSONException e) {
			Util.logError(TAG, "error: " + e);
		}

	}

	@Override
	protected void onResume() {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
	}

	public OnClickListener makeOnClickListener(int action, Bundle taskInfoBundle, String url) {
		final int thisAction = action;
		final Bundle thisBundle = taskInfoBundle;
		final String thisUrl = url;
		OnClickListener result = null;
		switch (thisAction) {
		case (MissionModel.BUTTONACTIONS_DO_TASK): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.logInfo(TAG, "do task clicked");
					ContentResolver cr = getContentResolver();
					ContentValues cv = new ContentValues();
					int rowId = thisBundle.getInt(Tasks.KEY_ROW_ID);
					String sc = Tasks.KEY_ROW_ID + " = " + rowId;
					if ( responses.length() > 0 ) {
						cv.put(Tasks.KEY_RESPONSES_JSON, responses.toString());
					}
					cv.put(Tasks.KEY_DONE, 1);
					cr.update(Util.getMissionsUri(MissionActivity.this), cv, sc, null);
					// CHECK IF NOTIFICATIONS SHOULD BE REMOVED
					sc = Tasks.KEY_ACTIVE + " = 1 AND " + Tasks.KEY_DONE
							+ " = 0 AND " + Tasks.KEY_EXPIRATION_TIME
							+ " <= " + System.currentTimeMillis();
					Cursor c = cr.query(Util.getMissionsUri(MissionActivity.this), Tasks.KEYS_DONE, sc, null, null);
Util.logInfo(TAG, "remaining tasks: " + c.getCount());
					if (c.getCount() == 0) {
						Util.internalBroadcast(MissionActivity.this, Messages.REMOVE_TASK_NOTIFICATION);
Util.logInfo(TAG, "just broadcast: " + Messages.REMOVE_TASK_NOTIFICATION);
					}

					c.close();

					long currentTime = System.currentTimeMillis();
					String currentTimeString = Util.ecma262(currentTime);

					String packageName = "";
					int packageVersion = Report.MISSING;
					try {
						PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
						packageName = pInfo.packageName;
						packageVersion = pInfo.versionCode;
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}

					String phoneManufacturer = Build.MANUFACTURER;
					String phoneModel = Build.MODEL;

					String os = "Android";
					String osversion = Integer.toString(Build.VERSION.SDK_INT);
					String osLanguage = Locale.getDefault().getLanguage();
					String appLanguage = PropertyHolder.getLanguage();

					Report missionReport = new Report(MissionActivity.this, UUID.randomUUID().toString(),
							PropertyHolder.getUserId(), Util.makeReportId(), 0,
							currentTime, currentTimeString, currentTimeString,
							Report.TYPE_MISSION, responses.toString(),
							Report.CONFIRMATION_CODE_MISSING,
							Report.LOCATION_CHOICE_MISSING, Report.MISSING,
							Report.MISSING, Report.MISSING, Report.MISSING,
							Report.NO, null, null, Report.UPLOADED_NONE,
							Report.MISSING, Report.NO, Report.YES, packageName,
							packageVersion, phoneManufacturer, phoneModel, os,
							osversion, osLanguage, appLanguage, missionId);

					// First save report to internal DB
					cr.insert(Util.getReportsUri(MissionActivity.this), ContProvValuesReports.createReport(missionReport));

					if (thisUrl != null) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(thisUrl));
						startActivity(i);
					}

					// take 1 fix for this task
					Util.internalBroadcast(MissionActivity.this, Messages.START_TASK_FIX);

					// Now trigger sync task
					Intent syncIntent = new Intent(MissionActivity.this, SyncData.class);
					syncIntent.setPackage(MissionActivity.this.getPackageName());
					startService(syncIntent);

					finish();
				}
			};
			break;
		}
		case (MissionModel.BUTTONACTIONS_DO_TASK_LATER): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			};
			break;
		}
		case (MissionModel.BUTTONACTIONS_DELETE_TASK): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentResolver cr = getContentResolver();
					ContentValues cv = new ContentValues();
					int rowId = thisBundle.getInt(Tasks.KEY_ROW_ID);
					String sc = Tasks.KEY_ROW_ID + " = " + rowId;
					cr.delete(Util.getMissionsUri(MissionActivity.this), sc, null);
					// CHECK IF NOTIFICATIONS SHOULD BE REMOVED
					sc = Tasks.KEY_DONE + " = 0 AND "
							+ Tasks.KEY_EXPIRATION_TIME + " <= " + System.currentTimeMillis();
					Cursor c = cr.query(Util.getMissionsUri(MissionActivity.this), Tasks.KEYS_DONE, sc, null, null);
					if (c.getCount() == 0) {
						Util.internalBroadcast(MissionActivity.this, Messages.REMOVE_TASK_NOTIFICATION);
					}
					c.close();
					finish();
				}
			};
			break;
		}
		case (MissionModel.BUTTONACTIONS_GO_TO_URL): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (thisUrl != null) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(thisUrl));
						startActivity(i);
					}
				}
			};
			break;
		}
		case (MissionModel.BUTTONACTIONS_MARK_COMPLETE): {
			result = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.logInfo(TAG, "do task clicked");
					ContentResolver cr = getContentResolver();
					ContentValues cv = new ContentValues();
					int rowId = thisBundle.getInt(Tasks.KEY_ROW_ID);
					String sc = Tasks.KEY_ROW_ID + " = " + rowId;
					if ( responses.length() > 0 ) {
						cv.put(Tasks.KEY_RESPONSES_JSON, responses.toString());
					}
					cv.put(Tasks.KEY_DONE, 1);
					cr.update(Util.getMissionsUri(MissionActivity.this), cv, sc, null);
					// CHECK IF NOTIFICATIONS SHOULD BE REMOVED
					sc = Tasks.KEY_ACTIVE + " = 1 AND " + Tasks.KEY_DONE
							+ " = " + "0 AND " + Tasks.KEY_EXPIRATION_TIME
							+ " <=" + System.currentTimeMillis();
					Cursor c = cr.query(Util.getMissionsUri(MissionActivity.this),
							Tasks.KEYS_DONE, sc, null, null);
					Util.logInfo(TAG, "remaining tasks: " + c.getCount());
					if (c.getCount() == 0) {
						Util.internalBroadcast(MissionActivity.this, Messages.REMOVE_TASK_NOTIFICATION);
						Util.logInfo(TAG, "just broadcast: " + Messages.REMOVE_TASK_NOTIFICATION);
					}

					c.close();

					long currentTime = System.currentTimeMillis();
					String currentTimeString = Util.ecma262(currentTime);

					String packageName = "";
					int packageVersion = Report.MISSING;
					try {
						PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
						packageName = pInfo.packageName;
						packageVersion = pInfo.versionCode;
					} catch (NameNotFoundException e) {
					}

					String phoneManufacturer = Build.MANUFACTURER;
					String phoneModel = Build.MODEL;

					String os = "Android";
					String osVersion = Integer.toString(Build.VERSION.SDK_INT);
					String osLanguage = Locale.getDefault().getLanguage();
					String appLanguage = PropertyHolder.getLanguage();

					Report missionReport = new Report(MissionActivity.this, UUID.randomUUID().toString(),
							PropertyHolder.getUserId(), Util.makeReportId(), 0,
							currentTime, currentTimeString, currentTimeString,
							Report.TYPE_MISSION, responses.toString(),
							Report.CONFIRMATION_CODE_MISSING,
							Report.LOCATION_CHOICE_MISSING, Report.MISSING,
							Report.MISSING, Report.MISSING, Report.MISSING,
							Report.NO, null, null, Report.UPLOADED_NONE,
							Report.MISSING, Report.NO, Report.YES, packageName,
							packageVersion, phoneManufacturer, phoneModel, os,
							osVersion, osLanguage, appLanguage, missionId);

					// First save report to internal DB
					cr.insert(Util.getReportsUri(MissionActivity.this), ContProvValuesReports.createReport(missionReport));

					// take 1 fix for this task
					Util.internalBroadcast(MissionActivity.this, Messages.START_TASK_FIX);

					// Now trigger sync task
					Intent syncIntent = new Intent(MissionActivity.this, SyncData.class);
					syncIntent.setPackage(MissionActivity.this.getPackageName());
					startService(syncIntent);

					finish();
				}
			};
			break;
		}

		}
		return result;
	}

}
