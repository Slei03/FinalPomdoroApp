package sallycs.com.pomoapp;

import android.app.Application;
import android.os.SystemClock;

//Splash Screen lasts for 1 second

public class App extends Application {
    public void onCreate(){
        super.onCreate();
        SystemClock.sleep(1000);
    }
}
