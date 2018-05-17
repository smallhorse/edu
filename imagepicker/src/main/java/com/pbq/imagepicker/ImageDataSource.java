package com.pbq.imagepicker;

import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.pbq.imagepicker.bean.ImageFolder;
import com.pbq.imagepicker.bean.ImageItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：加载手机图片实现类  异步加载数据 数据改变数重新更新
 * 修订历史：
 * ================================================
 */
public class ImageDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ImageDataSource.class.getSimpleName();

    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608

    private FragmentActivity activity;
    private OnImagesLoadedListener loadedListener;                     //图片加载完成的回调接口
    private ArrayList<ImageFolder> imageFolders = new ArrayList<>();   //所有的图片文件夹

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有图片
     * @param loadedListener 图片加载完成的监听
     */
    public ImageDataSource(FragmentActivity activity, String path, OnImagesLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;

        //得到LoaderManager对象
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        //初始化loader
        if (path == null) {
            loaderManager.initLoader(Constant.LOADER_IMAGE_ALL, null, this);//加载所有的图片
        } else {
            //加载指定目录的图片
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(Constant.LOADER_IMAGE_CATEGORY, bundle, this);
        }
    }

    /**
     * 指定ID不存在 触发该方法返回一个新的loader对象
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        //扫描所有图片
        //查询ContentResolver并返回一个Cursor对象

        if (id == Constant.LOADER_IMAGE_ALL){
            //cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC");
            String selection = IMAGE_PROJECTION[5] + " like '%image/%' or " + IMAGE_PROJECTION[5] + " like '%video/%'";

            Log.d("ImageDataSource","selection = " + MediaStore.Files.getContentUri("external"));
            Log.d("ImageDataSource","selection = " + selection + " /" + MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, selection, null, IMAGE_PROJECTION[6] + " DESC");
        }
        //扫描某个图片文件夹
        if (id == Constant.LOADER_IMAGE_CATEGORY){
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[6] + " DESC");
        }

        return cursorLoader;
    }

    /**
     * 完成对UI界面的更新
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished = ");
        imageFolders.clear();
        if (data != null) {
            ArrayList<ImageItem> allImages = new ArrayList<>();   //所有图片的集合,不分文件夹
            while (data.moveToNext()) {
                //查询数据
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                String imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                long imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));

                File imageFile = new File(imagePath);
                if(!imageFile.exists()){
                    Log.d(TAG,"imagePath = " + imagePath + "    imageFile.exists() = " + imageFile.exists() + "/");
                    continue;
                }

                //封装实体
                ImageItem imageItem = new ImageItem();
                imageItem.name = imageName;
                imageItem.path = imagePath;
                imageItem.size = imageSize;
                imageItem.width = imageWidth;
                imageItem.height = imageHeight;
                imageItem.mimeType = imageMimeType;
                imageItem.addTime = imageAddTime;
                allImages.add(imageItem);

                //根据父路径分类存放图片
                //根据图片的路径获取到图片所在文件夹的路径和名称

                File imageParentFile = imageFile.getParentFile();
                ImageFolder imageFolder = new ImageFolder();
                imageFolder.name = imageParentFile.getName();
                imageFolder.path = imageParentFile.getAbsolutePath();

                //判断这个文件夹是否已经存在  如果存在直接添加图片进去  否则将文件夹添加到文件夹的集合中
                if (!imageFolders.contains(imageFolder)) {
                    ArrayList<ImageItem> images = new ArrayList<>();
                    images.add(imageItem);
                    //缩略图
                    imageFolder.cover = imageItem;
                    imageFolder.images = images;
                    imageFolders.add(imageFolder);
                } else {
                    imageFolders.get(imageFolders.indexOf(imageFolder)).images.add(imageItem);
                }
            }

            //防止没有图片报异常
            if (data.getCount() > 0) {
                //构造所有图片的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.name = activity.getResources().getString(R.string.all_images_videos);
                allImagesFolder.path = "/";
                //把第一张设置缩略图
                allImagesFolder.cover = allImages.get(0);
                allImagesFolder.images = allImages;
                imageFolders.add(0, allImagesFolder);  //确保第一条是所有图片
            }
        }

        //回调接口，通知图片数据准备完成
        ImagePicker.getInstance().setImageFolders(imageFolders);
        loadedListener.onImagesLoaded(imageFolders);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }

    /** 所有图片加载完成的回调接口 */
    public interface OnImagesLoadedListener {
        void onImagesLoaded(List<ImageFolder> imageFolders);
    }
}