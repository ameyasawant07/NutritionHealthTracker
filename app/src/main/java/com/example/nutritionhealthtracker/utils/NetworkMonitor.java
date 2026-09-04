package com.example.nutritionhealthtracker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class NetworkMonitor {

    public interface NetworkStatusListener {
        void onNetworkStatusChanged(boolean isOnline);
    }

    private static NetworkMonitor instance;
    private final Context context;
    private boolean currentlyOnline = false;
    private final List<NetworkStatusListener> listeners = new ArrayList<>();
    private ConnectivityManager.NetworkCallback networkCallback;

    private NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.currentlyOnline = checkIsOnline(this.context);
        registerNetworkCallback();
    }

    public static synchronized NetworkMonitor getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkMonitor(context);
        }
        return instance;
    }

    public static boolean isOnline(Context context) {
        return getInstance(context).isCurrentlyOnline();
    }

    public boolean isCurrentlyOnline() {
        currentlyOnline = checkIsOnline(context);
        return currentlyOnline;
    }

    public void addListener(NetworkStatusListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            listener.onNetworkStatusChanged(isCurrentlyOnline());
        }
    }

    public void removeListener(NetworkStatusListener listener) {
        listeners.remove(listener);
    }

    private void registerNetworkCallback() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        updateStatus(true);
                    }

                    @Override
                    public void onLost(Network network) {
                        updateStatus(checkIsOnline(context));
                    }
                };
                cm.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkRequest request = new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build();
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        updateStatus(true);
                    }

                    @Override
                    public void onLost(Network network) {
                        updateStatus(checkIsOnline(context));
                    }
                };
                cm.registerNetworkCallback(request, networkCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(boolean isOnline) {
        if (currentlyOnline != isOnline) {
            currentlyOnline = isOnline;
            new Handler(Looper.getMainLooper()).post(() -> {
                for (NetworkStatusListener listener : new ArrayList<>(listeners)) {
                    listener.onNetworkStatusChanged(currentlyOnline);
                }
            });
        }
    }

    public static boolean checkIsOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network == null) return false;
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if (nc == null) return false;
                boolean hasCapability = nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                boolean hasTransport = nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                return hasCapability && hasTransport;
            } else {
                android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = cm.getActiveNetwork();
                if (activeNetwork == null) return false;
                NetworkCapabilities nc = cm.getNetworkCapabilities(activeNetwork);
                return nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            } else {
                android.net.NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
