package com.androidcorpo.lindapp.network;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.network.response.PublicKeyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/lindapp/read.php")
    Call<PublicKeyResponse> read(@Query(Constant.QUERY_PARAM_CONTACT) String contact);

    @GET("/lindapp/create.php")
    Call<PublicKeyResponse> create(@Query(Constant.QUERY_PARAM_CONTACT)String contact, @Query(Constant.QUERY_PARAM_PUBLIC_KEY) String publicKey);

}

