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

package ceab.movlab.tigerapp;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Overlay for map view
 * 
 * @author John R.B. Palmer
 * 
 */
public class MyItemizedOverlay extends ItemizedOverlay {
	Context context;
	int thisItem;

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;

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
		
		Intent i = new Intent(context, ViewReportsTab.class);
		if (item.photoUris != null) {
		i.putExtra(Reports.KEY_PHOTO_URIS, item.photoUris);
		}
		if (item.responses != null) {
			i.putExtra(Reports.KEY_CONFIRMATION, item.responses);
			}
			
		
		if(item.reportId != null){
			i.putExtra(Reports.KEY_REPORT_ID, item.reportId);	
		}
		if(item.type != Report.MISSING){
			i.putExtra(Reports.KEY_TYPE, item.type);	
		}
		if(item.getSnippet() != null){
			i.putExtra(Reports.KEY_NOTE, item.getSnippet());	
		}
		if(item.getTitle() != null){
			i.putExtra("title", item.getTitle());	
		}
		

		context.startActivity(i);

			return true;
	}

	public void addOverlay(MyOverlayItem overlay) {
		mOverlays.add(overlay);

	}

	public void populateNow() {
		populate();
	}

}
