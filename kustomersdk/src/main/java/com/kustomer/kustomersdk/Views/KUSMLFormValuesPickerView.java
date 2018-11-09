package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kustomer.kustomersdk.Interfaces.KUSOptionPickerViewListener;
import com.kustomer.kustomersdk.Models.KUSMLNode;
import com.kustomer.kustomersdk.R2;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KUSMLFormValuesPickerView extends LinearLayout implements KUSOptionPickerViewListener {
    //region Properties
    @BindView(R2.id.mlFormOptionPicker)
    KUSOptionsPickerView mlFormOptionPicker;
    @BindView(R2.id.btnSendMessage)
    View btnSendMessage;

    private ArrayList<KUSMLNode> valuesTree;
    private ArrayList<KUSMLNode> currentOptionsToShow;
    private ArrayList<KUSMLNode> selectedValuesStack;
    private ArrayList<String> currentOptionsValues;

    private Boolean isLastNodeRequired;
    private Boolean isOptionPickerNeeded;
    //endregion

    //region LifeCycle
    public KUSMLFormValuesPickerView(Context context) {
        super(context);
    }

    public KUSMLFormValuesPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSMLFormValuesPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KUSMLFormValuesPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        mlFormOptionPicker.setListener(this);
    }
    //endregion

    //region Public Methods
    public void setMlFormValues(ArrayList<KUSMLNode> valuesTree, boolean isLastNodeRequired){
        this.valuesTree = new ArrayList<>(valuesTree);
        this.currentOptionsToShow = new ArrayList<>(valuesTree);
        selectedValuesStack = new ArrayList<>();
        this.isLastNodeRequired = isLastNodeRequired;
        showCurrentOptionsAndUpdateView();
    }
    //endregion

    //region Private Methods
    private void updateSendButton(){
        if(selectedValuesStack.size() == 0)
            return;

        boolean shouldEnableSend = !isLastNodeRequired
                || selectedValuesStack.get(selectedValuesStack.size() - 1).getChildNodes().size() == 0;

        btnSendMessage.setEnabled(shouldEnableSend);
        btnSendMessage.setAlpha(shouldEnableSend ? 1.0f : 0.5f);
    }

    private void showCurrentOptionsAndUpdateView(){
        if(currentOptionsToShow.size() == 0){
            mlFormOptionPicker.setVisibility(GONE);
        }else{
            mlFormOptionPicker.setVisibility(VISIBLE);
            currentOptionsValues = new ArrayList<>(currentOptionsToShow.size());

            for (KUSMLNode node : currentOptionsToShow) {
                currentOptionsValues.add(node.getDisplayName());
            }

            mlFormOptionPicker.setOptions(currentOptionsValues);
        }

        updateSendButton();
        //TODO: reload data in recyclerView & smooth scroll to last index
    }
    //endregion

    //region Listeners
    @Override
    public void optionPickerOnOptionSelected(String option) {
        int optionIndex = mlFormOptionPicker.getOptions().indexOf(option);

        if(optionIndex != -1 && optionIndex < currentOptionsToShow.size()){
            selectedValuesStack.add(currentOptionsToShow.get(optionIndex));
        }

        currentOptionsToShow.clear();
        if(selectedValuesStack.size() > 0
                && selectedValuesStack.get(selectedValuesStack.size()-1).getChildNodes().size()>0){
            currentOptionsToShow = new ArrayList<>(selectedValuesStack.get(selectedValuesStack.size()-1)
                    .getChildNodes());
        }

        showCurrentOptionsAndUpdateView();

    }
    //endregion
}
