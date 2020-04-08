package com.svrpublicschool.services;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.svrpublicschool.BuildConfig;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.SVRApplication;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.BannerModel;
import com.svrpublicschool.models.KeyValueModel;
import com.svrpublicschool.models.TestModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);

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

        OkHttpClient client = httpClientBuilder.addInterceptor(interceptor).build();

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

    public Observable<BannerModel> getBannerList(String url) {
        return svrServices.getBannerList(url)
                .flatMap((Function<BannerModel, ObservableSource<BannerModel>>) bannerModel ->
                {
                    storeBanner(bannerModel);
                    return Observable.just(bannerModel);
                });
    }

    private void storeUser(KeyValueModel keyValueModel) {
        getSharedPreference().putStringValueForKey(Constants.KEY_VALUE_STRING, new Gson().toJson(keyValueModel));
    }

    private void storeBanner(BannerModel bannerModel) {
        getSharedPreference().putStringValueForKey(Constants.BANNER, new Gson().toJson(bannerModel));
    }
}
