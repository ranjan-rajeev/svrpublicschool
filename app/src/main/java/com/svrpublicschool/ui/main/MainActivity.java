package com.svrpublicschool.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.svrpublicschool.BaseActivity;
import com.svrpublicschool.BuildConfig;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.database.DatabaseController;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.DatabaseVersion;
import com.svrpublicschool.models.DatabaseVesionModel;
import com.svrpublicschool.models.GroupDetailsEntity;
import com.svrpublicschool.models.KeyValueModel;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.services.HttpService;
import com.svrpublicschool.ui.book.BookFragment;
import com.svrpublicschool.ui.contact.ContactFragment;
import com.svrpublicschool.ui.facility.FacilityFragment;
import com.svrpublicschool.ui.faculty.FacultyFragment;
import com.svrpublicschool.ui.gallery.GalleryFragment;
import com.svrpublicschool.ui.home.HomeFragment;
import com.svrpublicschool.ui.homework.HomeworkFragment;
import com.svrpublicschool.ui.login.LoginBottomSheetDialog;
import com.svrpublicschool.ui.newdashboard.DashboardFragment;
import com.svrpublicschool.ui.study.StudyFragment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.svrpublicschool.Util.Constants.DB_GROUP_DETAILS;
import static com.svrpublicschool.Util.Constants.DB_VERSION;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_PERMISSIONS_FILE_ACCESS = 1002;
    RecyclerView rvBottom;
    NavigationAdapter mAdapter;
    boolean doubleBackToExitPressedOnce = false;

    BottomNavigationView bottom_navigation;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPrefManager sharedPrefManager;
    TextView tvName;
    LinearLayout llNavParent;
    NavigationView navigationView;
    DrawerLayout drawer;

    private static final int REQUEST_UPDATE = 11;
    private AppUpdateManager mAppUpdateManager;
    private static boolean updatePopUpShown = false;

    public void setmAppUpdateManager(AppUpdateManager mAppUpdateManager) {
        this.mAppUpdateManager = mAppUpdateManager;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)) {

                Snackbar.make(findViewById(R.id.fab), "Let us update your app in background while you continue exploring the app !!!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Update", v -> {
                            try {
                                mAppUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, REQUEST_UPDATE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }).show();
            }
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, REQUEST_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                    popupSnackbarForCompleteUpdate();
            } else {
                Logger.d("checkForAppUpdateAvailability: something else");
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }
                    } else {
                        Logger.d("InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fab),
                "New app is ready!", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });
        //snackbar.setActionTextColor(getResources().getColor(R.color.install_color));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE) {
            if (resultCode != RESULT_OK) {
                Logger.d("onActivityResult: app download failed");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.name);
        getSupportActionBar().setTitle("");
        sharedPrefManager = SharedPrefManager.getInstance(this);
        init_widgets();

        //getVersionFromExcel(); //todo uncomment it
        setListener();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        loadHomeFragment();

        //String url = "https://google-developer-training.github.io/android-developer-fundamentals-course-concepts/en/android-developer-fundamentals-course-concepts-en.pdf";
        String url = "https://drive.google.com/uc?export=download&id=1QSqoaCmrYYdDurytJ-bB-gqA8S8Mk0qg";
        //new DownloadFileFromURL().execute(url);
        //updateVersionTodb();
        //updateUser();
        if (!checkPermission()) {
            requestPermission();
        }

        if (Utility.shoulFetchFromServer(this, Constants.SHD_PRF_USER_MASTER, Constants.SHD_PRF_USER_MASTER)) {
            new GetLatestUserKey().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }

        new GetLoginUser().execute();


        //getTestData();
        //updateVersionTodb();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!updatePopUpShown) {
            checkFirebaseConfigUpdate();
        }
    }

    private void checkFirebaseConfigUpdate() {
        if (Utility.getVersionCode(this) < FirebaseHelper.getServerVersionCode()) {
            if (FirebaseHelper.getForceUpdate()) {
                showAppUpdateAlert(false);
            } else {
                updatePopUpShown = true;
                showAppUpdateAlert(true);
            }
        }
    }

    private void showAppUpdateAlert(boolean b) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        builder.setTitle("Update SVR Public School ?");
        builder.setMessage("SVR Public School recommends that you update app to the latest version for better performance.");
        builder.setCancelable(b);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = MainActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        if (b) {
            builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    alertDialog.dismiss();
                }
            });
        }
        builder.show();
    }

    private void updateUser() {

        Observable<KeyValueModel> userModelObservable = HttpService.getInstance().getStringList(Constants.KEY_VALUE_STRING_URL);
        userModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<KeyValueModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KeyValueModel keyValueModel) {
                        Logger.d("TAGEER", keyValueModel.getKeyValue().get(0).getKey());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadHomeFragment() {
        bottom_navigation.setSelectedItemId(R.id.navigation_home);
        //View view = bottom_navigation.findViewById(R.id.navigation_home);
        //view.performClick();
    }

    private void setListener() {
        mAdapter = new NavigationAdapter(this, new DatabaseController(this).getDshboardList());
        rvBottom.setAdapter(mAdapter);
        bottom_navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        llNavParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginBottomSheet();
            }
        });
    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment fragment = null;
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    fragment = new DashboardFragment();
                    break;
                case R.id.navigation_gallery:
                    fragment = new GalleryFragment();
                    break;
                case R.id.navigation_facility:
                    fragment = new FacilityFragment();
                    break;

                case R.id.navigation_faculty:
                    fragment = new FacultyFragment();
                    break;

                case R.id.navigation_study:
                    fragment = new StudyFragment();
                    break;
            }


            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();

                return true;
            }
            return false;
        }
    };

    private void init_widgets() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        llNavParent = navigationView.getHeaderView(0).findViewById(R.id.llNavParent);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        rvBottom = findViewById(R.id.rvBottom);
        rvBottom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/

        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            if (getSupportFragmentManager().getBackStackEntryCount() == 0 &&
                    bottom_navigation.getSelectedItemId() != R.id.navigation_home) {
                loadHomeFragment();
            } else if (bottom_navigation.getSelectedItemId() == R.id.navigation_home && getSupportFragmentManager().getBackStackEntryCount() == 0) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    protected boolean isNavDrawerOpen() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            // Handle the camera action
            fragment = new DashboardFragment();
        } else if (id == R.id.nav_homework) {
            fragment = new HomeworkFragment();
        } else if (id == R.id.nav_book) {
            fragment = new BookFragment();
        } else if (id == R.id.nav_study) {
            fragment = new StudyFragment();
        } else if (id == R.id.nav_faculty) {
            fragment = new FacultyFragment();
        } else if (id == R.id.nav_gallery) {
            fragment = new GalleryFragment();
        } else if (id == R.id.nav_facility) {
            fragment = new FacilityFragment();

        } else if (id == R.id.nav_gallery) {
            fragment = new GalleryFragment();

        } else if (id == R.id.nav_contact) {
            fragment = new ContactFragment();

        } else if (id == R.id.nav_share) {
            drawer.closeDrawer(GravityCompat.START);
            openPlayStore();
            return true;
        } else if (id == R.id.nav_aboutus) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_logout) {
            drawer.closeDrawer(GravityCompat.START);
            new LogOutUser().execute();
            return true;
        }
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openPlayStore() {
        /*final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }*/

        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        final String appPackageName = getPackageName();
        String shareBody = "Download the SVR Public school app from Playstore " + "https://play.google.com/store/apps/details?id=" + appPackageName;
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "Share using"));
    }

    public void getVersionFromFirebase() {
        DatabaseReference versionRef = database.getReference(DB_VERSION);
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    List<DatabaseVersion> databaseVersions = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            DatabaseVersion databaseVersion = snapshot.getValue(DatabaseVersion.class);
                            databaseVersions.add(databaseVersion);
                        } catch (Exception e) {
                            databaseVersions = null;
                        }
                    }
                    if (databaseVersions != null && databaseVersions.size() > 0)
                        sharedPrefManager.putStringValueForKey(Constants.SHD_PRF_VERSION_FIREBASE, new Gson().toJson(databaseVersions));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.d("MainActivity", databaseError.getMessage());
            }
        });
    }

    public void getVersionFromExcel() {
        Observable<DatabaseVesionModel> userModelObservable = HttpService.getInstance().getDbVersionExcel(Constants.URL_DBVERSION);
        userModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DatabaseVesionModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DatabaseVesionModel keyValueModel) {
                        Logger.d(keyValueModel.getDbversion().get(0).getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateVersionTodb() {
        DatabaseReference versionRef = database.getReference(DB_GROUP_DETAILS).push();
        GroupDetailsEntity groupDetailsEntity = new GroupDetailsEntity();
        groupDetailsEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        groupDetailsEntity.setFid(versionRef.getKey());
        groupDetailsEntity.setGpName("Ask Question ?");
        groupDetailsEntity.setGpIcon("");
        versionRef.setValue(groupDetailsEntity);
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }
    }

    public void addFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }
    }

    public void addFragment(Fragment fragment, String tag, Bundle bundle) {
        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }
    }

    public void addSubjectFragment(Fragment fragment, ClassEntity classEntity) {
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("CLASSENTITY", classEntity);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "subject");
            fragmentTransaction.addToBackStack("subject");
            fragmentTransaction.commit();
        }
    }

    public void dialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit the application?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        builder.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog exitdialog = builder.create();
        exitdialog.show();

        Button negative = exitdialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button positive = exitdialog.getButton(DialogInterface.BUTTON_POSITIVE);
        negative.setTextColor(getResources().getColor(R.color.header_light_text));
        positive.setTextColor(getResources().getColor(R.color.black));
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog("Downloading file ...");
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/test.pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Logger.d(e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            Logger.d("Dowloaded(%) : " + progress[0] + "%");
            showDialog("Dowloaded(%) : " + progress[0] + "%");
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            cancelDialog();
            Logger.d("Download complete");

        }

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED /*&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/},
                REQUEST_CODE_PERMISSIONS_FILE_ACCESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS_FILE_ACCESS:
               /* if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Utility.shoulFetchFromServer(this, subjectEntity.getfId(), subjectEntity.getfId())) {
                        getChatListFromFirebase();
                    } else {
                        new ChatActivity.LoadLocalClassList(0).execute();
                    }
                } else {
                    //requestPermission();
                }*/
                break;
        }
    }

    public void openLoginBottomSheet() {

        if (isNavDrawerOpen()) {
            closeNavDrawer();
        }
        LoginBottomSheetDialog addClassBottomSheetDialog = new LoginBottomSheetDialog(this, false, result -> {
            if (result != null) { //success
                loadHomeFragment();
                tvName.setText("Welcome " + result.getName());
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                setUserEntity(result);
            } else { // Failure
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
        addClassBottomSheetDialog.show(getSupportFragmentManager(), "Opening login bottom sheet");
    }

    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity userEntity = gson.fromJson(allString, UserEntity.class);
            return userEntity;
        }

        @Override
        protected void onPostExecute(UserEntity userEntity) {
            super.onPostExecute(userEntity);
            if (userEntity != null) {
                tvName.setText("Welcome " + userEntity.getName());
                setUserEntity(userEntity);
            }
        }
    }

    class LogOutUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            sharedPrefManager.putStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            return null;
        }

        @Override
        protected void onPostExecute(Void loginEntity) {
            super.onPostExecute(loginEntity);
            tvName.setText("Welcome Guest");
            Toast.makeText(MainActivity.this, "Logout Success!!", Toast.LENGTH_SHORT).show();
            setUserEntity(null);
            loadHomeFragment();
        }
    }

    public void getTestData() {
        DatabaseReference classRef = database.getReference(Constants.DB_GROUP_CHAT).child("-M8Lanj7FiV5iwmsaPql");
        Query query = classRef.orderByKey().startAt("-M8M2gpNC-2r0JmIoewA");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatEntity> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                        list.add(chatEntity);
                        Logger.d(chatEntity.toString());
                    } catch (Exception e) {
                        Logger.d("Unable to parse");
                    }
                    Logger.d("List size : " + list.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class GetLatestUserKey extends AsyncTask<Void, Void, List<UserEntity>> {

        @Override
        protected List<UserEntity> doInBackground(Void... voids) {
            try {
                Logger.d("Fetching user list from room db");
                return DatabaseClient.getInstance(MainActivity.this).getAppDatabase().userDao().getAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<UserEntity> list) {
            super.onPostExecute(list);
            try {
                if (list != null && list.size() > 0) {
                    getAlluserAfterKey(list.get(list.size() - 1).getfId());
                } else {
                    getAlluserAfterKey("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class StoreUserList extends AsyncTask<Void, Void, Void> {
        List<UserEntity> list;

        StoreUserList(List<UserEntity> list) {
            this.list = list;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.d("Storing user list to local database" + list.size());
            DatabaseClient.getInstance(MainActivity.this).getAppDatabase().userDao().insertList(list);
            sharedPrefManager.putIntegerValueForKey(Constants.SHD_PRF_USER_MASTER, Utility.getServerVersionExcel(Constants.SHD_PRF_USER_MASTER));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getAlluserAfterKey(String lastKey) {
        Logger.d("Load user from firebase After :" + lastKey);
        DatabaseReference userRef = database.getReference(Constants.DB_USER);
        Query userQueru;
        if (lastKey.equals("")) {
            userQueru = userRef;
        } else {
            userQueru = userRef.orderByKey().startAt(lastKey);
        }

        userQueru.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserEntity> list = new ArrayList<>();
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserEntity chatEntity = snapshot.getValue(UserEntity.class);
                        list.add(chatEntity);
                        Logger.d(chatEntity.getfId());
                    }
                    if (list.size() > 0) {
                        new StoreUserList(list).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.d(databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
}
