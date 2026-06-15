package com.rao.multiaccountcredentials.comman;

import com.rao.multiaccountcredentials.Models.RegistrationResponse;
import com.rao.multiaccountcredentials.Models.ResetImagesResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/verify")
    Call<VerificationResult> verifyFaces(
            @Part MultipartBody.Part image,
            @Part("username") RequestBody username
    );
    @Multipart
    @POST("/register")
    Call<RegistrationResponse> registerUser(
            @Part("username") RequestBody username,
            @Part("full_name") RequestBody fullName,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part[] images
    );

    @Multipart
    @POST("/reset_images")
    Call<ResetImagesResponse> resetImages(
            @Part("username") RequestBody userId,
            @Part MultipartBody.Part[] images
    );
}

