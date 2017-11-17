package com.ubt.alpha1e.userinfo.aboutus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.CheckUpdateRequest;
import com.ubt.alpha1e.base.RequstMode.FeedbackRequest;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.update.UpdateTools;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class AboutUsPresenter extends BasePresenterImpl<AboutUsContract.View> implements AboutUsContract.Presenter{

    private static final String TAG = AboutUsPresenter.class.getSimpleName();

    private final int DO_CHECK_UPDATE = 1001;

    @Override
    public boolean isOnlyWifiDownload(Context context) {
        String value = BasicSharedPreferencesOperator.getInstance(context,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY);
        if (value.equals(BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isWifiCoon(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doUpdateApk(String version) {
        if (AlphaApplicationValues.getCurrentEdit() == AlphaApplicationValues.EdtionCode.for_factory_edit) {
            mView.noteNewestVersion();
            return;
        }

        CheckUpdateRequest checkUpdateRequest = new CheckUpdateRequest();
        checkUpdateRequest.setVersion(version);
        checkUpdateRequest.setType("1");

        String url = HttpEntity.CHECK_APP_UPDATE;
        doRequestFromWeb(url, checkUpdateRequest, DO_CHECK_UPDATE);
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
                    case DO_CHECK_UPDATE:
                        mView.noteApkUpsateFail(((MVPBaseActivity)mView.getContext()).getStringResources("ui_common_network_request_failed"));
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                switch (id){
                    case DO_CHECK_UPDATE:
                    {
                        if (JsonTools.getJsonStatus(response) ) {
                            JSONObject result  = JsonTools.getJsonModel(response);
                            UbtLog.d(TAG,"result = " + result);
                            try {
                                String versionPath = result.getString("path");
                                UbtLog.d(TAG,"versionPath = " + versionPath);
                                if(!TextUtils.isEmpty(versionPath)){
                                    mView.noteApkUpdate(versionPath);
                                }else {
                                    mView.noteNewestVersion();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            mView.noteApkUpsateFail(((MVPBaseActivity)mView.getContext()).getStringResources("ui_common_network_request_failed"));
                        }

                    }
                    break;

                }

            }
        });

    }

}
