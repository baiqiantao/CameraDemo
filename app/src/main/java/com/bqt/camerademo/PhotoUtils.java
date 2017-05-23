package com.bqt.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

public class PhotoUtils {

	public static final String tempFileSavePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "temp.png";

	//**************************************************************************************************************************
	//                                                                                          使用相机拍照
	//**************************************************************************************************************************

	/**
	 * 使用相机拍照并获取返回的照片
	 * 注意返回的并非原图，照片会自动被压缩
	 */
	public static void startCamera(Activity activity, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用系统相机拍照
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 使用相机拍照并将照片保存在指定的位置
	 * 注意这时在指定位置保存的是原图
	 */
	public static void startCameraAndCrop(Activity activity, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用系统相机拍照
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempFileSavePath)));//将返回的数据保存到指定位置
		activity.startActivityForResult(intent, requestCode);
	}

	//**************************************************************************************************************************
	//                                                                                         从相册中获取图片
	//**************************************************************************************************************************

	public static void startAlbum(Activity activity, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK);
//		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		intent.setType("image/*");//视频【"video/*"】
		activity.startActivityForResult(intent, requestCode);
	}

	//**************************************************************************************************************************
	//                                                                                          裁剪图片
	//**************************************************************************************************************************

	/**
	 * 调用系统图库APP的裁剪图片功能，将指定Uri中的图片裁剪为指定大小
	 */
	public static void cropImage(Activity activity, int requestCode, Uri fromUri) {
		cropImage(activity, requestCode, fromUri, 288, 288, null);
	}

	/**
	 * 将裁剪后的照片保存在指定的位置
	 * 注意一个大bug，当指定toUri时，设置的裁剪大小是无效的。
	 */
	public static void cropImage(Activity activity, int requestCode, Uri fromUri, Uri toUri) {
		cropImage(activity, requestCode, fromUri, 288, 288, toUri);
	}

	/**
	 * 调用系统图库APP的裁剪图片功能，将指定图片裁剪为指定大小
	 */
	public static void cropImage(Activity activity, int requestCode, Uri fromUri, int desWidth, int desHeight, Uri toUri) {
		if (fromUri == null) return;
		//指定action是使用系统图库裁剪图片
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromUri, "image/*");
		//设置在开启的Intent中，显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		//设置裁剪比例及裁剪图片的具体宽高
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", desWidth);
		intent.putExtra("outputY", desHeight);
		//设置是否允许拉伸
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		//设置输出格式
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		//设置是否需要人脸识别，默认不需要设置
		intent.putExtra("noFaceDetection", true);
		//设置是否返回数据
		if (toUri == null) {
			//如果设置为true，那么便不会在给定的uri中获取到裁剪的图片，这里是将数据返回到了onActivityResult中的intent中了
			intent.putExtra("return-data", true);
		} else {
			//如果要在给定的uri中返回图片，则必须设置为false。注意：要防止裁减前图片的uri与保存时图片的uri相同
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, toUri);
		}
		activity.startActivityForResult(intent, requestCode);
	}
}