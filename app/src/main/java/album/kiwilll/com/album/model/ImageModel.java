package album.kiwilll.com.album.model;

import java.io.Serializable;

/**
 * Created by wei on 2015/10/10.
 */
public class ImageModel implements Serializable {

    private int id;
    private String path = "";
    private int width;
    private int height;
    private String type = "";
    private int order;
    private String name = "";
    private int imageableId;
    private String imageableType;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active;

    @Override
    public String toString() {
        return "{path:" + path + ",height:" + height + ",type:" + type + ",width:" + width + ",name:" + name + ",order:" + order + ",imageableid:" + imageableId + ",imagetype:" + imageableType + "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageModel() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getImageableId() {
        return imageableId;
    }

    public void setImageableId(int imageableId) {
        this.imageableId = imageableId;
    }

    public String getImageableType() {
        return imageableType;
    }

    public void setImageableType(String imageableType) {
        this.imageableType = imageableType;
    }


}
