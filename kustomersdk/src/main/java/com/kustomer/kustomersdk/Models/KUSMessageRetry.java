package com.kustomer.kustomersdk.Models;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Junaid on 3/22/2018.
 */

public class KUSMessageRetry extends KUSRetry {

    //region Properties
    private List<KUSModel> temporaryMessages;
    private List<Bitmap> attachments;
    private String text;
    private List<String> cachedImages;
    //endregion

    //region LifeCycle
    public KUSMessageRetry(List<KUSModel> temporaryMessages, List<Bitmap> attachments, String text, List<String> cachedImages ){

        this.temporaryMessages = temporaryMessages;
        this.attachments = attachments;
        this.text = text;
        this.cachedImages = cachedImages;

    }
    //endregion

    //region Getters

    public List<KUSModel> getTemporaryMessages() {
        return temporaryMessages;
    }

    public List<Bitmap> getAttachments() {
        return attachments;
    }

    public String getText() {
        return text;
    }

    public List<String> getCachedImages() {
        return cachedImages;
    }

    //endregion
}
