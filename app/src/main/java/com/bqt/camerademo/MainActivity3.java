package com.bqt.camerademo;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity3 extends ListActivity {
	private ImageView iv;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"", "", "", "", "", "",};
		iv = new ImageView(this);
		//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		getListView().addFooterView(iv);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
	}

	public class BlurTransformation extends BitmapTransformation {

		private RenderScript rs;

		public BlurTransformation(Context context) {
			super(context);

			rs = RenderScript.create(context);
		}

		@Override
		protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
			Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.ARGB_8888, true);

			// Allocate memory for Renderscript to work with
			Allocation input = Allocation.createFromBitmap(rs, blurredBitmap,
					Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
			Allocation output = Allocation.createTyped(rs, input.getType());

			// Load up an instance of the specific script that we want to use.
			ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			script.setInput(input);
			script.setRadius(10);// Set the blur radius
			script.forEach(output);// Start the ScriptIntrinisicBlur
			output.copyTo(blurredBitmap);// Copy the output to the blurred bitmap
			toTransform.recycle();
			return blurredBitmap;
		}

		@Override
		public String getId() {
			return "blur";
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				test();
				break;
			case 1:
				Glide.with(this)
						.load("http://img.ivsky.com/img/tupian/pre/201703/24/gaoshan_hubo_fengjing-011.jpg")
						.thumbnail(0.05f)
						.transform(new BlurTransformation(this))
						.into(iv);
				break;
			case 2:

				break;
		}
	}

	public static void test() {
		//DecimalFormat中，占位符 0 表示如果位数不足则以 0 填充，占位符 # 表示只要有可能就把数字拉上这个位置
		double pi = 3.1415927;
		System.out.println(new DecimalFormat("0").format(pi)); //取一位整数，3
		System.out.println(new DecimalFormat("0.00").format(pi));//取一位整数和两位小数，3.14
		System.out.println(new DecimalFormat("00.000").format(pi));//整数不足部分以0填补，03.142
		System.out.println(new DecimalFormat("#").format(pi)); //取所有整数部分，3
		System.out.println(new DecimalFormat("#.##%").format(pi));//以百分比方式计数，314.16%
		System.out.println("-------------------------------------");

		long c = 299792458L;
		System.out.println(new DecimalFormat("#.#####E0").format(c));//显示为科学计数法，2.99792E8
		System.out.println(new DecimalFormat("00.####E0").format(c));//显示为两位整数的科学计数法，29.9792E7
		System.out.println(new DecimalFormat(",###").format(c)); //每三位以逗号进行分隔。299,792,458
		System.out.println(new DecimalFormat("光速为,###米/秒").format(c));//嵌入文本【光速为299,792,458米/秒】
		System.out.println("-------------------------------------");

		float f = 12.34f;
		System.out.println(new DecimalFormat("0.0").format(f));//12.3
		System.out.println(new DecimalFormat("#.#").format(f));//12.3
		System.out.println(new DecimalFormat("000.000").format(f));//012.340
		System.out.println(new DecimalFormat("###.###").format(f));//12.34
		System.out.println("-------------------------------------");

		float f2 = 0.05f;
		System.out.println(new DecimalFormat("0.0").format(f2));//0.1
		System.out.println(new DecimalFormat("#.#").format(f2));//0.1
		System.out.println(new DecimalFormat("000.000").format(f2));//000.050
		System.out.println(new DecimalFormat("###.###").format(f2));//0.05

		//常用
		String format = "###,##0.00";
		System.out.println("------------------常用-------------------");
		System.out.println(new DecimalFormat(format).format(pi));//0.05
		System.out.println(new DecimalFormat(format).format(c));//0.05
		System.out.println(new DecimalFormat(format).format(f));//0.05
		System.out.println(new DecimalFormat(format).format(f2));//0.05
	}
}