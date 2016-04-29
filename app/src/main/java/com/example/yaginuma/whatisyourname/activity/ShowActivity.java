package com.example.yaginuma.whatisyourname.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yaginuma.whatisyourname.R;
import com.example.yaginuma.whatisyourname.model.Label;
import com.example.yaginuma.whatisyourname.service.PhotoService;
import com.example.yaginuma.whatisyourname.service.ServiceGenerator;
import com.example.yaginuma.whatisyourname.util.PathUtil;
import com.example.yaginuma.whatisyourname.widget.ProgressDialogBuilder;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowActivity extends AppCompatActivity {

    private Uri mImageUri;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }
        getImageInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void handleSendImage(Intent intent) {
        mImageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setImageURI(mImageUri);
    }

    private void getImageInfo() {
        mProgressDialog = ProgressDialogBuilder.build(this, "Now Loading...");
        mProgressDialog.show();
        PhotoService service = ServiceGenerator.createService(PhotoService.class);
        File file = new File(PathUtil.getPath(this, mImageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<List<Label>> call = service.getInfoFromFile(body);
        call.enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call,
                                   Response<List<Label>> response) {
                mProgressDialog.dismiss();
                List<Label> labels = response.body();
                TextView detailView = (TextView) findViewById(R.id.text_detail);
                String detail = "";

                for (Label label : labels) {
                    detail += label.toSentence() + "\n";
                }

                if (detailView != null) {
                    detailView.setText(detail);
                }
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
