package com.wiyixiao.lzone.bean;

public class IconItemBean {
    private String iconName;
    private int imageId;

    public IconItemBean(String iconName, int imageId) {
        super();
        this.iconName = iconName;
        this.imageId = imageId;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
