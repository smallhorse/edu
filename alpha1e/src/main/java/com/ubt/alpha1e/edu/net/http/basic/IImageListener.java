package com.ubt.alpha1e.edu.net.http.basic;

import android.graphics.Bitmap;

public interface IImageListener {
	void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code);

}