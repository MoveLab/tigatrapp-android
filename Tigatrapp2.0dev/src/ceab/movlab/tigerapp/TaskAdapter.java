package ceab.movlab.tigerapp;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class TaskAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	TaskItemModel tempValues = null;
	int i = 0;

	/************* CustomAdapter Constructor *****************/
	public TaskAdapter(Activity a, ArrayList d, Resources resLocal) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		res = resLocal;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/******** What is the size of Passed Arraylist Size ************/
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public TextView itemText;
		public ImageView helpIcon;
		public Spinner itemChoices;

		public String itemId;

	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.task_item, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.itemText = (TextView) vi.findViewById(R.id.itemText);
			holder.helpIcon = (ImageView) vi.findViewById(R.id.helpIcon);
			holder.itemChoices = (Spinner) vi.findViewById(R.id.itemChoices);

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {
			holder.itemText.setVisibility(View.GONE);
			holder.helpIcon.setVisibility(View.GONE);
			holder.itemChoices.setVisibility(View.GONE);

		} else {
			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			tempValues = (TaskItemModel) data.get(position);

			/************ Set Model values in Holder elements ***********/

			Log.i("ITEMTEXT", tempValues.getItemText());

			holder.itemText.setText(tempValues.getItemText());
			holder.itemId = tempValues.getItemId();

			if (tempValues.getItemHelp() == null)
				holder.helpIcon.setVisibility(View.GONE);

			ArrayAdapter<String> choicesAdapter = new ArrayAdapter<String>(
					activity, android.R.layout.simple_spinner_item,
					tempValues.getItemChoices());
			choicesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			holder.itemId = tempValues.getItemId();
			holder.itemChoices.setAdapter(choicesAdapter);

			Log.d("TAd1", holder.itemId);
			Log.d("TAd2", holder.itemChoices.toString());

			int responsePos = tempValues.getItemResponsePosition();
			Log.d("TAd3", "" + responsePos);

			if (responsePos >= 0) {
				holder.itemChoices.setSelection(responsePos);
				Log.d("TAd4", "" + holder.itemChoices.getSelectedItemPosition());

			}

			holder.itemChoices
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

							try {

								JSONObject thisResponse = new JSONObject();

								thisResponse.put(TaskItemModel.KEY_ITEM_ID,
										holder.itemId);

								thisResponse.put(TaskItemModel.KEY_ITEM_TEXT,
										holder.itemText.getText());

								Log.i("Holder ITEMTEXT",
										holder.itemText.getText().toString());

								thisResponse.put(
										TaskItemModel.KEY_ITEM_RESPONSE, String
												.valueOf(holder.itemChoices
														.getSelectedItem()));

								TaskActivity ta = (TaskActivity) activity;
								ta.responses.put(holder.itemId, thisResponse);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});

			/******** Set Item Click Listner for LayoutInflater for each row *******/

			vi.setOnClickListener(new OnItemClickListener(position));
		}
		return vi;
	}

	@Override
	public void onClick(View v) {
		Log.v("CustomAdapter", "=====Row button clicked=====");
	}

	/********* Called when Item click in ListView ************/
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {

			TaskItemModel thisTask = (TaskItemModel) data.get(mPosition);

			if (thisTask.getItemHelpImage() != -1) {
				Util.showHelp(activity, thisTask.getItemHelp(),
						thisTask.getItemHelpImage());
			} else
				Util.showHelp(activity, thisTask.getItemHelp());

			return;
		}
	}
}