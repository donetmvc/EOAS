package com.eland.android.eoas.Util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.eland.android.eoas.R;
import com.rey.material.widget.ProgressView;
import com.victor.loading.rotate.RotateLoading;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by liu.wenbin on 15/11/12.
 * 加载框
 */
public class ProgressUtil {

    private static ProgressView progressView;
    private static RotateLoading rotateLoading;
    private static String TAG = "EOAS";

    public static IOnDialogConfirmListener iOnDialogConfirmListener;

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

    public static MaterialDialog showDialogUpdate(String message, String title, Context context) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);

        dialog.setPositiveButton(context.getResources().getString(R.string.update), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != iOnDialogConfirmListener) {
                    iOnDialogConfirmListener.OnDialogConfirmListener();
                }
            }
        }).setNegativeButton(context.getResources().getString(R.string.refuse), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public void setOnDialogConfirmListener(IOnDialogConfirmListener iOnDialogConfirmListener) {
        this.iOnDialogConfirmListener = iOnDialogConfirmListener;
    }

    public interface IOnDialogConfirmListener {
        void OnDialogConfirmListener();
    }
}
