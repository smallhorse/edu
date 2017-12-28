package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.maincourse.model.CourseActionModel;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/12/14 15:54
 * @modifier：ubt
 * @modify_date：2017/12/14 15:54
 * [A brief description]
 * version
 */

public class ActionCourseDataManager {
    /**
     * 获取关卡一第一个课时数据
     *
     * @param context
     * @return
     */
    public static List<CourseOne1Content> getCardOneList(Context context) {
        List<CourseOne1Content> one1ContentList = new ArrayList<>();
        CourseOne1Content one1Content1 = new CourseOne1Content();
        one1Content1.setIndex(0);
        one1Content1.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_1"));
        one1Content1.setId(R.id.ll_frame);
        one1Content1.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content1.setActionPath(Constant.COURSE_ACTION_PATH + "时间轴.hts");
        one1Content1.setTitle("时间轴");
        one1Content1.setDirection(0);
        one1Content1.setX(0);
        one1Content1.setY(-50);
        one1Content1.setVertGravity(VerticalGravity.CENTER);
        one1Content1.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content1);

        CourseOne1Content one1Content3 = new CourseOne1Content();
        one1Content3.setIndex(2);
        one1Content3.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_3"));
        one1Content3.setId(R.id.rl_musicz_zpne);
        one1Content3.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content3.setActionPath(Constant.COURSE_ACTION_PATH + "音乐轴.hts");
        one1Content3.setTitle("音乐轴");
        one1Content3.setX(0);
        one1Content3.setY(-50);
        one1Content3.setVertGravity(VerticalGravity.CENTER);
        one1Content3.setHorizGravity(HorizontalGravity.RIGHT);
        one1Content3.setDirection(0);
        one1ContentList.add(one1Content3);

        CourseOne1Content one1Content4 = new CourseOne1Content();
        one1Content4.setIndex(3);
        one1Content4.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_4"));
        one1Content4.setId(R.id.iv_reset_index);
        one1Content4.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content4.setActionPath(Constant.COURSE_ACTION_PATH + "动作帧.hts");
        one1Content4.setTitle("动作帧");
        one1Content4.setDirection(0);
        one1Content4.setX(80);
        one1Content4.setY(30);
        one1Content4.setVertGravity(VerticalGravity.ALIGN_BOTTOM);
        one1Content4.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content4);

        CourseOne1Content one1Content5 = new CourseOne1Content();
        one1Content5.setIndex(4);
        one1Content5.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_5"));
        one1Content5.setId(R.id.iv_add_frame);
        one1Content5.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content5.setActionPath(Constant.COURSE_ACTION_PATH + "添加按钮.hts");
        one1Content5.setTitle("缩放时间轴");
        one1Content5.setDirection(1);
        one1Content5.setX(20);
        one1Content5.setY(0);
        one1Content5.setVertGravity(VerticalGravity.CENTER);
        one1Content5.setHorizGravity(HorizontalGravity.LEFT);
        one1ContentList.add(one1Content5);
//
//        CourseOne1Content one1Content5 = new CourseOne1Content();
//        one1Content5.setIndex(4);
//        one1Content5.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_5"));
//        one1Content5.setId(R.id.iv_add_frame);
//        one1Content5.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
//        one1Content5.setActionPath(Constant.COURSE_ACTION_PATH + "添加按钮.hts");
//        one1Content5.setTitle("添加按钮");
//        one1Content5.setDirection(1);
//        one1Content5.setX(20);
//        one1Content5.setY(0);
//        one1Content5.setVertGravity(VerticalGravity.CENTER);
//        one1Content5.setHorizGravity(HorizontalGravity.LEFT);
//        one1ContentList.add(one1Content5);
//
//        CourseOne1Content one1Content6 = new CourseOne1Content();
//        one1Content6.setIndex(5);
//        one1Content6.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_6"));
//        one1Content6.setId(R.id.iv_play_music);
//        one1Content6.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
//        one1Content6.setActionPath(Constant.COURSE_ACTION_PATH + "播放按钮.hts");
//        one1Content6.setTitle("播放按钮");
//        one1Content6.setDirection(0);
//        one1Content6.setX(-20);
//        one1Content6.setY(0);
//        one1Content6.setVertGravity(VerticalGravity.CENTER);
//        one1Content6.setHorizGravity(HorizontalGravity.RIGHT);
//        one1ContentList.add(one1Content6);
        return one1ContentList;
    }

    /**
     * 关卡课时及状态
     *
     * @return
     */
    public static List<CourseActionModel> getCourseActionModel(int card, int currentCourse) {
        List<CourseActionModel> list = new ArrayList<>();
        CourseActionModel model1 = null;
        CourseActionModel model2 = null;
        CourseActionModel model3 = null;
        if (card == 1) {
            model1 = new CourseActionModel("1.认识时间轴", 0);
            model2 = new CourseActionModel("2.了解添加键", 0);
            model3 = new CourseActionModel("3.了解播放键", 0);
        } else if (card == 2) {
            model1 = new CourseActionModel("1.了解动作库", 0);
            model2 = new CourseActionModel("2.初级动作库", 0);
            model3 = new CourseActionModel("3.高级动作库", 0);
        }
        if (currentCourse == 1) {
            model1.setStatu(2);
        } else if (currentCourse == 2) {
            model1.setStatu(1);
            model2.setStatu(2);
        } else if (currentCourse == 3) {
            model1.setStatu(1);
            model2.setStatu(1);
            model3.setStatu(2);
        }
        list.add(model1);
        list.add(model2);
        list.add(model3);

        return list;
    }


    public static List<CourseActionModel> getCourseDataList(int position) {
        int level = 1;// 当前第几个课时
        if (position == 0) {//第一关卡
            LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
            if (null != record) {
                UbtLog.d("getDataList", "record===" + record.toString());
                int course = record.getCourseLevel();
                int recordlevel = record.getPeriodLevel();
                if (course == 2) {
                    if (recordlevel == 0) {
                        level = 1;
                    } else if (recordlevel == 1) {
                        level = 2;
                    } else if (recordlevel == 2) {
                        level = 3;
                    } else if (recordlevel == 3) {
                        level = 1;
                    }
                }

            }
        }

        return getCourseActionModel(++position, level);
    }


}