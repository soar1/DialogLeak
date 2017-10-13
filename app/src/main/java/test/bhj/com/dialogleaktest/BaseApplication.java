package test.bhj.com.dialogleaktest;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class BaseApplication extends Application
{
    private static BaseApplication mInstance;

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = this;

        mRefWatcher = LeakCanary.install(this);
    }

    public static BaseApplication getInstance()
    {
        return mInstance;
    }

    public static RefWatcher getRefWatcher(Context context)
    {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
}