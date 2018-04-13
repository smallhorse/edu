package com.ubt.alpha1e.onlineaudioplayer.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.onlineresAdpater;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.FileUtils;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.CategoryMax;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineresList;
import com.ubt.alpha1e.onlineaudioplayer.DividerItemDecorationNew;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.onlineresrearch.OnlineResRearchActivity;
import com.ubt.alpha1e.ui.dialog.RobotBindingDialog;
import com.ubt.alpha1e.ui.main.MainActivity;
import com.ubt.alpha1e.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * @作者：ubt
 * @日期: 2018/4/4 10:37
 * @描述:
 */


public class OnlineAudioResourcesFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    String TAG = "OnlineAudioResourcesFragment";

    @BindView(R.id.online_res_list)
    RecyclerView mRecyclerview;

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ib_rearch)
    ImageButton ib_rearch;

    @BindView(R.id.ig_player_state)
    ImageView ig_player_state;

    @BindView(R.id.player_name)
    TextView player_name;

    @BindView(R.id.ig_player_button)
    ImageView ig_player_button;

    public LinearLayoutManager mLayoutManager;
    public onlineresAdpater mAdapter;
    public List<OnlineresList> onlineresList = new ArrayList<>();

    private static final int GET_MAX_CATEGORY = 50;
    Unbinder unbinder;
    private  OnlineAudioResourcesHelper mHelper = null;
    public static OnlineAudioResourcesFragment newInstance() {
        OnlineAudioResourcesFragment onlineAudioResourcesFragment = new OnlineAudioResourcesFragment();
        return onlineAudioResourcesFragment;
    }


    @OnClick({R.id.ib_return, R.id.ib_rearch, R.id.ig_player_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_return:
                getActivity().finish();
                break;
            case R.id.ib_rearch:
                UbtLog.d(TAG,"ib_rearch" );
                Intent i = new Intent();
                i.setClass(getActivity(), OnlineResRearchActivity.class);
                getActivity().startActivity(i);
                break;
            case R.id.ig_player_button:
                UbtLog.d(TAG,"ig_player_button" );
                break;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        mHelper = new OnlineAudioResourcesHelper(getContext());
        return rootView;
    }

    @Override
    protected void initUI() {
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);

        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        DividerItemDecorationNew dividerItemDecoration = new DividerItemDecorationNew(getActivity(),DividerItemDecorationNew.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.linesharp));
        mRecyclerview.addItemDecoration(dividerItemDecoration);
        mRecyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 90;
                outRect.left = 1;
                outRect.top = 40;
                outRect.bottom = 40;
            }
        });
        mAdapter = new onlineresAdpater(getActivity().getApplicationContext(),onlineresList);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        getMaxCategory();
    }



    //拉最大的类别
    public void getMaxCategory() {
        com.ubt.alpha1e.base.loading.LoadingDialog.show(getActivity());
        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.GET_MAX_CATERGOTY;
        doRequestGetMaxCategory(url, gotoBindRequest, GET_MAX_CATEGORY);

    }

    /**
     * 网络请求
     */
    public void doRequestGetMaxCategory(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "getMaxCategory onError:" + e.getMessage());
                switch (id) {
                    case GET_MAX_CATEGORY:
                        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(getActivity());
                        ToastUtils.showShort("请求失败");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getMaxCategory response = " + response);
                switch (id) {
                    case GET_MAX_CATEGORY:
                        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(getActivity());
                        BaseResponseModel<ArrayList<CategoryMax>> modle = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<CategoryMax>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if (modle.status) {
                            UbtLog.d(TAG, "请求成功");
                            if(modle.models.size() == 0){
                                UbtLog.d(TAG, "没有类别" );
                                return;
                            }else {
                                UbtLog.d(TAG, "size = "+modle.models.size());
                                int size = modle.models.size();
                                onlineresList.clear();
                                for(int i =0;i<size;i++){
                                    OnlineresList s = new OnlineresList();
                                    s.setRes_id(modle.models.get(i).getCategoryId());
                                    s.setRes_name(modle.models.get(i).getCategoryName());
                                    onlineresList.add(s);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            UbtLog.d(TAG, "请求失败");
                            ToastUtils.showShort("请求失败");
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
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_onlineres_list;
    }

    @Override
    public void showGradeList() {

    }

    @Override
    public void showAlbumList(List<String> albumList) {

    }

}
