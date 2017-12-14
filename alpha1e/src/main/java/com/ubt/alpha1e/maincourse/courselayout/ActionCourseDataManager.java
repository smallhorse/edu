package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;

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
        one1Content5.setTitle("添加按钮");
        one1Content5.setDirection(1);
        one1Content5.setX(20);
        one1Content5.setY(0);
        one1Content5.setVertGravity(VerticalGravity.CENTER);
        one1Content5.setHorizGravity(HorizontalGravity.LEFT);
        one1ContentList.add(one1Content5);

        CourseOne1Content one1Content6 = new CourseOne1Content();
        one1Content6.setIndex(5);
        one1Content6.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_6"));
        one1Content6.setId(R.id.iv_play_music);
        one1Content6.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content6.setActionPath(Constant.COURSE_ACTION_PATH + "播放按钮.hts");
        one1Content6.setTitle("播放按钮");
        one1Content6.setDirection(0);
        one1Content6.setX(-20);
        one1Content6.setY(0);
        one1Content6.setVertGravity(VerticalGravity.CENTER);
        one1Content6.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content6);
        return one1ContentList;
    }
}
