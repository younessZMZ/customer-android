package com.kustomer.kustomersdk.ViewHolders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSText;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.R;
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
    @BindView(R2.id.tvDate)
    TextView tvDate;
    //endregion

    //region LifeCycle
    public UserMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void onBind(KUSChatMessage chatMessage, boolean showDate){
        //KUSText.setMarkDownText(tvMessage,chatMessage.getBody().trim());
        tvMessage.setText(chatMessage.getBody().trim());

        if(showDate){
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(KUSDate.messageTimeStampTextFromDate(chatMessage.getCreatedAt()));
        }else {
            tvDate.setText("");
            tvDate.setVisibility(View.GONE);
        }
    }
    //endregion
}
