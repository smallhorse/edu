package com.ubt.alpha1e.edu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.AppManager;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.edu.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.data.DB.RemoteRecordOperater;
import com.ubt.alpha1e.edu.data.RemoteItem;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.edu.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.IRemoteUI;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.ui.helper.RemoteHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoteActivity extends BaseActivity implements IRemoteUI , BaseDiaUI {

    private static final String TAG = RemoteActivity.class.getSimpleName();

    public static final int UPLOADING_FILE = 101;
    public static final int SEND_FILE_FINISH = 102;
    private static final int ACTION_FILE_NOT_EXIST = 105;
    private static final int EXEC_STOP_ACTION = 106;

    private ImageButton btn_up;
    private ImageButton btn_left;
    private ImageButton btn_right;
    private ImageButton btn_down;
    private ImageButton btn_to_left;
    private ImageButton btn_to_right;
    private ImageButton btn_stop;

    private ImageButton btn_1;
    private ImageButton btn_2;
    private ImageButton btn_3;
    private ImageButton btn_4;
    private ImageButton btn_5;
    private ImageButton btn_6;

    private ImageView img_action_1;
    private ImageView img_action_2;
    private ImageView img_action_3;
    private ImageView img_action_4;
    private ImageView img_action_5;
    private ImageView img_action_6;

    private TextView titleView = null;

    private ImageView ivSetting;
    private LinearLayout llBack;

    private List<View> mButtons;
    private static Date lastTime_onNote = null;
    private RemoteRoleInfo mRemoteRoleInfo = null;
    public int playIndex = -1;

    private boolean keepExec = false;
    private View longClickItem = null;

    //private RemoteGuideView guideView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOADING_FILE:
                    ((LoadingDialog) mCoonLoadingDia).showMessage(getStringResources("ui_remote_select_robot_synchoronizing"));
                    break;
                case SEND_FILE_FINISH:
                    try {
                        mCoonLoadingDia.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(RemoteActivity.this, getStringResources("ui_remote_select_robot_synchoronize_success"),
//                            Toast.LENGTH_SHORT).show();
                    if((boolean)msg.obj){
                        RemoteItem item = RemoteRecordOperater.getItemByIndex(playIndex, RemoteHelper.mCurrentInfo);
                        ((RemoteHelper) mHelper).addActionName(item.hts_name.split("\\.")[0]);
                        ((RemoteHelper) mHelper).doAction(playIndex);
                    }else{
//                        Toast.makeText(RemoteActivity.this, getStringResources("ui_remote_select_robot_synchoronize_failed"),
//                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ACTION_FILE_NOT_EXIST:
                    /*Toast.makeText(RemoteActivity.this, getStringResources("ui_remote_action_not_exist"),
                            Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(RemoteActivity.this, getResources().getString(R.string.ui_remote_action_not_exist),
                            Toast.LENGTH_SHORT).show();
                    break;
                case EXEC_STOP_ACTION:
                    ((RemoteHelper) mHelper).doAction(-1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isPad = AlphaApplication.isPad();
        int layoutId = 0;
        UbtLog.d(TAG,"layoutId isPad == " + isPad);
        if (RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.FIGHTER) {
            if(isPad){
                layoutId = R.layout.activity_remote_football_1_pad;
            }else {
                //layoutId = R.layout.activity_remote_fighter;
                layoutId = R.layout.activity_remote_football_1;
            }
        } else if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.FOOTBALL_PLAYER){
            if(isPad){
                layoutId = R.layout.activity_remote_football_1_pad;
            }else {
                layoutId = R.layout.activity_remote_football_1;
            }
        }else{
            /*if(isPad){
                layoutId = R.layout.activity_remote_costom_pad;
            }else {
                layoutId = R.layout.activity_remote_costom;
            }*/
            layoutId = R.layout.activity_remote_football_1;
        }

        mRemoteRoleInfo = (RemoteRoleInfo)getIntent().getSerializableExtra(RemoteHelper.REMOTE_ROLE_INFO_PARAM);
        setContentView(layoutId);
        mButtons = new ArrayList<>();


        mHelper = new RemoteHelper(this, this);

        initUI();
        initControlListener();

        if(!isBulueToothConnected()){
            showBluetoothConnectDialog();
        }
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(RemoteActivity.class.getSimpleName());
//        mHelper = new RemoteHelper(this, this);
        //((RemoteHelper) mHelper).doReadActions();
        super.onResume();

        /*if(guideView == null && !RemoteGuideView.isShowGuide(RemoteActivity.this)){
             guideView = new RemoteGuideView(RemoteActivity.this);
        }*/

        if(isBulueToothConnected()){
            startOrStopRun((byte)0x05);

            UbtLog.d(TAG, "stopEventSound = ");
            byte[] mCmd = {0};
            mCmd[0] = 0;
            mHelper.doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_EXIT, mCmd);
        }


        if(mHelper.isStartHibitsProcess()){
            mHelper.showStartHibitsProcess(new IDismissCallbackListener() {
                @Override
                public void onDismissCallback(Object obj) {
                    UbtLog.d(TAG,"onDismissCallback = obj == " + obj);
                    if((boolean)obj){
                        //行为习惯流程未结束，退出当前流程
                        finish();
                    }
                }
            });
        }
    }

    /**
     * 进入或者退出遥控器
     */
    public void startOrStopRun(byte  params) {
        if ((RemoteHelper)mHelper != null) {
            ((RemoteHelper) mHelper).doRemoterState(params);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        /*if(guideView != null){
            guideView.closeGuideView();
            guideView = null;
        }*/
    }

    @Override
    protected void initUI() {

        mCoonLoadingDia = LoadingDialog.getInstance(this, this);

        btn_1 = (ImageButton) findViewById(R.id.btn_1);
        btn_2 = (ImageButton) findViewById(R.id.btn_2);
        btn_3 = (ImageButton) findViewById(R.id.btn_3);
        btn_4 = (ImageButton) findViewById(R.id.btn_4);
        btn_5 = (ImageButton) findViewById(R.id.btn_5);
        btn_6 = (ImageButton) findViewById(R.id.btn_6);

        UbtLog.d(TAG,"RemoteHelper.mCurrentType="+RemoteHelper.mCurrentType);
        if(RemoteHelper.mCurrentType != RemoteRecordOperater.ModelType.CUSTOM){
            //R.drawable.remoter_gohead_select
            btn_1.setBackgroundResource(((RemoteHelper) mHelper).getResId(RemoteHelper.mCurrentInfo.do_1.image_name));
            btn_2.setBackgroundResource(((RemoteHelper) mHelper).getResId(RemoteHelper.mCurrentInfo.do_2.image_name));
            btn_3.setBackgroundResource(((RemoteHelper) mHelper).getResId(RemoteHelper.mCurrentInfo.do_3.image_name));
            btn_4.setBackgroundResource(((RemoteHelper) mHelper).getResId(RemoteHelper.mCurrentInfo.do_4.image_name));
            btn_5.setBackgroundResource(((RemoteHelper) mHelper).getResId(RemoteHelper.mCurrentInfo.do_5.image_name));
            btn_6.setBackgroundResource(((RemoteHelper) mHelper).getResId(RemoteHelper.mCurrentInfo.do_6.image_name));

        }else{

            img_action_1 = (ImageView) findViewById(R.id.img_action_1);
            img_action_2 = (ImageView) findViewById(R.id.img_action_2);
            img_action_3 = (ImageView) findViewById(R.id.img_action_3);
            img_action_4 = (ImageView) findViewById(R.id.img_action_4);
            img_action_5 = (ImageView) findViewById(R.id.img_action_5);
            img_action_6 = (ImageView) findViewById(R.id.img_action_6);

            titleView = (TextView)findViewById(R.id.txt_model_name);
            titleView.setText(mRemoteRoleInfo.roleName);
            replaceImageView1To6(img_action_1,RemoteHelper.mCurrentInfo.do_1);
            replaceImageView1To6(img_action_2,RemoteHelper.mCurrentInfo.do_2);
            replaceImageView1To6(img_action_3,RemoteHelper.mCurrentInfo.do_3);
            replaceImageView1To6(img_action_4,RemoteHelper.mCurrentInfo.do_4);
            replaceImageView1To6(img_action_5,RemoteHelper.mCurrentInfo.do_5);
            replaceImageView1To6(img_action_6,RemoteHelper.mCurrentInfo.do_6);

        }

        btn_up = (ImageButton) findViewById(R.id.btn_up);
        btn_left = (ImageButton) findViewById(R.id.btn_left);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        btn_down = (ImageButton) findViewById(R.id.btn_down);
        btn_to_left = (ImageButton) findViewById(R.id.btn_to_left);
        btn_to_right = (ImageButton) findViewById(R.id.btn_to_right);
        btn_stop = (ImageButton) findViewById(R.id.btn_stop);

        mButtons.clear();

        mButtons.add(btn_up);
        mButtons.add(btn_left);
        mButtons.add(btn_right);
        mButtons.add(btn_down);
        mButtons.add(btn_to_left);
        mButtons.add(btn_to_right);
        if(RemoteHelper.mCurrentType != RemoteRecordOperater.ModelType.CUSTOM){
            mButtons.add(btn_1);
            mButtons.add(btn_2);
            mButtons.add(btn_3);
            mButtons.add(btn_4);
            mButtons.add(btn_5);
            mButtons.add(btn_6);
        }else{
            mButtons.add(img_action_1);
            mButtons.add(img_action_2);
            mButtons.add(img_action_3);
            mButtons.add(img_action_4);
            mButtons.add(img_action_5);
            mButtons.add(img_action_6);
        }

        ivSetting = (ImageView) findViewById(R.id.iv_title_right);
//        ivSetting.setBackgroundResource(R.drawable.icon_setting);
        ivSetting.setVisibility(View.INVISIBLE);
        llBack = (LinearLayout) findViewById(R.id.ll_base_back);

        TextView ivTitleName = (TextView) findViewById(R.id.tv_base_title_name);
        ivTitleName.setText(mRemoteRoleInfo.roleName);
        ivTitleName.setTextColor(getResources().getColor(R.color.white));
    }

    private void replaceImageView1To6(ImageView imageView, RemoteItem mRemoteItem){

        String image_name = mRemoteItem.image_name;
        if(mRemoteItem.show_name != null && mRemoteItem.show_name.length() > 0){

            Glide.with(this).load(image_name).centerCrop().placeholder(R.drawable.sec_action_logo).into(imageView);
        }
    }

    private View.OnTouchListener viewOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(!isBulueToothConnected()){
                    showBluetoothConnectDialog();
                }

//                if(view.getId() == R.id.btn_up
//                        || view.getId() == R.id.btn_down
//                        || view.getId() == R.id.btn_left
//                        || view.getId() == R.id.btn_right){
//
//                    if(view.getId() == R.id.btn_up){
//                        ((RemoteHelper) mHelper).doWalkAction(0,1,0);
//                    } else if(view.getId() == R.id.btn_left){
//                        ((RemoteHelper) mHelper).doWalkAction(2,1,0);
//                    } else if(view.getId() == R.id.btn_right){
//                        ((RemoteHelper) mHelper).doWalkAction(3,1,0);
//                    } else if(view.getId() == R.id.btn_down){
//                        ((RemoteHelper) mHelper).doWalkAction(1,1,0);
//                    }
//                }else {
//                    longClickItem = view;
//                    execActions(longClickItem);
//                    keepExec = true;
//
//                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                if(!isBulueToothConnected()){
                    return false;
                }

                //松开stop
//                if(view.getId() == R.id.btn_up
//                        || view.getId() == R.id.btn_down
//                        || view.getId() == R.id.btn_left
//                        || view.getId() == R.id.btn_right){
//
//                    ((RemoteHelper) mHelper).doStopWalkAction();
//                }else {
                if(!keepExec){
                    return false;
                }
//
                    keepExec = false;
                    longClickItem = null;
                    handler.sendEmptyMessageDelayed(EXEC_STOP_ACTION,200);
//                }
                lastClickTime = System.currentTimeMillis();
            }
            return false;
        }
    };



    private View.OnLongClickListener viewOnLongTouchListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            if(!isBulueToothConnected()){
                return false;
            }

            longClickItem = view;
            execActions(longClickItem);
            keepExec = true;
            return false;
        }
    };

//             if(event.getAction() == MotionEvent.ACTION_DOWN){
//        handler.removeCallbacks(stopRunnable);
//        if(System.currentTimeMillis()-lastClickTime < 800){
//            UbtLog.d(TAG,"800ms才能点击");
//            longClickItem = view;
//            handler.postDelayed(actiondownRunnable,800);
//        }else {
//            longClickItem = view;
//            execActions(longClickItem);
//            keepExec = true;
//        }
//    }else if(event.getAction() == MotionEvent.ACTION_UP){
//        handler.removeCallbacks(actiondownRunnable);
//        handler.removeCallbacks(stopRunnable);
//        keepExec = false;
////                longClickItem = null;
//        //松开stop
////                handler.sendEmptyMessageDelayed(EXEC_STOP_ACTION,200);
//        handler.postDelayed(stopRunnable,200);
//        lastClickTime = System.currentTimeMillis();
//    }
//            return false;

//    Runnable actiondownRunnable = new Runnable() {
//        @Override
//        public void run() {
//            execActions(longClickItem);
//            keepExec = true;
//        }
//    };
//
//    Runnable stopRunnable = new Runnable() {
//        @Override
//        public void run() {
//            ((RemoteHelper) mHelper).doAction(-1);
//        }
//    };

    long lastClickTime = System.currentTimeMillis();
    private View.OnClickListener controlListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
//            arg0.setClickable(false);
            if(System.currentTimeMillis()-lastClickTime < 1000){
                UbtLog.d(TAG,"1000ms才能点击");
                return;
            }

            if(!isBulueToothConnected()){
                showBluetoothConnectDialog();
                return;
            }

            lastClickTime = System.currentTimeMillis();
            execActions(arg0);
        }
    };

    private void execActions(View view){

        playIndex = -1;
        for (int i = 0; i < mButtons.size(); i++) {
            if (mButtons.get(i).getId() == view.getId()) {
                playIndex = i + 1;
                break;
            }
        }

        if(RemoteHelper.mCurrentType != RemoteRecordOperater.ModelType.CUSTOM){
            UbtLog.d(TAG,"lihai------playIndex:" + playIndex);
            ((RemoteHelper) mHelper).doAction(playIndex);
        }else{
            String show_name = "";
            if(playIndex == 7){
                show_name = RemoteHelper.mCurrentInfo.do_1.show_name;
            }else if(playIndex == 8){
                show_name = RemoteHelper.mCurrentInfo.do_2.show_name;
            }else if(playIndex == 9){
                show_name = RemoteHelper.mCurrentInfo.do_3.show_name;
            }else if(playIndex == 10){
                show_name = RemoteHelper.mCurrentInfo.do_4.show_name;
            }else if(playIndex == 11){
                show_name = RemoteHelper.mCurrentInfo.do_5.show_name;
            }else if(playIndex == 12){
                show_name = RemoteHelper.mCurrentInfo.do_6.show_name;
            }
            if(playIndex >= 7){
                if(show_name == null || show_name.length() == 0){
                    return;
                }
            }
            ((RemoteHelper) mHelper).doCustomAction(playIndex,mRemoteRoleInfo.roleid);

        }
    }

    @Override
    protected void initControlListener() {

        //方向键可以长按一直执行
        btn_up.setOnTouchListener(viewOnTouchListener);
        btn_up.setOnLongClickListener(viewOnLongTouchListener);
        btn_left.setOnTouchListener(viewOnTouchListener);
        btn_left.setOnLongClickListener(viewOnLongTouchListener);
        btn_right.setOnTouchListener(viewOnTouchListener);
        btn_right.setOnLongClickListener(viewOnLongTouchListener);
        btn_down.setOnTouchListener(viewOnTouchListener);
        btn_down.setOnLongClickListener(viewOnLongTouchListener);

        btn_to_left.setOnTouchListener(viewOnTouchListener);
        btn_to_left.setOnLongClickListener(viewOnLongTouchListener);
        btn_to_right.setOnTouchListener(viewOnTouchListener);

        btn_to_right.setOnLongClickListener(viewOnLongTouchListener);

        btn_stop.setOnClickListener(controlListener);
        if(RemoteHelper.mCurrentType != RemoteRecordOperater.ModelType.CUSTOM){

            btn_1.setOnClickListener(controlListener);
            btn_2.setOnClickListener(controlListener);
            btn_3.setOnClickListener(controlListener);
            btn_4.setOnClickListener(controlListener);
            btn_5.setOnClickListener(controlListener);
            btn_6.setOnClickListener(controlListener);
        }else{

            img_action_1.setOnClickListener(controlListener);
            img_action_2.setOnClickListener(controlListener);
            img_action_3.setOnClickListener(controlListener);
            img_action_4.setOnClickListener(controlListener);
            img_action_5.setOnClickListener(controlListener);
            img_action_6.setOnClickListener(controlListener);
        }

        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent inte = new Intent();
                if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
                    inte.putExtra("roleId",mRemoteRoleInfo.roleid+"");
                }
                inte.setClass(RemoteActivity.this, RemoteSetActivity.class);
                RemoteActivity.this.startActivity(inte);*/
            }
        });
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBulueToothConnected()){
                    startOrStopRun((byte)0x06);
                    handler.sendEmptyMessage(EXEC_STOP_ACTION);
                }
                RemoteActivity.this.finish();
            }
        });
    }

    @Override
    public void onLostBtCoon() {
        if(handler.hasMessages(EXEC_STOP_ACTION)){
            handler.removeMessages(EXEC_STOP_ACTION);
        }
        super.onLostBtCoon();
    }

    boolean showDialog = false;
    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS){
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            if(event.isHibitsProcessStatus() && !showDialog){
                showDialog = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new ConfirmDialog(RemoteActivity.this).builder()
                                .setTitle("提示")
                                .setMsg(getStringResources("ui_habits_process_start"))
                                .setCancelable(false)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UbtLog.d(TAG, "确定");
                                        showDialog = false;
                                        finish();
                                    }
                                }).show();
                    }
                },10);
                //行为习惯流程未结束，退出当前流程
            }
        }
    }


    @Override
    protected void onDestroy() {
        MyActionsHelper.mCurrentLocalPlayType = null ;
        if(mCoonLoadingDia!=null)
        {
            if(mCoonLoadingDia.isShowing()&&!isFinishing()){
                mCoonLoadingDia.cancel();
            }
            mCoonLoadingDia = null;
        }

        if(handler.hasMessages(EXEC_STOP_ACTION)){
            handler.removeMessages(EXEC_STOP_ACTION);
        }
//        startOrStopRun((byte)0x06);
        super.onDestroy();
    }
    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onReadActionsFinish(List<String> mActionsNames) {

    }

    @Override
    public void noteTFPulled() {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()) {
            try {
                mCoonLoadingDia.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!isFinishing()){
            this.showToast("ui_remote_synchoronize_no_sd");
            this.finish();
        }
    }

    @Override
    public void onSendFileFinish(boolean isSuccess) {

        Message msg = new Message();
        msg.what = SEND_FILE_FINISH;
        msg.obj = isSuccess;
        handler.sendMessage(msg);
    }

    @Override
    public void onPlayActionFileNotExist() {
        handler.sendEmptyMessage(ACTION_FILE_NOT_EXIST);
    }

    @Override
    public void onSendFileStart() {
        handler.sendEmptyMessage(UPLOADING_FILE);
    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG,"--notePlayFinish--");
        if(keepExec){
            execActions(longClickItem);
        }
    }

    @Override
    public void notePlayChargingError() {

        //防止连续提醒---------------start
        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 500;
        if (lastTime_onNote != null) {
            time_difference = curDate.getTime() - lastTime_onNote.getTime();
        }
        lastTime_onNote = curDate;
        if (time_difference < 500) {
            return;
        }
        //防止连续提醒---------------end
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                RemoteActivity.this.showToast("ui_settings_play_during_charging_tips");
            }
        });
    }

    @Override
    public void notePlayCycleNext(String action_name) {

    }

    @Override
    public void onReadSettingItem(List<RemoteItem> items) {

    }

    @Override
    public void onReadRemoteRoleFinish(List<RemoteRoleInfo> mRemoteRoles) {

    }

    @Override
    public void onAddRemoteRole(boolean isSuccess,int roleId) {

    }

    @Override
    public void onUpdateRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onDelRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onAddRemoteRoleActions(boolean isSuccess,int roleId) {

    }

    @Override
    public void onDelRemoteHeadPrompt(boolean isSuccess) {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isBulueToothConnected()){
                startOrStopRun((byte)0x06);
                handler.sendEmptyMessage(EXEC_STOP_ACTION);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //显示蓝牙连接对话框
    void showBluetoothConnectDialog() {
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接蓝牙")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        gotoConnectBluetooth();
                    }
                }).show();
    }

    //去连接蓝牙
    void gotoConnectBluetooth() {
        boolean isfirst = SPUtils.getInstance().getBoolean("firstBluetoothConnect", true);
        Intent bluetoothConnectIntent = new Intent();
        if (isfirst) {
            UbtLog.d("MainCourse", "第一次蓝牙连接");
            SPUtils.getInstance().put("firstBluetoothConnect", false);
            bluetoothConnectIntent.setClass(RemoteActivity.this, BluetoothguidestartrobotActivity.class);
        } else {
            UbtLog.d("MainCourse", "非第一次蓝牙连接 ");
            bluetoothConnectIntent.setClass(RemoteActivity.this, BluetoothandnetconnectstateActivity.class);
        }

        startActivityForResult(bluetoothConnectIntent, 100);
        this.overridePendingTransition(R.anim.activity_open_up_down, 0);
    }
}
