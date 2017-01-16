package com.eland.android.eoas.Service;

import android.content.Context;

import com.eland.android.eoas.Model.WetherInfo;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by liu.wenbin on 2016/2/24.
 */
public class WetherService {

    private Context context;

    public WetherService(Context context) {
        this.context = context;
    }

    public void getWether(String lng, String lat, final IOnGetWetherListener iOnGetWetherListener) {

        String uri = "api/Wether";

        RequestParams params = new RequestParams();
        params.add("lng", lng);
        params.add("lat", lat);

        HttpRequstUtil.get(context, uri, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                WetherInfo dto = new WetherInfo();

                if(null != response) {
                    try {
                        dto.sd = response.getString("sd");
                        dto.hightTemp = response.getString("hightTemp");
                        dto.lowTemp = response.getString("lowTemp");
                        dto.temperature = response.getString("temperature");
//                        dto.temperature_time = response.getString("temperature_time");
                        dto.weather = response.getString("weather");
                        dto.wind_direction = response.getString("wind_direction");
                        dto.wind_power = response.getString("wind_power");
                        dto.weather_pic = response.getString("weather_pic");

                        iOnGetWetherListener.onGetSucess(dto);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        iOnGetWetherListener.onGetFailure(99999, "");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                iOnGetWetherListener.onGetFailure(99999, "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                iOnGetWetherListener.onGetFailure(99999, "");
            }
        });

    }

    public void cancelRequest() {
        if(null != context) {
            HttpRequstUtil.cancelSingleRequest(context, true);
        }
    }

    public interface IOnGetWetherListener {
        void onGetSucess(WetherInfo weather);
        void onGetFailure(int code, String message);
    }
}
