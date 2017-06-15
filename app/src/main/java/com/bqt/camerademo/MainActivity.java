package com.bqt.camerademo;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends ListActivity {
	private static final int NOTIFICATION_ID = 10086;
	private Context context;

	private ImageView ivImg;
	private CustomView customView;

	private RemoteViews rv;
	private Notification notification;

	private static final String url = "http://pic.meituba.com/uploads/allimg/2015/10/23/363.jpg";
	private static final String url2 = "http://pic.meituba.com/uploads/allimg/2015/10/23/364.jpg";
	private static final String url3 = "http://pic.meituba.com/uploads/allimg/2015/10/23/365.jpg";
	private static final String url4 = "http://pic.meituba.com/uploads/allimg/2015/10/23/367.jpg";
	private static final String url5 = "http://pic.meituba.com/uploads/allimg/2015/10/23/368.jpg";
	private static final String url6 = "http://pic.meituba.com/uploads/allimg/2015/10/23/369.jpg";
	private static final String url7 = "http://pic.meituba.com/uploads/allimg/2015/10/23/371.jpg";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {
				"1.基本用法",
				"2.扩展用法",
				"3.SimpleTarget回调",
				"5.ViewTarget回调",
				"6.发一个通知",
				"7.NotificationTarget回调",
		};

		context = getApplicationContext();
		ivImg = new ImageView(this);
		ivImg.setBackgroundColor(0xff008888);
		ivImg.setPadding(20, 20, 20, 20);
		ivImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ivImg.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 500));
		getListView().addHeaderView(ivImg);

		customView = new CustomView(this);
		customView.setBackgroundColor(0xff880088);
		customView.setPadding(20, 20, 20, 20);
		getListView().addFooterView(customView);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));

		rv = new RemoteViews(context.getPackageName(), R.layout.remoteview_notification);
		rv.setImageViewResource(R.id.iv, R.mipmap.ic_launcher);
		rv.setTextViewText(R.id.tv, "不懂左右逢源，不喜趋炎附势，不会随波逐流");
		notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setTicker("TickerText:" + "您有新短消息，请注意查收！").setContentTitle("我是标题").setContentText("我是内容")
				.setContent(rv)
				.build();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				recreate();
				break;
			case 1:
				Glide.with(context)
						.load(url)
						.into(ivImg);
				break;
			case 2:
				Glide.with(context)
						.load(url2)
						.asBitmap()//如果是 Gif 则显示 Gif 的第一帧
						//.asGif()//如果不是一个 Gif 而只是一个普通的图片，则.error()会被调用

						.placeholder(R.mipmap.ic_launcher)////资源加载过程中的占位Drawable
						.error(R.mipmap.ic_launcher)//load失败时显示的Drawable，如果没设置，则显示placeholder的Drawable
						.fallback(R.mipmap.ic_launcher)//设置model为空时要显示的Drawable。如果没设置，则显示error的Drawable

						.override(600, 600)//重新设置Target的宽高值

						.priority(Priority.LOW)//设置请求优先级

						.thumbnail(0.1f)//显示原始图像宽、高的10%，要根据需求使用合适的 ScaleType
						.thumbnail(Glide.with(context).load(url3).asBitmap().priority(Priority.HIGH))//缩略图

						.dontAnimate()//去除淡入淡出效果
						//.crossFade(800)//让图片切换时更加平滑
						//.animate(android.R.anim.accelerate_decelerate_interpolator)//在异步加载资源完成时会执行该动画

						.centerCrop()//ImageView 会完全填充，但图像可能不会完整显示
						.fitCenter()//图像会完全显示，但可能不会填满整个 ImageView
						.transform(new BlurTransformation(context, 15), new CropCircleTransformation(context))//自定义转换
						//.bitmapTransform(new BlurTransformation(context, 25), new CropCircleTransformation(context))

						.skipMemoryCache(true)//禁止内存缓存，但不保证一定不被缓存
						//.clearMemory()//清除内存缓存，不能在UI线程中调用
						.diskCacheStrategy(DiskCacheStrategy.NONE)//更改磁盘缓存策略
						//.clearDiskCache()//清除磁盘缓存，不能在UI线程中调用，建议同时clearMemory()

						.into(customView.getImageView());
				break;
			case 3:
				if (new Random().nextBoolean()) {
					SimpleTarget<GlideDrawable> target = new SimpleTarget<GlideDrawable>() {//回调泛型为【GlideDrawable】
						@Override
						public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
							ivImg.setImageDrawable(resource.getCurrent());
							customView.getImageView().setImageDrawable(resource.getCurrent());
						}
					};

					Glide.with(context).load(url4).crossFade(1000).into(target);
				} else {
					SimpleTarget<Bitmap> target2 = new SimpleTarget<Bitmap>(200, 200) {//可以指定回调图片的具体大小
						@Override
						public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
							ivImg.setImageBitmap(resource);
							customView.getImageView().setImageBitmap(resource);
						}
					};

					Glide.with(context).load(url5).asBitmap().into(target2);//调用asBitmap后SimpleTarget的回调泛型为【Bitmap】
				}

				break;
			case 4:
				ViewTarget<CustomView, GlideDrawable> vTarget = new ViewTarget<CustomView, GlideDrawable>(customView) {
					@Override
					public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
						this.view.getImageView().setImageDrawable(resource.getCurrent());
						ivImg.setImageDrawable(resource.getCurrent());
					}
				};

				Glide.with(context).load(url6).into(vTarget);
				break;
			case 5:
				((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
				break;
			case 6:
				if (new Random().nextBoolean()) {
					NotificationTarget notificationTarget = new NotificationTarget(context, rv, R.id.iv, 300, 300,//指定了view，id，宽高
							notification, NOTIFICATION_ID);//加载完之后自动用指定notification_id刷新指定notification
					Glide.with(context.getApplicationContext()) // safer!
							.load(url7)
							.asBitmap()
							.into(notificationTarget);
				} else {
					SimpleTarget<Bitmap> target3 = new SimpleTarget<Bitmap>(300, 300) {
						@Override
						public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
							rv.setImageViewBitmap(R.id.iv, resource);
							((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
						}
					};
					Glide.with(context.getApplicationContext()).load(url5).asBitmap().into(target3);
				}
				break;
		}
	}
}