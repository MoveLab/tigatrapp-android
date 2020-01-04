package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ceab.movelab.tigabib.adapters.RSSAdapter;

public class RSSActivity extends Activity {
	private static final String TAG = "RSSActivity";

	private static String lang;

	private ArrayList<RSSPost> listData;
	private final int IGNORETAG = 0;
	private final int TITLE = 1;
	private final int LINK = 2;
	private final int DATE = 3;
	private RSSAdapter itemAdapter;
	private ProgressBar pb;
	private TextView title;
	private TextView tv;

	// TODO deal with RSS language
	public static final String RSSEXTRA_URL = "rssextra_url";
	public static final String RSSEXTRA_TITLE = "rssextra_title";
	public static final String RSSEXTRA_DEFAULT_THUMB = "default_thumb";

	private String thisUrl;
	private String thisTitle;
	private int thisDefaultThumb;


	//private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.activity_view_rss);
		/*getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setLogo(R.drawable.ic_launcher);*/

		Intent i = getIntent();
		thisUrl = i.getStringExtra(RSSEXTRA_URL);
		thisTitle = i.getStringExtra(RSSEXTRA_TITLE);
		thisDefaultThumb = i.getIntExtra(RSSEXTRA_DEFAULT_THUMB, R.drawable.ic_launcher);

		listData = new ArrayList<>();

		pb = (ProgressBar) this.findViewById(R.id.rssProgress);
		title = (TextView) this.findViewById(R.id.rssViewTitle);

		title.setText(thisTitle);

		tv = (TextView) this.findViewById(R.id.offlineWarning);
		ListView listView = (ListView) this.findViewById(R.id.rsslist);
		itemAdapter = new RSSAdapter(this, R.layout.rss_item, listData, thisDefaultThumb);
		listView.setAdapter(itemAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RSSPost data = listData.get(arg2);
				Uri uri = Uri.parse(data.postLink);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		// Obtain the FirebaseAnalytics instance.
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
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_news_list", "News List");
		// [END set_current_screen]

		setInfoDisplay(this, true, thisUrl);
	}

	class RssDataController extends AsyncTask<String, Integer, ArrayList<RSSPost>> {
		private int currentTag;

		@Override
		protected ArrayList<RSSPost> doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream is = null;
			ArrayList<RSSPost> RSSPostList = new ArrayList<>();
			try {
				URL url = new URL(params[0]);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setReadTimeout(10 * 1000);
				connection.setConnectTimeout(10 * 1000);
				connection.setRequestMethod("GET");
				connection.setDoInput(true);
				connection.connect();
				int response = connection.getResponseCode();
				if (response >= 300 || response < 200)
					return RSSPostList;

				is = connection.getInputStream();

				// parse xml after getting the data
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(is, "UTF-8");

				int eventType = xpp.getEventType();
				RSSPost pdData = null;
				SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
				DateFormat outputDateFomat = DateFormat.getDateTimeInstance();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						if (xpp.getName().equals("item")) {
							pdData = new RSSPost();
							currentTag = IGNORETAG;
						} else if (xpp.getName().equals("title")) {
							currentTag = TITLE;
						} else if (xpp.getName().equals("link")) {
							currentTag = LINK;
						} else if (xpp.getName().equals("pubDate")) {
							currentTag = DATE;
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if (xpp.getName().equals("item")) {
							// format the data here, otherwise format data in Adapter
							Date postDate = inputDateFormat.parse(pdData.postDate);
							pdData.postDate = outputDateFomat.format(postDate);
							RSSPostList.add(pdData);
						} else {
							currentTag = IGNORETAG;
						}
					} else if (eventType == XmlPullParser.TEXT) {
						String content = xpp.getText();
						content = content.trim();
						if (pdData != null) {
							switch (currentTag) {
								case TITLE:
									if (content.length() != 0) {
										if (pdData.postTitle != null) {
											pdData.postTitle += content;
										} else {
											pdData.postTitle = content;
										}
									}
									break;
								case LINK:
									if (content.length() != 0) {
										if (pdData.postLink != null) {
											pdData.postLink += content;
										} else {
											pdData.postLink = content;
										}
									}
									break;
								case DATE:
									if (content.length() != 0) {
										if (pdData.postDate != null) {
											pdData.postDate += content;
										} else {
											pdData.postDate = content;
										}
									}
									break;
								default:
									break;
							}
						}
					}
					eventType = xpp.next();
				}
/*			} catch (MalformedURLException e) {
				Util.logError(RSSActivity.this, TAG, "error: " + e);
			} catch (IOException e) {
				Util.logError(RSSActivity.this, TAG, "error: " + e);
			} catch (XmlPullParserException e) {
				Util.logError(RSSActivity.this, TAG, "error: " + e);
			} catch (ParseException e) {
				Util.logError(RSSActivity.this, TAG, "error: " + e);
			} catch (java.text.ParseException e) {
				Util.logError(RSSActivity.this, TAG, "error: " + e);
			}*/
			} catch (Exception e) {
				Util.logError(TAG, "error: " + e);
			}

			return RSSPostList;
		}

		@Override
		protected void onPostExecute(ArrayList<RSSPost> result) {

			if (result != null && result.size() > 0) {
				listData.clear();
				for (int i = 0; i < result.size(); i++) {
					listData.add(result.get(i));
				}
				itemAdapter.notifyDataSetChanged();
				setInfoDisplay(RSSActivity.this, false, null);
			}
		}

		public InputStream getInputStream(URL url) {
			try {
				return url.openConnection().getInputStream();
			} catch (IOException e) {
				return null;
			}
		}
	}

	static final private int REFRESH = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, REFRESH, Menu.NONE, R.string.refresh);
		Util.setMenuTextColor(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case REFRESH: {
				setInfoDisplay(this, true, thisUrl);
				return true;
			}
		}
		return false;
	}

	private void setInfoDisplay(Context context, boolean refreshData, String dataUrl) {
		if ( Util.isOnline(context) ) {
			if ( refreshData )
				new RssDataController().execute(dataUrl);
			tv.setVisibility(View.GONE);

			if (listData.size() > 0) {
				pb.setVisibility(View.GONE);
			} else {
				pb.setVisibility(View.VISIBLE);
			}
		} else {
			if (listData.size() > 0) {
				pb.setVisibility(View.GONE);
			} else {
				tv.setVisibility(View.VISIBLE);
				pb.setVisibility(View.GONE);
			}
		}
	}

}
