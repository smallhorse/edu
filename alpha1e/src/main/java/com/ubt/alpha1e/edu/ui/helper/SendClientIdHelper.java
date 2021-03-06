package com.ubt.alpha1e.edu.ui.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.base.AppManager;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.login.LoginActivity;
import com.ubt.alpha1e.edu.login.LoginManger;
import com.ubt.alpha1e.edu.services.AutoScanConnectService;
import com.ubt.alpha1e.edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.BlueToothManager;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.sqlite.DBAlphaInfoManager;

import org.greenrobot.eventbus.EventBus;


/**
 * 发送clientId
 */

public class SendClientIdHelper extends BaseHelper {

    private static final String TAG = "SendClientIdHelper";
    Context mContext = null ;

    private int clientIdSendWhich = 0 ; //clientId发送到哪一段
    String clientid[] = null;
    public SendClientIdHelper(Context context) {
        super(context);
        mContext = context ;
        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() == null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .setBlueToothManager(new BlueToothManager(mContext));

            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().startProcess();
        }

        if (((AlphaApplication) mContext.getApplicationContext())
                .getDBAlphaInfoManager() == null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .setDBAlphaInfoManager(new DBAlphaInfoManager(mContext));
        }

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }

    @Override
    public void DistoryHelper() {

    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if(cmd == ConstValue.DV_PRODUCT_AND_DSN){
            UbtLog.d(TAG,"cmd = " + cmd + "    获取到 product 和 dsn  ");
            String  productAndDsn = new String(param);
            UbtLog.d(TAG,"productAndDsn = " + productAndDsn);
            String [] ss = productAndDsn.split(",");
            if(productAndDsn != null && ss.length >= 2){
                UbtLog.d(TAG,"product ID=   "+ss[0]);
                UbtLog.d(TAG,"dsn =  "+ss[1]);
                SPUtils.getInstance().put(Constant.SP_ROBOT_PRODUCT_ID,ss[0]);
                SPUtils.getInstance().put(Constant.SP_ROBOT_DSN,ss[1]);
                AlphaApplication.currentRobotSN = ss[1];
            }else {
                return;
            }
            UbtLog.d(TAG,"再发送clientId 给机器人  ");
            LoginManger.getInstance().init((Activity) mContext,null);
            LoginManger.getInstance().refreshLoginToken(ss[0],ss[1],onRefreshListener);

        }else if(cmd == ConstValue.DV_CLIENT_ID){
            UbtLog.d(TAG,"cmd = " + cmd + "    发送clientId" +(clientIdSendWhich+1)+" 段成功 ");
            if(clientid != null && clientIdSendWhich == clientid.length ){
                UbtLog.d(TAG, "    发送clientId 完成 ");
                clientIdSendWhich = 0;
                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.BLUETOOTH_SEND_CLIENTID_SUCCESS);
                EventBus.getDefault().post(robotEvent);
                return;
            }
            clientIdSendWhich ++ ;
            UbtLog.d(TAG,"cmd = " + cmd + "    发送下一段clientId  命令 ");
            try {
                if(clientid != null && clientIdSendWhich == clientid.length){
                    doSendComm(ConstValue.DV_CLIENT_ID, BluetoothParamUtil.stringToBytes("end:"+clientid[clientIdSendWhich-1]));
                }else {
                    doSendComm(ConstValue.DV_CLIENT_ID, BluetoothParamUtil.stringToBytes("start:"+clientid[clientIdSendWhich-1]));
                }
                UbtLog.d(TAG,"发送 clientid  "+clientIdSendWhich+":"+clientid[clientIdSendWhich-1]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(cmd == ConstValue.READ_SN_CODE){
            UbtLog.d(TAG,"cmd  = " + cmd + "    获取到序列号 1: "+new String(param));
            String sn = new String(param, 1, param.length - 1);
            UbtLog.d(TAG,"cmd = " + cmd + "    获取到序列号 2: "+sn);
            if(sn == null || sn.equals("")){
                UbtLog.d(TAG,"序列号为空 " );
                return;
            }
            AlphaApplication.currentRobotSN = sn ;
            RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.BLUETOOTH_GET_ROBOT_SN_SUCCESSS);
            EventBus.getDefault().post(robotEvent);
            return;
        }else if(cmd == ConstValue.DV_DO_UPGRADE_SOFT){
            UbtLog.d(TAG,"接收到机器人是否升级命令 " );
            UbtLog.d(TAG,"升级命令长度: "+ param.length);
            if(param.length == 0){
                return;
            }
            UbtLog.d(TAG,"param0: "+ param[0]);

            if(param[0] == 1){
                UbtLog.d(TAG,"升级命令 " );
                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.BLUETOOTH_GET_ROBOT_UPGRADE);
                EventBus.getDefault().post(robotEvent);
            }else if(param[0] == 0) {
                UbtLog.d(TAG,"开始升级回复命令" );
            }
        }
    }

    //获取productId 和 DSN
    public void send() {
        UbtLog.d(TAG,"发送 给mac：  ");
        clientIdSendWhich = 0;
        clientid = null ;
        doSendComm(ConstValue.DV_PRODUCT_AND_DSN, null);

    }

    //升级命令
    public void startUpgrade(int type) {
        UbtLog.d(TAG,"发送 startUpgrade  ");
        if(type == 2){
            doSendComm(ConstValue.DV_DO_UPGRADE_SOFT, new byte[] { 2 });
        }else if(type == 3){
            doSendComm(ConstValue.DV_DO_UPGRADE_SOFT, new byte[] { 3 });
        }
    }

    //获取机器人序列号
    public void sendCmdReadSN() {
        UbtLog.d(TAG,"获取机器人序列号  ");
        doSendComm(ConstValue.READ_SN_CODE, new byte[] { 0 });

    }


    LoginManger.OnRefreshListener onRefreshListener = new LoginManger.OnRefreshListener() {
        @Override
        public void onSuccess() {
            UbtLog.d(TAG,"onRefreshListener onSuccess  ");
            String params = LoginManger.getInstance().getClientId();
            UbtLog.d(TAG,"params :  "+params);
            if(params.equals("")){
                UbtLog.d(TAG,"params 为空   ");
                return;
            }
            int clientidNum = params.length()/200 ;//clientId分段发送
            if(params.length()%200 >0){
                clientidNum ++ ;
            }
            clientid = new String[clientidNum] ;
            for(int i =0;i<clientidNum;i++){
                if(i+1 == clientidNum){
                    clientid[i] = params.substring(i*200,params.length());
                }else {
                    clientid[i] = params.substring(i*200,(i+1)*200);
                }
                UbtLog.d(TAG,"clientid  "+i+":"+clientid[i]);
            }

            clientIdSendWhich = 1 ;
            if(clientid.length == 1){
                doSendComm(ConstValue.DV_CLIENT_ID, BluetoothParamUtil.stringToBytes("end:"+clientid[0]));
            }else {
                doSendComm(ConstValue.DV_CLIENT_ID, BluetoothParamUtil.stringToBytes("start:"+clientid[0]));
            }
            UbtLog.d(TAG,"发送clientid 0  "+":"+clientid[0]);
        }

        @Override
        public void onError(int i) {
            UbtLog.d(TAG,"onRefreshListener onError  ");
            clientIdSendWhich = 0 ;
            if(i == Constant.INVALID_TOKEN){
                ((AlphaApplication) mContext.getApplicationContext()).doLostConnect();
                AutoScanConnectService.doStopSelf();
                ((AlphaApplication) mContext.getApplicationContext()).setmCurrentNetworkInfo(null);
                SPUtils.getInstance().remove(Constant.SP_USER_INFO);
                SPUtils.getInstance().remove(Constant.SP_USER_ID);
                SPUtils.getInstance().remove(Constant.SP_USER_IMAGE);
                SPUtils.getInstance().remove(Constant.SP_USER_NICKNAME);
                SPUtils.getInstance().remove(Constant.SP_LOGIN_TOKEN);
                LoginManger.getInstance().loginOut();
                AppManager.getInstance().finishAllActivity();
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(LoginActivity.INVALID_TOKEN, true);
                mContext.startActivity(intent);
            }
        }
    };

}
