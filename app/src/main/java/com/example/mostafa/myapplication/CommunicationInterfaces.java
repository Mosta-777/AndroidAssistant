package com.example.mostafa.myapplication;

import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/20/2018.
 */

public interface CommunicationInterfaces {

    interface MainActivityFunctionalityClassesInterface {
        void onAlarmSetSucceeded();
        //No passed data for the alarm as we will ask what time in all cases
        void onAlarmSetRequestingData();
        void onGettingWitResponseFailed(String failingMessage);
    }
    interface AnalyzerNetworkUtilsInterface {
        void toNetworkUtils(String message);
        void toAnalyzer(ArrayList<Entity> entities);
        void toAnalyzerFailedResponse(String failingMessage);
    }

}
