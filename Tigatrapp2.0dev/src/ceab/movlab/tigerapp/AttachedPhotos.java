package ceab.movlab.tigerapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import ceab.movelab.tigerapp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

public class AttachedPhotos extends Activity {

	Context context = this;
	ArrayList<String> thesePhotos;
	File root;
	File directory;
	String photoFileName = "";
	PhotoGridAdapter adapter;
	Report tempReport;
	Uri photoUri;
	GridView gridview;

	JSONArray jsonPhotos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.attached_photos);

		root = Environment.getExternalStorageDirectory();
		directory = new File(root, getResources().getString(
				R.string.app_directory));
		directory.mkdirs();

		Intent incoming = getIntent();

		tempReport = new Report(
				incoming.getStringExtra(ReportTool.EXTRA_REPORT_ID),
				incoming.getIntExtra(ReportTool.EXTRA_REPORT_VERSION,
						Report.MISSING));

		tempReport.reassemblePhotos(
				incoming.getStringArrayExtra(ReportTool.EXTRA_PHOTO_URI_ARRAY),
				incoming.getLongArrayExtra(ReportTool.EXTRA_PHOTO_TIME_ARRAY));

		if (savedInstanceState != null) {
			tempReport.reportId = savedInstanceState.getString("reportId");
			tempReport.confirmation = savedInstanceState
					.getString("confirmation");
			tempReport.locationChoice = savedInstanceState
					.getInt("locationChoice");
			tempReport.currentLocationLat = savedInstanceState
					.getFloat("currentLocationLat");
			tempReport.currentLocationLon = savedInstanceState
					.getFloat("currentLocationLon");
			tempReport.selectedLocationLat = savedInstanceState
					.getFloat("selectedLocationLat");
			tempReport.selectedLocationLon = savedInstanceState
					.getFloat("selectedLocationLon");
			tempReport.photoAttached = savedInstanceState
					.getInt("photoAttached");
			tempReport.note = savedInstanceState.getString("note");
			tempReport.mailing = savedInstanceState.getInt("mailing");
			tempReport.reassemblePhotos(savedInstanceState
					.getStringArray(ReportTool.EXTRA_PHOTO_URI_ARRAY),
					savedInstanceState
							.getLongArray(ReportTool.EXTRA_PHOTO_TIME_ARRAY));

			photoUri = Uri.fromFile(new File(savedInstanceState
					.getString("photoUri")));

		}

		thesePhotos = new ArrayList<String>(Arrays.asList(tempReport
				.photoUris2Array()));

		jsonPhotos = Util.StringArrayList2JsonArray(thesePhotos);

		adapter = new PhotoGridAdapter(context, jsonPhotos);

		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				if (jsonPhotos.length() > 0) {
					try {
						final Dialog dialog = new Dialog(context);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.photo_view);
						ImageView iv = (ImageView) dialog
								.findViewById(R.id.photoView);
						//TODO find better way of choosing max pixel size  -- based on screen
						iv.setImageBitmap(Util.getSmallerBitmap(new File(jsonPhotos.getString(position)),
								context, 300));
						iv.setOnClickListener(new View.OnClickListener() {
							public void onClick(View View3) {
								dialog.dismiss();
							}
						});
						dialog.setCanceledOnTouchOutside(true);
						dialog.setCancelable(true);
						dialog.show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {

				final String item = (String) parent.getItemAtPosition(position);
				final int pos = position;
				final AlertDialog.Builder dialog = new AlertDialog.Builder(
						context);
				dialog.setTitle("Remove Attachment");
				dialog.setMessage("Remove this photo" + item
						+ " from this report?");
				dialog.setCancelable(true);
				dialog.setPositiveButton("Remove",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								thesePhotos.remove(pos);
								jsonPhotos = Util
										.StringArrayList2JsonArray(thesePhotos);
								// I realize this is ugly, but it is the
								// quickest fix right now to get the grid
								// updated...

								adapter = new PhotoGridAdapter(context,
										jsonPhotos);
								gridview.setAdapter(adapter);
								tempReport.photos.remove(pos);

								d.dismiss();
							}

						});

				dialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								d.cancel();
							};
						});

				dialog.show();

				return true;
			}
		});

		Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);

		Button attachFileButton = (Button) findViewById(R.id.attachFileButton);

		Button okButton = (Button) findViewById(R.id.okButton);

		Button cancelButton = (Button) findViewById(R.id.cancelButton);

		attachFileButton.setOnClickListener(new OnClickListener() {
			String m_chosen;

			@Override
			public void onClick(View v) {
				// ///////////////////////////////////////////////////////////////////////////////////////////////
				// Create FileOpenDialog and register a callback
				// ///////////////////////////////////////////////////////////////////////////////////////////////
				SimpleFileDialog FileOpenDialog = new SimpleFileDialog(
						AttachedPhotos.this, "FileOpen",
						new SimpleFileDialog.SimpleFileDialogListener() {
							@Override
							public void onChosenDir(String chosenDir) {
								// The code in this function will be executed
								// when the dialog OK button is pushed
								m_chosen = chosenDir;
								thesePhotos.add(m_chosen);
								jsonPhotos = Util
										.StringArrayList2JsonArray(thesePhotos);
								// I realize this is ugly, but it is the
								// quickest fix right now to get the grid
								// updated...
								adapter = new PhotoGridAdapter(context,
										jsonPhotos);
								gridview.setAdapter(adapter);
								tempReport.photos.add(new Photo(
										tempReport.reportId,
										tempReport.reportVersion, m_chosen,
										Report.MISSING, Report.NO,
										Report.MISSING, Report.NO));

							}

						});

				// You can change the default filename using the public variable
				// "Default_File_Name"
				FileOpenDialog.Default_File_Name = "";
				FileOpenDialog.chooseFile_or_Dir();

				// ///////////////////////////////////////////////////////////////////////////////////////////////

			}
		});

		takePhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isIntentAvailable(context, MediaStore.ACTION_IMAGE_CAPTURE)) {
					dispatchTakePictureIntent(ReportTool.REQUEST_CODE_TAKE_PHOTO);
				}

			};
		});

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent dataForReport = new Intent();

				dataForReport.putExtra(ReportTool.EXTRA_PHOTO_URI_ARRAY,
						tempReport.photoUris2Array());
				dataForReport.putExtra(ReportTool.EXTRA_PHOTO_TIME_ARRAY,
						tempReport.photoTimes2Array());
				setResult(RESULT_OK, dataForReport);
				finish();
			};
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			};
		});

	}

	@Override
	protected void onPause() {
		int count = gridview.getCount();
		for (int i = 0; i < count; i++) {
			ImageView v = (ImageView) gridview.getChildAt(i);
			if (v != null) {
				if (v.getDrawable() != null)
					v.getDrawable().setCallback(null);
			}
		}
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putString("reportId", tempReport.reportId);
		icicle.putString("confirmation", tempReport.confirmation);
		icicle.putInt("locationChoice", tempReport.locationChoice);

		if (tempReport.selectedLocationLat != null)
			icicle.putFloat("selectedLocationLat",
					tempReport.selectedLocationLat);

		if (tempReport.selectedLocationLon != null)
			icicle.putFloat("selectedLocationLon",
					tempReport.selectedLocationLon);

		if (tempReport.currentLocationLat != null)
			icicle.putFloat("currentLocationLat", tempReport.currentLocationLat);

		if (tempReport.currentLocationLon != null)
			icicle.putFloat("currentLocationLon", tempReport.currentLocationLon);

		icicle.putInt("photoAttached", tempReport.photoAttached);
		icicle.putString("note", tempReport.note);
		icicle.putInt("mailing", tempReport.mailing);

		icicle.putStringArray(ReportTool.EXTRA_PHOTO_URI_ARRAY,
				tempReport.photoUris2Array());
		icicle.putLongArray(ReportTool.EXTRA_PHOTO_TIME_ARRAY,
				tempReport.photoTimes2Array());
		icicle.putString("photoUri", photoUri.getPath());

	}

	private void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String stringDate = dateFormat.format(date);
			String photoFileName = getResources().getString(
					R.string.saved_image_prefix)
					+ stringDate + ".jpg";

			photoUri = Uri.fromFile(new File(directory, photoFileName));
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

		} catch (Exception e) {
			Log.e("ReportTool", "photo exception: " + e);
		}

		startActivityForResult(takePictureIntent, actionCode);

	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case (ReportTool.REQUEST_CODE_TAKE_PHOTO): {

			if (resultCode == RESULT_OK && photoUri != null) {

				thesePhotos.add(photoUri.getPath());
				jsonPhotos = Util.StringArrayList2JsonArray(thesePhotos);

				// I realize this is ugly, but it is the
				// quickest fix right now to get the grid
				// updated...
				adapter = new PhotoGridAdapter(context, jsonPhotos);
				gridview.setAdapter(adapter);

				tempReport.photos.add(new Photo(tempReport.reportId,
						tempReport.reportVersion, photoUri.getPath(), System
								.currentTimeMillis(), Report.NO,
						Report.MISSING, Report.NO));

			}
			break;

		}

		}

	}
}
