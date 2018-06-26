package com.hxh.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageGetUtils
{
    static final int CODE_FROM_ALBUM = 100;
    static final int CODE_FROM_CAMERA = 200;
    static final int CODE_FROM_CROP = 300;

    public File cameraImgFile = null;
    public File cropImgFile = null;

    public void gotoCamera(Context context)
    {
        @SuppressLint("SimpleDateFormat")
        String fileName = "Camera/img_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        //设置保存拍摄照片路径(DCIM/Camera/img_20170212_122223.jpg)
        cameraImgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fileName);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //如果指定了目标uri，data=null(kotlin写的Activity中onActivityResult的Intent参数要写成data: Intent?)
        //如果没有指定uri，则data返回拍摄的照片的缩略图(bitmap)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getUriFromFile(context, cameraImgFile));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getImageContentUriCamera(context, cameraImgFile));
        ((Activity) context).startActivityForResult(intent, CODE_FROM_CAMERA);
    }

//        Uri uri;
//        if (Build.VERSION.SDK_INT < 24)
//        {
//            uri = Uri.fromFile(cameraImgFile);
//        }
//        else
//        {
//            uri = FileUtils.getImageContentUri(context, cameraImgFile);
//        }

    public void gotoAlbum(Context context)
    {
        ((Activity) context).startActivityForResult(new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI), CODE_FROM_ALBUM);
    }

    public void cropImg(Context context, File inputFile, boolean isSquare)
    {
        @SuppressLint("SimpleDateFormat")
        String fileName = "img_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        //设置保存路径名称
        cropImgFile = new File(context.getExternalFilesDir("crop"), fileName);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(FileUtils.getImageContentUri(context, inputFile), "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropImgFile));
        intent.putExtra("crop", "true");

        if (isSquare)
        {
            // 裁剪框比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            // 输出图片大小
            intent.putExtra("outputX", 240);
            intent.putExtra("outputY", 240);
        }
        else
        {
            // 裁剪框比例
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 2);

            // 输出图片大小
            intent.putExtra("outputX", 600);
            intent.putExtra("outputY", 400);
        }

        // 返回格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        ((Activity) context).startActivityForResult(intent, CODE_FROM_CROP);
    }

    public void refreshAlbum(Context context, String path)
    {
        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + path)));
    }
}
