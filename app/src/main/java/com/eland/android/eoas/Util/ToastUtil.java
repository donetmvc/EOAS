package com.eland.android.eoas.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.R;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class ToastUtil {

    private static Toast toast = null;
    private static TextView textView ;

    public static void showToast(Context context,int textId,int showTime){
        if(toast == null){
            LayoutInflater inflater = LayoutInflater.from(context) ;
            View view = inflater.inflate(R.layout.toast_view,null) ;
            textView = (TextView) view.findViewById(R.id.toast_text);
            textView.setText(textId);
            //textView.setTypeface(ElandApplication.systemTypeface);
            toast = new Toast(context) ;
            toast.setView(view);
            toast.setDuration(showTime);
            //   toast.setGravity(Gravity.BOTTOM,0,50);
        }else{
            textView.setText(textId);
        }
        toast.show();
    }

    public static void showToast(Context context,String str,int showTime){
        if(toast == null){
            LayoutInflater inflater = LayoutInflater.from(context) ;
            View view = inflater.inflate(R.layout.toast_view,null) ;
            textView = (TextView) view.findViewById(R.id.toast_text);
            textView.setText(str);
            //textView.setTypeface(ElandApplication.systemTypeface);
            toast = new Toast(context) ;
            toast.setView(view);
            toast.setDuration(showTime);
            //  toast.setGravity(Gravity.BOTTOM,0,50);
        }else{
            textView.setText(str);
        }
        toast.show();
    }
}
