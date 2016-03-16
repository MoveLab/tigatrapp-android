package ceab.movelab.tigabib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.ContProvContractReports.Reports;

public class AttachedPhotos extends Activity {

	private static String TAG = "AttachedPhotos";
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
	String lang;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

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

						final String thisPhotoUri = Report.getPhotoUri(context,
								jsonPhotos, position);
						final int pos = position;

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
								
								LinearLayout button_area = (LinearLayout) dialog.findViewById(R.id.photo_button_area);
								button_area.setVisibility(View.VISIBLE);
								Button positive = (Button) dialog.findViewById(R.id.share);
								Button negative = (Button) dialog.findViewById(R.id.alertCancel);

								positive.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										Uri imageUri = Uri.fromFile(new File(thisPhotoUri)); 		
										Intent shareIntent = new Intent();
										shareIntent.setAction(Intent.ACTION_SEND);
										shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
										shareIntent.setType("image/*");
										// add a subject
//										shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//												"Tigatrapp");

										// build the body of the message to be shared
										String shareMessage = getResources().getString(
												R.string.photo_share_message);

										// add the message
										shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
												shareMessage);
										

										// start the chooser for sharing
										startActivity(Intent.createChooser(shareIntent, getResources()
												.getText(R.string.share_with)));

										

									}
								});

								negative.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {

										jsonPhotos = Report.deletePhoto(
												context, jsonPhotos, pos);
										// I realize this is ugly, but it is the
										// quickest fix right now to get the
										// grid
										// updated...
										adapter = new PhotoGridAdapter(context,
												jsonPhotos);
										gridview.setAdapter(adapter);

										dialog.dismiss();

									}
								});
								
								dialog.setCancelable(true);
								dialog.show();
							} catch (FileNotFoundException e) {
								Util.logError(context, TAG, "error: " + e);
							} catch (IOException e) {
								Util.logError(context, TAG, "error: " + e);
							}
						}

					}
				});

				gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View v, int position, long id) {

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
												context, jsonPhotos, pos);
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

				
				Intent intent = new Intent();
				// Show only images, no videos or anything else
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
				// Always show the chooser (if there are multiple options
				// available)
				startActivityForResult(Intent.createChooser(
						intent,
					getResources().getString(
						R.string.photo_selector_attach_photo_button)),
						ReportTool.REQUEST_CODE_GET_PHOTO_FROM_GALLERY);

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
		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
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
		if (jsonPhotos != null)
			icicle.putString(Reports.KEY_PHOTO_URIS, jsonPhotos.toString());

		if (photoUri != null)
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
			Util.logError(context, TAG, "photo exception: " + e);
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
		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

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
					Util.logError(context, TAG, "error: " + e);
				}

				// I realize this is ugly, but it is the
				// quickest fix right now to get the grid
				// updated...
				adapter = new PhotoGridAdapter(context, jsonPhotos);
				gridview.setAdapter(adapter);

			}
			break;

		}
		case (ReportTool.REQUEST_CODE_GET_PHOTO_FROM_GALLERY): {
			
		// this is ugly, but a quick safety to avoid crashes
		try{
			if(data != null){
				Uri this_data = data.getData();
				if(this_data != null){
			
			String realPath;
			// SDK < API11
			if (Build.VERSION.SDK_INT < 11)
				realPath = Util.getRealPathFromURI_BelowAPI11(this,
						data.getData());

			// SDK >= 11 && SDK < 19
			else if (Build.VERSION.SDK_INT < 19)
				realPath = RealPathFromURI_API11to18
						.getRealPathFromURI_API11to18(this, data.getData());

			// SDK >= 19 (Android 4.4)
			else
				realPath = RealPathFromURI_API19.getRealPathFromURI_API19(this,
						data.getData());

			Uri this_uri = null;
			if (realPath != null && realPath != "") {
				this_uri = Uri.fromFile(new File(realPath));
			}

			if (resultCode == RESULT_OK && this_uri != null) {

				JSONObject newPhoto = new JSONObject();
				try {
					newPhoto.put(Report.KEY_PHOTO_URI, this_uri.getPath());
					newPhoto.put(Report.KEY_PHOTO_TIME,
							System.currentTimeMillis());
					jsonPhotos.put(newPhoto);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

				// I realize this is ugly, but it is the
				// quickest fix right now to get the grid
				// updated...
				adapter = new PhotoGridAdapter(context, jsonPhotos);
				gridview.setAdapter(adapter);
			}
			}
			}
			
		} catch (Exception e){
			// do nothing for now... TODO
		}
			break;

		}

		}

	}
	
	


}
