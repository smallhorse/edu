package com.ubt.alpha1e.edu.ui.helper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.AppManager;
import com.ubt.alpha1e.edu.base.FileUtils;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.RequstMode.CheckIsBindRequest;
import com.ubt.alpha1e.edu.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.base.loading.LoadingDialog;
import com.ubt.alpha1e.edu.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.edu.blockly.BlocklyCourseActivity;
import com.ubt.alpha1e.edu.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.Md5;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.data.model.NetworkInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.event.LessonEvent;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.IImageListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.dialog.AlertDialog;
import com.ubt.alpha1e.edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.edu.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e.edu.ui.dialog.RobotBindingDialog;
import com.ubt.alpha1e.edu.ui.dialog.alertview.RobotBindDialog;
import com.ubt.alpha1e.edu.ui.main.MainUiBtHelper;
import com.ubt.alpha1e.edu.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public abstract class BaseHelper implements BlueToothInteracter, IImageListener {

    private static final String TAG = "BaseHelper";

    protected BaseActivity mBaseActivity;
    protected Context mContext = null;
    private static Timer read_battery_timer = null;
    private static Date lastTime_doReadState = null;
    private static boolean isNeedNoteLowPower = true;
    private static boolean isNeedNoteLowPowerTwenty = true;
    private static boolean isNeedNoteLowPowerFive = true;
    protected static long readUserHeadImgRequest = 1111001;
    public static boolean hasHandshakeBseven = false;
    public static boolean hasSupportA2DP = false;
    public static boolean hasGetScheme = false;

    public static boolean hasConnectNetwork = false;//Alpha 1E 是否联网

    public static boolean hasSdcard = true;
    public static boolean mCurrentVoiceState = true;
    public static int mCurrentVolume = 0;
    public static boolean mLightState = true;
    public static boolean mSensorState = true;

    public static boolean mSensorGreetingState = true;

    public static String mCourseAccessToken = "";
    private int LOW_BATTERY_TWENTY = 20;
    private int LOW_BATTERY_FIVE = 5;
    public final static int CHECK_ROBOT_INFO_HABIT=1;
    private static final int ROBOT_GOTO_BIND = 20;


    private static boolean isCharging = false; //用来判断机器人当前是否在充电中,false 表示没有充电中,true表示充电中.
    private static byte mPowerValue = 0;
    public static boolean isStartHibitsProcess = false;
   //
    public static boolean isLowBatteryNotExecuteAction=false;
    /*public BaseHelper(BaseActivity _baseActivity) {
        mBaseActivity = _baseActivity;
    }*/

    public void setBaseActivity(BaseActivity _baseActivity) {
        mBaseActivity = _baseActivity;
    }

    public BaseHelper(Context context) {
        mContext = context;
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
    }

    public boolean isLostCoon() {
        if (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth() == null) {
            return true;
        } else {
            return false;
        }
    }

    protected void doSendComm(String mac, byte cmd, byte[] param) {
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().sendCommand(mac, cmd, param,
                param == null ? 0 : param.length, false);
    }

    public void doSendComm(byte cmd, byte[] param) {
        // MyLog.writeLog("
        // �����", ByteHexHelper.byteToHexString(cmd) + "");
        doSendComm(cmd, param, false);
    }

    protected void doSendComm(byte cmd, byte[] param, boolean isMonopolyRight) {
        if (isLostCoon()) {
            UbtLog.e(TAG, "----onLostBtCoon---" + cmd + "    mContext = " + mContext);
            if (mContext != null && mContext instanceof BaseActivity) {
                ((BaseActivity) mContext).onLostBtCoon();
            } else if (mContext != null && mContext instanceof MVPBaseActivity) {
//                ((MVPBaseActivity) mContext).onLostBtCoon();
                UbtLog.e(TAG, "----onLostBtCoon---" + cmd + "    mContext =MVPBaseActivity ");
                ((AlphaApplication) mContext.getApplicationContext()).doLostConn(AlphaApplication.getBaseActivity());
            } else {
                //自动连接等
                UbtLog.e(TAG, "----onLostBtCoon---" + cmd + "    mContext = else");
                ((AlphaApplication) mContext.getApplicationContext()).doLostConn(AlphaApplication.getBaseActivity());
            }
            return;
        }

        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager()
                .sendCommand(
                        ((AlphaApplication) mContext
                                .getApplicationContext()).getCurrentBluetooth()
                                .getAddress(), cmd,
                        param, param == null ? 0 : param.length,
                        isMonopolyRight);
    }

    public void UnRegisterHelper() {
        UbtLog.d(TAG, this.getClass().getName() + "DistoryHelper");
        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() != null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().removeBlueToothInteraction(this);
        }
    }

    public void unRegister() {

    }

    public abstract void DistoryHelper();

    public void RegisterHelper() {
        UbtLog.d(TAG, this.getClass().getName() + "RegisterHelper");

        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() != null) {
            UbtLog.d(TAG, "注册一个 RegisterHelper");
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().addBlueToothInteraction(this);
        }
    }

    @Override
    public void onDeviceDisConnected(String mac) {

        UbtLog.d(TAG, "--onDeviceDisConnected--" + this);
        try {
            if (read_battery_timer != null) {
                read_battery_timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        read_battery_timer = null;

        // if (isLostCoon()
        // && ((AlphaApplication) mBaseActivity.getApplicationContext())
        // .getCurrentBluetooth().getAddress().equals(mac))
        // ((AlphaApplication) mBaseActivity.getApplicationContext())
        // .setCurrentBluetooth(null);

        if (isLostCoon()) {
        } else {
            if (((AlphaApplication) mContext.getApplicationContext())
                    .getCurrentBluetooth().getAddress().equals(mac)) {
                ((AlphaApplication) mContext.getApplicationContext())
                        .setCurrentBluetooth(null);

            }
        }
        if (EventBus.getDefault().hasSubscriberForEvent(RobotEvent.class)) {
            RobotEvent disconnectEvent = new RobotEvent(RobotEvent.Event.DISCONNECT);
            EventBus.getDefault().post(disconnectEvent);
            UbtLog.d(TAG, "--MSG_DO_NOTE_DISCONNECT ");
        }

        if (mContext != null && mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).onLostBtCoon();
        }
    }

    public void doSendReadStateComm() {
        UbtLog.d(TAG, "doSendReadStateComm isStartHibitsProcess = " + isStartHibitsProcess);
        //读机器人状态(音量，灯光状态）
        byte[] params = new byte[1];
        params[0] = 0;
        doSendComm(ConstValue.DV_READSTATUS, params);
        //读取机器人加速度传感器是否开启（摔倒开启，关闭）状态
        byte[] sensorParams = new byte[2];
        sensorParams[0] = 0;
        sensorParams[1] = 0;
        doSendComm(ConstValue.DV_SENSOR_CONTROL, sensorParams);

        //读取机器人打招呼（摔倒开启，关闭）状态
        byte[] sensorGreetingParams = new byte[2];
        sensorGreetingParams[0] = 0;
        sensorGreetingParams[1] = 0;
        doSendComm(ConstValue.DV_SENSOR_GREETING, sensorGreetingParams);

        //连接蓝牙成功，读取一次行为习惯播放状态
        doSendComm(ConstValue.DV_READ_HIBITS_PLAY_STATUS, null);

        //定时读电量(10S读一次)
        byte[] param = new byte[1];
        param[0] = 4;
        doSendComm(ConstValue.DV_READ_BATTERY, param);

        if (read_battery_timer == null) {
            read_battery_timer = new Timer();
            read_battery_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isLostCoon()) {
                        return;
                    }
                    byte[] param = new byte[1];
                    param[0] = 4;
                    doSendComm(ConstValue.DV_READ_BATTERY, param);
                }
            }, 0, 10 * 1000);
        }
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        if (cmd == ConstValue.DV_READ_BATTERY) {
            Date curDate = new Date(System.currentTimeMillis());
            float time_difference = 500;
            if (lastTime_doReadState != null) {
                time_difference = curDate.getTime()
                        - lastTime_doReadState.getTime();
            }
            if (time_difference < 500) {
                return;
            }
            lastTime_doReadState = curDate;

            try {
                //根据机器人上报的状态来判断当前是否在充电中，0x01表示充电中， 0x00表示未在充电。
                byte charge = param[2];
                if (charge == 0x01) {
                    //UbtLog.d("BaseHelper", "charging" + "   power = " + param[3]);
                    setChargingState(true);
                } else if (charge == 0x0) {
                    //UbtLog.d("BaseHelper", "not charging" + "   power = " + param[3]);
                    setChargingState(false);
                } else if (charge == 0x03) {
                    setChargingState(true);
                }

                int power = param[3];
                setPowerValue((byte) power);
                if (power <= 10 && isNeedNoteLowPower) {
                    isNeedNoteLowPower = false;
                    // AlphaApplication.getBaseActivity().onNoteLowPower();
                } else if (power > 10) {
                    isNeedNoteLowPower = true;
                }
                if (param[2] == 0) {
                    if (power > 5 && power <= 20 && isNeedNoteLowPowerTwenty) {
                        UbtLog.d(TAG, "LESS 20 SHOW DIALOG");
                        AlphaApplication.getBaseActivity().onNoteLowPower(LOW_BATTERY_TWENTY);
                        isNeedNoteLowPowerTwenty = false;
                    }
                    if (power <= 5 && isNeedNoteLowPowerFive) {
                        UbtLog.d(TAG, "LESS 5 SHOW DIALOG");
                        AlphaApplication.getBaseActivity().onNoteLowPower(LOW_BATTERY_FIVE);
                        //notify the others modules
                        RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.LOW_BATTERY_LESS_FIVE_PERCENT);
                        EventBus.getDefault().post(robotEvent);
                        isLowBatteryNotExecuteAction=true;
                        isNeedNoteLowPowerFive = false;
                    }
                    if (power >= 20) {
                        if(isLowBatteryNotExecuteAction) {
                            UbtLog.d(TAG, "NOISE DATA POWER VALUE IS LARGER 20, SO NO DIALOG ");
                            isNeedNoteLowPowerTwenty = true;
                            isNeedNoteLowPowerFive = true;
                            isLowBatteryNotExecuteAction = false;
                        }
                    }
                } else {
                    isNeedNoteLowPowerTwenty = true;
                    isNeedNoteLowPowerFive = true;
                    isLowBatteryNotExecuteAction=false;
                }

            } catch (Exception e) {
                UbtLog.e(TAG, "Exception:" + e.getMessage());
            }
        } else if (cmd == ConstValue.DV_READSTATUS) {
            // 声音状态
            if (param[0] == 0) {
                // 静音
                if (param[1] == 1) {
                    mCurrentVoiceState = false;
                }
                // 有声音
                else {
                    mCurrentVoiceState = true;
                }
            }
            // 播放状态
            else if (param[0] == 1) {
                if (param[1] == 0) {
                    // 暂停
                } else {
                    // 非暂停
                }
            }
            // 音量状态
            else if (param[0] == 2) {

                int nCurrentVolume = param[1];
                if (nCurrentVolume < 0) {
                    nCurrentVolume += 256;
                }
                mCurrentVolume = nCurrentVolume;
            }
            // 舵机灯状态
            else if (param[0] == 3) {
                if (param[1] == 0) {
                    // 灭
                    mLightState = false;
                } else {
                    // 亮
                    mLightState = true;
                }
            }
            //TF卡状态
            else if (param[0] == 4) {
                // 拔出
                if (param[1] == 0) {
                    hasSdcard = false;
                }
                // 插入
                else {
                    hasSdcard = true;
                }
            }
        } else if (cmd == ConstValue.DV_DO_UPGRADE_STATUS) {
            final int upgradeStatus = param[0];
            UbtLog.d(TAG, "收到升级指令 :: " + upgradeStatus);

                /*mBaseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(upgradeStatus == 1){
                            Toast.makeText(mBaseActivity,mBaseActivity.getStringResources("ui_robot_upgrade_start"),Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(mBaseActivity,mBaseActivity.getStringResources("ui_upgrade_robot_low_battery"),Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
        } else if (cmd == ConstValue.DV_READ_NETWORK_STATUS) {
            String networkInfoJson = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "base cmd = " + cmd + "    networkInfoJson = " + networkInfoJson);

            NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson, NetworkInfo.class);
            if (networkInfo.status) {
                hasConnectNetwork = true;
                UbtLog.d(TAG, "base 网络已经连接");
            } else {
                hasConnectNetwork = false;
                UbtLog.d(TAG, "base 网络没有连接");
            }
        } else if (cmd == ConstValue.DV_SENSOR_CONTROL) {
            UbtLog.d(TAG, "DV_SENSOR_CONTROL Status" + param[0]);
            if (param[0] == 0) {
                //SENSOR DISABLE
                mSensorState = false;
            } else {
                //SENSOR ENABLE
                mSensorState = true;
            }
        } else if (cmd == ConstValue.DV_SENSOR_GREETING) {
            UbtLog.d(TAG, "DV_SENSOR_GREETING Status" + param[0]);
            if (param[0] == 0) {
                //SENSOR DISABLE
                mSensorGreetingState = false;
            } else {
                //SENSOR ENABLE
                mSensorGreetingState = true;
            }
        } else if (cmd == ConstValue.DV_READ_HIBITS_PLAY_STATUS) {

            String eventPlayStatusJson = BluetoothParamUtil.bytesToString(param);

            UbtLog.d(TAG, "cmd = " + cmd + "    eventPlayStatusJson = " + eventPlayStatusJson);
            EventPlayStatus eventPlayStatus = GsonImpl.get().toObject(eventPlayStatusJson, EventPlayStatus.class);
            isStartHibitsProcess = "1".equals(eventPlayStatus.eventState) ? true : false;

            UbtLog.d(TAG, "isStartHibitsProcess = " + isStartHibitsProcess);

            RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.HIBITS_PROCESS_STATUS);
            robotEvent.setHibitsProcessStatus(isStartHibitsProcess);
            EventBus.getDefault().post(robotEvent);
        }
    }

    public UserInfo getCurrentUser() {
        return ((AlphaApplication) mContext.getApplicationContext()).getCurrentUserInfo();
    }

    public void doRecordActionDownloadFinish(ActionInfo mAction) {
        // TODO Auto-generated method stub

    }

    public void readUserHead(int h, int w) {

        UserInfo currentUser = ((AlphaApplication) mContext
                .getApplicationContext()).getCurrentUserInfo();

        if (currentUser == null)
            return;
        String str = currentUser.userImage;

        GetDataFromWeb.getImageFromHttp(((AlphaApplication) mContext
                        .getApplicationContext()).getCurrentUserInfo().userImage,
                readUserHeadImgRequest, this, -1, -1, -1, -1);

    }

    public boolean isRightName(String name, int maxLenth, boolean isSpc,
                               String formate) {

        if (!isSpc && name.contains(" ")) {
            AlphaApplication.getBaseActivity().showToast("ui_action_name_spaces");
            return false;
        }

        // "[0-9A-Za-z_]*"

        if (!formate.equals("") && !name.matches(formate)) {
            AlphaApplication.getBaseActivity().showToast("ui_action_name_error");
            return false;
        }

        int lenth = 0;
        try {
            lenth = name.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(mContext,
                    AlphaApplication.getBaseActivity().getStringResources("ui_remote_synchoronize_unknown_error"), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        if (maxLenth > 0 && lenth > maxLenth) {
            Toast.makeText(mContext,
                    AlphaApplication.getBaseActivity().getStringResources("ui_action_name_too_long"), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        return true;
    }


    public void doClickedMessage() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD,
                BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD,
                null, -1);
    }

    public boolean clickedMessage() {
        return BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD).equals(BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD);
    }

    public String getRecordMessageId() {
        return BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.MESSAGE_ID);
    }

    public void setMessageIdRecord(String messageId) {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.MESSAGE_ID,
                messageId,
                null, -1);
    }

    public boolean isFirstUseSetting() {
        if (BasicSharedPreferencesOperator
                .getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS_FALSE)) {

            return false;
        }

        return true;
    }

    public void setFirstUsedSetting() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS,
                BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS_FALSE,
                null, -1);
    }

    public boolean isFirstPlayAction() {
        String value = BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION);
        if (value.equals(BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION)) {
            return false;
        } else {
            return true;
        }
    }

    public void setIsFirstPlayAction() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION,
                BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION, null, 0);
    }

    /**
     * 显示首次播放提示
     */
    public void showFirstPlayTip() {
        new AlertDialog(AlphaApplication.getBaseActivity())
                .builder()
                .setMsg(AlphaApplication.getBaseActivity().getStringResources("ui_first_play_tips"))
                .setCancelable(true)
                .setPositiveButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_confirm"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
    }


    public void initCourseAccessToken() {

        if (!TextUtils.isEmpty(mCourseAccessToken)) {
            return;
        }

        UserInfo userInfo = ((AlphaApplication) mContext.getApplicationContext()).getCurrentUserInfo();
        if (userInfo == null) {
            return;
        }

        String url = "";
        String param = "";

        if (userInfo.userRelationType > 0) {//第三方登录

            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.thrid_login);
            param = HttpAddress.getParamsForPost(new String[]{userInfo.userRelationId, userInfo.userRelationType + ""},
                    HttpAddress.Request_type.thrid_login, this.mBaseActivity);

        } else {
            if (TextUtils.isEmpty(userInfo.userPassword)) {
                return;
            }

            UbtLog.d(TAG, "userInfo userPhone = " + userInfo.userPhone + "	userEmail = " + userInfo.userEmail + "	userPassword = " + userInfo.userPassword + "   convertMD5 = " + Md5.convertMD5(userInfo.userPassword));

            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.login);

            if (!TextUtils.isEmpty(userInfo.userPhone)) {
                param = HttpAddress.getParamsForPost(new String[]{userInfo.userPhone, Md5.convertMD5(userInfo.userPassword)},
                        HttpAddress.Request_type.login, this.mBaseActivity);

            } else {
                param = HttpAddress.getParamsForPost(new String[]{userInfo.userEmail, Md5.convertMD5(userInfo.userPassword)},
                        HttpAddress.Request_type.login, this.mBaseActivity);
            }
        }

        UbtLog.d(TAG, "url = " + url);
        UbtLog.d(TAG, "param = " + param);

        OkHttpClientUtils
                .getJsonByPostRequest(url, param, -1)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d(TAG, "initCourseAccessToken exception : " + e.getMessage() + "   mContext = " + mContext);
                        //mHandler.sendEmptyMessage(DO_GET_COURSE_TOKEN_FAIL);
                        if (mContext instanceof BlocklyCourseActivity) {
                            LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_FAIL);
                            EventBus.getDefault().post(lessonEvent);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        BaseResponseModel<List<UserInfo>> baseResponseModel = GsonImpl.get().toObject(response, new TypeToken<BaseResponseModel<List<UserInfo>>>() {
                        }.getType());

                        UbtLog.d(TAG, "initCourseAccessToken response " + response + "   mContext = " + mContext);
                        if (baseResponseModel.status) {
                            UserInfo current_uer = baseResponseModel.models.get(0);

                            mCourseAccessToken = current_uer.accessToken;
                            if (mContext instanceof BlocklyCourseActivity) {
                                LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_SUCCESS);
                                EventBus.getDefault().post(lessonEvent);
                            }
                        } else {
                            if (mContext instanceof BlocklyCourseActivity) {
                                LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_FAIL);
                                EventBus.getDefault().post(lessonEvent);
                            }
                        }
                    }
                });
    }


    //设置当前是否充电中
    private void setChargingState(boolean charging) {
        isCharging = charging;
    }

    //获取当前是否充电中
    public boolean getChargingState() {
        return isCharging;
    }

    private void setPowerValue(byte value) {
        mPowerValue = value;
    }

    public byte getPowerValue() {
        return mPowerValue;
    }


    //获取当前机器人是否正在行为提醒
    public boolean isStartHibitsProcess() {
        //isStartHibitsProcess = true;

        return isStartHibitsProcess;
    }

    //显示行为提醒弹出框
    public void showStartHibitsProcess(final IDismissCallbackListener mIListener) {
        String msg = "行为习惯正在进行中，请先完成";
        String position = "好的";
        if (mContext instanceof MVPBaseActivity) {
            msg = ((MVPBaseActivity) mContext).getStringResources("ui_habits_process_starting");
            position = ((MVPBaseActivity) mContext).getStringResources("ui_common_ok");
        } else if (mContext instanceof BaseActivity) {
            msg = ((BaseActivity) mContext).getStringResources("ui_habits_process_starting");
            position = ((BaseActivity) mContext).getStringResources("ui_common_ok");
        }

        new ConfirmDialog(mContext)
                .builder()
                .setMsg(msg)
                .setCancelable(false)
                .setPositiveButton(position, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mIListener != null) {
                            mIListener.onDismissCallback(isStartHibitsProcess);
                        }
                    }
                }).show();
    }
    //BIND interface
    public void checkMyRobotState() {
        UbtLog.d(TAG, "click rl_hibits_event 2");
        LoadingDialog.show(AppManager.getInstance().currentActivity());
        CheckIsBindRequest checkRobotInfo = new CheckIsBindRequest();
        checkRobotInfo.setSystemType("3");
        String url = HttpEntity.CHECK_ROBOT_INFO;
        doRequest(url,checkRobotInfo,CHECK_ROBOT_INFO_HABIT);
    }

    /**
     * 请求网络操作
     */
    public void doRequest(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequest onError:" + e.getMessage());
                switch (id){
                    case CHECK_ROBOT_INFO_HABIT:
                       // mView.onGetRobotInfo(0,null);
                        ToastUtils.showShort("获取机器人信息失败！");
                        LoadingDialog.dismiss(AppManager.getInstance().currentActivity());
                        break;
                    case ROBOT_GOTO_BIND:
                        if (robotBindingDialog != null && robotBindingDialog.isShowing()) {
                            robotBindingDialog.display();
                            robotBindingDialog = null;
                        }
                        adviceBindFail("");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                switch (id) {
                    case CHECK_ROBOT_INFO_HABIT:
                        LoadingDialog.dismiss(AppManager.getInstance().currentActivity());
                        BaseResponseModel<ArrayList<MyRobotModel>> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<MyRobotModel>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if(!baseResponseModel.status || baseResponseModel.models == null){
                            //mView.onGetRobotInfo(0,null);
                            ToastUtils.showShort("获取机器人信息失败！");
                        }else {
                            if(baseResponseModel.models.size() == 0){
                                //mView.onGetRobotInfo(2,null);
                                habitAdviceGotoBindDialog();
                                return;
                            }else {
                                // mView.onGetRobotInfo(1,baseResponseModel.models.get(0));
                                UbtLog.d(TAG, "账号已经绑定 ");
                                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.ROBOT_BIND_SUCCESS);
                                EventBus.getDefault().post(robotEvent);
                                UbtLog.d(TAG, "size = "+baseResponseModel.models.size());
                                UbtLog.d(TAG, "autoUpgrade = " + baseResponseModel.models.get(0).getAutoUpgrade());
                                UbtLog.d(TAG, "equipmentSeq = " + baseResponseModel.models.get(0).getEquipmentSeq());
                                UbtLog.d(TAG, "equipmentVersion = " + baseResponseModel.models.get(0).getEquipmentVersion());
                            }
                        }
                        break;
                    case ROBOT_GOTO_BIND:
                        //				BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());
                        BaseResponseModel<String> baseResponseModel1 = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<String>>() {
                                }.getType());
                        if (robotBindingDialog != null && robotBindingDialog.isShowing()) {
                            robotBindingDialog.display();
                            robotBindingDialog = null;
                        }
                        UbtLog.d(TAG, "status:" + baseResponseModel1.status);
                        UbtLog.d(TAG, "info:" + baseResponseModel1.info);
                        if (baseResponseModel1.status) {
                            UbtLog.d(TAG, "绑定状态获取验证");
                            if (baseResponseModel1.models == null || baseResponseModel1.models.equals("")) {
                                adviceBindSuccess();
                            } else if (baseResponseModel1.models != null && baseResponseModel1.models.startsWith("1002:")) {
                                if(baseResponseModel1.models.length() == 5){
                                    return;
                                }
                                adviceBindFail("机器人已被 "+ FileUtils.utf8ToString(baseResponseModel1.models.substring(5))+" 绑定！");
                            } else if(baseResponseModel1.models != null && baseResponseModel1.models.equals("1004")){
                                adviceBindFail("机器人不存在！");
                            }
                        } else {
                            adviceBindFail("");
                            UbtLog.d(TAG, "绑定失败");
                        }
                        break;
                    default:
                        break;
                }
            }
        });

    }
    //若要使用此功能，需先绑定机器人！
    public void habitAdviceGotoBindDialog() {
        new ConfirmDialog(mContext).builder()
                .setTitle("提示")
                .setMsg("若要使用此功能，需先绑定机器人！")
                .setCancelable(false)
                .setPositiveButton("一键绑定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "一键绑定 ");
                        if (MainUiBtHelper.getInstance(mContext).isLostCoon()) {
                            UbtLog.d(TAG, "没有连接蓝牙 ");
                            if(mContext!=null) {
                                Intent intent = new Intent(mContext, BluetoothandnetconnectstateActivity.class);
                                mContext.startActivity(intent);
                            }
                        } else {
                            UbtLog.d(TAG, "连接了蓝牙 ");
                            gotoBind();
                        }
                    }
                })
                .setNegativeButton("暂不", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "暂不 ");
                    }
                }).show();
    }

    RobotBindingDialog robotBindingDialog = null;

    //一键绑定
    public void gotoBind() {
        if (AlphaApplication.currentRobotSN == null || AlphaApplication.currentRobotSN.equals("")) {
            ToastUtils.showShort("未读到机器人序列号");
            return;
        }
        if (robotBindingDialog == null) {
            robotBindingDialog = new RobotBindingDialog(AppManager.getInstance().currentActivity())
                    .builder()
                    .setCancelable(true);
        }
        robotBindingDialog.show();
        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.ROBOT_BIND;
        doRequest(url, gotoBindRequest, ROBOT_GOTO_BIND);

    }

    //绑定成功！
    public void adviceBindSuccess() {
        Drawable img_ok;
        Resources res1 =mContext.getResources();
        img_ok = res1.getDrawable(R.drawable.ic_bind_success);
        new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("绑定成功！")
                .setMsg("可到“个人中心-设置-我的机器人”查看状态。")
                .setCancelable(true)
                .setPositiveButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "我知道了 ");
                    }
                })
                .setTitlePicture(img_ok)
                .setNoTitleLayout()
                .show();
    }

    //绑定失败！
    public void adviceBindFail(String reason) {
        Drawable img_off;
        Resources res2 =mContext.getResources();
        img_off = res2.getDrawable(R.drawable.ic_bind_fail);
        new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("绑定失败！")
                .setMsg(reason)
                .setCancelable(true)
                .setPositiveButton("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "重试 ");
                        gotoBind();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "取消 ");
                    }
                })
                .setTitlePicture(img_off)
                .setNoTitleLayout()
                .show();
    }
}
