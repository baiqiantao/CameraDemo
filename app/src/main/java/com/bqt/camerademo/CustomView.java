package com.bqt.camerademo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CustomView extends FrameLayout {
	ImageView iv;

	public CustomView(Context context) {
		super(context);
		initialize(context);
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context);
	}

	public void initialize(Context context) {
		iv = new ImageView(context);
	}

	public void setImage(Drawable drawable) {
		iv.setImageDrawable(drawable);
	}
}