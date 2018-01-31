package com.kustomer.kustomersdk.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSUtils;
import com.kustomer.kustomersdk.Views.KUSAvatarImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Junaid on 1/19/2018.
 */

public class AgentMessageViewHolder extends RecyclerView.ViewHolder {

    //region Properties
    @BindView(R2.id.tvMessage)
    TextView tvMessage;
    @BindView(R2.id.flAvatar)
    FrameLayout imageLayout;
    //endregion

    public AgentMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void onBind(KUSChatMessage chatMessage, KUSUserSession userSession, boolean showAvatar){
        tvMessage.setText(chatMessage.getBody());

        imageLayout.removeAllViews();
        if(showAvatar) {
            KUSAvatarImageView avatarImageView = new KUSAvatarImageView(itemView.getContext());
            avatarImageView.setFontSize(13);
            avatarImageView.setDrawableSize(40);

            avatarImageView.initWithUserSession(userSession);
            avatarImageView.setUserId(chatMessage.getSentById());

            FrameLayout.LayoutParams avatarLayoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            avatarImageView.setLayoutParams(avatarLayoutParams);


            imageLayout.addView(avatarImageView);
        }

    }
}
