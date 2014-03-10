package ceab.movlab.tigre;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoGridAdapter extends BaseAdapter {
	private Context mContext;
	private JSONArray jsonPhotos;

	public PhotoGridAdapter(Context c, JSONArray jsonPhotos) {
		mContext = c;
		this.jsonPhotos = jsonPhotos;

	}

	public int getCount() {
		return jsonPhotos.length();
	}

	public Object getItem(int position) {
		String result = "";

		try {
			result = jsonPhotos.getString(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(0, 0, 0, 0);
		} else {
			imageView = (ImageView) convertView;
		}

		Uri thisUri;
		if (jsonPhotos.length() > 0) {
			try {

				imageView.setImageBitmap(Util.getSmallerBitmap(new File(jsonPhotos.getString(position)),
						mContext, 85));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return imageView;

	}

}
