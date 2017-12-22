package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.CheckIsBindRequest;
import com.ubt.alpha1e.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.RobotBindingDialog;
import com.ubt.alpha1e.ui.dialog.UnbindConfirmDialog;
import com.ubt.alpha1e.ui.dialog.alertview.RobotBindDialog;
import com.ubt.alpha1e.ui.helper.SendClientIdHelper;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import okhttp3.Call;

/**
 * 发送clientId服务
 */

public class SendClientIdService extends Service {

	private static final String TAG = "SendClientIdService";
	private static final int CONNECT_WIFI = 4;
	private static final int CHECK_IS_BIND = 5;
	private static final int ROBOT_GOTO_BIND = 6;

	private static SendClientIdService instance = null;

	static SendClientIdHelper sendClientIdHelper = null ;

	static boolean isSendClientId = false ;

	static long lastSendTime = System.currentTimeMillis();

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case CONNECT_WIFI:
					UbtLog.d(TAG, "-CONNECT_WIFI-");
					checkIsBind();
					break;

				default:
					break;
			}
		}
	};

    static Context mContext = null ;

	/**
	 * 启动服务
	 * @param context
     */
	public static void startService(Context context){

		if(instance == null){
            mContext = context;
			Intent mIntent = new Intent(context,SendClientIdService.class);
			context.startService(mIntent);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initHelper();
		registerBroadcastReceive();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		UbtLog.d(TAG, "-onStartCommand-");
		instance = this;
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		return START_NOT_STICKY;
	}

	private static void initHelper() {
		sendClientIdHelper = new SendClientIdHelper(mContext);
		sendClientIdHelper.RegisterHelper();
	}

	@Subscribe
	public void onEventRobot(RobotEvent event) {

		if(event.getEvent() == RobotEvent.Event.BLUETOOTH_SEND_CLIENTID_SUCCESS){
			UbtLog.d(TAG, "-发送clientId成功-");
			isSendClientId = true ;
			sendClientIdHelper.sendCmdReadSN();
		}else if(event.getEvent() == RobotEvent.Event.BLUETOOTH_GET_ROBOT_SN_SUCCESSS){
			UbtLog.d(TAG, "-获取到sn成功-");
			if(SendClientIdService.this != null && ((AlphaApplication) SendClientIdService.this.getApplication()).getmCurrentNetworkInfo() != null){
				com.ubt.alpha1e.data.model.NetworkInfo networkInfo = ((AlphaApplication) SendClientIdService.this.getApplication()).getmCurrentNetworkInfo();
				UbtLog.d(TAG,"机器人网络为：  "+ networkInfo.name);
				gotoCheckIsBind();
			}else {
				UbtLog.d(TAG,"机器人网络为null  ");
			}
		}

	}


	void gotoCheckIsBind(){
		mHandler.sendEmptyMessage(CONNECT_WIFI);
	}


	void checkIsBind(){
		String token = SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN, "");
		String userId = SPUtils.getInstance().getString(Constant.SP_USER_ID, "");
		UbtLog.d(TAG,"token:  "+token+"   userId:"+userId);

		CheckIsBindRequest checkIsBindRequest = new CheckIsBindRequest();
		checkIsBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
		checkIsBindRequest.setSystemType("3");

		String url = HttpEntity.CHECK_IS_BIND;
		doRequestBind(url,checkIsBindRequest,CHECK_IS_BIND);

	}


	/**
	 * 网络请求
	 */
	public void doRequestBind(String url, BaseRequest baseRequest, int requestId) {

		OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				UbtLog.d(TAG, "doRequestCheckIsBind onError:" + e.getMessage());
				switch (id){
					case CHECK_IS_BIND:
						break;
					case ROBOT_GOTO_BIND:
						if(robotBindingDialog != null && robotBindingDialog.isShowing()){
							robotBindingDialog.display();
						}
						adviceBindFail();
						break;
					default:
						break;
				}
			}

			@Override
			public void onResponse(String response, int id) {
				UbtLog.d(TAG,"doRequestCheckIsBind response = " + response);
//				BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());
				BaseResponseModel<String> baseResponseModel = GsonImpl.get().toObject(response,
						new TypeToken<BaseResponseModel<String>>(){}.getType());
				switch (id){
					case CHECK_IS_BIND:
						UbtLog.d(TAG, "status:" + baseResponseModel.status);
						if(!baseResponseModel.status){
							return;
						}
						UbtLog.d(TAG, "info:" + baseResponseModel.info);
						UbtLog.d(TAG, "models:" + baseResponseModel.models.toString());
						String state = baseResponseModel.models;
						if(state == null){
							return;
						}
						if(state.equals("1003")){
							adviceBind();
						}else if(state.equals("1002")){
							adviceRobotBinded();
						}else if(state.equals("1001")){
							adviceBindedOtherRobot();
						}else if(state.equals("1000")){

						}
					break;
					case ROBOT_GOTO_BIND:
						if(robotBindingDialog != null && robotBindingDialog.isShowing()){
							robotBindingDialog.display();
						}
						UbtLog.d(TAG, "status:" + baseResponseModel.status);
						UbtLog.d(TAG, "info:" + baseResponseModel.info);
						if(baseResponseModel.status){
							UbtLog.d(TAG, "绑定成功" );
							adviceBindSuccess();
						}else {
							adviceBindFail();
							UbtLog.d(TAG, "绑定失败" );
						}
						break;

					default:
						break;
				}
			}
		});

	}


	/**
	 *  注册WIFI网络状态改变广播
	 */
	public void registerBroadcastReceive() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mBroadCastReceiver, mIntentFilter);
	}

	private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//connected and disconnected
				UbtLog.d(TAG, "接收到网络改变");
				wifiConnectState(intent);
			}
		}
	};

	/**
	 *  WIFI网络状态改变处理
	 */
	protected void wifiConnectState(Intent intent) {
		Parcelable parcelableExtra = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (null != parcelableExtra) {
			NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
			NetworkInfo.State state = networkInfo.getState();
			boolean isConnected = state == NetworkInfo.State.CONNECTED;
			if (isConnected) {
				UbtLog.d(TAG, "-网络已经连接--");
				if (((AlphaApplication) mContext.getApplicationContext())
						.getCurrentBluetooth() == null) {
					UbtLog.d(TAG, "-蓝牙未连上--");
					return ;
				}

				if(!isSendClientId && System.currentTimeMillis() - lastSendTime >60000){
					try {
						Thread.sleep(5000);
						send();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				UbtLog.d(TAG, "-网络已经断开--");
			}
		}
	}


	@Override
	public void onDestroy() {
		UbtLog.d(TAG, "-onDestroy--");
		super.onDestroy();
		if(mContext != null){
			mContext.unregisterReceiver(mBroadCastReceiver);
		}
	}


	public static void send(){
		if(sendClientIdHelper!= null){
			UbtLog.d(TAG, "-发送clientId--");
			isSendClientId = false ;
			lastSendTime = System.currentTimeMillis();
			sendClientIdHelper.send();
		}
	}

	/**
	 * 停止服务
	 */
	public static void doStopSelf(){
		if(instance != null){
			instance.stopSelf();
		}
	}

	RobotBindingDialog robotBindingDialog = null ;
	//一键绑定
	public void gotoBind(){

		if(robotBindingDialog == null){
			robotBindingDialog = new RobotBindingDialog(AppManager.getInstance().currentActivity())
					.builder()
					.setCancelable(true);
		}
		robotBindingDialog.show();
		GotoBindRequest gotoBindRequest = new GotoBindRequest();
		gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
		gotoBindRequest.setSystemType("3");

		String url = HttpEntity.ROBOT_BIND;
		doRequestBind(url,gotoBindRequest,ROBOT_GOTO_BIND);

	}


	//提示去绑定
	public void adviceBind(){
		new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
				.setTitle("请将账户与机器人绑定以开通功能")
				.setMsg("1、“行为习惯养成”功能 \n2、控制机器人版本升级")
				.setCancelable(true)
				.setPositiveButton("一键绑定", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						UbtLog.d(TAG, "一键绑定 ");
						gotoBind();
					}
				})
				.setNegativeButton("暂不", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						UbtLog.d(TAG, "暂不 ");
					}
				}).show();
	}

	//该机器人已被其他账号绑定部分功能不可用
	public void adviceRobotBinded(){
			new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
			.setTitle("提示")
			.setMsg("该机器人已被其他账号绑定部分功能不可用！")
			.setCancelable(true)
			.setPositiveButton("我知道了", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UbtLog.d(TAG, "我知道了 ");
				}
			})
			.show();
	}

	//是否要解绑之前的机器人，绑定当前机器人
	public void adviceBindedOtherRobot(){
		new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
		.setTitle("你的账户连接了一台新机器人")
		.setMsg("是否要解绑之前的机器人，绑定当前机器人")
		.setCancelable(true)
		.setPositiveButton("更换绑定", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UbtLog.d(TAG, "更换绑定 ");
				gotoBind();
			}
		})
		.setNegativeButton("暂不", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UbtLog.d(TAG, "暂不 ");
			}
		}).show();
	}

	//如要使用此功能，需先绑定机器人
	public void adviceGoBindForHabit(){
		new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
				.setTitle("提示")
				.setMsg("如要使用此功能，需先绑定机器人")
				.setCancelable(true)
				.setPositiveButton("一键绑定", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						UbtLog.d(TAG, "一键绑定 ");
					}
				})
				.setNegativeButton("暂不", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						UbtLog.d(TAG, "暂不 ");
					}
				}).show();
	}

	//绑定成功！
	public void adviceBindSuccess(){
		Drawable img_ok;
		Resources res1 = getResources();
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
	public void adviceBindFail(){
			Drawable img_off;
			Resources res2 = getResources();
			img_off = res2.getDrawable(R.drawable.ic_bind_fail);
			new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
					.setTitle("绑定失败！")
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

	//解绑机器人后，将无法使用
	public void adviceUnBind(){
			new UnbindConfirmDialog(AppManager.getInstance().currentActivity()).builder()
			.setTitle("解绑机器人后，将无法使用：")
			.setUnbindMsg("1、“行为习惯养成”功能\n" +
					"2、控制机器人邦本升级\n"+"  确定要解绑么？")
			.setCancelable(true)
			.setPositiveButton("解绑", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UbtLog.d(TAG, "解绑 ");
				}
			})
			.setNegativeButton("取消", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UbtLog.d(TAG, "取消 ");
				}
			})
			.show();
	}

}
