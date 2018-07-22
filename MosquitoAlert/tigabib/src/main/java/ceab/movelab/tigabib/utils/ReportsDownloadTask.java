package ceab.movelab.tigabib.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import ceab.movelab.tigabib.ContProvContractMissions;
import ceab.movelab.tigabib.ContProvContractReports.Reports;
import ceab.movelab.tigabib.ContProvValuesReports;
import ceab.movelab.tigabib.MissionItemModel;
import ceab.movelab.tigabib.MissionModel;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.Report;
import ceab.movelab.tigabib.Util;
import ceab.movelab.tigabib.UtilLocal;
import ceab.movelab.tigabib.model.profile.PhotoServer;
import ceab.movelab.tigabib.model.profile.ProfileDevice;
import ceab.movelab.tigabib.model.profile.Response;
import ceab.movelab.tigabib.model.profile.UserReport;

import static ceab.movelab.tigabib.Util.getReportsUri;


public class ReportsDownloadTask extends AsyncTask<Void, Integer, Boolean> {
    private static String TAG = "ReportsDownloadTask";

    private WeakReference<Context> mWeakContext;
    private ContentResolver mCR;
    private List<ProfileDevice> mProfileDeviceList;

    private ProgressDialog mProgDialog;
    private int myProgress;


    public ReportsDownloadTask(WeakReference<Context> reference, List<ProfileDevice> profileDeviceList)  {
        this.mWeakContext = reference;
        this.mCR = reference.get().getContentResolver();
        this.mProfileDeviceList = profileDeviceList;
    }

    @Override
    protected void onPreExecute() {
        mProgDialog = new ProgressDialog(mWeakContext.get());
        mProgDialog.setTitle(mWeakContext.get().getResources().getString(R.string.downloading_user_data));
        mProgDialog.setIndeterminate(false);
        mProgDialog.setCancelable(false);
        mProgDialog.setMax(100);
        mProgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgDialog.show();

        myProgress = 0;
    }

    protected Boolean doInBackground(Void... s) {
        int currentOffset = 2;
        myProgress = currentOffset;
        publishProgress(myProgress);

        int numReports = 0;
        for ( ProfileDevice profileDevice: mProfileDeviceList ) {
            numReports += profileDevice.getUserReports().size();
        }
        int progressReport = ( numReports == 0 ? 100 : (98/numReports) );

        for ( ProfileDevice profileDevice: mProfileDeviceList ) {
            List<UserReport> userReportsList = profileDevice.getUserReports();
            for ( UserReport userReport : userReportsList ) {
                currentOffset = progressReport;
                int type = (userReport.getType().contentEquals("adult") ? Report.TYPE_ADULT : Report.TYPE_BREEDING_SITE);
                JSONObject responses = new JSONObject();
                try {
                    String thisTaskModel = type == Report.TYPE_ADULT ?
                            MissionModel.makeAdultConfirmation(mWeakContext.get()).getString(ContProvContractMissions.Tasks.KEY_TASK_JSON) :
                            MissionModel.makeSiteConfirmation(mWeakContext.get()).getString(ContProvContractMissions.Tasks.KEY_TASK_JSON);
                    JSONObject thisTask = new JSONObject(thisTaskModel);
                    if ( thisTask.has(MissionModel.KEY_ITEMS) ) {
                        JSONArray theseItems = new JSONArray(thisTask.getString(MissionModel.KEY_ITEMS));
                        int i = 0;
                        List<Response> responseList = userReport.getResponses();
                        for ( Response response : responseList ) {
                            JSONObject json = new JSONObject(theseItems.getString(i++));
                            String itemId = json.getString("id");

                            JSONObject thisResponse = new JSONObject();
                            thisResponse.put(MissionItemModel.KEY_ITEM_ID, itemId);
                            thisResponse.put(MissionItemModel.KEY_ITEM_TEXT, response.getQuestion());
                            thisResponse.put(MissionItemModel.KEY_ITEM_RESPONSE, response.getAnswer());

                            responses.put(itemId, thisResponse);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int locationChoice = (userReport.getLocationChoice().contentEquals("selected") ?
                        Report.LOCATION_CHOICE_SELECTED : Report.LOCATION_CHOICE_CURRENT);

                File directory = new File(Environment.getExternalStorageDirectory(),
                        mWeakContext.get().getResources().getString(R.string.app_directory));
                directory.mkdirs();

                JSONArray jsonPhotos = new JSONArray();
                List<PhotoServer> photosList = userReport.getPhotos();
                int photoOffset = progressReport / (photosList.size()+1);
                for ( PhotoServer photo : photosList ) {
                    String photoPath = directory + "/" + photo.getPhoto().replace("tigapics/", "");
                    JSONObject newPhoto = new JSONObject();
                    try {
                        newPhoto.put(Report.KEY_PHOTO_URI, photoPath);
                        newPhoto.put(Report.KEY_PHOTO_TIME, System.currentTimeMillis());
                        jsonPhotos.put(newPhoto);
                        downloadPhoto(mWeakContext.get(), photo.getPhoto(), photoPath);
                        myProgress += photoOffset;
                        publishProgress(myProgress);
                        currentOffset -= photoOffset;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                int photoAttached = (jsonPhotos.length() > 0 ? Report.YES : Report.NO);

                Report thisReport = new Report(mWeakContext.get(), userReport.getVersionUUID(), userReport.getUser(),
                        userReport.getReportId(), userReport.getVersionNumber(), 0,
                        userReport.getCreationTime(), userReport.getVersionTime(), type,
                        responses.toString(), Report.CONFIRMATION_CODE_POSITIVE, locationChoice,
                        userReport.getCurrentLocationLat(), userReport.getCurrentLocationLon(),
                        userReport.getSelectedLocationLat(), userReport.getSelectedLocationLon(),
                        photoAttached, jsonPhotos.toString(), userReport.getNote(),
                        Report.UPLOADED_ALL, -1, 0,1,
                        userReport.getPackageName(), userReport.getPackageVersion(),
                        userReport.getDeviceManufacturer(), userReport.getDeviceModel(), userReport.getOs(),
                        userReport.getOsVersion(), userReport.getOsLanguage(), userReport.getAppLanguage(),
                        0);

/*					Report thisssReport = new Report(mContext, userReport.getVersionUUID(), userReport.getUser(),
							userReport.getReportId(), userReport.getVersionNumber(), 0,
							userReport.getCreationTime(), userReport.getVersionTime(), type,
							responses.toString(), Report.CONFIRMATION_CODE_POSITIVE, locationChoice,
							userReport.getCurrentLocationLat(), userReport.getCurrentLocationLon(),
							userReport.getSelectedLocationLat(), userReport.getSelectedLocationLon(),
							photoAttached, jsonPhotos.toString(), userReport.getNote(),
							0, 0, 0, 0, "", 0, "", "", "", "", "", "", 0) {
					}*/
                // First delete any previous existing report with same id
                String sc = Reports.KEY_REPORT_ID + " = '" + userReport.getReportId() + "'";
                int nDeleted = mCR.delete(getReportsUri(mWeakContext.get()), sc, null);
Util.logInfo(TAG, sc);
Util.logInfo(TAG, "n deleted: " + nDeleted);

                // ContProvValuesReports.createReport(thisReport);
                // Then save report to internal DB
                Uri repUri = getReportsUri(mWeakContext.get());
                mCR.insert(repUri, ContProvValuesReports.createReport(thisReport));

                // now mark all prior reports as not latest version
                String where = Reports.KEY_REPORT_ID + " = '" + thisReport.getReportId() +
                        "' AND " + Reports.KEY_REPORT_VERSION + " < " + thisReport.getReportVersion();
                ContentValues cv = new ContentValues();
                cv.put(Reports.KEY_LATEST_VERSION, 0);
                mCR.update(repUri, cv, where, null);

                myProgress += currentOffset;
                publishProgress(myProgress);
            }
        }
        myProgress = 100;
        publishProgress(myProgress);

        return true;
    }

    protected void onProgressUpdate(Integer... progress) {
        mProgDialog.setProgress(progress[0]);
    }

    protected void onPostExecute(Boolean result){
        try {
            mProgDialog.dismiss();
            mProgDialog = null;
        } catch (Exception e) {
            // I realize this is ugly, but it is a solution to the problem discussed here:
            // https://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager/5102572#5102572
        }
//			if ( result && resultFlag == SUCCESS ) {
//				Util.toastTimed(mContext, mContext.getResources().getString(R.string.report_sent_confirmation), Toast.LENGTH_LONG);
//				mReport.clear();
//			}
    }

    private void downloadPhoto(Context ctx, String remotePhoto, String localPhotoPath) {
        //http://humboldt.ceab.csic.es/media/tigapics/4f9f58ce-901e-4e0b-81ff-061a43f8e5a5.jpg
        String urlPhoto = UtilLocal.URL_TIGASERVER + Util.API_MEDIA + remotePhoto;
Util.logInfo("==============", "TEST downloadPhoto: " + urlPhoto);

        try {
            Ion.with(ctx)
                    .load(urlPhoto)
                    // have a ProgressDialog
                    //.progressDialog(progressDialog)
// can also use a custom callback
                    /*.progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            System.out.println("" + downloaded + " / " + total);
                            //publishProgress(progressOffset);
                            Long offset = downloaded/total;
                            Integer intValue = offset.intValue();
                            progressDialog.setSecondaryProgress(intValue);
                        }
                    })*/
                    .write(new File(localPhotoPath))
                    .get();
					/*.setCallback(new FutureCallback<File>() {
						@Override
						public void onCompleted(Exception e, File file) {
							// download done...
							// do stuff with the File or error
						}
					})*/;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}