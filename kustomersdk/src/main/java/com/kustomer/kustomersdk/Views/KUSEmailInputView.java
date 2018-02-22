package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kustomer.kustomersdk.Helpers.KUSText;
import com.kustomer.kustomersdk.Interfaces.KUSEmailInputViewListener;
import com.kustomer.kustomersdk.R;

/**
 * Created by Junaid on 2/22/2018.
 */

public class KUSEmailInputView extends LinearLayout {

    //region Properties
    EditText etEmail;
    View submitButton;

    KUSEmailInputViewListener listener;
    //endregion

    //regionLifeCycle
    public KUSEmailInputView(Context context) {
        super(context);
    }

    public KUSEmailInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSEmailInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KUSEmailInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        setListener();
    }
    //endregion

    //region Initializer
    private void initViews(){
        etEmail = findViewById(R.id.etEmail);
        submitButton = findViewById(R.id.sendEmailButton);

        updateSubmitButton();
    }

    private void setListener(){
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateSubmitButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                userWantsToSubmit();
            }
        });
    }
    //endregion

    //region Public Methods
    public void setListener(KUSEmailInputViewListener listener){
        this.listener = listener;
    }
    //endregion

    //region Private Methods
    private void userWantsToSubmit(){
        boolean isValidEmail = KUSText.isValidEmail(getSanitizedText());

        if(isValidEmail && listener != null)
            listener.onSubmitEmail(getSanitizedText());
    }

    private String getSanitizedText(){
        return etEmail.getText().toString().trim();
    }

    private void updateSubmitButton(){
        boolean isValidEmail = KUSText.isValidEmail(getSanitizedText());
        submitButton.setClickable(isValidEmail);
        submitButton.setAlpha(isValidEmail ? 1.0f : 0.5f);
    }
    //endregion


}
