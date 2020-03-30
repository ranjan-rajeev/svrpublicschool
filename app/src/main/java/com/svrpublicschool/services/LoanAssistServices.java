package com.svrpublicschool.services;



import com.svrpublicschool.models.PostModel;
import com.svrpublicschool.models.TestModel;
import com.svrpublicschool.models.UserModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface LoanAssistServices {

    @GET("users")
    Observable<List<UserModel>> getUserList();

    @GET("users/{id}")
    Observable<UserModel> getUserDetails(@Path("id") int id);

    @GET("posts")
    Observable<List<PostModel>> getPostList(@Query("userId") int userId);

    @GET
    Observable<TestModel> getUsers(@Url String url);
}
