package com.kustomer.kustomersdk.ViewHolders;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kustomer.kustomersdk.Adapters.MessageListAdapter;
import com.kustomer.kustomersdk.Enums.KUSChatMessageType;
import com.kustomer.kustomersdk.Helpers.KUSCache;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSText;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Views.KUSSquareFrameLayout;

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
    @BindView(R2.id.ivAttachmentImage)
    ImageView ivAttachmentImage;
    @BindView(R2.id.attachmentLayout)
    KUSSquareFrameLayout attachmentLayout;
    @BindView(R2.id.progressBarImage)
    ProgressBar progressBarImage;

    private boolean imageLoadedSuccessfully = false;
    //endregion

    //region LifeCycle
    public UserMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void onBind(final KUSChatMessage chatMessage, boolean showDate, final MessageListAdapter.ChatMessageItemListener mListener){

        if(chatMessage.getType() == KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT){
            tvMessage.setVisibility(View.VISIBLE);
            attachmentLayout.setVisibility(View.GONE);
            KUSText.setMarkDownText(tvMessage,chatMessage.getBody().trim());
        }else if(chatMessage.getType() == KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_IMAGE){
            tvMessage.setVisibility(View.GONE);
            attachmentLayout.setVisibility(View.VISIBLE);

            updateImageForMessage(chatMessage,mListener);
        }

        if(showDate){
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(KUSDate.messageTimeStampTextFromDate(chatMessage.getCreatedAt()));
        }else {
            tvDate.setText("");
            tvDate.setVisibility(View.GONE);
        }
    }

    private void updateImageForMessage(final KUSChatMessage chatMessage,
                                       final MessageListAdapter.ChatMessageItemListener mListener){


        progressBarImage.setVisibility(View.VISIBLE);

        Bitmap cachedImage = new KUSCache().getBitmapFromMemCache(chatMessage.getImageUrl().toString());
        if(cachedImage != null){
            ivAttachmentImage.setImageBitmap(cachedImage);
            progressBarImage.setVisibility(View.GONE);
            imageLoadedSuccessfully = true;
        }else {
            Glide.with(itemView)
                    .setDefaultRequestOptions(RequestOptions.errorOf(R.drawable.ic_error_outline_red_33dp))
                    .load(chatMessage.getImageUrl().toString())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            imageLoadedSuccessfully = false;
                            ivAttachmentImage.setScaleType(ImageView.ScaleType.CENTER);
                            progressBarImage.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageLoadedSuccessfully = true;
                            ivAttachmentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            progressBarImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivAttachmentImage);
        }

        ivAttachmentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageLoadedSuccessfully)
                    updateImageForMessage(chatMessage,mListener);

                mListener.onChatMessageImageClicked(chatMessage);
            }
        });
    }
    //endregion

}
