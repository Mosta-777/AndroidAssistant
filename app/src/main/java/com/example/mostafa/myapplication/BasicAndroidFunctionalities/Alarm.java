package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mostafa on 6/19/2018.
 */

public class Alarm {

    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private String dateTime;

    public Alarm(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                 ArrayList<ArrayList<Entity>> theWinningSentences) {
        analyzerinterface=intentAnalyzerAndRecognizer;
        // We then choose the best sentence , the sentence containing
        // either one datetime entity or one duration entity .
        int i=isThereAnAlarmShowOrDelete(theWinningSentences);
        switch (i) {
            case 0 : analyzerinterface.onAlarmShowSucceeded("Tamam eshta il mnbhat ahy");break;
            case 1 : analyzerinterface.onAlarmDeleteSucceeded("Tamam t2dar tms7 il alarmn mn il app");break;
            case 2 : determineTheBestSentenceForAlarmSet(theWinningSentences);break;
        }
    }

    private int isThereAnAlarmShowOrDelete(ArrayList<ArrayList<Entity>> alarmSentences) {
        for (int i=0;i<alarmSentences.size();i++){
            if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.ALARM_SHOW_INTENT_TYPE_ENTITY
                            ,alarmSentences.get(i))) {
                analyzerinterface
                        .onChoosingTheWinningSentence
                                (IntentAnalyzerAndRecognizer.extractTextFromSentence(alarmSentences.get(i)));
                return 0;
            }
            else if (IntentAnalyzerAndRecognizer
                    .containsIntentValue(IntentAnalyzerAndRecognizer.ALARM_DELETE_INTENT_TYPE_ENTITY
                            ,alarmSentences.get(i))){
                analyzerinterface
                        .onChoosingTheWinningSentence
                                (IntentAnalyzerAndRecognizer.extractTextFromSentence(alarmSentences.get(i)));
                return 1;
            }
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
            analyzerinterface
                    .onChoosingTheWinningSentence
                            (IntentAnalyzerAndRecognizer.extractTextFromSentence(selectedSentence));
            int dateTimeEntityIndex=IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,selectedSentence);
            int durationEntityIndex=IntentAnalyzerAndRecognizer
                    .containsEntity(IntentAnalyzerAndRecognizer.DURATION_ENTITY,selectedSentence);
            if (dateTimeEntityIndex!=-1){
                // set the alarm using date and time
                dateTime = (String) selectedSentence.get(dateTimeEntityIndex).getValue();
                Log.d("TAG","Setting time using datetime "+dateTime);
                analyzerinterface.onAlarmSetSucceeded(dateTime);
            }else if (durationEntityIndex!=-1){
                //get duration value
                int durationValue = (int) selectedSentence.get(durationEntityIndex).getValue();
                //get current dateandtime then add the duration
                String alarmValue = durationToDateTime(durationValue);
                analyzerinterface.onAlarmSetSucceeded(alarmValue);
                Log.d("TAG","Setting time using duration");
            }
        }else {
            // This means that none of the sentences contains one and only one datetime or duration .
            analyzerinterface
                    .onChoosingTheWinningSentence
                            (IntentAnalyzerAndRecognizer.extractTextFromSentence(alarmSentences.get(0)));
            analyzerinterface.onAlarmSetRequestingData("tamam , azboto il sa3a kam ?");
        }

    }
    static boolean isThereOnlyOne(String entityName, ArrayList<Entity> sentence){
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
        Intent openNewAlarm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            openNewAlarm = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            context.startActivity(openNewAlarm);
        }
    }
    public static boolean setAlarm(Context context,String date) {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateFormatted = dateFormat.format(currentDate);
        String tomorrowDateFormatted = dateFormat.format(tomorrowDate);
        if((currentDateFormatted.substring(0, 4).equals(date.substring(0,4))) &&
                (currentDateFormatted.substring(5,7).equals(date.substring(5,7)))){
            if(currentDateFormatted.substring(8,10).equals(date.substring(8,10)) ||
                    tomorrowDateFormatted.substring(8,10).equals(date.substring(8,10))) {
                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(date.substring(11,13)));
                i.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(date.substring(14,16)));
                i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                context.startActivity(i);
                return true;
            }
            else return false;
        }
        else
            return false;
    }

    public static String durationToDateTime(int durationValue)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, durationValue);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String alarmValue = dateFormat.format(calendar.getTime());
        return alarmValue;
    }
}
