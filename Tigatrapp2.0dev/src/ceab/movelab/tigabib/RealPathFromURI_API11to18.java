package ceab.movelab.tigabib;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RealPathFromURI_API11to18 {
	  public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
          String[] proj = { MediaStore.Images.Media.DATA };
          String result = null;
           
          CursorLoader cursorLoader = new CursorLoader(
                  context, 
            contentUri, proj, null, null, null);        
          Cursor cursor = cursorLoader.loadInBackground();
          
          if(cursor != null){
           int column_index = 
             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           cursor.moveToFirst();
           result = cursor.getString(column_index);
          }
          return result;  
    }
}
