package com.kustomer.kustomer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kustomer.kustomer.BaseClasses.BaseActivity;
import com.kustomer.kustomer.R;
import com.kustomer.kustomersdk.Activities.KUSChatActivity;
import com.kustomer.kustomersdk.Kustomer;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    //region Properties
    Button btnStartChat;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_main,R.id.toolbar_main,getResources().getString(R.string.app_name),false);
        super.onCreate(savedInstanceState);

        initViews();
        setListeners();
    }
    //endregion

    //region Initializer
    private void initViews(){
        btnStartChat = findViewById(R.id.btnPresentSupport);
    }

    private void setListeners(){
        btnStartChat.setOnClickListener(this);
    }
    //endregion

    //region Click Listener
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPresentSupport:
                Kustomer.showSupport(this);
        }
    }
    //endregion
}
