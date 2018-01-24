package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.Models.KUSChatAttachment;

import java.util.List;

/**
 * Created by Junaid on 1/24/2018.
 */

public interface KUSImageUploadListener {
    void onCompletion(Error error, List<KUSChatAttachment> attachments);
}
