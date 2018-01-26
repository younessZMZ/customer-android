package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;

import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.R;

import java.io.IOException;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSAudio {

    //TODO: Need to be enhanced for multiple audio players

    //region Properties
    private static KUSAudio kusAudio;
    //endregion

    //region LifeCycle
    private static KUSAudio getSharedInstance(){
        if(kusAudio == null)
            kusAudio = new KUSAudio();

        return kusAudio;
    }
    //endregion

    //region Public Methods
    public static void playMessageReceivedSound(){
        getSharedInstance().playMsgReceivedSound();
    }
    //endregion

    //region Private Methods
    private void playMsgReceivedSound(){
        final MediaPlayer mPlayer = MediaPlayer.create(Kustomer.getContext(), R.raw.message_received);
        mPlayer.start();
    }
    //endregion

}
