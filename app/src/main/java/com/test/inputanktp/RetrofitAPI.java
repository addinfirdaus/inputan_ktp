package com.test.inputanktp;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @Multipart
    @POST("api.php")
    Call<DataModal> createPostWithImage(
            @Part("nik") RequestBody nik,
            @Part("nama") RequestBody nama,
            @Part("alamat") RequestBody alamat,
            @Part("til") RequestBody til,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part("agama") RequestBody agama,
            @Part("negara") RequestBody negara,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("satatus") RequestBody satatus,
            @Part("masa_berlaku") RequestBody masa_berlaku,
            @Part MultipartBody.Part gambar
    );
    @GET
    Call<List<MasterModel>> getMasterData(@Url String url);
    @GET("api.php")
    Call<List<DataModal>> getKtpData();
    @DELETE("api.php")
    Call<DataModal> deleteKtpData(@Query("id") String id);

    // Jika update menggunakan PUT (atau POST dengan parameter ID tergantung kebutuhan PHP Anda)
    @PUT("api.php")
    Call<DataModal> updateKtpData(@Query("id") String id, @retrofit2.http.Body DataModal data);

}
