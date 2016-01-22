package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Model.ScheduleInfo;
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
 * Created by liu.wenbin on 15/12/25.
 */
public class SearchScheduleService {

    private Context context;

    public SearchScheduleService(Context context) {
        this.context = context;
    }

    public void searchSchedule(String userId, String startDate, String endDate, final IOnSearchScheduleListener iOnSearchScheduleListener) {
        String uri = "api/ADM";

        RequestParams params = new RequestParams();
        params.add("userId",userId);
        params.add("startDate",startDate);
        params.add("endDate",endDate);
        params.add("app","");

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                List<ScheduleInfo> list = new ArrayList<ScheduleInfo>();
                ScheduleInfo dto = null;

                if(null != response && response.length() > 0) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            dto = new ScheduleInfo();
                            dto.work = obj.getString("work").equals("null") ? "" : obj.getString("work");
                            dto.workdes = obj.getString("workdes").equals("null") ? "" : obj.getString("workdes");
                            dto.offwork = obj.getString("offwork").equals("null") ? "" : obj.getString("offwork");
                            dto.offworkdes = obj.getString("offworkdes").equals("null") ? "" : obj.getString("offworkdes");
                            dto.date = obj.getString("date");

                            list.add(dto);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                iOnSearchScheduleListener.onSuccess(list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnSearchScheduleListener.onFailure(9999, "连接服务器超时");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnSearchScheduleListener.onFailure(9999, "连接服务器超时");
            }
        });

    }

    public void cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true);
    }

    public interface IOnSearchScheduleListener {
        void onSuccess(List<ScheduleInfo> list);
        void onFailure(int code, String msg);
    }

}
