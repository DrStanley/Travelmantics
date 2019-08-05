package com.stanleyj.android.travelmantics;

/**
 * Created by Stanley on 2019/06/21.
 */
public class DownloadModel {
    String title;
    String description;
    String image;
    String itemID;
    int price;

    public DownloadModel() {
    }

    public DownloadModel(String productName, String image1,
                         String description, int price) {

    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
