package sallycs.com.pomoapp;

import android.app.Application;
import android.os.SystemClock;

public class App extends Application {
    public void onCreate(){
        super.onCreate();
        SystemClock.sleep(1000);
    }
}
