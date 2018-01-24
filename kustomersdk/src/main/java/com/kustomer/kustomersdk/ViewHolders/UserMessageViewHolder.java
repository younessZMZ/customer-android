package com.kustomer.kustomersdk.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Junaid on 1/19/2018.
 */

public class UserMessageViewHolder extends RecyclerView.ViewHolder {

    //region Properties
    @BindView(R2.id.tvMessage)
    TextView tvMessage;
    //endregion

    //region LifeCycle
    public UserMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void onBind(KUSChatMessage chatMessage){
        tvMessage.setText(chatMessage.body);
    }
    //endregion
}
