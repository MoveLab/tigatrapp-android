package ceab.movelab.tigabib;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

public class PhotoGridAdapter extends BaseAdapter {
	private static String TAG = "PhotoGridAdapter";
	private Context mContext;
	private JSONArray jsonPhotos;
	//private Bitmap mPlaceHolderBitmap;

	public PhotoGridAdapter(Context ctx, JSONArray jsonPhotos) {
		mContext = ctx;
		this.jsonPhotos = jsonPhotos;

		//mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_action_camera);
	}

	public int getCount() {
		return jsonPhotos.length();
	}

	public String getItem(int position) {
		String result = "";
		try {
			result = jsonPhotos.getJSONObject(position).getString(Report.KEY_PHOTO_URI);
		} catch (JSONException e) {
			Util.logError(TAG, "error: " + e);
		}
		return result;
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView imageView;
		if ( convertView == null ) { // if it's not recycled, initialize some attributes
			imageView = new ImageView(mContext);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 240);
			layoutParams.gravity = Gravity.CENTER;
			imageView.setLayoutParams(layoutParams);
			//imageView.setLayoutParams(new GridView.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 240));
			imageView.setScaleType(ImageView.ScaleType.CENTER);
			//imageView.setForegroundGravity();
			//imageView.setPadding(0, 0, 0, 0);
		} else {
			imageView = (ImageView) convertView;
		}

		//String thisUri;
		if ( jsonPhotos.length() > 0 ) {
			/*try {
				thisUri = jsonPhotos.getJSONObject(position).getString(Report.KEY_PHOTO_URI);
				loadBitmap(getItem(position), imageView);
				// imageView.setImageBitmap(Util.getSmallerBitmap(new File(jsonPhotos.getString(position)), mContext, 85));
			} catch (JSONException e) {
				Util.logError(TAG, "error: " + e);
			}*/
			Picasso.get().load(new File(getItem(position)))
					.placeholder(R.drawable.ic_action_camera)
					.error(R.drawable.ic_action_camera)
					.resize(200, 240)
					.centerInside()
					.into(imageView);
		}
		return imageView;
	}

	/*private void loadBitmap(String uri, ImageView imageView) {
		if (cancelPotentialWork(uri, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(uri);
		}
	}

	private static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		private AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		private BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private static boolean cancelPotentialWork(String data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.data;

			if (bitmapData != null) {
				if (!bitmapData.equals(data)) {
					// Cancel previous task
					bitmapWorkerTask.cancel(true);
				} else {
					// The same work is already in progress
					return false;
				}
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private String data = null;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
			// return decodeSampledBitmapFromResource(mContext.getResources(),
			// data, 100, 100));
			Bitmap result = null;
			try {
				result = Util.getSmallerBitmap(new File(data), mContext, 185);
			} catch (FileNotFoundException e) {
				Util.logError(TAG, "error: " + e);
			} catch (IOException e) {
				Util.logError(TAG, "error: " + e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}*/

}
