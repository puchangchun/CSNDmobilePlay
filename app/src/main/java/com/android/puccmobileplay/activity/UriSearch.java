package com.android.puccmobileplay.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.puccmobileplay.R;

public class UriSearch extends AppCompatActivity {

    private Button mPlayButtom;
    private EditText mUriEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri_search);
        mPlayButtom = (Button)findViewById(R.id.app_bar_button_play);
        mUriEditText = (EditText)findViewById(R.id.app_bar_edit_text_uri);
        mPlayButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mUriEditText.getText().toString();
                if (s != null && !s.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(s), "video/*");
                    startActivity(intent);
                }
            }
        });
    }
}
