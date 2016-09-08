package album.kiwilll.com.album.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

import album.kiwilll.com.album.R;
import album.kiwilll.com.album.model.AlbumBean;
import album.kiwilll.com.album.tool.NativeImageLoader;

/**
 * Created by wei on 2015/11/3.
 */
public class AlbumAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<AlbumBean> albums;
    private Point mPoint = new Point(0, 0);
    ListView mListView;

    public AlbumAdapter(Context context, int mwidth, ListView mListView) {
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mListView = mListView;
        mPoint.set(mwidth, mwidth);
    }

    public void setData(List<AlbumBean> albums) {
        this.albums = albums;
    }

    @Override
    public int getCount() {
        return albums == null || albums.size() == 0 ? 0 : albums.size();
    }

    @Override
    public Object getItem(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(
                    R.layout.list_dir_item, null);
            viewHolder.album_count = (TextView) convertView
                    .findViewById(R.id.id_dir_item_count);
            viewHolder.album_name = (TextView) convertView
                    .findViewById(R.id.id_dir_item_name);
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.id_dir_item_image);
//            viewHolder.mImageView
//                    .setOnMeasureListener(new MyImageView.OnMeasureListener() {
//
//                        @Override
//                        public void onMeasureSize(int width, int height) {
//                            mPoint.set(width, height);
//                        }
//                    });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView
                    .setImageResource(R.mipmap.ic_launcher);
        }
        final AlbumBean b = (AlbumBean) getItem(position);
        viewHolder.mImageView.setTag(b.thumbnail);

        viewHolder.album_name.setText(b.folderName);
        viewHolder.album_count.setText(b.count + "");

        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
                b.thumbnail, mPoint, new NativeImageLoader.NativeImageCallBack() {

                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mListView
                                .findViewWithTag(b.thumbnail);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            //viewHolder.mImageView.setImageDrawable(null);
            viewHolder.mImageView.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }

    public static class ViewHolder {
        public ImageView mImageView;
        public TextView album_name;
        public TextView album_count;
    }
}
