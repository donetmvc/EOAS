package com.eland.android.eoas.Adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eland.android.eoas.Model.ApproveListInfo;
import com.eland.android.eoas.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 16/1/7.
 */
public class ApproveListAdapt extends BaseAdapter {

    private Context context;
    private List<ApproveListInfo> list;
    private LayoutInflater layoutInflater;

    public ApproveListAdapt(Context context, List<ApproveListInfo> list) {
        this.context = context;
        this.list = list;
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
            view = layoutInflater.inflate(R.layout.approvelist_item_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ApproveListInfo dto = list.get(i);

        viewHolder.txtApplicant.setText(dto.applyUserName);
        viewHolder.txtRemark.setText(dto.reason);
        viewHolder.txtVacationdays.setText(dto.vacationDays + "天");

        if(dto.vacationType.isEmpty() || dto.vacationType.equals("null") || dto.vacationType == null) {
            viewHolder.txtVacationName.setText(dto.vacationTypeName);
        }
        else {
            viewHolder.txtVacationName.setText(dto.vacationType);
        }

        viewHolder.txtVacationperiod.setText(dto.applyPeriod);
        viewHolder.txtApplyId.setText(dto.applyId);

        return view;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'approvelist_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.txt_applicant)
        TextView txtApplicant;
        @Bind(R.id.txt_vacationName)
        TextView txtVacationName;
        @Bind(R.id.txt_vacationdays)
        TextView txtVacationdays;
        @Bind(R.id.txt_vacationperiod)
        TextView txtVacationperiod;
        @Bind(R.id.txt_remark)
        TextView txtRemark;
        @Bind(R.id.txt_applyId)
        TextView txtApplyId;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
