package album.kiwilll.com.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import album.kiwilll.com.album.adapter.AlbumAdapter;
import album.kiwilll.com.album.adapter.PicSelectAdapter;
import album.kiwilll.com.album.model.AlbumBean;
import album.kiwilll.com.album.model.ImageBean;
import album.kiwilll.com.album.model.ImageModel;
import album.kiwilll.com.album.tool.AlbumHelper;
import album.kiwilll.com.album.tool.Config;
import album.kiwilll.com.album.tool.MyBimp;
import album.kiwilll.com.album.tool.UploadTool;
import album.kiwilll.com.album.tool.Utils;

public class MainActivity extends Activity implements
        OnItemClickListener {

    GridView gridView;
    PicSelectAdapter adapter;
    TextView album;
    static final int SCAN_OK = 0x1001;
    static boolean isOpened = false;
    PopupWindow popWindow;

    List<AlbumBean> mAlbumBean;


    int selected = 0;
    private TextView nextButton;

    private View parentView;

    private int mwidth;
    private int mheight;

    @Override
    protected void onDestroy() {
        gridView = null;
        adapter = null;
        album = null;
        popWindow = null;
        mAlbumBean = null;
        nextButton = null;
        parentView = null;
        System.gc();
        super.onDestroy();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mwidth = size.x;
        mheight = size.y;

        Config.setLimit(9);
        MyBimp.setMax(9);


        parentView = getLayoutInflater().inflate(R.layout.the_picture_selection, null);
        gridView = (GridView) parentView.findViewById(R.id.child_grid);


        setContentView(parentView);
        album = (TextView) this.findViewById(R.id.album);
        nextButton = (TextView) this.findViewById(R.id.next);
        album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpened && popWindow != null) {
                    WindowManager.LayoutParams ll = getWindow().getAttributes();
                    ll.alpha = 0.3f;
                    getWindow().setAttributes(ll);
                    popWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    if (popWindow != null) {
                        popWindow.dismiss();
                    }
                }

            }
        });

        adapter = new PicSelectAdapter(MainActivity.this, gridView, (mwidth - 50) / 4, onImageSelectedCountListener);

        gridView.setAdapter(adapter);
        adapter.setOnImageSelectedListener(onImageSelectedListener);

        if (MyBimp.AblumBitmap.size() == 0)
            showPic();
        else {
            Message msg = handler.obtainMessage();
            msg.what = 2;
            msg.obj = MyBimp.AblumBitmap;
            msg.sendToTarget();
        }

        gridView.setOnItemClickListener(this);
        initOnclick();
    }


    private PopupWindow showPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.list_dir, null);
        final PopupWindow mPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, mheight - Utils.dip2px(MainActivity.this, 48), true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation1);

        ListView listView = (ListView) view.findViewById(R.id.id_list_dir);
        AlbumAdapter albumAdapter = new AlbumAdapter(MainActivity.this, (mwidth - 50) / 4,
                listView);
        listView.setAdapter(albumAdapter);
        albumAdapter.setData(mAlbumBean);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams ll = getWindow().getAttributes();
                ll.alpha = 1f;
                getWindow().setAttributes(ll);
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AlbumBean b = (AlbumBean) parent.getItemAtPosition(position);
                adapter.setImageBeans(b.sets);
                album.setText(b.folderName);
                mPopupWindow.dismiss();
            }
        });
        return mPopupWindow;
    }

    private void initOnclick() {
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // List<ImageBean> selectBean = getSelectedItem();
                int len = MyBimp.tempSelectBitmap.size();

                if (len > 0) {
                    UploadTool.getInstance().uploadImages(new UploadTool.onUploadImagesListenter() {
                        @Override
                        public void onUploadDone(List<ImageModel> imageModelList, String error) {

                        }

                        @Override
                        public void onProgressUpdate(int position, int tatol) {

                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this,
                            "至少选择一张图片或者拍照",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void takePhoto() {
        //调用相机拍照
    }


    private void showPic() {
        //直接获取图片
//        AlbumHelper.newInstance(MainActivity.this).getImages(new AlbumHelper.onImageBeanLoadListenter() {
//            @Override
//            public void onLoadComplete(List<ImageBean> list) {
//                Message msg = handler.obtainMessage();
//                msg.what = SCAN_OK;
//                msg.obj = list;
//                msg.sendToTarget();
//            }
//        });
        //按相册获取图片
        AlbumHelper.newInstance(MainActivity.this).getFolders(new AlbumHelper.onFoldersLoadListener() {
            @Override
            public void onLoadComplete(List<AlbumBean> list) {
                Message msg = handler.obtainMessage();
                msg.what = 3;
                msg.obj = list;
                msg.sendToTarget();
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
//                    MyBimp.AblumBitmap.clear();
//                    ImageBean bean = new ImageBean();
//                    MyBimp.AblumBitmap.add(bean);
//                    MyBimp.AblumBitmap.addAll((List<ImageBean>) msg.obj);
//                    //mImageBean = (List<ImageBean>) msg.obj;//getmImageBean(mAlbumBean);
//                    if (MyBimp.AblumBitmap != null && MyBimp.AblumBitmap.size() != 0) {
//                        adapter.setImageBeans(MyBimp.AblumBitmap);
//                    } else {
//                        MyBimp.AblumBitmap = new ArrayList<>();
//                        adapter.setImageBeans(MyBimp.AblumBitmap);
//                    }
                    break;
                case 2:
                    adapter.setImageBeans(MyBimp.AblumBitmap);
                    mAlbumBean = MyBimp.AblumAll;
                    popWindow = showPopWindow();
                    break;
                case 3:
                    mAlbumBean = (List<AlbumBean>) msg.obj;
                    MyBimp.AblumAll = (ArrayList<AlbumBean>) mAlbumBean;

                    MyBimp.AblumBitmap.clear();

                    ImageBean bean = new ImageBean();
                    MyBimp.AblumBitmap.add(bean);

                    MyBimp.AblumBitmap.addAll(getAllBean());

                    if (MyBimp.AblumBitmap != null && MyBimp.AblumBitmap.size() != 0) {
                        adapter.setImageBeans(MyBimp.AblumBitmap);
                    } else {
                        MyBimp.AblumBitmap = new ArrayList<>();
                        adapter.setImageBeans(MyBimp.AblumBitmap);
                    }

                    if (mAlbumBean != null && mAlbumBean.size() != 0) {
                        popWindow = showPopWindow();
                    }
                    break;
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (position == 0) {
            takePhoto();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyBimp.clearAll();
    }


    OnImageSelectedCountListener onImageSelectedCountListener = new OnImageSelectedCountListener() {

        @Override
        public int getImageSelectedCount() {
            return selected;
        }
    };

    OnImageSelectedListener onImageSelectedListener = new OnImageSelectedListener() {

        @Override
        public void notifyChecked() {
            selected = getSelectedCount();
        }
    };


    private int getSelectedCount() {
        int count = 0;
        for (AlbumBean albumBean : mAlbumBean) {
            for (ImageBean b : albumBean.sets) {
                if (b.isChecked) {
                    count++;
                }
            }
        }
        return count;
    }


    private List<ImageBean> getAllBean() {
        int size = 0;
        List<ImageBean> list = new ArrayList<>();

        for (int i = 0, len = mAlbumBean.size(); i < len; i++) {
            list.addAll(mAlbumBean.get(i).sets);
            if (list.size() > 0 && size < list.size())
                list.remove(size);
            if (i < len - 1) {
                size = list.size();
            }
        }
        if (list.size() > 0 && size < list.size())
            list.remove(size);

        return list;
    }


    @Override
    protected void onRestart() {
        adapter.setImageBeans(MyBimp.AblumBitmap);
        super.onRestart();
    }

    public interface OnImageSelectedListener {
        void notifyChecked();
    }

    public interface OnImageSelectedCountListener {
        int getImageSelectedCount();
    }
}
