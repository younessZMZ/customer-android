package com.kustomer.kustomersdk.API;

public class KUSClientActivityManager {

    //region Properties
    private String currentPageName;
    private KUSUserSession userSession;
    //endregion

    //region LifeCycle
    public KUSClientActivityManager(KUSUserSession userSession){
        this.userSession = userSession;
    }
    //endregion

    //region Public Methods
    public void setCurrentPageName(String currentPageName){
        this.currentPageName = currentPageName;
    }
    //endregion
}
