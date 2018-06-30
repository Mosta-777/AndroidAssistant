package com.example.mostafa.myapplication;

import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/20/2018.
 */

public interface CommunicationInterfaces {

    interface MainActivityFunctionalityClassesInterface {
        void onAlarmSetSucceeded(String dateTime);
        //No passed data for the alarm as we will ask what time in all cases
        void onAlarmSetRequestingData(String missingDataString);
        void onAlarmShowSucceeded(String message);
        void onAlarmDeleteSucceeded(String message);
        void onGettingWitResponseFailed(String failingMessage);
        void onFlashLightOn(String message);
        void onFlashLightOff(String message);
        void onCancelling(String intentToCancel);
        void onCancellingWhat(String message);
        void onFailingToUnderstand(String message);
        void onShowCallLog(String message);
        void onShowContacts(String message);
        void onReminderSucceeded(String dateTime, String reminderFreeText);
        void onReminderRequestingData(boolean dateTimeExists, boolean reminderFreeTextExists);
        void onCallingNumberSucceeded(String phoneNumber);
        void onCallingNumberRequestingData(String message);
        void onCallingByName(String name);
        void onCallingContactNotFound(String message);
        void onWiFiOnSucceeded();
        void onWiFiOffSucceeded();
        void onBluetoothOnSucceeded();
        void onBluetoothOffSucceeded();
    }
    interface AnalyzerNetworkUtilsInterface {
        void toNetworkUtils(String message);
        void toAnalyzer(ArrayList<Entity> entities);
        void toAnalyzerFailedResponse(String failingMessage);
    }

}
