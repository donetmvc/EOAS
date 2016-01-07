package com.eland.android.eoas.Adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eland.android.eoas.Model.ProxyInfo;
import com.eland.android.eoas.Model.ProxyUserInfo;
import com.eland.android.eoas.R;
import com.rey.material.widget.Spinner;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 16/1/6.
 */
public class ProxyAdapt extends BaseAdapter {

    private Context context;
    private List<ProxyInfo> list;
    private LayoutInflater layoutInflater;
    private String startDate,endDate;

    public ProxyAdapt(Context context, List<ProxyInfo> list, String startDate, String endDate) {
        this.context = context;
        this.list = list;
        this.startDate = startDate;
        this.endDate = endDate;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return list.size() == 0 ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == view) {
            view = layoutInflater.inflate(R.layout.setproxy_item_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ProxyInfo proxyInfo = list.get(i);

        viewHolder.txtOrgName.setText(proxyInfo.OrgName);
        viewHolder.txtOrgCode.setText(proxyInfo.OrgCode);
        viewHolder.txtStart.setText(startDate);
        viewHolder.txtEnd.setText(endDate);

        String[] proxyUser = new String[proxyInfo.proxyInfoList.size()];
        ProxyUserInfo dto;
        for (int j = 0; j < proxyUser.length; j++) {
            dto = proxyInfo.proxyInfoList.get(j);
            proxyUser[j] = dto.UserName;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.vacation_spinner_layout, proxyUser);
        adapter.setDropDownViewResource(R.layout.vacation_spinner_dropdown);
        viewHolder.spinnerProxy.setAdapter(adapter);

        return view;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'setproxy_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.txt_orgName)
        TextView txtOrgName;
        @Bind(R.id.txt_orgCode)
        TextView txtOrgCode;
        @Bind(R.id.spinner_proxy)
        Spinner spinnerProxy;
        @Bind(R.id.txt_start)
        TextView txtStart;
        @Bind(R.id.txt_end)
        TextView txtEnd;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
