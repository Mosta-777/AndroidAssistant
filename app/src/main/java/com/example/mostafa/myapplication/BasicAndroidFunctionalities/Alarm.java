package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.util.ArrayList;

/**
 * Created by Mostafa on 6/19/2018.
 */

public class Alarm {

    private ArrayList<ArrayList<Entity>> alarmSentences;
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;

    public Alarm(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                 ArrayList<ArrayList<Entity>> theWinningSentences) {
        alarmSentences=theWinningSentences;
        analyzerinterface=intentAnalyzerAndRecognizer;
        // We then choose the best sentence , the sentence containing
        // either one datetime entity or one duration entity .
        int i=isThereAnAlarmShowOrDelete(alarmSentences);
        switch (i) {
            case 0 : analyzerinterface.onAlarmShowSucceeded();break;
            case 1 : analyzerinterface.onAlarmDeleteSucceeded();break;
            case 2 : determineTheBestSentenceForAlarmSet(alarmSentences);break;
        }
    }

    private int isThereAnAlarmShowOrDelete(ArrayList<ArrayList<Entity>> alarmSentences) {
        for (int i=0;i<alarmSentences.size();i++){
            if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.ALARM_SHOW_INTENT_TYPE_ENTITY
                            ,alarmSentences.get(i))) return 0;
            else if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.ALARM_DELETE_INTENT_TYPE_ENTITY
                            ,alarmSentences.get(i))) return 1;
        }
        return 2;
    }

    private void determineTheBestSentenceForAlarmSet(ArrayList<ArrayList<Entity>> alarmSentences) {
        ArrayList<Entity> selectedSentence = null;
        for (int i=0;i<alarmSentences.size();i++){
            // Is there only one datetime or only one duration , not one datetime and one duration
            // and not no date time and no duration ? if yes this is our statement , no
            // then continue searching .
            if ((isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,alarmSentences.get(i)) ^
                    isThereOnlyOne(IntentAnalyzerAndRecognizer.DURATION_ENTITY,alarmSentences.get(i)))) {
                selectedSentence = alarmSentences.get(i);break;
            }
        }
        if (selectedSentence!=null){
            int dateTimeEntityIndex=IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,selectedSentence);
            int durationEntityIndex=IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.DURATION_ENTITY,selectedSentence);
            if (dateTimeEntityIndex!=-1){
                // set the alarm using date and time
                Log.d("TAG","Setting time using datetime");
            }else if (durationEntityIndex!=-1){
                Log.d("TAG","Setting time using duration");
            }
        }else {
            // This means that none of the sentences contains one and only one datetime or duration .
            analyzerinterface.onAlarmSetRequestingData();
        }

    }
    private boolean isThereOnlyOne(String entityName,ArrayList<Entity> sentence){
        int counter=0;
        for (int i=0;i<sentence.size();i++){
            if (sentence.get(i).getName().equals(entityName)){
                counter++;
                if (counter>=2) return false;
            }
        }
        return counter == 1;
    }


    public static void showAlarm(Context context){
        //Toast.makeText(context,"Showing the alarms ..... ",Toast.LENGTH_LONG).show();
        Intent openNewAlarm = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        context.startActivity(openNewAlarm);
    }


}
