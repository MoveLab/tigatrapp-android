package ceab.movlab.tigre;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	String reportId;
	int type;
	
	public MyOverlayItem(GeoPoint arg0, String arg1, String arg2, String _reportId, int _type) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
		reportId = _reportId;
		type = _type;
	}


	public String getReportId(){
		return reportId;
	}
	
	public int getType(){
		return type;
	}
	
	
	
}
