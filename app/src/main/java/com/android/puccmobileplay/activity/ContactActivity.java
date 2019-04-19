package com.android.puccmobileplay.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.android.puccmobileplay.R;
import com.android.puccmobileplay.pager.ContactListFragment;


public class ContactActivity extends AppCompatActivity {

    private ContactListFragment mContactListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mContactListFragment = new ContactListFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.contact_fragment_content,mContactListFragment).commit();
    }
}
