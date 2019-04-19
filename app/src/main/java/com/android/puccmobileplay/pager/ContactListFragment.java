package com.android.puccmobileplay.pager;

import android.content.Intent;
import android.view.View;


import com.android.puccmobileplay.R;
import com.android.puccmobileplay.activity.AddContactActivity;
import com.android.puccmobileplay.activity.ChatActivity;
import com.android.puccmobileplay.model.ModelController;
import com.android.puccmobileplay.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 99653 on 2017/11/5.
 */

public class ContactListFragment extends EaseContactListFragment {

    Map<String, EaseUser> map = new HashMap<>();
    private ArrayList<String> usernames;

    @Override
    protected void initView() {
        super.initView();
        titleBar.setRightImageResource(R.drawable.em_add);

        View header = View.inflate(getActivity(), R.layout.header_fragment_contact_list, null);
        listView.addHeaderView(header);

        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

                if (user == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                // 传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());

                startActivity(intent);
            }
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();

        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddContactActivity.class));
            }
        });

        // 从环信服务器获取所有的联系人信息
        getContactFromHxServer();
    }



    // 从环信服务器获取所有的联系人信息
    private void getContactFromHxServer() {

        ModelController.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取到所有的好友的环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    // 校验
                    if (hxids != null && hxids.size() >= 0) {

                        List<UserInfo> contacts = new ArrayList<UserInfo>();

                        // 转换
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }

                        // 保存好友信息到本地数据库
                        ModelController.getInstance().getDbManager().getContactTableDao().saveContacts(contacts, true);

                        if (getActivity() == null) {
                            return;
                        }

                        // 刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新页面的方法
                                refreshContact();
                            }
                        });

                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // 刷新页面
    private void refreshContact() {

        // 获取数据
        List<UserInfo> contacts = ModelController.getInstance().getDbManager().getContactTableDao().getContacts();

        // 校验
        if (contacts != null && contacts.size() >= 0) {

            // 设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();

            // 转换
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());

                contactsMap.put(contact.getHxid(), easeUser);
            }

            setContactsMap(contactsMap);

            // 刷新页面
            refresh();
        }
    }
}
