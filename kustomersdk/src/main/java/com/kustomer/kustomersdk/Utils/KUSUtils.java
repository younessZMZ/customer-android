package com.kustomer.kustomersdk.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.List;

/**
 * Created by Junaid on 1/26/2018.
 */

public class KUSUtils {
    public static String KUSUnescapeBackslashesFromString (String string){
        String updatedString = "";

        int startingIndex = 0;
        for(int i = 0; i<string.length(); i++){
            String character = string.substring(i,i+1);
            if(character.equals("\\")){
                String lastString = string.substring(startingIndex, i);
                updatedString = updatedString.concat(lastString);

                i++;
                startingIndex = i;
            }
        }

        String endingString = string.substring(startingIndex);
        updatedString = updatedString.concat(endingString);

        return updatedString;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static String listJoinedByString(List<String> list, String join){
        StringBuilder joinedString = new StringBuilder();

        for(int i = 0; i<list.size(); i++){

            if(i<list.size()-1)
                joinedString.append(list.get(i)).append(join);
            else
                joinedString.append(list.get(i));
        }

        return joinedString.toString();
    }
}
