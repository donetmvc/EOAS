package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Model.ProxyInfo;
import com.eland.android.eoas.Model.ProxyUserInfo;
import com.eland.android.eoas.Model.SaveProxyInfo;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by liu.wenbin on 16/1/6.
 */
public class ProxyService {

    private Context context;

    public ProxyService(Context context) {
        this.context = context;
    }

    public void searchProxyInfo(String userId, String startDate, String endDate, final IOnSearchProxyInfoListener iOnSearchProxyInfoListener) {
        String uri = "api/ActingEmp";

        RequestParams params = new RequestParams();
        params.add("StartDate", startDate);
        params.add("EndDate", endDate);
        params.add("userId", userId);

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                List<ProxyInfo> infoList = new ArrayList<ProxyInfo>();
                ProxyInfo proxyInfo;
                List<ProxyUserInfo> userInfo;// = new ArrayList<ProxyUserInfo>();
                ProxyUserInfo proxyUserInfo;

                try {
                    if(null != response) {
                        for(int i = 0; i < response.length(); i++) {
                            userInfo = new ArrayList<ProxyUserInfo>();
                            JSONObject obj = response.getJSONObject(i);
                            proxyInfo = new ProxyInfo();
                            proxyInfo.OrgCode = obj.getString("OrganizationID");
                            proxyInfo.OrgName = obj.getString("OrganizationName");

                            JSONArray newArray = obj.getJSONArray("AgentInfo");

                            if(null != newArray && newArray.length() > 0) {
                                for(int j = 0; j < newArray.length(); j++) {
                                    JSONObject newObj = newArray.getJSONObject(j);
                                    proxyUserInfo = new ProxyUserInfo();
                                    proxyUserInfo.EmpId = newObj.getString("EmpID");
                                    proxyUserInfo.UserName = newObj.getString("UserName");

                                    userInfo.add(proxyUserInfo);
                                }
                            }

                            proxyInfo.proxyInfoList = userInfo;
                            infoList.add(proxyInfo);
                        }
                    }

                    iOnSearchProxyInfoListener.onSuccess(infoList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时");
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时");
            }
        });

    }

    public void saveProxy(List<SaveProxyInfo> list, final IOnSearchProxyInfoListener iOnSearchProxyInfoListener) {
        String uri = "api/ActingEmp";

        JSONArray array = new JSONArray();
        JSONObject obj;
        StringEntity stringEntity = null;
        for(int i = 0; i < list.size(); i++) {
            obj = new JSONObject();
            try {
                obj.put("empId", list.get(i).empId);
                obj.put("orgId", list.get(i).orgId);
                obj.put("startDate", list.get(i).startDate);
                obj.put("endDate", list.get(i).endDate);
                obj.put("userId", list.get(i).userId);
                array.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            stringEntity = new StringEntity(array.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpRequstUtil.post(context, uri, stringEntity, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                 iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if(responseString.equals("\"OK\"")) {
                    iOnSearchProxyInfoListener.onFailure(1000, "保存成功");
                }
                else {
                    iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时");
                }
            }
        });
    }

    public void cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true);
    }

    public interface IOnSearchProxyInfoListener {
        void onSuccess(List<ProxyInfo> list);
        void onFailure(int code, String msg);
    }
}
