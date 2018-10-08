package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;

public class SetupPhoneNumber extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_phone_number);

        setupToolbar(R.id.toolbar, "Setting Messenger");
        Button button = (Button) findViewById(R.id.bt_continue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupPhoneNumber.this, MainActivity.class));
            }
        });
    }
}
