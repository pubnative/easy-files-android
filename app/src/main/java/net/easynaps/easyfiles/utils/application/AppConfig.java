package net.easynaps.easyfiles.utils.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.database.UtilsHandler;
import net.easynaps.easyfiles.utils.LruBitmapCache;
import net.easynaps.easyfiles.utils.ScreenUtils;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.PNLite;

/**
 * Created by vishal on 7/12/16 edited by Emmanuel Messulam<emmanuelbendavid@gmail.com>
 */

public class AppConfig extends GlideApplication {

    public static final String TAG = AppConfig.class.getSimpleName();

    private UtilitiesProvider utilsProvider;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private UtilsHandler mUtilsHandler;

    private static Handler mApplicationHandler = new Handler();
    private static HandlerThread sBackgroundHandlerThread = new HandlerThread("app_background");
    private static Handler sBackgroundHandler;
    private static Context sActivityContext;
    private static ScreenUtils screenUtils;

    private static AppConfig mInstance;

    public UtilitiesProvider getUtilsProvider() {
        return utilsProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);//selector in srcCompat isn't supported without this
        mInstance = this;

        utilsProvider = new UtilitiesProvider(this);
        mUtilsHandler = new UtilsHandler(this);

        sBackgroundHandlerThread.start();
        sBackgroundHandler = new Handler(sBackgroundHandlerThread.getLooper());

        // disabling file exposure method check for api n+
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        PNLite.initialize(getString(R.string.pnlite_app_token), this, success -> {

        });

        SdkConfiguration sdkConfiguration = new SdkConfiguration
                .Builder(getString(R.string.mopub_banner_ad_unit_id))
                .build();
        MoPub.initializeSdk(AppConfig.this, sdkConfiguration, () -> {

        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sBackgroundHandlerThread.quit();
    }

    /**
     * Post a runnable to handler. Use this in case we don't have any restriction to execute after
     * this runnable is executed, and {@link #runInBackground(CustomAsyncCallbacks)} in case we need
     * to execute something after execution in background
     *
     * @param runnable
     */
    public static void runInBackground(Runnable runnable) {
        synchronized (sBackgroundHandler) {
            sBackgroundHandler.post(runnable);
        }
    }

    /**
     * A compact AsyncTask which runs which executes whatever is passed by callbacks.
     * Supports any class that extends an object as param array, and result too.
     *
     * @param customAsyncCallbacks
     */
    public static void runInBackground(final CustomAsyncCallbacks customAsyncCallbacks) {

        synchronized (customAsyncCallbacks) {

            new AsyncTask<Object, Object, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    customAsyncCallbacks.onPreExecute();
                }

                @Override
                protected void onProgressUpdate(Object... values) {
                    super.onProgressUpdate(values);
                    customAsyncCallbacks.publishResult(values);
                }

                @Override
                protected Void doInBackground(Object... params) {
                    return customAsyncCallbacks.doInBackground();
                }

                @Override
                protected void onPostExecute(Object aVoid) {
                    super.onPostExecute(aVoid);
                    customAsyncCallbacks.onPostExecute(aVoid);
                }
            }.execute(customAsyncCallbacks.params());
        }
    }

    /**
     * Interface providing callbacks utilized by {@link #runInBackground(CustomAsyncCallbacks)}
     */
    public interface CustomAsyncCallbacks {

        <E extends Object> E doInBackground();

        Void onPostExecute(Object result);

        Void onPreExecute();

        Void publishResult(Object... result);

        <T extends Object> T[] params();
    }

    /**
     * Shows a toast message
     *
     * @param context Any context belonging to this application
     * @param message The message to show
     */
    public static void toast(Context context, String message) {
        // this is a static method so it is easier to call,
        // as the context checking and casting is done for you

        if (context == null) return;

        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }

        if (context instanceof Application) {
            final Context c = context;
            final String m = message;

            ((AppConfig) context).runInApplicationThread(() -> {
                Toast.makeText(c, m, Toast.LENGTH_LONG).show();
            });
        }
    }

    /**
     * Run a runnable in the main application thread
     *
     * @param r Runnable to run
     */
    public void runInApplicationThread(Runnable r) {
        mApplicationHandler.post(r);
    }

    public static synchronized AppConfig getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            this.mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
        }
        return mImageLoader;
    }

    public UtilsHandler getUtilsHandler() {
        return mUtilsHandler;
    }

    public static void setActivityContext(Context context) {
        sActivityContext = context;
        screenUtils = new ScreenUtils((Activity) context);
    }

    public ScreenUtils getScreenUtils() {
        return screenUtils;
    }

    public Context getActivityContext() {
        return sActivityContext;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
