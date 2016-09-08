package album.kiwilll.com.album.tool;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import album.kiwilll.com.album.model.AlbumBean;
import album.kiwilll.com.album.model.ImageBean;

public class AlbumHelper {

    Context context;
    ContentResolver contentResolver;

    private static AlbumHelper instance;

    private AlbumHelper(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public static AlbumHelper newInstance(Context context) {
        if (instance == null) {
            instance = new AlbumHelper(context);
        }
        return instance;
    }

    ;

    public List<AlbumBean> getFolders() {

        List<AlbumBean> mAlbumBeans = new ArrayList<AlbumBean>();
        try {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor mCursor = contentResolver.query(mImageUri, null,
                            MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    MediaStore.Images.Media.DATE_ADDED);

//            Cursor mCursor = contentResolver.query(mImageUri, null, "(" +
//                            MediaStore.Images.Media.MIME_TYPE + "=? or "
//                            + MediaStore.Images.Media.MIME_TYPE + "=? or "
//                            + MediaStore.Images.Media.MIME_TYPE + "=?) and "
//                            + MediaStore.Images.Media.DATA + " not like '%zhongcm%'",
//                    new String[]{"image/jpeg", "image/png", "image/jpg"},
//                    MediaStore.Images.Media.DATE_MODIFIED);
            HashMap<String, List<ImageBean>> map = capacity(mCursor);
            Log.i("TAG", "-->getfolders map " + map.size() + "-" + mCursor.getCount());

            Set<Map.Entry<String, List<ImageBean>>> set = map.entrySet();
            for (Iterator<Map.Entry<String, List<ImageBean>>> iterator = set
                    .iterator(); iterator.hasNext(); ) {

                Map.Entry<String, List<ImageBean>> entry = iterator.next();
                String parentName = entry.getKey();
                ImageBean b = entry.getValue().get(0);
                AlbumBean tempAlbumBean = new AlbumBean(parentName, entry
                        .getValue().size(), entry.getValue(), b.path);
                // 在第0个位置加入了拍照图片
                if (FileUtils.fileIsExists(b.path)) {
                    tempAlbumBean.sets.add(0, new ImageBean());
                    mAlbumBeans.add(tempAlbumBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mAlbumBeans;
    }

    public void getFolders(final onFoldersLoadListener loadListener) {
        List<AlbumBean> list = getFolders();
        loadListener.onLoadComplete(list);
    }

    public void getImages(onImageBeanLoadListenter listenter) {
        List<ImageBean> imageBeans = new ArrayList<>();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor mCursor = contentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? ",
                new String[]{"image/jpeg", "image/png", "image/jpg"},
                MediaStore.Images.Media.DATE_ADDED + " desc ");

        List<String> mybitpathlist = MyBimp.getOrrinPathLsit();
        int i = 0;
        while (mCursor.moveToNext()) {
            i++;
            int rotation = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            boolean ischeck = false;
            if (mybitpathlist.contains(path))
                ischeck = true;


            long size = mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.SIZE));
            float height = (float) mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.HEIGHT));
            float width = (float) mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.WIDTH));
            float scale = height / width;
            boolean islarge = false;
            if (scale > 2)
                islarge = true;

            String display_name = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

            String parentName = new File(path).getParentFile().getName();
            // Log.i("TAG", "-->parentname " + parentName);
            if (parentName.equals("index") || parentName.equals("pic") || parentName.equals("album"))
                continue;
            if (FileUtils.fileIsExists(path)) {
                imageBeans.add(new ImageBean(parentName, size, display_name, path,
                        ischeck, rotation, islarge));
            }
        }
        listenter.onLoadComplete(imageBeans);
    }

    private HashMap<String, List<ImageBean>> capacity(Cursor mCursor) {

        HashMap<String, List<ImageBean>> beans = new HashMap<String, List<ImageBean>>();
        int i = 0;
        while (mCursor.moveToNext()) {
            int rotation = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            long size = mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.SIZE));

//            Log.i("TAG", "-->size capacity" + mCursor.getLong(mCursor
//                    .getColumnIndex(MediaStore.Images.Media.HEIGHT)));
            float height = (float) mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.HEIGHT));
            float width = (float) mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.WIDTH));
            float scale = height / width;
            boolean islarge = false;
            if (scale > 2)
                islarge = true;


            String display_name = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

            String parentName = new File(path).getParentFile().getName();
            List<ImageBean> sb;
            if (beans.containsKey(parentName)) {
                sb = beans.get(parentName);
                sb.add(new ImageBean(parentName, size, display_name, path,
                        false, rotation, islarge));
            } else {
                sb = new ArrayList<ImageBean>();
                sb.add(new ImageBean(parentName, size, display_name, path,
                        false, rotation, islarge));
            }
            beans.put(parentName, sb);
        }
        return beans;
    }

    public interface onImageBeanLoadListenter {
        void onLoadComplete(List<ImageBean> list);
    }

    public interface onFoldersLoadListener {
        void onLoadComplete(List<AlbumBean> list);
    }
}
