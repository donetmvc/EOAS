package com.eland.android.eoas.Util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eland.android.eoas.R;
import com.rey.material.app.Dialog;

/**
 * Created by elandmac on 15/12/7.
 */
public class ChooseImageUtil {

    private IOnAlbumListener iOnAlbumListener;
    private IOnCarmerListener iOnCarmerListener;

    public Dialog showChooseImageDialog(Context context) {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.chooseimage_layout, null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setContentView(view);

        TextView txt_pic_album = (TextView) view.findViewById(R.id.album_pic);
        TextView txt_pic_carmer = (TextView) view.findViewById(R.id.camera_pic);

        txt_pic_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != iOnAlbumListener) {
                    iOnAlbumListener.onAlbumClick();
                }
            }
        });

        txt_pic_carmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != iOnCarmerListener) {
                    iOnCarmerListener.onCarmerClick();
                }
            }
        });

        dialog.show();

        return dialog;
    }

    public void setOnAlbumListener(IOnAlbumListener iOnAlbumListener) {
        this.iOnAlbumListener = iOnAlbumListener;
    }

    public interface IOnAlbumListener {
        void onAlbumClick();
    }

    public void setOnCarmerListener(IOnCarmerListener iOnCarmerListener) {
        this.iOnCarmerListener = iOnCarmerListener;
    }

    public interface IOnCarmerListener {
        void onCarmerClick();
    }
}
