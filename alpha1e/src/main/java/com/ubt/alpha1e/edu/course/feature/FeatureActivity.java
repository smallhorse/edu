package com.ubt.alpha1e.edu.course.feature;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.animator.BezierView;
import com.ubt.alpha1e.edu.animator.FloatAnimator;
import com.ubt.alpha1e.edu.animator.IBezierView;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.course.event.PrincipleEvent;
import com.ubt.alpha1e.edu.course.helper.PrincipleHelper;
import com.ubt.alpha1e.edu.course.merge.MergeActivity;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.edu.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e.edu.utils.SizeUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class FeatureActivity extends MVPBaseActivity<FeatureContract.View, FeaturePresenter> implements FeatureContract.View, IBezierView {


    private static final String TAG = FeatureActivity.class.getSimpleName();

    private static final int SHOW_VIEW = 1;
    private static final int BEZIER_ANIMATOR_FINISH = 2;
    private static final int PLAY_ACTION_FINISH = 3;
    private static final int ENABLE_ALL_VIEW = 4;
    private static final int RECEVICE_SENSOR = 5;
    private static final int RECEVICE_HEAD = 6;
    private static final int RECEVICE_VOICE_WAIT = 7;
    private static final int LEARN_FINISH = 8;
    private static final int TAP_HEAD = 9;
    private static final int BLUETOOTH_DISCONNECT = 10;
    private static final int OVER_TIME_FINISH = 11;
    private static final int RECIEVE_HIBITS_START = 12;
    private static final int LOW_BATTERY_LESS_FIVE = 13;
    private static final int SHOW_BEZIER_VIEW = 14;

    @BindView(R.id.tv_next)
    TextView tvNext;
    Unbinder unbinder;
    @BindView(R.id.iv_robot)
    ImageView ivRobot;
    @BindView(R.id.iv_hand_left)
    ImageView ivHandLeft;
    @BindView(R.id.iv_hand_right)
    ImageView ivHandRight;
    @BindView(R.id.iv_leg_left)
    ImageView ivLegLeft;
    @BindView(R.id.iv_leg_right)
    ImageView ivLegRight;
    @BindView(R.id.rl_robot)
    RelativeLayout rlRobot;
    @BindView(R.id.iv_principle_steering_engine)
    ImageView ivPrincipleSteeringEngine;
    @BindView(R.id.iv_principle_infrared_sensor)
    ImageView ivPrincipleInfraredSensor;
    @BindView(R.id.iv_principle_soundbox)
    ImageView ivPrincipleSoundbox;
    @BindView(R.id.iv_principle_head)
    ImageView ivPrincipleHead;
    @BindView(R.id.iv_principle_eye)
    ImageView ivPrincipleEye;
    @BindView(R.id.iv_principle_voice)
    ImageView ivPrincipleVoice;
    @BindView(R.id.iv_principle_voice_obstacle_avoidance)
    ImageView ivPrincipleVoiceObstacleAvoidance;
    @BindView(R.id.bzv_principle_steering_engine)
    BezierView bzvPrincipleSteeringEngine;
    @BindView(R.id.bzv_principle_infrared_sensor)
    BezierView bzvPrincipleInfraredSensor;
    @BindView(R.id.bzv_principle_soundbox)
    BezierView bzvPrincipleSoundbox;
    @BindView(R.id.bzv_principle_head)
    BezierView bzvPrincipleHead;
    @BindView(R.id.bzv_principle_eye)
    BezierView bzvPrincipleEye;
    @BindView(R.id.bzv_principle_voice)
    BezierView bzvPrincipleVoice;
    @BindView(R.id.bzv_principle_voice_obstacle_avoidance)
    BezierView bzvPrincipleVoiceObstacleAvoidance;
    @BindView(R.id.rl_principle_steering_engine_intro)
    RelativeLayout rlPrincipleSteeringEngineIntro;
    @BindView(R.id.rl_principle_infrared_sensor_intro)
    RelativeLayout rlPrincipleInfraredSensorIntro;
    @BindView(R.id.rl_principle_soundbox_intro)
    RelativeLayout rlPrincipleSoundboxIntro;
    @BindView(R.id.rl_principle_head_intro)
    RelativeLayout rlPrincipleHeadIntro;
    @BindView(R.id.rl_principle_eye_intro)
    RelativeLayout rlPrincipleEyeIntro;
    @BindView(R.id.rl_principle_voice_intro)
    RelativeLayout rlPrincipleVoiceIntro;
    @BindView(R.id.rl_principle_voice_obstacle_avoidance_intro)
    RelativeLayout rlPrincipleVoiceObstacleAvoidanceIntro;
    @BindView(R.id.tv_msg_show)
    TextView tvMsgShow;
    @BindView(R.id.tv_principle_steering_engine_intro)
    TextView tvPrincipleSteeringEngineIntro;
    @BindView(R.id.tv_principle_infrared_sensor_intro)
    TextView tvPrincipleInfraredSensorIntro;
    @BindView(R.id.tv_principle_soundbox_intro)
    TextView tvPrincipleSoundboxIntro;
    @BindView(R.id.tv_principle_head_intro)
    TextView tvPrincipleHeadIntro;
    @BindView(R.id.tv_principle_eye_intro)
    TextView tvPrincipleEyeIntro;
    @BindView(R.id.tv_principle_voice_intro)
    TextView tvPrincipleVoiceIntro;
    @BindView(R.id.tv_principle_voice_obstacle_avoidance_intro)
    TextView tvPrincipleVoiceObstacleAvoidanceIntro;
    @BindView(R.id.iv_radiological_wave)
    ImageView ivRadiologicalWave;


    private Animation biggerLeftBottomAnim = null;
    private Animation biggerLeftTopAnim = null;
    private Animation biggerRightTopAnim = null;
    private Animation smallerLeftBottomAnim = null;
    private Animation smallerLeftTopAnim = null;
    private Animation smallerRightTopAnim = null;
    private AnimationDrawable radiologicalWaveAnim = null;

    private int containerWidth;
    private int containerHeight;
    private int scale = 0;
    private int playIndex = 0;// 1、SteeringEngine 2、InfraredSensor 3、Soundbox  4、head  5、eye  6、voice   7、VoiceObstacleAvoidance
    private int playCount = 0;
    private int mCurrentProgress = 0;

    private boolean hasInitRobot = false;
    private RelativeLayout.LayoutParams params = null;
    private FloatAnimator mFloatAnimator = null;
    private boolean hasReceviceSensor = false;
    private boolean hasReceviceHead = false;
    private boolean hasReceviceVoice = false;

    private boolean hasLearnEngine = false;
    private boolean hasLearnSensor = false;
    private boolean hasLearnSoundbox = false;
    private boolean hasLearnHead = false;
    private boolean hasLearnEye = false;
    private boolean hasLearnVoice = false;
    private boolean hasLearnObstacle = false;

    //是否正在头部学习
    private boolean hasLearnHeadIng = false;
    private ConfirmDialog mTapHeadDialog = null;

    private boolean isDoBack = false;
    private boolean isClickable = true;
    private final int OVER_TIME = 15 * 1000;//(15S音频等播放时间)超时

    private Date startTime = null;
    private boolean isShowHibitsDialog = false;
    private boolean hasReceiveHibitsStart = false;

    private int mPadDelayTime = 0;//平板改变View位置后一会才能取得真实的位置，所有要延时一会，手机则为0

    private boolean hasInitBezierView = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_BEZIER_VIEW:
                    if(hasInitBezierView){
                        showBezierView();
                    }else {
                        mHandler.sendEmptyMessageDelayed(SHOW_BEZIER_VIEW, 100);
                    }
                    break;
                case SHOW_VIEW: {
                    int viewId = msg.arg1;
                    if (viewId == R.id.bzv_principle_steering_engine) {
                        bzvPrincipleSteeringEngine.start();
                    } else if (viewId == R.id.bzv_principle_infrared_sensor) {
                        bzvPrincipleInfraredSensor.start();
                    } else if (viewId == R.id.bzv_principle_soundbox) {
                        bzvPrincipleSoundbox.start();
                    } else if (viewId == R.id.bzv_principle_head) {
                        bzvPrincipleHead.start();
                    } else if (viewId == R.id.bzv_principle_eye) {
                        bzvPrincipleEye.start();
                    } else if (viewId == R.id.bzv_principle_voice) {
                        bzvPrincipleVoice.start();
                    } else if (viewId == R.id.bzv_principle_voice_obstacle_avoidance) {
                        bzvPrincipleVoiceObstacleAvoidance.start();
                    }
                }

                break;
                case BEZIER_ANIMATOR_FINISH:
                    int viewId = msg.arg1;
                    if (viewId == R.id.bzv_principle_steering_engine) {
                        ivPrincipleSteeringEngine.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleSteeringEngine);
                    } else if (viewId == R.id.bzv_principle_infrared_sensor) {
                        ivPrincipleInfraredSensor.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleInfraredSensor);
                    } else if (viewId == R.id.bzv_principle_soundbox) {
                        ivPrincipleSoundbox.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleSoundbox);
                    } else if (viewId == R.id.bzv_principle_head) {
                        ivPrincipleHead.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleHead);
                    } else if (viewId == R.id.bzv_principle_eye) {
                        ivPrincipleEye.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleEye);
                    } else if (viewId == R.id.bzv_principle_voice) {
                        ivPrincipleVoice.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleVoice);
                    } else if (viewId == R.id.bzv_principle_voice_obstacle_avoidance) {
                        ivPrincipleVoiceObstacleAvoidance.setVisibility(View.VISIBLE);
                        mFloatAnimator.addShow(ivPrincipleVoiceObstacleAvoidance);
                    }
                    break;
                case PLAY_ACTION_FINISH:
                    if (playIndex == 1) {
                        //finish
                        isClickable = true;
                        playIndex = 0;
                        playCount = 0;
                        hasLearnEngine = true;
                        stopRadiologicalWaveAnim();
                        showView(rlPrincipleSteeringEngineIntro, false, smallerLeftBottomAnim);
                        mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);

                    } else if (playIndex == 2) {

                        if (playCount == 0) {

                        } else if (playCount == 1) {

                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleInfraredSensorIntro, true, biggerRightTopAnim);
                            playActionFile("红外传感.hts");
                            startRadiologicalWaveAnim();
                            playCount = 2;
                        } else if (playCount == 2) {
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            isClickable = true;
                            hasLearnSensor = true;
                            stopRadiologicalWaveAnim();
                            showView(rlPrincipleInfraredSensorIntro, false, smallerRightTopAnim);
                            ((PrincipleHelper) mHelper).doReadInfraredSensor((byte) 0);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);
                        }

                    } else if (playIndex == 3) {

                        //finish
                        playIndex = 0;
                        playCount = 0;
                        isClickable = true;
                        hasLearnSoundbox = true;
                        stopRadiologicalWaveAnim();
                        showView(rlPrincipleSoundboxIntro, false, smallerRightTopAnim);
                        mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);
                    } else if (playIndex == 4) {
                        if (playCount == 0) {

                        } else if (playCount == 1) {
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleHeadIntro, true, biggerLeftTopAnim);
                            playActionFile("触摸传感.hts");
                            startRadiologicalWaveAnim();
                            playCount = 2;
                        } else if (playCount == 2) {
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            isClickable = true;
                            hasLearnHead = true;
                            hasLearnHeadIng = false;
                            stopRadiologicalWaveAnim();
                            showView(rlPrincipleHeadIntro, false, smallerLeftTopAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);
                            ((PrincipleHelper) mHelper).doReadHeadClick((byte) 0);
                        }
                    } else if (playIndex == 5) {

                        //finish
                        playIndex = 0;
                        playCount = 0;
                        isClickable = true;
                        hasLearnEye = true;
                        stopRadiologicalWaveAnim();
                        showView(rlPrincipleEyeIntro, false, smallerLeftTopAnim);
                        mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);
                    } else if (playIndex == 6) {
                        if (playCount == 0) {

                        } else if (playCount == 1) {
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleVoiceIntro, true, biggerLeftBottomAnim);
                            playActionFile("麦克风.hts");
                            startRadiologicalWaveAnim();
                            playCount = 2;

                        } else if (playCount == 2) {
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            isClickable = true;
                            hasLearnVoice = true;
                            stopRadiologicalWaveAnim();
                            showView(rlPrincipleVoiceIntro, false, smallerLeftBottomAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);
                        }
                    } else if (playIndex == 7) {
                        if (playCount == 0) {
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleVoiceObstacleAvoidanceIntro, true, biggerLeftBottomAnim);
                            playCount = 1;
                            playActionFile("百宝袋.hts");
                            startRadiologicalWaveAnim();
                        } else if (playCount == 1) {
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            isClickable = true;
                            hasLearnObstacle = true;
                            stopRadiologicalWaveAnim();
                            showView(rlPrincipleVoiceObstacleAvoidanceIntro, false, smallerLeftBottomAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH, 500);
                        }
                    }

                    break;
                case ENABLE_ALL_VIEW:
                    setAllEnable(true);
                    break;
                case LEARN_FINISH:
                    mHandler.sendEmptyMessage(ENABLE_ALL_VIEW);
                    UbtLog.d(TAG, "getLearnCount() = " + getLearnCount());
                    if (getLearnCount() == 7) {
                        doSaveCourseProgress(1, 1, 4);
                        playActionFile("胜利.hts");
                        new ConfirmDialog(getContext()).builder()
                                .setTitle(getStringResources("ui_setting_principle_learn_finish"))
                                .setMsg(getStringResources("ui_setting_principle_learn_next"))
                                .setCancelable(false)
                                .setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        exitPrincipleProcess();
                                    }
                                }).show();
                    } else {
                        if (getLearnCount() < 5 && mCurrentProgress < 4) {
                            setViewEnable(tvNext, false, 0.3f);
                        } else {
                            setViewEnable(tvNext, true, 1f);
                        }
                    }
                    break;

                case RECEVICE_SENSOR:
                    if (!hasReceviceSensor && playIndex == 2) {
                        hasReceviceSensor = true;
                        playActionFile("红外传感2.hts");
                        playCount = 1;
                    }
                    break;
                case RECEVICE_HEAD:
                    if (!hasReceviceHead && playIndex == 4) {
                        hasReceviceHead = true;
                        playCount = 1;
                        playActionFile("触摸传感2.hts");
                    }
                    break;
                case RECEVICE_VOICE_WAIT:
                    if (!hasReceviceVoice && playIndex == 6) {
                        hasReceviceVoice = true;
                        playCount = 1;
                        playActionFile("麦克2.hts");
                    }
                    break;
                case TAP_HEAD:
                    if(!isShowHibitsDialog){
                        //拍头退出课程模式
                        showTapHeadDialog();
                    }
                    break;
                case BLUETOOTH_DISCONNECT:
                    ToastUtils.showShort(getStringResources("ui_robot_disconnect"));
                    //MainCourseActivity.finishByMySelf();
                    finish();
                    break;
                case OVER_TIME_FINISH:
                    isClickable = true;
                    break;
                case RECIEVE_HIBITS_START:
                    isDoBack = true;
                    MainCourseActivity.showHabitsStartDialog();
                    ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
                    FeatureActivity.this.finish();
                    FeatureActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                    break;
                case LOW_BATTERY_LESS_FIVE:
                    new ConfirmDialog(getContext()).builder()
                            .setMsg(getStringResources("ui_low_battery_less"))
                            .setCancelable(false)
                            .setPositiveButton(getStringResources("ui_common_ok"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //调到主界面
                                    MainCourseActivity.finishByMySelf();
                                    ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
                                    FeatureActivity.this.finish();
                                    FeatureActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                                }
                            }).show();
                    break;
            }

        }
    };

    public static void launchActivity(Activity activity, boolean isFinish) {
        Intent intent = new Intent(activity, FeatureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
    }

    private int getLearnCount() {
        int i = 0;
        if (hasLearnEngine) i++;
        if (hasLearnSensor) i++;
        if (hasLearnSoundbox) i++;
        if (hasLearnHead) i++;
        if (hasLearnEye) i++;
        if (hasLearnVoice) i++;
        if (hasLearnObstacle) i++;
        return i;
    }

    @Override
    protected void initUI() {
        tvNext.setText(getStringResources("ui_setting_principle_end"));
        tvPrincipleSteeringEngineIntro.setText(getStringResources("ui_setting_principle_engine_tip"));
        tvPrincipleInfraredSensorIntro.setText(getStringResources("ui_setting_principle_sensor_tip"));
        tvPrincipleSoundboxIntro.setText(getStringResources("ui_setting_principle_soundbox_tip"));
        tvPrincipleHeadIntro.setText(getStringResources("ui_setting_principle_head_tip"));
        tvPrincipleEyeIntro.setText(getStringResources("ui_setting_principle_eye_tip"));
        tvPrincipleVoiceIntro.setText(getStringResources("ui_setting_principle_voice_tip"));
        tvPrincipleVoiceObstacleAvoidanceIntro.setText(getStringResources("ui_setting_principle_obstacle_tip"));

        ivPrincipleSteeringEngine.setVisibility(View.INVISIBLE);
        ivPrincipleInfraredSensor.setVisibility(View.INVISIBLE);
        ivPrincipleSoundbox.setVisibility(View.INVISIBLE);
        ivPrincipleHead.setVisibility(View.INVISIBLE);
        ivPrincipleEye.setVisibility(View.INVISIBLE);
        ivPrincipleVoice.setVisibility(View.INVISIBLE);
        ivPrincipleVoiceObstacleAvoidance.setVisibility(View.INVISIBLE);

        containerWidth = this.getResources().getDisplayMetrics().widthPixels;
        containerHeight = this.getResources().getDisplayMetrics().heightPixels;
        mFloatAnimator = FloatAnimator.getIntanse();
        mFloatAnimator.setDistance(20F);
        mFloatAnimator.clear();
        mCurrentProgress = SPUtils.getInstance().getInt(Constant.PRINCIPLE_PROGRESS + SPUtils.getInstance().getString(Constant.SP_USER_ID), 0);
        if (mCurrentProgress < 4) {
            setViewEnable(tvNext, false, 0.5f);
        }

        biggerLeftBottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_left_bottom);
        smallerLeftBottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_left_bottom);

        biggerLeftTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_left_top);
        smallerLeftTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_left_top);

        biggerRightTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_right_top);
        smallerRightTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_right_top);

        radiologicalWaveAnim = (AnimationDrawable)ivRadiologicalWave.getBackground();
        radiologicalWaveAnim.setOneShot(false);
        radiologicalWaveAnim.setVisible(true,true);

        UbtLog.d(TAG, "scale = " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

        if(AlphaApplication.isPad()){
            mPadDelayTime = 130;
        }

        rlRobot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (rlRobot != null) {
                    rlRobot.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!hasInitRobot && ivRobot.getHeight() > 0) {
                                hasInitRobot = true;
                                initRobot();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mHelper.isStartHibitsProcess()){
            isShowHibitsDialog = true;
            mHelper.showStartHibitsProcess(new IDismissCallbackListener() {
                @Override
                public void onDismissCallback(Object obj) {
                    isShowHibitsDialog = false;
                    UbtLog.d("onDismissCallback","obj = " +obj);
                    if((boolean)obj){
                        //行为习惯流程未结束，退出当前流程
                        isDoBack = true;
                        ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
                        FeatureActivity.this.finish();
                        FeatureActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                    }else {
                        //行为习惯流程结束，该干啥干啥
                        mHandler.sendEmptyMessage(SHOW_BEZIER_VIEW);
                    }
                }
            });
        }else {
            //行为习惯流程未开始，该干啥干啥
            ((PrincipleHelper) mHelper).doInit();
            ((PrincipleHelper) mHelper).doEnterCourse((byte) 1);
            mHandler.sendEmptyMessage(SHOW_BEZIER_VIEW);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isDoBack){
            reset();
        }
        showView(tvMsgShow, false, null);
        showView(rlPrincipleSteeringEngineIntro,false,null);
        showView(rlPrincipleInfraredSensorIntro,false,null);
        showView(rlPrincipleSoundboxIntro,false,null);
        showView(rlPrincipleHeadIntro,false,null);
        showView(rlPrincipleEyeIntro,false,null);
        showView(rlPrincipleVoiceIntro,false,null);
        showView(rlPrincipleVoiceObstacleAvoidanceIntro,false,null);
    }

    private void showBezierView(){
        startTime = new Date(System.currentTimeMillis());

        showView(bzvPrincipleSteeringEngine, 0);
        showView(bzvPrincipleInfraredSensor, 300);
        showView(bzvPrincipleSoundbox, 600);
        showView(bzvPrincipleHead, 900);
        showView(bzvPrincipleEye, 1200);
        showView(bzvPrincipleVoice, 1500);
        showView(bzvPrincipleVoiceObstacleAvoidance, 1800);
    }

    private void showView(View view, int delayTime) {
        Message msg = new Message();
        msg.what = SHOW_VIEW;
        msg.arg1 = view.getId();
        mHandler.sendMessageDelayed(msg, delayTime);
    }

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {

        if (event.getEvent() == PrincipleEvent.Event.PLAY_ACTION_FINISH) {

            mHandler.sendEmptyMessage(PLAY_ACTION_FINISH);

            if(mHandler.hasMessages(OVER_TIME_FINISH)){
                mHandler.removeMessages(OVER_TIME_FINISH);
                isClickable = true;
            }
        } else if (event.getEvent() == PrincipleEvent.Event.CALL_GET_INFRARED_DISTANCE) {
            int infraredDistance = event.getInfraredDistance();
            UbtLog.d(TAG, "infraredDistance = " + infraredDistance);
            if (infraredDistance > 0 && infraredDistance < 30) {
                mHandler.sendEmptyMessage(RECEVICE_SENSOR);
            }
        } else if (event.getEvent() == PrincipleEvent.Event.CALL_CLICK_HEAD) {
            int status = event.getStatus();
            UbtLog.d(TAG, "CALL_CLICK_HEAD = " + status);
            if (status == 2) {
                mHandler.sendEmptyMessage(RECEVICE_HEAD);
            }
        } else if (event.getEvent() == PrincipleEvent.Event.VOICE_WAIT) {
            mHandler.sendEmptyMessage(RECEVICE_VOICE_WAIT);
        } else if (event.getEvent() == PrincipleEvent.Event.TAP_HEAD) {
            if (!hasLearnHeadIng) {
                mHandler.sendEmptyMessage(TAP_HEAD);
            }
        }else if(event.getEvent() == PrincipleEvent.Event.DISCONNECTED){
            mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECT);
        }
    }

    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS && !isShowHibitsDialog){
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            if(event.isHibitsProcessStatus() && !hasReceiveHibitsStart){
                hasReceiveHibitsStart = true;
                mHandler.sendEmptyMessage(RECIEVE_HIBITS_START);
            }
        }else if(event.getEvent() == RobotEvent.Event.LOW_BATTERY_LESS_FIVE_PERCENT){
            mHandler.sendEmptyMessage(LOW_BATTERY_LESS_FIVE);
        }
    }

    private void reset() {
        playIndex = 0;
        playCount = 0;
        hasLearnHeadIng = false;
        setAllEnable(true);
        stopRadiologicalWaveAnim();
        ((PrincipleHelper) mHelper).doInit();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        scale = (int) this.getResources().getDisplayMetrics().density;
        int screenWidth = SizeUtils.getScreenWidth(this);
        int screenHeight = SizeUtils.getScreenHeight(this);
        UbtLog.d(TAG, "screenWidth = " + screenWidth + "screenHeight = " + screenHeight + " scale = " + scale);
        if ((screenWidth >= 1920 && scale == 2) || (screenHeight >= 1080 && scale == 2)) {
            scale = 3;
        }

        if (scale == 3) {
            return R.layout.fragment_robot_feature_3;
        } else if (scale == 4) {
            return R.layout.fragment_robot_feature_4;
        } else {
            return R.layout.fragment_robot_feature;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mHelper = new PrincipleHelper(this);
        initUI();
    }

    @Override
    protected void onDestroy() {

        if(mHandler.hasMessages(OVER_TIME_FINISH)){
            mHandler.removeMessages(OVER_TIME_FINISH);
        }

        if (mHandler.hasMessages(SHOW_VIEW)) {
            mHandler.removeMessages(SHOW_VIEW);
        }

        if (mHandler.hasMessages(SHOW_BEZIER_VIEW)) {
            mHandler.removeMessages(SHOW_BEZIER_VIEW);
        }

        if (mHandler.hasMessages(ENABLE_ALL_VIEW)) {
            mHandler.removeMessages(ENABLE_ALL_VIEW);
        }
        if (mHandler.hasMessages(LEARN_FINISH)) {
            mHandler.removeMessages(LEARN_FINISH);
        }

        super.onDestroy();
    }

    private void initRobot() {

        if (scale == 2.0) {

            /*initViewLayout(ivRobot, scale, 0);

            initViewLayout(ivHandLeft, scale, 110);

            initViewLayout(ivHandRight, scale, 110);

            initViewLayout(ivLegLeft, scale, -36);

            initViewLayout(ivLegRight, scale, -36);

            initViewLayout(ivPrincipleSteeringEngine, scale, 0);

            initViewLayout(ivPrincipleInfraredSensor, scale, 0);

            initViewLayout(ivPrincipleSoundbox, scale, 0);

            initViewLayout(ivPrincipleHead, scale, 0);

            initViewLayout(ivPrincipleEye, scale, 0);

            initViewLayout(ivPrincipleVoice, scale, 0);

            initViewLayout(ivPrincipleVoiceObstacleAvoidance, scale, 0);
*/
        }

        if(AlphaApplication.isPad()){
            float densityRatio = 3.0f / SizeUtils.getDensity(getContext());

            RelativeLayout.LayoutParams ivEngineParams = (RelativeLayout.LayoutParams) ivPrincipleSteeringEngine.getLayoutParams();
            ivEngineParams.topMargin = (int)(ivEngineParams.topMargin * densityRatio);
            ivEngineParams.rightMargin = (int)(ivEngineParams.rightMargin * densityRatio);
            ivPrincipleSteeringEngine.setLayoutParams(ivEngineParams);

            RelativeLayout.LayoutParams ivSensorParams = (RelativeLayout.LayoutParams) ivPrincipleInfraredSensor.getLayoutParams();
            ivSensorParams.rightMargin = (int)(ivSensorParams.rightMargin * densityRatio);
            ivPrincipleInfraredSensor.setLayoutParams(ivSensorParams);

            RelativeLayout.LayoutParams ivSoundboxParams = (RelativeLayout.LayoutParams) ivPrincipleSoundbox.getLayoutParams();
            ivSoundboxParams.rightMargin = (int)(ivSoundboxParams.rightMargin * densityRatio);
            ivSoundboxParams.bottomMargin = (int)(ivSoundboxParams.bottomMargin * densityRatio);
            ivPrincipleSoundbox.setLayoutParams(ivSoundboxParams);

            RelativeLayout.LayoutParams ivHeadParams = (RelativeLayout.LayoutParams) ivPrincipleHead.getLayoutParams();
            ivHeadParams.bottomMargin = (int)(ivHeadParams.bottomMargin * densityRatio);
            ivPrincipleHead.setLayoutParams(ivHeadParams);

            RelativeLayout.LayoutParams ivEyeParams = (RelativeLayout.LayoutParams) ivPrincipleEye.getLayoutParams();
            ivEyeParams.leftMargin = (int)(ivEyeParams.leftMargin * densityRatio);
            ivEyeParams.bottomMargin = (int)(ivEyeParams.bottomMargin * densityRatio);
            ivPrincipleEye.setLayoutParams(ivEyeParams);

            RelativeLayout.LayoutParams ivVoiceParams = (RelativeLayout.LayoutParams) ivPrincipleVoice.getLayoutParams();
            ivVoiceParams.leftMargin = (int)(ivVoiceParams.leftMargin * densityRatio);
            ivPrincipleVoice.setLayoutParams(ivVoiceParams);

            RelativeLayout.LayoutParams ivAvoidanceParams = (RelativeLayout.LayoutParams) ivPrincipleVoiceObstacleAvoidance.getLayoutParams();
            ivAvoidanceParams.topMargin = (int)(ivAvoidanceParams.topMargin * densityRatio);
            ivAvoidanceParams.leftMargin = (int)(ivAvoidanceParams.leftMargin * densityRatio);
            ivPrincipleVoiceObstacleAvoidance.setLayoutParams(ivAvoidanceParams);

        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViewLine(ivRobot, ivPrincipleSteeringEngine);
                initViewLine(ivRobot, ivPrincipleInfraredSensor);
                initViewLine(ivRobot, ivPrincipleSoundbox);
                initViewLine(ivRobot, ivPrincipleHead);
                initViewLine(ivRobot, ivPrincipleEye);
                initViewLine(ivRobot, ivPrincipleVoice);
                initViewLine(ivRobot, ivPrincipleVoiceObstacleAvoidance);
                hasInitBezierView = true;

            }
        }, mPadDelayTime);

        bzvPrincipleSteeringEngine.setCallbackListence(this);
        bzvPrincipleInfraredSensor.setCallbackListence(this);
        bzvPrincipleSoundbox.setCallbackListence(this);
        bzvPrincipleHead.setCallbackListence(this);
        bzvPrincipleEye.setCallbackListence(this);
        bzvPrincipleVoice.setCallbackListence(this);
        bzvPrincipleVoiceObstacleAvoidance.setCallbackListence(this);
    }

    /**
     * 播放点定位动画显示
     */
    private void startRadiologicalWaveAnim(){

        PointF startPoints = null;
        if(playIndex == 1){
            startPoints = bzvPrincipleSteeringEngine.getStartPoints();
        }else if(playIndex == 2){
            startPoints = bzvPrincipleInfraredSensor.getStartPoints();
        }else if(playIndex == 3){
            startPoints = bzvPrincipleSoundbox.getStartPoints();
        }else if(playIndex == 4){
            startPoints = bzvPrincipleHead.getStartPoints();
        }else if(playIndex == 5){
            startPoints = bzvPrincipleEye.getStartPoints();
        }else if(playIndex == 6){
            startPoints = bzvPrincipleVoice.getStartPoints();
        }else if(playIndex == 7){
            startPoints = bzvPrincipleVoiceObstacleAvoidance.getStartPoints();
        }
        if(startPoints != null){
            RelativeLayout.LayoutParams startPointShowParams = (RelativeLayout.LayoutParams) ivRadiologicalWave.getLayoutParams();
            startPointShowParams.leftMargin = (int) startPoints.x - ivRadiologicalWave.getWidth()/2;
            startPointShowParams.topMargin = (int) startPoints.y - ivRadiologicalWave.getHeight()/2;
            UbtLog.d(TAG,"startPointShowParams.leftMargin = " + startPointShowParams.leftMargin + "/" + startPointShowParams.topMargin);
            ivRadiologicalWave.setLayoutParams(startPointShowParams);
            ivRadiologicalWave.setVisibility(View.VISIBLE);
            radiologicalWaveAnim.start();
        }
    }

    /**
     * 停止播放定位动物
     */
    private void stopRadiologicalWaveAnim(){
        radiologicalWaveAnim.stop();
        ivRadiologicalWave.setVisibility(View.GONE);
    }

    /**
     * 显示拍头弹出框
     */
    private void showTapHeadDialog(){
        if(mTapHeadDialog == null){
            mTapHeadDialog = new ConfirmDialog(getContext()).builder()
                    .setMsg(getStringResources("ui_course_principle_exit_tip"))
                    .setCancelable(false)
                    .setPositiveButton(getStringResources("ui_common_yes"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            MainCourseActivity.finishByMySelf();
                            exitPrincipleProcess();
                        }
                    }).setNegativeButton(getStringResources("ui_common_no"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        }
        if(!mTapHeadDialog.isShowing()){
            mTapHeadDialog.show();
        }
    }

    /**
     * 初始化Layout布局
     * @param view
     * @param scale
     * @param margetTop
     */
    private void initViewLayout(View view, int scale, int margetTop) {

        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        UbtLog.d(TAG, "width start :: " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " + params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft());

        params.width = view.getWidth() / 2 * scale;
        params.height = view.getHeight() / 2 * scale;
        if (margetTop != 0) {
            params.topMargin = margetTop / 2 * scale;
        }
        view.setLayoutParams(params);
        int newLeft = view.getLeft() - (params.width - view.getWidth()) / 2;
        int newTop = view.getTop() - (params.height - view.getHeight()) / 2;

        view.layout(newLeft, newTop, newLeft + params.width, newTop + params.height);

    }

    /**
     * 初始化虚线定位点
     * @param startView
     * @param targetView
     */
    private void initViewLine(View startView, View targetView) {
        UbtLog.d(TAG, "targetView = " + targetView);
        int[] startPos = getlocation(startView);
        int[] targetPos = getlocation(targetView);
        int startX, startY, targetX, targetY;
        if (targetView.getId() == R.id.iv_principle_steering_engine) {
            startX = startPos[0] + startView.getWidth() / 4;
            startY = startPos[1] + startView.getHeight() - 14 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleSteeringEngine.addPoint(startX, startY);
            bzvPrincipleSteeringEngine.addPoint(targetX + (startX - targetX) / 2, targetY - (targetY - startY) / 2 - 20 * scale);
            bzvPrincipleSteeringEngine.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_infrared_sensor) {

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + startView.getHeight() / 2 + 26 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleInfraredSensor.addPoint(startX, startY);
            bzvPrincipleInfraredSensor.addPoint(startX - 40 * scale, startY);
            bzvPrincipleInfraredSensor.addPoint(startX - 45 * scale, startY - 20 * scale);
            bzvPrincipleInfraredSensor.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_soundbox) {

            startX = startPos[0] + startView.getWidth() / 2 - 46 / 2 * scale;
            startY = startPos[1] + 58 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleSoundbox.addPoint(startX, startY);
            bzvPrincipleSoundbox.addPoint(startX - 30 * scale, startY);
            bzvPrincipleSoundbox.addPoint(targetX + (startX - targetX) / 2, targetY + 60 * scale);
            bzvPrincipleSoundbox.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_head) {

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + 5 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleHead.addPoint(startX, startY);
            bzvPrincipleHead.addPoint(startX - 5 * scale, startY - 5 * scale);
            bzvPrincipleHead.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_eye) {

            startX = startPos[0] + startView.getWidth() / 2 + 24 / 2 * scale;
            startY = startPos[1] + 58 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleEye.addPoint(startX, startY);
            bzvPrincipleEye.addPoint(targetX - 10 * scale, targetY + 50 * scale);
            bzvPrincipleEye.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_voice) {

            startX = startPos[0] + startView.getWidth() / 2 + 46 / 2 * scale;
            startY = startPos[1] + 126 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleVoice.addPoint(startX, startY);
            bzvPrincipleVoice.addPoint(targetX - (targetX - startX) / 2, startY);
            bzvPrincipleVoice.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_voice_obstacle_avoidance) {

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + 200 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleVoiceObstacleAvoidance.addPoint(startX, startY);
            bzvPrincipleVoiceObstacleAvoidance.addPoint(targetX - (targetX - startX) / 2, targetY - (targetY - startY) / 2 - 20 * scale);
            bzvPrincipleVoiceObstacleAvoidance.addPoint(targetX, targetY);
        }
    }

    /**
     * 获取View在屏幕的位置
     * @param view
     * @return
     */
    private int[] getlocation(View view) {
        int x, y;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        x = location[0];
        y = location[1];

        UbtLog.d(TAG, "Screenx--->>" + x + "  " + "Screeny--->" + y + "     = " + view.getLeft() + "    " + view.getTop());
        return location;
    }


    @OnClick({R.id.iv_back,
            R.id.tv_next,
            R.id.tv_msg_show,
            R.id.iv_principle_steering_engine,
            R.id.iv_principle_infrared_sensor,
            R.id.iv_principle_soundbox,
            R.id.iv_principle_head,
            R.id.iv_principle_eye,
            R.id.iv_principle_voice,
            R.id.iv_principle_voice_obstacle_avoidance,
            R.id.rl_principle_steering_engine_intro,
            R.id.rl_principle_infrared_sensor_intro,
            R.id.rl_principle_soundbox_intro,
            R.id.rl_principle_head_intro,
            R.id.rl_principle_eye_intro,
            R.id.rl_principle_voice_intro,
            R.id.rl_principle_voice_obstacle_avoidance_intro
    })
    public void onViewClicked(View view) {
        UbtLog.d(TAG, "view = " + view);
        switch (view.getId()) {
            case R.id.iv_back:
                onBack();
                break;
            case R.id.tv_next:
                if (mCurrentProgress < 4 && getLearnCount() < 7) {
                    new ConfirmDialog(getContext()).builder()
                            .setMsg(getStringResources("ui_setting_principle_skip_tip"))
                            .setCancelable(true)
                            .setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    doSaveCourseProgress(1, 1, 4);
                                    exitPrincipleProcess();
                                }
                            }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                } else {
                    exitPrincipleProcess();
                }
                break;

            case R.id.iv_principle_steering_engine:
                if(playIndex == 0 ){//播放完成之后才能播
                    if (tvMsgShow.getVisibility() == View.VISIBLE) {
                        showView(tvMsgShow, false, smallerLeftBottomAnim);
                    }
                    startPlayActionFile(ivPrincipleSteeringEngine, 1, "舵机.hts");
                    showView(rlPrincipleSteeringEngineIntro, true, biggerLeftBottomAnim);
                    setViewEnable(bzvPrincipleSteeringEngine, true, 1);
                    startRadiologicalWaveAnim();
                    ivPrincipleSteeringEngine.bringToFront();
                }
                break;
            case R.id.iv_principle_infrared_sensor:
                if(playIndex == 0 ){
                    hasReceviceSensor = false;
                    tvMsgShow.setText(getStringResources("ui_principle_sensor_tips"));
                    startPlayActionFile(ivPrincipleInfraredSensor, 2, "红外传感1.hts");
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    setViewEnable(bzvPrincipleInfraredSensor, true, 1);
                    ((PrincipleHelper) mHelper).doReadInfraredSensor((byte) 1);
                    ivPrincipleInfraredSensor.bringToFront();
                }
                break;
            case R.id.iv_principle_soundbox:
                if(playIndex == 0 ){
                    if (tvMsgShow.getVisibility() == View.VISIBLE) {
                        showView(tvMsgShow, false, smallerLeftBottomAnim);
                    }
                    startPlayActionFile(ivPrincipleSoundbox, 3, "扬声器.hts");
                    showView(rlPrincipleSoundboxIntro, true, biggerRightTopAnim);
                    setViewEnable(bzvPrincipleSoundbox, true, 1);
                    startRadiologicalWaveAnim();
                    ivPrincipleSoundbox.bringToFront();
                }
                break;
            case R.id.iv_principle_head:
                if(playIndex == 0 ){
                    hasLearnHeadIng = true;
                    hasReceviceHead = false;
                    tvMsgShow.setText(getStringResources("ui_principle_head_tips"));
                    startPlayActionFile(ivPrincipleHead, 4, "触摸传感1.hts");
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    setViewEnable(bzvPrincipleHead, true, 1);
                    ((PrincipleHelper) mHelper).doReadHeadClick((byte) 1);
                    ivPrincipleHead.bringToFront();
                }
                break;
            case R.id.iv_principle_eye:
                if(playIndex == 0 ){
                    if (tvMsgShow.getVisibility() == View.VISIBLE) {
                        showView(tvMsgShow, false, smallerLeftBottomAnim);
                    }
                    startPlayActionFile(ivPrincipleEye, 5, "眼睛LED.hts");
                    showView(rlPrincipleEyeIntro, true, biggerLeftTopAnim);
                    setViewEnable(bzvPrincipleEye, true, 1);
                    startRadiologicalWaveAnim();
                    ivPrincipleEye.bringToFront();
                }
                break;
            case R.id.iv_principle_voice:
                if(playIndex == 0 ){
                    hasReceviceVoice = false;
                    tvMsgShow.setText(getStringResources("ui_principle_voice_tips"));
                    startPlayActionFile(ivPrincipleVoice, 6, "麦克1.hts");
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    setViewEnable(bzvPrincipleVoice, true, 1);
                    ivPrincipleVoice.bringToFront();
                }
                break;
            case R.id.iv_principle_voice_obstacle_avoidance:
                if(playIndex == 0 ){
                    tvMsgShow.setText(getStringResources("ui_principle_obstacle_tips"));
                    startPlayActionFile(ivPrincipleVoiceObstacleAvoidance, 7, "百宝1.hts");
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    setViewEnable(bzvPrincipleVoiceObstacleAvoidance, true, 1);
                    ivPrincipleVoiceObstacleAvoidance.bringToFront();
                }
                break;
            case R.id.tv_msg_show:
                if(isClickable){
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_steering_engine_intro:
                if(isClickable){
                    showView(rlPrincipleSteeringEngineIntro, false, smallerLeftBottomAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_infrared_sensor_intro:
                if(isClickable){
                    showView(rlPrincipleInfraredSensorIntro, false, smallerRightTopAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_soundbox_intro:
                if(isClickable){
                    showView(rlPrincipleSoundboxIntro, false, smallerRightTopAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_head_intro:
                if(isClickable){
                    showView(rlPrincipleHeadIntro, false, smallerLeftTopAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_eye_intro:
                if(isClickable){
                    showView(rlPrincipleEyeIntro, false, smallerLeftTopAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_voice_intro:
                if(isClickable){
                    showView(rlPrincipleVoiceIntro, false, smallerLeftBottomAnim);
                    reset();
                }
                break;
            case R.id.rl_principle_voice_obstacle_avoidance_intro:
                if(isClickable){
                    showView(rlPrincipleVoiceObstacleAvoidanceIntro, false, smallerLeftBottomAnim);
                    reset();
                }
                break;

        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() ==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回
     */
    public void onBack() {

        Date curDate = new Date(System.currentTimeMillis());
        long diffTime = (curDate.getTime() - startTime.getTime());
        UbtLog.d(TAG,"onBack diffTime = " + diffTime);
        if(startTime != null && diffTime < 2200 ){
            UbtLog.d(TAG,"onBack = " + diffTime);
            return;
        }

        isDoBack = true;
        if(SPUtils.getInstance().getInt(Constant.PRINCIPLE_ENTER_PROGRESS, 0) > 2 ){
            exitPrincipleProcess();
        }else {
            ((PrincipleHelper) mHelper).doInit();
            MergeActivity.launchActivity(this, true);
            this.finish();
        }
    }

    private void exitPrincipleProcess(){
        ((PrincipleHelper) mHelper).doInit();
        ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
        this.finish();
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
    }

    /**
     * 播放动作文件
     * @param view 点击的View
     * @param index 点击的View的索引
     * @param actionFile 播放的文件名
     */
    private void startPlayActionFile(View view, int index, String actionFile) {
        playCount = 0;
        playIndex = index;
        setAllEnable(false);
        setViewEnable(view, true, 1);
        playActionFile(actionFile);


    }

    /**
     * 播放动作文件
     * @param acitonFile 播放的文件名
     */
    private void playActionFile(String acitonFile) {
        ((PrincipleHelper) mHelper).playFile(acitonFile);
        if(mHandler.hasMessages(OVER_TIME_FINISH)){
            mHandler.removeMessages(OVER_TIME_FINISH);
        }

        isClickable = false;
        mHandler.sendEmptyMessageDelayed(OVER_TIME_FINISH, OVER_TIME);
    }

    /**
     * 设置View的透明度
     * @param enable
     */
    private void setAllEnable(boolean enable) {
        setViewEnable(ivPrincipleSteeringEngine, enable, 0.3f);
        setViewEnable(ivPrincipleInfraredSensor, enable, 0.3f);
        setViewEnable(ivPrincipleSoundbox, enable, 0.3f);
        setViewEnable(ivPrincipleHead, enable, 0.3f);
        setViewEnable(ivPrincipleEye, enable, 0.3f);
        setViewEnable(ivPrincipleVoice, enable, 0.3f);
        setViewEnable(ivPrincipleVoiceObstacleAvoidance, enable, 0.3f);

        setViewEnable(bzvPrincipleSteeringEngine, enable, 0);
        setViewEnable(bzvPrincipleInfraredSensor, enable, 0);
        setViewEnable(bzvPrincipleSoundbox, enable, 0);
        setViewEnable(bzvPrincipleHead, enable, 0);
        setViewEnable(bzvPrincipleEye, enable, 0);
        setViewEnable(bzvPrincipleVoice, enable, 0);
        setViewEnable(bzvPrincipleVoiceObstacleAvoidance, enable, 0);
    }

    /**
     * 设置View的透明度
     * @param mView 设置的View
     * @param enable 是否可点击
     * @param alpha 透明度
     */
    private void setViewEnable(View mView, boolean enable, float alpha) {
        //mView.setEnabled(enable);
        mView.setClickable(enable);
        if (enable) {
            mView.setAlpha(1f);
        } else {
            mView.setAlpha(alpha);
        }
    }


    /**
     * 显示隐藏View
     * @param view 操作的View
     * @param isShow 显示或隐藏
     * @param anim 显示或隐藏的动画
     */
    private void showView(View view, boolean isShow, Animation anim) {
        if (view.getVisibility() == View.VISIBLE && isShow) {
            return;
        }

        if (view.getVisibility() != View.VISIBLE && !isShow) {
            return;
        }

        if (isShow) {

            if (rlPrincipleSteeringEngineIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleSteeringEngineIntro.setVisibility(View.GONE);
            }

            if (rlPrincipleInfraredSensorIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleInfraredSensorIntro.setVisibility(View.GONE);
            }

            if (rlPrincipleSoundboxIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleSoundboxIntro.setVisibility(View.GONE);
            }

            if (rlPrincipleHeadIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleHeadIntro.setVisibility(View.GONE);
            }

            if (rlPrincipleEyeIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleEyeIntro.setVisibility(View.GONE);
            }

            if (rlPrincipleVoiceIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleVoiceIntro.setVisibility(View.GONE);
            }

            if (rlPrincipleVoiceObstacleAvoidanceIntro.getVisibility() == View.VISIBLE) {
                rlPrincipleVoiceObstacleAvoidanceIntro.setVisibility(View.GONE);
            }

            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                tvMsgShow.setVisibility(View.GONE);
            }

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        if (anim != null) {
            view.startAnimation(anim);
        }
    }

    /**
     * 虚线动画完成回调
     * @param view
     */
    @Override
    public void onAnimatorEnd(View view) {
        //UbtLog.d(TAG, "onAnimatorEnd = " + view);
        Message msg = new Message();
        msg.what = BEZIER_ANIMATOR_FINISH;
        msg.arg1 = view.getId();
        mHandler.sendMessage(msg);
    }

    /**
     * 获取课程进度
     *
     * @param type
     */
    public void doGetCourseProgress(int type) {
        mPresenter.doGetCourseProgress(type);
    }

    /**
     * 保存课程进度
     *
     * @param type
     * @param courseOne
     * @param progressOne
     */
    public void doSaveCourseProgress(int type, int courseOne, int progressOne) {
        mPresenter.doSaveCourseProgress(type, courseOne, progressOne);
    }

    @Override
    public void onSaveCourseProgress(boolean isSuccess, String msg) {

    }

    @Override
    public void onGetCourseProgress(boolean isSuccess, String msg, int progress) {

    }
}
