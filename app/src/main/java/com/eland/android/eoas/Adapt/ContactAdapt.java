package com.eland.android.eoas.Adapt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Util.ImageLoaderOption;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elandmac on 15/12/15.
 */
public class ContactAdapt extends BaseAdapter {

    private String TAG = "EOAS";
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private LayoutInflater mInflater;
    private List<LoginInfo> lists;

    public ContactAdapt(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public ContactAdapt(Context context, List<LoginInfo> list, ImageLoader imageLoader) {
        this.context = context;
        this.lists = list;
        this.imageLoader = imageLoader;
        this.mInflater = LayoutInflater.from(context);
        this.options = ImageLoaderOption.getOptionsById(R.mipmap.eland_log);
    }

    public void setList(List<LoginInfo> list) {
        this.lists = list;
    }

    @Override
    public int getCount() {
        return lists.size() == 0 ? 0 : lists.size();
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

        final ViewHolder viewHolder;

        if (null == view) {
            view = mInflater.inflate(R.layout.contact_item_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final LoginInfo dto = lists.get(i);
        viewHolder.txtName.setText(dto.userName);
        viewHolder.txtEmail.setText(dto.email);
        viewHolder.txtTel.setText(dto.cellNo);
        imageLoader.displayImage(dto.url, viewHolder.imgPhoto, options);

        viewHolder.itemActionCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToastUtil.showToast(context, "打电话咯", Toast.LENGTH_LONG);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dto.cellNo));

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
            }
        });

        viewHolder.itemActionEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToastUtil.showToast(context, "发邮件咯", Toast.LENGTH_LONG);
                sendEmail(dto.email);
            }
        });

        viewHolder.itemActionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToastUtil.showToast(context, "发短信咯", Toast.LENGTH_LONG);
                Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto://" + dto.cellNo));
                context.startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String email){
        Uri uri = Uri.parse("mailto:" + email);
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos == null || infos.size() <= 0){
            ToastUtil.showToast(context, "请先安装邮件应用并设置账户", Toast.LENGTH_SHORT);
            return;
        }
        context.startActivity(intent);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'contact_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.img_photo)
        CircleImageView imgPhoto;
        @Bind(R.id.txt_name)
        TextView txtName;
        @Bind(R.id.txt_email)
        TextView txtEmail;
        @Bind(R.id.txt_tel)
        TextView txtTel;
        @Bind(R.id.item_txt_call)
        TextView itemTxtCall;
        @Bind(R.id.item_action_call)
        LinearLayout itemActionCall;
        @Bind(R.id.item_txt_text)
        TextView itemTxtText;
        @Bind(R.id.item_action_text)
        LinearLayout itemActionText;
        @Bind(R.id.item_txt_email)
        TextView itemTxtEmail;
        @Bind(R.id.item_action_email)
        LinearLayout itemActionEmail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
