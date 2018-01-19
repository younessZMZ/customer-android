package com.kustomer.kustomersdk.Activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;

import butterknife.BindView;
import butterknife.OnClick;

public class KUSChatActivity extends BaseActivity {

    //region Properties
    @BindView(R2.id.etTypeMessage) EditText etTypeMessage;
    @BindView(R2.id.rvMessages) RecyclerView rvMessages;
    @BindView(R2.id.fabSendMessage) FloatingActionButton fabSendMessage;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_kuschat,R.id.toolbar_main,getString(R.string.chat_screen),true);
        super.onCreate(savedInstanceState);
    }
    //endregion

    //region Initializer
    private void setupAdapter(){

    }
    //endregion

    //region Click Listener
    @OnClick(R2.id.fabSendMessage)
    void fabSendMessageClick(){
        Toast.makeText(this,"Fab Clicked",Toast.LENGTH_SHORT).show();
    }
    //endregion

}
