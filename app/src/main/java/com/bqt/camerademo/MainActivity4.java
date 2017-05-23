package com.bqt.camerademo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity4 extends Activity {
	private ArrayList<String> selectedIds = new ArrayList<>();
	private ArrayList<SimpleBean> indexs = new ArrayList<SimpleBean>();
	private EditText et;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et = new EditText(this);
		setContentView(et);

		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//This method is called to notify you that,
				// within [s], the [count] characters beginning at [start] are about to be replaced by new text with length [after].
				// It is an error to attempt to make changes to s from this callback.
				//Log.i("bqt", "【beforeTextChanged】" + s + "   " + start + "   " + after + "   " + count);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//This method is called to notify you that,
				//within [s], the [count] characters beginning at [start] have just replaced old text that had length [before].
				//It is an error to attempt to make changes to [s] from this callback.
				//在s中，从start位置开始，有before个字符被count个字符【替换】了
				//s 表示改变后输入框中的字符串，start 表示内容是从哪个位置(从0开始)开始改变的
				//如果before!=0，代表字符被替换了(可能增加了也可能减少了)；如果before=0，可以确定是增加了count个字符

				//*************************************************测试代码*****************************************
				Log.i("bqt", "【onTextChanged】" + s + "   " + start + "   " + before + "   " + count);
				if (before == 0) Log.i("bqt", "【直接增加了一些字符】" + start + " " + count + " " + before);
				else {//替换或减少了一些字符
					if (count - before > 0) Log.i("bqt", "【替换后增加了一些字符】" + start + " " + count + " " + before);
					else if (count - before == 0) Log.i("bqt", "【替换后字符个数没有变】" + start + " " + count + " " + before);
					else {
						if (count == 0) Log.i("bqt", "【直接减少了一些字符】" + start + " " + count + " " + before);
						else Log.i("bqt", "【替换后减少了一些字符】" + start + " " + count + " " + before);
					}
				}

				//***********************************************@逻辑代码*******************************************
				if (before != 0 && count - before < 0) {//如果是减少了一些字符
					for (final SimpleBean sbean : indexs) {
						if (start == sbean.end || start == sbean.end - 1) {//如果是在某个昵称之后减少了一些字符
							if (selectedIds != null) selectedIds.remove(sbean.userAlias);//如果是的话，在@列表中去除此昵称
							Log.i("bqt", "【删除掉文本框中的此昵称】");
							et.postDelayed(new Runnable() {
								@Override
								public void run() {
									et.getEditableText().replace(sbean.start, sbean.end, "");//删除掉文本框中的此昵称
								}
							}, 100);
						}
					}
				}

				for (SimpleBean sbean : indexs) {
					if (start > sbean.start && start < sbean.end) {//是否在【某个昵称之间】增加或减少或替换了一些字符
						Log.i("bqt", "【在某个昵称之间_替换_了一些字符】" + sbean.start + " " + start + " " + sbean.end);
						if (selectedIds != null) selectedIds.remove(sbean.userAlias);//如果是的话，在@列表中去除此昵称
					}
				}

				if (start + count - 1 >= 0 && s.toString().charAt(start + count - 1) == '@') {//如果增加或减少或替换后改变的文本以@结尾
					showSingleChoiceDialog();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				//Log.i("bqt", "【afterTextChanged】" + s.toString());
				indexs.clear();//先清空
				//当输入内容后把所有信息封装起来
				if (selectedIds != null && selectedIds.size() > 0) {
					for (String userAlias : selectedIds) {
						String newUserAlias = "@" + userAlias;
						int startIndex = et.getText().toString().indexOf(newUserAlias);//注意。这里把@加进去了
						int endIndex = startIndex + newUserAlias.length();
						indexs.add(new SimpleBean(userAlias, startIndex, endIndex));
						Log.i("bqt", userAlias + "的【边界值】" + startIndex + "  " + endIndex);
					}
					Log.i("bqt", "【选择的id有：】" + Arrays.toString(selectedIds.toArray(new String[selectedIds.size()])));
				}
			}
		});
	}

	//单选对话框
	public void showSingleChoiceDialog() {
		final String[] items = {"白乾涛", "包青天", "baiqiantao"};
		AlertDialog dialog = new AlertDialog.Builder(this)//
				.setTitle("请选择")//
				.setPositiveButton("确定", null).setNegativeButton("取消", null)
				.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (selectedIds == null) selectedIds = new ArrayList<>();
						selectedIds.add(items[which]);//先把选择的内容存起来再更改EditText中的内容

						//先把选择的内容存起来再更改EditText中的内容，顺序不能反
						int index = et.getSelectionStart();//获取光标所在位置
						et.getEditableText().insert(index, items[which] + " ");//在光标所在位置插入文字
						dialog.dismiss();
					}
				})
				.create();
		dialog.show();
	}

	static class SimpleBean {
		public int start;
		public int end;
		public String userAlias;

		public SimpleBean(String userAlias, int start, int end) {
			this.userAlias = userAlias;
			this.start = start;
			this.end = end;
		}
	}
}
