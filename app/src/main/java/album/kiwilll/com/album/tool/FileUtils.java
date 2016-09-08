package album.kiwilll.com.album.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment
            .getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
//    private static ExecutorService mImageThreadPool = null;

    public static String SDPATH =
            "/demo/pic";
    public static String INDEXPATH = "/demo/index";
    public static String ALBUM = "/demo/album";
    private static String FIGUREPATH = "/demo/figure";

    public final static int SDPATHINT = 1;
    public final static int INDEXPATHINT = 2;
    public final static int ALBUMPATHINT = 3;

//    public static ExecutorService getThreadPool() {
//        if (mImageThreadPool == null) {
//            synchronized (ExecutorService.class) {
//                if (mImageThreadPool == null) {
//                    mImageThreadPool = Executors.newFixedThreadPool(1);
//                }
//            }
//        }
//
//        return mImageThreadPool;
//
//    }
//    public static int mWidth = UserContext.getInstance(c).getScreenWidth();
//    public static int mHeight = UserContext.getInstance().getScreenHeight();

    //获取根目录
    private static String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + SDPATH
                : mDataRootPath + SDPATH;
    }

    //获取index目录
    private static String getStorageDirectoryIndex() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + INDEXPATH
                : mDataRootPath + INDEXPATH;
    }

    //获取album目录
    private static String getStorageDirectoryAlbum() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + ALBUM
                : mDataRootPath + ALBUM;
    }

    //获取album目录
    private static String getStorageDirectoryAFigure() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + FIGUREPATH
                : mDataRootPath + FIGUREPATH;
    }

    public static void saveBitmap(final Bitmap bm, final String picName) {

        if (bm == null) {
            return;
        }
//        getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
        try {
            String path = getStorageDirectory();
            File folderFile = new File(path);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            File file = new File(path + File.separator + picName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (!bm.isRecycled()) {
                bm.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }

//        });


    }

    public static String saveFigureIndexBitmap(Bitmap bm, String picName) {
        String savepath = "";
        try {
            if (bm == null) {
                return savepath;
            }
            String path = getStorageDirectoryAFigure();
            File folderFile = new File(path);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            savepath = path + File.separator + picName;
            File file = new File(savepath);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savepath;
    }

    //
    public static void saveBitmapIndex(Bitmap bm, String picName) {
        try {
            if (bm == null) {
                return;
            }
            String path = getStorageDirectoryIndex();
            File folderFile = new File(path);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            //按尺寸压缩
//            if (bm.getWidth() > mPoint.x && bm.getHeight() > mPoint.y)
//                bm = Bitmap.createBitmap(bm, 0, 0, mPoint.x, mPoint.y);

            File file = new File(path + File.separator + picName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

//            if (!isFileExist(INDEXPATH, "")) {
//                File tempf = createSDDir(INDEXPATH, "");
//            }
//            File f = new File(INDEXPATH, picName );
//            if (f.exists()) {
//                f.delete();
//            }
//            f.createNewFile();
//            //按尺寸压缩
//            if (bm.getWidth() > mPoint.x && bm.getHeight() > mPoint.y)
//                bm = Bitmap.createBitmap(bm, 0, 0, mPoint.x, mPoint.y);
//
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmapAlbum(Bitmap bm, String picName) {
        try {
            if (bm == null) {
                return;
            }
            String path = getStorageDirectoryAlbum();
            File folderFile = new File(path);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            File file = new File(path + File.separator + picName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String path, String dirName) throws IOException {
        File dir = new File(path + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String path, String fileName) {
        File file = new File(path + fileName);
        if (file.length() == 0)
            return false;
        file.isFile();
        return file.exists();
    }

    public static void delFile(String path, String fileName) {
        File file = new File(path + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir(int pathtype) {
        String path = "";
        switch (pathtype) {
            case 1:
                path = getStorageDirectory();
                break;
            case 2:
                path = getStorageDirectoryIndex();
                break;
            case 3:
                path = getStorageDirectoryAlbum();
                break;
        }

        File dir = new File(path);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir(pathtype);
        }
        dir.delete();
    }

    public static void deleteDir(String path) {


        File dir = new File(path);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir(file.getPath());
        }
        dir.delete();
    }


    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    public static boolean fileIsExists(int type, String filename) {
        String path = "";
        switch (type) {
            case SDPATHINT:
                path = getStorageDirectory() + File.separator
                        + filename;
                break;
            case 2:
                path = getStorageDirectoryIndex() + File.separator + filename;
                break;
            case 3:
                path = getStorageDirectoryAlbum() + File.separator
                        + filename;
                break;
        }

        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getBitmapPath(int pathtype, String fileName) {
        switch (pathtype) {
            case 1:
                return getStorageDirectory() + File.separator
                        + fileName;

            case 2:
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(getStorageDirectoryIndex() + File.separator + fileName , options);
//               // Log.i("TAG", "-->width x " + options.outWidth + "-" + mPoint.x);
//                int size = (int) Math.floor(options.outWidth / mPoint.x);
//               // Log.i("TAG", "-->size " + size);
//                if (size > 0)
//                    options.inSampleSize = size;
//                options.inJustDecodeBounds = false;
//                options.inDither = false;
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                 return BitmapFactory.decodeFile(getStorageDirectoryIndex() + File.separator + fileName );

                break;
            case 3:
                return getStorageDirectoryAlbum() + File.separator
                        + fileName;
        }
        return null;
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(int path, String fileName) {
        switch (path) {
            case 1:
                if (fileIsExists(path, fileName))
                    return BitmapFactory.decodeFile(getStorageDirectory() + File.separator
                            + fileName);
                break;
            case 2:
                if (fileIsExists(path, fileName))
                    return BitmapFactory.decodeFile(getStorageDirectoryIndex() + File.separator + fileName);
                break;
            case 3:
                if (fileIsExists(path, fileName))
                    return BitmapFactory.decodeFile(getStorageDirectoryAlbum() + File.separator
                            + fileName);
                break;
        }
        return null;
    }

    public static String getPath(int type, String fileName) {
        switch (type) {
            case 1:
                return getStorageDirectory() + File.separator
                        + fileName;
            case 2:
                return getStorageDirectoryIndex() + File.separator + fileName;
            case 3:
                return getStorageDirectoryAlbum() + File.separator
                        + fileName;
        }
        return null;
    }


    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile(int pathtype) {
        String path = "";
        switch (pathtype) {
            case 1:
                path = getStorageDirectory();
                break;
            case 2:
                path = getStorageDirectoryIndex();
                break;
            case 3:
                path = getStorageDirectoryAlbum();
                break;
        }
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }

        dirFile.delete();
    }

    public static String isFolderExist(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        boolean rs = (folder.exists() && folder.isDirectory()) ? true : folder
                .mkdirs();
        return folder.getAbsolutePath();
    }

}
