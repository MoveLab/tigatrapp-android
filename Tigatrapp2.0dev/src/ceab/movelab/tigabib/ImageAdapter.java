package ceab.movelab.tigabib;

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
	public Integer[] data = {R.drawable.n, R.drawable.a, R.drawable.m, R.drawable.c, R.drawable.o, R.drawable.d,
			R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
			R.drawable.i, R.drawable.k, R.drawable.j, R.drawable.l, };

	public String[] captions;

	public ImageAdapter(Activity a, String[] captions) {
		activity = a;
		this.captions = captions;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

	public static class ViewHolder {
		public ImageView image;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.image_gallery_items, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) vi.findViewById(R.id.image);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();
		final int stub_id = data[position];
		holder.image.setImageResource(stub_id);
		return vi;
	}

}
