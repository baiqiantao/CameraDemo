package com.bqt.camerademo;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 使用LoaderManager获取系统通话记录
 */
public class CallLogLoader implements LoaderManager.LoaderCallbacks<Cursor> {

	private int type;
	private String selectNumber;
	private Context context;

	public static final String[] PROJECTION = new String[]{// 查询指定的条目
			CallLog.Calls._ID,//也可以用"_id"
			CallLog.Calls.NUMBER,//
			CallLog.Calls.CACHED_NAME,//
			CallLog.Calls.TYPE, //
			CallLog.Calls.DURATION,//
			CallLog.Calls.DATE};
	public static final String SORTORDER = CallLog.Calls.DEFAULT_SORT_ORDER;//按时间排序【date DESC】

	public CallLogLoader(Context context) {
		this.context = context;
	}

	public CallLogLoader(Context context, int type) {
		this.context = context;
		this.type = type;
	}

	public CallLogLoader(Context context, String selectNumber) {
		this.context = context;
		this.selectNumber = selectNumber;
	}

	public CallLogLoader(Context context, int type, String selectNumber) {
		this.context = context;
		this.type = type;
		this.selectNumber = selectNumber;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i("bqt", "【onCreateLoader】");
		String selection = null;
		String[] selectionArgs = null;
		if (selectNumber != null) {
			selection = CallLog.Calls.NUMBER + "=? ";//格式为【"number=? and (type=1 or type=3)"】
			selectionArgs = new String[]{selectNumber};
			if (type != CallLog.Calls.INCOMING_TYPE && type != CallLog.Calls.OUTGOING_TYPE && type != CallLog.Calls.MISSED_TYPE) type = 0;
			if (type != 0) selection = CallLog.Calls.NUMBER + "=? and " + CallLog.Calls.TYPE + "=" + type;
		}
		if (type == CallLog.Calls.INCOMING_TYPE) {// 来电=1
			Log.i("bqt", "【来电】 " + selectNumber);
		} else if (type == CallLog.Calls.OUTGOING_TYPE) { // 去电=2
			Log.i("bqt", "【去电】 " + selectNumber);
		} else if (type == CallLog.Calls.MISSED_TYPE) {// 未接=3
			Log.i("bqt", "【未接来电】 " + selectNumber);
		} else {
			Log.i("bqt", "【所有通话记录】 " + selectNumber);
		}
		
		//******************************************************************************************
		
		return new CursorLoader(context,
				CallLog.Calls.CONTENT_URI, //
				PROJECTION, //
				selection, //
				selectionArgs,//
				SORTORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data == null) return;
		Log.i("bqt", "【通话记录数量为】" + data.getCount());
		data.moveToFirst();
		do {
			int _id = data.getInt(data.getColumnIndex("_id"));
			int type = data.getInt(data.getColumnIndex("type"));//通话类型，1 来电 .INCOMING_TYPE；2 已拨 .OUTGOING_；3 未接 .MISSED_
			String number = data.getString(data.getColumnIndex("number"));// 电话号码
			String name = data.getString(data.getColumnIndex("name"));//联系人
			long date = data.getLong(data.getColumnIndex("date"));//通话时间，即可以用getString接收，也可以用getLong接收
			String formatDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(date));
			int duration = data.getInt(data.getColumnIndex("duration"));//通话时长，单位：秒
			String msgObj = "\nID：" + _id + "\n类型：" + type + "\n号码：" + number + "\n名称：" + name + "\n时间：" + formatDate + "\n时长：" + duration;
			Log.i("bqt", "【通话记录信息为】" + msgObj);
		} while (data.moveToNext());
		if (Build.VERSION.SDK_INT < 14) data.close();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.i("bqt", "【onLoaderReset】");
	}
}