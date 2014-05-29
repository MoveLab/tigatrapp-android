package ceab.movlab.tigerapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;

public class ViewResponsesAdapter extends ArrayAdapter<TaskItemModel> {
	private Activity myContext;
	private ArrayList<TaskItemModel> data;
	int resourceId;

	public ViewResponsesAdapter(Context context, int textViewResourceId,
			ArrayList<TaskItemModel> data) {
		super(context, textViewResourceId, data);
		// TODO Auto-generated constructor stub
		myContext = (Activity) context;
		this.data = data;
		resourceId = textViewResourceId;

	}

	static class ViewHolder {
		TextView itemQuestion;
		TextView itemResponse;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = myContext.getLayoutInflater();
			convertView = inflater.inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.itemQuestion = (TextView) convertView
					.findViewById(R.id.itemQuestion);
			viewHolder.itemResponse = (TextView) convertView
					.findViewById(R.id.itemResponse);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		
		viewHolder.itemQuestion.setText(data.get(position).getItemText());
		viewHolder.itemResponse.setText(data.get(position).getItemResponse());

		return convertView;
	}

}
