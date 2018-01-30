package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.util.ArrayList;

/**
 * Created by Junaid on 1/30/2018.
 */

public class KUSMultipleAvatarsView extends FrameLayout {

    //region Properties
    private static final int K_KUS_DEFAULT_MAXIMUM_AVATARS_TO_DISPLAY = 3;

    private int maximumAvatarsToDisplay;
    private ArrayList<KUSAvatarImageView> avatarImageViews;
    private ArrayList<String> userIds;
    KUSUserSession userSession;
    //endregion

    //region Initializer
    public KUSMultipleAvatarsView(@NonNull Context context) {
        super(context);
    }

    public KUSMultipleAvatarsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSMultipleAvatarsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KUSMultipleAvatarsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setMaximumAvatarsToDisplay(K_KUS_DEFAULT_MAXIMUM_AVATARS_TO_DISPLAY);
    }

    //endregion

    //region Public Methods
    public void initWithUserSession(KUSUserSession userSession){
        this.userSession = userSession;
        rebuildAvatarViews();
    }

    public void setUserIds(ArrayList<String> userIds){
        if(this.userIds != null && this.userIds.equals(userIds))
            return;

        this.userIds = userIds;
        rebuildAvatarViews();
    }
    //endregion

    //region Private Methods
    void setMaximumAvatarsToDisplay(int maximumAvatarsToDisplay){
        this.maximumAvatarsToDisplay = Math.max(maximumAvatarsToDisplay,1);
        rebuildAvatarViews();
    }

    void rebuildAvatarViews(){

        //TODO: Make avatars appear in list

        if(userSession == null)
            return;

        if(avatarImageViews != null) {
            for (KUSAvatarImageView avatarImageView : avatarImageViews)
                removeView(avatarImageView);
        }

        ArrayList<KUSAvatarImageView> avatarImageViews = new ArrayList<>();

        if(userIds != null) {
            for (int i = 0; i < Math.min(userIds.size(), maximumAvatarsToDisplay); i++) {
                String userId = userIds.get(i);
                KUSAvatarImageView userAvatarView = new KUSAvatarImageView(getContext());
                userAvatarView.setLayoutParams(new LayoutParams((int)KUSUtils.dipToPixels(getContext(),40)
                        ,(int)KUSUtils.dipToPixels(getContext(),40)));
                userAvatarView.initWithUserSession(userSession);
                userAvatarView.setUserId(userId);
                avatarImageViews.add(userAvatarView);
            }
        }

        if(avatarImageViews.size() < maximumAvatarsToDisplay){
            KUSAvatarImageView companyAvatarView = new KUSAvatarImageView(getContext());
            companyAvatarView.setLayoutParams(new LayoutParams((int)KUSUtils.dipToPixels(getContext(),40)
                    ,(int)KUSUtils.dipToPixels(getContext(),40)));
            companyAvatarView.initWithUserSession(userSession);
            avatarImageViews.add(companyAvatarView);
        }

        for(int i = avatarImageViews.size()-1 ; i>=0; i--){
            KUSAvatarImageView avatarImageView = avatarImageViews.get(i);
            addView(avatarImageView);
        }

        this.avatarImageViews = avatarImageViews;
    }
    //endregion
}
