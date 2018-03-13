package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSUtils;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;
import java.util.Locale;

/**
 * Created by Junaid on 3/13/2018.
 */

public class KUSLargeImageViewer implements View.OnClickListener {

    //region Properties
    View header;
    ImageView ivClose;
    ImageView ivShare;
    TextView tvheader;
    ImageViewer imageViewer;

    Context mContext;
    String currentImageLink;
    //endregion

    //region LifeCycle
    public KUSLargeImageViewer(Context context){
        mContext = context;

        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(layoutInflater == null)
            return;

        header = layoutInflater.inflate(R.layout.kus_large_image_viewer_header, null);

        ivClose = header.findViewById(R.id.ivClose);
        ivShare = header.findViewById(R.id.ivShare);
        tvheader = header.findViewById(R.id.tvHeader);

        ivClose.setOnClickListener(this);
        ivShare.setOnClickListener(this);
    }
    //endregion

    public void showImages(final List<String> imageURIs, int startingIndex){

        currentImageLink = imageURIs.get(startingIndex);

        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(mContext.getResources())
                .setFailureImage(R.drawable.ic_error_outline_red_33dp);

        imageViewer = new ImageViewer.Builder<>(mContext, imageURIs)
                .setStartPosition(startingIndex)
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                .setOverlayView(header)
                .setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                    @Override
                    public void onImageChange(int position) {
                        tvheader.setText(String.format(Locale.getDefault(),"%d/%d",position+1,imageURIs.size()));
                        currentImageLink = imageURIs.get(position);
                    }
                })
                .setImageMarginPx((int) KUSUtils.dipToPixels(mContext,10))
                .show();
    }

    @Override
    public void onClick(View v) {
        if(v == ivClose){
            imageViewer.onDismiss();
        }else if(v == ivShare){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentImageLink);
            mContext.startActivity(Intent.createChooser(sharingIntent,
                    mContext.getResources().getString(R.string.share_via)));
        }
    }
}
