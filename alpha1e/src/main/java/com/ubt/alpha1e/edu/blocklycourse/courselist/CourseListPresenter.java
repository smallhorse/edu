package com.ubt.alpha1e.edu.blocklycourse.courselist;

import android.content.Context;

import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.blocklycourse.model.BlocklyCourseListCallback;
import com.ubt.alpha1e.edu.blocklycourse.model.BlocklyCourseModel;
import com.ubt.alpha1e.edu.blocklycourse.model.CourseData;
import com.ubt.alpha1e.edu.blocklycourse.model.UpdateCourseRequest;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CourseListPresenter extends BasePresenterImpl<CourseListContract.View> implements CourseListContract.Presenter{

    private static final String TAG = "CourseListPresenter";

    @Override
    public void getBlocklyCourseList(final Context context) {
        //从服务器获取数据并保存本地数据



        final BaseRequest request = new BaseRequest();
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.BLOCKLY_COURSE_LIST, request, 0).execute(new BlocklyCourseListCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "BLOCKLY_COURSE_LIST onError e:" + e.getMessage());
                ToastUtils.showShort("网络出错,请检查是否开启网络");
                if (isAttachView()) {
                    List<CourseData> courseDataList = DataSupport.findAll(CourseData.class);
                    UbtLog.d(TAG, "courseDataList:" + courseDataList.toString());
                    mView.setBlocklyCourseData(courseDataList);
                }

            }

            @Override
            public void onResponse(List<BlocklyCourseModel> response, int id) {
                UbtLog.d(TAG, "BLOCKLY_COURSE_LIST onResponse:" + response.toString());



                for(int i=0; i<response.size(); i++){
                    CourseData courseData = new CourseData();
                    courseData.setSequence(response.get(i).getSequence());
                    courseData.setCurrGraphProgramId(response.get(i).getCurrGraphProgramId());
                    courseData.setName(response.get(i).getName());
                    courseData.setStatus(response.get(i).getStatus());
                    courseData.setUserId(response.get(i).getUserId());
                    courseData.setThumbnailUrl(response.get(i).getThumbnailUrl());
                    courseData.setVideoUrl(response.get(i).getVideoUrl());
                    courseData.setCid(response.get(i).getCid());
                    courseData.setSubTitle(response.get(i).getSubTitle());
                    courseData.saveOrUpdate("cid = ?" , String.valueOf(response.get(i).getCid()));

                    UbtLog.d(TAG, "courseData:" + courseData.toString());
                }



                if (isAttachView()) {
                    List<CourseData> courseDataList = DataSupport.findAll(CourseData.class);
                    UbtLog.d(TAG, "courseDataList:" + courseDataList.toString());
                    mView.setBlocklyCourseData(courseDataList);
                }


            }
        });

    }

    @Override
    public void updateCurrentCourse(int cid) {
        UpdateCourseRequest courseRequest = new UpdateCourseRequest();
        courseRequest.setCurrGraphProgramId(cid);

        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_BLOCKLY_COURSE, courseRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.e(TAG, "updateCurrentCourse onError:" + e.getMessage());
                if(isAttachView()){
                    mView.updateFail();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "updateCurrentCourse onResponse:" + response.toString());
                if(isAttachView()){
                    mView.updateSuccess();
                }
            }
        });
    }




}
