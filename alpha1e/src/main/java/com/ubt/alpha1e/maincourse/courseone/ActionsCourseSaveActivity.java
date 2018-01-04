package com.ubt.alpha1e.maincourse.courseone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;
import com.ubt.alpha1e.base.FileUtils;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.ImageTools;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.maincourse.adapter.CourseArrowAminalUtil;
import com.ubt.alpha1e.net.http.basic.FileUploadListener;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.DubActivity;
import com.ubt.alpha1e.ui.custom.EditTextCheck;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActionsCourseSaveActivity extends BaseActivity implements
        IEditActionUI, FileUploadListener, BaseDiaUI {

    private static final String TAG = "ActionsEditSaveActivity";

    private NewActionInfo mCurrentAction;


    private ImageView img_action_logo;
    private RelativeLayout lay_head_sel;
    private TextView txt_shooting;
    private TextView txt_from_file;
    private TextView txt_cancel;
    private Uri mImageUri;
    private Bitmap mCurrentActionImg = null;

    private EditText edt_name;
    private EditText edt_disc;

    private GridView mGridView;
    private SimpleAdapter simpleAdapter;
    private TextView txt_actions_type_des;
    private TextView txt_action_type;
    private int[] imageIds = {R.drawable.mynew_publish_dance, R.drawable.mynew_publish_story, R.drawable.myniew_publish_sport,
            R.drawable.mynew_publish_childsong, R.drawable.mynew_publish_science};
    private String[] desHintKeys = {"ui_distribute_desc_dance",
            "ui_distribute_desc_story",
            "ui_distribute_desc_sport",
            "ui_distribute_desc_song",
            "ui_distribute_desc_education"};

    private LoadingDialog mLoadingDialog;

    private int actionType = -1;

    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    private String mSchemeId = "";
    private String mSchemeName = "";
    private long dubTag = -1;
    private int type = -1;
    private int fromType = -1;
    private ImageView ivDemo1, ivDemo2, ivDemo3;
    //初始默认横竖屛默认值 默认为竖屏
    private int mScreenOrientation = 1;

    private String musicDir = "";
    public static String MUSIC_DIR = "music_dir";

    private ImageView ivBack;
    private ImageView ivSave;

    private ImageView ivSaveArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_actions_new_edit_save);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_actions_edit_save);
        }

        mCurrentAction = getIntent().getParcelableExtra(ActionsEditHelper.NewActionInfo);//get parcelable object
        mSchemeId = getIntent().getStringExtra(SCHEME_ID);
        mSchemeName = getIntent().getStringExtra(SCHEME_NAME);
        dubTag = getIntent().getLongExtra(DubActivity.DUB_TAG, -1);
        UbtLog.d(TAG, "dubTag=" + dubTag);
        type = getIntent().getIntExtra(DubActivity.ACTION_TYPE, -1);

        fromType = getIntent().getIntExtra(BaseActionEditLayout.FROM_TYPE, 0);
        if (dubTag != -1) {
            mHelper = new ActionsEditHelper(this, this, dubTag);
        } else {
            mHelper = new ActionsEditHelper(this, this);
        }

        musicDir = getIntent().getStringExtra(MUSIC_DIR);
        UbtLog.d(TAG, "musicDir:" + musicDir);

        initUI();
        initControlListener();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(ActionsCourseSaveActivity.class.getSimpleName());

        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing() && !this.isFinishing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing() && !this.isFinishing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void initUI() {

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivSave = (ImageView) findViewById(R.id.iv_save);
        ivSave.setEnabled(false);
        ivSaveArrow = (ImageView) findViewById(R.id.iv_save_arrow);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               // saveNewAction();
                CourseArrowAminalUtil.startViewAnimal(false, ivSaveArrow, 1);
                Intent intent = new Intent();
                intent.putExtra(ActionsEditHelper.SaveActionResult, true);
                setResult(ActionsEditHelper.SaveActionReq, intent);
                finish();
            }
        });

        mGridView = (GridView) findViewById(R.id.grid_actions_type);
        txt_actions_type_des = (TextView) findViewById(R.id.txt_action_type_des);
        txt_action_type = (TextView) findViewById(R.id.txt_action_type);
        img_action_logo = (ImageView) findViewById(R.id.img_action_logo);
        lay_head_sel = (RelativeLayout) findViewById(R.id.lay_head_sel);
        txt_shooting = (TextView) findViewById(R.id.txt_shooting);
        txt_from_file = (TextView) findViewById(R.id.txt_from_file);
        txt_cancel = (TextView) findViewById(R.id.txt_del);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 这里可以知道你已经输入的字数，大家可以自己根据需求来自定义文本控件实时的显示已经输入的字符个数
                Log.e("此时你已经输入了", "" + s.length());

                int after_length = s.length();// 输入内容后编辑框所有内容的总长度
                // 如果字符添加后超过了限制的长度，那么就移除后面添加的那一部分，这个很关键
                if (after_length > 1) {
                    edt_name.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_name.getWindowToken(), 0); //强制隐藏键盘
                    isShow = true;
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        });
        edt_disc = (EditText) findViewById(R.id.edt_disc);
        initGrids(this);

        if (mCurrentAction.actionId != -1) {
            edt_name.setText(mCurrentAction.actionName);
            edt_disc.setText(mCurrentAction.actionDesciber);
            ((ActionsEditHelper) mHelper).readImg(mCurrentAction.actionId,
                    mCurrentAction.actionImagePath);
        }
        generateBitmap(this);
        mLoadingDialog = LoadingDialog.getInstance(this, this);

        if (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ivDemo1 = (ImageView) findViewById(R.id.iv_demo1);
            ivDemo2 = (ImageView) findViewById(R.id.iv_demo2);
            ivDemo3 = (ImageView) findViewById(R.id.iv_demo3);

            ivDemo1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentActionImg = null;
                    int imageId = R.drawable.action_dance_1b;
                    if (actionType == 1) {
                        imageId = R.drawable.action_dance_1b;
                    } else if (actionType == 2) {
                        imageId = R.drawable.action_story_1b;
                    } else if (actionType == 3) {
                        imageId = R.drawable.action_sport_1b;
                    } else if (actionType == 4) {
                        imageId = R.drawable.action_er_1b;
                    } else if (actionType == 5) {
                        imageId = R.drawable.action_science_1b;
                    }
                    mCurrentActionImg = getBitmap(imageId);
                    img_action_logo.setImageResource(imageId);
                }
            });

            ivDemo2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentActionImg = null;
                    int imageId = R.drawable.action_dance_2b;
                    if (actionType == 1) {
                        imageId = R.drawable.action_dance_2b;
                    } else if (actionType == 2) {
                        imageId = R.drawable.action_story_2b;
                    } else if (actionType == 3) {
                        imageId = R.drawable.action_sport_2b;
                    } else if (actionType == 4) {
                        imageId = R.drawable.action_er_2b;
                    } else if (actionType == 5) {
                        imageId = R.drawable.action_science_2b;
                    }

                    mCurrentActionImg = getBitmap(imageId);
                    img_action_logo.setImageResource(imageId);
                }
            });

            ivDemo3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentActionImg = null;
                    int imageId = R.drawable.action_dance_3b;
                    if (actionType == 1) {
                        imageId = R.drawable.action_dance_3b;
                    } else if (actionType == 2) {
                        imageId = R.drawable.action_story_3b;
                    } else if (actionType == 3) {
                        imageId = R.drawable.action_sport_3b;
                    } else if (actionType == 4) {
                        imageId = R.drawable.action_er_3b;
                    } else if (actionType == 5) {
                        imageId = R.drawable.action_science_3b;

                    }

                    mCurrentActionImg = getBitmap(imageId);
                    img_action_logo.setImageResource(imageId);
                }
            });
        }

        if (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mCurrentActionImg = getBitmap(R.drawable.action_dance_1b);
            img_action_logo.setImageResource(R.drawable.action_dance_1b);
        }

    }

    private Bitmap getBitmap(int imageId) {
        Bitmap bitmap = ImageTools.compressImage(getResources(), imageId, 2);

        return bitmap;
    }

    private void showProgress() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.getInstance(this, this);
        }

        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void dismissProgress() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing() && !this.isFinishing()) {
            mLoadingDialog.cancel();
        }

    }

    boolean isShow;

    private void initGrids(Context ctx) {
        final ArrayList<Map<String, Object>> listItems = new ArrayList<>();
        final String[] imageNames = {
                getStringResources("ui_square_dance"),
                getStringResources("ui_square_story"),
                getStringResources("ui_square_sport"),
                getStringResources("ui_square_childrensong"),
                getStringResources("ui_square_science")};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("image", imageIds[i]);
            item.put("name", imageNames[i]);
            item.put("select", false);
            listItems.add(item);
        }
        simpleAdapter = new SimpleAdapter(ctx, listItems, R.layout.layout_actions_type_select,
                new String[]{
                        "image", "name", "select"
                }, new int[]{
                R.id.img_type_item, R.id.txt_action_select_type, R.id.img_select_item
        }) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View thiz = super.getView(position, convertView, parent);
                ImageView img = (ImageView) thiz.findViewById(R.id.img_select_item);
                if ((boolean) listItems.get(position).get("select")) {
                    img.setImageResource(R.drawable.mynew_publish_choose);
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setVisibility(View.GONE);
                }
                ImageView ivArrow = thiz.findViewById(R.id.iv_show_arrow);
                if (position == 2 && isShow) {
                    ivArrow.setVisibility(View.VISIBLE);
                    ivArrow.setImageResource(R.drawable.animal_left_arrow);
                    AnimationDrawable animation1;
                    animation1 = (AnimationDrawable) ivArrow.getDrawable();
                    animation1.start();
                } else {
                    ivArrow.setVisibility(View.GONE);
                }
                return thiz;
            }
        };
        mGridView.setAdapter(simpleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (position == 2) {
                    for (int i = 0; i < listItems.size(); i++) {
                        listItems.get(i).put("select", i == position ? true : false);
                    }
                    isShow = false;
                    actionType = position + 1;
                    simpleAdapter.notifyDataSetChanged();
                    CourseArrowAminalUtil.startViewAnimal(true, ivSaveArrow, 1);
                    ivSave.setEnabled(true);
                    ActionsCourseSaveActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            UbtLog.d(TAG, "txt_action_type.getText() = " + txt_action_type.getText());
                            txt_action_type.setText(getStringResources("ui_readback_save_action_type") + " " + imageNames[position]);

                            txt_actions_type_des.setText(imageNames[position]);

                            edt_disc.setHint(getStringResources(desHintKeys[position]));
                        }
                    });

                    if (mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        int imageId = R.drawable.action_dance_1b;
                        if (actionType == 1) {
                            imageId = R.drawable.action_dance_1b;
                            ivDemo1.setImageResource(R.drawable.action_dance_1b);
                            ivDemo2.setImageResource(R.drawable.action_dance_2b);
                            ivDemo3.setImageResource(R.drawable.action_dance_3b);
                        } else if (actionType == 2) {
                            imageId = R.drawable.action_story_1b;
                            ivDemo1.setImageResource(R.drawable.action_story_1b);
                            ivDemo2.setImageResource(R.drawable.action_story_2b);
                            ivDemo3.setImageResource(R.drawable.action_story_3b);
                        } else if (actionType == 3) {
                            imageId = R.drawable.action_sport_1b;
                            ivDemo1.setImageResource(R.drawable.action_sport_1b);
                            ivDemo2.setImageResource(R.drawable.action_sport_2b);
                            ivDemo3.setImageResource(R.drawable.action_sport_3b);
                        } else if (actionType == 4) {
                            imageId = R.drawable.action_er_1b;
                            ivDemo1.setImageResource(R.drawable.action_er_1b);
                            ivDemo2.setImageResource(R.drawable.action_er_2b);
                            ivDemo3.setImageResource(R.drawable.action_er_3b);
                        } else if (actionType == 5) {
                            imageId = R.drawable.action_science_1b;
                            ivDemo1.setImageResource(R.drawable.action_science_1b);
                            ivDemo2.setImageResource(R.drawable.action_science_2b);
                            ivDemo3.setImageResource(R.drawable.action_science_3b);
                        }

                        mCurrentActionImg = getBitmap(imageId);
                        img_action_logo.setImageResource(imageId);
                    }
                }
            }
        });

        if (actionType == -1) {
            actionType = 1;
            // listItems.get(0).put("select", true);
            simpleAdapter.notifyDataSetChanged();

            txt_actions_type_des.setText(imageNames[0]);
            txt_action_type.setText(getStringResources("ui_readback_save_action_type") + " " + imageNames[0]);
            edt_disc.setHint(getStringResources(desHintKeys[0]));

            edt_name.setHint(getStringResources("ui_readback_save_name_placeholder"));
        }

    }

    private void saveNewAction() {

        if (mCurrentActionImg == null || img_action_logo.getDrawable() == null) {
            this.showToast("ui_distribute_lack_picture");
            return;
        }

        if (edt_name.getText().toString().equals("")) {
            this.showToast("ui_action_name_empty");
            return;
        }

        if (!mHelper.isRightName(edt_name.getText().toString(), -1, false, "")) {
            return;
        }

        int length = edt_disc.getText().toString().length();

        if (length > 100) {
            this.showToast("ui_about_feedback_input_too_long");
            return;
        }

        if (TextUtils.isEmpty(txt_actions_type_des.getText())) {
            this.showToast("ui_save_action_choose_type");
            return;
        }
        showProgress();
        if (TextUtils.isEmpty(edt_disc.getText().toString())) {
            mCurrentAction.actionDesciber = edt_disc.getHint().toString();
        } else {
            mCurrentAction.actionDesciber = edt_disc.getText().toString();
        }
        mCurrentAction.actionName = edt_name.getText().toString().replace("\n", "");
        mCurrentAction.actionSonType = actionType;
        mCurrentAction.actionType = actionType;
        if (dubTag != -1) {
            ((ActionsEditHelper) mHelper).saveMyNewAction(mCurrentAction, mCurrentActionImg, dubTag, type);
        } else {
            mCurrentAction.actionTime = mCurrentAction.getTitleTime() / 1000;
            ((ActionsEditHelper) mHelper).saveMyNewAction(mCurrentAction, mCurrentActionImg, musicDir);
        }

    }


    public void generateBitmap(final Context mContext) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    String path = FileTools.actions_new_cache + File.separator + "Images/" + "default.jpg";
                    Bitmap bitmap = Glide.with(mContext).
                            load(R.drawable.sec_robot_action).
                            asBitmap().into(90, 90).get();
                    FileTools.writeImage(bitmap,
                            path, false);
                } catch (Exception e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();


    }

    @Override
    protected void initControlListener() {

        EditTextCheck.addCheckForLenth(edt_name, 16, this);
        EditTextCheck.addCheckForLenth(edt_disc, 100, this);

        img_action_logo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (lay_head_sel.getVisibility() == View.GONE) {
                    lay_head_sel.setVisibility(View.VISIBLE);
                } else {
                    lay_head_sel.setVisibility(View.GONE);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        txt_shooting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                PermissionUtils.getInstance(ActionsCourseSaveActivity.this)
                        .request(new PermissionUtils.PermissionLocationCallback() {
                            @Override
                            public void onSuccessful() {
                                //  ToastUtils.showShort("申请拍照权限成功");
                                Intent cameraIntent = new Intent(
                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                File path = new File(FileTools.image_cache);
                                if (!path.exists()) {
                                    path.mkdirs();
                                }
                                mImageUri = Uri.fromFile(new File(path, new Date().getTime()
                                        + ""));

                                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                                        mImageUri);
                                cameraIntent.putExtra("return-data", true);
                                startActivityForResult(cameraIntent,
                                        ActionsEditHelper.GetUserHeadRequestCodeByShoot);

                                lay_head_sel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure() {
                                //ToastUtils.showShort("申请拍照权限失败");
                            }

                            @Override
                            public void onRationSetting() {
                                // ToastUtils.showShort("申请拍照权限已经被拒绝过");
                            }

                            @Override
                            public void onCancelRationSetting() {
                            }


                        }, PermissionUtils.PermissionEnum.CAMERA, ActionsCourseSaveActivity.this);


            }
        });

        txt_from_file.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,
                        ActionsEditHelper.GetUserHeadRequestCodeByFile);
                lay_head_sel.setVisibility(View.GONE);
            }
        });

        txt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                lay_head_sel.setVisibility(View.GONE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByFile
                || requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByShoot) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByFile) {
                    if (data == null) {
                        return;
                    }

                    //android gao ban ben
                    String h_type = cr.getType(data.getData());
                    //android di ban ben
                    String l_type = data.getType();
                    UbtLog.d(TAG, "h_type:" + h_type + "   l_type:" + l_type);
                    if (h_type == null && l_type == null) {
                        return;
                    }
                    mImageUri = data.getData();
                }

                try {
                    InputStream in = cr.openInputStream(mImageUri);
                    int bitmapWidth = img_action_logo.getWidth() > 100 ? 100 : img_action_logo.getWidth();
                    int bitmapHeight = img_action_logo.getHeight() > 100 ? 100 : img_action_logo.getHeight();
                    ImageTools.compressImage(in,
                            bitmapWidth,
                            bitmapHeight,
                            new IImageListener() {
                                @Override
                                public void onGetImage(boolean isSuccess,
                                                       final Bitmap bitmap, long request_code) {
                                    if (isSuccess) {
                                        mCurrentActionImg = ImageTools
                                                .ImageCrop(bitmap);
//                                        setBg();
                                        setImage(mImageUri);
                                    }
                                }

                            }, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void setImage(final Uri uri) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap bitmap = FileUtils.getBitmapFormUri(ActionsCourseSaveActivity.this, uri);
                    img_action_logo.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setBg() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                img_action_logo.setImageBitmap(mCurrentActionImg);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPlaying() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPausePlay() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinishPlay() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadEng(byte[] eng_angle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChangeActionFinish() {
        UbtLog.d(TAG, "wmma onChangeActionFinish");
        boolean state = ((ActionsEditHelper) mHelper).getActionSaveState();
//
        edt_name.post(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
            }
        });
        if (state) {
            UbtLog.d("wilson", "onChangeActionFinish");
            Intent intent = new Intent();
            intent.putExtra(ActionsEditHelper.SaveActionResult, state);
            setResult(ActionsEditHelper.SaveActionReq, intent);
            finish();
        } else {
            FileTools.DeleteFile(new File(mCurrentAction.actionDir_local));
            FileTools.DeleteFile(new File(mCurrentAction.actionZip_local));
            mCurrentAction.actionId = -1;
            showToast("ui_save_action_save_failed");
        }
    }

    @Override
    public void onFrameDo(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {
        if (request_code == mCurrentAction.actionId) {
            mCurrentActionImg = img;
            setBg();
        }

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result,
                                    boolean result_state, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result,
                                     long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }


    @Override
    public void onReadCacheSize(int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClearCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void onGetFileLenth(long request_code, int file_lenth) {

    }

    @Override
    public void onReportProgress(long request_code, double progess) {

    }

    @Override
    public void onUpLoadFileFinish(long request_code, String json, State state) {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }
}
