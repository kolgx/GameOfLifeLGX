package com.mindle.gameoflife;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContentResolverCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.InputStream;

public class MyUtils {

    public static int[][] parseJsonArray(String jsonString) throws JSONException {
        int[][] matrix = null;

        JSONArray jsonArray = new JSONArray(jsonString);
        int rowNum = jsonArray.length();
        matrix = new int[rowNum][rowNum];
        for (int row = 0; row < rowNum; row++) {
            JSONArray colJSONArray = jsonArray.getJSONArray(row);
            int colNum = colJSONArray.length();
            if (colNum != rowNum) {
                return null;
            }
            for (int col = 0; col < colNum; col++) {
                matrix[row][col] = colJSONArray.getInt(col);
            }
        }
        return matrix;
    }

    public static String getFileName(ContentResolver cr, Uri uri) {
        if ("content".equals(uri.getScheme())) {
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor metaCursor = ContentResolverCompat.query(cr, uri, projection, null, null, null, null);
            if (metaCursor != null) {
                try {
                    if (metaCursor.moveToFirst()) {
                        return metaCursor.getString(0);
                    }
                } finally {
                    metaCursor.close();
                }
            }
        }
        return uri.getLastPathSegment();
    }

    public static void closeSilently(@Nullable Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String readString(@NonNull InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, "utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
