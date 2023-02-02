package tech.pixelw.castrender.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/4/28
 */
public class ApkUtil {
    public static void install(@NonNull Uri uri, Context context) {
        if (!"content".equals(uri.getScheme())) {
            throw new IllegalArgumentException("not supported uri while installing: " + uri.toString());
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            File file = getFileFromUri(uri, context);
            uri = Uri.fromFile(file);
        }
        Intent install = new Intent(Intent.ACTION_VIEW, uri);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(install);
    }

    private static File getFileFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        switch (uri.getScheme()) {
            case "content":
                return getFileFromContentUri(uri, context);
            case "file":
                return new File(uri.getPath());
            default:
                return null;
        }
    }

    private static File getFileFromContentUri(Uri contentUri, Context context) {
        if (contentUri == null) {
            return null;
        }
        File file = null;
        String filePath;
        String fileName;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
            cursor.close();
            if (!TextUtils.isEmpty(filePath)) {
                file = new File(filePath);
            }
            System.out.println(filePath);
//            if (!file.exists() || file.length() <= 0 || TextUtils.isEmpty(filePath)) {
//                filePath = getPathFromInputStreamUri(context, contentUri, fileName);
//            }
//            if (!TextUtils.isEmpty(filePath)) {
//                file = new File(filePath);
//            }
        }
        return file;
    }
}