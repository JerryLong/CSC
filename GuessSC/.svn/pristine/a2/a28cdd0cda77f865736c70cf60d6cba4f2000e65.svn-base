package com.guess.image.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageLoader {
	private static ImageLoader mInstance;
	/**
	 * 加載圖片到內存緩存
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEAFULT_THREAD_COUNT = 1;
	/**
	 * 隊列調度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任務隊列
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 後臺輪詢線程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	/**
	 * 調整控制 mPoolThreadHandler的執行順序
	 */
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	private Semaphore mSemaphoreThreadPool;

	/**
	 * UI線程中的Hanlder
	 */
	private Handler mUIHandler;

	public enum Type {
		LIFO, FIFO
	}

	public static ImageLoader getInstance(int threadCount, Type type) {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;

	}

	public ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	/**
	 * 初始化
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		// 后台轮询线程
		mPoolThread = new Thread() {
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					public void handleMessage(android.os.Message msg) {
						// 線程池取出一個任務執行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				// 释放一个信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			};
		};
		mPoolThread.start();

		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}

	/**
	 * 获取图片，为imageview设置图片
	 * 
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ImageBean holder = (ImageBean) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageView = holder.imageView;
					String path = holder.path;
					// 將path与getTag存儲的路徑進行比較
					if (imageView.getTag().toString().equals(path)) {
						imageView.setImageBitmap(bm);
					}
				}
			};
		}
		// 从内存缓存中获取图片
		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null) {
			refreshBitmap(path, imageView, bm);
		} else {
			addTask(new Runnable() {

				@Override
				public void run() {
					// 加载图片，以及图片的压縮
					// 1.获得图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					// 2.压缩图片
					Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
					// 3、把圖片添加的緩存
					addBitmapToLruCache(path, bm);
					refreshBitmap(path, imageView, bm);

					mSemaphoreThreadPool.release();
				}
			});
		}
	}

	private void refreshBitmap(final String path, final ImageView imageView, Bitmap bm) {
		Message message = Message.obtain();
		ImageBean holder = new ImageBean();
		holder.bitmap = bm;
		holder.imageView = imageView;
		holder.path = path;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * 將圖片加入到LruCache
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm) {
		if (getBitmapFromLruCache(path) == null) {
			if (bm != null)
				mLruCache.put(path, bm);
		}
	}

	/**
	 * 根据圖片的寬和高對圖片進行壓縮
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
		// 获取图片的宽和高,并不把圖片加載到內存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = caculateSamleSize(options, width, height);
		// 使用獲取到的inSampleSize再次解析圖片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 根据需求的宽和高以及实际的宽和高计算SampleSize
	 * 
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	private int caculateSamleSize(Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);
			inSampleSize = Math.max(widthRadio, heightRadio);
		}
		return inSampleSize;
	}

	/**
	 * 根据imageSview获取适当的宽和高进行压缩
	 * 
	 * @param imageView
	 * @return
	 */
	protected ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams lp = imageView.getLayoutParams();

		int width = imageView.getWidth(); // 获取imageview的实际宽度
		if (width <= 0) {
			width = lp.width;// 獲取imageview在layout中聲明的寬度
		}
		if (width <= 0) {
			// width = imageView.getMaxWidth(); // 检查最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // 检查最大值
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();// 获取imageview的实际高度
		if (height <= 0) {
			height = lp.height;// 獲取imageview在layout中聲明的高度
		}
		if (height <= 0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 通過反射獲取imageview的某個屬性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		// if(mPoolThreadHandler==null) wait();
		try {
			if (mPoolThreadHandler == null)
				mSemaphorePoolThreadHandler.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	private Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	/**
	 * 根据path在缓存中找到bitmap
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String path) {
		return mLruCache.get(path);
	}

	public class ImageSize {
		int width;
		int height;
	}

	public class ImageBean {
		Bitmap bitmap;
		ImageView imageView;
		String path;

	}
}
