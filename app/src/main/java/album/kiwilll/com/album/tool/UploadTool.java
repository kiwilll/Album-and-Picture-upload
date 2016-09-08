package album.kiwilll.com.album.tool;

import java.util.ArrayList;
import java.util.List;

import album.kiwilll.com.album.model.ImageBean;
import album.kiwilll.com.album.model.ImageModel;

/**
 * Created by wei on 2016/8/18.
 */
public class UploadTool {
    private static UploadTool uploadTool = null;

    private UploadTool() {
    }

    public static UploadTool getInstance() {

        if (uploadTool == null) {
            uploadTool = new UploadTool();
        }
        return uploadTool;
    }

    int len;// = MyBimp.tempSelectBitmap.size();
    List<ImageModel> list = new ArrayList<>();
    int position;


    ArrayList<ImageBean> imageBeans;//背景图片墙上传

    private synchronized void upload(final int next, final onUploadImagesListenter listenter) {
        listenter.onProgressUpdate(next, len);
        if (next < len) {
            ImageBean item = MyBimp.tempSelectBitmap.get(next);
            if (next > 0) {
                MyBimp.tempSelectBitmap.get(next - 1).recy();
            }
            if (!item.isLarge) {
                item.getBitmapLocal(new ImageBean.onBitmapLoadListener() {
                    @Override
                    public void onBitmapLoad(byte[] result) {
                        if (result != null) {
                            //以byte[]上传
                        } else {
                            String error = "数据错误";
                            listenter.onUploadDone(list, error);
                        }
                    }
                });
            } else {
                //以路径上传
            }
        }
    }

    public void uploadImages(final onUploadImagesListenter listenter) {
        len = MyBimp.tempSelectBitmap.size();
        position = 0;
        upload(0, listenter);
    }


    public interface onUploadImagesListenter {
        void onUploadDone(List<ImageModel> imageModelList, String error);

        void onProgressUpdate(int position, int tatol);
    }
}
