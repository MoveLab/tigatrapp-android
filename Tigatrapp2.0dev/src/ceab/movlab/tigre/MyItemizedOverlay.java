/*
 * Space Mapper
 * Copyright (C) 2012 John R.B. Palmer
 * Contact: jrpalmer@princeton.edu
 * 
 * This file is part of Space Mapper.
 * 
 * Space Mapper is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Space Mapper is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 */

package ceab.movlab.tigre;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Overlay for map view
 * 
 * @author John R.B. Palmer
 * 
 */
public class MyItemizedOverlay extends ItemizedOverlay {
	Context mContext;
	int thisItem;

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;

	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		thisItem = index;
		final MyOverlayItem item = mOverlays.get(index);
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.map_item_info);
		Util.overrideFonts(mContext, dialog.findViewById(android.R.id.content));

		JSONArray jsonPhotosTry = new JSONArray();
		if (item.photoUris != null) {
			try {
				jsonPhotosTry = new JSONArray(item.photoUris);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		final JSONArray jsonPhotos = jsonPhotosTry;

		GridView gridview = (GridView) dialog.findViewById(R.id.gridview);
		gridview.setAdapter(new PhotoGridAdapter(mContext, jsonPhotos));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Uri thisUri;
				if (jsonPhotos.length() > 0) {
					try {
						thisUri = Uri.parse(jsonPhotos.getString(position));
						final Dialog dialog = new Dialog(mContext);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.photo_view);
						ImageView iv = (ImageView) dialog
								.findViewById(R.id.photoView);
						//TODO find better way of choosing max pixel size  -- based on screen
						iv.setImageBitmap(Util.getSmallerBitmap(new File(jsonPhotos.getString(position)),
								mContext, 300));
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

		dialog.setCancelable(true);

		((TextView) dialog.findViewById(R.id.title)).setText(item.getTitle());
		;
		((TextView) dialog.findViewById(R.id.noteText)).setText(item
				.getSnippet());
		;

		((Button) dialog.findViewById(R.id.buttonDelete))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String thisReportId = item.getReportId();

						mOverlays.remove(thisItem);
						ViewDataActivity.mapView.invalidate();
						Util.toast(mContext, "Report deleted.");

						ContentResolver cr = mContext.getContentResolver();
						ContentValues cv = new ContentValues();
						String sc = Reports.KEY_REPORT_ID + " = '"
								+ thisReportId + "'";
						cv.put(Reports.KEY_DELETE_REPORT, 1);
						cr.update(Reports.CONTENT_URI, cv, sc, null);

						// TODO sync deletion to server

						setLastFocusedIndex(-1);
						populate();
						dialog.dismiss();

					};
				});

		((Button) dialog.findViewById(R.id.buttonEdit))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, ReportTool.class);
						;
						i.putExtra("type", item.getType());
						i.putExtra("reportId", item.getReportId());
						mContext.startActivity(i);
						dialog.dismiss();
					};
				});

		((Button) dialog.findViewById(R.id.buttonCancel))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					};
				});
		dialog.show();
		return true;
	}

	public void addOverlay(MyOverlayItem overlay) {
		mOverlays.add(overlay);

	}

	public void populateNow() {
		populate();
	}


}
