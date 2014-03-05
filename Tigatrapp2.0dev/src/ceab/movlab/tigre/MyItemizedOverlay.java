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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setCancelable(true);
		dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface d, int arg1) {
				d.cancel();
			};	
		});
		dialog.setNeutralButton("edit", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface d, int arg1) {
				Intent i = new Intent(mContext,
						ReportTool.class);;
				i.putExtra("type", item.getType());
				i.putExtra("reportId", item.getReportId());
				mContext.startActivity(i);

			};	
		});
		
		DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String thisReportId = item.getReportId();

				mOverlays.remove(thisItem);
				ViewDataActivity.mapView.invalidate();
				Util.toast(mContext, "Report "+thisReportId+" deleted.");
				
				ContentResolver cr = mContext.getContentResolver();				
				ContentValues cv = new ContentValues();
				String sc = Reports.KEY_REPORT_ID + " = '"
						+ thisReportId + "'"; 
				cv.put(Reports.KEY_DELETE_REPORT, 1);
				cr.update(Reports.CONTENT_URI, cv, sc, null);
				
				//TODO sync deletion to server
				
				setLastFocusedIndex(-1);
				populate();
			};	
		};
		
		dialog.setPositiveButton("delete", ocl);
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
