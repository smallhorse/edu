package com.ubt.alpha1e.course.principle1;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PrincipleContract {
    interface View extends BaseView {

    }

    interface  Presenter extends BasePresenter<View> {

    }
}
