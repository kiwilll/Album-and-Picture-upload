package album.kiwilll.com.album.adapter;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import album.kiwilll.com.album.MainActivity;
import album.kiwilll.com.album.R;
import album.kiwilll.com.album.model.ImageBean;
import album.kiwilll.com.album.tool.Config;
import album.kiwilll.com.album.tool.MyBimp;
import album.kiwilll.com.album.tool.NativeImageLoader;

public class PicSelectAdapter extends BaseAdapter {

    Context context;
    private Point mPoint = new Point(0, 0);
    private List<ImageBean> imageBeans;
    private GridView mGridView;
    private int mwidth;
    private RelativeLayout.LayoutParams params;
    private LinearLayout.LayoutParams paramsL;

//    private MySurfaceViewImageView surfaceViewImageView;
//    private boolean ispre = false;
//    private Camera camera;
//    private Postprocessor postprocessor;

    MainActivity.OnImageSelectedListener onImageSelectedListener;
    MainActivity.OnImageSelectedCountListener onImageSelectedCountListener;

//    public void setCamera(Camera camera) {
//        this.camera = camera;
//    }

    private String action = "";
    private int num = 0;
    private int count;

    public void setAction(String action, int num) {
        this.num = num;
        this.action = action;
    }

    public PicSelectAdapter(Context context, GridView mGridView, final int mwidth,
                            MainActivity.OnImageSelectedCountListener onImageSelectedCountListener) {
        this.context = context;
        this.mGridView = mGridView;
        this.mwidth = mwidth;
        mPoint.set(mwidth / 2, mwidth / 2);
        this.onImageSelectedCountListener = onImageSelectedCountListener;
        params = new RelativeLayout.LayoutParams(mwidth, mwidth);
        paramsL = new LinearLayout.LayoutParams(mwidth, mwidth);
    }

    public void setImageBeans(List<ImageBean> imageBeans) {
        //  mybitpathlist = MyBimp.getOrrinPathLsit();
        //  Log.i("TAG", "-->mybitpathlist size " + mybitpathlist.size());
        this.imageBeans = imageBeans;
        notifyDataSetChanged();
    }

    public void setOnImageSelectedListener(
            MainActivity.OnImageSelectedListener onImageSelectedListener) {
        this.onImageSelectedListener = onImageSelectedListener;
    }

    @Override
    public int getCount() {
        return imageBeans == null || imageBeans.size() == 0 ? 0 : imageBeans.size();
    }


    @Override
    public Object getItem(int position) {
        return imageBeans == null || imageBeans.size() == 0 ? null : imageBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int index = position;
        final ImageBean ib = (ImageBean) getItem(index);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context,
                    R.layout.the_picture_selection_item, null);
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.child_image);
            viewHolder.gouxuan = (ImageView) convertView.findViewById(R.id.child_gouxuan);
            viewHolder.mImageView.setLayoutParams(params);
            viewHolder.tokephoto = (LinearLayout) convertView.findViewById(R.id.cameraview);
            viewHolder.tokephoto.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder.mImageView == null) {
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.child_image);
            viewHolder.mImageView.setLayoutParams(params);
        }

        if (ib != null && viewHolder.mImageView != null)
            viewHolder.mImageView.setTag(ib.path);


        if (position == 0) {
            viewHolder.tokephoto.setVisibility(View.VISIBLE);

            viewHolder.mImageView.setVisibility(View.INVISIBLE);
            viewHolder.gouxuan.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tokephoto.setVisibility(View.INVISIBLE);
            viewHolder.mImageView.setVisibility(View.VISIBLE);
            viewHolder.gouxuan.setVisibility(View.VISIBLE);


            viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count = onImageSelectedCountListener.getImageSelectedCount();

                    if (count == Config.limit && !ib.isChecked) {
                        Toast.makeText(context,
                                "只能选择" + Config.limit + "张照片",
                                Toast.LENGTH_SHORT).show();
                    } else {
//                        ImageItem imageItem = new ImageItem();
//                        imageItem.setImagePath(ib.path);
//                        imageItem.setRotation(ib.rotation);
                        if (!ib.isChecked) {
                            addAnimation(viewHolder.gouxuan);
                            ib.isChecked = true;
                            ib.isNetPic = false;

                            MyBimp.tempSelectBitmap.add(ib);
                        } else {
                            MyBimp.tempSelectBitmap.remove(ib);
                            ib.isChecked = false;
                        }

                    }
                    onImageSelectedListener.notifyChecked();
                    if (ib.isChecked) {
                        ib.position = count + num - 1;
                        viewHolder.gouxuan.setImageResource(R.mipmap.gouxuan);
                    } else {
                        viewHolder.gouxuan.setImageResource(R.mipmap.weigouxuan);
                    }
                }
            });
            if (ib != null && ib.isChecked) {
                ib.position = count + num - 1;
                viewHolder.gouxuan.setImageResource(R.mipmap.gouxuan);
            } else {
                viewHolder.gouxuan.setImageResource(R.mipmap.weigouxuan);
            }


//            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + ib.path))
//                    .setLocalThumbnailPreviewsEnabled(true)
//                    .setPostprocessor(postprocessor)
//                    .build();
//
//            PipelineDraweeController controller = (PipelineDraweeController)
//                    Fresco.newDraweeControllerBuilder()
//                            .setImageRequest(request)
//                            .setOldController(viewHolder.mImageView.getController())
//                            .build();
//            viewHolder.mImageView.setController(controller);


            Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
                    ib, mPoint, new NativeImageLoader.NativeImageCallBack() {

                        @Override
                        public void onImageLoader(Bitmap bitmap, String path) {
                            ImageView mImageView = (ImageView) mGridView
                                    .findViewWithTag(ib.path);
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
        }

        return convertView;
    }


    public static class ViewHolder {
        public LinearLayout tokephoto;
        public ImageView mImageView;
        public ImageView gouxuan;
    }

    /**
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
                1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }
}
