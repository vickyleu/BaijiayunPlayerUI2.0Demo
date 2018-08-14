package com.baijiayun.videoplayer.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baijiayun.videoplayer.event.EventKey;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayerui.R;

/**
 * Created by yongjiaming on 2018/8/7
 */

public class ErrorComponent extends BaseComponent {

    private TextView errorMsgTv;
    private TextView errorCodeTv;
    private TextView retryBtn;

    public ErrorComponent(Context context) {
        super(context);
    }

    @Override
    protected View onCreateComponentView(Context context) {
        return View.inflate(context, R.layout.layout_error_component, null);
    }

    @Override
    protected void onInitView() {
        errorMsgTv = findViewById(R.id.error_msg_tv);
        errorCodeTv = findViewById(R.id.error_code_tv);
        retryBtn = findViewById(R.id.retry_btn);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyComponentEvent(UIEventKey.CUSTOM_CODE_REQUEST_REPLAY, null);
            }
        });
    }

    @Override
    protected void setKey() {
        key = UIEventKey.KEY_ERROR_COMPONENT;
    }


    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            default:
                break;
        }
        setComponentVisibility(View.GONE);
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            default:
                break;
        }
        setComponentVisibility(View.VISIBLE);
        errorMsgTv.setText(bundle.getString(EventKey.STRING_DATA));
        errorCodeTv.setText("[" + eventCode + "]");
    }
}
