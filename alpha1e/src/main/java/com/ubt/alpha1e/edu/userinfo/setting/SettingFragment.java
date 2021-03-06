package com.ubt.alpha1e.edu.userinfo.setting;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.mvp.MVPBaseFragment;
import com.ubt.alpha1e.edu.login.LoginActivity;
import com.ubt.alpha1e.edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.userinfo.aboutus.AboutUsActivity;
import com.ubt.alpha1e.edu.userinfo.cleancache.CleanCacheActivity;
import com.ubt.alpha1e.edu.userinfo.contactus.ContactUsActivity;
import com.ubt.alpha1e.edu.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.edu.userinfo.myrobot.MyRobotActivity;
import com.ubt.alpha1e.edu.userinfo.psdmanage.PsdManageActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubt.alpha1e.edu.webcontent.WebContentActivity;
import com.zhy.changeskin.SkinManager;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SettingFragment extends MVPBaseFragment<SettingContract.View, SettingPresenter> implements SettingContract.View {

    private static final String TAG = SettingFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REFRESH_LANGUAGE = 100;
    private static final int REFRESH_LANGUAGE_FINISH = 101;
    private static final int SHOW_NOTICE_STATUS = 102;

    @BindView(R.id.rl_clear_cache)
    RelativeLayout rlClearCache;
    @BindView(R.id.rl_language)
    RelativeLayout rlLanguage;
    @BindView(R.id.btn_wifi_download)
    ImageButton btnWifiDownload;
    @BindView(R.id.btn_message_note)
    ImageButton btnMessageNote;
    @BindView(R.id.btn_auto_upgrade)
    ImageButton btnAutoUpgrade;
    @BindView(R.id.rl_about)
    RelativeLayout rlAbout;
    @BindView(R.id.rl_contact_us)
    RelativeLayout rlContactUs;
    @BindView(R.id.rl_message_myrobot)
    RelativeLayout rlMessageMyrobot;


    Unbinder unbinder;
    @BindView(R.id.tv_current_language)
    TextView tvCurrentLanguage;
    @BindView(R.id.rl_logout)
    RelativeLayout rlLogout;

    private String mParam1;
    private String mParam2;

    protected LoadingDialog mCoonLoadingDia;

    private List<String> mLanguageUpList = null;
    private List<String> mLanguageTitleList = null;
    private int mCurrentLanguageIndex = -1;
    private int mNoticeStatus = 1;

    long lastClickTime = System.currentTimeMillis();

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_LANGUAGE: {
                    mCoonLoadingDia.cancel();
                    mCoonLoadingDia.setDoCancelable(false);
                    mCoonLoadingDia.show();

                    mPresenter.doChangeLanguage(getContext(), mLanguageUpList.get(mCurrentLanguageIndex));
                }
                break;
                case REFRESH_LANGUAGE_FINISH: {
                    mCoonLoadingDia.cancel();
                    SkinManager.getInstance().setLanguage(mLanguageUpList.get(mCurrentLanguageIndex));
                    SkinManager.getInstance().changeSkin(FileTools.theme_pkg_file, FileTools.package_name, null);
                }
                break;
                case SHOW_NOTICE_STATUS:
                    if (mNoticeStatus == 1) {
                        btnMessageNote.setBackgroundResource(R.drawable.menu_setting_select);
                    } else {
                        btnMessageNote.setBackgroundResource(R.drawable.menu_setting_unselect);
                    }
                    break;
                default:
                    break;
            }


        }
    };

    public SettingFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "--initUI--");
        String[] languageUps = ((MVPBaseActivity) getActivity()).getStringArray("ui_lanuages_up");
        String[] languageTitles = ((MVPBaseActivity) getActivity()).getStringArray("ui_lanuages_title");
        mLanguageUpList = Arrays.asList(languageUps);
        mLanguageTitleList = Arrays.asList(languageTitles);

        mCoonLoadingDia = LoadingDialog.getInstance(getContext());

        if (mPresenter.isAutoUpgrade()) {
            btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
        }

        if (mPresenter.isOnlyWifiDownload(getContext())) {
            btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_select);
        } else {
            btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_unselect);
        }



        mPresenter.doGetMessageNote();
        mPresenter.doReadCurrentLanguage();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.rl_clear_cache, R.id.rl_password_massage, R.id.btn_wifi_download, R.id.btn_message_note,R.id.btn_auto_upgrade, R.id.rl_language, R.id.rl_help_feedback, R.id.rl_about, R.id.rl_contact_us, R.id.rl_logout ,R.id.rl_message_myrobot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_clear_cache:
                CleanCacheActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_password_massage:
                PsdManageActivity.LaunchActivity(getActivity(),false);
                break;
            case R.id.btn_wifi_download:
                if (mPresenter.isOnlyWifiDownload(getContext())) {
                    mPresenter.doSetOnlyWifiDownload(getContext(), false);
                    btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_unselect);
                } else {
                    mPresenter.doSetOnlyWifiDownload(getContext(), true);
                    btnWifiDownload.setBackgroundResource(R.drawable.menu_setting_select);
                }
                break;
            case R.id.btn_message_note:
                if (mNoticeStatus == 1) {
                    new ConfirmDialog(getContext()).builder()
                            .setMsg(getStringRes("ui_setting_close_notice_tip"))
                            .setCancelable(true)
                            .setPositiveButton(getStringRes("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //btnMessageNote.setBackgroundResource(R.drawable.menu_setting_unselect);
                                    com.ubt.alpha1e.edu.base.loading.LoadingDialog.show(getActivity());
                                    mPresenter.doSetMessageNote(false);

                                }
                            }).setNegativeButton(getStringRes("ui_common_cancel"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();

                } else {
                    //btnMessageNote.setBackgroundResource(R.drawable.menu_setting_select);
                    com.ubt.alpha1e.edu.base.loading.LoadingDialog.show(getActivity());
                    mPresenter.doSetMessageNote(true);
                }
                break;
            case R.id.btn_auto_upgrade:
                if (mPresenter.isAutoUpgrade()) {
                    btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
                    mPresenter.doSetAutoUpgrade(false);
                } else {
                    btnAutoUpgrade.setBackgroundResource(R.drawable.menu_setting_select);
                    mPresenter.doSetAutoUpgrade(true);
                }
                break;

            case R.id.rl_language:
                mPresenter.showLanguageDialog(getContext(), mCurrentLanguageIndex, mLanguageTitleList);
                break;
            case R.id.rl_help_feedback:

                WebContentActivity.launchActivity(getActivity(),HttpEntity.HELP_FEEDBACK, "");
                //HelpFeedbackActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_about:
                AboutUsActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_contact_us:
                ContactUsActivity.LaunchActivity(getContext());
                break;
            case R.id.rl_logout:

                new ConfirmDialog(getContext()).builder()
                        .setTitle(((MVPBaseActivity)getActivity()).getStringResources("ui_setting_logout"))
                        .setMsg(((MVPBaseActivity)getActivity()).getStringResources("ui_setting_logout_tip"))
                        .setCancelable(true)
                        .setPositiveButton(((MVPBaseActivity)getActivity()).getStringResources("ui_common_confirm"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mPresenter.doLogout();
                                UbtLog.d(TAG, "--logout--success");
                                LoginActivity.LaunchActivity(getContext());
                                getActivity().finish();
                            }
                        }).setNegativeButton(((MVPBaseActivity)getActivity()).getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            case R.id.rl_message_myrobot:
                UbtLog.d(TAG, "--rl_message_myrobot");
                if(System.currentTimeMillis()-lastClickTime < 1000){
                    UbtLog.d(TAG,"1000ms后才能点击");
                    return;
                }
                lastClickTime = System.currentTimeMillis();
                com.ubt.alpha1e.edu.base.loading.LoadingDialog.show(getActivity());
                mPresenter.checkMyRobotState();
                break;
        }
    }

    @Override
    public void onLanguageSelectItem(int index, String language) {
        UbtLog.d(TAG, "index = " + index + " mCurrentLanguageIndex = " + mCurrentLanguageIndex + "    language = " + language);
        if (mCurrentLanguageIndex != index) {
            mCurrentLanguageIndex = index;
            tvCurrentLanguage.setText(language);

            mHandler.sendEmptyMessage(REFRESH_LANGUAGE);
        }
    }

    @Override
    public void onReadCurrentLanguage(int index, String language) {
        UbtLog.d(TAG, "index == " + index + "    language == " + language);
        mCurrentLanguageIndex = index;
        tvCurrentLanguage.setText(language);

    }

    @Override
    public void onChangeLanguage() {

        mHandler.sendEmptyMessage(REFRESH_LANGUAGE_FINISH);
    }

    @Override
    public void onGetRobotInfo(int result, MyRobotModel model) {
        UbtLog.d(TAG, "onGetRobotInfo == " );
        com.ubt.alpha1e.edu.base.loading.LoadingDialog.dismiss(getActivity());
        if(result == 0){
            ToastUtils.showShort("获取机器人信息失败！");
        }else if(result == 1){
            UbtLog.d(TAG, "账号已经绑定 " );
            Intent intent = new Intent(getActivity(),MyRobotActivity.class);
            intent.putExtra("isBinded",1);
            intent.putExtra("serverVersion",model.getServerVersion());
            intent.putExtra("equipmentSeq",model.getEquipmentSeq());
            intent.putExtra("autoupdate",model.getAutoUpdate());
            startActivity(intent);
        }else if(result == 2){
            UbtLog.d(TAG, "账户没有绑定 " );
            Intent intent = new Intent(getActivity(),MyRobotActivity.class);
            intent.putExtra("isBinded",2);
            startActivity(intent);
        }
    }

    @Override
    public void onGetMessageNote(boolean isSuccess, String msg) {
        UbtLog.d(TAG,"onGetMessageNote isSuccess = " + isSuccess + " msg = " + msg);
        if(isSuccess){
            if("0".equals(msg)){
                mNoticeStatus = 0;
            }
        }
        mHandler.sendEmptyMessage(SHOW_NOTICE_STATUS);
    }

    @Override
    public void onSetMessageNote(boolean isSuccess, String msg) {
        com.ubt.alpha1e.edu.base.loading.LoadingDialog.dismiss(getActivity());
        UbtLog.d(TAG,"onSetMessageNote isSuccess = " + isSuccess);
        if(isSuccess){
            if(mNoticeStatus == 1){
                mNoticeStatus = 0;
            }else {
                mNoticeStatus = 1;
            }
            mHandler.sendEmptyMessage(SHOW_NOTICE_STATUS);
        }else {
            ToastUtils.showShort(getStringRes("ui_common_operate_fail"));
        }
    }

}
