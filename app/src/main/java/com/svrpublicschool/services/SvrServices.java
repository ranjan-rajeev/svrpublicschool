package com.svrpublicschool.services;


import com.svrpublicschool.models.BooksResponse;
import com.svrpublicschool.models.DashBoardResponse;
import com.svrpublicschool.models.DatabaseVesionModel;
import com.svrpublicschool.models.FacultyResponse;
import com.svrpublicschool.models.KeyValueModel;
import com.svrpublicschool.models.TestModel;
import com.svrpublicschool.models.YoutubeMetaDataEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface SvrServices {

    @GET
    Observable<TestModel> getUsers(@Url String url);

    @GET
    Observable<KeyValueModel> getKeyValueStrings(@Url String url);

    @GET
    Observable<DatabaseVesionModel> getDbVersion(@Url String url);

    @GET
    Observable<DashBoardResponse> getDashBoard(@Url String url);

    @GET
    Observable<BooksResponse> getBooks(@Url String url);

    @GET
    Observable<FacultyResponse> getFacultyList(@Url String url);

    @GET
    Observable<YoutubeMetaDataEntity> getYoutubeMetaData(@Url String url);

}
