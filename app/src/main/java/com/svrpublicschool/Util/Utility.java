package com.svrpublicschool.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.SVRApplication;
import com.svrpublicschool.models.DatabaseVersion;
import com.svrpublicschool.models.DatabaseVesionModel;
import com.svrpublicschool.models.GroupDetailsEntity;
import com.svrpublicschool.models.KeyValueModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;

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

    public static int getServerVersionExcel(String keyname) {
        String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_VERSION_EXCEL, "");
        Gson gson = new Gson();
        DatabaseVesionModel databaseVesionModel = gson.fromJson(allString, DatabaseVesionModel.class);
        if (databaseVesionModel != null) {
            for (DatabaseVersion databaseVersion : databaseVesionModel.getDbversion()) {
                if (keyname.equals(databaseVersion.getName())) {
                    return databaseVersion.getVersion();
                }
            }
        }
        return -1;
    }

    public static boolean shoulFetchFromServer(Context context, String versionKey, String name) {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        if (isNetworkConnected(context)) {
            int localVersion = sharedPrefManager.getIntegerValueForKey(versionKey, -1);
            if (localVersion == -1) {
                return true;
            }
            int serverVersion = getServerVersionExcel(name);
            if (localVersion < serverVersion) {
                return true;
            }
        }
        return false;
    }

    public static int getServerVersion(Context context, String name) {
        Gson gson = new Gson();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        try {
            String versions = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_VERSION_FIREBASE, "");
            Type listType = new TypeToken<List<DatabaseVersion>>() {
            }.getType();
            List<DatabaseVersion> databaseVersions = gson.fromJson(versions, listType);
            if (databaseVersions != null && databaseVersions.size() > 0) {
                for (int i = 0; i < databaseVersions.size(); i++) {
                    if (databaseVersions.get(i).getName().equalsIgnoreCase(name)) {
                        return databaseVersions.get(i).getVersion();
                    }
                }
            }
        } catch (Exception e) {

        }
        return -1;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String readAssetsJsonFile(Context context, String file) {
        String json;
        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static int getDisplayWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getDisplayHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static String getFormattedTime(long time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            return simpleDateFormat.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getRandomColor(int number, Context context) {
        if (number == -1) {
            number = new Random().nextInt(10) + 1;
        }
        number = number % 7;

        switch (number) {
            case 0:
                return ContextCompat.getColor(context, R.color.progress_blue);
            case 1:
                return ContextCompat.getColor(context, R.color.colorAccent);
            case 2:
                return ContextCompat.getColor(context, R.color.whatsapp_image_bg);
            case 3:
                return ContextCompat.getColor(context, R.color.green_descent);
            case 4:
                return ContextCompat.getColor(context, R.color.colorGrey88);
            case 5:
                return ContextCompat.getColor(context, R.color.lightBlue);
            case 6:
                return ContextCompat.getColor(context, R.color.red_text);
            case 7:
                return ContextCompat.getColor(context, R.color.uvv_green);

            default:
                return ContextCompat.getColor(context, R.color.whatsapp_image_bg);

        }
    }

    public static final GroupDetailsEntity getGroupDetails() {
        GroupDetailsEntity groupDetailsEntity = new GroupDetailsEntity();
        groupDetailsEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        groupDetailsEntity.setFid("-M8Lanj7FiV5iwmsaPql");
        groupDetailsEntity.setGpName("Ask Question ?");
        groupDetailsEntity.setGpIcon("");
        return groupDetailsEntity;
    }

    public static String getFormattedDate(long timeInMilli, boolean withTime) {
        String result = "";
        try {
            Calendar estimateDate = Calendar.getInstance();
            estimateDate.setTimeInMillis(timeInMilli);
            SimpleDateFormat desiredFormat;
            desiredFormat = new SimpleDateFormat("d\'" + getFormattedEstimatedDeliveryDate(estimateDate.get(Calendar.DAY_OF_MONTH)) + "\' MMM, yyyy" + (withTime ? " h:mm a" : ""), Locale.ENGLISH);
            result = desiredFormat.format(estimateDate.getTime());
        } catch (Exception e) {

        }
        return result;
    }

    public static String getFormattedEstimatedDeliveryDate(int dayOfMonth) {
        String result = "";
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            result = "th";
        } else {
            switch (dayOfMonth % 10) {
                case 1:
                    result = "st";
                    break;
                case 2:
                    result = "nd";
                    break;
                case 3:
                    result = "rd";
                    break;
                default:
                    result = "th";
                    break;
            }
        }
        return result;
    }


    public static boolean isListEmpty(List mDataList) {
        return null == mDataList || mDataList.isEmpty();
    }

    @SuppressLint("HardwareIds")
    public static String getUniqueDeviceId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID((long) androidId.hashCode(), (long) androidId.hashCode());
        return deviceUuid.toString();
    }

    public static FakeAppUpdateManager getFakeAppUpdateManager(Context context) {
        FakeAppUpdateManager fakeAppUpdateManager = new FakeAppUpdateManager(context);
        fakeAppUpdateManager.getAppUpdateInfo().getResult().isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);
        fakeAppUpdateManager.setUpdateAvailable(10);
        return fakeAppUpdateManager;
    }
}