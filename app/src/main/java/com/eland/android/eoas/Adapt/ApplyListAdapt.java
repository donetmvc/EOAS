package com.eland.android.eoas.Adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eland.android.eoas.Model.ApplyListInfo;
import com.eland.android.eoas.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elandmac on 15/12/28.
 */
public class ApplyListAdapt extends BaseAdapter {

    private Context context;
    private List<ApplyListInfo> list;
    private LayoutInflater layoutInflater;

    public ApplyListAdapt(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public ApplyListAdapt(Context context, List<ApplyListInfo> list) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<ApplyListInfo> list) {
        this.list = list;
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
            view = layoutInflater.inflate(R.layout.applylist_item_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ApplyListInfo dto = list.get(i);

        if(dto.processStateCode.equals("01")) {
            //审批开始
            viewHolder.imgVacationstate.setImageResource(R.mipmap.apply_wait_icon);
        }
        else if(dto.processStateCode.equals("02")) {
            //审批进行中
            viewHolder.imgVacationstate.setImageResource(R.mipmap.apply_wait_icon);
        }
        else {
            //审批结束
            viewHolder.imgVacationstate.setImageResource(R.mipmap.apply_success_icon);
        }

        viewHolder.txtVacationtype.setText(dto.vacationTypeName);
        viewHolder.txtVacationdays.setText(dto.vacationDays + "天");
        viewHolder.txtVacationtperiod.setText(dto.vacationPeriod);
        viewHolder.txtVacationtremark.setText(dto.remarks);
        viewHolder.txtVacationno.setText(dto.vacationNo);

        return view;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'applylist_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.img_vacationstate)
        ImageView imgVacationstate;
        @Bind(R.id.txt_vacationtype)
        TextView txtVacationtype;
        @Bind(R.id.txt_vacationdays)
        TextView txtVacationdays;
        @Bind(R.id.txt_vacationtperiod)
        TextView txtVacationtperiod;
        @Bind(R.id.txt_vacationtremark)
        TextView txtVacationtremark;
        @Bind(R.id.txt_vacationno)
        TextView txtVacationno;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
