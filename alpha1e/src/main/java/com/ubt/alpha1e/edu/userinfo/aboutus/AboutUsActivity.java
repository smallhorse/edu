package com.ubt.alpha1e.edu.userinfo.aboutus;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.edu.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.edu.update.ApkUpdateManager;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AboutUsActivity extends MVPBaseActivity<AboutUsContract.View, AboutUsPresenter> implements AboutUsContract.View {

    private static final String TAG = AboutUsActivity.class.getSimpleName();

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.rl_app_update)
    RelativeLayout rlAppUpdate;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_update)
    ImageView ivUpdate;

    private String version = "";
    private String newVersionPath = "";
    private boolean isAutoRequest = true;
    private ConfirmDialog confirmDialog = null;

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {
        try {
            version = AboutUsActivity.this.getPackageManager().getPackageInfo(
                    AboutUsActivity.this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }

        UbtLog.d(TAG, "version = " + version);

        tvVersion.setText(getStringResources("ui_about_version") + version);
        tvBaseTitleName.setText(getStringResources("ui_leftmenu_about"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAutoRequest = true;
        mPresenter.doUpdateApk(version);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_about_us_mvp;
    }

    @OnClick({R.id.ll_base_back, R.id.rl_app_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                AboutUsActivity.this.finish();
                break;
            case R.id.rl_app_update: {
                if (ApkUpdateManager.isUpdating()) {
                    ToastUtils.showShort(getStringResources("ui_about_update_doing"));
                    return;
                }

                if (mPresenter.isOnlyWifiDownload(getContext())
                        && (!mPresenter.isWifiCoon(getContext()))) {
                    ToastUtils.showShort(getStringResources("ui_about_update_wifi_need"));
                } else {
                    if (TextUtils.isEmpty(newVersionPath)) {
                        isAutoRequest = false;
                        mPresenter.doUpdateApk(version);
                        if (mCoonLoadingDia != null) {
                            mCoonLoadingDia.cancel();
                        }
                        mCoonLoadingDia = SLoadingDialog.getInstance(AboutUsActivity.this);
                        mCoonLoadingDia.show();
                    } else {
                        showUpdateDialog();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void noteNewestVersion() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }
                //mCoonLoadingDia = SLoadingDialog.getInstance(getContext());
                //((SLoadingDialog) mCoonLoadingDia).show(getStringResources("ui_about_update_already_latest"));
                if (!isAutoRequest) {
                    ToastUtils.showShort(getStringResources("ui_about_update_already_latest"));
                }
            }
        });
    }

    @Override
    public void noteApkUpsateFail(final String info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }

                if (!isAutoRequest) {
                    ToastUtils.showShort(info);
                }
            }
        });
    }

    @Override
    public void noteApkUpdate(final String versionPath) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }
                newVersionPath = versionPath;
                if (isAutoRequest) {
                    ivUpdate.setVisibility(View.VISIBLE);
                } else {
                    showUpdateDialog();
                }
            }
        });
    }

    private void showUpdateDialog() {
        if (confirmDialog == null) {
            confirmDialog = new ConfirmDialog(getContext()).builder().setMsg(getStringResources("ui_setting_app_update_tips"))
                    .setCancelable(true)
                    .setPositiveButton(getStringResources("ui_common_yes"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ApkUpdateManager.getInstance(AboutUsActivity.this, newVersionPath).Update();
                        }
                    }).setNegativeButton(getStringResources("ui_common_no"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        }

        confirmDialog.show();
    }
}
