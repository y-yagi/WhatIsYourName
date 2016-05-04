package com.example.yaginuma.whatisyourname.service;

import com.example.yaginuma.whatisyourname.model.Edict;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yaginuma on 16/05/04.
 */
public interface TranslatorService {
    @GET("edicts")
    Call<List<Edict>> edicts(@Query("word") String word);
}
