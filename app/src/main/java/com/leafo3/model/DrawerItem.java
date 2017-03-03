package com.leafo3.model;

/**
 * Created by root on 9/08/15.
 */
public class DrawerItem {
    private String title;
    private int imageResource;

    public DrawerItem() {
    }

    public DrawerItem(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
