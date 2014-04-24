package ceab.movlab.tigerapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
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
import ceab.movelab.tigerapp.R;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;

public class AttachedPhotos extends Activity {

	Context context = this;
	File root;
	File directory;
	String photoFileName = "";
	PhotoGridAdapter adapter;
	Uri photoUri;
	GridView gridview;

	JSONArray jsonPhotos;
	String jsonPhotosString;

	Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);
		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(AttachedPhotos.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			res = getResources();
			Util.setDisplayLanguage(res);
		}

		setContentView(R.layout.attached_photos);

		root = Environment.getExternalStorageDirectory();
		directory = new File(root, getResources().getString(
				R.string.app_directory));
		directory.mkdirs();

		Intent incoming = getIntent();

		if (incoming.hasExtra(Reports.KEY_PHOTO_URIS))
			jsonPhotosString = incoming.getStringExtra(Reports.KEY_PHOTO_URIS);

		if (savedInstanceState != null) {
			jsonPhotosString = savedInstanceState
					.getString(Reports.KEY_PHOTO_URIS);
			photoUri = Uri.fromFile(new File(savedInstanceState
					.getString("photoUri")));
		}

		if (jsonPhotosString != null) {
			try {
				jsonPhotos = new JSONArray(jsonPhotosString);
				adapter = new PhotoGridAdapter(context, jsonPhotos);

				gridview = (GridView) findViewById(R.id.gridview);
				gridview.setAdapter(adapter);

				gridview.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {

						String thisPhotoUri = Report.getPhotoUri(jsonPhotos,
								position);
						if (thisPhotoUri != null) {

							try {
								final Dialog dialog = new Dialog(context);
								dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
								dialog.setContentView(R.layout.photo_view);
								ImageView iv = (ImageView) dialog
										.findViewById(R.id.photoView);
								// TODO find better way of choosing max pixel
								// size -- based on screen
								iv.setImageBitmap(Util.getSmallerBitmap(
										new File(thisPhotoUri), context, 300));
								iv.setOnClickListener(new View.OnClickListener() {
									public void onClick(View View3) {
										dialog.dismiss();
									}
								});
								dialog.setCanceledOnTouchOutside(true);
								dialog.setCancelable(true);
								dialog.show();
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
					public boolean onItemLongClick(AdapterView<?> parent,
							View v, int position, long id) {

						Log.d("AP2", "pos: " + position);
						Log.d("AP2", "jsonphotoslength: " + jsonPhotos.length());

						final String item = (String) parent
								.getItemAtPosition(position);
						final int pos = position;
						final AlertDialog.Builder dialog = new AlertDialog.Builder(
								context);
						dialog.setTitle(getResources().getString(
								R.string.photo_selector_remove_attachment));
						dialog.setMessage(getResources().getString(
								R.string.photo_selector_remove_this_photo)
								+ item
								+ " "
								+ getResources()
										.getString(
												R.string.photo_selector_from_thisreport));
						dialog.setCancelable(true);
						dialog.setPositiveButton(
								getResources()
										.getString(
												R.string.photo_selector_remove_button_label),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d,
											int arg1) {

										jsonPhotos = Report.deletePhoto(
												jsonPhotos, pos);
										// I realize this is ugly, but it is the
										// quickest fix right now to get the
										// grid
										// updated...
										adapter = new PhotoGridAdapter(context,
												jsonPhotos);
										gridview.setAdapter(adapter);

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

						return true;
					}
				});

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

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

								JSONObject newPhoto = new JSONObject();
								try {
									newPhoto.put(Report.KEY_PHOTO_URI, m_chosen);
									jsonPhotos.put(newPhoto);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								// I realize this is ugly, but it is the
								// quickest fix right now to get the grid
								// updated...
								adapter = new PhotoGridAdapter(context,
										jsonPhotos);
								gridview.setAdapter(adapter);

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

				if (jsonPhotos != null && jsonPhotos.length() > 0)
					dataForReport.putExtra(Reports.KEY_PHOTO_URIS,
							jsonPhotos.toString());
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
	protected void onResume() {
		super.onResume();
		res = getResources();
		Util.setDisplayLanguage(res);
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
		icicle.putString(Reports.KEY_PHOTO_URIS, jsonPhotos.toString());
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

				JSONObject newPhoto = new JSONObject();
				try {
					newPhoto.put(Report.KEY_PHOTO_URI, photoUri.getPath());
					newPhoto.put(Report.KEY_PHOTO_TIME,
							System.currentTimeMillis());
					jsonPhotos.put(newPhoto);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// I realize this is ugly, but it is the
				// quickest fix right now to get the grid
				// updated...
				adapter = new PhotoGridAdapter(context, jsonPhotos);
				gridview.setAdapter(adapter);

			}
			break;

		}

		}

	}
}
