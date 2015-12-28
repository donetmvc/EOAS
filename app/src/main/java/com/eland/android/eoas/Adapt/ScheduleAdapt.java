package com.eland.android.eoas.Adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eland.android.eoas.Model.ScheduleInfo;
import com.eland.android.eoas.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 15/12/25.
 */
public class ScheduleAdapt extends BaseAdapter {

    private Context context;
    private List<ScheduleInfo> list;
    private LayoutInflater layoutInflater;

    public ScheduleAdapt(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public ScheduleAdapt(Context context, List<ScheduleInfo> list) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<ScheduleInfo> list) {
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
            view = layoutInflater.inflate(R.layout.schedule_item_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ScheduleInfo scheduleInfo = list.get(i);

        if (null != scheduleInfo) {
            viewHolder.txtDate.setText(scheduleInfo.date);
            viewHolder.txtWork.setText(scheduleInfo.work);
            viewHolder.txtWorkdes.setText(scheduleInfo.workdes);

            if(scheduleInfo.workdes.equals("迟到") || scheduleInfo.workdes.equals("旷工")) {
                viewHolder.txtWorkdes.setBackgroundColor(context.getResources().getColor(R.color.md_red_500));
            }
            else {
                viewHolder.txtWorkdes.setBackgroundColor(context.getResources().getColor(R.color.green));
            }

            viewHolder.txtOffwork.setText(scheduleInfo.offwork);
            viewHolder.txtOffworkdes.setText(scheduleInfo.offworkdes);

            if(scheduleInfo.offworkdes.equals("早退") || scheduleInfo.offworkdes.equals("旷工")) {
                viewHolder.txtOffworkdes.setBackgroundColor(context.getResources().getColor(R.color.md_red_500));
            }
            else {
                viewHolder.txtOffworkdes.setBackgroundColor(context.getResources().getColor(R.color.green));
            }
        }

        return view;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'schedule_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.txt_date)
        TextView txtDate;
        @Bind(R.id.txt_work)
        TextView txtWork;
        @Bind(R.id.txt_offwork)
        TextView txtOffwork;
        @Bind(R.id.txt_workdes)
        TextView txtWorkdes;
        @Bind(R.id.txt_offworkdes)
        TextView txtOffworkdes;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
