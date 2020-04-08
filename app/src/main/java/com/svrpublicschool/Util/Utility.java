package com.svrpublicschool.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.SVRApplication;
import com.svrpublicschool.models.BannerModel;
import com.svrpublicschool.models.KeyValueModel;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;

public class Utility {

    public static final String SHARED_PREFERENCE = "shared_svr";


    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getSharedPreferenceEditor(Context context) {
        return getSharedPreference(context).edit();
    }

    public static String getCurrentMobileDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }


    public static File createDirIfNotExists() {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), "/FINMART");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return file;
    }

    public static File createShareDirIfNotExists() {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), "/FINMART/QUOTES");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Quotes folder");
                ret = false;
            }
        }
        return file;
    }


    public static String getLocalIpAddress(Context context) {
        String IPaddress;

        boolean WIFI = false;

        boolean MOBILE = false;

        ConnectivityManager CM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    WIFI = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    MOBILE = true;
        }

        if (WIFI == true) {
            return GetDeviceipWiFiData(context);
        }

        if (MOBILE == true) {
            return GetDeviceipMobileData();

        }


       /* WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());*//*
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }*/
        return "";
    }

    public static String GetDeviceipMobileData() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Current IP", ex.toString());
        }
        return "";
    }

    public static String getMacAddress(Context context) throws IOException {
//        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wInfo = wifiManager.getConnectionInfo();
//        Toast.makeText(context, "" + wInfo.getMacAddress(), Toast.LENGTH_SHORT).show();
//        return wInfo.getMacAddress();
        String address = "";
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifiManager.isWifiEnabled()) {
                // WIFI ALREADY ENABLED. GRAB THE MAC ADDRESS HERE
                WifiInfo info = wifiManager.getConnectionInfo();
                address = info.getMacAddress();
            } else {

                try {
                    // get all the interfaces
                    List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

                    //find network interface wlan0
                    for (NetworkInterface networkInterface : all) {
                        if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                        //get the hardware address (MAC) of the interface
                        byte[] macBytes = networkInterface.getHardwareAddress();
                        if (macBytes == null) {
                            return "";
                        }


                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            //gets the last byte of b
                            res1.append(Integer.toHexString(b & 0xFF) + ":");
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        address = res1.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Toast.makeText(context, "" + address, Toast.LENGTH_SHORT).show();
        return address;
    }

    public static String GetDeviceipWiFiData(Context context) {

        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);

        @SuppressWarnings("deprecation")

        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;

    }


    public static String getVersionName(Context context) {
        String versionName = "";
        PackageInfo pinfo;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        PackageInfo pinfo;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getDeviceId(Context context) {
        String deviceId = "";
        if (context != null)
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        return deviceId;
    }


    public static void loadWebViewUrlInBrowser(Context context, String url) {
        Log.d("URL", url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        if (Uri.parse(url) != null) {
            browserIntent.setData(Uri.parse(url));
        }
        context.startActivity(browserIntent);
    }

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();

        // I used getActivity() as if you were calling from a fragment.
        // You just want to call getTheme() on the current activity, however you can get it
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        // it's probably a good idea to check if the color wasn't specified as a resource
        if (typedValue.resourceId != 0) {
            return typedValue.resourceId;
        } else {
            // this should work whether there was a resource id or not
            return typedValue.data;
        }
    }

    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        // I used getActivity() as if you were calling from a fragment.
        // You just want to call getTheme() on the current activity, however you can get it
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);

        // it's probably a good idea to check if the color wasn't specified as a resource
        if (typedValue.resourceId != 0) {
            return typedValue.resourceId;
        } else {
            // this should work whether there was a resource id or not
            return typedValue.data;
        }
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(SVRApplication.getContext());

    public static String getStringValue(String keyname) {
        String result = "";
        String allString = sharedPrefManager.getStringValueForKey(Constants.KEY_VALUE_STRING, "");
        Gson gson = new Gson();
        KeyValueModel keyValueModel = gson.fromJson(allString, KeyValueModel.class);
        if (keyValueModel != null) {
            for (KeyValueModel.KeyValueEntity keyValueEntity : keyValueModel.getKeyValue()) {
                if (keyname.equals(keyValueEntity.getKey())) {
                    return keyValueEntity.getValue();
                }
            }
        }

        return result;
    }

    public static String[] getBannerList() {

        ArrayList<String> stringArrayList = new ArrayList<String>();
        String allBanner = sharedPrefManager.getStringValueForKey(Constants.BANNER, "");
        Gson gson = new Gson();
        BannerModel bannerModel = gson.fromJson(allBanner, BannerModel.class);
        if (bannerModel != null) {
            for (int i = 0; i < bannerModel.getBanner().size(); i++) {
                stringArrayList.add(bannerModel.getBanner().get(i).getUrl());
            }
        }

        if (stringArrayList.size() < 1) {
            stringArrayList.add("https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg");
            stringArrayList.add("https://c1.staticflickr.com/5/4816/45746112072_23c3e157a4_o.jpg");
            stringArrayList.add("https://c1.staticflickr.com/5/4806/45746111412_486e1a26b5_o.jpg");
            stringArrayList.add("https://c1.staticflickr.com/5/4889/45746110872_5c96e1df01_o.jpg");
        }
        return GetStringArray(stringArrayList);
    }

    // Function to convert ArrayList<String> to String[]
    public static String[] GetStringArray(ArrayList<String> arr) {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // Convert ArrayList to object array
        Object[] objArr = arr.toArray();

        // Iterating and converting to String
        int i = 0;
        for (Object obj : objArr) {
            str[i++] = (String) obj;
        }

        return str;
    }
}