package com.eland.android.eoas.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.eland.android.eoas.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elandmac on 15/12/29.
 */
public class ApplyStateActivity extends AppCompatActivity {

    @Bind(R.id.applytoolbar)
    Toolbar applytoolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apply_state);
        ButterKnife.bind(this);

        initToolbar();
    }

    private void initToolbar() {
        applytoolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        applytoolbar.setTitle("批准流程");
        setSupportActionBar(applytoolbar);
        applytoolbar.setNavigationIcon(R.mipmap.icon_back);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
