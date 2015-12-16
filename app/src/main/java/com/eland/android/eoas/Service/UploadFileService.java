package com.eland.android.eoas.Service;

import android.content.Context;
import android.os.Message;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by elandmac on 15/12/8.
 */
public class UploadFileService {

    private Context context;
    private IOnUploadListener iOnUploadListener;

    public UploadFileService(Context context) {
        this.context = context;
    }

    public void uploadFile(String path, String fileName, final Message message) {

        String uri = "api/UploadImage";

        File myFile = new File(path);
        RequestParams params = new RequestParams();
        try {
            params.put(fileName, myFile);

            HttpRequstUtil.post(uri, params, new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(null != iOnUploadListener) {
                        message.what = 0;
                        iOnUploadListener.onUploadSuccess(message);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(null != iOnUploadListener) {
                        message.what = 1;
                        iOnUploadListener.onUploadFailure(message);
                    }
                }
            });
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setUploadListener(IOnUploadListener iOnUploadListener) {
        this.iOnUploadListener = iOnUploadListener;
    }

    public interface IOnUploadListener {
        void onUploadSuccess(Message message);
        void onUploadFailure(Message message);
    }
}
