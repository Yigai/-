package com.anjoyo.cnblog.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.anjoyo.cnblog.activitys.R;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 根据博主头像的URL地址下载头像
 * 
 * @author ChnAdo
 * 
 */
public class ImgDownload {
	// 1、内存
	// 2、文件
	//
	// 先判断是否在内存中 如果不在 再判断是否在文件中 如果都不在再去服务器重新下载一遍
	private static final ImgDownload imgload = new ImgDownload();
	ExecutorService pool;
	HashMap<String, SoftReference<Drawable>> imageCache;// 图片的缓存

	public static synchronized ImgDownload getImageInstance() {
		return imgload;
	}

	private ImgDownload() {
		pool = Executors.newFixedThreadPool(4);
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	/**
	 * 根据URL得到Drawable
	 * 
	 * @param imageUrl
	 * @return 返回Drawable
	 */
	public Drawable loadImageFromUrl(String imageUrl) {

		Drawable drawable = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream in = conn.getInputStream();
			drawable = Drawable.createFromStream(in, imageUrl);
			conn.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return drawable;
	}

	// 回调接口
	public interface ImageCallback {
		public void imageLoaded(Drawable drawable, String imageUrl);
	}

	// 判断加载
	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		// 如果缓存中有图片 直接加载
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		// 如果不允许下载图片 返回空
		if (!ImageEnorDis.isDownImg) {
			return null;
		}
		// 如果允许下载图片
		// 开线程 下载 下载完 发消息 在此处取图片
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				imageCallback.imageLoaded((Drawable) msg.obj, imageUrl);
			}

		};
		// 如果缓存中没图片 就开线程 下载图片
		pool.execute(new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				if (drawable != null) {// 如果下载到了图片
					// 将图片存入缓存中
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message msg = handler.obtainMessage(0, drawable);
					handler.sendMessage(msg);
				} else {
					Message msg = handler.obtainMessage(0, null);
					handler.sendMessage(msg);
				}

			}
		});

		return null;
	}

	public void getImageDrawable(String imageUrl, final ImageView imageView) {
		Drawable drawable = loadDrawable(imageUrl, new ImageCallback() {

			@Override
			public void imageLoaded(Drawable imgdrawable, String imageUrl) {
				if (imageView != null) {
					if (imgdrawable != null) {
						imageView.setImageDrawable(imgdrawable);
					} else {
						imageView.setImageResource(R.drawable.sample_face);
					}
				} else {
					imageView.setImageResource(R.drawable.sample_face);
				}

			}
		});
		if (drawable == null) {
			imageView.setImageResource(R.drawable.sample_face);
		} else {
			imageView.setImageDrawable(drawable);
		}

	}

}
