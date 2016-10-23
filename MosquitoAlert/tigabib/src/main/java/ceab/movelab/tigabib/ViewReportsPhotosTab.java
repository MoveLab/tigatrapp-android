package ceab.movelab.tigabib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ceab.movelab.tigabib.ContProvContractReports.Reports;

public class ViewReportsPhotosTab extends Activity {

	private static String TAG = "ViewReportsPhotosTab";
	Context context = this;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources res = getResources();
		Util.setDisplayLanguage(res);

		TextView textview = new TextView(this);
		textview.setText(getResources().getString(R.string.no_photo_attached));
		setContentView(textview);
		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();
		if (b != null && b.containsKey(Reports.KEY_PHOTO_URIS)) {
			try {
				final JSONArray jsonPhotos = new JSONArray(
						b.getString(Reports.KEY_PHOTO_URIS));

				textview.setVisibility(View.GONE);
				GridView gridview = new GridView(this);
				gridview.setColumnWidth(86);
				gridview.setHorizontalSpacing(2);
				gridview.setNumColumns(GridView.AUTO_FIT);
				gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
				gridview.setVerticalSpacing(2);
				gridview.setPadding(2, 2, 2, 2);
				setContentView(gridview);
				gridview.setAdapter(new PhotoGridAdapter(this, jsonPhotos));
				gridview.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {

						final String thisPhotoUri = Report.getPhotoUri(context, jsonPhotos, position);
						if (thisPhotoUri != null) {

							try {
								final Dialog dialog = new Dialog(context);
								dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
								dialog.setContentView(R.layout.photo_view);
								ImageView iv = (ImageView) dialog.findViewById(R.id.photoView);
								// TODO find better way of choosing max pixel
								// size --
								// based on screen
								iv.setImageBitmap(Util.getSmallerBitmap(new File(thisPhotoUri), context, 300));

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
										shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));

										// build the body of the message to be shared
										String shareMessage = getResources().getString(R.string.project_website) + " " +
												getString(R.string.app_tag);

										// add the message
										shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);

										// start the chooser for sharing
										startActivity(Intent.createChooser(shareIntent, getResources()
												.getText(R.string.share_with)));
									}
								});

								negative.setVisibility(View.GONE);;

								dialog.setCancelable(true);
								dialog.show();
							} catch (FileNotFoundException e) {
								Util.logError(TAG, "error: " + e);
							} catch (IOException e) {
								Util.logError(TAG, "error: " + e);
							}
						}

					}
				});

			} catch (JSONException e) {
				Util.logError(TAG, "error: " + e);
			}
		}

	}
}