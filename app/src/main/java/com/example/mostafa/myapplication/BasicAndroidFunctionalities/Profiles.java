package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/29/2018.
 */

public class Profiles {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerInterface;


    public Profiles(CommunicationInterfaces.MainActivityFunctionalityClassesInterface context,
                    ArrayList<ArrayList<Entity>> theWinningSentences){

        analyzerInterface=context;
        int i = isItNormalSilentOrVibration(theWinningSentences);
        switch (i){
            case 0:analyzerInterface.onNormalModeOn("Tamam b2a 3ala il normal mode");break;
            case 1:analyzerInterface.onSilentModeOn("Tamam b2a 3ala il silent");break;
            case 2:analyzerInterface.onVibrationModeOn("Tamam b2a 3ala il vibration");break;
        }
    }

    private int isItNormalSilentOrVibration(ArrayList<ArrayList<Entity>> theWinningSentences) {
        for (int i=0;i<theWinningSentences.size();i++){
            if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.NORMAL_MODE_INTENT_TYPE_ENTITY
                            ,theWinningSentences.get(i))){
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i)));
                return 0;
            }
            else if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.SILENT_MODE_INTENT_TYPE_ENTITY
                            ,theWinningSentences.get(i))) {
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i)));
                return 1;
            }
            else if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.VIBRATION_MODE_INTENT_TYPE_ENTITY
                            ,theWinningSentences.get(i))){
                analyzerInterface.onChoosingTheWinningSentence
                        (IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i)));
                return 2;
            }
        }
        return 0;
    }


    public static void putOnNormalMode(Context context){
        checkThePermission(context);
        AudioManager am;
        am= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

    }
    public static void putOnSilentMode(Context context){
        checkThePermission(context);
        AudioManager am;
        am= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    public static void putOnVibrationMode(Context context){
        checkThePermission(context);
        AudioManager am;
        am= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    private static void checkThePermission(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            context.startActivity(intent);
        }

    }


}
