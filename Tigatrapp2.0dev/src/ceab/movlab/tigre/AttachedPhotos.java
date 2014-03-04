package ceab.movlab.tigre;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class AttachedPhotos extends ListActivity {

	Context context = this;
	ArrayList<String> thesePhotos;
	File root;
	File directory;
	String photoFileName = "";
	ArrayAdapter<String> adapter;
	Report tempReport;
	Uri photoUri;

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
				incoming.getStringExtra(ReportTool.EXTRA_REPORT_ID));

		tempReport.reassemblePhotos(
				incoming.getStringArrayExtra(ReportTool.EXTRA_PHOTO_URI_ARRAY),
				incoming.getLongArrayExtra(ReportTool.EXTRA_PHOTO_TIME_ARRAY));

		thesePhotos = new ArrayList<String>(Arrays.asList(tempReport
				.photoUris2Array()));

		adapter = new ArrayAdapter<String>(this, R.layout.list_item,
				thesePhotos);

		setListAdapter(adapter);

		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							final View view, int position, long id) {
						final String item = (String) parent
								.getItemAtPosition(position);
						final int pos = position;
						final AlertDialog.Builder dialog = new AlertDialog.Builder(
								context);
						dialog.setTitle("Remove Attachment");
						dialog.setMessage("Remove " + item
								+ " from this report?");
						dialog.setCancelable(true);
						dialog.setPositiveButton("Remove",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d,
											int arg1) {
										thesePhotos.remove(pos);
										adapter.notifyDataSetChanged();
										tempReport.photos.remove(pos);

										d.dismiss();
									}

								});

						dialog.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d,
											int arg1) {
										d.cancel();
									};
								});

						dialog.show();

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
								adapter.notifyDataSetChanged();
								tempReport.photos.add(new Photo(
										tempReport.reportId, m_chosen,
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Do something when a list item is clicked
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
				adapter.notifyDataSetChanged();
				tempReport.photos.add(new Photo(tempReport.reportId, photoUri
						.getPath(), System.currentTimeMillis(), Report.NO,
						Report.MISSING, Report.NO));

			}
			break;

		}

		}

	}
}
