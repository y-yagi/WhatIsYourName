package com.example.yaginuma.whatisyourname.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ActionMode;
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
import android.widget.Toast;

import com.example.yaginuma.whatisyourname.BuildConfig;
import com.example.yaginuma.whatisyourname.R;
import com.example.yaginuma.whatisyourname.model.Edict;
import com.example.yaginuma.whatisyourname.model.Label;
import com.example.yaginuma.whatisyourname.service.PhotoService;
import com.example.yaginuma.whatisyourname.service.ServiceGenerator;
import com.example.yaginuma.whatisyourname.service.TranslatorService;
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

        mProgressDialog = ProgressDialogBuilder.build(this, "Now Loading...");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }

        setSelectionAction();
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
        mProgressDialog.show();
        PhotoService service = ServiceGenerator.createService(
                PhotoService.class, BuildConfig.PHOTO_API_URL, BuildConfig.USERNAME, BuildConfig.PASSWORD);
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

    private void setSelectionAction() {
        TextView textView = (TextView) findViewById(R.id.text_detail);
        if (textView != null) {
            textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.getMenuInflater().inflate(R.menu.edit_text, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.action_toast) {
                        TextView textView = (TextView) findViewById(R.id.text_detail);
                        int selectionStart = textView.getSelectionStart();
                        int selectionEnd = textView.getSelectionEnd();
                        CharSequence selectedText = textView.getText().subSequence(selectionStart, selectionEnd);
                        translate(selectedText.toString());
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                }
            });
        }
    }

    private void translate(String word) {
      mProgressDialog.show();
      TranslatorService service = ServiceGenerator.createService(TranslatorService.class, BuildConfig.TRANSLATE_API_URL);
      Call<List<Edict>> call = service.edicts(word);
      call.enqueue(new Callback<List<Edict>>() {
          @Override
          public void onResponse(Call<List<Edict>> call,
                                 Response<List<Edict>> response) {
              mProgressDialog.dismiss();
              List<Edict> edicts = response.body();
              String detail = "";

              for (Edict edict: edicts) {
                  detail += edict.japanese + "(" + edict.japanese_yomi + ")" + "\n";
              }

              if (detail.isEmpty()) {
                  detail = "指定された単語は見つかりませんでした";
              }
              Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_LONG).show();

          }

          @Override
          public void onFailure(Call<List<Edict>> call, Throwable t) {
              mProgressDialog.dismiss();
              Toast.makeText(getApplicationContext(), "指定された単語は見つかりませんでした", Toast.LENGTH_LONG).show();
              Log.e("Upload error:", t.getMessage());
          }
      });
  }
}
