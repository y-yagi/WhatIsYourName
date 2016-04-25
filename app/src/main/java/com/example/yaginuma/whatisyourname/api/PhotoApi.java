package com.example.yaginuma.whatisyourname.api;

import android.net.Uri;
import android.util.Log;

import com.example.yaginuma.whatisyourname.service.PhotoService;
import com.example.yaginuma.whatisyourname.service.ServiceGenerator;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yaginuma on 16/04/26.
 */
public class PhotoApi {
    public void getInfoFromFile(Uri fileUri) {
        PhotoService service = ServiceGenerator.createService(PhotoService.class);
        File file = new File(fileUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<ResponseBody> call = service.getInfoFromFile(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
