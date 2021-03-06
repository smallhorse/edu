package com.ubt.alpha1e.edu.action.actioncreate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.community.CommunityActivity;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class SaveSuccessActivity extends Activity implements View.OnClickListener{

    private static String TAG = "SaveSuccessActivity";

    ImageView ivClose;
    Button btn_to_other;
    TextView txt_to_other;
    TextView txt_base_title_name;

    private NewActionInfo actionInfo = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_action_success);
        ivClose = (ImageView) findViewById(R.id.img_cancel);
        ivClose.setOnClickListener(this);
        btn_to_other = (Button) findViewById(R.id.btn_to_other) ;
        btn_to_other.setOnClickListener(this);
        txt_to_other = (TextView) findViewById(R.id.txt_to_other);
        txt_base_title_name = (TextView) findViewById(R.id.txt_base_title_name);
        if(SPUtils.getInstance().getBoolean(Constant.SP_EDU_MODULE)){
            btn_to_other.setVisibility(View.INVISIBLE);
            txt_to_other.setVisibility(View.INVISIBLE);
            txt_base_title_name.setText("保存");
        }else{
            btn_to_other.setVisibility(View.VISIBLE);
            txt_to_other.setVisibility(View.VISIBLE);
            txt_base_title_name.setText("保存并发布");
        }

        if(getIntent() != null){
            actionInfo = getIntent().getParcelableExtra("NewActionInfo");
        }
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_cancel:
                UbtLog.d(TAG, "img_cancel");
                finish();
                this.overridePendingTransition(0, R.anim.activity_close_down_up);
                if(isBulueToothConnected()){
                    startActivity(new Intent(this, ActionTestActivity.class));
                }
                break;
            case R.id.btn_to_other:
                //ToastUtils.showShort("社区暂未开放");
                goReplyAction();
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            if(isBulueToothConnected()){
                startActivity(new Intent(this, ActionTestActivity.class));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isBulueToothConnected() {

        if (((AlphaApplication) this.getApplicationContext())
                .getCurrentBluetooth() == null) {
            return false;
        } else {
            return true;
        }
    }

    private void goReplyAction(){
        if(actionInfo != null){
            DynamicActionModel replyActionModel = new DynamicActionModel();
            replyActionModel.setActionDesciber(actionInfo.actionDesciber);
            replyActionModel.setActionHeadUrl(actionInfo.actionHeadUrl);
            replyActionModel.setActionId((int) actionInfo.actionId);
            replyActionModel.setActionUrl(actionInfo.actionUrl);
            replyActionModel.setActionName(actionInfo.actionName);
            replyActionModel.setActionOriginalId(actionInfo.actionOriginalId + "");
            replyActionModel.setActionType(actionInfo.actionType);

            CommunityActivity.launchToReplyAction(this,replyActionModel);
        }

    }


}
