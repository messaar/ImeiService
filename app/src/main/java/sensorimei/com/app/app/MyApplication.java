package sensorimei.com.app.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by Oswaldo Gomez on 27/05/2015.
 */
public class MyApplication extends Application {


    private static Context context;

    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        MyApplication.context=getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
