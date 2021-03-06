package com.ubt.alpha1e.edu.business;

import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.event.ActionEvent;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e.edu.utils.log.MyLog;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.BlueToothManager;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ActionPlayer implements BlueToothInteracter {

    private static final String TAG = "ActionPlayer";

    // 当前正在放的动作（可能是动作列表）
    private List<String> mSourceActionNameList;
    // 主干信息
    private static ActionPlayer thiz;
    private BlueToothManager mBtManager;
    private String mBtMac;
    private List<ActionPlayerListener> mListeners;
    // 状态信息
    private Play_state mCurrentPlayState = Play_state.action_finish;
    public Play_type mCurrentPlayType;
    private Play_type mSend_Stop_playType=Play_type.default_action;
    // 循环播放相关信息
    private CycleThread mCyclePlayThread;
    private Object mCyclePlaylock = new Object();
    private Object mStoplock=new Object();
    private boolean mIsCycleContinuePlay = true;
    // 机器人里面的动作列表
    private static List<String> mRobotActions;
    private String mCurrentDefaultAction = "初始化";
    // 常量
    public final static String CYCLE_ACTION_NAME = "CYCLE_ACTION_NAME";

    private static final int UI_NOTE_PLAY_CYCLE_NEXT = 1001;
    private static final int UI_NOTE_PLAY_CYCLE_STOP = 1002;//循环20分钟 自动停止
    //Servo protection time 20 minutes
    private static final int AUTO_STOP_PLAY_CYCLE_TIME =20*60*1000;  //20分钟
    private String Servo_protection_indication="{\"filename\":\"循环播放20分钟停下.wav\",\"playcount\":1}";

    private String cycleActionName = "";  //增加循环播放时动作的名称，用于在全局控件中更新显示动作名称
    private String mCurrentPlayActionName = "";
    private long mCurrentExecuteTime=0;
    private long mCycleExecuteTime=0;
    private long actionOriginalId = 0;


    private Date mLastPlayTime = null;
    private long time=0;
    private List<Map<String, Object>> mDatas = new ArrayList<>();
    private boolean isCycleLive=false;
    //private boolean syncCommand=false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UI_NOTE_PLAY_CYCLE_NEXT:
                    UbtLog.d(TAG,"PLAY CYCLE NEXT");
                    if (thiz.mListeners != null) {
                        for (int j = 0; j < mListeners.size(); j++) {
                            cycleActionName = (String)msg.obj;
                            thiz.mListeners.get(j).notePlayCycleNext((String)msg.obj);
                        }
                    }
                    break;
                case UI_NOTE_PLAY_CYCLE_STOP:
                    UbtLog.d(TAG,"20 分钟时间到，自动停止循环播放");
                    mBtManager.sendCommand(mBtMac, ConstValue.DV_SET_PLAY_SOUND, BluetoothParamUtil.stringToBytes(Servo_protection_indication),BluetoothParamUtil.stringToBytes(Servo_protection_indication).length,false);
                    //bug22702
                    try {
                        stopPlayingAndClearPlayingList(true);
                        notifyMainActivityEvent(mCurrentPlayActionName, ActionEvent.Event.ACTION_PLAY_FINISH);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

        }
    };

    public static enum Play_type {
        single_action, cycle_action,default_action
    }

    public static enum Play_state {
        action_playing, action_pause, action_finish
    }

    private ActionPlayer() {

    }

    public Play_state getState() {
        // 获取当前播放状态
        return thiz.mCurrentPlayState;
    }

    public static void setState() {
        thiz.mCurrentPlayState = Play_state.action_finish;
    }

    public String getBtMac(){
        return thiz.mBtMac;
    }

    public String getPlayActonName() {
        if (mSourceActionNameList == null || mSourceActionNameList.size() < 1) {
            return "";
        } else if (mSourceActionNameList.size() > 1) {
            return cycleActionName;
        } else{
            return mSourceActionNameList.get(0);
        }
    }

    public long getActionOriginalId(){
        return actionOriginalId;
    }

    public static void setRobotActions(List<String> actions) {
        mRobotActions = actions;
    }

    public Play_type getPlayType() {
        return thiz.mCurrentPlayType;
    }

    public int getCycleNum() {
        if (mSourceActionNameList == null){
            return 0;
        }else{
            return mSourceActionNameList.size();
        }
    }

    public static ActionPlayer getInstance(BlueToothManager _btManager,
                                           String device_mac) {
        if (thiz == null){
            thiz = new ActionPlayer();
        }
        thiz.mBtManager = _btManager;
        thiz.mBtManager.addBlueToothInteraction(thiz);
        thiz.mBtMac = device_mac;
        return thiz;
    }

    public static ActionPlayer getInstance() {
            if (thiz == null){
                thiz = new ActionPlayer();
            }
        return thiz;
    }

    public static void reset() {
        thiz.mBtManager.removeBlueToothInteraction(thiz);
        thiz = null;
    }

    public void addListener(ActionPlayerListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<ActionPlayerListener>();
        }

        if (!mListeners.contains(listener)){
            mListeners.add(listener);
        }
    }

    public void removeListener(ActionPlayerListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<ActionPlayerListener>();
        }

        if (mListeners.contains(listener)){
            mListeners.remove(listener);
        }
    }

    public void doStopPlay() {
        //DEBUG
       // new Exception().printStackTrace();
        doStopCurrentPlay(true);
    }

    private void doStopCurrentPlay(boolean needSendComm) {
        UbtLog.d(TAG, "doStopPlay尝试停止播放器:mCurrentPlayState-->"
                + mCurrentPlayState + ",mCurrentPlayType-->" + mCurrentPlayType);
        mSend_Stop_playType=mCurrentPlayType;
        if (thiz.mCurrentPlayState != Play_state.action_finish) {
            if (thiz.mCurrentPlayType == Play_type.single_action) {
                mCyclePlayThread = null;
               // new Exception().printStackTrace();
                thiz.doStopAction(needSendComm);
            } else {
                thiz.doStopCycle(needSendComm);
            }
        }
        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<String>();
        }
        mSourceActionNameList.clear();
    }

    /**
     * 单独播放内容，播放的动作路径取决于PLAYTYPE(GAMEPAD,默认NULL类型的话，认为DEFAULT路径
     * @param info
     */

    public void doPlayAction(ActionInfo info) {
        UbtLog.d(TAG, "---wmma mCurrentPlayState=" + mCurrentPlayState + " actionOriginalId =" + info.actionOriginalId);
        if(thiz == null){
            return;
        }
        actionOriginalId = info.actionOriginalId;
        if (thiz.mCurrentPlayState == Play_state.action_pause) {
            thiz.mCurrentPlayState = Play_state.action_finish;
            UbtLog.d(TAG, "---wmma mCurrentPlayType=" + mCurrentPlayType);
            if (mCurrentPlayType == Play_type.cycle_action) {
                thiz.doStopCycle(true);
            }else{
                doStopCurrentPlay(false);
            }
        } else {
            // 原来逻辑
            // doStopPlay();
            // 修改之后的逻辑
            doStopCurrentPlay(false);
        }
        mCurrentPlayType = Play_type.single_action;
        UbtLog.d(TAG, "doPlayAction-->" + thiz.mCurrentPlayState + " to action_playing");
        thiz.mCurrentPlayState = Play_state.action_playing;

        String action_name = "";

        if (info.hts_file_name != null && !info.hts_file_name.equals("")){
            action_name = info.hts_file_name.split("\\.")[0];
        }else{
            action_name = info.actionName;
        }

        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<>();
        }
        mSourceActionNameList.add(action_name);
        AlphaApplication.getBaseActivity().saveCurrentPlayingActionName(action_name);
        MyActionsHelper.Action_type mCurrentActionType= MyActionsHelper.mCurrentLocalPlayType;
        if(action_name.equals(mCurrentDefaultAction)){
            MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
            MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
        }
        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local
                || MyActionsHelper.mCurrentPlayType == MyActionsHelper.Action_type.My_download){
            action_name = FileTools.actions_download_robot_path + "/"+action_name+".hts";
        }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
            action_name = FileTools.actions_creation_robot_path + "/"+action_name+".hts";
        }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_gamepad){
            action_name = FileTools.actions_gamepad_robot_path + "/"+action_name+".hts";
        }else{
            action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
        }

        UbtLog.d(TAG,"mCurrentPlayActionName = " + action_name + "--" + MyActionsHelper.mCurrentLocalPlayType + "---" + MyActionsHelper.mCurrentPlayType + "--" + mCurrentDefaultAction);

        doPlay(action_name);
        if (thiz.mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                //BUG show playing status, play name is default foot when enter the remote UI
                try {
                    if (action_name.equals("action/controller/Default foot.hts")) {
                        UbtLog.d(TAG, "remote control return default foots");
                        return;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                thiz.mListeners.get(i).notePlayStart(mSourceActionNameList,
                        info, mCurrentPlayType);
            }

        }

        notifyMainActivityEvent(action_name, ActionEvent.Event.ACTION_PLAY_START);
    }

    private void notifyMainActivityEvent(String action_name, ActionEvent.Event actionPlayStart) {
        if (EventBus.getDefault().hasSubscriberForEvent(ActionEvent.class)) {
            ActionEvent actionEvent = new ActionEvent(actionPlayStart);
            actionEvent.setActionName(action_name);
            EventBus.getDefault().post(actionEvent);
        }
    }

    /*******************************add for block*****************************************/

    public void doPlayActionForBlockly(String actionName, boolean isWalk){
        UbtLog.d(TAG, "---wmma mCurrentPlayState=" + mCurrentPlayState + " actionName =" + actionName);
        if (thiz.mCurrentPlayState == Play_state.action_pause) {
            thiz.mCurrentPlayState = Play_state.action_finish;
            UbtLog.d(TAG, "---wmma mCurrentPlayType=" + mCurrentPlayType);
            if (mCurrentPlayType == Play_type.cycle_action) {
                thiz.doStopCycle(true);
            }else{
                doStopCurrentPlay(false);
            }
        } else {
            doStopCurrentPlay(false);
        }
        mCurrentPlayType = Play_type.single_action;
        MyLog.writeLog("播放功能", "doPlayAction-->" + thiz.mCurrentPlayState + " to action_playing");
        thiz.mCurrentPlayState = Play_state.action_playing;

        String action_name = "";
        action_name = actionName;

        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<>();
        }
        mSourceActionNameList.add(action_name);
        MyActionsHelper.Action_type mCurrentActionType= MyActionsHelper.mCurrentLocalPlayType;
        if(actionName.equals(mCurrentDefaultAction)){
            MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
            MyActionsHelper.mCurrentPlayType = MyActionsHelper.Action_type.Unkown;
        }

        if(isWalk){
            action_name = FileTools.actions_walk_robot_path + "/"+action_name+".hts";
        }else{
            if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local
                    || MyActionsHelper.mCurrentPlayType == MyActionsHelper.Action_type.My_download){
                action_name = FileTools.actions_download_robot_path + "/"+action_name+".hts";
            }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
                action_name = FileTools.actions_creation_robot_path + "/"+action_name+".hts";
            }else{
                action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
            }
        }

        UbtLog.d(TAG,"mCurrentPlayActionName = " + action_name + "--" + MyActionsHelper.mCurrentLocalPlayType + "---" + MyActionsHelper.mCurrentPlayType + "--" + mCurrentDefaultAction);

        doPlay(action_name);
        if (thiz.mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                thiz.mListeners.get(i).notePlayStart(mSourceActionNameList,
                        null, mCurrentPlayType);
            }
        }
    }

    /*******************************end*****************************************/

    public void doCycle(String[] _action_name) {
        UbtLog.d(TAG, "****循环播放功能 ActionPlayer.doCycle:_action_name-->"+ _action_name.length);
        mCycleExecuteTime=System.currentTimeMillis();
        doStopPlay();
        doInitDefaultAction();

        mIsCycleContinuePlay=true;
        isStopCycleThread=false;
        mCurrentPlayType = Play_type.cycle_action;
        thiz.mCurrentPlayState = Play_state.action_playing;

        if (mSourceActionNameList == null) {
            mSourceActionNameList = new ArrayList<String>();
        }
        mSourceActionNameList.clear();
        for (int i = 0; i < _action_name.length; i++) {
            mSourceActionNameList.add(_action_name[i]);
        }

        String[] mActionNameList = new String[thiz.mSourceActionNameList.size() * 2];
        for (int i = 0; i < thiz.mSourceActionNameList.size(); i++) {
            mActionNameList[i * 2] = mCurrentDefaultAction;
            mActionNameList[i * 2 + 1] = mSourceActionNameList.get(i);
        }

        UbtLog.d(TAG, "播放列表长度:mActionNameList-->" + mActionNameList.length);

        if (mActionNameList == null || mActionNameList.length < 1){
            return;
        }

        if (mCyclePlayThread != null && mCyclePlayThread.isAlive()) {

        }else {
            mCyclePlayThread = new CycleThread(mActionNameList);
        MyLog.writeLog("循环播放功能", "线程启动");
        mCyclePlayThread.start();
    }

        thiz.mCurrentPlayState = Play_state.action_playing;

        if (thiz.mListeners != null)
        {
            for (int i = 0; i < mListeners.size(); i++) {
                thiz.mListeners.get(i).notePlayStart(mSourceActionNameList, null, mCurrentPlayType);
            }
        }

    }

    /**
     * 无论是单独播放还是循环播放都调用这个函数
     * @param needSendComm
     * needSendComm=true, robot need reply the command
     * needSendComm=false robot donot reply the command
     */
    private void doStopAction(boolean needSendComm) {

        if (needSendComm) {
            UbtLog.d(TAG,"doStopAction : " + ConstValue.DV_STOPPLAY+" time is      :"+System.currentTimeMillis());
            time=System.currentTimeMillis();
            mBtManager.sendCommand(mBtMac, ConstValue.DV_STOPPLAY, null, 0, false);
        }
        if(thiz == null){
            return;
        }
        notePlayFinish();
        UbtLog.d(TAG, "doStopAction-->" + thiz.mCurrentPlayState + " to action_finish");
    }
/**
 *  shit code
 *  DOSTOPCYCLE
 */
    private void doStopCycle(boolean needSendCommand) {
        if(mCyclePlayThread == null){
            return;
        }
        if(thiz == null){
            return;
        }
        if(mHandler.hasMessages(UI_NOTE_PLAY_CYCLE_STOP)){
            mHandler.removeMessages(UI_NOTE_PLAY_CYCLE_STOP);
        }
        //退出循环
        mIsCycleContinuePlay = false;
        mCyclePlayThread.isShutDowm = true;
        //发送给机器人停止播放
        UbtLog.d(TAG,"doStopCycle "+needSendCommand);
        doStopAction(true);
        notifyUnlockCycle();
    }

    private void notePlayFinish() {
        UbtLog.d(TAG, "notePlayFinish!");
        if(thiz == null){
            return;
        }
        thiz.mCurrentPlayState = Play_state.action_finish;

        if (thiz.mListeners != null){
            for (int i = 0; i < mListeners.size(); i++) {
                //UbtLog.d(TAG, "mListeners" + "_" + i + ":" + thiz.mListeners.get(i).hashCode());
                thiz.mListeners.get(i).notePlayFinish(mSourceActionNameList,
                        mCurrentPlayType,
                        thiz.mListeners.get(i).hashCode() + "");
            }
        }

    }



    private void notifyUnlockCycle() {
        try {
            synchronized (mCyclePlaylock) {
                UbtLog.d(TAG, "---mCyclePlaylock.notify");
                mCyclePlaylock.notify();
            }
        } catch (Exception e) {

        }

    }

    public void doLostPower(String device_mac) {
        mBtManager.sendCommand(device_mac, ConstValue.DV_DIAODIAN, null, 0,false);
        if (thiz.mCurrentPlayType == Play_type.cycle_action) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ActionPlayer.this.doStopPlay();
                }
            }, 100);
        }
    }

    public void doPauseOrContinueAction() {

        if (thiz.mCurrentPlayState == Play_state.action_finish){
            return;
        }

        if (thiz.mCurrentPlayState == Play_state.action_playing) {
            byte[] papram = new byte[1];
            papram[0] = 0;
            mBtManager.sendCommand(mBtMac, ConstValue.DV_PAUSE, papram, papram.length, false);
            if (thiz.mListeners != null){
                for (int i = 0; i < mListeners.size(); i++) {
                    thiz.mListeners.get(i).notePlayPause(mSourceActionNameList,
                            mCurrentPlayType);
                }
            }

            UbtLog.d(TAG, "doPauseOrContinueAction-->" + thiz.mCurrentPlayState + " to action_pause");

            thiz.mCurrentPlayState = Play_state.action_pause;
        } else {
            byte[] papram = new byte[1];
            papram[0] = 1;
            mBtManager.sendCommand(mBtMac, ConstValue.DV_PAUSE, papram, papram.length, false);

            if (thiz.mListeners != null){
                for (int i = 0; i < mListeners.size(); i++) {
                    thiz.mListeners.get(i).notePlayContinue(
                            mSourceActionNameList, mCurrentPlayType);
                }
            }

            UbtLog.d(TAG, "doPauseOrContinueAction-->" + thiz.mCurrentPlayState + " to action_playing");
            thiz.mCurrentPlayState = Play_state.action_playing;
        }

        if(EventBus.getDefault().hasSubscriberForEvent(ActionEvent.class)){
            ActionEvent actionEvent = new ActionEvent(ActionEvent.Event.ACTION_PLAY_PAUSE);
            if(thiz.mCurrentPlayState == Play_state.action_playing){
                actionEvent.setStatus(1);
            }else {
                actionEvent.setStatus(0);
            }
            EventBus.getDefault().post(actionEvent);
        }
    }

    // ------------------------------------------------------------------------

    private void doPlay(String info) {
        mCurrentPlayActionName = info;
        UbtLog.d(TAG,"play action = " + info);
        mLastPlayTime = new Date(System.currentTimeMillis());
        notifyMainActivityEvent(info, ActionEvent.Event.ACTION_PLAY_START);
        byte[] actions = BluetoothParamUtil.stringToBytes(info);
        mBtManager.sendCommand(mBtMac, ConstValue.DV_PLAYACTION, actions,actions.length, false);
        mCurrentExecuteTime=System.currentTimeMillis();

    }

    // ------------------------------------------------------------------------

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {

        if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {

            if (param[0] == 0) {
                // 声音状态
                if (param[1] == 1) {
                    // 静音
                } else {
                    // 有声音
                }
            } else if (param[0] == 1) {
                // 播放状态

                if (param[1] == 0) {
                    // 暂停
                } else {
                    // 非暂停
                }
            } else if (param[0] == 2) {
                // 音量状态
                int nCurrentVolume = param[1];
                if (nCurrentVolume < 0) {
                    nCurrentVolume += 256;
                }
            } else if (param[0] == 3) {
                // 舵机灯状态

                if (param[1] == 0) {
                    // 灭
                } else {
                    // 亮
                }
            } else if (param[0] == 4) {

                if (param[1] == 0) {
                    // 拔出
                } else {
                    // 插入
                }
            }
        }else if(cmd==ConstValue.DV_TAP_HEAD||cmd==ConstValue.DV_VOICE_WAIT){
            UbtLog.d(TAG,"bt receive robot reply: DV_TAP_HEAD");
            //解决拍头循环播放还进入下一首的问题，直接停止
            stopPlayingAndClearPlayingList(true);
            notifyMainActivityEvent(mCurrentPlayActionName, ActionEvent.Event.ACTION_PLAY_FINISH);
        }else if (cmd==ConstValue.DV_6D_GESTURE){

        }else if (cmd == ConstValue.DV_ACTION_FINISH)// 动作播放完毕
        {
            String finishPlayActionName = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "bt receive robot reply: DV_ACTION_FINISH:   finishPlayActionName = " + finishPlayActionName + "    mCurrentPlayActionName = " + mCurrentPlayActionName);
            UbtLog.d(TAG, "bt receive robot reply: DV_ACTION_FINISH:   mCurrentPlayType = " + mCurrentPlayType);
            boolean isStopLocal = false;
            if(finishPlayActionName.contains(mCurrentPlayActionName)){
                isStopLocal = true;
            }
            if(!isStopLocal){
                //如果回复播放完成的动作名称与本地当前播放动作名称不一致，则不予处理
                return;
            }
            if (mCurrentPlayType == Play_type.cycle_action) {
                if (thiz != null) {
                    notifyUnlockCycle();
                }
            } else if (mCurrentPlayType == Play_type.single_action) {
                doStopCurrentPlay(true);
            }
            notifyMainActivityEvent(finishPlayActionName, ActionEvent.Event.ACTION_PLAY_FINISH);

        } else if (cmd == ConstValue.DV_PLAYACTION) {
            UbtLog.d(TAG, "bt receive robot reply: DV_PLAYACTION " + param[0] + "    = " + mCurrentPlayType);
            //TODO ???
//            if (param[0] == 2) {
//                if (mCurrentPlayType == Play_type.cycle_action) {
//                    thiz.doStopCycle(true);
//                }
//            }
        }
        // 如果禁止边充边玩
        else if (cmd == ConstValue.SET_PALYING_CHARGING) {
//            if (param[0] == 0 && thiz.mCurrentPlayState == Play_state.action_playing) {
//                if(mIsCycleContinuePlay){
//                    UbtLog.d(TAG,"SET_PALYING_CHARGING  ");
//                    mIsCycleContinuePlay = false;
//                }
//                if (thiz.mListeners != null){
//                    for (int i = 0; i < mListeners.size(); i++) {
//                        thiz.mListeners.get(i).notePlayChargingError();
//                    }
//                }
//            }
        }else if(cmd == ConstValue.DV_STOPPLAY){
            UbtLog.d(TAG,"bt receive robot reply:DV_STOPPLAY reply stop  time "+(System.currentTimeMillis()-time)+" mCurrentPlayType:  "+ mCurrentPlayType+"MyActionsHelper.mCurrentLocalPlayType: "+MyActionsHelper.mCurrentLocalPlayType);
//            if(System.currentTimeMillis()-mCurrentExecuteTime>1){
//                stopPlayingAndClearPlayingList(false);
//                notifyMainActivityEvent(mCurrentPlayActionName, ActionEvent.Event.ACTION_PLAY_FINISH);
//            }

        }else if(cmd == ConstValue.DV_CURRENT_PLAY_NAME){
            String robotCurrentPlayName = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "bt receive robot reply: DV_CURRENT_PLAY_NAME robotCurrentPlayName : " + robotCurrentPlayName + "    mCurrentPlayActionName = " + mCurrentPlayActionName);
        }
    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub
        try {
            UbtLog.d(TAG, "---onConnectState");
            stopPlayingAndClearPlayingList(true);
            MyActionsHelper.getInstance(AlphaApplication.getBaseActivity()).resetPlayer();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.d(TAG, "---mCyclePlaylock.onDeviceDisConnected");
             if(mHandler.hasMessages(UI_NOTE_PLAY_CYCLE_STOP)){
            mHandler.removeMessages(UI_NOTE_PLAY_CYCLE_STOP);
        }
        mIsCycleContinuePlay = false;
        mCurrentPlayType = null;
        doStopPlay();
        clearPlayingInfoList();
        mDatas.clear();
    }


    class CycleThread extends Thread {
        private String[] mActionNameList;
        public boolean isShutDowm = false;

        public CycleThread(String[] actions) {
            mActionNameList = actions;
        }

        public void run() {

            //最长播放时长
            mHandler.sendEmptyMessageDelayed(UI_NOTE_PLAY_CYCLE_STOP,AUTO_STOP_PLAY_CYCLE_TIME);
            int i = 0;
            UbtLog.d(TAG, "mCyclePlaylock BEFORE " + mActionNameList[i] );
            UbtLog.d(TAG, "mCyclePlaylock BEFORE isShutDowm：" + isShutDowm);
            UbtLog.d(TAG, "mCyclePlaylock BEFORE mIsCycleContinuePlay:   "+mIsCycleContinuePlay);
            UbtLog.d(TAG, "mCyclePlaylock BEFORE isStopCycleThread:  "+isStopCycleThread);
            while (mIsCycleContinuePlay && !isStopCycleThread ) {
                if (!isShutDowm && thiz.mCurrentPlayState == Play_state.action_playing) {
                    String action_name = mActionNameList[i];
                    String actionName = mActionNameList[i];
                    if(!mCurrentDefaultAction.equals(action_name)){
                        ActionInfo actionInfo = MyActionsHelper.mCurrentSeletedActionInfoMap.get(action_name);
                        if(actionInfo == null){
                            //线上友盟crash报null,但是找不到规律，判null处理
                            UbtLog.d(TAG,"mCyclePlaylock ACTIONINFO NULL");
                            continue;
                        }
                        actionName = actionInfo.actionName;
//                        //全局控制按钮消失DESTROY后，需要通过这个变量来获取正在播放的
                        AlphaApplication.getBaseActivity().saveCurrentPlayingActionName(actionInfo.actionName);
                        int pos = actionInfo.actionSize;
                        if(pos < MyActionsHelper.localSize){
                            action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
                        }else if(pos >= MyActionsHelper.localSize  &&  pos < (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                            action_name = FileTools.actions_download_robot_path + "/"+action_name+".hts";
                        }else if(pos >= (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                            action_name = FileTools.actions_creation_robot_path + "/"+actionInfo.hts_file_name+".hts";
                        }
                    }else {
                        //默认动作
                        action_name = FileTools.action_robot_file_path + "/"+action_name+".hts";
                    }

                    UbtLog.d(TAG,"mCyclePlaylock mCurrentPlayActionName = " + action_name);
                    doPlay(action_name);

                    Message msg = new Message();
                    msg.what = UI_NOTE_PLAY_CYCLE_NEXT;
                    //msg.obj = mActionNameList[i];
                    msg.obj = actionName;
                    mHandler.sendMessage(msg);

                }else {
                    UbtLog.d(TAG,"mCyclePlaylock not loop");
                }
                i++;
                if (i == mActionNameList.length){
                    i = 0;
                }
                synchronized (mCyclePlaylock) {
                    try {
                        UbtLog.d(TAG, "循环播放功能，等待解锁");
                        mCyclePlaylock.wait();
                        UbtLog.d(TAG, "循环播放功能，已经解锁");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                UbtLog.d(TAG,"CycleThread unlock   mCurrentPlayState "+thiz.mCurrentPlayState);
                UbtLog.d(TAG,"CycleThread unlock   mIsCycleContinuePlay "+mIsCycleContinuePlay );
                UbtLog.d(TAG,"CycleThread unlock   isStopCycleThread "+isStopCycleThread );

            }
            UbtLog.d(TAG,"CycleThread exit    "+thiz.mCurrentPlayState);
            if(mHandler.hasMessages(UI_NOTE_PLAY_CYCLE_STOP)){
                mHandler.removeMessages(UI_NOTE_PLAY_CYCLE_STOP);
            }
            mIsCycleContinuePlay = false;
            mCyclePlayThread.isShutDowm = true;
        }
    }

    public void doDefault() {
        doInitDefaultAction();
        ActionInfo info = new ActionInfo();
        info.actionName = mCurrentDefaultAction;
        doPlayAction(info);
    }

    /**
     * 初始化Default动作
     */
    private void doInitDefaultAction(){
        if (mRobotActions != null) {
            for (int i = 0; i < mRobotActions.size(); i++) {
                if (mRobotActions.get(i).equals("default")) {
                    mCurrentDefaultAction = "default";
                    break;
                }
                if (mRobotActions.get(i).equals("初始化")) {
                    mCurrentDefaultAction = "初始化";
                    break;
                }
            }
        }
    }

    public static boolean isStopCycleThread = false;
    public static void StopCycleThread(boolean stop){
        UbtLog.d(TAG, "StopCycleThread isStopCycleThread "+stop );
       // new Exception().printStackTrace();
        isStopCycleThread = stop;
    }

    public void clearPlayingInfoList(){
        MyActionsHelper.mCurrentSeletedNameList.clear();
        MyActionsHelper.mCurrentSeletedActionInfoMap.clear();
        AlphaApplication.getBaseActivity().saveCurrentPlayingActionName("");
        for (Map<String, Object> item : mDatas) {
            item.put(MyActionsHelper.map_val_action_is_playing, false);
            item.put(MyActionsHelper.map_val_action_selected, false);
        }
    }
    private void clearSinglePlayStatus(){
        for (Map<String, Object> item : mDatas) {
            item.put(MyActionsHelper.map_val_action_is_playing, false);
        }
        //mDatas.clear();
    }
    public void setPlayContent(List<Map<String,Object>> nameList){
        mDatas=nameList;
    }

    /**
     * 是否发送给机器人停止执行动作的命令
     * @param isSendStop
     */
    private void stopPlayingAndClearPlayingList(boolean isSendStop){
        clearPlayingInfoList();
        if (mCurrentPlayType == Play_type.cycle_action){
            StopCycleThread(true);
            mIsCycleContinuePlay = false;
            MyActionsHelper.setLooping(false);
            AlphaApplication.getBaseActivity().saveCurrentPlayingActionName("");
        }else if(mCurrentPlayType==Play_type.single_action){
            notePlayFinish();
        }
        if(isSendStop){
            doStopPlay();
        }
    }

}
