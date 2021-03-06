package com.ubt.alpha1e.edu.userinfo.useredit;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.RequstMode.UpdateUserInfoRequest;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.userinfo.model.UserAllModel;
import com.ubt.alpha1e.edu.userinfo.model.UserModel;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.StringUtils;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.weigan.loopview.LoopView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.List;

import okhttp3.Call;

import static android.R.attr.key;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserEditPresenter extends BasePresenterImpl<UserEditContract.View> implements UserEditContract.Presenter {

    /**
     * 修改头像对话框
     *
     * @param activity
     */
    @Override
    public void showImageHeadDialog(Activity activity) {
        ViewHolder viewHolder = new ViewHolder(R.layout.dialog_useredit_head);
        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.rl_take_photo || view.getId() == R.id.tv_take_photo) {
                            mView.takeImageFromShoot();
                        } else if (view.getId() == R.id.rl_take_ablum || view.getId() == R.id.tv_take_ablum) {
                            mView.takeImageFromAblum();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create().show();
    }

    @Override
    public void showImageCenterHeadDialog(Activity activity) {
        ViewHolder viewHolder = new ViewHolder(R.layout.dialog_userecenter_head);
        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.drawable.bg_edit_user)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.tv_take_photo) {
                            mView.takeImageFromShoot();
                        } else if (view.getId() == R.id.tv_take_ablum) {
                            mView.takeImageFromAblum();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create().show();
    }


    /**
     * 显示年龄对话框
     *
     * @param activity
     * @param currentPosition
     */
    @Override
    public void showAgeDialog(Activity activity, final List<String> ageList, int currentPosition) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_useredit_wheel, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.loopView);

        loopView.setItems(ageList);
        loopView.setInitPosition(0);

        loopView.setCurrentPosition(currentPosition);
        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_sure) {
                            if (isAttachView()) {
                                mView.ageSelectItem(0, ageList.get(loopView.getSelectedItem()));
                            }
                            dialog.dismiss();
                        }
                    }
                })
                .setCancelable(true)
                .create().show();


    }


    /**
     * 年级选择框
     *
     * @param activity
     * @param currentPosition
     * @param list
     */
    @Override
    public void showGradeDialog(Activity activity, int currentPosition, final List<String> list) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_useredit_wheel, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.loopView);

        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_sure) {
                            if (isAttachView()) {
                                mView.ageSelectItem(1, list.get(loopView.getSelectedItem()));
                                Log.d("showGradeDialog", "string==" + list.get(loopView.getSelectedItem()));
                            }
                            dialog.dismiss();
                        }
                    }
                })
                .create().show();
        // 设置原始数据
        loopView.setItems(list);
        loopView.setInitPosition(0);

        loopView.setCurrentPosition(currentPosition);
    }


    /**
     * 每个Item更新用户信息
     *
     * @param key
     * @param value
     */
    public void updateUserInfo(int key, String value) {
        UserModel mUserModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
        File file = null;
        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        switch (key) {
            case Constant.KEY_NICK_NAME:
                request.setNickName(value);
                if (mUserModel != null) {
                    request.setAge(StringUtils.getAgeByType(mUserModel.getAge()));
                    request.setSex(mUserModel.getSex());
                    request.setGrade(StringUtils.getGradeByType(mUserModel.getGrade()));
                }
                break;
            case Constant.KEY_NICK_SEX:
                request.setSex(value);
                if (mUserModel != null) {
                    request.setNickName(mUserModel.getNickName());
                    request.setAge(StringUtils.getAgeByType(mUserModel.getAge()));
                    request.setGrade(StringUtils.getGradeByType(mUserModel.getGrade()));
                }
                break;
            case Constant.KEY_NICK_AGE:
                request.setAge(StringUtils.getAgeByType(value));
                if (mUserModel != null) {
                    request.setNickName(mUserModel.getNickName());
                    request.setSex(mUserModel.getSex());
                    request.setGrade(StringUtils.getGradeByType(mUserModel.getGrade()));
                }
                break;
            case Constant.KEY_NICK_GRADE:
                request.setGrade(StringUtils.getGradeByType(value));
                if (mUserModel != null) {
                    request.setNickName(mUserModel.getNickName());
                    request.setSex(mUserModel.getSex());
                    request.setAge(StringUtils.getAgeByType(mUserModel.getAge()));
                }
                break;
            case Constant.KEY_NICK_HEAD:

                break;
            default:
                break;
        }
        UbtLog.d("UpdateHead--------", "request====" + request.toString() + "  headPath===" + value);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_USERINFO, file, request, key)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (null != mView) {
                            mView.updateUserModelFailed("用户名更新失败");
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        BaseResponseModel<UserModel> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<UserModel>>() {
                                }.getType());
                        UbtLog.d("userEdit", "baseResponseModel==" + baseResponseModel.status + "  " + "info" + baseResponseModel.info + baseResponseModel.models);
                        if (baseResponseModel.status) {
                            if (null != baseResponseModel.models) {
                                UserModel model = baseResponseModel.models;
                                UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
                                userModel.setAge(StringUtils.getAgeStringBytype(model.getAge()));
                                userModel.setSex(model.getSex());
                                userModel.setPhone(model.getPhone());
                                userModel.setHeadPic(model.getHeadPic());
                                userModel.setGrade(StringUtils.getGradeStringBytype(model.getGrade()));
                                userModel.setNickName(model.getNickName());
                                SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, userModel);
                            }
                            if (isAttachView()) {
                                mView.updateUserModelSuccess(baseResponseModel.models);
                            }
                        } else {
                            if (baseResponseModel.info.equals("11300")) {
                                if (null != mView) {
                                    mView.updateUserModelFailed("不能输入非法字符");
                                }
                            } else {
                                if (null != mView) {
                                    mView.updateUserModelFailed("更新失败");
                                }
                            }
                        }
                    }
                });
    }


    /**
     * 更新用户头像
     *
     * @param path
     */
    public void updateHead(String path) {
        File file = new File(path);


        UserModel mUserModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);

        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        if (mUserModel != null) {
            request.setNickName(mUserModel.getNickName());
            request.setSex(mUserModel.getSex());
            request.setGrade(StringUtils.getGradeByType(mUserModel.getGrade()));
            request.setAge(StringUtils.getAgeByType(mUserModel.getAge()));
        }
        UbtLog.d("UpdateHead--------", "request====" + request + "  headPath===" + path);
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_USERINFO, file, request, key)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("userEditHead", "e===" + e.getMessage());
                        if (isAttachView()) {
                            mView.updateUserModelFailed("更新失败");
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        BaseResponseModel<UserModel> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<UserModel>>() {
                                }.getType());
                        UbtLog.d("userEditHead", "baseResponseModel==" + baseResponseModel.status + "  " + baseResponseModel.models);
                        if (baseResponseModel.status) {
                            if (null != baseResponseModel.models) {
                                UserModel model = baseResponseModel.models;
                                UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
                                userModel.setAge(StringUtils.getAgeStringBytype(model.getAge()));
                                userModel.setSex(model.getSex());
                                userModel.setPhone(model.getPhone());
                                userModel.setHeadPic(model.getHeadPic());
                                userModel.setGrade(StringUtils.getGradeStringBytype(model.getGrade()));
                                userModel.setNickName(model.getNickName());
                                SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, userModel);
                            }
                            if (isAttachView()) {
                                mView.updateUserModelSuccess(baseResponseModel.models);
                            }
                        }
                    }
                });
    }

    /**
     * 获取列表数据
     */
    public void getLoopData() {
        BaseRequest baseRequest = new BaseRequest();
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.GET_USER_INFO, baseRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("getLoopData", "onError:" + e.getMessage());
                if (mView != null) {
                    mView.updateLoopData(null);
                }

            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("getLoopData", "getUser__response==" + response);
                BaseResponseModel<UserAllModel> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<UserAllModel>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    UserAllModel userAllModel = baseResponseModel.models;
                    if (isAttachView()) {
                        mView.updateLoopData(userAllModel);
                    }
                }
            }
        });

    }


    public int getPosition(String content, List<String> list) {
        int position = 0;
        if (TextUtils.isEmpty(content)) {
            position = 0;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(content)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }


}
