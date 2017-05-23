package com.bqt.camerademo.media;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaLoader {
	// load type
	private int type = TYPE_IMAGE;
	public static final int TYPE_IMAGE = 1;//照片
	public static final int TYPE_VIDEO = 2;//视频

	public int index = 0;//临时记录个数的变量
	private Activity activity;
	
	private final static String[] IMAGE_PROJECTION = {
			MediaStore.Images.Media.DATA,//Path to the file on disk.
			MediaStore.Images.Media.DISPLAY_NAME,//The display name of the file
			MediaStore.Images.Media.DATE_ADDED,//The time the file was added to the media provider
			MediaStore.Images.Media._ID
	};
	private final static String[] VIDEO_PROJECTION = {
			MediaStore.Video.Media.DATA,
			MediaStore.Video.Media.DISPLAY_NAME,
			MediaStore.Video.Media.DATE_ADDED,
			MediaStore.Video.Media._ID,
			MediaStore.Video.Media.DURATION,//The duration of the video file, in ms
	};
	
	public MediaLoader(Activity activity, int type) {
		this.activity = activity;
		this.type = type;
	}
	
	public void loadAllImage(final MediaLoadListener imageLoadListener) {
		activity.getLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {
				Loader<Cursor> loader = null;
				if (id == TYPE_IMAGE) {
					loader = new CursorLoader(activity, //
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//
							IMAGE_PROJECTION,//
							MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?" + " or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[]{"image/jpeg", "image/png", "image/gif"},//类型
							MediaStore.Images.Media.DATE_ADDED + " DESC");
				} else if (id == TYPE_VIDEO) {
					loader = new CursorLoader(activity,
							MediaStore.Video.Media.EXTERNAL_CONTENT_URI,//
							VIDEO_PROJECTION,//
							null,//
							null,//
							MediaStore.Video.Media.DATE_ADDED + " DESC");
				}
				return loader;
			}
			
			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
				try {
					ArrayList<MediaFolderBean> imageFolders = new ArrayList<MediaFolderBean>();
					MediaFolderBean imageFolder = new MediaFolderBean();
					List<MediaBean> images = new ArrayList<MediaBean>();
					if (data != null) {
						int count = data.getCount();
						if (count > 0) {
							data.moveToFirst();
							do {
								String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));//路径
								String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
								// 如原图路径不存在，或者路径存在但文件不存在，就结束当前循环
								if (TextUtils.isEmpty(path) || !new File(path).exists()) continue;
								long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
								int duration = (type == TYPE_VIDEO ? data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4])) : 0);
								MediaBean image = new MediaBean(path, dateTime, duration, type);
								MediaFolderBean folder = getImageFolder(path, imageFolders);
								folder.getImages().add(image);
								folder.setType(type);
								index++;
								folder.setImageNum(folder.getImageNum() + 1);

								//if (index >= 100) break;// 只添加最新的100条
								images.add(image);
								imageFolder.setType(type);
								imageFolder.setImageNum(imageFolder.getImageNum() + 1);
							} while (data.moveToNext());
							
							if (images.size() > 0) {
								// 文件夹按图片数量排序
								sortFolder(imageFolders);
								imageFolders.add(0, imageFolder);
								String title = "";
								switch (type) {
									case TYPE_VIDEO:
										title = "最近视频";
										break;
									case TYPE_IMAGE:
										title = "最近照片";
										break;
								}
								imageFolder.setFirstImagePath(images.get(0).getPath());
								imageFolder.setName(title);
								imageFolder.setType(type);
								imageFolder.setImages(images);
							}
							imageLoadListener.loadComplete(imageFolders);
							if (Build.VERSION.SDK_INT < 14) data.close();
						} else {
							// 如果没有相册
							imageLoadListener.loadComplete(imageFolders);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
			}
		});
	}
	//*************************************************************************************************************************

	private void sortFolder(List<MediaFolderBean> imageFolders) {
		Collections.sort(imageFolders, new Comparator<MediaFolderBean>() {
			@Override
			public int compare(MediaFolderBean lhs, MediaFolderBean rhs) {
				if (lhs.getImages() == null || rhs.getImages() == null) return 0;
				int lsize = lhs.getImageNum();
				int rsize = rhs.getImageNum();
				return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);// 文件夹按图片数量排序
			}
		});
	}
	
	private MediaFolderBean getImageFolder(String path, List<MediaFolderBean> imageFolders) {
		File imageFile = new File(path);
		File folderFile = imageFile.getParentFile();
		
		for (MediaFolderBean folder : imageFolders) {
			if (folder.getName().equals(folderFile.getName())) return folder;
		}
		MediaFolderBean newFolder = new MediaFolderBean();
		newFolder.setName(folderFile.getName());
		newFolder.setPath(folderFile.getAbsolutePath());
		newFolder.setFirstImagePath(path);
		imageFolders.add(newFolder);
		return newFolder;
	}
	
	public interface MediaLoadListener {
		void loadComplete(List<MediaFolderBean> folders);
	}
}