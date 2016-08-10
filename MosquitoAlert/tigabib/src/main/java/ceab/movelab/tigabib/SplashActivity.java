package ceab.movelab.tigabib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public class SplashActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       	setContentView(R.layout.splash);

		//Set Runnable to remove splash screen just in case
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				continueFromSplashScreen();
		  	}
		}, 2000); //  2500 real !!!
    }

    protected void continueFromSplashScreen() {
    	Intent intent = new Intent(this, SwitchboardActivity.class);
		startActivity(intent);
        this.finish();
    }

}