package album.kiwilll.com.album.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import album.kiwilll.com.album.model.AlbumBean;
import album.kiwilll.com.album.model.ImageBean;

/**
 * Created by wei on 2015/9/22.
 */
public class MyBimp {
    public static void setMax(int max) {
        MyBimp.max = max;
    }

    public static int max = 9;
    private static ArrayList<String> pathList = new ArrayList<>();
    private static ArrayList<String> orrintpathList = new ArrayList<>();
    public static ArrayList<ImageBean> tempSelectBitmap = new ArrayList<ImageBean>();   //选择的图片的临时列表
    public static ArrayList<ImageBean> AblumBitmap = new ArrayList<ImageBean>();
    public static ArrayList<AlbumBean> AblumAll = new ArrayList<>();

    public static void clearAll() {
        pathList.clear();
        orrintpathList.clear();
        recy();
        tempSelectBitmap.clear();
        AblumAll.clear();
        AblumBitmap.clear();

//        pathList = null;
//        orrintpathList = null;
//        tempSelectBitmap = null;
//        AblumBitmap = null;
//        AblumAll = null;
    }

    private static void recy() {
        for (ImageBean imageBean : tempSelectBitmap) {
            imageBean.recy();
        }
    }

    public static Bitmap revitionImageSize(String path, int rotation) throws IOException {
        if (path == null)
            return null;
        try {
            try {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                        new File(path)));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                int i = 0;
                Bitmap bitmap = null;
                while (true) {
                    if ((options.outWidth >> i <= 2000)
                            && (options.outHeight >> i <= 2000)) {
                        in = new BufferedInputStream(
                                new FileInputStream(new File(path)));
                        options.inSampleSize = (int) Math.pow(2.0D, i);
                        options.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeStream(in, null, options);
                        if (rotation != 0) {
                            Matrix m = new Matrix();
                            m.setRotate(rotation);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                        }
                        break;
                    }
                    i += 1;
                }

                return bitmap;
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromFile(String path, int rotation) throws IOException {
        if (path == null)
            return null;
        try {
            try {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                        new File(path)));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                WeakReference<Bitmap> weakReference = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path));
                if (weakReference.get() != null) {
                    Bitmap bitmap = Bitmap.createScaledBitmap(weakReference.get(), options.outWidth, options.outHeight, true);
                    if (rotation != 0 && bitmap != null) {
                        Matrix m = new Matrix();
                        m.setRotate(rotation);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    }
                    return bitmap;
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getPathList() {
        int len = tempSelectBitmap.size();
        for (int i = 0; i < len; i++) {
            pathList.add("file://" + tempSelectBitmap.get(i).path);
        }
        return pathList;
    }

    public static ArrayList<String> getOrrinPathLsit() {
        int len = tempSelectBitmap.size();
        orrintpathList.clear();
        for (int i = 0; i < len; i++) {
            orrintpathList.add(tempSelectBitmap.get(i).path);
        }
        return orrintpathList;
    }
}
