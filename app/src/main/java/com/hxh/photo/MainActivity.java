package com.hxh.photo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

import static com.hxh.photo.ImageGetUtils.CODE_FROM_ALBUM;
import static com.hxh.photo.ImageGetUtils.CODE_FROM_CAMERA;
import static com.hxh.photo.ImageGetUtils.CODE_FROM_CROP;

public class MainActivity extends AppCompatActivity
{
    private Context mContext = this;

    private ImageView iv;

    private ImageGetUtils imageGetUtils = new ImageGetUtils();

    private boolean isCrop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);

        getPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("onActivityResult", "requestCode=" + requestCode + "-resultCode=" + resultCode + "-data=" + data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == CODE_FROM_CAMERA && !isCrop)
            {
                imageGetUtils.refreshAlbum(mContext, imageGetUtils.cameraImgFile.toString());

                Glide
                        .with(mContext)
                        .load(imageGetUtils.cameraImgFile)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(iv);
            }
            else if (requestCode == CODE_FROM_ALBUM && !isCrop)
            {
                Glide
                        .with(mContext)
                        .load(FileUtils.getFileFromUri(mContext, data.getData()))
                        .placeholder(R.mipmap.ic_launcher)
                        .into(iv);
            }
            else if (requestCode == CODE_FROM_CAMERA && isCrop)
            {
                imageGetUtils.refreshAlbum(mContext, imageGetUtils.cameraImgFile.toString());

                imageGetUtils.cropImg(mContext, imageGetUtils.cameraImgFile, true);
            }
            else if (requestCode == CODE_FROM_ALBUM && isCrop)
            {
                imageGetUtils.cropImg(mContext, FileUtils.getFileFromUri(mContext, data.getData()), false);
            }
            else if (requestCode == CODE_FROM_CROP)
            {
                imageGetUtils.refreshAlbum(mContext, imageGetUtils.cropImgFile.toString());

                Glide
                        .with(mContext)
                        .load(imageGetUtils.cropImgFile)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(iv);
            }

//            Bitmap bitmap = data.getParcelableExtra("data")
//
//            FileOutputStream out =new FileOutputStream(imageGetUtils.cameraImgFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//
//            out.flush();
//            out.close();
//
//            imageGetUtils.refreshAlbum(mContext, imageGetUtils.cameraImgFile.toString());
        }
    }

    public void camera(View v)
    {
        isCrop = false;

        imageGetUtils.gotoCamera(mContext);
    }

    public void album(View v)
    {
        isCrop = false;

        imageGetUtils.gotoAlbum(mContext);
    }

    public void cc(View v)
    {
        isCrop = true;

        imageGetUtils.gotoCamera(mContext);
    }

    public void ca(View v)
    {
        isCrop = true;

        imageGetUtils.gotoAlbum(mContext);
    }

    @SuppressLint("CheckResult")
    private void getPermission()
    {
        //监听具体的某一个权限是否进行了授权
        new RxPermissions(this)
                .requestEach( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>()
                {
                    @Override
                    public void accept(Permission permission)
                    {
                        if (permission.granted)
                        {
                            //用户已经同意权限
                            //执行操作

                        }
                        else if (permission.shouldShowRequestPermissionRationale)
                        {
                            //用户拒绝了该权限，没有选中『不再询问』,再次启动时，还会提示请求权限的对话框
//                            Toast.makeText(mContext, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, "No permission no work", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                        else
                        {
                            //用户拒绝了该权限，并且选中『不再询问』
                            //启动系统权限设置界面
//                            Toast.makeText(mContext, "在该页面中点击“权限”进入，开启“文件”权限", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);

                            finish();
                        }
                    }
                });
    }
}
