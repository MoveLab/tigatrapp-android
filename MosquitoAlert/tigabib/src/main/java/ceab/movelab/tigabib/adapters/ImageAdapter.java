package ceab.movelab.tigabib.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ceab.movelab.tigabib.R;

public class ImageAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	private Integer[] data = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g,
			R.drawable.h, R.drawable.i, R.drawable.j, R.drawable.k,
			R.drawable.l, };

	//private String[] captions;

	public ImageAdapter(Activity act, String[] captions) {
		activity = act;
		//this.captions = captions;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		ImageView image;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		if ( convertView == null ) {
			vi = inflater.inflate(R.layout.image_gallery_items, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) vi.findViewById(R.id.image);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		int stub_id = data[position];
		holder.image.setImageResource(stub_id);
		return vi;
	}

}
