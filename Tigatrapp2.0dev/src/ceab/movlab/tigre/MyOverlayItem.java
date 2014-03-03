package ceab.movlab.tigre;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	String reportId;
	
	public MyOverlayItem(GeoPoint arg0, String arg1, String arg2, String _reportId) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
		reportId = _reportId;
	}


	public String getReportId(){
		return reportId;
	}
	
	
}
