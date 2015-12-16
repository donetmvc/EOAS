package com.eland.android.eoas.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.eland.android.eoas.Model.Constant;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class SharedReferenceHelper {
    private static SharedReferenceHelper sharedPreferencesHelper = null;
    private SharedPreferences sharedPreferences;

    //实例化
    public SharedReferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(Constant.GLOBLE_SETTING, context.MODE_PRIVATE);
    }

    public static SharedReferenceHelper getInstance(Context context) {
        if (sharedPreferencesHelper == null) {
            sharedPreferencesHelper = new SharedReferenceHelper(context);
        }
        return sharedPreferencesHelper;
    }

    //设置缓存值
    public void setValue(String key,String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    //获取缓存值
    public String getValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    //清理缓存的数据
    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
