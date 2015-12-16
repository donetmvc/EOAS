package com.eland.android.eoas.Util;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.eland.android.eoas.R;
import com.rey.material.widget.ProgressView;
import com.victor.loading.rotate.RotateLoading;

/**
 * Created by liu.wenbin on 15/11/12.
 * 加载框
 */
public class ProgressUtil {

    private static ProgressView progressView;
    private static RotateLoading rotateLoading;
    private static String TAG = "EOAS";

    public static Dialog showProgress(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context) ;
        View view = inflater.inflate(R.layout.progress_view,null) ;
        progressView = (ProgressView) view.findViewById(R.id.progress);
        progressView.start();
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return dialog;
    }

    public static com.rey.material.app.Dialog showHttpLoading(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context) ;
        View view = inflater.inflate(R.layout.http_loading,null) ;

        rotateLoading = (RotateLoading) view.findViewById(R.id.rotateloading);
        rotateLoading.start();
        com.rey.material.app.Dialog dialog = new com.rey.material.app.Dialog(context);
        dialog.setContentView(view);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return dialog;
    }
}
