package com.ubt.alpha1e.edu.onlineaudioplayer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.mvp.MVPBaseFragment;
import com.ubt.alpha1e.edu.onlineaudioplayer.DataObj.OnlineresList;
import com.ubt.alpha1e.edu.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.edu.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.edu.onlineaudioplayer.adapter.GradeSelectedAdapter;
import com.ubt.alpha1e.edu.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.edu.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.edu.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.edu.onlineaudioplayer.model.CategoryContentInfo;
import com.ubt.alpha1e.edu.onlineaudioplayer.searchActivity.OnlineResRearchActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：ubt
 * @日期: 2018/4/4 10:35
 * @描述:
 */


public class OnlineAlbumListFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View,OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    private static String TAG="OnlineAlbumListFragment";
    private View mActivityView;
    private View mView;
    RecyclerView  mAlbumView;

    AlbumAdapter mAdapter;
    List<AlbumContentInfo> mOriginalDatas=new ArrayList<>();
    List<AlbumContentInfo> mAlbumDatas=new ArrayList<>();
    ImageView mSearch;
    ImageView mGradeSort;
    ImageView mBack;
    ListView  mGradeSelect;
    TextView mTitleName;
    RelativeLayout rl_no_net;
    RelativeLayout rl_content;
    public static ArrayList<String> mGradData=new ArrayList<>();
    public static ArrayList<Boolean> mGradeSelectedData=new ArrayList<>();
    public static ArrayList<String>mSelectedGrade=new ArrayList<>();
    public static Context mContext;
    public final static int GRADE_SELECT_ADD = 1;
    public final static int GRADE_UNSELECT_DELETE= 2;
    private static String mCategoryId;
    private static String mCategoryName;
    public final static int SEARCH_ENTER_FRAGMENT=1;
    public final static int CATEGORY_ENTER_FRAGMENT=2;
    public static int type=0;
    public static List<AlbumContentInfo> mSearchDatas;
    public static String mAlbumId="";
    public static AlbumContentInfo mAlbumContentInfo;
    OnlineAudioResourcesHelper mHelper;
    static OnlineAlbumListFragment mOnlineAudioAlbumPlayerFragment;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GRADE_SELECT_ADD:
                   // UbtLog.d(TAG,"ADD "+msg.obj+"mOriginalDatas  "+mOriginalDatas.size());
                    for(int i=0;i<mOriginalDatas.size();i++){
                     //   UbtLog.d(TAG,"after mOriginalDatas" + i+mOriginalDatas.get(i).albumName+mOriginalDatas.get(i).grade);
                        if(mOriginalDatas.get(i).grade!=null) {
                            if (mOriginalDatas.get(i).grade.equals(msg.obj.toString())) {
                                if (!mAlbumDatas.contains(mOriginalDatas.get(i))) {
                                  //  UbtLog.d(TAG,"ADD CONTENT "+mOriginalDatas.get(i));
                                    mAlbumDatas.add(mOriginalDatas.get(i));
                                }
                            }
                        }
                    }
                    mSelectedGrade.add(msg.obj.toString());
                    mAdapter.notifyDataSetChanged();
                    break;
                case GRADE_UNSELECT_DELETE:
                    UbtLog.d(TAG,"DELETE "+msg.obj+mAlbumDatas.size());
                   for(int i=0;i< mOriginalDatas.size();i++){
                       UbtLog.d(TAG,"mAlbumDatas.get(i).grade" );
                       if(mOriginalDatas.get(i).grade!=null) {
                           if (mOriginalDatas.get(i).grade.equals(msg.obj.toString())) {
                               UbtLog.d(TAG, "DELETE content" + msg.obj + "mAlbumDatas position ");
                               mAlbumDatas.remove(mOriginalDatas.get(i));
                           }
                       }
                   }
                   UbtLog.d(TAG,"UNSELECTED "+mAlbumDatas.size());
                      mGradeSelectedData.remove(msg.obj.toString());
                      mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    public static OnlineAlbumListFragment newInstance(OnlineresList mCategory){
        if(mOnlineAudioAlbumPlayerFragment==null) {
            mOnlineAudioAlbumPlayerFragment = new OnlineAlbumListFragment();
        }
       // OnlineAlbumListFragment  mOnlineAudioAlbumPlayerFragment= new OnlineAlbumListFragment();
        mOnlineAudioAlbumPlayerFragment= new OnlineAlbumListFragment();
        mCategoryId=mCategory.getRes_id();
        mCategoryName  =mCategory.getRes_name();
        type = CATEGORY_ENTER_FRAGMENT;
        UbtLog.d(TAG,"Category click enter "+mCategoryId +"Category name "+mCategoryName);
        return mOnlineAudioAlbumPlayerFragment;
    }
    public static OnlineAlbumListFragment newInstance(AlbumContentInfo albumContentInfo) {
        if(mOnlineAudioAlbumPlayerFragment==null) {
            mOnlineAudioAlbumPlayerFragment = new OnlineAlbumListFragment();
        }
        //OnlineAlbumListFragment  mOnlineAudioAlbumPlayerFragment= new OnlineAlbumListFragment();
        mAlbumContentInfo=albumContentInfo;
        mCategoryId=albumContentInfo.getCategoryId();
        mAlbumId=albumContentInfo.albumId;
        type=SEARCH_ENTER_FRAGMENT;
        UbtLog.d(TAG,"SEARCH click enter "+mCategoryId+"Category name "+mCategoryName);
        return mOnlineAudioAlbumPlayerFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_onlineaudio_player, container, false);
        mContext=getContext();
        mHelper = OnlineAudioResourcesHelper.getInstance(getContext());
        mActivityView=(View)mView.findViewById(R.id.v_transparent);
        mAlbumView =(RecyclerView)mView.findViewById(R.id.rv_albums);
        mGradeSort=(ImageView)mView.findViewById(R.id.iv_grade_sort);
        mSearch=(ImageView)mView.findViewById(R.id.iv_search);
        mBack=(ImageView)mView.findViewById(R.id.iv_back);
        mGradeSelect=(ListView)mView.findViewById(R.id.grade_select_dialog);
        mTitleName=mView.findViewById(R.id.tv_base_title_name);
        mTitleName.setText(mCategoryName);
        rl_no_net=mView.findViewById(R.id.rl_no_net);
        rl_content =mView.findViewById(R.id.rl_content);
        GridLayoutManager mGridLayoutManager=new GridLayoutManager(getActivity(),2);
        mAlbumView.setLayoutManager(mGridLayoutManager);
      //  mAlbumView.addItemDecoration(new DividerItemDecoration(mAlbumView.getContext(),mLinearLayoutManager.getOrientation()));
        mAlbumView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 36;
                outRect.left = 15;
                outRect.right = 1;

            }
        });
        mAdapter = new AlbumAdapter();
        mAlbumView.setAdapter(mAdapter);
        //P require data from back-end
        UbtLog.d(TAG, "ENTER CATEGORY FRAGMENT ");
        mPresenter.getAlbumList(mCategoryId);
        if(type==SEARCH_ENTER_FRAGMENT){
            OnlineAudioListFragment mfragment = OnlineAudioListFragment.newInstance(mAlbumContentInfo);
            start(mfragment);
        }
        mGradeSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGradeSelect.setVisibility(View.VISIBLE);
                mActivityView.setVisibility(View.VISIBLE);
                mGradeSelect.setAdapter(new GradeSelectedAdapter(mHandler));
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                UbtLog.d(TAG,"ib_rearch" );
                Intent i = new Intent();
                i.setClass(getActivity(), OnlineResRearchActivity.class);
                getActivity().startActivity(i);
                pop();

            }
        });
        mBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               if(type!=SEARCH_ENTER_FRAGMENT) {
                   pop();
               }else {
                   getActivity().finish();
               }
            }
        });
        mActivityView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                UbtLog.d(TAG,"ON TOUCH "+view.getId());
                mGradeSelect.setVisibility(View.INVISIBLE);
                mActivityView.setVisibility(View.INVISIBLE);
                return false;
            }
        });
        rl_no_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mPresenter.getAlbumList(mCategoryId);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return mView;
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public void showCourseList(List<CategoryContentInfo> album) {

    }

    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {
        if(status) {
            rl_content.setVisibility(View.VISIBLE);
            rl_no_net.setVisibility(View.INVISIBLE);
            UbtLog.d(TAG,"request result from back-end"+album);
            mAlbumDatas.clear();
            mGradData.clear();
            mOriginalDatas.clear();
            mAlbumDatas = album;
            ArrayList<String> temp = new ArrayList();
            for (int i = 0; i < album.size(); i++) {
                mOriginalDatas.add(i, album.get(i));
            }
            for (int i = 0; i < album.size(); i++) {
                mGradeSelectedData.add(i, true);
            }
            for (int i = 0; i < album.size(); i++) {
                mGradData.add(i, album.get(i).grade);
            }
            for (int i = 0; i < mGradData.size(); i++) {
                if (!temp.contains(mGradData.get(i))) {
                    if (mGradData.get(i) != null) {
                        temp.add(mGradData.get(i));
                    }
                }
            }
            mGradData = temp;
            mAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(getActivity(),"后台出错，没有配置数据",Toast.LENGTH_LONG).show();
            rl_content.setVisibility(View.INVISIBLE);
            rl_no_net.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {
    }


    @Override
    public void onRequestStatus(int requestType, int errorCode) {

        rl_content.setVisibility(View.INVISIBLE);
        rl_no_net.setVisibility(View.VISIBLE);
    }

    public class AlbumHolder extends RecyclerView.ViewHolder{
        public TextView txt_album_name;
        public RelativeLayout rl_online_albumlist_item ;
        public AlbumHolder(View itemView) {
            super(itemView);
            txt_album_name=(TextView)itemView.findViewById(R.id.item_album);
            rl_online_albumlist_item = (RelativeLayout)itemView.findViewById(R.id.online_albumlist_item);
        }
    }


    class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mView =LayoutInflater.from(mContext).inflate(R.layout.layout,parent,false);
            AlbumHolder mAlbumHolder=new AlbumHolder(mView);
            return mAlbumHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((AlbumHolder) holder).txt_album_name.setText(mAlbumDatas.get(position).albumName);//+"GRADE"+mAlbumDatas.get(position).grade);
            ((AlbumHolder) holder).rl_online_albumlist_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlbumId = mAlbumDatas.get(position).albumId;
//                    mHelper.setmCategoryId(mAlbumDatas.get(position).getCategoryId());
//                    mHelper.setAlbumId(mAlbumDatas.get(position).getAlbumId());
                   // mHelper.setCurentPlayingAudioIndex(0);
                    OnlineAudioListFragment mfragment = OnlineAudioListFragment.newInstance(mAlbumDatas.get(position));
                    start(mfragment);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAlbumDatas.size();
        }
    }
}

