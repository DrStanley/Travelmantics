package com.stanleyj.android.travelmantics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Stanley on 2019/06/24.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    private ViewHolder.ClickListener mClickListener;

    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());

                return true;
            }
        });
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    //    set details to recycler view row
    public void setDetail(Context ctx, String productsName, String descrp,
                           String image1,String itemID,int price) {
//        get views
        TextView pName = (TextView) mView.findViewById(R.id.rtitle);
        TextView pCategory = (TextView) mView.findViewById(R.id.descrip);
        TextView itm = (TextView) mView.findViewById(R.id.item);
        TextView prize = (TextView) mView.findViewById(R.id.price);
        ImageView rImage1 = (ImageView) mView.findViewById(R.id.rImage1);
        try {

//        set data to views
            pName.setText(productsName);
            pCategory.setText(descrp);
            itm.setText(itemID);
            prize.setText(""+price);
            rImage1.setImageBitmap(decodeFromFirebaseBase64(image1));
        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Error Alert");
            alertDialog.setMessage("The following Error occurred\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }


    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    // interface to send call backs
    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
