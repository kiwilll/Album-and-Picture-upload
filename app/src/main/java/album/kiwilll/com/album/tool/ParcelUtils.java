package album.kiwilll.com.album.tool;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class ParcelUtils {

    final static int EXIST_SEPARATOR = 1;
    final static int NON_SEPARATOR = 0;

    public static void writeToParcel(Parcel out, String str) {
        if (str != null) {
            out.writeInt(EXIST_SEPARATOR);
            out.writeString(str);
        } else {
            out.writeInt(NON_SEPARATOR);
        }
    }

    public static String readStringFromParcel(Parcel in) {
        int flag = in.readInt();
        if (flag == EXIST_SEPARATOR) {
            return in.readString();
        } else {
            return null;
        }
    }

    public static <T extends Parcelable> T readFromParcel(Parcel in, Class<T> cls) {
        int flag = in.readInt();
        if (flag == EXIST_SEPARATOR) {
            return in.readParcelable(cls.getClassLoader());
        } else {
            return null;
        }
    }

    public static <T extends Parcelable> void writeToParcel(Parcel out, T model) {
        if (model != null) {
            out.writeInt(EXIST_SEPARATOR);
            out.writeParcelable(model, 0);
        } else {
            out.writeInt(NON_SEPARATOR);
        }
    }

    public static <T extends Parcelable> void writeToParcel(Parcel out, long in) {
        out.writeInt(EXIST_SEPARATOR);
        out.writeLong(in);
    }

    public static <T extends Parcelable> void writeToParcel(Parcel out, int in) {
        out.writeInt(EXIST_SEPARATOR);
        out.writeInt(in);
    }

    public static long readLongFromParcel(Parcel in) {
        int flag = in.readInt();
        if (flag == EXIST_SEPARATOR) {
            return in.readLong();
        } else {
            return 0;
        }
    }

    public static int readIntFromParcel(Parcel in) {
        int flag = in.readInt();
        if (flag == EXIST_SEPARATOR) {
            return in.readInt();
        } else {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> List<T> readListFromParcel(Parcel in, Class<T> cls) {
        int flag = in.readInt();
        if (flag == EXIST_SEPARATOR) {
            return in.readArrayList(cls.getClassLoader());
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> void writeListToParcel(Parcel out, List<T> collection) {
        if (collection != null) {
            out.writeInt(EXIST_SEPARATOR);
            out.writeParcelableArray((T[]) collection.toArray(), 0);
        } else {
            out.writeInt(NON_SEPARATOR);
        }
    }

}
