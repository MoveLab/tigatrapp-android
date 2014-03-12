package ceab.movlab.tigerapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;
import ceab.movlab.tigerapp.ImageAdapter.ViewHolder;
import ceab.movelab.tigerapp.R;

public class PhotoGallery extends Activity {
	private Gallery galleryView;
	private TextView captionView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tiger_photos);
		galleryView = (Gallery) findViewById(R.id.galleryid);
		captionView = (TextView) findViewById(R.id.captionid);
		captionView.setText(ImageAdapter.captions[0]);
		ImageAdapter adapter = new ImageAdapter(this);
		galleryView.setAdapter(adapter);
		galleryView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				captionView.setText(ImageAdapter.captions[arg2]);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}
}