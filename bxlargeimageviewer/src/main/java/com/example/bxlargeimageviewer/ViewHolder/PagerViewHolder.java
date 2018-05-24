package com.example.bxlargeimageviewer.ViewHolder;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.example.bxlargeimageviewer.Drawee.ZoomableDraweeView;
import com.example.bxlargeimageviewer.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import java.io.File;
import java.util.List;


public class PagerViewHolder extends ViewHolder {
    //region properties
    ZoomableDraweeView simpleDraweeView;
    Context context;
    List<String> imageURIs;
    ///endregion

    //region LieCycle
    public PagerViewHolder(View itemView, List<String> imageURIs) {
        super(itemView);
        context = itemView.getContext();
        simpleDraweeView = itemView.findViewById(R.id.image_view);
        this.imageURIs = imageURIs;
    }

    //endregion

    //region public method
    public void onBind(int position) {

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        String path = imageURIs.get(position);
        builder.setUri(path);
        builder.setAutoPlayAnimations(true);
        builder.setControllerListener(getDraweeControllerListener(simpleDraweeView));
        simpleDraweeView.setController(builder.build());

    }

    //endregion

    //region private method
    private BaseControllerListener<ImageInfo>
    getDraweeControllerListener(final ZoomableDraweeView drawee) {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                drawee.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        };
    }

    //endregion
}
