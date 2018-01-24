package com.kustomer.kustomersdk.Helpers;

import android.graphics.Bitmap;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Interfaces.KUSImageUploadListener;
import com.kustomer.kustomersdk.Models.KUSChatAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUpload {

    //region Properties

    //endregion

    //region LifeCycle

    //endregion

    //region Public Methods
    public static void uploadImages(List<Bitmap> images, KUSUserSession userSession, KUSImageUploadListener listener){
        //TODO: Incomplete

        if(images == null || images.size() == 0){
            if(listener != null){
                listener.onCompletion(null,new ArrayList<KUSChatAttachment>());
            }
            return;
        }

    }
    //endregion

}
