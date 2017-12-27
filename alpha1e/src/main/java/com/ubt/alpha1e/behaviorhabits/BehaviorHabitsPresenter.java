package com.ubt.alpha1e.behaviorhabits;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourControlRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourDelayAlertRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourEventRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourListRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourSaveUpdateRequest;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.weigan.loopview.LoopView;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BehaviorHabitsPresenter extends BasePresenterImpl<BehaviorHabitsContract.View> implements BehaviorHabitsContract.Presenter{

    private static final String TAG = BehaviorHabitsPresenter.class.getSimpleName();

    private static final int GET_BEHAVIOURLIST_CMD = 1;
    private static final int GET_BEHAVIOUREVENT_CMD=2;
    private static final int GET_BEHAVIOURPLAYCONTENT_CMD=3;
    private static final int GET_BEHAVIOURCONTROL_CMD=4;
    private static final int GET_BEHAVIOURSAVEUPDATE_CMD=5;
    private static final int GET_BEHAVIOURDELAYALERT_CMD=6;
    private static final int GET_BEHAVIOURPARENTEVENTLIST_CMD=7;
    private static final int NETWORK_ERROR=1000;
    private static final int NETWORK_SUCCESS=2000;
    String url = "http://10.10.1.14:8090";
    private String GetTemplatePath="/alpha1e/event/getEventList";
    private String GetEventPath="/alpha1e/event/getUserEvent";
    private String SaveModifyEventPath="/alpha1e/event/updateUserEvent";
    private String DelayAlert="/alpha1e/event/remindReply";
    private String ControlEventPath="/alpha1e/event/updateUserEvent"; //the same SaveModifyEventPath
    private String GetPlayContentPath="/behaviour/template/getEventContent";

    @Override
    public void doTest() {
        mView.onTest(true);
    }

    @Override
    public void getBehaviourList(String sex, String grade) {
        BehaviourListRequest mBehaviourListRequest = new BehaviourListRequest();
        mBehaviourListRequest.setSex(sex);
        mBehaviourListRequest.setGrade(grade);
        doRequestFromWeb(url+GetTemplatePath, mBehaviourListRequest, GET_BEHAVIOURLIST_CMD);
    }

    @Override
    public void getParentBehaviourList(String sex, String grade, String type) {
        BehaviourListRequest mBehaviourListRequest = new BehaviourListRequest();
        mBehaviourListRequest.setSex(sex);
        mBehaviourListRequest.setGrade(grade);
        mBehaviourListRequest.setType(type);
        doRequestFromWeb(url+GetTemplatePath, mBehaviourListRequest, GET_BEHAVIOURPARENTEVENTLIST_CMD);
    }

    @Override
    public void getBehaviourEvent(String eventId) {
        BehaviourEventRequest mBehaviourEventRequest=new BehaviourEventRequest();
        mBehaviourEventRequest.setEventId(eventId);
        doRequestFromWeb(url+GetEventPath, mBehaviourEventRequest, GET_BEHAVIOUREVENT_CMD);
    }

    @Override
    public void getBehaviourPlayContent(String sex, String grade) {
        BehaviourListRequest mBehaviourListRequest = new BehaviourListRequest();
        mBehaviourListRequest.setSex(sex);
        mBehaviourListRequest.setGrade(grade);
        doRequestFromWeb(url+GetPlayContentPath, mBehaviourListRequest, GET_BEHAVIOURPLAYCONTENT_CMD);
    }

    @Override
    public void enableBehaviourEvent(String eventId, int status) {
        BehaviourControlRequest mBehaviourControlRequest=new BehaviourControlRequest();
        mBehaviourControlRequest.setEventId(eventId);
        mBehaviourControlRequest.setStatus(new Integer(status).toString());
        doRequestFromWeb(url+ControlEventPath, mBehaviourControlRequest,GET_BEHAVIOURCONTROL_CMD);
    }

    @Override
    public void saveBehaviourEvent(EventDetail content, int dayType) {
        BehaviourSaveUpdateRequest mBehaviourSaveUpdateRequest=new BehaviourSaveUpdateRequest();
        mBehaviourSaveUpdateRequest.setEventId(content.eventId);
        mBehaviourSaveUpdateRequest.setEventTime(content.eventTime);
        mBehaviourSaveUpdateRequest.setRemindFirst(content.remindFirst);
        mBehaviourSaveUpdateRequest.setRemindSecond(content.remindSecond);
        mBehaviourSaveUpdateRequest.setContentIds(content.contentIds);
        mBehaviourSaveUpdateRequest.setStatus(content.status);
        mBehaviourSaveUpdateRequest.setType(String.valueOf(dayType));
        doRequestFromWeb(url+SaveModifyEventPath, mBehaviourSaveUpdateRequest,GET_BEHAVIOURSAVEUPDATE_CMD);
    }

    @Override
    public void delayBehaviourEventAlert(String eventId, String delayTime) {
        BehaviourDelayAlertRequest  mBehaviourDelayAlertRequest=new BehaviourDelayAlertRequest();
        mBehaviourDelayAlertRequest.setEventId(eventId);
        mBehaviourDelayAlertRequest.setDelayTime(delayTime);
        doRequestFromWeb(url+DelayAlert, mBehaviourDelayAlertRequest,GET_BEHAVIOURDELAYALERT_CMD);
    }

    @Override
    public void showAlertDialog(Context context, int currentPosition, final List<String> alertList, final int alertType) {
        List<String> alertShowList = new ArrayList<>();
        for(String alert : alertList){
            alertShowList.add(alert + ((MVPBaseActivity) mView.getContext()).getStringResources("ui_habits_minute_later"));
        }

        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_useredit_wheel, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.loopView);
        TextView textView = contentView.findViewById(R.id.tv_wheel_name);
        textView.setVisibility(View.GONE);
        DialogPlus.newDialog(context)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_sure) {
                            if (isAttachView()) {
                                mView.onAlertSelectItem(loopView.getSelectedItem(),alertList.get(loopView.getSelectedItem()),alertType);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
        // 设置原始数据
        loopView.setItems(alertShowList);
        loopView.setInitPosition(0);
        UbtLog.d("currentPosition","currentPosition => " + currentPosition);
        if(currentPosition < 0){
            loopView.setCurrentPosition(0);
        }else {
            loopView.setCurrentPosition(currentPosition);
        }
    }



    /**
     * 请求网络操作
     */
    public void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage());
                switch (id){
                    case GET_BEHAVIOURLIST_CMD:
                       // mView.showBehaviourList(false,null,"network error");
                        mView.onRequestStatus(GET_BEHAVIOURLIST_CMD,NETWORK_ERROR);
                        break;
                    case GET_BEHAVIOUREVENT_CMD:
                       // mView.showBehaviourEventContent(false,null,"network error");
                        mView.onRequestStatus(GET_BEHAVIOUREVENT_CMD,NETWORK_ERROR);
                        break;
                    case GET_BEHAVIOURCONTROL_CMD:
                        mView.onRequestStatus(GET_BEHAVIOURCONTROL_CMD,NETWORK_ERROR);
                        break;
                    case GET_BEHAVIOURSAVEUPDATE_CMD:
                        mView.onRequestStatus(GET_BEHAVIOURSAVEUPDATE_CMD,NETWORK_ERROR);
                        break;
                    case GET_BEHAVIOURDELAYALERT_CMD:
                        mView.onRequestStatus(GET_BEHAVIOURDELAYALERT_CMD,NETWORK_ERROR);
                        break;
                    default:
                        mView.onRequestStatus(id,NETWORK_ERROR);
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                switch (id) {
                    case GET_BEHAVIOURLIST_CMD:
                        BaseResponseModel<UserScore<List<HabitsEvent>>> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<UserScore<List<HabitsEvent>>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if(!baseResponseModel.status){
                             return;
                        }
                        UbtLog.d(TAG, "baseResponseModel = " + baseResponseModel.models);
                        UbtLog.d(TAG, "baseResponseModel percent = " + ((UserScore) baseResponseModel.models).percent);
                        UbtLog.d(TAG, "baseResponseModel details = " + ((List<HabitsEvent>) (((UserScore) baseResponseModel.models).details)));
                        mView.showBehaviourList(true,((UserScore) baseResponseModel.models),"success");
                        break;
                    case GET_BEHAVIOUREVENT_CMD:
                        BaseResponseModel<EventDetail<List<PlayContentInfo>>>baseResponseModel1=GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<EventDetail<List<PlayContentInfo>>>>(){
                        }.getType());
                        if(!baseResponseModel1.status){
                            return;
                        }
                        UbtLog.d(TAG, "GET_BEHAVIOUREVENT_CMD baseResponseModel = " + baseResponseModel1.models.contents.get(0).contentName);
                        mView.showBehaviourEventContent(true,(EventDetail)baseResponseModel1.models,"success");
                        break;
                    case GET_BEHAVIOURCONTROL_CMD:
                        mView.onRequestStatus(GET_BEHAVIOURCONTROL_CMD,NETWORK_SUCCESS);
                        break;
                    case GET_BEHAVIOURSAVEUPDATE_CMD:
                        break;
                    case GET_BEHAVIOURDELAYALERT_CMD:
                        mView.onRequestStatus(GET_BEHAVIOURDELAYALERT_CMD,NETWORK_SUCCESS);
                        break;
                    case GET_BEHAVIOURPARENTEVENTLIST_CMD:
                        BaseResponseModel<UserScore<List<HabitsEvent>>> baseResponseModel2 = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<UserScore<List<HabitsEvent>>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if(!baseResponseModel2.status){
                               return;
                        }
                        UbtLog.d(TAG, "parent baseResponseModel = " + baseResponseModel2.models);
                        UbtLog.d(TAG, "parent baseResponseModel percent = " + ((UserScore) baseResponseModel2.models).percent);
                        UbtLog.d(TAG, "parent baseResponseModel details = " + ((List<HabitsEvent>) (((UserScore) baseResponseModel2.models).details)));
                        mView.showParentBehaviourList(true,((UserScore) baseResponseModel2.models),"success");
                        break;
                    default:
                        break;
                }
            }
        });

    }
}
