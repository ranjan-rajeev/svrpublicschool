package com.svrpublicschool;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.ui.main.MainActivity;

import static com.svrpublicschool.firebase.FirebaseHelper.getBannerList;

public class SplashScreenActivity extends BaseActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final int REQUEST_UPDATE = 100;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private ImageView logo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primaryDarkColor));
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.logo);
      /*  setUpdateListener();
        checkAppUpdate(this);*/
        FirebaseHelper.logAppOpenedEvent(this);
        Logger.d(getBannerList());
        FirebaseApp.initializeApp(this);
        initializeFireBaseRemoteConfig();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Create an Intent that will start the Menu-Activity.
                redirectToMain();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /*private void setUpdateListener() {
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(@NonNull InstallState state) {
                switch (state.installStatus()) {
                    case INSTALLED:
                        Logger.d("INSTALLED");
                        break;
                    case PENDING:
                        Logger.d("PENDING");
                        break;
                    case DOWNLOADING:
                        Logger.d("DOWNLOADING");
                        break;
                    case CANCELED:
                        Logger.d("CANCELED");
                        break;
                    case FAILED:
                        Logger.d("FAILED");
                        break;
                }
            }
        };
    }*/

    public void redirectToMain() {
        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        mainIntent.putExtra(Constants.INTENT_PARAM_FROM, "SPLASH");
        startActivity(mainIntent);
        finish();
    }

    private void initializeFireBaseRemoteConfig() {
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            Logger.v("FireBaseRemoteConfig", "InstanceID " + deviceToken);
            //AppEventsLogger.setPushNotificationsRegistrationId(deviceToken);
            // Do whatever you want with your token now
            // i.e. store it on SharedPreferences or DB
            // or directly send it to server
        });
        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        FirebaseHelper.getValuesFromFirebaseRemoteConfig();
    }

    /*private void checkAppUpdate(Context context) {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                Logger.d("Update Available  : IMMEDIATE");
                handleImmediateUpdate(appUpdateManager, appUpdateInfoTask);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                Logger.d("Update Available  : FLEXIBLE");
                handleFlexibleRelease(appUpdateManager, appUpdateInfoTask);
            }
        });
    }

    private void handleImmediateUpdate(AppUpdateManager appUpdateManager, Task<AppUpdateInfo> info) {
        if ((info.getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                info.getResult().updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                info.getResult().isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            try {
                appUpdateManager.startUpdateFlowForResult(info.getResult(), AppUpdateType.IMMEDIATE, this, REQUEST_UPDATE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFlexibleRelease(AppUpdateManager appUpdateManager, Task<AppUpdateInfo> info) {
        if ((info.getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                info.getResult().updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                info.getResult().isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            showUpdateAvailableDialog(logo, appUpdateManager, info);
        }
    }

    private void showUpdateAvailableDialog(View view, AppUpdateManager appUpdateManager, Task<AppUpdateInfo> info) {
        Snackbar.make(view, "New version is available to download !!!", Snackbar.LENGTH_LONG)
                .setAction("Download", v -> {
                    try {
                        appUpdateManager.registerListener(installStateUpdatedListener);
                        appUpdateManager.startUpdateFlowForResult(info.getResult(), AppUpdateType.FLEXIBLE, this, REQUEST_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }).show();
    }

    private void launchRestartDialog(AppUpdateManager appUpdateManager) {

    }*/

}
