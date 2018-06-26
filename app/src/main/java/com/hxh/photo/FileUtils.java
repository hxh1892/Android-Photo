package com.hxh.photo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

public class FileUtils
{
    public static Uri getUriFromFile(Context context, File file)
    {
        Uri uri;

        if (Build.VERSION.SDK_INT >= 24)
        {
            uri = FileProvider.getUriForFile(context, "com.hxh.photo.provider.FileProviderPicture", file);
        }
        else
        {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    public static Uri getImageContentUri(Context context, File imageFile)
    {
        String filePath = imageFile.getAbsolutePath();

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{filePath}, (String)null);

        if(cursor != null && cursor.moveToFirst())
        {
            int values1 = cursor.getInt(cursor.getColumnIndex("_id"));
            Uri baseUri = Uri.parse("content://media/external/images/media");

            return Uri.withAppendedPath(baseUri, "" + values1);
        }
        else if(imageFile.exists())
        {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);

            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        else
        {
            return null;
        }
    }

    public static Uri getImageContentUriCamera(Context context, File imageFile)
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static File getFileFromUri(Context context, Uri uri)
    {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        File picturePath = new File(cursor.getString(columnIndex));

        cursor.close();

        return picturePath;
    }
}
