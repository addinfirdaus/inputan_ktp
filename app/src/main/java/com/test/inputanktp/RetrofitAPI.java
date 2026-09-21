package com.test.inputanktp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @POST("api.php")
    Call<DataModal> createPost(@Body DataModal dataModal);

//    @GET("get_religion.php")
//    Call<List<AgamaModel>> getAgamaList();

    @GET
    Call<List<MasterModel>> getMasterData(@Url String url);
}
