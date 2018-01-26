package com.kustomer.kustomersdk.Utils;

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
}
