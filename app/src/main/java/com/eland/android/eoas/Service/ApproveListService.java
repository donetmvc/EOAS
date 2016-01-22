package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Model.ApproveListInfo;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 16/1/7.
 */
public class ApproveListService {

    private Context context;

    public ApproveListService(Context context) {
        this.context = context;
    }

    public void searchApproveList(String userId, final IOnApproveListListener onApproveListListener) {
        String uri = "api/Push";
        RequestParams params = new RequestParams("userId", userId);

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                List<ApproveListInfo> list = new ArrayList<ApproveListInfo>();
                ApproveListInfo dto;
                JSONObject obj;

                if(response != null) {
                    try {
                        for(int i = 0; i < response.length(); i++) {
                            dto = new ApproveListInfo();
                            obj = response.getJSONObject(i);
                            dto.vacationType = obj.getString("vacationType");
                            dto.applyUserName = obj.getString("applyUserName");
                            dto.applyId = obj.getString("applyID");
                            dto.applyPeriod = obj.getString("applyPeriod");
                            dto.reason = obj.getString("reason");
                            dto.vacationDays = obj.getString("vacationDays");
                            dto.vacationTypeName = obj.getString("vacationTypeName");

                            list.add(dto);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(null != onApproveListListener) {
                    onApproveListListener.onSuccess(list);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(null != onApproveListListener) {
                    onApproveListListener.onFailure(99999, "连接服务器超时");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if(null != onApproveListListener) {
                    onApproveListListener.onFailure(99999, "连接服务器超时");
                }
            }
        });
    }

    public void cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true);
    }

    public interface IOnApproveListListener {
        void onSuccess(List<ApproveListInfo> list);
        void onFailure(int code, String msg);
    }
}
