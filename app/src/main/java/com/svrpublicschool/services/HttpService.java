package com.svrpublicschool.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.svrpublicschool.BuildConfig;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.SVRApplication;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.BooksResponse;
import com.svrpublicschool.models.DashBoardResponse;
import com.svrpublicschool.models.DatabaseVesionModel;
import com.svrpublicschool.models.FacultyResponse;
import com.svrpublicschool.models.KeyValueModel;
import com.svrpublicschool.models.YoutubeMetaDataEntity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpService {
    private static HttpService instance = new HttpService();
    public Retrofit retrofit;
    private SvrServices svrServices;

    private HttpService() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        long TIMEOUT = 60L;

        File httpCacheDirectory = new File(getContext().getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                if (Utility.isNetworkConnected(getContext())) {
                    int maxAge = 60; // read from cache for 1 minute
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
            }
        };

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .cache(cache)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);

        httpClientBuilder.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        httpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            requestBuilder.addHeader("appversion", Utility.getPackageInfo(getContext()).versionName);
            requestBuilder.addHeader("appplatform", "android");
            requestBuilder.method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(BuildConfig.IS_IN_DEBUG_MODE ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);// todo : change it to none

        OkHttpClient client = httpClientBuilder

                .addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder().baseUrl(BuildConfig.HOST + "/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();

        svrServices = retrofit.create(SvrServices.class);
    }

    public static HttpService getInstance() {
        if (instance == null) {
            instance = getSyncedInstance();
        }
        return instance;
    }

    private static synchronized HttpService getSyncedInstance() {
        if (instance == null) {
            instance = new HttpService();
        }
        return instance;
    }

    public Context getContext() {
        return SVRApplication.getContext();
    }

    private SharedPrefManager getSharedPreference() {
        return SharedPrefManager.getInstance(getContext());
    }

    private String getTagName() {
        return HttpService.class.getSimpleName();
    }


    public Observable<KeyValueModel> getStringList(String url) {
        return svrServices.getKeyValueStrings(url)
                .flatMap((Function<KeyValueModel, ObservableSource<KeyValueModel>>) keyValueModel ->
                {
                    storeUser(keyValueModel);
                    return Observable.just(keyValueModel);
                });
    }

    public Observable<DatabaseVesionModel> getDbVersionExcel(String url) {
        return svrServices.getDbVersion(url)
                .flatMap((Function<DatabaseVesionModel, ObservableSource<DatabaseVesionModel>>) keyValueModel ->
                {
                    getSharedPreference().putStringValueForKey(Constants.SHD_PRF_VERSION_EXCEL, new Gson().toJson(keyValueModel));
                    return Observable.just(keyValueModel);
                });
    }

    public Observable<FacultyResponse> getFaculty(String url) {
        return svrServices.getFacultyList(url)
                .flatMap((Function<FacultyResponse, ObservableSource<FacultyResponse>>) keyValueModel ->
                {
                    getSharedPreference().putIntegerValueForKey(Constants.SHD_PRF_FACULTY_VERSION, Utility.getServerVersionExcel(Constants.SHD_PRF_FACULTY_VERSION));
                    return Observable.just(keyValueModel);
                });
    }

    public Observable<YoutubeMetaDataEntity> getYoutubeMetaData(String url) {
        return svrServices.getYoutubeMetaData(url)
                .flatMap((Function<YoutubeMetaDataEntity, ObservableSource<YoutubeMetaDataEntity>>) bannerModel ->
                {
                    return Observable.just(bannerModel);
                });
    }


    public Observable<BooksResponse> getBooks(String url) {
        return svrServices.getBooks(url)
                .flatMap((Function<BooksResponse, ObservableSource<BooksResponse>>) booksResponse ->
                {
                    getSharedPreference().putIntegerValueForKey(Constants.SHD_PRF_BOOKS_VERSION, Utility.getServerVersionExcel(Constants.SHD_PRF_BOOKS_VERSION));
                    return Observable.just(booksResponse);
                });
    }

    public Observable<DashBoardResponse> getDashBoard(String url) {
        return svrServices.getDashBoard(url)
                .flatMap((Function<DashBoardResponse, ObservableSource<DashBoardResponse>>) booksResponse ->
                {
                    getSharedPreference().putIntegerValueForKey(Constants.SHD_PRF_DASHBOARD_VERSION, Utility.getServerVersionExcel(Constants.SHD_PRF_DASHBOARD_VERSION));
                    return Observable.just(booksResponse);
                });
    }


    private void storeUser(KeyValueModel keyValueModel) {
        getSharedPreference().putStringValueForKey(Constants.KEY_VALUE_STRING, new Gson().toJson(keyValueModel));
    }

}
