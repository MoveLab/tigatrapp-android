package ceab.movelab.tigabib;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	String reportId;
	int type;
	String photoUris;
	String responses;
	long reportTime;
	
	public MyOverlayItem(GeoPoint location, String title, String snippet,
						 String _reportId, int _type, String photoUris, String responses, long reportTime) {
		super(location, title, snippet);
		this.reportId = _reportId;
		this.type = _type;
		this.photoUris = photoUris;
		this.responses = responses;
		this.reportTime = reportTime;
	}


	public String getReportId(){
		return reportId;
	}
	
	public long getReportTime(){
		return reportTime;
	}
	
	
	public int getType(){
		return type;
	}
	
	
	public String getPhotoUris(){
		return photoUris;
	}
	
	
}
