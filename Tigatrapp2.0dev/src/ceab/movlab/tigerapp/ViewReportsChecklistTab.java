package ceab.movlab.tigerapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewReportsChecklistTab extends Activity {
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       TextView textview = new TextView(this);
       textview.setText("This is Android tab");
       setContentView(textview);
   }
}