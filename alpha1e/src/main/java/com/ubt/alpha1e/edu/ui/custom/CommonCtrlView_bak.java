package com.ubt.alpha1e.edu.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.business.NewActionPlayer;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.MyActionsActivity;
import com.ubt.alpha1e.edu.ui.helper.BaseHelper;
import com.ubt.alpha1e.edu.ui.helper.IActionsUI;
import com.ubt.alpha1e.edu.ui.helper.IMainUI;
import com.ubt.alpha1e.edu.ui.helper.MainHelper;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static android.app.Service.START_NOT_STICKY;

/**
 *
 *ControlCenterActivity
 * @author wmma
 * @description 全局浮动控制窗口
 * @date 2016/10/25
 */


public class CommonCtrlView_bak implements IActionsUI, IMainUI {

    private static final String TAG = "ControlCenterActivity";
    public final static String KEY_CURRENT_PLAYING_ACTION_NAME = "currentPlayingActionName";
    //定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private LinearLayout  mPopWindowLayout;
    private RelativeLayout guideLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private int paddingBottomHeight ; //定义浮动按钮距离页面底部的高度

    private ImageView ivPop;
    private LinearLayout lay_ctrl, lay_ctrl_more;
    private Button btn_stop, btn_reset, btn_more;  //lay_ctrl btn
    private Button btn_cycle, btn_reset_m, btn_pause_or_continue, btn_lose_power, btn_stop_m, btn_vol_log, btn_lig_logo;
    private TextView txt_action_name, txt_action_name_m, txt_cycle_num;
    private SeekBar sek_vol_ctrl;
    private RelativeLayout rl_close_more, rl_control;
    private GifImageView gifImageView;
    private LinearLayout dialogLayout;
    private TextView tvTips;
    private Button btnCancle, btnOk;

    //control
    public MyActionsHelper mHelper;
    private BaseActivity mBaseActivity;
    private MyActionsHelper.Action_type mCurrentActionType = MyActionsHelper.Action_type.Base_type;
    private boolean is_change_vol = false;

    private MainHelper mMainHelper;

    private TextView tvFloatTips;
    private boolean isShowDialog = false;
    private Context mContext;
    private static CommonCtrlView_bak commonCtrlView = null;
    private Date lastTime_doPauseOrContinuePlay = null;

    private String playingName = "";
    private ActionPlayer.Play_state currentState = ActionPlayer.Play_state.action_finish;
    private NewActionPlayer.PlayerState currentNewPlayState = NewActionPlayer.PlayerState.STOPING;
    private static final int CLOSE_VIEW = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CLOSE_VIEW:
                    //Handler.post 会有延时，所以此处再判断一次是否为null
                    if(commonCtrlView != null){
                        commonCtrlView.onDestroy();
                        commonCtrlView = null;
                    }
                    break;
                default:
                    break;
            }

        }
    };

    public static CommonCtrlView_bak getInstace(Context context){
        if(commonCtrlView != null){
            commonCtrlView.onDestroy();
            commonCtrlView = null;
        }
        commonCtrlView = new CommonCtrlView_bak(context);
        return commonCtrlView;
    }

    public static void closeCommonCtrlView(){
        if(commonCtrlView != null){
            //蓝牙断开的时候，为非主线程调用关闭
            //android低版本的时候，直接调关闭会报,硬件加速只能在单个UI线程中使用,所以需放大handler主线线程中调用关闭
            //Hardware acceleration can only be used with a single UI thread
            commonCtrlView.mHandler.sendEmptyMessage(CLOSE_VIEW);
        }
    }

    public CommonCtrlView_bak(Context context) {
        Log.d(TAG, "Float View  Created!");
        mContext = context;
        initHelper();
        createFloatView();
        //Alpha 1E from Briann
        mWindowManager.removeView(mFloatLayout);
        rl_control.setVisibility(View.GONE);
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lay_ctrl_more.setVisibility(View.VISIBLE);
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    private void initHelper() {
        Log.d(TAG, "--wmma--init MyActionHelper!");
        mBaseActivity = AlphaApplication.getBaseActivity();
        mHelper = MyActionsHelper.getInstance(mBaseActivity);
        mHelper.registerListeners(this);

        mMainHelper = new MainHelper(mBaseActivity);
        mMainHelper.doRegisterListenerUI(this);
        mHelper.RegisterHelper();

        playingName = mBaseActivity.readCurrentPlayingActionName();
        currentState = mHelper.getPlayerState();
        currentNewPlayState = mHelper.getNewPlayerState();
        UbtLog.d(TAG, "currentState=" + currentState);

    }

    private void createFloatView() {

        Log.d(TAG, "createFloatView!");

        wmParams = new WindowManager.LayoutParams();
        //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final float scale = mBaseActivity.getResources().getDisplayMetrics().density;
        paddingBottomHeight = (int)(50 * scale + 0.5f);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
//        wmParams.x = 16;
        wmParams.y = paddingBottomHeight;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.view_float_control, null);
        mPopWindowLayout = (LinearLayout) inflater.inflate(R.layout.layout_ctrl_more_ft, null);
        dialogLayout = (LinearLayout) inflater.inflate(R.layout.view_alertdialog, null);
        guideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_float_guid, null);

        initView(mFloatLayout);
        initRobotState();

        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "---wmma---Float view onTouched!");
                return false;
            }
        });

        //添加mFloatLayout
        if(!readShowState().equals("4")){
            ColorDrawable colorDrawable = new ColorDrawable(Color.argb(150, 0, 0, 0));
            guideLayout.setBackground(colorDrawable);
            wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            wmParams.y = 0;

            tvFloatTips =  (TextView)guideLayout.findViewById(R.id.tv_guide_controls);
            tvFloatTips.setText(AlphaApplication.getBaseActivity().getStringResources("ui_introduction_play_control"));

            mWindowManager.addView(guideLayout, wmParams);
        }else{
            mWindowManager.addView(mFloatLayout, wmParams);
        }
    }

    /**
     * 初始化控件和设置点击事件
     * @param view
     */
    private void initView(View view) {
        ivPop = (ImageView) view.findViewById(R.id.iv_pop);
        lay_ctrl_more = (LinearLayout) view.findViewById(R.id.lay_ctrl_more);
        rl_control = (RelativeLayout) view.findViewById(R.id.rl_control);
        //gifImageView = (GifImageView) view.findViewById(R.id.gif_playing_control);

        //init hide view
        btn_cycle = (Button) view.findViewById(R.id.btn_cycle);
        btn_reset_m = (Button) view.findViewById(R.id.btn_do_default_m);
        btn_lose_power = (Button) view.findViewById(R.id.btn_lost_power_m);
        btn_pause_or_continue = (Button) view.findViewById(R.id.btn_pause_or_continue_m);
        btn_stop_m = (Button) view.findViewById(R.id.btn_stop_m);
        btn_vol_log = (Button) view.findViewById(R.id.btn_vol_logo);
        btn_lig_logo = (Button) view.findViewById(R.id.btn_lig_logo);
        sek_vol_ctrl = (SeekBar) view.findViewById(R.id.skb_vol_control);
        rl_close_more = (RelativeLayout) view.findViewById(R.id.lay_ctrl_more_close);

        txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
        txt_action_name_m = (TextView) view.findViewById(R.id.txt_action_name_m);
        txt_cycle_num = (TextView) view.findViewById(R.id.txt_cycle_num);

        UbtLog.d(TAG, "playingName=" + playingName);
        if(playingName.equals("NO_VALUE")){
            playingName = "" ;
        }

        if((currentState == ActionPlayer.Play_state.action_playing || currentNewPlayState == NewActionPlayer.PlayerState.PLAYING) && playingName != ""){
            btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_pause_icon_ft"));
            gifImageView.setVisibility(View.VISIBLE);
            txt_action_name_m.setText(playingName);
        }else if((currentState == ActionPlayer.Play_state.action_pause || currentNewPlayState == NewActionPlayer.PlayerState.PAUSING) && playingName != ""){
            txt_action_name_m.setText(playingName);
        }else{
            gifImageView.setVisibility(View.INVISIBLE);
            txt_action_name_m.setText("");
        }

        guideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordShowGuide("4");
                showFloatView();
            }
        });

        rl_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "iv pop onclick!");
                mWindowManager.removeView(mFloatLayout);
                rl_control.setVisibility(View.GONE);
                wmParams.y = 0;
                wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                lay_ctrl_more.setVisibility(View.VISIBLE);
                mWindowManager.addView(mFloatLayout, wmParams);
            }
        });

        btn_cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BaseHelper.hasSdcard){
                    Toast.makeText(mBaseActivity,mBaseActivity.getStringResources("ui_remote_synchoronize_no_sd"),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(AlphaApplication.isCycleActionFragment()){
                    return;
                }
                mWindowManager.removeView(mFloatLayout);
                lay_ctrl_more.setVisibility(View.GONE);
                rl_control.setVisibility(View.VISIBLE);
                wmParams.y = paddingBottomHeight;
                wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                mWindowManager.addView(mFloatLayout, wmParams);
                MyActionsActivity.launchActivity(mBaseActivity, 4);
//                ((MyActionsActivity)AlphaApplication.getBaseActivity()).startCycleActionFragment();
            }
        });

        btn_reset_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "--wmma--current Action Type=" + AlphaApplication.getActionType());
                //检测是否在充电状态和边充边玩状态是否打开
//                if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
//                    Toast.makeText(mContext, mContext.getResources().getString(R.string.ui_settings_play_during_charging_tips), Toast.LENGTH_SHORT).show();
//                    return;
//                }

                mHelper.doActionCommand(
                        MyActionsHelper.Command_type.Do_default, "", AlphaApplication.getActionType());
                txt_action_name_m.setText("");
                mBaseActivity.saveCurrentPlayingActionName("");
                btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_play_icon_ft"));
                gifImageView.setVisibility(View.INVISIBLE);

            }
        });

        btn_lose_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                lay_ctrl_more.setVisibility(View.GONE);

                initDialogView(dialogLayout);
                wmParams.gravity = Gravity.CENTER;
                wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                ColorDrawable colorDrawable = new ColorDrawable(Color.argb(120, 0, 0, 0));
                dialogLayout.setBackground(colorDrawable);
                mWindowManager.addView(dialogLayout, wmParams);
                isShowDialog = true;

            }
        });

        btn_pause_or_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        // 防止连续响应-----------start
                        Date curDate = new Date(System.currentTimeMillis());
                        float time_difference = 500;
                        if (lastTime_doPauseOrContinuePlay != null) {
                            time_difference = curDate.getTime()
                                    - lastTime_doPauseOrContinuePlay.getTime();
                        }
                        if (time_difference < 500) {
                             return;
                        }
                        lastTime_doPauseOrContinuePlay = curDate;
                        // 防止连续响应-----------end

                        mHelper.doActionCommand(
                        MyActionsHelper.Command_type.Do_pause_or_continue, "",
                        mCurrentActionType);
            }
        });

        btn_stop_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.stopPlayAction();
            }
        });

        btn_lig_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.doTurnLight();
                if(mHelper.mLightState){
                    noteLightOn();
                }else {
                    noteLightOff();
                }
            }
        });

        btn_vol_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.doTurnVol();
                onNoteVolState(mHelper.mCurrentVoiceState);
            }
        });

        sek_vol_ctrl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                if(!mHelper.mCurrentVoiceState){
                    //静音先解静音
                    mHelper.doTurnVol();
                    onNoteVolState(mHelper.mCurrentVoiceState);
                }
                //修改为，移动停之后，再发送改变音量
                mHelper.doChangeVol(arg0.getProgress());
                is_change_vol = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                is_change_vol = true;
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1,boolean arg2) {

                //mHelper.doChangeVol(arg1);
            }
        });

        rl_close_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mWindowManager.removeView(mFloatLayout);
                lay_ctrl_more.setVisibility(View.GONE);
//                rl_control.setVisibility(View.VISIBLE);
//                wmParams.y = paddingBottomHeight;
//                wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
//                mWindowManager.addView(mFloatLayout, wmParams);


            }
        });


    }

    /**
     * 初始化机器人状态
     */
    private void initRobotState(){
        UbtLog.d(TAG,"initRobotState mCurrentVolume = " + mHelper.mCurrentVolume + "   mCurrentVoiceState " + mHelper.mCurrentVoiceState + "   mLightState = " + mHelper.mLightState);
        onNoteVol(mHelper.mCurrentVolume);
        onNoteVolState(mHelper.mCurrentVoiceState);
        if(mHelper.mLightState){
            noteLightOn();
        }else {
            noteLightOff();
        }
    }

    private void initDialogView(View view) {
        tvTips = (TextView) view.findViewById(R.id.txt_msg);
        btnCancle = (Button) view.findViewById(R.id.btn_neg);
        btnOk = (Button) view.findViewById(R.id.btn_pos);

        tvTips.setText(AlphaApplication.getBaseActivity().getStringResources("ui_action_cutoff_warning"));
        btnCancle.setText(AlphaApplication.getBaseActivity().getStringResources("ui_common_cancel"));
        btnOk.setText(AlphaApplication.getBaseActivity().getStringResources("ui_common_confirm"));

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mWindowManager.removeView(dialogLayout);
                lay_ctrl_more.setVisibility(View.VISIBLE);
                isShowDialog = false;
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.doActionCommand(
                        MyActionsHelper.Command_type.Do_lost_power, "",
                        mCurrentActionType);
                mWindowManager.removeView(dialogLayout);
                isShowDialog = false;
            }
        });

    }


    public void onDestroy() {
//
//        if(readShowState().equals("4")){
//            if (mFloatLayout != null) {
//                mWindowManager.removeView(mFloatLayout);
//            }
//        }else{
//            if(guideLayout != null ){
//                mWindowManager.removeView(guideLayout);
//            }
//        }
//
//        if(dialogLayout !=null && isShowDialog){
//            mWindowManager.removeView(dialogLayout);
//        }

        mHelper.unRegisterListeners(this);
        mHelper.UnRegisterHelper();
        mMainHelper.doUnRegisterListenerUI(this);
    }


    @Override
    public void onNoteNoUser() {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionsFinish(List<String> names) {

    }

    @Override
    public void onNoteVol(int vol_pow) {
        if (!is_change_vol) {
            int value = vol_pow;
            value *= 100;
            int add_val = 0;
            if (value % 255 >= 127.5) {
                add_val = 1;
            }
            value /= 255;
            value += add_val;
            sek_vol_ctrl.setProgress(value);
        }
    }

    @Override
    public void onNoteVolState(boolean vol_state) {
        if (vol_state) {
            if (mHelper.mCurrentVolume < 0) {
                mHelper.mCurrentVolume *= -1;
                mHelper.doChangeVol(mHelper.mCurrentVolume);
            }
            onNoteVol(mHelper.mCurrentVolume);
            btn_vol_log.setBackground(mBaseActivity.getDrawableRes("action_control_sound_icon_ft"));
        } else {
            if (sek_vol_ctrl.getProgress() != 0){
                mHelper.mCurrentVolume = -1 * sek_vol_ctrl.getProgress();
            }
            sek_vol_ctrl.setProgress(0);
            btn_vol_log.setBackground(mBaseActivity.getDrawableRes("action_control_silence_icon_ft"));
            mHelper.ChangeMisucVol(0);
        }
    }

    @Override
    public void onReadHeadImgFinish(boolean b, Bitmap obj) {

    }

    @Override
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

    }

    @Override
    public void onSendFileStart() {

    }

    @Override
    public void onSendFileBusy() {

    }

    @Override
    public void onSendFileError() {

    }

    @Override
    public void onSendFileFinish(int pos) {

    }

    @Override
    public void onSendFileCancel() {

    }

    @Override
    public void onSendFileUpdateProgress(String progress) {

    }

    @Override
    public void noteCharging() {

    }

    @Override
    public void updateBattery(int power) {

    }

    @Override
    public void noteDiscoonected() {
//        this.stopSelf();
        UbtLog.d(TAG, "---noteDiscoonected");
        closeCommonCtrlView();
    }

    @Override
    public void noteLightOn() {
        btn_lig_logo.setBackground(mBaseActivity.getDrawableRes("action_control_light_sel_icon_ft"));
    }

    @Override
    public void noteLightOff() {
        btn_lig_logo.setBackground(mBaseActivity.getDrawableRes("action_control_light_icon_ft"));
    }

    @Override
    public void noteChangeFinish(boolean b, String string) {

    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    public void syncServerDataEnd(List<Map<String, Object>> data) {

    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "--wmma--notePlayStart callback!");
        btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_pause_icon_ft"));
        gifImageView.setVisibility(View.VISIBLE);
        if (action != null) {
            String name = action.actionName;
            if (!name.equals("")
                    && "#@%".contains(name.toCharArray()[0] + "")) {
                name = name.substring(1);
            }
            txt_action_name_m.setText(name);
            mBaseActivity.saveCurrentPlayingActionName(name);
        }
    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "--wmma--notePlayPause callback!");
        if (mHelper.getCurrentPlayType() == MyActionsHelper.Action_type.My_download || mHelper.getCurrentPlayType() == MyActionsHelper.Action_type.My_new) {
            mHelper.doPauseMp3ForMyDownload();
        }
        if (mCurrentPlayType == ActionPlayer.Play_type.cycle_action) {
            btn_cycle.setBackground(mBaseActivity.getDrawableRes("action_control_cycle_icon_ft"));
            txt_cycle_num.setVisibility(View.GONE);
        }
        btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_play_icon_ft"));
        gifImageView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "--wmma--notePlayContinue callback!");
        if (mHelper.getCurrentPlayType() == MyActionsHelper.Action_type.My_download ||mHelper.getCurrentPlayType() == MyActionsHelper.Action_type.My_new) {
            mHelper.doPauseMp3ForMyDownload();
        }
        btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_pause_icon_ft"));
        gifImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG, "--wmma--notePlayFinish!");
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mHelper.doStopMp3ForMyDownload();
                txt_action_name_m.setText("");
                mBaseActivity.saveCurrentPlayingActionName("");
                btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_play_icon_ft"));
                gifImageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onPlaying() {
        UbtLog.d(TAG, "--wmma--onPlaying!");
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_pause_icon_ft"));

                String name = ((MyActionsHelper) mHelper).getNewPlayerName();
                mBaseActivity.saveCurrentPlayingActionName(name);
                txt_action_name.setText(name);
                txt_action_name_m.setText(name);
                gifImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPausePlay() {
        UbtLog.d(TAG, "--wmma--onPausePlay!");
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_play_icon_ft"));
                gifImageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "--wmma--onFinishPlay!-");
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_action_name_m.setText("");
                btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_play_icon_ft"));
                gifImageView.setVisibility(View.INVISIBLE);
                mBaseActivity.saveCurrentPlayingActionName("");
            }
        });
    }

    @Override
    public void onFrameDo(int index) {

    }

    @Override
    public void notePlayChargingError() {
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mBaseActivity,
                        mBaseActivity.getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void notePlayCycleNext(String action_name) {
        UbtLog.d(TAG, "--wmma--notePlayCycleNext!");
        if (action_name != null && !action_name.equals("default")) {
            if (!action_name.equals("")
                    && "#@%".contains(action_name.toCharArray()[0] + "")) {
                action_name = action_name.substring(1);
            }
            txt_action_name_m.setText(action_name);
            mBaseActivity.saveCurrentPlayingActionName(action_name);
            btn_pause_or_continue.setBackground(mBaseActivity.getDrawableRes("action_control_pause_icon_ft"));
            gifImageView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onReadCollocationRecordFinish(boolean isSuccess, String errorInfo, List<ActionColloInfo> history) {

    }

    @Override
    public void onDelRecordFinish() {

    }

    @Override
    public void onRecordFinish(long action_id) {

    }

    @Override
    public void onCollocateFinish(long action_id, boolean isSuccess, String error) {

    }

    @Override
    public void onCollocateRmoveFinish(boolean b) {

    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {

    }

    @Override
    public void onReportProgress(long request_code, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClearCache() {

    }


    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    public void resetFloatView(){
        UbtLog.d(TAG, "----resetFloatView！");
        mWindowManager.removeView(mFloatLayout);
        lay_ctrl_more.setVisibility(View.GONE);
        rl_control.setVisibility(View.VISIBLE);
        wmParams.y = paddingBottomHeight;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    private void showFloatView() {
        mWindowManager.removeView(guideLayout);
        wmParams.y = paddingBottomHeight;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mWindowManager.addView(mFloatLayout, wmParams);
    }


    private void recordShowGuide(String step) {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_HAS_FLOAT_SHOW,
                step, null, -1);
    }

    private String readShowState() {
        return BasicSharedPreferencesOperator.getInstance(mBaseActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_HAS_FLOAT_SHOW);
    }





}


