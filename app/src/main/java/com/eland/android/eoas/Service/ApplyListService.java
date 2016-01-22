package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Model.ApplyListInfo;
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
 * Created by liu.wenbin on 15/12/29.
 */
public class ApplyListService {

    private Context context;

    public ApplyListService(Context context) {
        this.context = context;
    }

    public void searchApplyList(String userId, String startDate, String endDate, final IOnSearchApplyListListener iOnSearchApplyListListener) {
        String uri = "api/Apply";

        RequestParams params = new RequestParams();
        params.add("userId", userId);
        params.add("startDate", startDate);
        params.add("endDate", endDate);
        params.add("searchType", "01"); //休假类型

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                List<ApplyListInfo> list = new ArrayList<ApplyListInfo>();
                ApplyListInfo dto = null;

                if(null != response && response.length() > 0) {
                    try {
                        for(int i = 0; i < response.length(); i++) {
                            dto = new ApplyListInfo();
                            JSONObject object = response.getJSONObject(i);

                            dto.processStateCode = object.getString("ProcessStateCode");
                            dto.vacationTypeName = object.getString("VacationTypeName");
                            dto.vacationDays = object.getString("VacationDays");
                            dto.vacationPeriod = object.getString("VacationPeriod");
                            dto.remarks = object.getString("Remarks");
                            dto.vacationNo = object.getString("VacationNo");
                            list.add(dto);
                        }
                    } catch (JSONException e) {
                        iOnSearchApplyListListener.onSearchFailure(99999, "数据异常");
                        e.printStackTrace();
                    }
                }

                iOnSearchApplyListListener.onSearchSuccess(list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnSearchApplyListListener.onSearchFailure(99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnSearchApplyListListener.onSearchFailure(99999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnSearchApplyListListener.onSearchFailure(99999, "连接服务器超时");
            }

        });
    }

    public void cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true);
    }

    public interface IOnSearchApplyListListener {
        void onSearchSuccess(List<ApplyListInfo> list);
        void onSearchFailure(int code, String msg);
    }

}
