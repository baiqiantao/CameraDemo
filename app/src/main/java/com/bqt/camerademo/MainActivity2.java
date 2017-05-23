package com.bqt.camerademo;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
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

public class MainActivity2 extends ListActivity {
	public static final String fileSavePath = Environment.getExternalStorageDirectory().getPath() + File.separator;
	public static final String tempFileSavePath = fileSavePath + "temp.png";
	public static final String tempCropFileSavePath = fileSavePath + "temp_crop.png";
	public static final int REQUEST_CODE_0 = 1100;
	public static final int REQUEST_CODE_1 = 1101;
	public static final int REQUEST_CODE_2 = 1102;
	public static final int REQUEST_CODE_3 = 1103;
	public static final int REQUEST_CODE_4 = 1104;
	public static final int REQUEST_CODE_5 = 1105;
	public static final int REQUEST_CODE_6 = 1106;
	public static final int REQUEST_CODE_11 = 1111;
	public static final int REQUEST_CODE_12 = 1112;
	private TextView tv;
	private ImageView iv;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int size = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		Toast.makeText(this, "内存 " + size, Toast.LENGTH_SHORT).show();

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
				PhotoUtils.startCamera(this, REQUEST_CODE_0);
				break;
			case 1:
				PhotoUtils.startCameraAndCrop(this, REQUEST_CODE_1);
				break;
			case 2:
				PhotoUtils.startCameraAndCrop(this, REQUEST_CODE_2);
				break;
			case 3:
				PhotoUtils.startCameraAndCrop(this, REQUEST_CODE_3);
				break;
			case 4:
				PhotoUtils.startAlbum(this, REQUEST_CODE_4);
				break;
			case 5:
				PhotoUtils.startAlbum(this, REQUEST_CODE_5);
				break;
			case 6:
				PhotoUtils.startAlbum(this, REQUEST_CODE_6);
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
		//*******************************************************************************************************************
		switch (requestCode) {
			case REQUEST_CODE_0:// 注意，返回的并非原图，照片会自动被压缩！
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					tv.append("返回的Bitmap的尺寸为" + bitmap.getWidth() + " * " + bitmap.getHeight() + "\n");
					iv.setImageBitmap(bitmap);
				}
				break;
			case REQUEST_CODE_1://注意，这里返回的data为null，这时在指定位置保存的是原图
				Bitmap bitmap1 = BitmapFactory.decodeFile(tempFileSavePath);
				if (bitmap1 != null) {
					iv.setImageBitmap(bitmap1);
					tv.append("保存的Bitmap的尺寸为" + bitmap1.getWidth() + " * " + bitmap1.getHeight() + "\n");
				}
				break;
			case REQUEST_CODE_2://
			case REQUEST_CODE_3:
				Bitmap bitmap2 = BitmapFactory.decodeFile(tempFileSavePath);
				if (bitmap2 != null) {
					tv.append("裁剪前的Bitmap的尺寸为" + bitmap2.getWidth() + " * " + bitmap2.getHeight() + "\n");
					Uri fromUri = Uri.fromFile(new File(tempFileSavePath));
					if (requestCode == REQUEST_CODE_2) PhotoUtils.cropImage(this, REQUEST_CODE_11, fromUri);
					else {
						Uri toUri = Uri.fromFile(new File(tempCropFileSavePath));
						PhotoUtils.cropImage(this, REQUEST_CODE_12, fromUri, toUri);
					}
				}
				break;
			//
			//*******************************************************************************************************************
			//
			case REQUEST_CODE_4:
				try {
					Uri fromUri = data.getData();
					Bitmap bitmap4 = MediaStore.Images.Media.getBitmap(getContentResolver(), fromUri);
					if (bitmap4 != null) {
						tv.append("返回的Bitmap的尺寸为" + bitmap4.getWidth() + " * " + bitmap4.getHeight() + "\n");
						iv.setImageBitmap(bitmap4);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case REQUEST_CODE_5:
			case REQUEST_CODE_6:
				try {
					Bitmap bitmap5 = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
					if (bitmap5 != null) {
						tv.append("裁剪前的Bitmap的尺寸为" + bitmap5.getWidth() + " * " + bitmap5.getHeight() + "\n");
						if (requestCode == REQUEST_CODE_5) PhotoUtils.cropImage(this, REQUEST_CODE_11, data.getData());
						else {
							Uri toUri = Uri.fromFile(new File(tempCropFileSavePath));
							PhotoUtils.cropImage(this, REQUEST_CODE_12, data.getData(), toUri);
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
				Bitmap bitmap11 = (Bitmap) data.getParcelableExtra("data");
				if (bitmap11 != null) {
					tv.append("裁剪后的Bitmap的尺寸为" + bitmap11.getWidth() + " * " + bitmap11.getHeight() + "\n");
					iv.setImageBitmap(bitmap11);
				}
				break;
			case REQUEST_CODE_12: //将裁剪后的图片通过指定的Uri返回
				try {
					Uri tempCropFileUri = Uri.fromFile(new File(tempCropFileSavePath));
					Bitmap bitmap12 = MediaStore.Images.Media.getBitmap(getContentResolver(), tempCropFileUri);
					tv.append("指定Uri后裁剪后的尺寸为" + bitmap12.getWidth() + " * " + bitmap12.getHeight() + "\n");
					iv.setImageBitmap(bitmap12);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}
}