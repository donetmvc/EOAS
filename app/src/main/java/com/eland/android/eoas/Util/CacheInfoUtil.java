package com.eland.android.eoas.Util;

import android.content.Context;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.RegAutoInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by liu.wenbin on 15/12/7.
 */
public class CacheInfoUtil {

    private static final String TAG = "EOAS";

    public static void saveIsRegAuto(Context context, ArrayList<RegAutoInfo> data) {
        new DataCache<RegAutoInfo>().saveGlobal(context, data, Constant.EOAS_REGAUTO);
    }

    //判断是否自动设置出勤
    public static Boolean loadIsRegAuto(Context context, String userId) {
        ArrayList<RegAutoInfo> list = new DataCache<RegAutoInfo>().loadGlobal(context, Constant.EOAS_REGAUTO);

        boolean isReg = false;

        for(int i= 0; i < list.size(); i++) {
            if((list.get(i).userId.equals(userId)
                    || list.get(i).imei.equals(userId))
                    && list.get(i).isRegAuto.equals("TRUE")) {
                isReg = true;
                break;
            }
        }

        return isReg;
    }

    static class DataCache<T> {
        public void save(Context ctx, ArrayList<T> data, String name) {
            save(ctx, data, name, "");
        }

        public void saveGlobal(Context ctx, ArrayList<T> data, String name) {
            save(ctx, data, name, Constant.EOAS_CACHE);
        }

        private void save(Context ctx, ArrayList<T> data, String name,
                          String folder) {
            if (ctx == null) {
                return;
            }
            File file;
            if (!folder.isEmpty()) {
                File fileDir = new File(ctx.getFilesDir(), folder);
                ConsoleUtil.i(TAG, "----------创建缓存:-------------" + fileDir);
                if (!fileDir.exists() || !fileDir.isDirectory()) {
                    fileDir.mkdir();
                }
                file = new File(fileDir, name);
            } else {
                file = new File(ctx.getFilesDir(), name);
            }
            if (file.exists()) {
                file.delete();
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(file));
                oos.writeObject(data);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ArrayList<T> load(Context ctx, String name) {
            return load(ctx, name, "");
        }

        public ArrayList<T> loadGlobal(Context ctx, String name) {
            return load(ctx, name, Constant.EOAS_CACHE);
        }

        private ArrayList<T> load(Context ctx, String name, String folder) {
            ArrayList<T> data = null;

            File file;
            if (!folder.isEmpty()) {
                File fileDir = new File(ctx.getFilesDir(), folder);
                if (!fileDir.exists() || !fileDir.isDirectory()) {
                    fileDir.mkdir();
                }
                file = new File(fileDir, name);
            } else {
                file = new File(ctx.getFilesDir(), name);
            }
            if (file.exists()) {
                try {
                    ConsoleUtil.i(TAG, "----------读取缓存-------------" );
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    data = (ArrayList<T>) ois.readObject();
                    ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (data == null) {
                data = new ArrayList<T>();
            }
            return data;
        }
    }
}
