package com.ubt.alpha1e.edu.ui.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.action.actioncreate.WriteImageListener;
import com.ubt.alpha1e.edu.business.NewActionPlayer;
import com.ubt.alpha1e.edu.business.NewActionPlayer.PlayerState;
import com.ubt.alpha1e.edu.business.NewActionsManager;
import com.ubt.alpha1e.edu.business.NewActionsManagerListener;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;

import java.io.File;
import java.util.List;

public class ActionsEditHelper extends BaseHelper implements
        NewActionsManagerListener {


    public enum StartType {
        edit_type, new_type
    }

    public enum Command_type {
        Do_play, Do_pause_or_continue, Do_Stop
    }

    public final static String StartTypeStr = "StartTypeStr";
    public final static String NewActionInfo = "NewActionInfo";

    public final static String MAP_FRAME = "MAP_FRAME";
    public final static String MAP_FRAME_NAME = "MAP_FRAME_NAME";
    public final static String MAP_FRAME_TIME = "MAP_FRAME_TIME";
    public final static String MAP_FRAME_SHOW_TIME = "MAP_FRAME_SHOW_TIME";

    public static final int SaveActionReq = 11001;
    public static final String SaveActionResult = "SaveActionResult";

    private static final int msg_on_read_all_eng = 1001;
    private static final int msg_on_change_action_finish = 1002;

    public static final int GetUserHeadRequestCodeByShoot = 1001;
    public static final int GetUserHeadRequestCodeByFile = 1002;
    public static final int GetThumbnailRequestCodeByVideo = 1008;

    private NewActionPlayer mNewPlayer;
    private NewActionsManager mNewActionsManager;
    private IEditActionUI mUI;
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == msg_on_read_all_eng) {
                mUI.onReadEng((byte[]) msg.obj);
            }
            if (msg.what == msg_on_change_action_finish) {
                UbtLog.d("hand", "wmma msg_on_change_action_finish:" + mUI.getClass());

                mUI.onChangeActionFinish();
            }
        }
    };

    @Override
    public void UnRegisterHelper() {
      //  super.UnRegisterHelper();
    }


    @Override
    public void unRegister(){
        super.UnRegisterHelper();
        mNewActionsManager.removeListener(this);
        mNewPlayer.removeListener(mUI);
    }

    public boolean getActionSaveState() {
        return mNewActionsManager.isSaveSuccess;
    }

    public String getErrCode() {
        return mNewActionsManager.errCode;
    }

    public NewActionInfo getNewActionInfo() {

        return mNewActionsManager.mChangeNewActionInfo;
    }

    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
        mNewActionsManager.addListener(this);
    }

    public ActionsEditHelper(BaseActivity _baseActivity, IEditActionUI _ui) {
        super(_baseActivity);
        mUI = _ui;
        try {  //以下会报空错误，先try catch
            if (mNewPlayer == null) {
                mNewPlayer = NewActionPlayer
                        .getPlayer(((AlphaApplication) mContext
                                .getApplicationContext()).getCurrentBluetooth()
                                .getAddress());
            }

        } catch (Exception e) {
            UbtLog.d("ActionsEditHelper", "e:" + e.getMessage());
        }

        if (mNewPlayer != null) {
            mNewPlayer.addListener(mUI);
        }
        mNewActionsManager = NewActionsManager.getInstance(_baseActivity);
        mNewActionsManager.addListener(this);

    }

    public ActionsEditHelper(Context context, IEditActionUI _ui) {
        super(context);
        mUI = _ui;
        try {  //以下会报空错误，先try catch
            if (mNewPlayer == null) {
                mNewPlayer = NewActionPlayer
                        .getPlayer(((AlphaApplication) context
                                .getApplicationContext()).getCurrentBluetooth()
                                .getAddress());
            }

        } catch (Exception e) {
            UbtLog.d("ActionsEditHelper", "e:" + e.getMessage());
        }

        if (mNewPlayer != null) {
            mNewPlayer.addListener(mUI);
        }
        mNewActionsManager = NewActionsManager.getInstance(context);
        mNewActionsManager.addListener(this);

    }


    public ActionsEditHelper(BaseActivity _baseActivity, IEditActionUI _ui, long tag) {
        super(_baseActivity);
        mUI = _ui;
//        if (mNewPlayer == null) {
//            mNewPlayer = NewActionPlayer
//                    .getPlayer(((AlphaApplication) mBaseActivity
//                            .getApplicationContext()).getCurrentBluetooth()
//                            .getAddress());
//        }
//        if (mNewPlayer != null) {
//            mNewPlayer.addListener(mUI);
//        }
        mNewActionsManager = NewActionsManager.getInstance(_baseActivity);
        mNewActionsManager.addListener(this);
    }


    public void doReadAllEng() {
        doSendComm(ConstValue.READ_ALL_ENGINE, null);
    }

    // ʱ�䵥λ������
    public void doCtrlAllEng(byte[] datas) {
        doSendComm(ConstValue.CTRL_ALL_ENGINE, datas);
    }


    /**
     * 读取红外传感器距离
     *
     * @param status 01表示进入 ，00表示离开
     */
    public void doEnterCourse(byte status) {
        UbtLog.d("ActionsEditHelper", "doEnterActionCourse status:" + status);
        byte[] params = new byte[1];
        params[0] = status;
        doSendComm(ConstValue.DV_ENTER_COURSE, params);
    }

    /**
     * 进入和退出动作编辑
     */
    public void doEnterOrExitActionEdit(byte status) {
        byte[] params = new byte[2];
        params[0] = status;
        params[1] = 0;
        UbtLog.d("ActionsEditHelper", "doEnterOrExitActionEdit status:" + ByteHexHelper.bytesToHexString(params));
        doSendComm(ConstValue.DV_INTO_EDIT, params);
    }


    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeviceDisConnected(String mac) {
        super.onDeviceDisConnected(mac);
        UbtLog.d("ActionsEditHelper", "--onDeviceDisConnected--" + this);
        if (mListener != null) {
            mListener.onDisconnect();
        }
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        // UbtLog.d("EditHelper", "cmd==" + cmd + "  params==" + ByteHexHelper.bytesToHexString(param));
        if (cmd == ConstValue.READ_ALL_ENGINE) {
            UbtLog.d("onReceiveData", ByteHexHelper.bytesToHexString(param));
            Message msg = new Message();
            msg.what = msg_on_read_all_eng;
            msg.obj = param;
            mHandler.sendMessage(msg);
        } else if (cmd == ConstValue.CTRL_ONE_ENGINE) {
            UbtLog.d("onReceiveData", ByteHexHelper.bytesToHexString(param));
            Message msg = new Message();
            msg.what = msg_on_read_all_eng;
            msg.obj = param;
            mHandler.sendMessage(msg);
        } else if (cmd == ConstValue.DV_SET_PLAY_SOUND) {
            if (param != null) {
                UbtLog.d("EditHelper", "sound:" + ByteHexHelper.bytesToHexString(param) + "param[0]:" + param[0]);
                if (param[0] == 1) {
                    UbtLog.d("EditHelper", "播放完成");
                    if (mListener != null) {
                        mListener.playComplete();
                    }
                }
            }
        } else if (cmd == 0xE1) {
            UbtLog.d("EditHelper", "退出课程");
        } else if (cmd == ConstValue.DV_ACTION_FINISH)// 动作播放完毕
        {
            UbtLog.d("ActionEditHelper", "动作播放完成");
            if (mListener != null) {
                mListener.playComplete();
            }
        } else if (cmd == ConstValue.DV_TAP_HEAD) {
            UbtLog.d("EditHelper", "TAP_HEAD = " + cmd);
            if (mListener != null) {
                mListener.tapHead();
            }
        } else if (cmd == ConstValue.DV_INTO_EDIT) {
            if (param != null) {
                UbtLog.d("", "进入或退出动作编辑param:" + ByteHexHelper.bytesToHexString(param));
            }
        }
    }

    PlayCompleteListener mListener;

    public PlayCompleteListener getListener() {
        return mListener;
    }

    public void setListener(PlayCompleteListener listener) {
        mListener = listener;
    }

    public interface PlayCompleteListener {
        void playComplete();

        void onDisconnect();

        void tapHead();
    }

    public void doLostPower() {
        doSendComm(ConstValue.DV_DIAODIAN, null);
    }

    public void doLostOnePower(int id) {
        byte[] params = new byte[1];
        params[0] = ByteHexHelper.intToHexByte(id);
        doSendComm(ConstValue.CTRL_ONE_ENGINE, params);
    }

    /**
     * 课程播放
     *
     * @param str
     */
    public void playCourse(String str) {

        doSendComm(ConstValue.DV_SET_PLAY_SOUND, BluetoothParamUtil.stringToBytes(str));
    }

    /**
     * 播放动作
     *
     * @param actionName
     */
    public void playAction(String actionName) {
        UbtLog.d("ActionEditHelper", " playAction actionName===" + actionName);
        byte[] actions = BluetoothParamUtil.stringToBytes(actionName);
        if (null != ((AlphaApplication) mContext
                .getApplicationContext()).getBlueToothManager() && null != (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth())) {
            ((AlphaApplication) mContext
                    .getApplicationContext()).getBlueToothManager().sendCommand(((AlphaApplication) mContext.getApplicationContext())
                    .getCurrentBluetooth().getAddress(), ConstValue.DV_PLAYACTION, actions, actions.length, false);
        }
    }

    /**
     * 结束动作
     */
    public void stopAction() {
        if (null != ((AlphaApplication) mContext
                .getApplicationContext()).getBlueToothManager() && null != (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth())) {
            ((AlphaApplication) mContext
                    .getApplicationContext()).getBlueToothManager().sendCommand(((AlphaApplication) mContext.getApplicationContext())
                    .getCurrentBluetooth().getAddress(), ConstValue.DV_STOPPLAY, null, 0, false);
        }
    }

    /**
     * 播放音效
     *
     * @param params
     */

    public void playSoundAudio(String params) {
        UbtLog.d("playSoundAudio", "params = " + params);
        doSendComm(ConstValue.DV_SET_PLAY_SOUND, BluetoothParamUtil.stringToBytes(params));
    }

    /**
     * 停止音效
     */

    public void stopSoundAudio() {
        doSendComm(ConstValue.DV_SET_STOP_VOICE, null);
    }

    public void doLostLeftHandAndRead() {
        doLostOnePower(1);
        doLostOnePower(2);
        doLostOnePower(3);

    }

    public void doLostRightHandAndRead() {
        doLostOnePower(4);
        doLostOnePower(5);
        doLostOnePower(6);
    }

    public void doLostLeftFootAndRead() {
        doLostOnePower(7);
        doLostOnePower(8);
        doLostOnePower(9);
        doLostOnePower(10);
        doLostOnePower(11);

    }

    public void doLostRightFootAndRead() {
        doLostOnePower(12);
        doLostOnePower(13);
        doLostOnePower(14);
        doLostOnePower(15);
        doLostOnePower(16);
    }

    public PlayerState getNewPlayerState() {
        if (this.mNewPlayer == null) {
            return PlayerState.STOPING;
        } else {
            return this.mNewPlayer.getState();
        }
    }

    public void doActionCommand(Command_type comm_type, NewActionInfo action) {

        if (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth() == null) {
            return;
        }

        if (comm_type == Command_type.Do_play) {

            mNewPlayer.PlayAction(action, mContext);

        } else if (comm_type == Command_type.Do_pause_or_continue) {
            if (mNewPlayer.getState() != PlayerState.STOPING) {
                if (mNewPlayer.getState() == PlayerState.PAUSING) {
                    mNewPlayer.ContinuePlayer();
                } else if (mNewPlayer.getState() == PlayerState.PLAYING) {
                    mNewPlayer.PausePalyer();
                }
            }
        } else if (comm_type == Command_type.Do_Stop) {
            if (mNewPlayer.getState() != PlayerState.STOPING)
                mNewPlayer.StopPlayer();

        }

    }

    public void saveMyNewAction(final NewActionInfo mCurrentAction,
                                Bitmap mCurrentActionImg, final String musicDir) {
        if (mCurrentActionImg != null) {
            mCurrentAction.actionHeadUrl = FileTools.actions_new_cache + File.separator + "Images/" + System.currentTimeMillis() + ".jpg";
            FileTools.writeActionImg(mCurrentActionImg,
                    mCurrentAction.actionHeadUrl, true, new WriteImageListener() {
                        @Override
                        public void writeFinsh() {
                            if (getCurrentUser() == null) {
                                mCurrentAction.editerId = "";
                            } else {
                                mCurrentAction.editerId = getCurrentUser().userId + "";
                            }
                            if (mCurrentAction.actionId == -1) {
                                mNewActionsManager.doSave(mCurrentAction, musicDir);
                            } else {
                                mNewActionsManager.doUpdate(mCurrentAction);
                            }
                        }
                    });
        } else {
            mCurrentAction.actionHeadUrl = "";
        }
/*        if (getCurrentUser() == null) {
            mCurrentAction.editerId = "";
        } else {
            mCurrentAction.editerId = getCurrentUser().userId + "";
        }
        if (mCurrentAction.actionId == -1) {
            mNewActionsManager.doSave(mCurrentAction, musicDir);
        } else {
            mNewActionsManager.doUpdate(mCurrentAction);
        }*/
    }

    public void saveMyNewAction(NewActionInfo mCurrentAction,
                                Bitmap mCurrentActionImg, long dubTag, int type) {
        if (mCurrentActionImg != null) {
            mCurrentAction.actionHeadUrl = FileTools.actions_new_cache + File.separator + "Images/" + System.currentTimeMillis() + ".jpg";
            FileTools.writeImage(mCurrentActionImg, mCurrentAction.actionHeadUrl, true);
        } else {
            mCurrentAction.actionHeadUrl = "";
        }
        if (getCurrentUser() == null) {
            mCurrentAction.editerId = "";
        } else {
            mCurrentAction.editerId = getCurrentUser().userId + "";
        }
        mNewActionsManager.doSave(mCurrentAction, dubTag, type);
    }

    @Override
    public void onReadNewActionsFinish(
            List<com.ubt.alpha1e.edu.data.model.NewActionInfo> actions) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChangeNewActionsFinish() {
        // TODO Auto-generated method stub
        UbtLog.d("achelper", "wmma msg_on_change_action_finish");
        Message msg = new Message();
        msg.what = msg_on_change_action_finish;
        mHandler.sendMessage(msg);
    }

    public void changeFirstUseEditState() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION,
                BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION_FALSE,
                null, -1);
    }

    public boolean isFirstEditMain() {
        if (BasicSharedPreferencesOperator
                .getInstance(mContext, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION_FALSE))
            return false;
        return true;
    }

    public void readImg(long action_id, String actionImagePath) {
        FileTools.readImageFromSDCacheASync(action_id, actionImagePath, -1, -1,
                mUI);
    }

    /**
     * 复位动作
     **/
    public void doDefaultActions() {
        byte[] param = new byte[1];
        param[0] = 0;
        doSendComm(ConstValue.DV_SET_ACTION_DEFAULT, param);
    }
}
