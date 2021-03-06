package com.eland.android.eoas.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.eland.android.eoas.Application.EOASApplication;
import com.eland.android.eoas.Fragment.DrawerFragment;
import com.eland.android.eoas.Fragment.MainFragment;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Util.HttpRequstUtil;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by liu.wenbin on 15/11/10.
 */
public class MainActivity extends AppCompatActivity implements ProgressUtil.IOnMainUpdateListener{

    private String TAG = "EOAS";
    private Boolean isExit = false;
    private static final int PROFILE_SETTING = 1;

    private static final int MAIN_FRAGMENT = 10;

    private FragmentManager fragmentManager;
    private MainFragment mainFragment;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean drawerStatus;

    private UpdateManagerListener updateManagerListener;
    private String downUri;
    private ProgressUtil dialogUtil;
    private MaterialDialog updateMainDialog;
    private Context context;
    private String theme = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_THEME);
        if(!theme.isEmpty()) {
            if(theme.equals("RED")) {
                setTheme(R.style.MainThenmeRed);
            }
            else {
                setTheme(R.style.MainThenmeBlue);
            }
        }
        else {
            setTheme(R.style.MainThenmeRed);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        EOASApplication.getInstance().addActivity(this);

        context = this;

        initDrawer();
        initActivity();
        initUpdate();
        setMainPage(MAIN_FRAGMENT);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.content_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.left_drawer,
                        new DrawerFragment(MainActivity.this)).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerStatus = false ;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerStatus = true ;
            }
        };

        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        setmDrawerLayout(mDrawerLayout);
    }

    private void initActivity() {
        dialogUtil = new ProgressUtil();
        dialogUtil.setOnMainUpdateListener(this);
    }

    private void initUpdate() {

        if(null == updateManagerListener) {
            updateManagerListener = new UpdateManagerListener() {
                @Override
                public void onNoUpdateAvailable() {

                }

                @Override
                public void onUpdateAvailable(String result) {
                    AppBean appBean = getAppBeanFromString(result);
                    String message = appBean.getReleaseNote();
                    downUri = appBean.getDownloadURL();

                    updateMainDialog = dialogUtil.showDialogUpdateForMain(message, getResources().getString(R.string.hasupdate), context);
                    updateMainDialog.show();
                }
            };

            //检测更新
            PgyUpdateManager.register(this, updateManagerListener);
        }
    }

    /*
    *页面跳转
    * */
    public void setMainPage(int menuType) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);

        Fragment ft = fragmentManager.findFragmentByTag("MainFragment");
        Bundle args = new Bundle();
        mainFragment = ft == null ? MainFragment.newInstance(args) : (MainFragment)ft;

        switch (menuType) {
            case MAIN_FRAGMENT:
                if (null != mainFragment && mainFragment.isAdded()) {
                    //transaction.show(mainFragment);
                    hideFragments(transaction);
                } else {
                    transaction.add(R.id.main_content, mainFragment, "MainFragment");
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null && mainFragment.isAdded()) {
            String isChanging = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_ISCHANGINGTHEME);
            if(isChanging.equals("TURE")) {
                int i = fragmentManager.getBackStackEntryCount();
                if(i > 0) {
                    transaction.hide(mainFragment);
                }
                else {
                    SharedReferenceHelper.getInstance(this).setValue(Constant.EOAS_ISCHANGINGTHEME, "FALSE");
                    transaction.show(mainFragment);
                }
            }
            else {
                SharedReferenceHelper.getInstance(this).setValue(Constant.EOAS_ISCHANGINGTHEME, "FALSE");
                transaction.show(mainFragment);
            }
        }
    }

    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //if(mDrawerLayout.openDrawer(Gravity.NO_GRAVITY))
            List<Fragment> fragmentList = fragmentManager.getFragments();

            int i = fragmentManager.getBackStackEntryCount();

            if(i > 0) {
                fragmentManager.popBackStack();
            }
            else {
                existAppByDoubleClick();
            }
        }

        return false;
    }

    private void existAppByDoubleClick() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtil.showToast(this, "再次点击将退出应用程序", Toast.LENGTH_LONG);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            EOASApplication.getInstance().existApp();
            finish();
        }
    }

    @Override
    public void onUpdateSuccess() {
        updateMainDialog.dismiss();
        updateManagerListener.startDownloadTask(MainActivity.this, downUri);
    }
}
