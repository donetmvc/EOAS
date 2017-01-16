package com.eland.android.eoas.Service;


import android.content.Context;

import com.eland.android.eoas.Application.EOASApplication;
import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.eland.android.eoas.Util.SystemMethodUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 15/12/16.
 */
public class ContactService {

    private Context context;

    public ContactService(Context context) {
        this.context = context;
    }

    public void searchContact(final String userId, final IOnSearchContactListener iOnSearchContactListener) {
        String uri = "api/contact";

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("type", "123");
        params.put("tips", "321");

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<LoginInfo> list = new ArrayList<LoginInfo>();
                LoginInfo dto = null;

                if(null != response && response.length() > 0) {
                    try {
                        for(int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            String userIds = object.getString("userIDField");
                            String cellNo = object.getString("cellNoField");
                            String backCellNo = object.getString("backCellNoField");
                            String backName = object.getString("backNameField");

                            //没名没姓没电话号码的一律不显示作为惩戒，任性。
                            if(userIds.isEmpty() || userIds.equals("null")
                                    || userIds.equals(userId)
                                    || cellNo.equals("null")) {
                                continue;
                            }

                            dto = new LoginInfo();
                            dto.userId = userIds;
                            dto.cellNo = cellNo;
                            dto.userName = object.getString("userNameField");
                            dto.email = userIds + "@eland.co.kr";
                            dto.backCellNo = backCellNo;
                            dto.backName = backName;
                            dto.url = EOASApplication.getInstance().photoUri + userIds.replace(".", "") + ".jpg";


                            //list中已存在的人员不再添加
                            if(SystemMethodUtil.isContains(list, userIds)) {
                                continue;
                            }

                            list.add(dto);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    iOnSearchContactListener.onSearchSuccess(list);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnSearchContactListener.onSearchFailure(99999, "连接服务器失败");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnSearchContactListener.onSearchFailure(99999, "连接服务器失败");
            }
        });
    }

    public void cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true);
    }

    public interface IOnSearchContactListener {
        void onSearchSuccess(ArrayList<LoginInfo> list);
        void onSearchFailure(int code, String msg);
    }

}
