package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kustomer.kustomersdk.Interfaces.KUSInputBarViewListener;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Junaid on 2/27/2018.
 */

public class KUSInputBarView extends LinearLayout implements TextWatcher, TextView.OnEditorActionListener {

    //region Properties
    @BindView(R2.id.etTypeMessage) EditText etTypeMessage;
    @BindView(R2.id.btnSendMessage) View btnSendMessage;

    KUSInputBarViewListener listener;
    //endregion

    //region LifeCycle
    public KUSInputBarView(Context context) {
        super(context);
    }

    public KUSInputBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSInputBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KUSInputBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        initViews();
        setListeners();
    }
    //endregion

    //region Initializer
    private void initViews(){
        updateSendButton();

        etTypeMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
        etTypeMessage.setRawInputType(InputType.TYPE_CLASS_TEXT);

        KUSUtils.showKeyboard(etTypeMessage,800);
    }

    private void setListeners(){
        etTypeMessage.addTextChangedListener(this);
        etTypeMessage.setOnEditorActionListener(this);
    }
    //endregion

    //region Public Methods
    public void setListener(KUSInputBarViewListener listener){
        this.listener = listener;
    }

    public void setText(String text){
        etTypeMessage.setText(text.trim());
    }

    public String getText(){
        return etTypeMessage.getText().toString().trim();
    }

    public void setImageAttachments(List<Bitmap> imageAttachments){
        //TODO:
    }

    public void requestInputFocus(){
        etTypeMessage.requestFocus();
    }

    public void clearInputFocus(){
        etTypeMessage.clearFocus();
        KUSUtils.hideKeyboard(this);
    }
    //endregion

    //region Interface element methods
    private void attachPressed(){
        if(listener != null)
            listener.inputBarAttachmentClicked();
    }

    @OnClick(R2.id.btnSendMessage)
    void sendPressed(){
        String text = getText();
        if(text.length() == 0)
            return;

        if(listener != null)
            listener.inputBarSendClicked();
    }

    private void updateSendButton(){
        String text = getText();
        boolean shouldEnableSend = text.length() > 0;

        if(listener != null)
            shouldEnableSend = listener.inputBarShouldEnableSend();

        btnSendMessage.setEnabled(shouldEnableSend);
        btnSendMessage.setAlpha(shouldEnableSend ? 1.0f : 0.5f);
    }
    //endregion

    //region Listeners
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updateSendButton();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i == EditorInfo.IME_ACTION_SEND){
            sendPressed();
        }
        return false;
    }
    //endregion
}
