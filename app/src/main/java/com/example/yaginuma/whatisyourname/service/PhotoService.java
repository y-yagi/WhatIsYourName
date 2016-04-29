package com.example.yaginuma.whatisyourname.service;

import com.example.yaginuma.whatisyourname.model.Label;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by yaginuma on 16/04/26.
 */
public interface PhotoService {
    @Multipart
    @POST("photo/info")
    Call<List<Label>> getInfoFromFile(@Part MultipartBody.Part file);
}
