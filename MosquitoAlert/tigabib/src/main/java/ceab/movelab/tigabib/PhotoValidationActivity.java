/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: info@atrapaeltigre.com
 * 
 * This file is part of Tigatrapp.
 * 
 * Tigatrapp is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Tigatrapp is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 **/

package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ceab.movelab.tigabib.model.Task;
import ceab.movelab.tigabib.model.TaskRun;
import ceab.movelab.tigabib.model.TaskRunInfo;

import static ceab.movelab.tigabib.R.id.validNotSure1;

/**
 * Displays the Pybossa photo validation system screen.
 * 
 * @author Màrius Garcia
 * 
 */
public class PhotoValidationActivity extends Activity {

	private static final String PHOTO_TOKEN = "q0n50KN2Tg1O0Zh";
	public static final String HELP_PARAM = "ceab.movelab.tigabib.help";

	String lang;
	private ViewFlipper mViewFlipper;

	private Task myTask;
	private TaskRun mTaskRun;
	private ImageView mPhoto1View, mPhoto2View, mPhoto3View, mPhoto4View;
	private TextView mYesButton_1, mNoButton_1, mNotSureButton_1, mNoneButton_2, mNotSureButton_2;
	private TextView mYesButton_3, mNoButton_3, mYesButton_4, mNoButton_4;
    private TextView mTigerButton, mYellowButton;
	private ImageView mValidHelp_1, mValidHelp_2, mValidHelp_3, mValidHelp_4;
	private ImageView mToraxImage, mAbdomenImage;
	private Boolean mIsTiger = null;
	private String mCurrentTorax = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

/*		Bundle b = getIntent().getExtras();
		if (b.containsKey(PYBOSSA_URL_PARAM))
			myUrl = b.getString(PYBOSSA_URL_PARAM);
		else
			finish();*/

		setContentView(R.layout.validation_layout);

		mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		mCurrentTorax = null;
		mIsTiger = false;

		loadNewTask();

		mValidHelp_1 = (ImageView) findViewById(R.id.validHelp1Image);
		mValidHelp_2 = (ImageView) findViewById(R.id.validHelp2Image);
		mValidHelp_3 = (ImageView) findViewById(R.id.validHelp3Image);
		mValidHelp_4 = (ImageView) findViewById(R.id.validHelp4Image);

		mPhoto1View = (ImageView) findViewById(R.id.validPhoto1Image);
		mPhoto2View = (ImageView) findViewById(R.id.validPhoto2Image);
		mPhoto3View = (ImageView) findViewById(R.id.validPhoto3Image);
		mPhoto4View = (ImageView) findViewById(R.id.validPhoto4Image);

		mYesButton_1 = (TextView) findViewById(R.id.validYes1);
		mNoButton_1 = (TextView) findViewById(R.id.validNo1);
		mNotSureButton_1 = (TextView) findViewById(validNotSure1);
		mNoneButton_2 = (TextView) findViewById(R.id.validNone2);
		mNotSureButton_2 = (TextView) findViewById(R.id.validNotSure2);
		mYesButton_3 = (TextView) findViewById(R.id.validYes3);
		mNoButton_3 = (TextView) findViewById(R.id.validNo3);
		mYesButton_4 = (TextView) findViewById(R.id.validYes4);
		mNoButton_4 = (TextView) findViewById(R.id.validNo4);

        mTigerButton = (TextView) findViewById(R.id.validMosquitoTigerButton);
        mYellowButton = (TextView) findViewById(R.id.validMosquitoYellowButton);
        mToraxImage = (ImageView) findViewById(R.id.validToraxImage);
        mAbdomenImage = (ImageView) findViewById(R.id.validAbdomenImage);

		setOnClickListeners();
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		if ( !Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			//startActivity(getIntent());
		}
	}

	private void setOnClickListeners() {
		mValidHelp_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadHelp(1);
			}
		});
		mValidHelp_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                loadHelp(2);
			}
		});
		mValidHelp_3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                loadHelp(3);
			}
		});
        mValidHelp_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHelp(4);
            }
        });

		mYesButton_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFlipperNext();
			}
		});
		mNoButton_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// user_lang,  tigerAbdomen,  tigerTorax,  mosquito,  yellowTorax,  yellowAbdomen,  type
				TaskRunInfo info = new TaskRunInfo(lang, "no", "no", "no", "no", "no", "unknown");
				mTaskRun.setInfo(info);
				sendValidationResults();

			}
		});
		mNotSureButton_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( mTaskRun == null ) createTaskRunObject();
				TaskRunInfo info = new TaskRunInfo(lang, "no", "no", "unknown", "no", "no", "unknown");
				mTaskRun.setInfo(info);
				sendValidationResults();
			}
		});

		mNotSureButton_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( mTaskRun == null ) createTaskRunObject();
				TaskRunInfo info = new TaskRunInfo(lang, "no", "no", "yes", "no", "no", "tiger-unkown");
				mTaskRun.setInfo(info);
				sendValidationResults();
							}
		});
		mNoneButton_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( mTaskRun == null ) createTaskRunObject();
				TaskRunInfo info = new TaskRunInfo(lang, "no", "no", "yes", "no", "no", "mosquito-noneofboth");
				mTaskRun.setInfo(info);
				sendValidationResults();
			}
		});

		mYesButton_3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFlipperNext();
				mCurrentTorax = "yes";
			}
		});
		mNoButton_3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFlipperNext();
				mCurrentTorax = "no";
			}
		});

		mYesButton_4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTaskRun == null) createTaskRunObject();
				// user_lang,  tigerAbdomen,  tigerTorax,  mosquito,  yellowTorax,  yellowAbdomen,  type
				TaskRunInfo info = (mIsTiger ? new TaskRunInfo(lang, "yes", mCurrentTorax, "yes", "no", "no", "tiger") :
						new TaskRunInfo(lang, "no", "no", "yes", mCurrentTorax, "yes", "yellow"));
				mTaskRun.setInfo(info);
				sendValidationResults();
			}
		});
		mNoButton_4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTaskRun == null) createTaskRunObject();
				// user_lang,  tigerAbdomen,  tigerTorax,  mosquito,  yellowTorax,  yellowAbdomen,  type
				TaskRunInfo info = (mIsTiger ? new TaskRunInfo(lang, "no", mCurrentTorax, "yes", "no", "no", "tiger") :
						new TaskRunInfo(lang, "no", "no", "yes", mCurrentTorax, "no", "yellow"));
				mTaskRun.setInfo(info);
				sendValidationResults();
			}
		});

		mTigerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mToraxImage.setImageDrawable(getResources().getDrawable(R.drawable.tigre_torax));
				mAbdomenImage.setImageDrawable(getResources().getDrawable(R.drawable.tigre_abdomen));
				mIsTiger = true;
				showFlipperNext();
			}
		});
		mYellowButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mToraxImage.setImageDrawable(getResources().getDrawable(R.drawable.yellow_torax));
				mAbdomenImage.setImageDrawable(getResources().getDrawable(R.drawable.yellow_abdomen));
				mIsTiger = false;
				showFlipperNext();
			}
		});

	}

	private void sendValidationResults() {
		String taskrunUrl = Util.URL_TASKRUN;
		Gson gson = new Gson();
		String jsonTaskRun = gson.toJson(mTaskRun);
		//mCurrentTorax = null;
		//mIsTiger = false;
		Ion.with(this)
				.load(taskrunUrl)
				.setHeader("Accept", "application/json")
				.setHeader("Content-type", "application/json")
				.setStringBody(jsonTaskRun)
				.asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject jsonObject) {
						// do stuff with the result or error
Util.logInfo("==========++", jsonObject.toString());
						Toast.makeText(PhotoValidationActivity.this, R.string.end_validation, Toast.LENGTH_SHORT).show();
						// PhotoValidationActivity.this.finish();
						startNewValidation();
					}
				});
	}

	 private void startNewValidation() {
		 mCurrentTorax = null;
		 mIsTiger = false;
		 loadNewTask();
		 showFlipperFirst();
	 }

	private void showFlipperFirst() {
		// Next screen comes in from left.
		mViewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
		// Current screen goes out from right.
		mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
		// Load irst screen
		mViewFlipper.setDisplayedChild(0);
	}

	private void loadHelp(int num) {
        Intent intent = new Intent(PhotoValidationActivity.this, PhotoValidationHelpActivity.class);
        intent.putExtra(PhotoValidationActivity.HELP_PARAM, num);
        startActivity(intent);
    }

	private void showFlipperNext() {
		// Next screen comes in from right.
		mViewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
		// Current screen goes out from left.
		mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
		mViewFlipper.showNext();
	}

	private void showFlipperPrev() {
		// Next screen comes in from left.
		mViewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
		// Current screen goes out from right.
		mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
		mViewFlipper.showPrevious();
	}

	private void loadNewTask() {
		String newTaskUrl = Util.URL_NEW_TASK + "2/newtask"; // !!!! 1 - production, 2- development
Util.logInfo("===========", "Authorization >> " + UtilLocal.TIGASERVER_AUTHORIZATION);
Util.logInfo("===========", newTaskUrl);
		Ion.with(this)
				.load(newTaskUrl)
				.setHeader("Accept", "application/json")
				.setHeader("Content-type", "application/json")
				.setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
				.as(new TypeToken<Task>(){})
				.setCallback(new FutureCallback<Task>() {
					@Override
					public void onCompleted(Exception e, Task resultTask) {
						// do stuff with the result or error
						if ( resultTask != null ) {
Util.logInfo(this.getClass().getName(), "loadNewTask >> " + resultTask.toString());
							myTask = resultTask;
							loadPhoto();
							createTaskRunObject();
						}
					}
				});
	}

	private void createTaskRunObject() {
		mTaskRun = new TaskRun(myTask.getProjectId(), myTask.getId(), null);
	}

	private void loadPhoto() {
		//http://webserver.mosquitoalert.com/get_photo/q0n50KN2Tg1O0Zh/90bb084c-2d6b-48e9-9429-433fceb23447/medium
		String getPhotoUrl = Util.URL_GET_PHOTO + PHOTO_TOKEN + "/" + myTask.getInfo().getUuid() + "/medium";
Util.logInfo("===========", "Authorization >> " + UtilLocal.TIGASERVER_AUTHORIZATION);
Util.logInfo("===========", getPhotoUrl);
//		Ion.with(mPhoto1View)
//				.placeholder(R.drawable.ic_switchboard_icon_validacio_large)
//				.load(getPhotoUrl);

		Ion.with(this)
            .load(getPhotoUrl)
				//.setLogging("DeepZoom", Log.VERBOSE)
				.withBitmap()
				.deepZoom()
			.asBitmap()
            .setCallback(new FutureCallback<Bitmap>() {
        @Override
        public void onCompleted(Exception e, Bitmap result) {
				// do something with your bitmap
				/*mPhoto1View.setImageBitmap(result);
				mPhoto2View.setImageBitmap(result);
				mPhoto3View.setImageBitmap(result);
				mPhoto4View.setImageBitmap(result);*/
			}
		});
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.validation_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if ( item.getItemId() == R.id.close ) {
			this.finish();
			return true;
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		if ( mViewFlipper.getDisplayedChild() > 0 )
			showFlipperPrev();
		else
			super.onBackPressed();
	}
	
}
