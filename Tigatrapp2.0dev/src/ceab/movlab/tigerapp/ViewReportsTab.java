/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: tigatrapp@ceab.csic.es
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

package ceab.movlab.tigerapp;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;

public class ViewReportsTab extends TabActivity {

	Context context = this;
	String reportId;
	String title;
	int type;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(ViewReportsTab.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			Resources res = getResources();
			Util.setDisplayLanguage(res);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_reports_tab);
		Resources resources = getResources();

		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();

		reportId = b.getString(Reports.KEY_REPORT_ID);
		type = b.getInt(Reports.KEY_TYPE);
		title = b.getString("title");

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

										Util.toast(
												context,
												getResources()
														.getString(
																R.string.report_deleted));

										ContentResolver cr = context
												.getContentResolver();
										ContentValues cv = new ContentValues();
										String sc = Reports.KEY_REPORT_ID
												+ " = '" + reportId + "'";
										cv.put(Reports.KEY_DELETE_REPORT, 1);
										cr.update(Reports.CONTENT_URI, cv, sc,
												null);
										// TODO sync deletion to server
										d.dismiss();
										finish();
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
						Intent i = new Intent(context, ReportTool.class);

						i.putExtra("type", type);
						i.putExtra("reportId", reportId);
						context.startActivity(i);
						finish();
					};
				});

		((Button) findViewById(R.id.buttonCancel))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					};
				});

		TabHost tabHost = getTabHost();

		// checklist tab
		Intent intentChecklist = new Intent().setClass(this,
				ViewReportsChecklistTab.class);
		if (b.containsKey(Reports.KEY_CONFIRMATION))
			intentChecklist.putExtra(Reports.KEY_CONFIRMATION,
					b.getString(Reports.KEY_CONFIRMATION));
		TabSpec tabSpecChecklist = tabHost
				.newTabSpec(
						getResources().getString(R.string.checklist_tab_title))
				.setIndicator(
						"",
						resources
								.getDrawable(R.drawable.view_reports_checklist_icon))
				.setContent(intentChecklist);

		// photos tab
		Intent intentPhotos = new Intent().setClass(this,
				ViewReportsPhotosTab.class);
		if (b.containsKey(Reports.KEY_PHOTO_URIS))
			intentPhotos.putExtra(Reports.KEY_PHOTO_URIS,
					b.getString(Reports.KEY_PHOTO_URIS));
		TabSpec tabSpecPhotos = tabHost
				.newTabSpec(getResources().getString(R.string.photos_tab_title))
				.setIndicator(
						"",
						resources
								.getDrawable(R.drawable.view_reports_photos_icon))
				.setContent(intentPhotos);

		// Notes tab
		Intent intentNotes = new Intent().setClass(this,
				ViewReportsNotesTab.class);
		if (b.containsKey(Reports.KEY_NOTE))
			intentNotes.putExtra(Reports.KEY_NOTE,
					b.getString(Reports.KEY_NOTE));
		TabSpec tabSpecNotes = tabHost
				.newTabSpec(getResources().getString(R.string.notes_tab_title))
				.setIndicator(
						"",
						resources
								.getDrawable(R.drawable.view_reports_notes_icon))
				.setContent(intentNotes);

		// add all tabs
		tabHost.addTab(tabSpecChecklist);
		tabHost.addTab(tabSpecPhotos);
		tabHost.addTab(tabSpecNotes);

		// set Windows tab as default (zero based)
		tabHost.setCurrentTab(0);
	}

}