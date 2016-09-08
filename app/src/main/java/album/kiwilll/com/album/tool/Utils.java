package album.kiwilll.com.album.tool;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by wei on 2016/9/8.
 */
public class Utils {
    public static byte[] Bitmap2Bytes(Bitmap image, long size) {
        try {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                long max = 1024 * 1024 + 3072;
                if (size > max) {
                    int options = 100;
                    while (baos.toByteArray().length > (size * 0.8)) {
                        baos.reset();
                        options -= 10;
                        image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    }
//                    Log.i("TAG", "Utils bitmap option " + options);
                    if (image.isRecycled())
                        image.recycle();

                    return baos.toByteArray();
                } else {
                    return baos.toByteArray();
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }

            stream.close();
            out.close();

//            Bitmap bitmap = BitmapFactory.decodeStream(stream);
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);

            byte[] c = "123abc".getBytes();


            byte[] result = out.toByteArray();

            return byteMerger(c, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
