package com.bqt.camerademo;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bqt.camerademo.media.MediaBean;
import com.bqt.camerademo.media.MediaFolderBean;
import com.bqt.camerademo.media.MediaLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PictureActivity extends ListActivity {
	private TextView tv_info;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"所有通话记录", "所有未接来电通话记录", "指定号码所有通话记录", "扫描图片", "扫描视频", };
		for (int i = 0; i < array.length; i++) {
			array[i] = i + "、" + array[i];
		}
		tv_info = new TextView(this);// 将内容显示在TextView中
		tv_info.setTextColor(Color.BLUE);
		tv_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv_info.setPadding(20, 10, 20, 10);
		getListView().addFooterView(tv_info);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				getLoaderManager().initLoader(0, null, new CallLogLoader(this));
				break;
			case 1:
				getLoaderManager().initLoader(1, null, new CallLogLoader(this, CallLog.Calls.MISSED_TYPE));
				break;
			case 2:
				getLoaderManager().initLoader(2, null, new CallLogLoader(this,"18620598604"));
				break;
			case 3:
				loadImages(MediaLoader.TYPE_IMAGE);
				break;
			case 4:
				loadImages(MediaLoader.TYPE_VIDEO);
				break;
		}
	}

	private void loadImages(int type) {
		new MediaLoader(this, type).loadAllImage(new MediaLoader.MediaLoadListener() {
			@Override
			public void loadComplete(List<MediaFolderBean> folders) {
				if (folders != null && folders.size() > 0) {
					Log.i("bqt", "【文件夹的数量】" + folders.size());
					for (MediaFolderBean folder : folders) {
						List<MediaBean> images = folder.getImages();
						Log.i("bqt", "      【文件夹中图片/视频的数量】" + images.size() + "【位置】" + folder.getPath());
						for (MediaBean media : images) {
							String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA).format(new Date(media.getLastUpdateAt()));
							Log.i("bqt", "            【图片/视频的位置】" + media.getPath() + "【修改时间】" + time );
						}
					}
				}
			}
		});
	}
}