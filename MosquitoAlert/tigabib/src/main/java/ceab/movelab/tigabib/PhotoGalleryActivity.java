package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;

import ceab.movelab.tigabib.adapters.ImageAdapter;

public class PhotoGalleryActivity extends Activity {

	private Gallery galleryView;
	private TextView captionView;
	private String lang;

	//private FirebaseAnalytics mFirebaseAnalytics;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.tiger_photos);

		this.setTitle(getResources().getString(R.string.activity_label_gallery));

		final String[] captions = getResources().getStringArray(R.array.gallery_array);

		galleryView = (Gallery) findViewById(R.id.galleryid);
		captionView = (TextView) findViewById(R.id.captionid);
		captionView.setText(Html.fromHtml(captions[0]));
		ImageAdapter adapter = new ImageAdapter(this, captions);
		galleryView.setAdapter(adapter);
		galleryView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				captionView.setText(Html.fromHtml(captions[arg2]));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		//mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		// [START set_current_screen]
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_photo_gallery", "Photo Gallery");
		// [END set_current_screen]
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.consent_menu, menu);
		Util.setMenuTextColor(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {
			Intent i = new Intent(PhotoGalleryActivity.this, LanguageSelectorActivity.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}