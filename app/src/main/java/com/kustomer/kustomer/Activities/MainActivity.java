package com.kustomer.kustomer.Activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kustomer.kustomer.BaseClasses.BaseActivity;
import com.kustomer.kustomer.R;
import com.kustomer.kustomersdk.Activities.KUSSessionsActivity;
import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;
import com.kustomer.kustomersdk.Kustomer;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    //region Properties
    Button btnStartChat;
    Button btnResetTrackingToken;
    Button btnKnowledgeBase;
    ImageView ivSupport;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_main,R.id.toolbar_main,getResources().getString(R.string.app_name),false);
        super.onCreate(savedInstanceState);

        initViews();
        setListeners();
        Kustomer.setCurrentPageName("Home");

//        // Describing Customer
//        KUSCustomerDescription customerDescription = new KUSCustomerDescription();
//        customerDescription.setEmail("address@example.com");
//
//        JSONObject customObject = new JSONObject();
//        try {
//            //You can put multiple values here
//            customObject.put("ageNum",22);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        customerDescription.setCustom(customObject);
//        Kustomer.describeCustomer(customerDescription);
//
//        // Describing Conversation
//        JSONObject conversationObject = new JSONObject();
//        try {
//            conversationObject.put("companyStr","acmeCorp");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Kustomer.describeConversation(conversationObject);

//        Kustomer.identify("[INSERT_JWT_TOKEN_HERE");

        Kustomer.setListener(new KUSKustomerListener() {
            @Override
            public boolean kustomerShouldDisplayInAppNotification() {
                return true;
            }

            @Override
            public PendingIntent getPendingIntent(Context context) {
                Intent intent = new Intent(context, KUSSessionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return PendingIntent.getActivity(context, 0, intent, 0);
            }
        });

//        Kustomer.presentCustomWebPage(this,"https://www.example.com");
    }

    //endregion

    //region Initializer
    private void initViews(){
        btnStartChat = findViewById(R.id.btnPresent);
        btnKnowledgeBase = findViewById(R.id.btnKnowledgeBase);
        ivSupport = findViewById(R.id.ivSupport);
        btnResetTrackingToken = findViewById(R.id.btnResetToken);
    }

    private void setListeners(){
        btnStartChat.setOnClickListener(this);
        btnResetTrackingToken.setOnClickListener(this);
        ivSupport.setOnClickListener(this);
        btnKnowledgeBase.setOnClickListener(this);
    }
    //endregion

    //region Click Listener
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivSupport:
            case R.id.btnPresent:
                Kustomer.showSupport(this);
                break;

            case R.id.btnResetToken:
                Kustomer.resetTracking();
                break;

            case R.id.btnKnowledgeBase:
                Kustomer.presentKnowledgeBase(this);
                break;
        }
    }
    //endregion
}
