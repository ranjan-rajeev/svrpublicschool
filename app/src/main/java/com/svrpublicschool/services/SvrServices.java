package com.svrpublicschool.services;


import com.svrpublicschool.models.BannerModel;
import com.svrpublicschool.models.KeyValueModel;
import com.svrpublicschool.models.TestModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface SvrServices {

    @GET
    Observable<TestModel> getUsers(@Url String url);

    @GET
    Observable<KeyValueModel> getKeyValueStrings(@Url String url);

    @GET
    Observable<BannerModel> getBannerList(@Url String url);

}
