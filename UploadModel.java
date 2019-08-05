package com.stanleyj.android.travelmantics;

/**
 * Created by Stanley on 2019/06/21.
 */
public class UploadModel {
    String title;
    String description;
    String image;
    String itemID;
    int price;

    public UploadModel() {
    }

    public UploadModel(String productName, String image1,
                       String description, String itemID, int price) {
        this.title = productName;
        this.image = image1;
        this.itemID = itemID;
        this.description = description;
        this.price = price;

    }

}
