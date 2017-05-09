/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: info@atrapaeltigre.com
 * 
 * This file is part of Tigatrapp.
 * 
 * Tigatrapp is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Tigatrapp is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file incorporates code from Space Mapper, which is subject 
 * to the following terms: 
 *
 * 		Space Mapper
 * 		Copyright (C) 2012, 2013 John R.B. Palmer
 * 		Contact: jrpalmer@princeton.edu
 *
 * 		Space Mapper is free software: you can redistribute it and/or modify 
 * 		it under the terms of the GNU General Public License as published by 
 * 		the Free Software Foundation, either version 3 of the License, or (at 
 * 		your option) any later version.
 * 
 * 		Space Mapper is distributed in the hope that it will be useful, but 
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of 
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along 
 * 		with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 */

package ceab.movelab.tigabib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import ceab.movelab.tigabib.ContProvContractReports.Reports;

public class ViewReportsTab extends TabActivity {

	private static String TAG = "View Reports Tab";

	Context context = this;
	String reportId;
	String title;
	int type;

	String lang;
	long reportTime;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		lang = Util.setDisplayLanguage(getResources());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_reports_tab);
		Resources resources = getResources();

		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();

		reportId = b.getString(Reports.KEY_REPORT_ID);
		type = b.getInt(Reports.KEY_TYPE);
		title = b.getString("title");
		reportTime = b.getLong("report_time");

		((TextView) findViewById(R.id.title)).setText(title);

		((Button) findViewById(R.id.buttonDelete))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						final AlertDialog.Builder dialog = new AlertDialog.Builder(
								context);
						dialog.setTitle(getResources().getString(
								R.string.delete_report));
						dialog.setMessage(getResources().getString(
								R.string.delete_report_warning));
						dialog.setPositiveButton(
								getResources().getString(R.string.delete),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d,
											int arg1) {
										new ReportDeleteTask().execute(context);
										d.dismiss();
									}

								});

						dialog.setNegativeButton(
								getResources().getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d,
											int arg1) {
										d.cancel();
									};
								});

						dialog.show();

					};
				});

		((Button) findViewById(R.id.buttonEdit))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(context, ReportToolActivity.class);

						i.putExtra("type", type);
						i.putExtra("reportId", reportId);
						context.startActivity(i);
						finish();
					};
				});

		((Button) findViewById(R.id.buttonCancel)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					};
				});

		TabHost tabHost = getTabHost();

		// checklist tab
		Intent intentChecklist = new Intent().setClass(this, ViewReportsChecklistTab.class);
		if (b.containsKey(Reports.KEY_CONFIRMATION))
			intentChecklist.putExtra(Reports.KEY_CONFIRMATION, b.getString(Reports.KEY_CONFIRMATION));
		TabSpec tabSpecChecklist = tabHost
				.newTabSpec(getResources().getString(R.string.checklist_tab_title))
				.setIndicator("", resources.getDrawable(R.drawable.view_reports_checklist_icon))
				.setContent(intentChecklist);

		// photos tab
		Intent intentPhotos = new Intent().setClass(this,
				ViewReportsPhotosTab.class);
		if (b.containsKey(Reports.KEY_PHOTO_URIS))
			intentPhotos.putExtra(Reports.KEY_PHOTO_URIS, b.getString(Reports.KEY_PHOTO_URIS));
		TabSpec tabSpecPhotos = tabHost
				.newTabSpec(getResources().getString(R.string.photos_tab_title))
				.setIndicator("", resources.getDrawable(R.drawable.view_reports_photos_icon))
				.setContent(intentPhotos);

		// Notes tab
		Intent intentNotes = new Intent().setClass(this,
				ViewReportsNotesTab.class);
		if (b.containsKey(Reports.KEY_NOTE))
			intentNotes.putExtra(Reports.KEY_NOTE, b.getString(Reports.KEY_NOTE));
		TabSpec tabSpecNotes = tabHost
				.newTabSpec(getResources().getString(R.string.notes_tab_title))
				.setIndicator("", resources.getDrawable(R.drawable.view_reports_notes_icon))
				.setContent(intentNotes);

		// add all tabs
		tabHost.addTab(tabSpecChecklist);
		tabHost.addTab(tabSpecPhotos);
		tabHost.addTab(tabSpecNotes);

		// set Windows tab as default (zero based)
		tabHost.setCurrentTab(0);
	}

	@Override
	protected void onResume() {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
	}

	public class ReportDeleteTask extends AsyncTask<Context, Integer, Boolean> {

		ProgressDialog prog;

		int myProgress;
		int resultFlag;

		int OFFLINE = 0;
		int UPLOAD_ERROR = 1;
		int DATABASE_ERROR = 2;
		int SUCCESS = 3;
		int PRIVATE_MODE = 4;

		@Override
		protected void onPreExecute() {

			PropertyHolder.init(context);
			resultFlag = SUCCESS;

			prog = new ProgressDialog(context);
			prog.setTitle(getResources().getString(R.string.progtitle_report));
			prog.setIndeterminate(false);
			prog.setMax(100);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.show();

			myProgress = 0;
		}

		protected Boolean doInBackground(Context... context) {
			myProgress = 2;
			publishProgress(myProgress);

			myProgress = 4;
			publishProgress(myProgress);

			myProgress = 10;
			publishProgress(myProgress);

			myProgress = 20;
			publishProgress(myProgress);

			Report dReport = new Report(context[0], reportId, type, reportTime);
			
			ContentResolver cr = context[0].getContentResolver();
			ContentValues cv = new ContentValues();
			String sc = Reports.KEY_REPORT_ID + " = '" + reportId + "'";
			cv.put(Reports.KEY_DELETE_REPORT, 1);
			cv.put(Reports.KEY_REPORT_VERSION, -1);
			int nUpdated = cr.update(Util.getReportsUri(context[0]), cv, sc, null);
			Util.logInfo(TAG, "n updated: " + nUpdated);
			Uri repUri = Util.getReportsUri(context[0]);
			cr.insert(repUri, ContProvValuesReports.createReport(dReport));

			myProgress = 50;
			publishProgress(myProgress);

			if (!Util.privateMode()) {

				// now test if there is a data connection
				if (!Util.isOnline(context[0])) {
					resultFlag = OFFLINE;
					return false;
				}
				if (!PropertyHolder.isRegistered())
					Util.registerOnServer(context[0]);

				Util.logInfo(TAG, sc);

				int uploadResult = dReport.upload(context[0]);

				if (uploadResult != Report.UPLOADED_ALL) {
					myProgress = 90;
					publishProgress(myProgress);
					startService(new Intent(context[0], SyncData.class));
					resultFlag = UPLOAD_ERROR;
				} else {
					int nDeleted = cr.delete(Util.getReportsUri(context[0]), sc, null);
					Util.logInfo(TAG, "n deleted: " + nDeleted);
					resultFlag = SUCCESS;

					myProgress = 100;
					publishProgress(myProgress);
				}
			} else {
				myProgress = 100;
				publishProgress(myProgress);

				resultFlag = PRIVATE_MODE;
			}
			return true;
		}

		protected void onProgressUpdate(Integer... progress) {
			prog.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {

			prog.dismiss();

			if ((result && resultFlag == SUCCESS) || resultFlag == PRIVATE_MODE) {
				Util.toast(context, getResources().getString(R.string.report_deleted));
				finish();
			} else {
				if (resultFlag == OFFLINE) {
					buildDeletionAlert(getResources().getString(R.string.deletion_no_network));
				}
				if (resultFlag == UPLOAD_ERROR) {
					buildDeletionAlert(getResources().getString(R.string.deletion_network_error));
				}
			}

		}
	}

	public void buildDeletionAlert(String message) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_alert);
		dialog.setCancelable(false);

		TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
		alertText.setText(message);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button negative = (Button) dialog.findViewById(R.id.alertCancel);
		negative.setVisibility(View.GONE);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				finish();
			}
		});

		dialog.show();
	}
	
}
