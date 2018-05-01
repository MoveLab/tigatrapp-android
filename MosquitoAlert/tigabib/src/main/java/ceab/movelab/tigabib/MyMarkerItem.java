package ceab.movelab.tigabib;

import com.google.android.gms.maps.model.LatLng;

public class MyMarkerItem {

	private LatLng position;
	private String title;
	private String snippet;
	private String reportId;
	private int type;
	private String photoUris;
	private String responses;
	private long reportTime;

	public MyMarkerItem(LatLng position, String title, String snippet, String reportId, int type,
						String photoUris, String responses, long reportTime) {
		this.position = position;
		this.title = title;
		this.snippet = snippet;
		this.reportId = reportId;
		this.type = type;
		this.photoUris = photoUris;
		this.responses = responses;
		this.reportTime = reportTime;
	}

	public LatLng getPosition(){
		return position;
	}

	public String getTitle(){
		return title;
	}

	public String getSnippet(){
		return snippet;
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

	public String getResponses(){
		return responses;
	}

	public long getReportTime(){
		return reportTime;
	}
	
}
