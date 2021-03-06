package com.ubt.alpha1e.edu.userinfo.dingdang;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.RequstMode.CheckIsBindRequest;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.base.loading.LoadingDialog;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.login.LoginManger;
import com.ubt.alpha1e.edu.mvp.MVPBaseFragment;
import com.ubt.alpha1e.edu.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class DingDangFragment extends MVPBaseFragment<DingDangContract.View, DingDangPresenter> implements DingDangContract.View {

    private static final String TAG = DingDangFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int CHECK_ROBOT_INFO = 1;

    @BindView(R.id.btn_dingdang)
    Button btnDingDang;


    public static DingDangFragment newInstance(String param1, String param2) {
        DingDangFragment fragment = new DingDangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG, "onCreate");
        UbtLog.d(TAG, "init LoginManger");
        LoginManger.getInstance().init(getActivity(),null);
    }

    @Override
    protected void initUI() {
        btnDingDang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.SP_ROBOT_PRODUCT_ID))){
//                    LoginManger.getInstance().toUserCenter();
//                }else{
//                    ToastUtils.showShort("请先连接机器人");
//                }

                checkMyRobotState();
            }
        });
    }


    public void checkMyRobotState() {
        LoadingDialog.show(getActivity());
        CheckIsBindRequest checkRobotInfo = new CheckIsBindRequest();
        checkRobotInfo.setSystemType("3");
        String url = HttpEntity.CHECK_ROBOT_INFO;
        doRequest(url,checkRobotInfo,CHECK_ROBOT_INFO);
    }

    /**
     * 请求网络操作
     */
    public void doRequest(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequest onError:" + e.getMessage());
                LoadingDialog.dismiss(getActivity());
                ToastUtils.showShort("获取绑定信息失败");

            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                LoadingDialog.dismiss(getActivity());
                switch (id) {
                    case CHECK_ROBOT_INFO:
                        BaseResponseModel<ArrayList<MyRobotModel>> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<MyRobotModel>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if(!baseResponseModel.status || baseResponseModel.models == null){
                            ToastUtils.showShort("获取绑定信息失败");
                        }else {
                            if(baseResponseModel.models.size() == 0){
                                ToastUtils.showShort("当前账号未绑定机器人");
                                return;
                            }else {
                                UbtLog.d(TAG, "size = "+baseResponseModel.models.size());
                                UbtLog.d(TAG, "AutoUpdate = " + baseResponseModel.models.get(0).getAutoUpdate());
                                UbtLog.d(TAG, "equipmentSeq = " + baseResponseModel.models.get(0).getEquipmentSeq());
                                UbtLog.d(TAG, "equipmentVersion = " + baseResponseModel.models.get(0).getEquipmentVersion());
                                LoginManger.getInstance().toUserCenter(baseResponseModel.models.get(0).getEquipmentSeq());
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        });

    }


    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_dingdang;
    }

    @Override
    protected void initBoardCastListener() {

    }
}
