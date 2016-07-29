package ceab.movelab.tigatrapp.testmap;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

import ceab.movelab.tigatrapp.R;


public class MapsActivity extends MapActivity {

    //private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }


}
