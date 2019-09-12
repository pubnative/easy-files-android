package net.easynaps.easyfiles.advertising;

import android.text.TextUtils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class AdManager {
    private final Map<String, Queue<AdNetwork>> mNetworkMap;

    private static AdManager sInstance;

    private AdManager() {
        mNetworkMap = new HashMap<>();
        setupQueues();
    }

    public static AdManager getInstance() {
        if (sInstance == null) {
            sInstance = new AdManager();
        }

        return sInstance;
    }

    public AdNetwork getNextNetwork(String placement) {
        if (TextUtils.isEmpty(placement) || mNetworkMap.get(placement) == null) {
            return null;
        } else {
            Queue<AdNetwork> queue = mNetworkMap.get(placement);
            AdNetwork next = queue.poll();
            queue.offer(next);
            return next;
        }
    }

    private void setupQueues() {
        setupSettingsBannerQueue();
        setupAboutMRectQueue();
        setupHomeInterstitialQueue();
        setupHistoryRewardedQueue();
    }

    private void setupSettingsBannerQueue() {
        Queue<AdNetwork> queue = new ArrayDeque<>();
        queue.offer(AdNetwork.PUBNATIVE);
        queue.offer(AdNetwork.APPLOVIN);
        queue.offer(AdNetwork.IRONSOURCE);
        queue.offer(AdNetwork.FYBER);
        //queue.offer(AdNetwork.FACEBOOK);
        queue.offer(AdNetwork.MOPUB);
        queue.offer(AdNetwork.GOOGLE_ADS_MANAGER);
        queue.offer(AdNetwork.ADMOB);
        queue.offer(AdNetwork.STARTAPP);
        queue.offer(AdNetwork.UNITY);
        queue.offer(AdNetwork.APPODEAL);

        mNetworkMap.put(EasyFilesAdConstants.PLACEMENT_BANNER_SETTINGS, queue);
    }

    private void setupAboutMRectQueue() {
        Queue<AdNetwork> queue = new ArrayDeque<>();
        queue.offer(AdNetwork.PUBNATIVE);
        queue.offer(AdNetwork.APPLOVIN);
        //queue.offer(AdNetwork.IRONSOURCE);
        queue.offer(AdNetwork.FYBER);
        queue.offer(AdNetwork.FACEBOOK);
        queue.offer(AdNetwork.MOPUB);
        queue.offer(AdNetwork.GOOGLE_ADS_MANAGER);
        //queue.offer(AdNetwork.ADMOB);
        queue.offer(AdNetwork.STARTAPP);
        queue.offer(AdNetwork.APPODEAL);

        mNetworkMap.put(EasyFilesAdConstants.PLACEMENT_MRECT_ABOUT, queue);
    }

    private void setupHomeInterstitialQueue() {
        Queue<AdNetwork> queue = new ArrayDeque<>();
        queue.offer(AdNetwork.PUBNATIVE);
        queue.offer(AdNetwork.APPLOVIN);
        queue.offer(AdNetwork.IRONSOURCE);
        queue.offer(AdNetwork.FYBER);
        //queue.offer(AdNetwork.FACEBOOK);
        queue.offer(AdNetwork.MOPUB);
        queue.offer(AdNetwork.GOOGLE_ADS_MANAGER);
        queue.offer(AdNetwork.ADMOB);
        queue.offer(AdNetwork.STARTAPP);
        queue.offer(AdNetwork.UNITY);
        queue.offer(AdNetwork.APPODEAL);

        mNetworkMap.put(EasyFilesAdConstants.PLACEMENT_INTERSTITIAL_HOME, queue);
    }

    private void setupHistoryRewardedQueue() {
        Queue<AdNetwork> queue = new ArrayDeque<>();
        queue.offer(AdNetwork.APPLOVIN);
        queue.offer(AdNetwork.IRONSOURCE);
        queue.offer(AdNetwork.FYBER);
        //queue.offer(AdNetwork.FACEBOOK);
        queue.offer(AdNetwork.MOPUB);
        //queue.offer(AdNetwork.GOOGLE_ADS_MANAGER);
        queue.offer(AdNetwork.ADMOB);
        queue.offer(AdNetwork.STARTAPP);
        //queue.offer(AdNetwork.IMA);
        queue.offer(AdNetwork.UNITY);
        queue.offer(AdNetwork.APPODEAL);

        mNetworkMap.put(EasyFilesAdConstants.PLACEMENT_REWARDED_HISTORY, queue);
    }
}
