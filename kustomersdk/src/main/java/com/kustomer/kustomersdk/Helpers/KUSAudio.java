package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;

import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSAudio implements MediaPlayer.OnCompletionListener {

    //region Properties
    private static KUSAudio kusAudio;
    private List<MediaPlayer> playingMediaPlayers = new ArrayList<>();
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
        MediaPlayer mPlayer = MediaPlayer.create(Kustomer.getContext(), R.raw.message_received);

        if(mPlayer != null) {
            playingMediaPlayers.add(mPlayer);
            mPlayer.setOnCompletionListener(this);
            mPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        mp.setOnCompletionListener(null);
        playingMediaPlayers.remove(mp);
    }
    //endregion

}
