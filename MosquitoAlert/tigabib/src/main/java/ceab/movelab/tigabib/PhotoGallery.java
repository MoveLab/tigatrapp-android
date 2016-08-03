package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;

import ceab.movelab.tigabib.adapters.ImageAdapter;

public class PhotoGallery extends Activity {
	private Gallery galleryView;
	private TextView captionView;
	Resources res;
	String lang;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this;

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

		setContentView(R.layout.tiger_photos);

		this.setTitle(context.getResources().getString(
				R.string.activity_label_gallery));

		final String[] captions = getResources().getStringArray(
				R.array.gallery_array);

		galleryView = (Gallery) findViewById(R.id.galleryid);
		captionView = (TextView) findViewById(R.id.captionid);
		captionView.setText(Html.fromHtml(captions[0]));
		ImageAdapter adapter = new ImageAdapter(this, captions);
		galleryView.setAdapter(adapter);
		galleryView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				captionView.setText(Html.fromHtml(captions[arg2]));

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	protected void onResume() {
		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.consent_menu, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {

			Intent i = new Intent(PhotoGallery.this, LanguageSelector.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}