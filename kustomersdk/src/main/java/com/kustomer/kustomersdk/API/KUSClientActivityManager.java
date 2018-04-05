package com.kustomer.kustomersdk.API;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.kustomer.kustomersdk.DataSources.KUSClientActivityDataSource;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;

import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KUSClientActivityManager implements KUSObjectDataSourceListener {

    //region Properties
    private String currentPageName;
    private KUSUserSession userSession;
    private String previousPageName;
    private Double currentPageStartTime;
    private List<Timer> timers = new ArrayList<>();
    private KUSClientActivityDataSource activityDataSource;
    //endregion

    //region LifeCycle
    public KUSClientActivityManager(KUSUserSession userSession){
        this.userSession = userSession;
    }
    //endregion

    //region Internal Methods
    private void cancelTimers(){

        if(timers == null || timers.size() == 0) {
            return;
        }

        List<Timer> tempTimers = new ArrayList<>(timers);
        timers = null;

        for(Timer timer : tempTimers){
            timer.cancel();
        }

    }

    private void updateTimers(){
        if(activityDataSource.getObject() == null){
            cancelTimers();
            return;
        }

        List <Timer> timers = new ArrayList<>();

        for(Double interval : activityDataSource.getIntervals()){
            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            onActivityTimer();
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, interval.longValue()*1000);

            timers.add(timer);
        }
        this.timers = timers;
    }

    private void requestClientActivity(){
        requestClientActivityWithCurrentPageSeconds(timeOnCurrentPage());
    }

    private void requestClientActivityWithCurrentPageSeconds(Double currentPageSeconds){

        activityDataSource = new KUSClientActivityDataSource(userSession,
                previousPageName,
                currentPageName,
                currentPageSeconds);

        activityDataSource.addListener(this);
        activityDataSource.fetch();
    }

    @NonNull
    private Double timeOnCurrentPage(){
        long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
        return currentTime - currentPageStartTime;
    }


    private void onActivityTimer(){
        requestClientActivity();
    }
    //endregion

    //region Public Methods
    public void setCurrentPageName(String currentPageName){

        if(this.currentPageName.equals(currentPageName))
            return;
        previousPageName = this.currentPageName;
        this.currentPageName = currentPageName;

        cancelTimers();
        activityDataSource.cancel();
        activityDataSource = null;

        //If we don't have a current page name, stop here.
        if(this.currentPageName == null)
            return;

        currentPageStartTime = (double) Calendar.getInstance().getTimeInMillis()/1000;
        requestClientActivityWithCurrentPageSeconds(0.0);
    }

    public String getCurrentPageName() {
        return currentPageName;
    }

    //endregion

    //region Callbacks
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        updateTimers();
    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {

        if(dataSource == activityDataSource){
            final WeakReference<KUSClientActivityManager> weakReference = new WeakReference<>(this);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    weakReference.get().requestClientActivity();
                }
            };
            handler.postDelayed(runnable,2000);
        }
    }
    //endregion
}
