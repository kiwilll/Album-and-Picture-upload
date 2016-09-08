package album.kiwilll.com.album.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;


import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import album.kiwilll.com.album.model.ImageBean;

public class NativeImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    private static NativeImageLoader mInstance = new NativeImageLoader();
    private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(3);

    private NativeImageLoader() {
        // 获取应用程序的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 用最大内存的1/4来存储图片
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            // 获取每张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * 通过此方法来获取NativeImageLoader的实例
     *
     * @return
     */
    public static NativeImageLoader getInstance() {
        return mInstance;
    }

    public void clear() {
//        for (int i=0;i<mMemoryCache.size();i++)
//        {
//            mMemoryCache.re
//        }
    }

    /**
     * 加载本地图片，对图片不进行裁剪
     *
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(ImageBean ImageBean,
                                  final NativeImageCallBack mCallBack) {
        return this.loadNativeImage(ImageBean, null, mCallBack);
    }

    /**
     * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
     * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack
     * mCallBack)来加载
     *
     * @param mPoint
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(final ImageBean imageBean, final Point mPoint,
                                  final NativeImageCallBack mCallBack) {
        final String path = imageBean.path;
        final int rotation = imageBean.rotation;

        // 先获取内存中的Bitmap
        Bitmap bitmap = getBitmapFromMemCache(path);
        ;

        final Handler mHander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj != null)
                    mCallBack.onImageLoader((Bitmap) msg.obj, path);
                else
                    mCallBack.onImageLoader(null, path);
            }

        };

        // 若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (bitmap == null) {
            mImageThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    // 先获取图片的缩略图
                    try {
                        Bitmap mBitmap = decodeThumbBitmapForFile(path,
                                mPoint == null ? 0 : mPoint.x, mPoint == null ? 0
                                        : mPoint.y);
                        // Log.i("TAG", "-->" + imageBean.path);
                        if (mBitmap == null) {
                            mBitmap = imageBean.getBitmap();
                            if (mBitmap != null) {
                                mBitmap = zoomImage(mBitmap, mPoint == null ? 0 : mPoint.x, mPoint == null ? 0
                                        : mPoint.y);
                            }
                        } else {
                            if (rotation != 0) {
                                Matrix m = new Matrix();
                                m.setRotate(rotation);
                                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true);
                            }
                        }

                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        mHander.sendMessage(msg);
                        // mBitmap.recycle();
                        // 将图片加入到内存缓存
                        if (mBitmap != null)
                            addBitmapToMemoryCache(path, mBitmap);
                    } catch (OutOfMemoryError ex) {
                        ex.printStackTrace();
                        Message msg = mHander.obtainMessage();
                        msg.obj = null;
                        mHander.sendMessage(msg);
                    }
                }
            });
        }
        return bitmap;

    }


    /**
     * index,detail,user里item的 图片
     *
     * @param path
     * @param mPoint    根据mPint返回特定大小的图片
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(final String path, final Point mPoint,
                                  final NativeImageCallBack mCallBack) {
        // 先获取内存中的Bitmap
        Bitmap bitmap = getBitmapFromMemCache(path);

        final Handler mHander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap) msg.obj, path);
            }

        };

        // 若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (bitmap == null) {
            mImageThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    // 先获取图片的缩略图
                    Bitmap mBitmap = decodeThumbBitmapForFile(path,
                            mPoint == null ? 0 : mPoint.x, mPoint == null ? 0
                                    : mPoint.y);
                    Message msg = mHander.obtainMessage();
                    msg.obj = mBitmap;
                    mHander.sendMessage(msg);

                    // 将图片加入到内存缓存
                    //  addBitmapToMemoryCache(path, mBitmap);
                }
            });
        }
        return bitmap;

    }

    public Bitmap showCacheBitmap(int pathtype, String name) {
        if (getBitmapFromMemCache(name) != null) {
            return getBitmapFromMemCache(name);
        } else if (FileUtils.fileIsExists(FileUtils.getBitmapPath(pathtype, name))) {
            // 从SD卡获取手机里面获取Bitmap
            Bitmap bitmap = FileUtils.getBitmap(pathtype, name);
            return bitmap;
        }
        return null;
    }

    public void saveCacheBitmap(Bitmap bitmap, int pathtype, String name) {
        switch (pathtype) {
            case 1:
                FileUtils.saveBitmap(bitmap, name);
                break;
            case 2:
                FileUtils.saveBitmapIndex(bitmap, name);
                break;
            case 3:
                addBitmapToMemoryCache(name, bitmap);
                //FileUtils.saveBitmapAlbum(bitmap, name);
                break;
        }
    }

    public void removeCache(HashMap<Integer, String> names) {
        if (names == null || names.size() == 0)
            return;
        int len = names.size();
        for (int i = 0; i < len; i++) {
            try {
                mMemoryCache.remove(names.get(i));
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * 往内存缓存中添加Bitmap
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 根据key来获取内存中的图片
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        if (key == null)
            return null;
        return mMemoryCache.get(key);
    }

    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth,
                                            int viewHeight) {
//        System.out.println(path + "-" + viewWidth + "-" + viewHeight);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        // 设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

//        Bitmap b = Bitmap.createBitmap(Bmp, 0, statusBarHeight, w, h
//                - statusBarHeight);

        return BitmapFactory.decodeFile(path, options);
    }


    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     *
     * @param
     * @param
     * @param
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth,
                             int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        // 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math
                    .round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight
                    / (float) viewWidth);

            // 为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    /**
     * 加载本地图片的回调接口
     *
     * @author xiaanming
     */
    public interface NativeImageCallBack {
        /**
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
         *
         * @param bitmap
         * @param path
         */
        public void onImageLoader(Bitmap bitmap, String path);
    }

    /**
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, int newWidth,
                                   int newHeight) {
        if (bgimage != null) {
            // 获取这个图片的宽和高
            float width = bgimage.getWidth();
            float height = bgimage.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                    (int) height, matrix, true);
            return bitmap;
        } else
            return null;
    }

//    public  void clearMemoryCache(){
//        mMemoryCache.evictAll();
//    }
}
