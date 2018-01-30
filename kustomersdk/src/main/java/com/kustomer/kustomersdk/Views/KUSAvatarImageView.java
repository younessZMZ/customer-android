package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSUser;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by Junaid on 1/30/2018.
 */

public class KUSAvatarImageView extends FrameLayout implements KUSObjectDataSourceListener {

    //region Properties
    private Bitmap companyAvatarImage;
    private String userId;

    private KUSUserSession userSession;
    private KUSUserDataSource userDataSource;

    ImageView staticImageView;
    ImageView remoteImageView;
    //endregion

    //region Initializer
    public KUSAvatarImageView(@NonNull Context context) {
        super(context);
    }

    public KUSAvatarImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSAvatarImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KUSAvatarImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    //region Public Methods
    public void initWithUserSession(KUSUserSession userSession){
        this.userSession = userSession;
        userSession.getChatSettingsDataSource().addListener(this);

        staticImageView = new ImageView(getContext());
        staticImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        staticImageView.setImageResource(R.drawable.shape_dark_grey_circle_with_stroke);
        staticImageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        addView(staticImageView);

        remoteImageView = new ImageView(getContext());
        remoteImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        remoteImageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        addView(remoteImageView);

        updateAvatarImage();
    }

    public void setCompanyAvatarImage(Bitmap bitmap){
        companyAvatarImage = bitmap;
        updateAvatarImage();
    }

    public void setUserId(String userId){
        if(this.userId != null && this.userId.equals(userId))
            return;

        if(userDataSource != null)
            userDataSource.removeListener(this);

        this.userId = userId;
        this.userDataSource = userSession.userDataSourceForUserId(userId);
        userDataSource.addListener(this);

        updateAvatarImage();
    }
    //endregion

    //region Private Methods
    private void updateAvatarImage(){
        if(this.userId == null && companyAvatarImage != null){
            staticImageView.setImageBitmap(companyAvatarImage);
            return;
        }

        KUSUser user = null;
        if(userDataSource != null) {
            user = (KUSUser) userDataSource.getObject();
            if (user == null && !userDataSource.isFetching()) {
                userDataSource.fetch();
            }
        }

        KUSChatSettings chatSettings = (KUSChatSettings) userSession.getChatSettingsDataSource().getObject();
        if(userSession.getChatSettingsDataSource() != null && chatSettings == null && !this.userSession.getChatSettingsDataSource().isFetching()){
            userSession.getChatSettingsDataSource().fetch();
        }


        String name = "";
        if (user != null && user.getDisplayName() != null)
            name = user.getDisplayName();
        else if (chatSettings != null && chatSettings.getTeamName() != null)
            name = chatSettings.getTeamName();
        else if (userSession.getOrganizationName() != null)
            name = userSession.getOrganizationName();

         //TODO: Create bitmap with name text

        if(user != null && chatSettings != null) {
            URL iconURL = user.getAvatarURL() != null ? user.getAvatarURL() : chatSettings.getTeamIconURL();
            Picasso.with(getContext())
                    .load(iconURL.toString())
                    .into(staticImageView);
        }
    }
    //endregion

    //region Listener
    @Override
    public void objectDataSourceOnLoad(final KUSObjectDataSource dataSource) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                if(dataSource == userSession.getChatSettingsDataSource()){
                    updateAvatarImage();
                }else if(dataSource == userDataSource){
                    updateAvatarImage();
                }

            }
        };
        mainHandler.post(myRunnable);

    }

    @Override
    public void objectDataSouceOnError(KUSObjectDataSource dataSource, Error error) {

    }
    //endregion

}
