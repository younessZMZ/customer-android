package com.kustomer.kustomersdk.Helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.facebook.imagepipeline.cache.BitmapMemoryCacheFactory;
import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSImageUploadListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatAttachment;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUpload {

    //region Properties
    private boolean sendingComplete = false;
    private int uploadedCount = 0;
    private List<KUSChatAttachment> attachments;
    //endregion

    //region LifeCycle

    //endregion

    //region Public Methods
    public void uploadImages(final List<Bitmap> images, KUSUserSession userSession, final KUSImageUploadListener listener){

        if(images == null || images.size() == 0){
            if(listener != null){
                listener.onCompletion(null,new ArrayList<KUSChatAttachment>());
            }
            return;
        }

        attachments = new ArrayList<>(images.size());

        while(attachments.size() < images.size())
            attachments.add(null);

        for(int i = 0; i<images.size(); i++){
            Bitmap bitmap = images.get(i);

            final int index = i;
            uploadImage(bitmap, userSession, new ImageUploadListener() {
                @Override
                public void onUploadComplete(Error error, KUSChatAttachment attachment) {
                    uploadCompleted(index,error,attachment,listener,images);
                }
            });
        }

    }
    //endregion

    //region Private Method
    private void uploadCompleted(int index, Error error, KUSChatAttachment attachment,
                                 KUSImageUploadListener listener, List<Bitmap> images){
        if(error != null){
            if(listener != null && !sendingComplete){
                sendingComplete = true;
                listener.onCompletion(error,null);
            }

            return;
        }

        uploadedCount++;
        attachments.set(index,attachment);
        if(uploadedCount == images.size()){
            if(listener !=null && !sendingComplete){
                sendingComplete = true;
                listener.onCompletion(null, attachments);
            }
        }
    }

    private void uploadImage(Bitmap image, final KUSUserSession userSession, final ImageUploadListener listener){

        if(image != null) {
            final byte[] imageBytes = KUSImage.getByteArrayFromBitmap(image);
            final String filename = String.format("%s.jpg", UUID.randomUUID().toString());

            userSession.getRequestManager().performRequestType(
                    KUSRequestType.KUS_REQUEST_TYPE_POST,
                    KUSConstants.URL.CHAT_ATTACHMENT_ENDPOINT,
                    new HashMap<String, Object>() {{
                        put("name", filename);
                        put("contentLength", imageBytes.length);
                        put("contentType", "image/jpeg");
                    }},
                    true,
                    new KUSRequestCompletionListener() {
                        @Override
                        public void onCompletion(Error error, JSONObject response) {

                            if(error != null){
                                if(listener != null){
                                    listener.onUploadComplete(error,null);
                                }

                                return;
                            }

                            final KUSChatAttachment chatAttachment;
                            try {
                                chatAttachment = new KUSChatAttachment(JsonHelper.jsonObjectFromKeyPath(response,"data"));
                            } catch (KUSInvalidJsonException e) {
                                return;
                            }

                            try {
                                URL uploadURL = new URL(JsonHelper.stringFromKeyPath(response,"meta.upload.url"));
                                HashMap<String, String> uploadFields =
                                        JsonHelper.hashMapFromKeyPath(response,"meta.upload.fields");

                                userSession.getRequestManager().uploadImageOnS3(uploadURL,
                                        filename,
                                        imageBytes,
                                        uploadFields,
                                        new KUSRequestCompletionListener() {
                                            @Override
                                            public void onCompletion(Error error, JSONObject response) {
                                                if(error != null){
                                                    if(listener != null)
                                                        listener.onUploadComplete(error, null);

                                                    return;
                                                }

                                                if(listener != null)
                                                    listener.onUploadComplete(null,chatAttachment);
                                            }
                                        });


                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                        }
                    }
            );
        }
    }


    //endregion

    //region Interface
    public interface ImageUploadListener{
        void onUploadComplete(Error error, KUSChatAttachment attachment);
    }
    //endregion

}
