package com.ubt.alpha1e.edu.behaviorhabits;

import android.content.Context;

import com.ubt.alpha1e.edu.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.edu.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.edu.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.edu.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BehaviorHabitsContract {
    public interface View extends BaseView {
        void onTest(boolean isSuccess);
        //显示行为列表以及分数
        void showBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg);
        //显示家长管理模式列表
        void showParentBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg);
        //显示编辑的EventID具体内容
        void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg);
        //显示行为习惯播放内容
        void showBehaviourPlayContent(boolean status, ArrayList<PlayContentInfo> playList, String errorMsg);
        void onUserPassword(String password);
        //提醒对话框点击
        void onAlertSelectItem(int index,String language,int alertType);
        //网络请求错误回调  requestType 网络请求的标识，errorCode 错误码
        void onRequestStatus(int requestType, int errorCode);
    }

    public interface  Presenter extends BasePresenter<View> {
        void doTest();
        //获得行为习惯列表以及分数
        void getBehaviourList(String  sex, String grade);
        //家长模式下，编辑行为列表，type=1 workday type=2 holiday
        void getParentBehaviourList(String sex,String grade, String type);
        //获得行为习惯某个EventId具体详情
        void getBehaviourEvent(String eventId);
        //获得行为习惯的播放内容
        void getBehaviourPlayContent(String eventId);
        //开启/关闭行为习惯具体某个EventId状态，status=0 关闭，status=1 打开
        void enableBehaviourEvent(String eventId, int status);
        //保存编辑的行为习惯 workday=1; holiday=2
        void saveBehaviourEvent(EventDetail content,int dayType);
        //显示提醒时间对话框
        void showAlertDialog(Context context, int currentPosition, List<String> alertList, int alertType);
        //延时提醒  delayTime: -1：关闭；0：准时；5：延迟5分钟；10：延迟10分钟
        void delayBehaviourEventAlert(String eventId, String delayTime);
        //从服务端获取密码
        void getUserPassword();
        //设置密码
        void doSetUserPassword(String password);
    }
}
