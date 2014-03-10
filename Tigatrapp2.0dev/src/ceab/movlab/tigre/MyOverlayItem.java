package ceab.movlab.tigre;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	String reportId;
	int type;
	String photoUris;
	
	public MyOverlayItem(GeoPoint location, String title, String snippet, String _reportId, int _type, String photoUris) {
		super(location, title, snippet);
		// TODO Auto-generated constructor stub
		reportId = _reportId;
		type = _type;
		this.photoUris = photoUris;
	}


	public String getReportId(){
		return reportId;
	}
	
	public int getType(){
		return type;
	}
	
	
	public String getPhotoUris(){
		return photoUris;
	}
	
	
}
