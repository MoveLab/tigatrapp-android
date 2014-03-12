package ceab.movlab.tigerapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;

public class RSSAdapter extends ArrayAdapter<RSSPost> {
	private Activity myContext;
	private ArrayList<RSSPost> data;
	private int defaultThumb;

	public RSSAdapter(Context context, int textViewResourceId, ArrayList<RSSPost> data, int defaultThumb) {
		super(context, textViewResourceId, data);
		// TODO Auto-generated constructor stub
		myContext = (Activity) context;
		this.data = data;
		this.defaultThumb = defaultThumb;
	}

	static class ViewHolder {
		TextView postTitleView;
		TextView postDateView;
		ImageView postThumbView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = myContext.getLayoutInflater();
			convertView = inflater.inflate(R.layout.rss_item, null);
			viewHolder = new ViewHolder();
			viewHolder.postThumbView = (ImageView) convertView
					.findViewById(R.id.postThumb);
			viewHolder.postTitleView = (TextView) convertView
					.findViewById(R.id.postTitleLabel);
			viewHolder.postDateView = (TextView) convertView
					.findViewById(R.id.postDateLabel);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (data.get(position).postThumbUrl == null) {
			viewHolder.postThumbView.setImageResource(defaultThumb);
		}

		viewHolder.postTitleView.setText(data.get(position).postTitle);
		viewHolder.postDateView.setText(data.get(position).postDate);

		return convertView;
	}

}
