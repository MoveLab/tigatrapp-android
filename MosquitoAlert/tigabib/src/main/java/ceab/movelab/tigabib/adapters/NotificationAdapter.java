package ceab.movelab.tigabib.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.model.Notification;

public class NotificationAdapter extends ArrayAdapter<Notification> {

	private ArrayList<Notification> mData;
	private LayoutInflater mInflater = null;

	private SimpleDateFormat dateFormat;

	public NotificationAdapter(Activity context, int textViewResourceId, ArrayList<Notification> data) {
		super(context, textViewResourceId);

		mData = data;

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public int getCount() {
		return mData.size();
	}

	public Notification getItem(int position) {
		return mData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	private class ViewHolder {
		TextView itemTitle;
		TextView itemDate;
		int id;
	}

	/****** Depends upon data size called for each row, Create each ListView row *****/
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = mInflater.inflate(R.layout.notifications_item, null);

			holder = new ViewHolder();
			holder.itemTitle = (TextView) vi.findViewById(R.id.itemTitle);
			holder.itemDate = (TextView) vi.findViewById(R.id.itemDate);

			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		Notification notification = getItem(position);

		holder.itemTitle.setText(notification.getExpertComment());
		holder.itemDate.setText(dateFormat.format(notification.getDateComment()));
		holder.id = notification.getId();

		return vi;
	}

}