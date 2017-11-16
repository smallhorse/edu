package com.ubt.alpha1e.ui.main;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseView {
        void showCartoonAction(int value );

        void showBluetoothStatus(String status);

        void showCartoonText(String text);

    }

    interface Presenter extends BasePresenter<View> {
        void requestCartoonAction(String json);

        void requestCartoonText(String text);

        void requestBluetoothStatus(String status);

        void commandRobotAction(String json);

        void dealMessage(String json);
    }
}
