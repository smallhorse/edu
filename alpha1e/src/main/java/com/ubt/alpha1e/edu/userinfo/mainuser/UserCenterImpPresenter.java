package com.ubt.alpha1e.edu.userinfo.mainuser;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.ResourceManager;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.userinfo.dingdang.DingDangFragment;
import com.ubt.alpha1e.edu.userinfo.dynamicaction.DynamicActionFragment;
import com.ubt.alpha1e.edu.userinfo.notice.NoticeFragment;
import com.ubt.alpha1e.edu.userinfo.notice.NoticeFragment1;
import com.ubt.alpha1e.edu.userinfo.setting.SettingFragment;
import com.ubt.alpha1e.edu.userinfo.usermanager.UserInfoFragment;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author：liuhai
 * @date：2017/10/27 11:44
 * @modifier：ubt
 * @modify_date：2017/10/27 11:44
 * [A brief description]
 * version
 */

public class UserCenterImpPresenter extends BasePresenterImpl<UserCenterContact.UserCenterView> implements UserCenterContact.UserCenterPresenter {

    @Override
    public void initData(Context context) {
        if (isAttachView()) {
            List<LeftMenuModel> leftMenuModels = new ArrayList<>();
            LeftMenuModel menuModel0 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_info"));
            menuModel0.setImageId(R.drawable.radio_selector_main_left_info);
            leftMenuModels.add(menuModel0);
            LeftMenuModel menuModel1 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_achievement"));
            menuModel1.setImageId(R.drawable.radio_selector_main_left_achievement);
            leftMenuModels.add(menuModel1);
            LeftMenuModel menuModel2 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_message"));
            menuModel2.setImageId(R.drawable.radio_selector_main_left_message);
            leftMenuModels.add(menuModel2);
//            LeftMenuModel menuModel3 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_dynamic"));
//            menuModel3.setImageId(R.drawable.radio_selector_main_left_dynaic);
//            leftMenuModels.add(menuModel3);
            LeftMenuModel menuModel4 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_original"));
            menuModel4.setImageId(R.drawable.radio_selector_main_left_create);
            leftMenuModels.add(menuModel4);
            LeftMenuModel menuModel5 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_download"));
            menuModel5.setImageId(R.drawable.radio_selector_main_left_download);
            leftMenuModels.add(menuModel5);
            LeftMenuModel menuModel7 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_dingdang"));
            menuModel7.setImageId(R.drawable.radio_selector_main_left_dingdang);
            leftMenuModels.add(menuModel7);
            LeftMenuModel menuModel6 = new LeftMenuModel(ResourceManager.getInstance(context).getStringResources("user_center_setting"));
            menuModel6.setImageId(R.drawable.radio_selector_main_left_setting);
            leftMenuModels.add(menuModel6);

            List<Fragment> fragmentList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    fragmentList.add(UserInfoFragment.newInstance(leftMenuModels.get(i).getNameString(), ""));
                } else if (i == 1) {
                    fragmentList.add(NoticeFragment1.newInstance("1", ""));
                } else if (i == 2) {
                    fragmentList.add(NoticeFragment.newInstance("2", ""));
                } else if (i == 3) {
                    fragmentList.add(DynamicActionFragment.newInstance(0, ""));
                } else if (i == 4) {
                    //fragmentList.add(NoticeFragment1.newInstance("5", ""));
                    fragmentList.add(DynamicActionFragment.newInstance(1, ""));
                } else if (i == 5) {
                    Fragment fragment = DingDangFragment.newInstance("", "");
                    fragmentList.add(fragment);
                }else if (i == 6) {
                    Fragment fragment = SettingFragment.newInstance(leftMenuModels.get(i).getNameString(), "");
                    fragmentList.add(fragment);
                }
            }

            mView.loadData(leftMenuModels, fragmentList);
        }
    }

    /**
     * 更新消息状态
     */
    @Override
    public void getUnReadMessage() {
        BaseRequest messageListRequest = new BaseRequest();
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.MESSAGE_UNREAD_TOTAL, messageListRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("getLoopData", "onError:" + e.getMessage());
                if (mView != null) {
                    mView.getUnReadMessage(false, 0);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("getLoopData", "getUser__response==" + response);

                BaseResponseModel baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel>() {
                        }.getType());
                if (baseResponseModel.status) {
                    if (mView != null) {
                        UbtLog.d("getLoopData", "baseResponseModel.models==" + baseResponseModel.models);
                        mView.getUnReadMessage(true, Integer.parseInt(baseResponseModel.models.toString()));
                    }
                } else {
                    if (mView != null) {
                        mView.getUnReadMessage(false, 0);
                    }
                }
            }
        });
    }

}
