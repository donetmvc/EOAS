package com.eland.android.eoas.Views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eland.android.eoas.R;

/**
 * Created by liu.wenbin on 15/12/4.
 */
public class PhoneNumberView {

    public static Dialog showNumActionSheete(Context context,
                                             final OnActionSheetSelected onActionSheetSelected) {
        final Dialog dialog = new Dialog(context, R.style.numberActionSheet);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.number_action_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        final EditTextView edit = (EditTextView) layout.findViewById(R.id.edt_tel);
        ImageView image = (ImageView) layout.findViewById(R.id.img_save);

//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onActionSheetSelected.onClick(0);
//            }
//        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActionSheetSelected.onClick(edit.getText().toString());
            }
        });

        Window w = dialog.getWindow();
        //使dialog跟随键盘的移动而往上移动
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(layout);
        dialog.show();

        return dialog;
    }

    public interface OnActionSheetSelected {
        void onClick(String numValue);
    }
}
