package com.ubt.alpha1e.edu.maincourse.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.ubt.alpha1e.edu.R;

/**
 * @author：liuhai
 * @date：2018/1/2 16:14
 * @modifier：ubt
 * @modify_date：2018/1/2 16:14
 * [A brief description]
 * version
 */

public class CourseArrowAminalUtil {

    /**
     * 执行指示动画
     *
     * @param flag
     * @param imageView
     * @param arrow
     */
    public static void startViewAnimal(boolean flag, ImageView imageView, int arrow) {
        AnimationDrawable animationDrawable = null;
        if (flag) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(arrow == 1 ? R.drawable.animal_right_arrow : R.drawable.animal_left_arrow);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
        } else {
            imageView.setVisibility(View.GONE);
            if (null != animationDrawable) {
                animationDrawable.stop();
            }
        }
    }

    /**
     * 执行指示动画
     *
     * @param flag
     * @param imageView
     * @param arrow
     */
    public static void startLegViewAnimal(boolean flag, ImageView imageView, int arrow) {
        AnimationDrawable animationDrawable = null;
        if (flag) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(arrow==1?R.drawable.animal_baitui:R.drawable.animal_baidongzuo);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
        } else {
            imageView.setVisibility(View.GONE);
            if (null != animationDrawable) {
                animationDrawable.stop();
            }
        }
    }
    /**
     * 执行指示动画
     *
     * @param flag
     * @param imageView
     * @param arrow
     */
    public static void startTwoLegViewAnimal(boolean flag, ImageView imageView, int arrow) {
        AnimationDrawable animationDrawable = null;
        if (flag) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource( R.drawable.animal_baidongleg);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
        } else {
            imageView.setVisibility(View.GONE);
            if (null != animationDrawable) {
                animationDrawable.stop();
            }
        }
    }


}
