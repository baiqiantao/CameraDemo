package com.bqt.camerademo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ListActivity {
	public static final String fileSavePath = Environment.getExternalStorageDirectory().getPath() + File.separator;
	public static final String tempFileSavePath = fileSavePath + "temp.png";
	public static final String tempCropFileSavePath = fileSavePath + "temp_crop.png";
	public static final int REQUEST_CODE_0 = 1100;
	public static final int REQUEST_CODE_1 = 1101;
	public static final int REQUEST_CODE_11 = 1111;
	public static final int REQUEST_CODE_12 = 1112;
	public static final int REQUEST_CODE_4 = 1104;
	public static final int REQUEST_CODE_5 = 1105;
	private TextView tv;
	private ImageView iv;
	private boolean isCrop;
	private boolean isSaveToUri;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"使用相机拍照并获取返回的照片\n注意返回的并非原图，照片会自动被压缩", //
				"使用相机拍照并将照片保存在指定的位置\n注意这时在指定位置保存的是原图",//
				"调用系统图库APP的裁剪图片功能，将图片裁剪为指定大小\n将裁剪后的图片通过onActivityResult返回",//
				"调用系统图库APP的裁剪图片功能，将图片裁剪为指定大小\n将裁剪后的照片保存在指定的位置",//
				"从系统图库APP中获取图片，这里获取的是原图",//
				"从系统图库APP中获取图片，并将照片裁剪为指定大小\n将裁剪后的图片通过onActivityResult返回",//
				"从系统图库APP中获取图片，并将照片裁剪为指定大小\n将裁剪后的照片保存在指定的位置",};
		for (int i = 0; i < array.length; i++) {
			array[i] = i + "、" + array[i];
		}
		iv = new ImageView(this);
		//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		getListView().addFooterView(iv);
		tv = new TextView(this);
		tv.setTextColor(Color.BLUE);
		getListView().addFooterView(tv);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
			case 1:
			case 2:
			case 3:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用系统相机拍照
				if (position == 0) startActivityForResult(intent, REQUEST_CODE_0);
				else {//将返回的数据保存到指定位置
					isCrop = (position >= 2);//裁剪
					isSaveToUri = (position == 3);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempFileSavePath)));
					startActivityForResult(intent, REQUEST_CODE_1);
				}
				break;
			case 4:
			case 5:
			case 6:
				isCrop = (position >= 5);//裁剪
				Intent intent2 = new Intent(Intent.ACTION_PICK);
				intent2.setType("image/*");
				if (position == 4) startActivityForResult(intent2, REQUEST_CODE_4);
				else {
					isSaveToUri = (position == 6);
					intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempFileSavePath)));
					startActivityForResult(intent2, REQUEST_CODE_5);
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {//0
			Toast.makeText(this, "结果码：" + requestCode + "   RESULT_CANCELED", Toast.LENGTH_SHORT).show();
			return;
		} else if (resultCode == RESULT_OK) {//-1
			Toast.makeText(this, "结果码：" + requestCode + "   RESULT_OK", Toast.LENGTH_SHORT).show();
		}
		//
		//*******************************************************************************************************************
		//
		switch (requestCode) {
			case REQUEST_CODE_0:// 注意，返回的并非原图，照片会自动被压缩！
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					tv.append("返回的Bitmap的尺寸为" + bitmap.getWidth() + " * " + bitmap.getHeight() + "\n");
					iv.setImageBitmap(bitmap);
				}
				break;
			case REQUEST_CODE_1://注意，这里返回的data为null，这时在指定位置保存的是原图
				tv.append("已将Bitmap保存到指定位置，此时 data == null : " + (data == null) + "\n");
				Bitmap bitmap1 = BitmapFactory.decodeFile(tempFileSavePath);
				if (bitmap1 != null) {
					iv.setImageBitmap(bitmap1);
					tv.append("裁剪前的Bitmap的尺寸为" + bitmap1.getWidth() + " * " + bitmap1.getHeight() + "\n");
					if (isCrop) {
						Uri fromUri = Uri.fromFile(new File(tempFileSavePath));
						if (!isSaveToUri) cropImage(this, REQUEST_CODE_11, fromUri);
						else {
							Uri toUri = Uri.fromFile(new File(tempCropFileSavePath));
							cropImage(this, REQUEST_CODE_12, fromUri, toUri);
						}
					}
				}
				break;
			//
			//*******************************************************************************************************************
			//
			case REQUEST_CODE_4:
			case REQUEST_CODE_5:
				try {
					Uri fromUri = data.getData();
					Bitmap bitmap4 = MediaStore.Images.Media.getBitmap(getContentResolver(), fromUri);
					if (bitmap4 != null) {
						tv.append("返回的Bitmap的尺寸为" + bitmap4.getWidth() + " * " + bitmap4.getHeight() + "\n");
						if (!isCrop) iv.setImageBitmap(bitmap4);
						else {
							if (!isSaveToUri) cropImage(this, REQUEST_CODE_11, fromUri);
							else {
								Uri toUri = Uri.fromFile(new File(tempCropFileSavePath));
								cropImage(this, REQUEST_CODE_12, fromUri, toUri);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			//
			//*******************************************************************************************************************
			//
			case REQUEST_CODE_11: // 将裁剪后的图片通过onActivityResult中的Intent返回
				Bitmap bitmap5 = (Bitmap) data.getParcelableExtra("data");
				if (bitmap5 != null) {
					tv.append("裁剪后的Bitmap的尺寸为" + bitmap5.getWidth() + " * " + bitmap5.getHeight() + "\n");
					iv.setImageBitmap(bitmap5);
				}
				break;
			case REQUEST_CODE_12: //将裁剪后的图片通过指定的Uri返回
				try {
					Uri tempCropFileUri = Uri.fromFile(new File(tempCropFileSavePath));
					Bitmap bitmap6 = MediaStore.Images.Media.getBitmap(getContentResolver(), tempCropFileUri);
					tv.append("指定Uri后裁剪后的尺寸为" + bitmap6.getWidth() + " * " + bitmap6.getHeight() + "\n");
					iv.setImageBitmap(bitmap6);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	/**
	 * 调用系统图库APP的裁剪图片功能，将指定Uri中的图片裁剪为指定大小
	 */
	public static void cropImage(Activity activity, int requestCode, Uri fromUri) {
		cropImage(activity, requestCode, fromUri, 144, 144, null);
	}

	/**
	 * 将裁剪后的照片保存在指定的位置
	 */
	public static void cropImage(Activity activity, int requestCode, Uri fromUri, Uri toUri) {
		cropImage(activity, requestCode, fromUri, 144, 144, toUri);
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