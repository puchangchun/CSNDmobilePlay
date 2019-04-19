package com.android.puccmobileplay.pager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.puccmobileplay.R;
import com.android.puccmobileplay.activity.ContactActivity;
import com.android.puccmobileplay.activity.LoginActivity;
import com.android.puccmobileplay.activity.MainActivity;
import com.android.puccmobileplay.base.BasePager;
import com.android.puccmobileplay.model.ModelController;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.widget.EaseSwitchButton;

public class SettingsPager extends BasePager implements View.OnClickListener {
    private RelativeLayout rl_switch_notification;
    private RelativeLayout rl_switch_sound;
    private RelativeLayout rl_switch_vibrate;
    private RelativeLayout rl_switch_speaker;
    private RelativeLayout rl_mail_log;
    private View rl_msg_typing;
    private EaseSwitchButton notifySwitch;
    private EaseSwitchButton vibrateSwitch;
    private View switch_msg_typing;
    private Button logoutBtn;
    private TextView textview1;
    private TextView textview2;
    private LinearLayout userProfileContainer;
    private EMOptions chatOptions;
    private EaseSwitchButton soundSwitch;
    private EaseSwitchButton speakerSwitch;

    public SettingsPager(Context context) {
        super(context);
    }



    @Override
    public View initView() {
        View v = View.inflate(mContext, R.layout.em_fragment_conversation_settings, null);
        rl_switch_notification = (RelativeLayout) v.findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) v.findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) v.findViewById(R.id.rl_switch_vibrate);
        rl_switch_speaker = (RelativeLayout) v.findViewById(R.id.rl_switch_speaker);

        rl_mail_log = (RelativeLayout) v.findViewById(R.id.rl_mail_log);

        rl_msg_typing = v.findViewById(R.id.rl_msg_typing);

        notifySwitch = (EaseSwitchButton) v.findViewById(R.id.switch_notification);
        soundSwitch = (EaseSwitchButton) v.findViewById(R.id.switch_sound);
        vibrateSwitch = (EaseSwitchButton) v.findViewById(R.id.switch_vibrate);
        speakerSwitch = (EaseSwitchButton) v.findViewById(R.id.switch_speaker);

        switch_msg_typing = v.findViewById(R.id.switch_msg_typing);

        logoutBtn = (Button) v.findViewById(R.id.btn_logout);
        textview1 = (TextView) v.findViewById(R.id.textview1);
        textview2 = (TextView) v.findViewById(R.id.textview2);

        userProfileContainer = (LinearLayout) v.findViewById(R.id.ll_user_profile);


        chatOptions = EMClient.getInstance().getOptions();

        userProfileContainer.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);

        logoutBtn.setOnClickListener(this);

        rl_mail_log.setOnClickListener(this);
        rl_msg_typing.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_notification:
                if (notifySwitch.isSwitchOpen()) {
                    notifySwitch.closeSwitch();
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);

                } else {
                    notifySwitch.openSwitch();
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.rl_switch_sound:
                if (soundSwitch.isSwitchOpen()) {
                    soundSwitch.closeSwitch();

                } else {
                    soundSwitch.openSwitch();

                }
                break;
            case R.id.rl_switch_vibrate:
                if (vibrateSwitch.isSwitchOpen()) {
                    vibrateSwitch.closeSwitch();

                } else {
                    vibrateSwitch.openSwitch();

                }
                break;
            case R.id.rl_switch_speaker:
                if (speakerSwitch.isSwitchOpen()) {
                    speakerSwitch.closeSwitch();

                } else {
                    speakerSwitch.openSwitch();

                }
                break;

            case R.id.btn_logout:
                logout();
                break;

            case R.id.ll_user_profile:
                break;

            case R.id.rl_mail_log:
                Intent intent = new Intent(mContext,ContactActivity.class);
                mContext.startActivity(intent);
                break;

            case R.id.rl_msg_typing:

                break;
            default:
                break;
        }
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(mContext);
        String st = mContext.getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        ModelController.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        ((MainActivity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                // show login screen
                                ((MainActivity) mContext).finish();
                                mContext.startActivity(new Intent(mContext, LoginActivity.class));

                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        ((MainActivity)mContext).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                pd.dismiss();
                                Toast.makeText(mContext, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}
