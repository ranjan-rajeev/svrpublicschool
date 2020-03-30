package com.svrpublicschool.services;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.svrpublicschool.BuildConfig;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.SvrApplication;
import com.svrpublicschool.models.PostModel;
import com.svrpublicschool.models.TestModel;
import com.svrpublicschool.models.UserModel;
import com.svrpublicschool.util.Constants;
import com.svrpublicschool.util.Logger;
import com.svrpublicschool.util.Utility;

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
    private LoanAssistServices loanAssistServices;

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

        loanAssistServices = retrofit.create(LoanAssistServices.class);

//        Retrofit middleLayerRetrofit = new Retrofit.Builder()
//                .baseUrl(CliqApplication.getInstance().environmentManager.getMiddleLayerHost() + "/")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        middleLayerService = middleLayerRetrofit.create(MiddleLayerService.class);
//
//        Retrofit juspayRetrofit = new Retrofit.Builder()
//                .baseUrl(CliqApplication.getInstance().environmentManager.getJustPayHost() + "/")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        justPayServices = juspayRetrofit.create(JustPayServices.class);
//
//        OkHttpClient.Builder stripeHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
//                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
//                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);
//
//        stripeHttpClient.addInterceptor(chain -> {
//            Request original = chain.request();
//            Request.Builder requestBuilder = original.newBuilder();
//            requestBuilder.addHeader("Authorization", "Bearer " + BuildConfig.STRIPE_API_KEY);
//            requestBuilder.method(original.method(), original.body());
//            Request request = requestBuilder.build();
//            return chain.proceed(request);
//        });
//
//        OkHttpClient stripeClient = stripeHttpClient.addInterceptor(interceptor).build();
//
//        Retrofit stripeRetrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.STRIPE_BASE_URL + "/")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(stripeClient)
//                .build();
//
//        stripeServices = stripeRetrofit.create(StripeServices.class);
//
//        Retrofit msdRetrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.MSD_RECOMMENDATION_HOST)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        msdServices = msdRetrofit.create(MsdServices.class);
//
//        Retrofit flixMediaRetrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.FLIX_MEDIA_HOST)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//        flixMediaServices = flixMediaRetrofit.create(FlixMediaServices.class);
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
        return SvrApplication.getContext();
    }

    private SharedPrefManager getSharedPreference() {
        return SharedPrefManager.getInstance(getContext());
    }

    private String getTagName() {
        return HttpService.class.getSimpleName();
    }

    public Observable<UserModel> getAppUser() {
        String appUser = getSharedPreference().getStringValueForKey(Constants.PREF_APP_USER, "");
        Observable<UserModel> userModelObservable;
        Logger.d(getTagName(), "saved app access token = " + appUser);
        if (TextUtils.isEmpty(appUser)) {
            userModelObservable = loanAssistServices.getUserDetails(1
            ).flatMap((Function<UserModel, ObservableSource<UserModel>>) user -> {
                // Storing app user on api response
                storeUser(user);
                return Observable.just(user);
            });
        } else {
            userModelObservable = Observable.just(new Gson().fromJson(appUser, UserModel.class));
        }
        return userModelObservable;
    }

    private void storeUser(UserModel user) {
        Logger.d(getTagName(), "Saving user  :  " + new Gson().toJson(user));
        getSharedPreference().putStringValueForKey(Constants.PREF_APP_USER, new Gson().toJson(user));
    }

    public Observable<UserModel> getUserList() {
        Observable<List<UserModel>> userModelObservable;
        userModelObservable = loanAssistServices.getUserList()
                .flatMap((Function<List<UserModel>, ObservableSource<List<UserModel>>>) users -> Observable.just(users));
        return userModelObservable.flatMapIterable(item -> item)
                .map(userModel -> {
                    userModel.setName("My Name is : " + userModel.getName() + " and my phone No  is  :" + userModel.getPhone());
                    return userModel;
                });
    }

    public Observable<PostModel> getPosts() {
//        Observable<List<UserModel>> userModelObservable;
//        userModelObservable = loanAssistServices.getUserList()
//                .flatMap((Function<List<UserModel>, ObservableSource<List<UserModel>>>) users -> Observable.just(users));
//        Observable<UserModel> modelObservable = userModelObservable.flatMapIterable(item -> item);
//        return modelObservable.flatMap(userModel -> loanAssistServices.getPostList(userModel.getId())
//                .flatMap((Function<List<PostModel>, ObservableSource<List<PostModel>>>) list -> Observable.just(list))
//                .flatMapIterable(item -> item));

        return loanAssistServices.getUserList()
                .flatMap((Function<List<UserModel>, ObservableSource<List<UserModel>>>) users -> Observable.just(users))
                .flatMapIterable(item -> item)
                .flatMap(userModel -> loanAssistServices.getPostList(userModel.getId())
                        .flatMap((Function<List<PostModel>, ObservableSource<List<PostModel>>>) list -> Observable.just(list))
                        .flatMapIterable(item -> item));
    }

    public Observable<TestModel.Sheet1Model> getTempUsers(String url) {
        return loanAssistServices.getUsers(url)
                .flatMap((Function<TestModel, ObservableSource<TestModel>>) users -> Observable.just(users))
                .flatMapIterable(item -> item.getSheet1());
    }

}
