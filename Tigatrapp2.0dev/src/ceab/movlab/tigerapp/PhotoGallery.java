package ceab.movlab.tigerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;
import ceab.movelab.tigerapp.R.array;

public class PhotoGallery extends Activity {
	private Gallery galleryView;
	private TextView captionView;
	Resources res;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this;

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(PhotoGallery.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			res = getResources();
			Util.setDisplayLanguage(res);
		}

		setContentView(R.layout.tiger_photos);

		final String[] captions = getResources().getStringArray(
				R.array.gallery_array);

		galleryView = (Gallery) findViewById(R.id.galleryid);
		captionView = (TextView) findViewById(R.id.captionid);
		captionView.setText(captions[0]);
		ImageAdapter adapter = new ImageAdapter(this, captions);
		galleryView.setAdapter(adapter);
		galleryView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				captionView.setText(captions[arg2]);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		res = getResources();
		Util.setDisplayLanguage(res);
	}

}