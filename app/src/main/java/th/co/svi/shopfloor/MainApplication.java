package th.co.svi.shopfloor;

import android.app.Application;

import th.co.svi.shopfloor.manager.Contextor;


/**
 * Created by MIS_Student5 on 4/2/2559.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
