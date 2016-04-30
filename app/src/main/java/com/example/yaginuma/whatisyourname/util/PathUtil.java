package com.example.yaginuma.whatisyourname.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by yaginuma on 16/04/29.
 */
public class PathUtil {
    public static String getPath(Context context, Uri uri) {
        String path = uri.toString();

        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);

        // add care of uri is not content
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(0);
            cursor.close();
        }

        return path;
    }

    public static Uri stringToUri(String str) {
        return Uri.parse(str);
    }
}
