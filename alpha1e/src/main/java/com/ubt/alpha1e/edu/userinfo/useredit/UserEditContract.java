package com.ubt.alpha1e.edu.userinfo.useredit;

import android.app.Activity;

import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;
import com.ubt.alpha1e.edu.userinfo.model.UserAllModel;
import com.ubt.alpha1e.edu.userinfo.model.UserModel;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserEditContract {
    public interface View extends BaseView {
        void getAgeDataList(List<String> list);

        void takeImageFromShoot();

        void takeImageFromAblum();

        void ageSelectItem(int type, String age);

        void updateUserModelSuccess(UserModel userModel);
        void updateUserModelFailed(String str);

        void updateLoopData(UserAllModel userAllModel);
    }

    interface Presenter extends BasePresenter<View> {
        void showImageHeadDialog(Activity activity);

        void showImageCenterHeadDialog(Activity activity);

        void showAgeDialog(Activity activity,List<String> ageList, int currentPosition);

        void showGradeDialog(Activity activity, int currentPosition, List<String> list);
    }
}
