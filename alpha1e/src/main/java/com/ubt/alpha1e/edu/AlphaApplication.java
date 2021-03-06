package com.ubt.alpha1e.edu;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.ai.tvs.LoginApplication;
import com.ubt.alpha1e.edu.AlphaApplicationValues.Thrid_login_type;
import com.ubt.alpha1e.edu.base.AppManager;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.MyRefreshHead;
import com.ubt.alpha1e.edu.base.ResponseMode.MyRefreshFoot;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.blockly.BlocklyActivity;
import com.ubt.alpha1e.edu.blockly.BlocklyCourseActivity;
import com.ubt.alpha1e.edu.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.edu.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.business.ActionsDownLoadManager;
import com.ubt.alpha1e.edu.business.thrid_party.MyWeiBoNew;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.data.model.NetworkInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.edu.services.AutoScanConnectService;
import com.ubt.alpha1e.edu.services.GlobalMsgService;
import com.ubt.alpha1e.edu.services.MyLifecycleHandler;
import com.ubt.alpha1e.edu.ui.AboutUsActivity;
import com.ubt.alpha1e.edu.ui.ActionUnpublishedActivity;
import com.ubt.alpha1e.edu.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.edu.ui.ActionsPublishActivity;
import com.ubt.alpha1e.edu.ui.ActionsSquareDetailActivity;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.FeedBackActivity;
import com.ubt.alpha1e.edu.ui.FindPassWdActivity;
import com.ubt.alpha1e.edu.ui.LanguageActivity;
import com.ubt.alpha1e.edu.ui.LoginActivity;
import com.ubt.alpha1e.edu.ui.MediaRecordActivity;
import com.ubt.alpha1e.edu.ui.MessageActivity;
import com.ubt.alpha1e.edu.ui.MyActionsActivity;
import com.ubt.alpha1e.edu.ui.MyMainActivity;
import com.ubt.alpha1e.edu.ui.PcUpdateActivity;
import com.ubt.alpha1e.edu.ui.PrivateInfoActivity;
import com.ubt.alpha1e.edu.ui.PrivateInfoEditActivity;
import com.ubt.alpha1e.edu.ui.RegisterActivity;
import com.ubt.alpha1e.edu.ui.RegisterNextStepActivity;
import com.ubt.alpha1e.edu.ui.RobotControlActivity;
import com.ubt.alpha1e.edu.ui.SettingActivity;
import com.ubt.alpha1e.edu.ui.StartInitSkinActivity;
import com.ubt.alpha1e.edu.ui.WebContentActivity;
import com.ubt.alpha1e.edu.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.edu.ui.helper.BaseHelper;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.ui.main.MainActivity;
import com.ubt.alpha1e.edu.update.EngineUpdateManager;
import com.ubt.alpha1e.edu.userinfo.dynamicaction.DownLoadActionManager;
import com.ubt.alpha1e.edu.utils.DynamicTimeFormat;
import com.ubt.alpha1e.edu.utils.connect.ConnectClientUtil;
import com.ubt.alpha1e.edu.utils.crash.CrashHandler;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubt.alpha1e.edu.xingepush.XGListener;
import com.ubt.xingemodule.XGUBTManager;
import com.ubtechinc.base.BlueToothManager;
import com.ubtechinc.sqlite.DBAlphaInfoManager;
import com.umeng.analytics.MobclickAgent;
import com.zhy.changeskin.SkinManager;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AlphaApplication extends LoginApplication {

    private static final String TAG = "AlphaApplication";

    private String mCurrentDeviceName;
    private UserInfo mCurrentUser;
    private Object mCurrentThridLoginInfo;
    private Thrid_login_type mCurrentTreadLoginType = null;
    private BlueToothManager mBlueManager;
    private BluetoothDevice mCurrentBluetooth;
    public DBAlphaInfoManager mDBAlphaInfoManager;
    private List<Activity> mActivityList;
    private String mCurrentRobotSoftVersion = null;
    private String mCurrentRobotHardVersion = null;
    private static Date lastTime_tryRestart = null;

    private static BaseActivity baseActivity;
    private static Activity mCurrentActivity;
    private static MyActionsHelper.Action_type action_type;

    public static Context mContext = null;

    //Activity前台个数，用来判断app是否进入后台
    public static int mStateActivityCount = 0;


    //默认当前连接对象为非1E，没有连上的时候，默认为非Alpha1E
    private boolean isAlpha1E = false;

    private static boolean isPad = false;

    public long getActionOriginalId() {
        return actionOriginalId;
    }

    public void setActionOriginalId(long actionOriginalId) {
        this.actionOriginalId = actionOriginalId;
    }

    public long actionOriginalId = 0;

    public NetworkInfo getmCurrentNetworkInfo() {
        return mCurrentNetworkInfo;
    }

    public void setmCurrentNetworkInfo(NetworkInfo mCurrentNetworkInfo) {
        this.mCurrentNetworkInfo = mCurrentNetworkInfo;
    }

    private NetworkInfo mCurrentNetworkInfo = null;

    public static String getmNeedOpenActivity() {
        return mNeedOpenActivity;
    }

    public static void setmNeedOpenActivity(String activity) {
        mNeedOpenActivity = activity;
    }

    private static String mNeedOpenActivity = null;

    public static String currentRobotSN = "";

    private static XGListener xgListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        xgListener = new XGListener();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        initActivityLife();
        initSkin(this);
        initConnectClient();
        startGlobalMsgService(); //处理全局消息，包括信鸽，必须在信鸽前初始化
        initXG();
        initWb();
        initLanguage();
        initIsPad(this);
        LitePal.initialize(this);
        initSmartRefresh();
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
//        LeakCanary.install(this);
        //   VCamera.setVideoCachePath(FileTools.media_cache);
        //  VCamera.setDebugMode(true);
        //  VCamera.initialize(this);
//        IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if(getCurrentBluetooth()!=null)
//                {
//                    setCurrentBluetooth(null);
//                    doRestartApp();
//                }
//            }
//        }, screenOffFilter);

    }

    private void startGlobalMsgService() {
        Intent mIntent = new Intent(this, GlobalMsgService.class);
        startService(mIntent);
    }

    private void initSmartRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.black);//全局设置主题颜色
                return new MyRefreshHead(context).setTimeFormat(new DynamicTimeFormat("更新于 %s")).setEnableLastTime(false)
                        .setDrawableMarginRight(5);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new MyRefreshFoot(context).setDrawableSize(20).setDrawableMarginRight(5);
            }
        });
    }


    public static Context getmContext() {
        return mContext;
    }


    private void initWb(){
        WbSdk.install(this,new AuthInfo(this, MyWeiBoNew.APP_KEY, MyWeiBoNew.REDIRECT_URL, MyWeiBoNew.SCOPE));
    }

    /**
     * 初始化主题
     */
    public void initSkin(Context ctx) {
        //改放到在baseActivity 初始化
        SkinManager.getInstance().init(ctx);
    }

    public static void initXG() {
        String accessId = SPUtils.getInstance().getString(Constant.SP_XG_ACCESSID);
        String accessKey = SPUtils.getInstance().getString(Constant.SP_XG_ACCESSKEY);
        if (!TextUtils.isEmpty(accessId) && !TextUtils.isEmpty(accessKey)) {
            XGUBTManager.getInstance().initXG(mContext, Long.parseLong(accessId), accessKey);
            XGUBTManager.getInstance().setXGListener(xgListener);
            //  XGUBTManager.getInstance(this).initXG(2100270011, "A783M4PIM7JI");
        }
    }

    /**
     * 初始化网络连接客户端
     */
    public void initConnectClient() {
        ConnectClientUtil.getInstance().init();
    }

    public void initLanguage() {
        String currentLanguage = BasicSharedPreferencesOperator.getInstance(this,
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY);

        if (currentLanguage.equals(BasicSharedPreferencesOperator.NO_VALUE)) {
            BasicSharedPreferencesOperator.getInstance(this,
                    BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(
                    BasicSharedPreferencesOperator.LANGUAGE_SET_KEY, "zh_CN",
                    null, -1);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

    }

    public void setCurrentThridUserInfo(Thrid_login_type _currentTreadType,
                                        Object _currentThridUserInfo) {
        mCurrentTreadLoginType = _currentTreadType;
        mCurrentThridLoginInfo = _currentThridUserInfo;
    }

    public Thrid_login_type getCurrentThridLoginType() {
        return mCurrentTreadLoginType;
    }

    public Object getCurrentThridLoginInfo() {
        return mCurrentThridLoginInfo;
    }

    public void setCurrentUserInfo(UserInfo info) {

        if (info == null) {
            MobclickAgent.onProfileSignOff();
        } else {
            if (mCurrentTreadLoginType == null) {
                MobclickAgent.onProfileSignIn(info.userId + "");
            } else {
                MobclickAgent.onProfileSignIn(mCurrentTreadLoginType.name(),
                        info.userId + "");
            }
        }

        this.mCurrentUser = info;
    }

    // 登录流程未完则清除用户信息内外缓存
    public void clearCurrentUserInfo() {
        UbtLog.d("APP", "clearCurrentUserInfo");
        MobclickAgent.onProfileSignOff();
        mCurrentUser = null;
        BasicSharedPreferencesOperator.getInstance(this,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.LOGIN_USER_INFO, "NO_VALUE",
                new ISharedPreferensListenet() {
                    @Override
                    public void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code, String value) {

                    }
                }, 10001);
    }

    public UserInfo getCurrentUserInfo() {
        return this.mCurrentUser;
    }

    public void setBlueToothManager(BlueToothManager manager) {
        this.mBlueManager = manager;
    }

    public BlueToothManager getBlueToothManager() {
        return this.mBlueManager;
    }

    public void setCurrentBluetooth(BluetoothDevice bluetooth) {
        this.mCurrentBluetooth = bluetooth;
    }

    public BluetoothDevice getCurrentBluetooth() {
        return this.mCurrentBluetooth;
    }

    public String getCurrentBluetoothAddress() {
        if (this.mCurrentBluetooth != null) {
            return this.mCurrentBluetooth.getAddress();
        }
        return "";
    }

    public DBAlphaInfoManager getDBAlphaInfoManager() {
        return mDBAlphaInfoManager;
    }

    public void setDBAlphaInfoManager(DBAlphaInfoManager manager) {
        this.mDBAlphaInfoManager = manager;
    }

    public void setCurrentDeviceName(String name) {
        this.mCurrentDeviceName = name;
    }

    public String getCurrentDeviceName() {
        return this.mCurrentDeviceName;
    }

    public void addToActivityList(Activity act) {
        if (mActivityList == null)
            mActivityList = new ArrayList<Activity>();
        if (!mActivityList.contains(act))
            mActivityList.add(act);
    }

    public void removeActivityList(Activity act) {
        if (mActivityList == null)
            mActivityList = new ArrayList<Activity>();
        if (mActivityList.contains(act))
            mActivityList.remove(act);
    }

    public List<Activity> getHistoryActivityList() {
        return mActivityList;
    }

    public void doLostConnect() {

        setCurrentBluetooth(null);
        UbtLog.d(TAG, "doLostConnect ..... ");
        ActionPlayer.StopCycleThread(true);
        // 蓝牙断线
        if (mBlueManager != null) {
            mBlueManager.releaseAllConnected();
        }

        cleanBluetoothConnectData();

        MyActionsHelper.mCacheActionsNames.clear();
    }

    public void doLostConn(Activity mCurrentAct) {
        CommonCtrlView.closeCommonCtrlView();
        MyActionsHelper.doStopMp3ForMyDownload();
        if (mCurrentAct != null) {
            MyActionsHelper.getInstance((BaseActivity) mCurrentAct).resetPlayer();
        }
        ActionPlayer.StopCycleThread(true);
        ActionsDownLoadManager.resetData();
        DownLoadActionManager.getInstance(this).resetData();
        // 蓝牙断线
        if (mBlueManager != null) {
            mBlueManager.releaseAllConnected();
        }

        cleanBluetoothConnectData();

        Activity mActivity = null;
        if (mActivityList == null) {
            return;
        }
        for (int i = 0; i < mActivityList.size(); i++) {
            try {
                mActivity = mActivityList.get(i);
                if (mActivity instanceof MyMainActivity
                        || mActivity instanceof ActionsSquareDetailActivity
                        || mActivity instanceof ActionsLibPreviewWebActivity
                        || mActivity instanceof WebContentActivity
                        || mActivity instanceof MessageActivity
                        || mActivity instanceof SettingActivity
                        || mActivity instanceof PrivateInfoActivity
                        || mActivity instanceof PrivateInfoEditActivity
                        || mActivity instanceof LanguageActivity
                        || mActivity instanceof AboutUsActivity
                        || mActivity instanceof FeedBackActivity
                        || mActivity instanceof LoginActivity
                        || mActivity instanceof RegisterActivity
                        || mActivity instanceof FindPassWdActivity
                        || mActivity instanceof RegisterNextStepActivity
                        /*|| mActivity instanceof CountryActivity*/
                        || mActivity instanceof ActionUnpublishedActivity
                        || mActivity instanceof ActionsPublishActivity
                        || mActivity instanceof MediaRecordActivity
                        || mActivity instanceof MyActionsActivity
                        || mActivity instanceof RobotControlActivity
                        || mActivity instanceof BlocklyActivity
                        || mActivity instanceof BlocklyCourseActivity
                        || mActivity instanceof NetconnectActivity //add by dicy.cheng  当在网络连接页面时，如果蓝牙掉线，则该网络连接页面也关掉
                        || mActivity instanceof MainActivity
                        || mActivity instanceof BluetoothandnetconnectstateActivity
                         ||mActivity instanceof MainCourseActivity
                        ) {
                    if (mActivity instanceof MyActionsActivity) {
                        if (MyActionsActivity.requestPosition == 1
                                || MyActionsActivity.requestPosition == 2
                                || MyActionsActivity.requestPosition == 3) {
                            //my creation/my download/my collect need not close
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                UbtLog.d(TAG, "mActivity finish");
                mActivity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //蓝牙断开不需求清除在线缓存
        //clearCacheData();
        MyActionsHelper.mCacheActionsNames.clear();
    }

    public void doGotoPcUpdate(Activity mCurrentAct) {
        // 蓝牙断线
        if (mBlueManager != null) {
            mBlueManager.releaseAllConnected();
        }

        cleanBluetoothConnectData();

        clearCacheData();

        Intent intent = new Intent();
        intent.setClass(mCurrentAct, PcUpdateActivity.class);
        mCurrentAct.startActivity(intent);
        mCurrentAct.finish();
    }

    public void doExitApp(boolean isStopProcress) {

        BaseHelper.mCourseAccessToken = "";
        AutoScanConnectService.doStopSelf();
        MyActionsHelper.doStopMp3ForMyDownload();
        ActionPlayer.StopCycleThread(true);

        clearCacheData();

        // 蓝牙断线
        if (mBlueManager != null) {
            mBlueManager.releaseAllConnected();
        }

        cleanBluetoothConnectData();

//        for (int i = 0; i < mActivityList.size(); i++) {
//            try {
//                mActivityList.get(i).finish();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        AppManager.getInstance().finishAllActivity();

        if (isStopProcress) {
            MobclickAgent.onKillProcess(this);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 清空缓存数据
     */
    public void clearCacheData() {
        //清除在线缓存
        BaseHelper.hasGetScheme = false;
        MyActionsHelper.mCacheActionsNames.clear();
//        ActionsLibMainFragment3.clearCacheDatas();

        //app 改版指引相关
        BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_GUIDE_STEP, "0", null, -1);
    }

    /**
     * 情况蓝牙连接数据
     */
    public void cleanBluetoothConnectData() {
        setCurrentBluetooth(null);
        setRobotHardVersion(null);
        setRobotSoftVersion(null);
    }

    public void doRestartApp() {
        // 500毫秒内不尝试连续重启-----------start
        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 500;
        if (lastTime_tryRestart != null) {
            time_difference = curDate.getTime() - lastTime_tryRestart.getTime();
        }
        lastTime_tryRestart = curDate;
        if (time_difference < 500) {
            return;
        }

        // 500毫秒内不尝试连续重启-----------end
        if (mBlueManager != null)
            mBlueManager.releaseAllConnected();
        Intent i = this.getPackageManager().getLaunchIntentForPackage(
                this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(i);
    }

    public void doCrashErrorRestartApp() {
        // 500毫秒内不尝试连续重启-----------start
        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 500;
        if (lastTime_tryRestart != null) {
            time_difference = curDate.getTime() - lastTime_tryRestart.getTime();
        }
        lastTime_tryRestart = curDate;
        if (time_difference < 500) {
            return;
        }

        AutoScanConnectService.doStopSelf();
        MyActionsHelper.doStopMp3ForMyDownload();
        ActionPlayer.StopCycleThread(true);
        clearCacheData();

        // 蓝牙断线
        if (mBlueManager != null) {
            mBlueManager.releaseAllConnected();
        }
        cleanBluetoothConnectData();

        Activity mCurrentActivity = null;
        if (mActivityList != null) {
            for (int i = 0; i < mActivityList.size(); i++) {
                try {
                    if (i == (mActivityList.size() - 1)) {
                        mCurrentActivity = mActivityList.get(i);
                    } else {
                        mActivityList.get(i).finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (mCurrentActivity != null && !mCurrentActivity.isFinishing()) {
            Intent mIntent = new Intent();
            //mIntent.setClass(mCurrentActivity, StartActivity.class);
            mIntent.setClass(mCurrentActivity, StartInitSkinActivity.class);
            mCurrentActivity.startActivity(mIntent);
            mCurrentActivity.finish();
        }

    }

    public String getRobotHardVersion() {
        return mCurrentRobotHardVersion;
    }

    public String getRobotSoftVersion() {
        return mCurrentRobotSoftVersion;
    }

    public void setRobotHardVersion(String version) {
        mCurrentRobotHardVersion = version;

        //判断是否Alpha1E
        if (mCurrentRobotHardVersion != null
                && mCurrentRobotHardVersion.toLowerCase().contains(EngineUpdateManager.Alpha1e)) {
            isAlpha1E = true;
        } else {
            isAlpha1E = false;
        }
    }

    public void setRobotSoftVersion(String version) {
        mCurrentRobotSoftVersion = version;
    }

    public boolean isAlpha1E() {
        return isAlpha1E;
    }

    public void LoginOut() {
        // TODO Auto-generated method stub
        setCurrentUserInfo(null);
    }

    public void setCurrentActivity(Activity activity) {
        this.mCurrentActivity = activity;
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public static BaseActivity getBaseActivity() {
        return baseActivity;
    }

    public static void setActionType(MyActionsHelper.Action_type actionType) {
        action_type = actionType;
    }

    public static MyActionsHelper.Action_type getActionType() {
        return action_type;
    }

    private static boolean isShowCircleFragemt = false;

    public synchronized static boolean isCycleActionFragment() {
        return isShowCircleFragemt;
    }

    public synchronized static void setCycleFragmentShow(boolean isShow) {
        isShowCircleFragemt = isShow;
    }

    public static boolean isPad() {
        return isPad;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @return 平板返回False True，手机返回
     */
    private void initIsPad(Context context){
        try {
            isPad = (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }catch (Exception ex){}
    }

    /**
     * 判断app是否进入后台
     *
     * @return
     */
    public static boolean isBackground() {
        if (mStateActivityCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 初始化简单Activity的生命周期
     */
    private void initActivityLife() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mStateActivityCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mStateActivityCount--;
                UbtLog.d(TAG, "mStateActivityCount stop = " + mStateActivityCount);
                if (mStateActivityCount == 0) {
                    AutoScanConnectService.doEntryBackground();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
