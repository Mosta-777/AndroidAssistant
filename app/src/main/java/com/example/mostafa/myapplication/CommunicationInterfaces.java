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
        void onFailingToUnderstand();
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
        void onCameraSucceeded();
        void onMusicSucceeded();
        void onGallerySucceeded();
        void onNormalModeOn(String message);
        void onSilentModeOn(String message);
        void onVibrationModeOn(String message);
        void onSmsShow(String message);
        void onSmsSendSucceeded(String contactNumber, String smsBody);
        void onSmsSendRequestingData(boolean contactNameExists, boolean smsBodyExists);
        void onSmsSendFailed(String message);
        void onSearchSuccess(String message);
        void onSearchRequestingData(String message);
        void onOpeningNonNativeAppSuccess(String appName);
        void onOpeningNonNativeAppRequestingData(String message);
        void onChoosingTheWinningSentence(String winningSentence);
        void onWeatherSucceeded(String url);
        void onFunctionListSucceeded();
        void onGreetingSucceeded();
    }
    interface AnalyzerNetworkUtilsInterface {
        void toNetworkUtils(String message);
        void toAnalyzer(ArrayList<Entity> entities);
        void toAnalyzerFailedResponse(String failingMessage);
    }

}
