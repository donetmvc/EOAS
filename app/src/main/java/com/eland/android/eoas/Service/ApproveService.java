package com.eland.android.eoas.Service;

import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 16/1/7.
 */
public class ApproveService {

    public static void searchApplyInfo(String userId, String applyId, final IOnApproveListener iOnApproveListener) {
        String uri = "api/Push";
        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("applyId", applyId);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                String content = "";
                String reason = "";

                try {
                    if(null != response && null != response.getJSONObject(0)) {
                        JSONObject obj = response.getJSONObject(0);
                        content = "【" + obj.getString("applyUserName") + "】"
                                + "申请了" + "【" + obj.getString("startDate")
                                + "】~【" + obj.getString("endDate") + "】的【" + obj.getString("vacationTypeName") + "】";
                        reason = obj.getString("reason");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                iOnApproveListener.onApproveSucess(content, reason);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时");
            }
        });
    }

    public static void saveApprove(String userId, final String approveCode, String approveReason, String applyId, final IOnApproveListener iOnApproveListener) {
        String uri = "api/Push";
        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("applyId", applyId);
        params.add("approveStateCode", approveCode);
        params.add("approveStateReason", approveReason);

        HttpRequstUtil.get(uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if(!responseString.isEmpty() && responseString.equals("\"审批执行成功\"")) {
                    iOnApproveListener.onApproveSucess("OK", approveCode);
                }
                else {
                    iOnApproveListener.onApproveFailure(99999, "审批失败，请重试");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时");
            }
        });
    }

    public interface IOnApproveListener {
        void onApproveSucess(String content, String reason);
        void onApproveFailure(int code, String msg);
    }
}
