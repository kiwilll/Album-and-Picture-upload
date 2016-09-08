package album.kiwilll.com.album.model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import album.kiwilll.com.album.tool.MyBimp;
import album.kiwilll.com.album.tool.ParcelUtils;
import album.kiwilll.com.album.tool.Utils;

public class ImageBean implements Parcelable, Serializable {

    public int id;
    public String parentName;
    public long size;
    public String displayName;
    public String path;
    public boolean isChecked;
    public boolean isedit = false;
    public int rotation;
    public int position;
    public int width;
    public int height;
    public boolean isLarge;
    private ExecutorService mImageThreadPool = null;
    public File imageFile;
    public boolean isNetPic = false;
    public String type;

    public byte[] result;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        try {
            ParcelUtils.writeToParcel(parcel, parentName);
            ParcelUtils.writeToParcel(parcel, displayName);
            ParcelUtils.writeToParcel(parcel, path);
            ParcelUtils.writeToParcel(parcel, size);
            ParcelUtils.writeToParcel(parcel, String.valueOf(isChecked));
            ParcelUtils.writeToParcel(parcel, String.valueOf(isedit));
            parcel.writeInt(rotation);
            parcel.writeInt(position);
            parcel.writeInt(width);
            parcel.writeInt(height);
            ParcelUtils.writeToParcel(parcel, String.valueOf(isLarge));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

    }


    public ImageBean(Parcel in) {
        try {
            path = ParcelUtils.readStringFromParcel(in);
            parentName = ParcelUtils.readStringFromParcel(in);
            displayName = ParcelUtils.readStringFromParcel(in);
            size = ParcelUtils.readLongFromParcel(in);
            isChecked = Boolean.getBoolean(ParcelUtils.readStringFromParcel(in) == null ? "false" : ParcelUtils.readStringFromParcel(in));
            isedit = Boolean.getBoolean(ParcelUtils.readStringFromParcel(in) == null ? "false" : ParcelUtils.readStringFromParcel(in));
            isLarge = Boolean.getBoolean(ParcelUtils.readStringFromParcel(in) == null ? "false" : ParcelUtils.readStringFromParcel(in));
            rotation = in.readInt();
            position = in.readInt();
            width = in.readInt();
            height = in.readInt();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {

        @Override
        public ImageBean createFromParcel(Parcel source) {

            return new ImageBean(source);

        }

        @Override
        public ImageBean[] newArray(int size) {

            return new ImageBean[size];

        }

    };

    public ImageBean() {
        super();
    }

    public ImageBean(String path) {
        super();
        this.path = path;
    }

    public ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    mImageThreadPool = Executors.newFixedThreadPool(1);
                }
            }
        }

        return mImageThreadPool;

    }

    public ImageBean(String parentName, long size, String displayName,
                     String path, boolean isChecked, int rotation, boolean isLarge) {
        super();
        this.parentName = parentName;
        this.size = size;
        this.displayName = displayName;
        this.path = path;
        this.isChecked = isChecked;
        this.rotation = rotation;
        this.isLarge = isLarge;
    }

    public Bitmap bitmap;

    public Bitmap getBitmap() {
        if (bitmap == null || bitmap.isRecycled()) {
            try {
                //bitmap = MyBimp.revitionImageSize(path, rotation);
                bitmap = MyBimp.getBitmapFromFile(path, rotation);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void recy() {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
        if (result != null)
            result = null;
        if (imageFile != null) {
            imageFile = null;
        }
    }

    public Bitmap getBitmapLocal() {
        if (bitmap == null || bitmap.isRecycled()) {
            try {
                bitmap = MyBimp.revitionImageSize(path, rotation);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public void getBitmap(final onBitmapLoadListener listener) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listener.onBitmapLoad((byte[]) msg.obj);
            }
        };
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                bitmap = getBitmap();// MyBimp.revitionImageSize(path, rotation);
                result = Utils.Bitmap2Bytes(bitmap, size);

                Message msg = handler.obtainMessage();
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    public void getBitmapLocal(final onBitmapLoadListener listener) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listener.onBitmapLoad((byte[]) msg.obj);
            }
        };
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (!isLarge) {
                    bitmap = getBitmapLocal();
                    result = Utils.Bitmap2Bytes(bitmap, size);
//                    bitmap.recycle();
                } else {
                    imageFile = new File(path);
                    result = Utils.getBytesFromFile(imageFile);
                    imageFile.deleteOnExit();
                }
                Message msg = handler.obtainMessage();
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public interface onBitmapLoadListener {
        void onBitmapLoad(byte[] result);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public File getImageFile() {
        if (imageFile == null) {
            try {
                imageFile = new File(path);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return imageFile;
    }

    @Override
    public String toString() {
        return "ImageBean [parentName=" + parentName + ", size=" + size
                + ", displayName=" + displayName + ", path=" + path
                + ", isChecked=" + isChecked + "]";
    }

}
