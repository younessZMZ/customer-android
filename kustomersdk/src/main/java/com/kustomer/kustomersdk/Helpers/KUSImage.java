package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.Size;

import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSImage {
    //TODO: Incomplete
    //region Properties
    private static List<Integer> defaultNameColors = null;
    //endreigon

    //region Resource Method
    public static Bitmap bitmapFromId(Context mContext, int id){
        return BitmapFactory.decodeResource(mContext.getResources(), id);
    }
    //endregion

    //region Public Methods
    public static Bitmap circularImage(Size size, int color,int strokeColor, int strokeWidth){
        Bitmap dstBitmap = Bitmap.createBitmap(
                size.getWidth(), // Width
                size.getHeight(), // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas to draw circular bitmap
        Canvas canvas = new Canvas(dstBitmap);

        // Initialize a new Paint instance
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Calculate the available radius of canvas
        int radius = Math.min(canvas.getWidth(),canvas.getHeight()/2);

        if(strokeWidth > 0) {
            paint.setColor(strokeColor);

            // Set a pixels value to padding around the circle
            canvas.drawCircle(
                    canvas.getWidth() / 2, // cx
                    canvas.getHeight() / 2, // cy
                    radius, // Radius
                    paint // Paint
            );
        }

        paint.setColor(color);
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getHeight() / 2, // cy
                radius - strokeWidth, // Radius
                paint // Paint
        );

        return dstBitmap;

    }

    private static Bitmap getBitmapWithText(Context mContext, Size size, int color, int strokeColor, int strokeWidth, String text, int textSize){
        Bitmap src = circularImage(size,color, strokeColor, strokeWidth);

        Resources resources = mContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        Canvas canvas = new Canvas(src);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE); // Text Color
        paint.setTextSize((int)textSize*scale);// Text Size
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (src.getWidth() - bounds.width())/6;
        int y = (src.getHeight() + bounds.height())/6;

        canvas.drawText(text, x * scale, y * scale, paint);


        return src;
    }

    public static Bitmap defaultAvatarBitmapForName(Context context, Size size, String Name, int strokeWidth, int fontSize){
        //TODO: Cache Image

        List<String> initials = initialsForName(Name);

        int letterSum = 0;
        StringBuilder text = new StringBuilder();
        for(String initial : initials){
            letterSum += initial.charAt(0);
            text.append(initial);
        }

        int colorIndex = letterSum % getDefaultNameColors().size();
        return getBitmapWithText(
                context,
                size,
                ContextCompat.getColor(context,getDefaultNameColors().get(colorIndex)),
                ContextCompat.getColor(context,R.color.colorPrimary),
                strokeWidth,
                text.toString(),
                fontSize);

    }
    //endregion

    //region Private Methods
    private static List<String> initialsForName(String name){
        int maximumInitialsCount = 3;

        String[] words = name.trim().split(" ");
        List<String> initials = new ArrayList<>();

        for(String word : words){

            if(word.length() > 0) {
                String firstLetter = String.valueOf(word.toUpperCase().charAt(0));
                initials.add(firstLetter);
            }
            if(initials.size() >= maximumInitialsCount)
                break;

        }

        if(initials.size()>0)
            return initials;

        initials.add("*");

        return initials;
    }

    private static List<Integer> getDefaultNameColors(){
        if(defaultNameColors == null) {
            defaultNameColors = new ArrayList<>();
            defaultNameColors.add(R.color.defaultNameColor1);
            defaultNameColors.add(R.color.defaultNameColor2);
            defaultNameColors.add(R.color.defaultNameColor3);
            defaultNameColors.add(R.color.defaultNameColor4);
            defaultNameColors.add(R.color.defaultNameColor5);
            defaultNameColors.add(R.color.defaultNameColor6);
        }

        return defaultNameColors;
    }
    //endregion

}
